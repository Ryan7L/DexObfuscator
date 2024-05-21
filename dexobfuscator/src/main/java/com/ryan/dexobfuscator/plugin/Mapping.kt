package com.ryan.dexobfuscator.plugin

import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset

class Mapping(val filePath: String?) {
    var file: File? = null
    val mapping = HashMap<String, String>()

    init {
        filePath?.let {
            file = File(it)
            parser()
        }
    }

    private fun parser() {
        if (file == null || !file!!.exists()) {
            return
        }
        try {
            val strings = FileUtils.readLines(file, Charset.defaultCharset())
            for (s in strings) {
                if (s.startsWith("#") || s.startsWith(" ")) {
                    continue
                }
                val split = s.split("->")
                if (split.size != 2) {
                    continue
                }
                //                System.out.println("add mapping : " + string);
                mapping[split[0]] = split[1].substring(0, split[1].length - 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}