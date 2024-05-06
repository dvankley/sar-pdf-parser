package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.spaces

enum class ParentPartnerSubsection(
    override val searchPattern: Regex
) : SearchableSection  {
    PERSONAL_IDENTIFIERS("""Personal[$spaces\n]+Identi(?:[ﬁ\u0000]|fi)ers""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}