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

package com.onurcan.exovideoreference.ui.presenters

import android.content.Context
import android.content.Intent
import com.onurcan.exovideoreference.ui.activities.MainActivity
import com.onurcan.exovideoreference.ui.activities.ProfileActivity
import com.onurcan.exovideoreference.ui.activities.RecordActivity

class MainPresenter {

    fun gotoProfile(context: Context) {
        context.startActivity(Intent(context, ProfileActivity::class.java))
    }

    fun gotoRecord(context: Context) {
        context.startActivity(Intent(context, RecordActivity::class.java))
    }

    fun restart(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java))
    }
}