package com.djvk.sarPdfParser.exceptions

fun Exception.dump(): String {
    return "${this}\n${this.stackTrace.map { it.toString() + "\n" }}"
}

