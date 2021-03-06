/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.ui.core.demos.gestures

import androidx.compose.runtime.Composable
import androidx.compose.runtime.state
import androidx.ui.core.Alignment
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.ScaleObserver
import androidx.ui.core.gesture.dragGestureFilter
import androidx.ui.core.gesture.scaleGestureFilter
import androidx.ui.core.gesture.tapGestureFilter
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

/**
 * Simple demo that shows off how [dragGestureFilter] and [scaleGestureFilter] automatically
 * interoperate.
 */
@Composable
fun DragAndScaleGestureFilterDemo() {
    val size = state { 200.dp }
    val offset = state { Offset.Zero }
    val dragInScale = state { false }

    val scaleObserver = object : ScaleObserver {
        override fun onScale(scaleFactor: Float) {
            size.value *= scaleFactor
        }
    }

    val dragObserver = object : DragObserver {
        override fun onDrag(dragDistance: Offset): Offset {
            offset.value += dragDistance
            return dragDistance
        }
    }

    val onRelease: (Offset) -> Unit = {
        dragInScale.value = !dragInScale.value
    }

    val gestures =
        if (dragInScale.value) {
            Modifier
                .scaleGestureFilter(scaleObserver)
                .dragGestureFilter(dragObserver)
                .tapGestureFilter(onRelease)
        } else {
            Modifier
                .dragGestureFilter(dragObserver)
                .scaleGestureFilter(scaleObserver)
                .tapGestureFilter(onRelease)
        }

    val color =
        if (dragInScale.value) {
            Red
        } else {
            Blue
        }

    val (offsetX, offsetY) =
        with(DensityAmbient.current) { offset.value.x.toDp() to offset.value.y.toDp() }

    Column {
        Text("Demonstrates combining dragging with scaling.")
        Text("Drag and scale around.  Play with how slop works for both dragging and scaling. Tap" +
                " on the box to flip the order of scaling and dragging.  The behavior is always " +
                "the same because the 2 gesture filters are completely orthogonal.")
        Box(
            Modifier.fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .offset(offsetX, offsetY)
                .preferredSize(size.value)
                .then(gestures),
            backgroundColor = color
        )
    }
}
