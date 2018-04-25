package com.djvk.sarPdfParser

import org.apache.commons.cli.Options

object ArgumentParser {
    fun getDefaultOptions(): Options {
        val options = Options()
        options.addOption("d", "directory", true, "Directory to read PDF files from")

        return options
    }
}