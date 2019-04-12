package com.djvk.sarPdfParser

import java.util.*

class Transcript(val filename: String, val name: String, val id: String, val dob: Date) {
    private var terms: MutableList<Term> = ArrayList()

    class Course(val subject: String, val number: String, val campus: String, val title: String, val grade: String, val creditHours: Float, val qualityPoints: Float)

    class Term(val name: String, val college: String, val major: String, val academicStanding: String) {
        private var courses: MutableList<Course> = ArrayList()
        private fun addCourse(course: Course) {
            this.courses.add(course)
        }
    }

    private fun addTerm(term: Term) {
        this.terms.add(term)
    }

}