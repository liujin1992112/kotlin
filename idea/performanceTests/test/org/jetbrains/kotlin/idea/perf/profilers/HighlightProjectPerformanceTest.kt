/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.perf.profilers

import org.jetbrains.kotlin.idea.perf.AbstractPerformanceProjectsTest
import org.jetbrains.kotlin.idea.perf.Stats
import org.jetbrains.kotlin.idea.perf.Stats.Companion.tcSuite
import org.jetbrains.kotlin.idea.perf.WarmUpProject
import org.jetbrains.kotlin.idea.perf.allFilesWithExtension
import org.jetbrains.kotlin.idea.testFramework.ProjectOpenAction
import java.io.File

class HighlightProjectPerformanceTest : AbstractPerformanceProjectsTest() {

    companion object {

        @JvmStatic
        val hwStats: Stats = Stats("helloWorld project")

        @JvmStatic
        val warmUp = WarmUpProject(hwStats)

        init {
            // there is no @AfterClass for junit3.8
            Runtime.getRuntime().addShutdownHook(Thread { hwStats.close() })
        }

    }

    override fun setUp() {
        super.setUp()
        warmUp.warmUp(this)
    }

    fun testHighlightAllKtFilesInProject() {
        for (projectName in listOf("space")) {
            tcSuite("$projectName project") {
                val stats = Stats("$projectName project")
                stats.use { stat ->
                    perfGradleBasedProject(projectName, stat)

                    val project = myProject!!
                    project.projectFilePath
                    val projectDir = File("../$projectName")
                    projectDir.allFilesWithExtension("kt").forEach {
                        println("$it")
                    }
                }
            }
        }
    }

    private fun perfGradleBasedProject(name: String, stats: Stats) {
        myProject = perfOpenProject(
            name = name,
            stats = stats,
            note = "",
            path = "../$name",
            openAction = ProjectOpenAction.GRADLE_PROJECT,
            fast = true
        )
    }
}