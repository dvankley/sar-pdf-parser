package com.djvk.sarPdfParser

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

class PdfParser {
    init {
        // PDFBox said to use this for JDK8 and later
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider")
    }

    suspend fun processFile(file: File): Map<String, String> {
        println("Processing file ${file.name}")
        PDDocument.load(file).use { document ->
            val parsedText = HashMap<String, String>()
            val text = getLayoutText(document)


            val EFCNUmber = getEFCNumber(text)
            parsedText.put("EFC Number", EFCNUmber)

            val regex = Regex("""^\s*\d+\S?\.(.*)[:\?](.*)${'$'}""", RegexOption.MULTILINE)
            val matchResults = regex.findAll(text)
            for (matchResult in matchResults) {
                val groups = matchResult.groups
                if (groups.size == 3) {
                    val label = groups[1]!!.value.trim()
                    val response = groups[2]!!.value.trim()
                    parsedText.put(label, response)
                }
            }
            return mapToCSVMap(parsedText)
        }
    }

    fun getLayoutText(document: PDDocument): String {
        val stripper = PDFLayoutTextStripper()
        val text = stripper.getText(document)
        return text
    }


    fun getEFCNumber(pdfContent: String): String {
        val regexForEFCWithLabel = """EFC:\s*\d+""".toRegex()
        var EFCNUmberWithLabel = regexForEFCWithLabel.find(pdfContent, 0)?.value ?: ""


        val regexForEFCNumber = """\d+""".toRegex()
        val EFCNUmber = regexForEFCNumber.find(EFCNUmberWithLabel, 0)?.value ?: ""

        return EFCNUmber
    }

    fun mapToCSVMap(m: HashMap<String, String>): HashMap<String, String> {
        val csvMap = HashMap<String, String>()
        csvMap.put(CsvHeaders.H_DSAR_EFC_NUMBER, m.getOrDefault("EFC Number", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_FIRST_NAME, m.getOrDefault("Student’s  Last  Name", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_FIRST_NAME, m.getOrDefault("Student’s  Last  Name", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_MIDDLE_NAME, m.getOrDefault("Student’s  Middle  Initial", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_LAST_NAME, m.getOrDefault("Student’s  First  Name", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_DOB, m.getOrDefault("Student's  Date  of  Birth", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_SSN_L4, m.getOrDefault("Student's  Social  Security  Number", ""))
        csvMap.put(CsvHeaders.H_DSAR_PARENT_1_ED_LEVEL, m.getOrDefault("Parent  1  Educational  Level", ""))
        csvMap.put(CsvHeaders.H_DSAR_PARENT_2_ED_LEVEL, m.getOrDefault("Parent  2  Educational  Level", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_GROSS_INCOME, m.getOrDefault("Student's  2015  Adjusted Gross  Income", ""))
        csvMap.put(CsvHeaders.H_DSAR_PARENT_GROSS_INCOME, m.getOrDefault("Parents'  2015  Adjusted Gross  Income", ""))
        csvMap.put(CsvHeaders.H_DSAR_CHILD_SUPPORT_PAID, m.getOrDefault("Student's  Child  Support Paid", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_HAS_CHILDREN, m.getOrDefault("Does Student Have  Children He/She Supports", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_HAS_DEPENDENTS, m.getOrDefault("Does Student Have  Dependents  Other than  Children/Spouse", ""))
        csvMap.put(CsvHeaders.H_DSAR_DECEASED_PARENTS_COURT_WARD_FOSTER_CARE, m.getOrDefault("Parents  Deceased?/Student  Ward  of  Court?/In  Foster Care", ""))
        csvMap.put(CsvHeaders.H_DSAR_EMANCIPATED_MINOR, m.getOrDefault("Is  or Was Student an  Emancipated  Minor", ""))
        csvMap.put(CsvHeaders.H_DSAR_IN_LEGAL_GUARDIANSHIP, m.getOrDefault("Is  or Was Student in  Legal  Guardianship", ""))
        csvMap.put(CsvHeaders.H_DSAR_UNACCOMPANIED_HOMELESS_YOUTH, m.getOrDefault("Is  Student an  Unaccompanied  Homeless  Youth  as  Determined  by  Director  of Homeless  Youth  Center", ""))
        csvMap.put(CsvHeaders.H_DSAR_PARENTS_RECEIVED_SNAP, m.getOrDefault("Parents  Received  SNAP", ""))
        csvMap.put(CsvHeaders.H_DSAR_PARENTS_RECEIVED_TANF, m.getOrDefault("Parents  Received  TANF", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_RECEIVED_SNAP, m.getOrDefault("Student  Received  SNAP", ""))
        csvMap.put(CsvHeaders.H_DSAR_STUDENT_RECEIVED_TANF, m.getOrDefault("Student  Received  TANF", ""))
        return csvMap
    }
}
