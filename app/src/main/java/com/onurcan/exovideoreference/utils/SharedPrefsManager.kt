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

package com.onurcan.exovideoreference.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private var mSharedPreferences: SharedPreferences =
        context.getSharedPreferences("filename", Context.MODE_PRIVATE)
    private var mSharedLanguagePrefs: SharedPreferences =
        context.getSharedPreferences("languageName", Context.MODE_PRIVATE)

    fun setNightModeState(state: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean("NightMode", state)
        editor.commit()
    }

    fun loadNightModeState(): Boolean {
        return mSharedPreferences.getBoolean("NightMode", false)
    }

    fun setLanguage(state: String) {
        val editor = mSharedLanguagePrefs.edit()
        editor.putString("tr", state)
        editor.commit()
    }

    fun loadLanguage(): String? {
        return mSharedLanguagePrefs.getString("tr","en")
    }
}