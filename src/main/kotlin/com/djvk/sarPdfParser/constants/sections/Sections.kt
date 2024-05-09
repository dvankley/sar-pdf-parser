package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.doubleEffPattern
import com.djvk.sarPdfParser.constants.spaces

enum class FileSection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    HEADER("""FAFSA[$spaces\n]+Submission[$spaces\n]+Summary""".toRegex()),
    ESTIMATED_AID("""Estimated[$spaces\n]+Federal[$spaces\n]+Student[$spaces\n]+Aid""".toRegex()),
    FORM_ANSWERS(
        """Y[$spaces\n]*our[$spaces\n]+FAFSA[$spaces\n]*®[$spaces\n]+Form[$spaces\n]+Answers""".toRegex(),
        listOf(FormSection.STUDENT, FormSection.PARENT, FormSection.PARENT_SPOUSE_OR_PARTNER),
    ),
    SCHOOL_AFFORDABILITY("""Find[$spaces\n]+an[$spaces\n]+A${doubleEffPattern}ordable[$spaces\n]+School""".toRegex()),
}

enum class FormSection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    STUDENT(
        """Student[$spaces\n]+Sections""".toRegex(),
        listOf(
            StudentSubsection.PERSONAL_IDENTIFIERS,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            StudentSubsection.DEMOGRAPHICS,
            StudentSubsection.FINANCIALS,
            StudentSubsection.COLLEGES,
            StudentSubsection.SIGNATURE,
        )
    ),
    PARENT(
        """Parent[$spaces\n]+Sections""".toRegex(),
        listOf(
            ParentSubsection.PERSONAL_IDENTIFIERS,
            ParentSubsection.DEMOGRAPHICS,
            ParentSubsection.FINANCIALS,
            ParentSubsection.SIGNATURE,
        ),
    ),

    PARENT_SPOUSE_OR_PARTNER(
        """Parent[$spaces\n]+Spouse[$spaces\n]+or[$spaces\n]+Partner[$spaces\n]+Sections""".toRegex(),
        listOf(
            ParentPartnerSubsection.PERSONAL_IDENTIFIERS,
            ParentPartnerSubsection.FINANCIALS,
            ParentPartnerSubsection.SIGNATURE,
        ),
        false,
    ),
}

enum class StudentSubsection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    PERSONAL_IDENTIFIERS("""Personal[$spaces\n]+Identi(?:[ﬁ\u0000]|fi)ers""".toRegex()),
    PERSONAL_CIRCUMSTANCES("""Personal[$spaces\n]+Circumstances""".toRegex()),
    DEMOGRAPHICS("""Demographics""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    COLLEGES("""Colleges""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}

enum class ParentSubsection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    PERSONAL_IDENTIFIERS("""Personal[$spaces\n]+Identi(?:[ﬁ\u0000]|fi)ers""".toRegex()),
    DEMOGRAPHICS("""Demographics""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}

enum class ParentPartnerSubsection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    PERSONAL_IDENTIFIERS("""Personal[$spaces\n]+Identi(?:[ﬁ\u0000]|fi)ers""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}
