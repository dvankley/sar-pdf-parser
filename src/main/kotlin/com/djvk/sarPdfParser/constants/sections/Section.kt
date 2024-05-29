package com.djvk.sarPdfParser.constants.sections

interface Section {
    /**
     * Regex pattern to find the start of the given section.
     *
     * Assumed to be the first match of the given pattern in the entire document.
     */
    val searchPattern: Regex


    /**
     * True if this section is required, false otherwise.
     * If a section is required but not present, it will cause an error.
     * A section being not required (i.e. optional) means that all its children are also not required.
     */
    val required: Boolean

    /**
     * Optional list of sections contained within this section
     */
    val children: List<Section>?

    val name : String
}