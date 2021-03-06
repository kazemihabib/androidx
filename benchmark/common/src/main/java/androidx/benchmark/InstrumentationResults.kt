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

package androidx.benchmark

import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import java.text.NumberFormat

/**
 * Provides way to report additional results via `Instrumentation.sendStatus()` / `addResult()`.
 */
internal object InstrumentationResults {
    private const val STUDIO_OUTPUT_KEY_PREFIX = "android.studio.display."
    private const val STUDIO_OUTPUT_KEY_ID = "benchmark"

    private const val ADDITIONAL_FILE_OUTPUT_KEY_PREFIX = "additionalTestOutputFile_"

    private fun ideSummaryLineWrapped(key: String, nanos: Long, allocations: Long?): String {
        val warningLines =
            Errors.acquireWarningStringForLogging()?.split("\n") ?: listOf()
        return (warningLines + ideSummaryLine(key, nanos, allocations))
            // remove first line if empty
            .filterIndexed { index, it -> index != 0 || it.isNotBlank() }
            // join, prepending key to everything but first string,
            // to make each line look the same
            .joinToString("\n$STUDIO_OUTPUT_KEY_ID: ")
    }

    // NOTE: this summary line will use default locale to determine separators. As
    // this line is only meant for human eyes, we don't worry about consistency here.
    internal fun ideSummaryLine(key: String, nanos: Long, allocations: Long?): String {
        val numberFormat = NumberFormat.getNumberInstance()
        return listOfNotNull(
            // 13 alignment is enough for ~10 seconds
            "%13s ns".format(numberFormat.format(nanos)),
            // 9 alignment is enough for ~10 million allocations
            allocations?.run {
                "%8s allocs".format(numberFormat.format(allocations))
            },
            key
        ).joinToString("    ")
    }

    internal fun getIdeSummaryLine(testName: String, nanos: Long, allocations: Long?) =
        Bundle().also {
            it.putString(
                STUDIO_OUTPUT_KEY_PREFIX + STUDIO_OUTPUT_KEY_ID,
                ideSummaryLineWrapped(testName, nanos, allocations)
            )
        }

    internal fun reportAdditionalFileToCopy(key: String, absoluteFilePath: String) {
        reportBundle(Bundle().also {
            it.putString(ADDITIONAL_FILE_OUTPUT_KEY_PREFIX + key, absoluteFilePath)
        })
    }

    internal fun report(bundle: Bundle) {
        reportBundle(bundle)
    }

    /**
     * Report results bundle to instrumentation
     *
     * Before addResults() was added in the platform, we use sendStatus(). The constant '2'
     * comes from IInstrumentationResultParser.StatusCodes.IN_PROGRESS, and signals the
     * test infra that this is an "additional result" bundle, equivalent to addResults()
     * NOTE: we should a version check to call addResults(), but don't yet due to b/155103514
     */
    private fun reportBundle(bundle: Bundle) {
        InstrumentationRegistry
            .getInstrumentation()
            .sendStatus(2, bundle)
    }
}