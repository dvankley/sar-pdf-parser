package com.djvk.sarPdfParser

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
        STUDENT_FIRST_NAME(DocType.SAR, "Student First Name", setOf(
            "Student’s  First  Name",
            "2. Student’s  First  Name",
        )),
        STUDENT_MIDDLE_NAME(DocType.SAR, "Student Middle Name", setOf(
            "Student’s  Middle  Initial",
            "3. Student’s  Middle  Initial",
        )),
        STUDENT_LAST_NAME(DocType.SAR, "Student Last Name", setOf(
            "Student’s  Last  Name",
            "1. Student’s  Last  Name",
        )),
        STUDENT_DOB(DocType.SAR, "Student Date of Birth", setOf(
            "Student's  Date  of  Birth",
            "9. Student's  Date  of  Birth",
        )),
        STUDENT_SSN_L4(
            DocType.SAR,
            "Social Security Number Last 4 Digits",
            setOf(
                "Student's  Social  Security  Number",
                "8. Student's  Social  Security  Number",
            )
        ),
        PARENT_1_ED_LEVEL(DocType.SAR, "Parent 1 Educational Level", setOf(
            "Parent  1  Educational  Level",
            "24. Parent  1  Educational  Level",
        )),
        PARENT_2_ED_LEVEL(DocType.SAR, "Parent 2 Educational Level", setOf(
            "Parent  2  Educational  Level",
            "25. Parent  2  Educational  Level",
        )),

        // Note the explicit years in these fields. They'll be specifically targeted in the field normalizing process.
        STUDENT_GROSS_INCOME(
            DocType.SAR,
            "Student's  2015  Adjusted Gross  Income",
            setOf(
                "Student's  2015  Adjusted Gross  Income",
                "36. Student's  2015  Adjusted Gross  Income",
            )
        ),
        PARENT_GROSS_INCOME(
            DocType.SAR,
            "Parents'  2015  Adjusted Gross  Income",
            setOf(
                "Parents'  2015  Adjusted Gross  Income",
                "84. 2015  Adjusted Gross  Income",
            )
        ),
        CHILD_SUPPORT_PAID(DocType.SAR, "Child Support Paid", setOf(
            "Student's  Child  Support Paid",
            "Student's  Child  Support Paid",
        )),
        STUDENT_HAS_CHILDREN(
            DocType.SAR,
            "Does student have children they support?",
            setOf(
                "Does Student Have  Children He/She Supports",
                "50. Does the Student Support Children",
            )
        ),
        STUDENT_HAS_DEPENDENTS(
            DocType.SAR,
            "Does student have other non-child/non-spouse dependents?",
            setOf(
                "Does Student Have  Dependents  Other than  Children/Spouse",
                "51. Does the student support other dependents",
            )
        ),
        DECEASED_PARENTS_COURT_WARD_FOSTER_CARE(
            DocType.SAR,
            "Parents deceased / student ward of court / in foster care",
            setOf(
                "Parents  Deceased?/Student  Ward  of  Court?/In  Foster Care",
                "52. Is the student a ward of court or in foster care or are the student's parents deceased",
            )
        ),
        EMANCIPATED_MINOR(
            DocType.SAR, "Emancipated minor?", setOf(
                "Is  or Was Student an  Emancipated  Minor",
                "53. Is the student an emancipated minor",
            )
        ),
        IN_LEGAL_GUARDIANSHIP(
            DocType.SAR,
            "In legal guardianship?",
            setOf(
                "Is  or Was Student in  Legal  Guardianship",
                "54. Is the student in a legal guardianship",
            )
        ),
        UNACCOMPANIED_HOMELESS_YOUTH(
            DocType.SAR,
            "Unaccompanied homeless youth",
            setOf(
                "Is Student an Unaccompanied Homeless Youth as Determined by HUD",
                "Is  Student an  Unaccompanied  Homeless  Youth  as  Determined  by  Director  of Homeless  Youth  Center",
                "Is Student an Unaccompanied Homeless Youth as Determined by High School/Homeless Liaison",
                "55. Is the student unaccompanied and homeless as determined by a high school homeless liaison",
                "56. Is the student unaccompanied and homeless as determined by the U.S. Department of Housing and Urban Development",
                "57. Is the student unaccompanied and homeless as determined by a director of a homeless youth center",
            )
        ),
        PARENTS_RECEIVED_SNAP(
            DocType.SAR, "Parents received SNAP", setOf(
                "Parents  Received  SNAP",
                "75. Did the parent receive SNAP",
            )
        ),
        PARENTS_RECEIVED_TANF(
            DocType.SAR, "Parents received TANF", setOf(
                "Parents  Received  TANF",
                "77. Did the parent receive TANF",
            )
        ),
        STUDENT_RECEIVED_SNAP(
            DocType.SAR, "Student received SNAP", setOf(
                "Student  Received  SNAP",
                "96. Did the student receive SNAP",
            )
        ),
        STUDENT_RECEIVED_TANF(
            DocType.SAR, "Student received TANF", setOf(
                "Student  Received  TANF",
                "98. Did the student receive TANF",
            )
        ),
        FIELDS_TO_REVIEW(DocType.SAR, "Fields to Review"),

        ERROR(DocType.ERROR, "Error"),

        FILENAME(DocType.ALL, "Filename");
    }

    enum class DocType {
        SAR, ERROR, ALL
    }
}