package com.djvk.sarPdfParser

object CsvHeaders {
    enum class Fields(val docType: DocType, val csvFieldName: String, val pdfFieldName: String) {
        EFC_NUMBER(DocType.SAR, "EFC Number", "EFC Number"),
        IS_EFC_STARRED(DocType.SAR, "Is EFC Starred", "Is EFC Starred"),
        HAS_EFC_C_SUFFIX(DocType.SAR, "Has EFC C Suffix", "Has EFC C Suffix"),
        HAS_EFC_H_SUFFIX(DocType.SAR, "Has EFC H Suffix", "Has EFC H Suffix"),
        RECEIVED_DATE(DocType.SAR, "Received Date", "Received Date"),
        PROCESSED_DATE(DocType.SAR, "Processed Date", "Processed Date"),
        YEAR(DocType.SAR, "Year", "Year"),
        STUDENT_FIRST_NAME(DocType.SAR, "Student First Name", "Student’s  First  Name"),
        STUDENT_MIDDLE_NAME(DocType.SAR, "Student Middle Name", "Student’s  Middle  Initial"),
        STUDENT_LAST_NAME(DocType.SAR, "Student Last Name", "Student’s  Last  Name"),
        STUDENT_DOB(DocType.SAR, "Student Date of Birth", "Student's  Date  of  Birth"),
        STUDENT_SSN_L4(DocType.SAR, "Social Security Number Last 4 Digits", "Student's  Social  Security  Number"),
        PARENT_1_ED_LEVEL(DocType.SAR, "Parent 1 Educational Level", "Parent  1  Educational  Level"),
        PARENT_2_ED_LEVEL(DocType.SAR, "Parent 2 Educational Level", "Parent  2  Educational  Level"),
        // Note the explicit years in these fields. They'll be specifically targeted in the field normalizing process.
        STUDENT_GROSS_INCOME(DocType.SAR, "Student's  2015  Adjusted Gross  Income", "Student's  2015  Adjusted Gross  Income"),
        PARENT_GROSS_INCOME(DocType.SAR, "Parents'  2015  Adjusted Gross  Income", "Parents'  2015  Adjusted Gross  Income"),
        CHILD_SUPPORT_PAID(DocType.SAR, "Child Support Paid", "Student's  Child  Support Paid"),
        STUDENT_HAS_CHILDREN(DocType.SAR, "Does student have children they support?", "Does Student Have  Children He/She Supports"),
        STUDENT_HAS_DEPENDENTS(DocType.SAR, "Does student have other non-child/non-spouse dependents?", "Does Student Have  Dependents  Other than  Children/Spouse"),
        DECEASED_PARENTS_COURT_WARD_FOSTER_CARE(DocType.SAR, "Parents deceased / student ward of court / in foster care", "Parents  Deceased?/Student  Ward  of  Court?/In  Foster Care"),
        EMANCIPATED_MINOR(DocType.SAR, "Emancipated minor?", "Is  or Was Student an  Emancipated  Minor"),
        IN_LEGAL_GUARDIANSHIP(DocType.SAR, "In legal guardianship?", "Is  or Was Student in  Legal  Guardianship"),
        UNACCOMPANIED_HOMELESS_YOUTH(DocType.SAR, "Unaccompanied homeless youth", "Is  Student an  Unaccompanied  Homeless  Youth  as  Determined  by  Director  of Homeless  Youth  Center"),
        PARENTS_RECEIVED_SNAP(DocType.SAR, "Parents received SNAP", "Parents  Received  SNAP"),
        PARENTS_RECEIVED_TANF(DocType.SAR, "Parents received TANF", "Parents  Received  TANF"),
        STUDENT_RECEIVED_SNAP(DocType.SAR, "Student received SNAP", "Student  Received  SNAP"),
        STUDENT_RECEIVED_TANF(DocType.SAR, "Student received TANF", "Student  Received  TANF"),

        ERROR(DocType.ERROR, "Error", "Error"),

        FILENAME(DocType.ALL, "Filename", "Filename");
    }

    enum class DocType {
        SAR, ERROR, ALL
    }
}