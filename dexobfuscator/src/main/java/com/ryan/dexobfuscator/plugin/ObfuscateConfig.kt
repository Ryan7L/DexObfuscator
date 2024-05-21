package com.ryan.dexobfuscator.plugin

import org.gradle.api.Project

open class ObfuscateConfig(project: Project) {
    var enabled: Boolean = false
    var obfuscationDepth: Int = 1
    var obfuscationList: Array<String> = arrayOf()
    var blackList: Array<String> = arrayOf()
    var customDictionaryPath: String = ""
    var stringLength: Int = 4

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObfuscateConfig

        if (enabled != other.enabled) return false
        if (obfuscationDepth != other.obfuscationDepth) return false
        if (!obfuscationList.contentEquals(other.obfuscationList)) return false
        if (!blackList.contentEquals(other.blackList)) return false
        if (customDictionaryPath != other.customDictionaryPath) return false
        if (stringLength != other.stringLength) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + obfuscationDepth
        result = 31 * result + obfuscationList.contentHashCode()
        result = 31 * result + blackList.contentHashCode()
        result = 31 * result + customDictionaryPath.hashCode()
        result = 31 * result + stringLength
        return result
    }

}