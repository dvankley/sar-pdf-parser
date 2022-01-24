package com.djvk.sarPdfParser.constants

enum class PerkinsLoanFields(
    override val pdfLabelPattern: String,
    val field: CsvHeaders.Fields,
) : FilterablePdfTableField {
    BALANCE("""(Total).+?(Outstanding).+?(Principal).+?(Balance)""", CsvHeaders.Fields.PERKINS_LOAN_BALANCE),
    CURRENT_YEAR_LOAN("""(\d{4}[$dashes]\d{2}[$spaces\n]+Loan).+?(Amount)""", CsvHeaders.Fields.PERKINS_LOAN_AMOUNT),
}

