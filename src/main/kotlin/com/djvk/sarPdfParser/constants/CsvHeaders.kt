package com.djvk.sarPdfParser.constants

import com.djvk.sarPdfParser.PdfNormalizer

object CsvHeaders {
    val fieldsByNormalizedPdfName = mutableMapOf<String, Fields>()

    init {
        for (field in Fields.values()) {
            if (field.pdfTableFieldNames == null) {
                continue
            }
            for (tableName in field.pdfTableFieldNames) {
                fieldsByNormalizedPdfName[PdfNormalizer.normalizeField(tableName)] = field
            }
        }
    }

    // Files without First Name, Last Name, DOB, and SSN should be logged as an error
    val requiredTableFields =
        listOf(Fields.STUDENT_FIRST_NAME, Fields.STUDENT_LAST_NAME, Fields.STUDENT_DOB, Fields.STUDENT_SSN_L4)

    enum class DataTypes {
        BOOLEAN,
        DOLLARS,
        STRING,
    }

    /**
     * @property docType Document type this field applies to
     * @property csvFieldName Name of CSV column this field is output to
     * @property pdfTableFieldNames The strings to match against the table field name in the PDF file. Matching any
     *  members of this set is considered a match to the field.
     * Note for [com.djvk.sarPdfParser.constants.SarFormat.AFTER_2022], the line number is included when searching
     *  for a matching on this property, so there needs to be an entry with a line number for post-2022.
     * Null if this field isn't a PDF table field.
     */
    enum class Fields(
        val docType: DocType,
        val csvFieldName: String,
        val dataType: DataTypes,
        val pdfTableFieldNames: Set<String>? = null
    ) {
        YEAR(DocType.SAR, "Year", DataTypes.STRING),
        RECEIVED_DATE(DocType.SAR, "Received Date", DataTypes.STRING),
        PROCESSED_DATE(DocType.SAR, "Processed Date", DataTypes.STRING),
        SAI(DocType.SAR, "Student Aid Index (SAI)", DataTypes.STRING),
        STUDENT_FIRST_NAME(
            DocType.SAR, "Student First Name", DataTypes.STRING, setOf(
                "2. Student’s  First  Name",
            )
        ),
        STUDENT_MIDDLE_NAME(
            DocType.SAR, "Student Middle Name", DataTypes.STRING, setOf(
                "3. Student’s  Middle  Initial",
            )
        ),
        STUDENT_LAST_NAME(
            DocType.SAR, "Student Last Name", DataTypes.STRING, setOf(
                "1. Student’s  Last  Name",
            )
        ),
        STUDENT_DOB(
            DocType.SAR, "Student Date of Birth", DataTypes.STRING, setOf(
                "9. Student's  Date  of  Birth",
            )
        ),
        STUDENT_SSN_L4(
            DocType.SAR,
            "Social Security Number Last 4 Digits",
            DataTypes.STRING,
            setOf(
                "8. Student's  Social  Security  Number",
            )
        ),
        STUDENT_EMAIL(DocType.SAR, "Student Email", DataTypes.STRING),
        STUDENT_HAS_DEPENDENTS(
            DocType.SAR,
            "Does student have other non-child/non-spouse dependents?",
            DataTypes.BOOLEAN,
            setOf(
                "51. Does the student support other dependents",
            )
        ),

        // Logical OR of several fields
        ORPHAN_STATE_CUSTODY_OR_EMANCIPATED(DocType.SAR, "Orphan, State Custody, or Emancipated", DataTypes.BOOLEAN),
        LEGAL_GUARDIAN_OTHER_THAN_PARENT(
            DocType.SAR,
            "Legal Guardian Other Than Parent or Stepparent",
            DataTypes.BOOLEAN
        ),

        // TODO: resolve ambiguity in notes about this
        HOMELESS(DocType.SAR, "Homeless or at Risk of Being Homeless", DataTypes.BOOLEAN),
        CANT_PROVIDE_PARENT_INFORMATION(
            DocType.SAR,
            "Can't Provide Parent Information--Unusual Circumstances",
            DataTypes.BOOLEAN
        ),
        APPLYING_FOR_UNSUBSIDIZED_ONLY(DocType.SAR, "Applying For Unsubsidized Loan Only", DataTypes.BOOLEAN),

        // TODO: is this just yes/no?
        PARENT_ATTENDED_COLLEGE(DocType.SAR, "Parent Attended College", DataTypes.BOOLEAN),
        PARENT_MARITAL_STATUS(DocType.SAR, "Parent Current Marital Status", DataTypes.STRING),
        FAMILY_RECEIVED_EIC(DocType.SAR, "Any Family Member Received Earned Income Credit (EIC)", DataTypes.BOOLEAN),
        FAMILY_RECEIVED_HOUSING_ASSISTANCE(
            DocType.SAR,
            "Any Family Member Received Federal Housing Assistance",
            DataTypes.BOOLEAN
        ),
        FAMILY_RECEIVED_FREE_LUNCH(
            DocType.SAR,
            "Any Family Member Received Free/Reduced Price Lunch",
            DataTypes.BOOLEAN
        ),
        FAMILY_RECEIVED_MEDICAID(DocType.SAR, "Any Family Member Received Medicaid", DataTypes.BOOLEAN),
        FAMILY_RECEIVED_HEALTH_PLAN_CREDIT(
            DocType.SAR,
            "Any Family Member Received Refundable Credit for 36B Health Plan (QHP)",
            DataTypes.BOOLEAN
        ),
        FAMILY_RECEIVED_SNAP(
            DocType.SAR,
            "Any Family Member Received Supplemental Nutrition Assistance Program (SNAP)",
            DataTypes.BOOLEAN
        ),
        FAMILY_RECEIVED_SSI(
            DocType.SAR,
            "Any Family Member Received Supplemental Security Income (SSI)",
            DataTypes.BOOLEAN
        ),
        FAMILY_RECEIVED_TANF(
            DocType.SAR,
            "Any Family Member Received Temporary Assistance for Needy Families (TANF)",
            DataTypes.BOOLEAN
        ),
        FAMILY_RECEIVED_WIC(
            DocType.SAR,
            "Any Family Member Received Special Supplemental Nutrition Program for Women, Infants, and Children (WIC)",
            DataTypes.BOOLEAN
        ),
        FAMILY_SIZE(DocType.SAR, "Family Size", DataTypes.STRING),
        FAMILY_MEMBERS_IN_COLLEGE(DocType.SAR, "Family Members in College", DataTypes.STRING),
        COLLEGE_1(DocType.SAR, "College 1", DataTypes.STRING),
        COLLEGE_2(DocType.SAR, "College 2", DataTypes.STRING),
        COLLEGE_3(DocType.SAR, "College 3", DataTypes.STRING),
        COLLEGE_4(DocType.SAR, "College 4", DataTypes.STRING),
        COLLEGE_5(DocType.SAR, "College 5", DataTypes.STRING),
        COLLEGE_6(DocType.SAR, "College 6", DataTypes.STRING),
        COLLEGE_7(DocType.SAR, "College 7", DataTypes.STRING),
        COLLEGE_8(DocType.SAR, "College 8", DataTypes.STRING),
        COLLEGE_9(DocType.SAR, "College 9", DataTypes.STRING),
        COLLEGE_10(DocType.SAR, "College 10", DataTypes.STRING),
        COLLEGE_11(DocType.SAR, "College 11", DataTypes.STRING),
        COLLEGE_12(DocType.SAR, "College 12", DataTypes.STRING),
        COLLEGE_13(DocType.SAR, "College 13", DataTypes.STRING),
        COLLEGE_14(DocType.SAR, "College 14", DataTypes.STRING),
        COLLEGE_15(DocType.SAR, "College 15", DataTypes.STRING),
        COLLEGE_16(DocType.SAR, "College 16", DataTypes.STRING),
        COLLEGE_17(DocType.SAR, "College 17", DataTypes.STRING),
        COLLEGE_18(DocType.SAR, "College 18", DataTypes.STRING),
        COLLEGE_19(DocType.SAR, "College 19", DataTypes.STRING),
        COLLEGE_20(DocType.SAR, "College 20", DataTypes.STRING),

        // These fields are not in the PDF, but are here to allow the program to give feedback to the user
        FIELDS_TO_REVIEW(DocType.SAR, "Fields to Review", DataTypes.STRING),

        ERROR(DocType.ERROR, "Error", DataTypes.STRING),

        FILENAME(DocType.ALL, "Filename", DataTypes.STRING);
    }

    enum class DocType {
        SAR, ERROR, ALL
    }
}