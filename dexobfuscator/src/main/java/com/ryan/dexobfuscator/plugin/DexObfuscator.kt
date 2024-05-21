package com.ryan.dexobfuscator.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import java.io.File

class DexObfuscator : Plugin<Project> {
    private val PLUGIN_NAME = "DexObfuscator"
    private lateinit var mProject: Project

    companion object {
        lateinit var obfuscateConfig: ObfuscateConfig
        val mTaskMapping = mutableMapOf<String, String>()
    }

    override fun apply(project: Project) {
        mProject = project
        val android = project.extensions.findByType(AppExtension::class.java)
        project.configurations.create(PLUGIN_NAME)
            .extendsFrom(project.configurations.getByName("implementation"))
        obfuscateConfig =
            project.extensions.create(PLUGIN_NAME, ObfuscateConfig::class.java, project)
        mTaskMapping.clear()

        project.afterEvaluate {
            println("==========================DexObfuscator Config==========================")
            println(obfuscateConfig.toString())
            DictionaryGenerator.generateDictionary(
                obfuscateConfig.customDictionaryPath,
                obfuscateConfig.stringLength
            )
            println("==========================DexObfuscator Config==========================")
        }

        project.afterEvaluate {
            if (!obfuscateConfig.enabled) {
                return@afterEvaluate
            }
            val action = object : Action<Task> {
                override fun execute(task: Task) {
                    task.outputs.files.forEach { element ->
                        val file = File(element.toString())

                        ObfuscatorDex.obfuscate(
                            file.absolutePath,
                            obfuscateConfig.obfuscationDepth,
                            obfuscateConfig.obfuscationList,
                            obfuscateConfig.blackList,
                            mTaskMapping[task.name]
                        )
                    }
                }
            }
            val tasks = mutableListOf<Task>()
            android?.applicationVariants?.all { applicationVariant ->
                var mappingFile: File? = null
                if (applicationVariant.buildType.isMinifyEnabled) {
                    mappingFile = applicationVariant.mappingFile
                }
                val buildType = applicationVariant.buildType.name.upperCaseFirst()
                var empty = true
                applicationVariant.productFlavors.forEach { _ ->
                    val flavorName = applicationVariant.flavorName.upperCaseFirst()
                    addOtherTask(tasks, flavorName, buildType, mappingFile)
                    empty = false
                    return@forEach
                }
                if (empty) {
                    addOtherTask(tasks, "", buildType, mappingFile)
                }
            }

            tasks.forEach { task ->
                task.doLast(action)
            }
            if (tasks.isEmpty()) {
                System.err.println("This gradle version is not applicable. Please submit issues in https://github.com/CodingGay/BlackObfuscator-ASPlugin")
            }
        }
    }

    private fun addOtherTask(
        tasks: MutableList<Task>,
        name: String,
        buildType: String,
        mappingFile: File?
    ) {
        addTask("mergeDex${name}${buildType}", tasks, mappingFile)
        addTask("mergeLibDex${name}${buildType}", tasks, mappingFile)
        addTask("mergeProjectDex${name}${buildType}", tasks, mappingFile)
        addTask("transformDexArchiveWithDexMergerFor${name}${buildType}", tasks, mappingFile)
        addTask("minify${name}${buildType}WithR8", tasks, mappingFile)

        println("$name$buildType mappingFile $mappingFile")
    }

    private fun String.upperCaseFirst(): String {
        if (this.isEmpty()) return this
        val arr = this.toCharArray()
        arr[0] = arr[0].uppercaseChar()
        return String(arr)
    }

    private fun addTask(name: String, tasks: MutableList<Task>, mappingFile: File?) {
        try {
            // Protected code
            val task = mProject.tasks.getByName(name)
            if (!tasks.contains(task)) {
                tasks.add(task)
                if (mappingFile != null) {
                    mTaskMapping[task.name] = mappingFile.absolutePath
                }
                println("add Task $name")
            }
        } catch (e1: UnknownTaskException) {
            // Catch block
        }
    }
}
