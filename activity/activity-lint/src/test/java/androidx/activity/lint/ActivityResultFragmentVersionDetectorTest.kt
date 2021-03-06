/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.activity.lint

import androidx.activity.lint.ActivityResultFragmentVersionDetector.Companion.FRAGMENT_VERSION
import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ActivityResultFragmentVersionDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = ActivityResultFragmentVersionDetector()

    override fun getIssues(): MutableList<Issue> =
        mutableListOf(ActivityResultFragmentVersionDetector.ISSUE)

    @Test
    fun expectPassRegisterForActivityResult() {
        lint().files(
            kotlin(
                """
                package com.example

                import androidx.activity.result.ActivityResultCaller
                import androidx.activity.result.contract.ActivityResultContract

                val launcher = ActivityResultCaller().registerForActivityResult(ActivityResultContract())
            """
            ),
            gradle(
                "build.gradle", """
                dependencies {
                    api("androidx.fragment:fragment:$FRAGMENT_VERSION")
                }
            """
            ).indented()
        )
            .run().expectClean()
    }

    @Test
    fun expectPassRegisterForActivityResultProject() {
        lint().files(
            kotlin(
                """
                package com.example

                import androidx.activity.result.ActivityResultCaller
                import androidx.activity.result.contract.ActivityResultContract

                val launcher = ActivityResultCaller().registerForActivityResult(ActivityResultContract())
            """
            ),
            gradle(
                "build.gradle", """
                dependencies {
                    implementation(project(":fragment:fragment-ktx"))
                }
            """
            ).indented()
        )
            .run().expectClean()
    }

    @Test
    fun expectFailRegisterForActivityResult() {
        lint().files(
            gradle(
                """
                dependencies {
                    api("androidx.fragment:fragment:1.3.0-alpha05")
                }
            """
            ),
            kotlin(
                """
                package com.example

                import androidx.activity.result.ActivityResultCaller
                import androidx.activity.result.contract.ActivityResultContract

                val launcher = ActivityResultCaller().registerForActivityResult(ActivityResultContract())
            """
            ).indented()
        )
            .run()
            .expect(
                """
                src/main/kotlin/com/example/test.kt:6: Error: Upgrade Fragment version to at least $FRAGMENT_VERSION. [InvalidFragmentVersionForActivityResult]
                val launcher = ActivityResultCaller().registerForActivityResult(ActivityResultContract())
                               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent()
            )
    }

    @Test
    fun expectFailRegisterForActivityResultInMethod() {
        lint().files(
            gradle(
                """
                dependencies {
                    api("androidx.fragment:fragment:1.3.0-alpha05")
                }
            """
            ),
            kotlin(
                """
                package com.example

                import androidx.activity.result.ActivityResultCaller
                import androidx.activity.result.contract.ActivityResultContract

                lateinit var launcher: ActivityResultLauncher

                fun foo() {
                    launcher = ActivityResultCaller().registerForActivityResult(ActivityResultContract())
                }
            """
            ).indented()
        )
            .run()
            .expect(
                """
                src/main/kotlin/com/example/test.kt:9: Error: Upgrade Fragment version to at least $FRAGMENT_VERSION. [InvalidFragmentVersionForActivityResult]
                    launcher = ActivityResultCaller().registerForActivityResult(ActivityResultContract())
                               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
            """.trimIndent()
            )
    }
}
