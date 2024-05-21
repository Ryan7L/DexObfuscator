package com.ryan.dexobfuscator.plugin

import com.googlecode.dex2jar.tools.Dex2jarCmd
import com.googlecode.dex2jar.tools.Jar2Dex
import org.jf.DexLib2Utils
import org.jf.util.TrieTree
import top.niunaijun.obfuscator.ObfuscatorConfiguration
import java.io.File

//object ObfuscatorDex {
//    fun obfuscate(
//        dir: String,
//        obfuscationDepth: Int,
//        obfuscationList: Array<String>,
//        blacklist: Array<String>,
//        mappingFile: String?
//    ) {
//
//        val file = File(dir)
//        val mapping = Mapping(mappingFile)
//        if (file.isDirectory) {
//            val files = file.listFiles()
//            files?.let {
//                for (input in it) {
//                    if (input.isFile) {
//                        handleDex(input, obfuscationDepth, obfuscationList, blacklist, mapping)
//                    } else {
//                        obfuscate(
//                            input.absolutePath,
//                            obfuscationDepth,
//                            obfuscationList,
//                            blacklist,
//                            mappingFile
//                        )
//                    }
//                }
//            }
//        } else {
//            handleDex(file, obfuscationDepth, obfuscationList, blacklist, mapping)
//        }
//
//    }
//
//    fun handleDex(
//        input: File,
//        obfuscationDepth: Int,
//        obfuscationList: Array<String>,
//        blacklist: Array<String>,
//        mapping: Mapping
//    ) {
//        if (!input.absolutePath.endsWith(".dex")) {
//            return
//        }
//        var tempJar: File? = null
//        var splitDex: File? = null
//        var obfuscatedDex: File? = null
//        try {
//            tempJar =
//                File(input.parent, "${System.currentTimeMillis()}obfuscated.jar${input.name}.jar")
//            splitDex = File(input.parent, "${System.currentTimeMillis()}split${input.name}.dex")
//            obfuscatedDex =
//                File(input.parent, "${System.currentTimeMillis()}obfuscated${input.name}.dex")
//            val obfuscationClassList = obfuscationList.toMutableList()
//            val blacklistClassList = blacklist.toMutableList()
//            val obfuscationTree = TrieTree()
//            obfuscationTree.addAll(obfuscationClassList)
//            for (clazz in mapping.mapping.keys) {
//                if (obfuscationTree.search(clazz)){
//                    val originalClass = mapping.mapping[clazz]
//                    if (originalClass != null) {
//                        println("mapping ---> $clazz ---> originalClass ---> $originalClass")
//                        obfuscationClassList.add(originalClass)
//                    }
//                }
//
//            }
//            val blacklistClassTree = TrieTree()
//            blacklistClassTree.addAll(blacklistClassList)
//            val tmpBlackClazz = ArrayList<String>(blacklistClassList)
//            for (clazz in tmpBlackClazz) {
//                if (blacklistClassTree.search(clazz)){
//                    val originalClass = mapping.mapping[clazz]
//                    if (originalClass != null) {
//                        blacklistClassList.add(originalClass)
//                    }
//                }
//            }
//            val len = DexLib2Utils.splitDex(input, splitDex, obfuscationClassList, blacklistClassList)
//            if (len < 0) {
//                println("Obfuscator class not found")
//            }
//            Dex2jarCmd(object : ObfuscatorConfiguration() {
//                /**
//                 * 混淆深度
//                 */
//                override fun getObfDepth(): Int {
//                    return obfuscationDepth
//                }
//
//                /**
//                 * 是否处理此method
//                 * @param className className
//                 * @param methodName methodName
//                 */
//                override fun accept(className: String?, methodName: String?): Boolean {
//                    println("className ---> ${className}，methodName ---> $methodName")
//                    return super.accept(className, methodName)
//                }
//            }).doMain("-f", splitDex.absolutePath, "-o", tempJar.absolutePath)
//            DexLib2Utils.mergerAndCoverDexFile(input, obfuscatedDex, input)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            tempJar?.delete()
//            splitDex?.delete()
//            obfuscatedDex?.delete()
//        }
//    }
//}


object ObfuscatorDex {
    @JvmStatic
    fun obfuscate(
        dir: String,
        obfuscationDepth: Int,
        obfuscationList: Array<String>,
        blacklist: Array<String>,
        mappingFile: String?
    ) {
        val file = File(dir)
        val mapping = Mapping(mappingFile)
        if (file.isDirectory) {
            val files = file.listFiles() ?: return
            for (input in files) {
                if (input.isFile) {
                    handleDex(input, obfuscationDepth, obfuscationList, blacklist, mapping)
                } else {
                    obfuscate(
                        input.absolutePath,
                        obfuscationDepth,
                        obfuscationList,
                        blacklist,
                        mappingFile
                    )
                }
            }
        } else {
            handleDex(file, obfuscationDepth, obfuscationList, blacklist, mapping)
        }
    }

    private fun handleDex(
        input: File,
        depth: Int,
        obfuscationList: Array<String>,
        blacklist: Array<String>,
        mapping: Mapping
    ) {
        if (!input.absolutePath.endsWith(".dex")) return
        var tempJar: File? = null
        var splitDex: File? = null
        var obfuscatedDex: File? = null
        try {
            tempJar = File(input.parent, "${System.currentTimeMillis()}obfuscated${input.name}.jar")
            splitDex = File(input.parent, "${System.currentTimeMillis()}split${input.name}.dex")
            obfuscatedDex =
                File(input.parent, "${System.currentTimeMillis()}obfuscated${input.name}.dex")
            val obfuscationClassList = obfuscationList.toMutableList()
            val blacklistClassList = blacklist.toMutableList()

            val obfuscationTree = TrieTree()
            obfuscationTree.addAll(obfuscationClassList)

            for (clazz in mapping.mapping.keys) {
                if (obfuscationTree.search(clazz)) {
                    val originalClass = mapping.mapping[clazz]
                    if (originalClass != null) {
                        println("mapping : $clazz ---> $originalClass")
                        obfuscationClassList.add(originalClass)
                    }
                }
            }

            val blacklistClassTree = TrieTree()
            blacklistClassTree.addAll(blacklistClassList)
            val tmpBlackClass = ArrayList(blacklistClassList)
            for (clazz in tmpBlackClass) {
                if (blacklistClassTree.search(clazz)) {
                    val originalClass = mapping.mapping[clazz]
                    if (originalClass != null) {
                        println("mapping : $clazz ---> $originalClass")
                        blacklistClassList.add(originalClass)
                    }
                }
            }
            val l = DexLib2Utils.splitDex(input, splitDex, obfuscationClassList, blacklistClassList)
            if (l <= 0) {
                println("Obfuscator Class not found")
                return
            }

            Dex2jarCmd(object : ObfuscatorConfiguration() {
                override fun getObfDepth(): Int {
                    return depth
                }

                override fun accept(className: String, methodName: String): Boolean {
                    println("obfuscate Class: $className#$methodName")
                    return super.accept(className, methodName)
                }
            }).doMain("-f", splitDex.absolutePath, "-o", tempJar.absolutePath)
            Jar2Dex().doMain("-f", "-o", obfuscatedDex.absolutePath, tempJar.absolutePath)
            DexLib2Utils.mergerAndCoverDexFile(input, obfuscatedDex, input)
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            tempJar?.delete()
            splitDex?.delete()
            obfuscatedDex?.delete()
        }
    }
}
