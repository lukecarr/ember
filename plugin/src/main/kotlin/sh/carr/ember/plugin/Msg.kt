package sh.carr.ember.plugin

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import java.time.Duration

/**
 * Buffered MiniMessage -> [Component] deserialisation.
 *
 * MiniMessage parsing is not free: every call tokenizes the input and builds a tag tree.
 * The resulting [Component] is immutable, so it is safe to cache and share a single
 * instance across any number of callers and threads.
 *
 * Three access patterns are provided because caching correctness depends on whether the
 * result is a pure function of a cacheable key:
 *
 *  - [of] (no resolvers): result depends only on the input string -> cached by the string.
 *  - [of] (with resolvers): result depends on resolver values, and [TagResolver] has no
 *    reliable equals/hashCode, so this path is intentionally NOT cached.
 *  - [ofKeyed]: dynamic content, but the caller supplies a stable key encoding the
 *    variable parts (e.g. `"welcome:" + playerName`) -> cached by that key.
 *
 * Thread-safe. Caffeine's loading `get` computes each key at most once under contention.
 */
object Msg {
    private val miniMessage = MiniMessage.miniMessage()

    /** Cache for static strings with no resolvers. Key == the input string. */
    private val staticCache: Cache<String, Component> =
        Caffeine
            .newBuilder()
            .maximumSize(4_096)
            .expireAfterAccess(Duration.ofMinutes(30))
            .recordStats()
            .build()

    /** Cache for dynamic strings keyed explicitly by the caller. */
    private val keyedCache: Cache<String, Component> =
        Caffeine
            .newBuilder()
            .maximumSize(4_096)
            .expireAfterAccess(Duration.ofMinutes(10))
            .recordStats()
            .build()

    /**
     * Deserialize a static MiniMessage string. Result is cached by [input].
     *
     * Use this for fixed text: menu titles, item names, prefixes, static lore. Do not
     * use it for strings you build by string-concatenating user/runtime values, or the
     * cache will grow with every distinct value — use [ofKeyed] for that.
     */
    fun of(input: String) = staticCache.get(input) { miniMessage.deserialize(it) }

    /**
     * Deserialize a MiniMessage string with [resolvers]. NOT cached — the result depends
     * on resolver values that cannot be turned into a reliable cache key. Use [ofKeyed]
     * if the same content recurs, and you can name a stable key for it.
     */
    fun of(
        input: String,
        vararg resolvers: TagResolver,
    ) = miniMessage.deserialize(input, *resolvers)

    /**
     * Deserialize a dynamic MiniMessage string, cached under a caller-supplied [cacheKey].
     *
     * [cacheKey] MUST encode every part of the output that varies — typically the values
     * fed to [resolvers]. For example:
     * ```
     * Msg.ofKeyed("balance:$amount", "<green>Balance: <amt>", Placeholder.unparsed("amt", amount.toString()))
     * ```
     * Reusing one key with different resolver values will return a stale component.
     */
    fun ofKeyed(
        cacheKey: String,
        input: String,
        vararg resolvers: TagResolver,
    ) = keyedCache.get(cacheKey) { miniMessage.deserialize(input, *resolvers) }

    /** Deserialize multiple static lines (e.g. item lore). Each line is cached via [of]. */
    fun ofLines(lines: Iterable<String>) = lines.map(::of)

    /** Drop a single entry from both caches (e.g. after a config reload of one message). */
    fun invalidate(key: String) {
        staticCache.invalidate(key)
        keyedCache.invalidate(key)
    }

    /** Clear every cached component. Call on full config reload. */
    fun invalidateAll() {
        staticCache.invalidateAll()
        keyedCache.invalidateAll()
    }

    /** Hit/miss stats for the static-string cache — handy for tuning `maximumSize`. */
    fun staticStats() = staticCache.stats()

    /** Hit/miss stats for the keyed cache. */
    fun keyedStats() = keyedCache.stats()
}

/** Ergonomic extension: `"<red>Hello".msg()`. Cached via [Msg.of]. */
fun String.msg() = Msg.of(this)

fun CommandSender.msg(message: String) = sendMessage(Msg.of(message))

/**
 * Ergonomic extension for resolved MiniMessage. Not cached, since [resolvers] have no reliable
 * equals/hashCode. Use this when you need to inject untrusted text via [TagResolver]s like
 * `Placeholder.unparsed`.
 */
fun CommandSender.msg(
    message: String,
    vararg resolvers: TagResolver,
) = sendMessage(Msg.of(message, *resolvers))

/**
 * Ergonomic extension for cached, resolved MiniMessage. Routes through [Msg.ofKeyed], so [cacheKey]
 * MUST encode every variable that affects the resolved output (typically the resolver values).
 */
fun CommandSender.msgKeyed(
    cacheKey: String,
    message: String,
    vararg resolvers: TagResolver,
) = sendMessage(Msg.ofKeyed(cacheKey, message, *resolvers))
