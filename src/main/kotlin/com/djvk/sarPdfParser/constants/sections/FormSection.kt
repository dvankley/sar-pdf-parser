package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.spaces

enum class FormSection(
    override val searchPattern: Regex
) : SearchableSection {
    STUDENT("""Student[$spaces\n]+Sections""".toRegex()),
    PARENT("""Parent[$spaces\n]+Sections""".toRegex()),
    // May not be present
    PARENT_SPOUSE_OR_PARTNER("""Parent[$spaces\n]+Spouse[$spaces\n]+or[$spaces\n]+Partner[$spaces\n]+Sections""".toRegex()),
}