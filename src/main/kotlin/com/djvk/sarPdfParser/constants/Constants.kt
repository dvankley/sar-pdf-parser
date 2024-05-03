package com.djvk.sarPdfParser.constants

import com.djvk.sarPdfParser.PdfNormalizer

val PRODUCT_NAME = "SAR PDF Parser"

val dashes = PdfNormalizer.groupByAsciiForRegex('-')
val spaces = PdfNormalizer.groupByAsciiForRegex(' ') + "\n"
/** There are weird conversion artifacts where "ff" is encoded as some weird single character in
 * the PDF, then gets converted back out into random other characters */
val doubleEffPattern = "[!\"]|ff"
/** Spaces with newlines */
val swn = spaces + "\n"

typealias RegexPattern = String
