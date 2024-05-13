package com.djvk.sarPdfParser.constants

import com.djvk.sarPdfParser.PdfNormalizer
import com.djvk.sarPdfParser.constants.sections.FileSection
import com.djvk.sarPdfParser.constants.sections.ParentSubsection
import com.djvk.sarPdfParser.constants.sections.Section
import com.djvk.sarPdfParser.constants.sections.StudentSubsection

object CsvHeaders {
    val fieldsByNormalizedPdfName: Map<String, Fields>
    val fieldsBySection: Map<Section, List<Fields>>

    init {
        val byNormalized = mutableMapOf<String, Fields>()
        val bySection = mutableMapOf<Section, MutableList<Fields>>()
        for (field in Fields.values()) {
            if (field.tableMatchPatterns != null) {
                for (tableName in field.tableMatchPatterns) {
                    byNormalized[PdfNormalizer.normalizeField(tableName)] = field
                }
            }
            if (field.section != null) {
                val fields = bySection.getOrDefault(field.section, mutableListOf())
                fields.add(field)
                bySection[field.section] = fields
            }
        }

        fieldsByNormalizedPdfName = byNormalized
        fieldsBySection = bySection
    }

    // Files without First Name, Last Name, DOB, and SSN should be logged as an error
    val requiredTableFields =
        listOf(Fields.STUDENT_FIRST_NAME, Fields.STUDENT_LAST_NAME, Fields.STUDENT_DOB, Fields.STUDENT_SSN_L4)

    enum class DataTypes {
        BOOLEAN, DOLLARS, STRING,
    }

