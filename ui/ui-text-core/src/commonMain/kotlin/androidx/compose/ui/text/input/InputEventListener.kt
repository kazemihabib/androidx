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

package androidx.compose.ui.text.input

/**
 * An interface of listening IME events.
 */
interface InputEventListener {
    /**
     * Called when IME sends some input events.
     *
     * @param editOps The list of edit operations.
     */
    fun onEditOperations(editOps: List<EditOperation>)

    /**
     * Called when IME triggered IME action.
     *
     * @param imeAction An IME action.
     */
    fun onImeAction(imeAction: ImeAction)
}