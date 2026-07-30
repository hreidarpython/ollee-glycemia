package pl.cukrzycowy.ollee.glycemia

import android.content.Context

enum class GlycemiaUnit {
    MMOL_L,
    MG_DL
}

/**
 * Stores and applies the user's preferred glycemia display unit
 * (mmol/L for most of Europe, mg/dL for the US and a few other
 * countries). Used to format values consistently across the main
 * screen, the history graph, and the BLE payload sent to the watch.
 */
object GlycemiaUnitStore {
    private const val PREFS_NAME = "data"
    private const val KEY_UNIT = "glycemia_unit"

    const val MGDL_PER_MMOL = 18.0182f

    fun getUnit(context: Context): GlycemiaUnit {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_UNIT, GlycemiaUnit.MMOL_L.name)
        return try {
            GlycemiaUnit.valueOf(stored ?: GlycemiaUnit.MMOL_L.name)
        } catch (e: IllegalArgumentException) {
            GlycemiaUnit.MMOL_L
        }
    }

    fun setUnit(context: Context, unit: GlycemiaUnit) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_UNIT, unit.name)
            .apply()
    }

    /** Formats a raw mg/dL value as a display string in the given unit (no unit suffix). */
    fun formatMgDl(mgDl: Float, unit: GlycemiaUnit): String {
        return when (unit) {
            GlycemiaUnit.MMOL_L -> String.format("%.1f", mgDl / MGDL_PER_MMOL)
            GlycemiaUnit.MG_DL -> mgDl.toInt().toString()
        }
    }

    /** Formats a raw mg/dL delta (already signed) as a display string in the given unit, with a leading +/- sign. */
    fun formatMgDlDelta(mgDlDelta: Float, unit: GlycemiaUnit): String {
        return when (unit) {
            GlycemiaUnit.MMOL_L -> String.format("%+.1f", mgDlDelta / MGDL_PER_MMOL)
            GlycemiaUnit.MG_DL -> String.format("%+d", mgDlDelta.toInt())
        }
    }

    fun unitLabel(unit: GlycemiaUnit): String {
        return when (unit) {
            GlycemiaUnit.MMOL_L -> "mmol/L"
            GlycemiaUnit.MG_DL -> "mg/dL"
        }
    }
}
