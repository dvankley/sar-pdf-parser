package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.spaces

enum class ParentSubsection(
    override val searchPattern: Regex
) : SearchableSection  {
    PERSONAL_IDENTIFIERS("""Personal[$spaces\n]+Identi(?:[ﬁ\u0000]|fi)ers""".toRegex()),
    DEMOGRAPHICS("""Demographics""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}