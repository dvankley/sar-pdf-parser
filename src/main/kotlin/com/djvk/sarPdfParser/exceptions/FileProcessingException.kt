package com.djvk.sarPdfParser.exceptions

class FileProcessingException(private val fileName: String, exception: Exception): Exception(exception.message, exception.cause) {
}