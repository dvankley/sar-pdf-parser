package com.djvk.sarPdfParser.constants

import com.djvk.sarPdfParser.PdfNormalizer

val PRODUCT_NAME = "SAR PDF Parser"

val dashes = PdfNormalizer.groupByAsciiForRegex('-')
val spaces = PdfNormalizer.groupByAsciiForRegex(' ') + "\n"
/** Spaces with newlines */
val swn = spaces + "\n"
