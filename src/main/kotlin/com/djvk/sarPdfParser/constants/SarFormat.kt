package com.djvk.sarPdfParser.constants

/**
 * The format of the SAR report being parsed.
 * This is currently determined from the SAR report date in the header, because the SAR report format has changed
 *  over time.
 */
enum class SarFormat {
    /**
     * Actually this is "less than or equal to 2021" but I'm not allowed to start variable names with numbers and
     *  I don't want to make this variable name really long.
     */
    BEFORE_2021,

    /**
     * Actually this is "greater than or equal to 2022" but I'm not allowed to start variable names with numbers and
     *  I don't want to make this variable name really long.
     */
    AFTER_2022;

    companion object {
        fun getFormatFromStartYear(startYear: Int): SarFormat {
            return when {
                startYear <= 2021 -> BEFORE_2021
                startYear >= 2022 -> AFTER_2022
                else -> throw IllegalArgumentException("Invalid report first year $startYear")
            }
        }
    }
}