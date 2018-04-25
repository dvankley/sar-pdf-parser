package com.djvk.sarPdfParser

import org.apache.commons.cli.DefaultParser

object Main {
    @JvmStatic fun main(args : Array<String>) {
        val parser = DefaultParser()
        val line = parser.parse(ArgumentParser.getDefaultOptions(), args)

        println("Target directory: ${line.getOptionValue("d")}")
    }
}
