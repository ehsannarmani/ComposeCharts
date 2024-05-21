package ir.ehsannarmani.compose_charts.models

sealed class GridLineStyle{
    data object Normal:GridLineStyle()
    data class Dashed(val intervals:FloatArray = floatArrayOf(10f,10f), val phase:Float = 10f):GridLineStyle() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Dashed

            if (!intervals.contentEquals(other.intervals)) return false
            if (phase != other.phase) return false

            return true
        }

        override fun hashCode(): Int {
            var result = intervals.contentHashCode()
            result = 31 * result + phase.hashCode()
            return result
        }
    }
}