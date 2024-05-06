package com.djvk.sarPdfParser.constants.fields

import com.djvk.sarPdfParser.constants.CsvHeaders

enum class FfelLoanFields(
    override val pdfLabelPattern: String,
    val balanceField: CsvHeaders.Fields,
    val remainingField: CsvHeaders.Fields,
    val totalField: CsvHeaders.Fields,
) : FilterablePdfTableField {
//    SUBSIDIZED(
//        """(Subsidized).+?(Loans)""",
//        CsvHeaders.Fields.SUBSIDIZED_LOANS_BALANCE,
//        CsvHeaders.Fields.SUBSIDIZED_LOANS_REMAINING,
//        CsvHeaders.Fields.SUBSIDIZED_LOANS_TOTAL
//    ),
//    UNSUBSIDIZED(
//        """(Unsubsidized).+?(Loans)""",
//        CsvHeaders.Fields.UNSUBSIDIZED_LOANS_BALANCE,
//        CsvHeaders.Fields.UNSUBSIDIZED_LOANS_REMAINING,
//        CsvHeaders.Fields.UNSUBSIDIZED_LOANS_TOTAL
//    ),
//    COMBINED(
//        """(Combined).+?(Loans)""",
//        CsvHeaders.Fields.COMBINED_LOANS_BALANCE,
//        CsvHeaders.Fields.COMBINED_LOANS_REMAINING,
//        CsvHeaders.Fields.COMBINED_LOANS_TOTAL
//    ),
//    UNALLOCATED(
//        """(Unallocated).+?(Consolidation).+?(Loans)""",
//        CsvHeaders.Fields.UNALLOCATED_LOANS_BALANCE,
//        CsvHeaders.Fields.UNALLOCATED_LOANS_REMAINING,
//        CsvHeaders.Fields.UNALLOCATED_LOANS_TOTAL
//    ),
}
