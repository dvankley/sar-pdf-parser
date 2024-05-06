package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.spaces

enum class StudentSubsection(
    override val searchPattern: Regex
) : SearchableSection  {
    PERSONAL_IDENTIFIERS("""Personal[$spaces\n]+Identi(?:[ﬁ\u0000]|fi)ers""".toRegex()),
    PERSONAL_CIRCUMSTANCES("""Personal[$spaces\n]+Circumstances""".toRegex()),
    DEMOGRAPHICS("""Demographics""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    COLLEGES("""Colleges""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}