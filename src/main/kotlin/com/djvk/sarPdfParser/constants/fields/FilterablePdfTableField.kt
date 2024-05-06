package com.djvk.sarPdfParser.constants.fields

import com.djvk.sarPdfParser.constants.RegexPattern

/**
 * Interface for a field definition (currently implemented by enum classes) that provides a pattern to match the
 *  label of a field in a PDF table.
 */
interface FilterablePdfTableField {
    /**
     * A pattern to match the label of a field in a PDF table.
     * This pattern should match all the terms of the label within capture groups.
     * Other unknown values may be matched by the pattern but they should NOT be in capture groups. This is because
     *  all capture group text will later be removed to allow more effective matching of interleaved values.
     */
    val pdfLabelPattern: RegexPattern
}