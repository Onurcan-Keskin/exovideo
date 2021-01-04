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
import com.huawei.hime.util.dbutils.DbValues
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.appuser.AppUser
import com.onurcan.exovideoreference.helper.FirebaseDbHelper
import com.onurcan.exovideoreference.ui.contracts.IProfile
import com.onurcan.exovideoreference.utils.showToast

class ProfilePresenter : IProfile.PresenterProfile {

    fun moveShowToHide(context: Context, lisResUid: String) {
        DbValues.copyFromToPath(
            FirebaseDbHelper.getShareItem(lisResUid),
            FirebaseDbHelper.getUnSharedItem(lisResUid)
        )
        FirebaseDbHelper.getVideoFeedItem(
            AppUser.getUserId(),
            lisResUid
        ).child("shareStat").setValue("0")
        FirebaseDbHelper.getShareItem(lisResUid).removeValue()
        showToast(
            context,
            context.getString(R.string.moving_to_unshare)
        )
    }

    fun moveHideToShow(context: Context, lisResUid: String) {
        DbValues.copyFromToPath(
            FirebaseDbHelper.getUnSharedItem(lisResUid),
            FirebaseDbHelper.getShareItem(lisResUid)
        )
        FirebaseDbHelper.getVideoFeedItem(
            AppUser.getUserId(),
            lisResUid
        ).child("shareStat").setValue("1")
        FirebaseDbHelper.getUnSharedItem(lisResUid).removeValue()
        showToast(context, context.getString(R.string.moving_to_share))
    }

}