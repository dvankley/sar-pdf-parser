package com.djvk.sarPdfParser

data class StudentAidReport(
        val efcNumber: String,
        val studentsLastName: String,
        val studentsFirstName: String,
        val studentsMiddleInitial: String,
        val studentsDateOfBirth: String,
        val studentsSocialSecurityNumber: String,
        val parent1EducationalLevel: String,
        val parent2EducationalLevel: String,
        val students2015AdjustedGrossIncome: String,
        val studentsChildSupportPaid: String,
        val doesStudentHaveChildrenHesheSupports: String,
        val doesStudentHaveDependentsOtherThanChildrenspouse: String,
        val parentsDeceasedstudentWardOfCourtinFosterCare: String,
        val isOrWasStudentAnEmancipatedMinor: String,
        val isOrWasStudentInLegalGuardianship: String,
        val isStudentAnUnaccompaniedHomelessYouthAsDeterminedByHighSchoolhomelessLiaison: String,
        val isStudentAnUnaccompaniedHomelessYouthAsDeterminedByHud: String,
        val isStudentAnUnaccompaniedHomelessYouthAsDeterminedByDirectorOfHomelessYouthCenter: String,
        val parentsReceivedSnap: String,
        val parentsReceivedTanf: String,
        val parents2015AdjustedGrossIncome: String,
        val studentReceivedSnap: String,
        val studentReceivedTanf: String
) {
}