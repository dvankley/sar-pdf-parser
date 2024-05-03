package com.djvk.sarPdfParser.constants

enum class TableField(
    override val pdfLabelPattern: RegexPattern,
    val outputField: CsvHeaders.Fields,
) : FilterablePdfTableField {
}