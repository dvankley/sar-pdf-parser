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

    /**
     * @property docType Document type this field applies to
     * @property csvFieldName Name of CSV column this field is output to
     * @property pdfTableFieldNames The strings to match against the table field name in the PDF file. Matching any
     *  members of this set is considered a match to the field.
     * Note for [com.djvk.sarPdfParser.constants.SarFormat.AFTER_2022], the line number is included when searching
     *  for a matching on this property, so there needs to be an entry with a line number for post-2022.
     * Null if this field isn't a PDF table field.
     */
    enum class Fields(val docType: DocType, val csvFieldName: String, val pdfTableFieldNames: Set<String>? = null) {
        EFC_NUMBER(DocType.SAR, "EFC Number"),
        IS_EFC_STARRED(DocType.SAR, "Is EFC Starred"),
        HAS_EFC_C_SUFFIX(DocType.SAR, "Has EFC C Suffix"),
        HAS_EFC_H_SUFFIX(DocType.SAR, "Has EFC H Suffix"),
        RECEIVED_DATE(DocType.SAR, "Received Date"),
        PROCESSED_DATE(DocType.SAR, "Processed Date"),
        YEAR(DocType.SAR, "Year"),
        STUDENT_FIRST_NAME(
            DocType.SAR, "Student First Name", setOf(
                "2. Student’s  First  Name",
            )
        ),
        STUDENT_MIDDLE_NAME(
            DocType.SAR, "Student Middle Name", setOf(
                "3. Student’s  Middle  Initial",
            )
        ),
        STUDENT_LAST_NAME(
            DocType.SAR, "Student Last Name", setOf(
                "1. Student’s  Last  Name",
            )
        ),
        STUDENT_DOB(
            DocType.SAR, "Student Date of Birth", setOf(
                "9. Student's  Date  of  Birth",
            )
        ),
        STUDENT_SSN_L4(
            DocType.SAR,
            "Social Security Number Last 4 Digits",
            setOf(
                "8. Student's  Social  Security  Number",
            )
        ),
        PARENT_1_ED_LEVEL(
            DocType.SAR, "Parent 1 Educational Level", setOf(
                "24. Parent  1  Educational  Level",
            )
        ),
        PARENT_2_ED_LEVEL(
            DocType.SAR, "Parent 2 Educational Level", setOf(
                "25. Parent  2  Educational  Level",
            )
        ),

        // Note the explicit years in these fields. They'll be specifically targeted in the field normalizing process.
        STUDENT_GROSS_INCOME(
            DocType.SAR,
            "Student's  Adjusted Gross  Income",
            setOf(
                "36. Student's  2015  Adjusted Gross  Income",
            )
        ),
        PARENT_GROSS_INCOME(
            DocType.SAR,
            "Parents'  Adjusted Gross  Income",
            setOf(
                "84. 2015  Adjusted Gross  Income",
            )
        ),
        STUDENT_HAS_CHILDREN(
            DocType.SAR,
            "Does student have children they support?",
            setOf(
                "50. Does the Student Support Children",
            )
        ),
        STUDENT_HAS_DEPENDENTS(
            DocType.SAR,
            "Does student have other non-child/non-spouse dependents?",
            setOf(
                "51. Does the student support other dependents",
            )
        ),
        DECEASED_PARENTS_COURT_WARD_FOSTER_CARE(
            DocType.SAR,
            "Parents deceased / student ward of court / in foster care",
            setOf(
                "52. Is the student a ward of court or in foster care or are the student's parents deceased",
            )
        ),
        EMANCIPATED_MINOR(
            DocType.SAR, "Emancipated minor?", setOf(
                "53. Is the student an emancipated minor",
            )
        ),
        IN_LEGAL_GUARDIANSHIP(
            DocType.SAR,
            "In legal guardianship?",
            setOf(
                "54. Is the student in a legal guardianship",
            )
        ),
        UNACCOMPANIED_HOMELESS_YOUTH(
            DocType.SAR,
            "Unaccompanied homeless youth",
            setOf(
                "55. Is the student unaccompanied and homeless as determined by a high school homeless liaison",
                "56. Is the student unaccompanied and homeless as determined by the U.S. Department of Housing and Urban Development",
                "57. Is the student unaccompanied and homeless as determined by a director of a homeless youth center",
            )
        ),
        PARENTS_RECEIVED_SNAP(
            DocType.SAR, "Parents received SNAP", setOf(
                "75. Did the parent receive SNAP",
            )
        ),
        PARENTS_RECEIVED_TANF(
            DocType.SAR, "Parents received TANF", setOf(
                "77. Did the parent receive TANF",
            )
        ),
        STUDENT_RECEIVED_SNAP(
            DocType.SAR, "Student received SNAP", setOf(
                "96. Did the student receive SNAP",
            )
        ),
        STUDENT_RECEIVED_TANF(
            DocType.SAR, "Student received TANF", setOf(
                "98. Did the student receive TANF",
            )
        ),
        INTERESTED_IN_WORK_STUDY(
            DocType.SAR, "Interested in Work Study", setOf(
                "31. Is the student interested in Work-Study",
            )
        ),
        STUDENT_RECEIVED_MEDICAID(
            DocType.SAR, "Student received Medicaid",
            setOf(
                "95. Did the student receive Medicaid",
            ),
        ),
        PARENTS_RECEIVED_MEDICAID(
            DocType.SAR, "Parents received Medicaid",
            setOf(
                "74. Did the parent receive Medicaid",
            ),
        ),
        STUDENT_RECEIVED_WIC(
            DocType.SAR, "Student received WIC",
            setOf(
                "99. Did the student receive WIC",
            ),
        ),
        SUBSIDIZED_LOANS_BALANCE(DocType.SAR, "Subsidized loans balance"),
        SUBSIDIZED_LOANS_REMAINING(DocType.SAR, "Subsidized loans remaining"),
        SUBSIDIZED_LOANS_TOTAL(DocType.SAR, "Subsidized loans total"),
        UNSUBSIDIZED_LOANS_BALANCE(DocType.SAR, "Unsubsidized loans balance"),
        UNSUBSIDIZED_LOANS_REMAINING(DocType.SAR, "Unsubsidized loans remaining"),
        UNSUBSIDIZED_LOANS_TOTAL(DocType.SAR, "Unsubsidized loans total"),
        COMBINED_LOANS_BALANCE(DocType.SAR, "Combined loans balance"),
        COMBINED_LOANS_REMAINING(DocType.SAR, "Combined loans remaining"),
        COMBINED_LOANS_TOTAL(DocType.SAR, "Combined loans total"),
        UNALLOCATED_LOANS_BALANCE(DocType.SAR, "Unallocated loans balance"),
        UNALLOCATED_LOANS_REMAINING(DocType.SAR, "Unallocated loans remaining"),
        UNALLOCATED_LOANS_TOTAL(DocType.SAR, "Unallocated loans total"),
        PERKINS_LOAN_BALANCE(DocType.SAR, "Perkins loan balance"),
        PERKINS_LOAN_AMOUNT(DocType.SAR, "Perkins loan amount"),

        // These fields are not in the PDF, but are here to allow the program to give feedback to the user
        FIELDS_TO_REVIEW(DocType.SAR, "Fields to Review"),

        ERROR(DocType.ERROR, "Error"),

        FILENAME(DocType.ALL, "Filename");
    }

    enum class DocType {
        SAR, ERROR, ALL
    }
}