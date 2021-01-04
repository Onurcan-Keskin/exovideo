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

import android.content.Context
import android.os.Environment
import com.google.firebase.storage.FirebaseStorage
import com.onurcan.exovideoreference.appuser.AppUser
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        /* Firebase References */
        val fUserInfoDB = FirebaseDbHelper.getUserInfo(AppUser.getUserId())
        val fFeedRef = FirebaseDbHelper.getVideoFeed(AppUser.getUserId())
        val fSharedRef = FirebaseDbHelper.getShared()
        val fUploadsStorageRef =
            FirebaseStorage.getInstance().reference.child("uploads/${AppUser.getUserId()}")
                .child("Videos")

        // Delay
        const val DELAY_SECOND = 1000L

        //Minimum Video you want to buffer while Playing
        const val MIN_BUFFER_DURATION = 3000

        //Max Video you want to buffer during PlayBack
        const val MAX_BUFFER_DURATION = 5000

        //Min Video you want to buffer before start Playing it
        const val MIN_PLAYBACK_START_BUFFER = 1500

        //Min video You want to buffer when user resumes video
        const val MIN_PLAYBACK_RESUME_BUFFER = 5000

        /* Timer */
        const val MS_TO_SECOND = 1000
        const val SECOND_TO_MINUTE = 60
        const val SECOND_TO_HOUR = 60 * 60

        /* Recording Video */
        const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS" //"yyyy-MM-dd-HH-mm-ss-SSS"
        const val VIDEO_EXTENSION = ".mp4"
        const val USE_FRAME_PROCESSOR = true
        const val DECODE_BITMAP = false
        var recPath = Environment.getExternalStorageDirectory().path + "/Pictures/ExoVideoReference"

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(
                    recPath
                ).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
        }

        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.ROOT)
                    .format(System.currentTimeMillis()) + extension
            )

        /* AGCErrorCode */
        const val SUCCESS_CODE = 200
        const val ERROR_CODE_AGC_USER_NOT_FOUND = 80001
        const val ERROR_GOOGLE_TOKEN_VALUE_NULL = 80002
        const val ERROR_PROFILE_JSON_EXCEPTION = 80003
        const val ERROR_GOOGLE_TOKEN_EXCEPTION = 80004
        const val ERROR_AGC_GOOGLE_SIGNIN = 80006
        const val ERROR_HUAWEI_LOGIN_FAILED = 80009
        const val ERROR_HUAWEI_TOKEN_FAILED = 80010
        const val ERROR_EMAIL_AUTH_FAILED = 80012
        const val ERROR_HUAWEI_LOGIN_CANCEL = 80013
        const val ERROR_GOOGLE_LOGIN_CANCEL = 80014

        const val AGC_USER_NOT_FOUND = "AGC user not found"
        const val AGC_USER_LOGIN_SUCCESS = "Successfully login into AGC"
        const val GOOGLE_TOKEN_VALUE_NULL = "Google token value is null"
        const val FACEBOOK_LOGIN_CANCEL = "Facebook login cancelled"
        const val HUAWEI_LOGIN_CANCEL = "Huawei login cancelled"
        const val GOOGLE_LOGIN_CANCEL = "Google login cancelled"
        const val ALREADY_REGISTERED = "been registered"

        /* Tag Identifier */
        const val mHmsLogin = "HmsLoginHelper"
        const val mMainPresenter = "MainPresenter"
        const val mMainActivity = "MainActivity"
        const val mLoginActivity = "LoginActivity"
        const val mProfileActivity = "ProfileActivity"
        const val mRecordActivity = "RecordActivity"
        const val mExoVideoApp = "application.ExoVideoApp"
        const val mHmsGmsVideoViewHolder = "HmsGmsVideoViewHolder"
        const val mHmsGmsVideoHelper="HmsGmsVideoHelper"
        const val mSinglePlayerActivity="SinglePlayerActivity"
        const val mPostMessagePresenter="PostMessagePresenter"
        const val mPostMessageActivity="PostMessageActivity"
    }
}