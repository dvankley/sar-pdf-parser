package com.djvk.sarPdfParser

import java.text.Normalizer

object PdfFieldNormalizer {
    fun normalize(field: String): String {
        return Normalizer
                .normalize(field, Normalizer.Form.NFD)
                .replace("""[^0-9A-Za-z]""".toRegex(), "")
                // Years aren't always parsed right, so sometimes you'll end up with some or none of the year digits
                // So we have to get smarter about the context the years might appear in
                // Parent1 and Parent2 are valid non-year strings, all other digit strings starting with 4 are dates
                .replace("""(?<!Parent)20?\d?\d?""".toRegex(), "|normalizedYear|")
    }
}