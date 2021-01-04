/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.onurcan.exovideoreference.helper

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.webkit.URLUtil
import java.util.regex.Pattern

class YoutubeUrlValidatorHelper : TextWatcher {

    private var isValid = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        isValid = isValidYoutube(s)
    }

    companion object {
        private val Youtube_Pattern = Pattern.compile(
            "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+"
        )

        fun isValidYoutube(ytUrl: CharSequence?): Boolean {
            return ytUrl != null && Youtube_Pattern.matcher(ytUrl).matches()
        }
    }
}