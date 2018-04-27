package com.djvk.sarPdfParser

import com.sun.xml.internal.fastinfoset.util.StringArray

object CsvHeaders {
    public val H_DSAR_EFC_NUMBER = "EFC Number"
    public val H_DSAR_STUDENT_FIRST_NAME = "Student First Name"
    public val H_DSAR_STUDENT_MIDDLE_NAME = "Student Middle Name"
    public val H_DSAR_STUDENT_LAST_NAME = "Student Last Name"
    public val H_DSAR_STUDENT_DOB = "Student Date of Birth"
    public val H_DSAR_STUDENT_SSN_L4 = "Social Security Number Last 4 Digits"
    public val H_DSAR_PARENT_1_ED_LEVEL = "Parent 1 Educational Level"
    public val H_DSAR_PARENT_2_ED_LEVEL = "Parent 2 Educational Level"
    public val H_DSAR_STUDENT_GROSS_INCOME = "Student's  2015  Adjusted Gross  Income"
    public val H_DSAR_PARENT_GROSS_INCOME = "Parents'  2015  Adjusted Gross  Income"
    public val H_DSAR_CHILD_SUPPORT_PAID = "Child Support Paid"
    public val H_DSAR_STUDENT_HAS_CHILDREN = "Does student have children they support?"
    public val H_DSAR_STUDENT_HAS_DEPENDENTS = "Does student have other non-child/non-spouse dependents?"
    public val H_DSAR_DECEASED_PARENTS_COURT_WARD_FOSTER_CARE = "Parents deceased / student ward of court / in foster care"
    public val H_DSAR_EMANCIPATED_MINOR = "Emancipated minor?"
    public val H_DSAR_IN_LEGAL_GUARDIANSHIP = "In legal guardianship?"
    public val H_DSAR_UNACCOMPANIED_HOMELESS_YOUTH = "Unaccompanied homeless youth"
    public val H_DSAR_PARENTS_RECEIVED_SNAP = "Parents received SNAP"
    public val H_DSAR_PARENTS_RECEIVED_TANF = "Parents received TANF"
    public val H_DSAR_STUDENT_RECEIVED_SNAP = "Student received SNAP"
    public val H_DSAR_STUDENT_RECEIVED_TANF = "Student received TANF"
    public val H_DSAR_FILENAME = "Filename"

    val DOC_SAR = arrayOf(
            H_DSAR_EFC_NUMBER,
            H_DSAR_STUDENT_FIRST_NAME,
            H_DSAR_STUDENT_MIDDLE_NAME,
            H_DSAR_STUDENT_LAST_NAME,
            H_DSAR_STUDENT_DOB,
            H_DSAR_STUDENT_SSN_L4,
            H_DSAR_PARENT_1_ED_LEVEL,
            H_DSAR_PARENT_2_ED_LEVEL,
            H_DSAR_STUDENT_GROSS_INCOME,
            H_DSAR_PARENT_GROSS_INCOME,
            H_DSAR_CHILD_SUPPORT_PAID,
            H_DSAR_STUDENT_HAS_CHILDREN,
            H_DSAR_STUDENT_HAS_DEPENDENTS,
            H_DSAR_DECEASED_PARENTS_COURT_WARD_FOSTER_CARE,
            H_DSAR_EMANCIPATED_MINOR,
            H_DSAR_IN_LEGAL_GUARDIANSHIP,
            H_DSAR_UNACCOMPANIED_HOMELESS_YOUTH,
            H_DSAR_PARENTS_RECEIVED_SNAP,
            H_DSAR_PARENTS_RECEIVED_TANF,
            H_DSAR_STUDENT_RECEIVED_SNAP,
            H_DSAR_STUDENT_RECEIVED_TANF,
            H_DSAR_FILENAME
    )
}