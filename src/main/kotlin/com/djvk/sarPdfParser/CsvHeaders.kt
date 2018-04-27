package com.djvk.sarPdfParser

import com.sun.xml.internal.fastinfoset.util.StringArray

class CsvHeaders {

    public var H_DSAR_STUDENT_FIRST_NAME = "Student Last Name"
    public var H_DSAR_STUDENT_MIDDLE_NAME = "Student Middle Name"
    public var H_DSAR_STUDENT_LAST_NAME = "Student Last Name"

    val DOC_SAR = arrayOf(
            H_DSAR_STUDENT_LAST_NAME,
            H_DSAR_STUDENT_MIDDLE_NAME,
            H_DSAR_STUDENT_LAST_NAME
    //todo add more
    )
}