package com.djvk.sarPdfParser.constants.fields

import com.djvk.sarPdfParser.constants.CsvHeaders
import com.djvk.sarPdfParser.constants.RegexPattern

enum class TableField(
    override val pdfLabelPattern: RegexPattern,
    val outputField: CsvHeaders.Fields,
) : FilterablePdfTableField {
}