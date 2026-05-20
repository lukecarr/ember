package sh.carr.ember.plugin.command.argument

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import sh.carr.ember.flag.Flag
import sh.carr.ember.plugin.flag.Flags
import java.util.concurrent.CompletableFuture

/**
 * Brigadier argument type that parses a flag id into a typed [Flag] value.
 *
 * Wraps [StringArgumentType.word] for the wire format and converts the parsed word into a [Flag]
 * via [Flags.byId]. Unknown ids surface a `CommandSyntaxException` with `"Unknown flag: <id>"`.
 *
 * Flag ids must use only `[A-Za-z0-9_\-.+]` (Brigadier's unquoted-string charset). The convention
 * is `.` for namespacing, matching Bukkit permission names (e.g. `command.version`).
 *
 * Use via [flag] at the registration site:
 * ```
 * Commands.argument("flag", flag())
 * ```
 */
class FlagArgumentType private constructor() : CustomArgumentType.Converted<Flag, String> {
    @Throws(CommandSyntaxException::class)
    override fun convert(nativeType: String): Flag = Flags.byId(nativeType) ?: throw UNKNOWN_FLAG.create(nativeType)

    override fun getNativeType(): ArgumentType<String> = StringArgumentType.word()

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        Flags.entries.forEach { builder.suggest(it.id) }
        return builder.buildFuture()
    }

    override fun getExamples(): Collection<String> = Flags.entries.take(3).map { it.id }

    companion object {
        private val UNKNOWN_FLAG = DynamicCommandExceptionType { id -> LiteralMessage("Unknown flag: $id") }

        /** Factory for use at command registration sites. */
        fun flag(): FlagArgumentType = FlagArgumentType()
    }
}
