package com.djvk.sarPdfParser.constants.sections

import com.djvk.sarPdfParser.constants.doubleEffPattern
import com.djvk.sarPdfParser.constants.fiPattern
import com.djvk.sarPdfParser.constants.spaces

enum class FileSection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    HEADER("""FAFSA[$spaces]+Submission[$spaces]+Summary""".toRegex()),
    ESTIMATED_AID("""Estimated[$spaces]+Federal[$spaces]+Student[$spaces]+Aid""".toRegex()),
    FORM_ANSWERS(
        """Y[$spaces]*our[$spaces]+FAFSA[$spaces]*®?[$spaces]+Form[$spaces]+Answers""".toRegex(),
        listOf(FormSection.STUDENT, FormSection.PARENT, FormSection.PARENT_SPOUSE_OR_PARTNER),
    ),
    SCHOOL_AFFORDABILITY("""Find[$spaces]+an[$spaces]+A${doubleEffPattern}ordable[$spaces]+School""".toRegex()),
}

enum class FormSection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    STUDENT(
        """Student[$spaces]+Sections""".toRegex(),
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
        """Parent[$spaces]+Sections""".toRegex(),
        listOf(
            ParentSubsection.PERSONAL_IDENTIFIERS,
            ParentSubsection.DEMOGRAPHICS,
            ParentSubsection.FINANCIALS,
            ParentSubsection.SIGNATURE,
        ),
    ),

    PARENT_SPOUSE_OR_PARTNER(
        """Parent[$spaces]+Spouse[$spaces]+or[$spaces]+Partner[$spaces]+Sections""".toRegex(),
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
    PERSONAL_IDENTIFIERS("""Personal[$spaces]+Identi${fiPattern}ers""".toRegex()),
    PERSONAL_CIRCUMSTANCES("""Personal[$spaces]+Circumstances""".toRegex()),
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
    PERSONAL_IDENTIFIERS("""Personal[$spaces]+Identi${fiPattern}ers""".toRegex()),
    DEMOGRAPHICS("""Demographics""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}

enum class ParentPartnerSubsection(
    override val searchPattern: Regex,
    override val children: List<Section>? = null,
    override val required: Boolean = true,
) : Section {
    PERSONAL_IDENTIFIERS("""Personal[$spaces]+Identi${fiPattern}ers""".toRegex()),
    FINANCIALS("""Financials""".toRegex()),
    SIGNATURE("""Signature""".toRegex()),
}