    /**
     * @property docType Document type this field applies to
     * @property csvFieldName Name of CSV column this field is output to
     * @property section The section that this field is contained within.
     * @property tableMatchPatterns The regex patterns to use to search for table label matches.
     * Should not contain any capture groups.
     * If [matchAnyYes] is false (the default), the value from the first match from this group
     *  will be used. If [matchAnyYes] is true, any "Yes" values matched by these patterns will
     *  result in the output being "Yes", otherwise it will be "No" (logical OR).
     * Null if this field isn't a PDF table field. Content matching logic for non-table fields
     *  (i.e. for [Fields.YEAR]) is implemented manually on a case-by-case basis.
     * @property matchAnyYes Changes the behavior of [tableMatchPatterns], see comment above for
     *  more details.
     */
    enum class Fields(
        val docType: DocType,
        val csvFieldName: String,
        val dataType: DataTypes,
        val section: Section?,
        val tableMatchPatterns: Set<RegexPattern>? = null,
        val matchAnyYes: Boolean = false,
    ) {
        YEAR(
            DocType.SAR,
            "Year",
            DataTypes.STRING,
            FileSection.HEADER,
        ),
        RECEIVED_DATE(
            DocType.SAR,
            "Received Date",
            DataTypes.STRING,
            FileSection.HEADER,
        ),
        PROCESSED_DATE(
            DocType.SAR,
            "Processed Date",
            DataTypes.STRING,
            FileSection.HEADER,
        ),
        SAI(
            DocType.SAR,
            "Student Aid Index (SAI)",
            DataTypes.STRING,
            FileSection.ESTIMATED_AID,
        ),
        STUDENT_FIRST_NAME(
            DocType.SAR,
            "Student First Name",
            DataTypes.STRING,
            StudentSubsection.PERSONAL_IDENTIFIERS,
            setOf("""First[${spaces}]+Name"""),
        ),
        STUDENT_MIDDLE_NAME(
            DocType.SAR,
            "Student Middle Name",
            DataTypes.STRING,
            StudentSubsection.PERSONAL_IDENTIFIERS,
            setOf("""Middle[${spaces}]+Name"""),
        ),
        STUDENT_LAST_NAME(
            DocType.SAR,
            "Student Last Name",
            DataTypes.STRING,
            StudentSubsection.PERSONAL_IDENTIFIERS,
            setOf("""Last[${spaces}]+Name"""),
        ),
        STUDENT_DOB(
            DocType.SAR,
            "Student Date of Birth",
            DataTypes.STRING,
            StudentSubsection.PERSONAL_IDENTIFIERS,
            setOf("""Date[${spaces}]+of[${spaces}]+Birth"""),
        ),
        STUDENT_SSN_L4(
            DocType.SAR,
            "Social Security Number Last 4 Digits",
            DataTypes.STRING,
            StudentSubsection.PERSONAL_IDENTIFIERS,
            setOf("""Social[${spaces}]+Security[${spaces}]+Number"""),
        ),
        STUDENT_EMAIL(
            DocType.SAR,
            "Student Email",
            DataTypes.STRING,
            StudentSubsection.PERSONAL_IDENTIFIERS,
            setOf("""Email"""),
        ),
        STUDENT_HAS_DEPENDENTS(
            DocType.SAR,
            "Does student have other non-child/non-spouse dependents?",
            DataTypes.BOOLEAN,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            setOf("""Has[${spaces}]+Dependents"""),
        ),

        // Logical OR of several fields
        ORPHAN_STATE_CUSTODY_OR_EMANCIPATED(
            DocType.SAR,
            "Orphan, State Custody, or Emancipated",
            DataTypes.BOOLEAN,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            setOf(
                """Orphaned[${spaces}]+After[${spaces}]+Age[${spaces}]+13""",
                """Ward[${spaces}]+of[${spaces}]+the[${spaces}]+Court[${spaces}]+After[${spaces}]+Age[${spaces}]+13""",
                """Foster[${spaces}]+Care[${spaces}]+After[${spaces}]+Age[${spaces}]+13""",
                """Was[${spaces}]+or[${spaces}]+Is[${spaces}]+a[${spaces}]+Legally[${spaces}]+Emancipated[${spaces}]+Minor""",
            ),
            true,
        ),
        LEGAL_GUARDIAN_OTHER_THAN_PARENT(
            DocType.SAR,
            "Legal Guardian Other Than Parent or Stepparent",
            DataTypes.BOOLEAN,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            setOf("""Legal[${spaces}]+Guardian[${spaces}]+Other[${spaces}]+Than[${spaces}]+Parent[${spaces}]+or[${spaces}]+Stepparent"""),
        ),

        // TODO: resolve ambiguity in notes about this
        HOMELESS(
            DocType.SAR,
            "Homeless or at Risk of Being Homeless",
            DataTypes.BOOLEAN,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            setOf(
                """Homeless[${spaces}]+or[${spaces}]+at[${spaces}]+Risk[${spaces}]+of[${spaces}]+Being[${spaces}]+Homeless""",
                """Determined[${spaces}]+Homeless[${spaces}]+by[${spaces}]+Director[${spaces}]+or[${spaces}]+Designee[${spaces}]+of[${spaces}]+a[${spaces}]+Program[${spaces}]+Addressing[${spaces}]+Homelessness""",
                """Determined[${spaces}]+Homeless[${spaces}]+by[${spaces}]+High[${spaces}]+School[${spaces}]+or[${spaces}]+District[${spaces}]+Homeless[${spaces}]+Liason[${spaces}]+or[${spaces}]+Designee""",
                """Determined[${spaces}]+Homeless[${spaces}]+by[${spaces}]+Director[${spaces}]+of[${spaces}]+Federal[${spaces}]+TRIO[${spaces}]+or[${spaces}]+GEAR[${spaces}]+UP[${spaces}]+Program[${spaces}]+Grant""",
                """Determined[${spaces}]+Homeless[${spaces}]+by[${spaces}]+Financial[${spaces}]+Aid[${spaces}]+Administrator""",
            ),
            true,
        ),
        CANT_PROVIDE_PARENT_INFORMATION(
            DocType.SAR,
            "Can't Provide Parent Information--Unusual Circumstances",
            DataTypes.BOOLEAN,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            setOf("""Can't[${spaces}]+Provide[${spaces}]+Parent[${spaces}]+Information[${dashes}]+Unusual[${spaces}]+Circumstances"""),
        ),
        APPLYING_FOR_UNSUBSIDIZED_ONLY(
            DocType.SAR,
            "Applying For Unsubsidized Loan Only",
            DataTypes.BOOLEAN,
            StudentSubsection.PERSONAL_CIRCUMSTANCES,
            setOf("""Can't[${spaces}]+Provide[${spaces}]+Parent[${spaces}]+Information[${dashes}]+Unusual[${spaces}]+Circumstances"""),
        ),
        PARENT_ATTENDED_COLLEGE(
            DocType.SAR,
            "Parent Attended College",
            DataTypes.STRING,
            StudentSubsection.DEMOGRAPHICS,
            setOf("""Parent[${spaces}]+Attended[${spaces}]+College"""),
        ),
        PARENT_MARITAL_STATUS(
            DocType.SAR,
            "Parent Current Marital Status",
            DataTypes.STRING,
            ParentSubsection.DEMOGRAPHICS,
            setOf("""Current[${spaces}]+Marital[${spaces}]+Status"""),
        ),
        FAMILY_RECEIVED_EIC(
            DocType.SAR,
            "Any Family Member Received Earned Income Credit (EIC)",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Earned[${spaces}]+Income[${spaces}]+Credit[${spaces}]+(EIC)"""),
        ),
        FAMILY_RECEIVED_HOUSING_ASSISTANCE(
            DocType.SAR,
            "Any Family Member Received Federal Housing Assistance",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Federal[${spaces}]+Housing[${spaces}]+Assistance"""),
        ),
        FAMILY_RECEIVED_FREE_LUNCH(
            DocType.SAR,
            "Any Family Member Received Free/Reduced Price Lunch",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Free/Reduced[${spaces}]+Price[${spaces}]+Lunch"""),
        ),
        FAMILY_RECEIVED_MEDICAID(
            DocType.SAR,
            "Any Family Member Received Medicaid",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Medicaid"""),
        ),
        FAMILY_RECEIVED_HEALTH_PLAN_CREDIT(
            DocType.SAR,
            "Any Family Member Received Refundable Credit for 36B Health Plan (QHP)",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Refundable[${spaces}]+Credit[${spaces}]+for[${spaces}]+36B[${spaces}]+Health[${spaces}]+Plan[${spaces}]+\(QHP\)"""),
        ),
        FAMILY_RECEIVED_SNAP(
            DocType.SAR,
            "Any Family Member Received Supplemental Nutrition Assistance Program (SNAP)",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Supplemental[${spaces}]+Nutrition[${spaces}]+Assistance[${spaces}]+Program[${spaces}]+\(SNAP\)"""),
        ),
        FAMILY_RECEIVED_SSI(
            DocType.SAR,
            "Any Family Member Received Supplemental Security Income (SSI)",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Supplemental[${spaces}]+Security[${spaces}]+Income[${spaces}]+\(SSI\)"""),

            ),
        FAMILY_RECEIVED_TANF(
            DocType.SAR,
            "Any Family Member Received Temporary Assistance for Needy Families (TANF)",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Temporary[${spaces}]+Assistance[${spaces}]+for[${spaces}]+Needy[${spaces}]+Families[${spaces}]+\(TANF\)"""),
        ),
        FAMILY_RECEIVED_WIC(
            DocType.SAR,
            "Any Family Member Received Special Supplemental Nutrition Program for Women, Infants, and Children (WIC)",
            DataTypes.BOOLEAN,
            ParentSubsection.FINANCIALS,
            setOf("""Any[${spaces}]+Family[${spaces}]+Member[${spaces}]+Received[${spaces}]+Special[${spaces}]+Supplemental[${spaces}]+Nutrition[${spaces}]+Program[${spaces}]+for[${spaces}]+Women,[${spaces}]+Infants,[${spaces}]+and[${spaces}]+Children[${spaces}]+\(WIC\)"""),
        ),
        FAMILY_SIZE(
            DocType.SAR,
            "Family Size",
            DataTypes.STRING,
            ParentSubsection.FINANCIALS,
            setOf("""Family[${spaces}]+Size"""),
        ),
        FAMILY_MEMBERS_IN_COLLEGE(
            DocType.SAR,
            "Family Members in College",
            DataTypes.STRING,
            ParentSubsection.FINANCIALS,
            setOf("""Family[${spaces}]+Members[${spaces}]+in[${spaces}]+College[${spaces}]+\d{4}[${dashes}]\d{2}"""),
        ),
        COLLEGE_1(
            DocType.SAR,
            "College 1",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+1"""),
        ),
        COLLEGE_2(
            DocType.SAR,
            "College 2",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+2"""),
        ),
        COLLEGE_3(
            DocType.SAR,
            "College 3",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+3"""),
        ),
        COLLEGE_4(
            DocType.SAR,
            "College 4",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+4"""),
        ),
        COLLEGE_5(
            DocType.SAR,
            "College 5",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+5"""),
        ),
        COLLEGE_6(
            DocType.SAR,
            "College 6",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+6"""),
        ),
        COLLEGE_7(
            DocType.SAR,
            "College 7",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+7"""),
        ),
        COLLEGE_8(
            DocType.SAR,
            "College 8",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+8"""),
        ),
        COLLEGE_9(
            DocType.SAR,
            "College 9",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+9"""),
        ),
        COLLEGE_10(
            DocType.SAR,
            "College 10",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+10"""),
        ),
        COLLEGE_11(
            DocType.SAR,
            "College 11",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+11"""),
        ),
        COLLEGE_12(
            DocType.SAR,
            "College 12",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+12"""),
        ),
        COLLEGE_13(
            DocType.SAR,
            "College 13",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+13"""),
        ),
        COLLEGE_14(
            DocType.SAR,
            "College 14",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+14"""),
        ),
        COLLEGE_15(
            DocType.SAR,
            "College 15",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+15"""),
        ),
        COLLEGE_16(
            DocType.SAR,
            "College 16",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+16"""),
        ),
        COLLEGE_17(
            DocType.SAR,
            "College 17",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+17"""),
        ),
        COLLEGE_18(
            DocType.SAR,
            "College 18",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+18"""),
        ),
        COLLEGE_19(
            DocType.SAR,
            "College 19",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+19"""),
        ),
        COLLEGE_20(
            DocType.SAR,
            "College 20",
            DataTypes.STRING,
            StudentSubsection.COLLEGES,
            setOf("""College[${spaces}]+20"""),
        ),

        // These fields are not in the PDF, but are here to allow the program to give feedback to the user
        FIELDS_TO_REVIEW(DocType.SAR, "Fields to Review", DataTypes.STRING, null),

        ERROR(DocType.ERROR, "Error", DataTypes.STRING, null),

        FILENAME(DocType.ALL, "Filename", DataTypes.STRING, null);
    }

    enum class DocType {
        SAR, ERROR, ALL
    }
}