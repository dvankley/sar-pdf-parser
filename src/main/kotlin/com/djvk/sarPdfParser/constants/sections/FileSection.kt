package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.doubleEffPattern
import com.djvk.sarPdfParser.constants.spaces

enum class FileSection(
    override val searchPattern: Regex
) : SearchableSection {
    HEADER("""FAFSA[$spaces\n]+Submission[$spaces\n]+Summary""".toRegex()),
    ESTIMATED_AID("""Estimated[$spaces\n]+Federal[$spaces\n]+Student[$spaces\n]+Aid""".toRegex()),
    FORM_ANSWERS("""Y[$spaces\n]*our[$spaces\n]+FAFSA[$spaces\n]*®[$spaces\n]+Form[$spaces\n]+Answers""".toRegex()),
    SCHOOL_AFFORDABILITY("""Find[$spaces\n]+an[$spaces\n]+A${doubleEffPattern}ordable[$spaces\n]+School""".toRegex()),
}
