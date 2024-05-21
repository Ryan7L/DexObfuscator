package com.ryan.dexobfuscator.plugin

import top.niunaijun.obfuscator.ObfDic
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object DictionaryGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        generateDictionary("D:\\DevelopmentTools\\file.txt", 4)
    }

    fun generateDictionary(dictionaryPath: String?, stringLength: Int) {
        val characters = mutableListOf<Char>()
        // Step 1: 读取文件并存储所有字符
        try {
            getInputStream(dictionaryPath).use { inputStream ->
                BufferedReader(
                    InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                    )
                ).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line!!.length == 1) {
                            characters.add(line!![0])
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        // Step 2: 随机选择10个字符
        if (characters.size < 10) {
//            println("文件中的字符数量不足10个")
            println("Dictionary has less than 10 characters")
            return
        }
        characters.shuffle()
        val selectedCharacters = characters.subList(0, 10)
        // Step 3: 生成由一到五个字符组成的所有不重复字符串
        val uniqueStrings = HashSet<String>()
        val sb = StringBuilder()
        selectedCharacters.forEach { sb.append(it) }
        println("CHAR:$sb")
        for (length in 1..stringLength) {
            generatePermutations("", sb.toString(), length, uniqueStrings)
        }
        ObfDic.setDic(uniqueStrings.toTypedArray())
        println("Size=" + uniqueStrings.size)
        // 打印生成的不重复字符串
        uniqueStrings.forEach { println(it) }
    }

    @Throws(IOException::class)
    private fun getInputStream(path: String?): InputStream {
        return if (!path.isNullOrEmpty()) {
            FileInputStream(path)
        } else {
            // 使用默认的资源路径下的 dic.txt 文件
            DictionaryGenerator::class.java.classLoader.getResourceAsStream("dictionary.txt") as InputStream
        }
    }

    private fun generatePermutations(
        prefix: String,
        str: String,
        maxLength: Int,
        permutations: MutableSet<String>
    ) {
        if (prefix.length == maxLength) {
            permutations.add(prefix)
            return
        }

        for (i in str.indices) {
            generatePermutations(
                prefix + str[i],
                str.substring(0, i) + str.substring(i + 1),
                maxLength,
                permutations
            )
        }
    }
}


