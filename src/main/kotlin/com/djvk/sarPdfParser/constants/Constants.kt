package com.djvk.sarPdfParser.constants

import com.djvk.sarPdfParser.PdfNormalizer

val PRODUCT_NAME = "SAR PDF Parser"

val dashes = PdfNormalizer.groupByAsciiForRegex('-')
val spacesWithoutNewline = PdfNormalizer.groupByAsciiForRegex(' ')
val spaces = spacesWithoutNewline + "\n"
/** There are weird conversion artifacts where "ff" is encoded as some weird single character in
 * the PDF, then gets converted back out into random other characters */
val doubleEffPattern = "(?:[!\"\u0000]|ff)"
/** Same as above, but for fi */
val fiPattern = "(?:[ﬁ\u0000]|fi)"
/** Spaces with newlines */

val textDateRegex = """\w{3,7}\.?[${spaces}]+\d{1,2},[${spaces}]+\d{4}""".toRegex()

val tableArrowSeparator = "\uF061"
val tableValueRegex = """[${spaces}]+${tableArrowSeparator}[${spacesWithoutNewline}]+(.+?)[${spacesWithoutNewline}]*$""".toRegex()

typealias RegexPattern = String
typealias CsvOutputValue = String
