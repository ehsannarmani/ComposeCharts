package ir.ehsannarmani.compose_charts.models

import androidx.compose.ui.graphics.PathEffect

sealed class StrokeStyle{
    data object Normal:StrokeStyle()
    data class Dashed(val intervals:FloatArray = floatArrayOf(10f,10f), val phase:Float = 10f):StrokeStyle() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
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

    val pathEffect:PathEffect? get() {
        return when(this){
            is Normal -> {
                null
            }
            is Dashed -> {
                PathEffect.dashPathEffect(intervals = intervals, phase = phase)
            }
        }
    }

}