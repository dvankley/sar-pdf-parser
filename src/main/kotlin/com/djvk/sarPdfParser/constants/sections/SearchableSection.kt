package com.djvk.sarPdfParser.constants.sections

interface SearchableSection {
    /**
     * Regex pattern to find the start of the given section.
     *
     * Assumed to be the first match of the given pattern in the entire document.
     */
    val searchPattern: Regex
}