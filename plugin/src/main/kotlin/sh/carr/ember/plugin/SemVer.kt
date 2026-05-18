package sh.carr.ember.plugin

import sh.carr.ember.Version

data class SemVer(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null,
) : Version {
    companion object {
        private val PATTERN =
            Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-([0-9A-Za-z.-]+))?$""")

        fun parse(input: String): SemVer {
            val m =
                PATTERN.matchEntire(input)
                    ?: throw IllegalArgumentException("Invalid SemVer: $input")
            return SemVer(
                major = m.groupValues[1].toInt(),
                minor = m.groupValues[2].toInt(),
                patch = m.groupValues[3].toInt(),
                preRelease = m.groupValues[4].ifEmpty { null },
            )
        }
    }

    override fun compareTo(other: Version): Int {
        if (other !is SemVer) return toString().compareTo(other.toString())
        compareValuesBy(this, other, SemVer::major, SemVer::minor, SemVer::patch)
            .takeIf { it != 0 }
            ?.let { return it }
        return when {
            preRelease == null && other.preRelease == null -> 0
            preRelease == null -> 1
            other.preRelease == null -> -1
            else -> comparePreRelease(preRelease, other.preRelease)
        }
    }

    override fun toString(): String = "$major.$minor.$patch" + (preRelease?.let { "-$it" } ?: "")
}

private fun comparePreRelease(
    a: String,
    b: String,
): Int {
    val ai = a.split('.')
    val bi = b.split('.')
    ai.zip(bi).forEach { (x, y) ->
        val xn = x.toIntOrNull()
        val yn = y.toIntOrNull()
        val c =
            when {
                xn != null && yn != null -> xn.compareTo(yn)
                xn != null -> -1
                yn != null -> 1
                else -> x.compareTo(y)
            }
        if (c != 0) return c
    }
    return ai.size.compareTo(bi.size)
}
