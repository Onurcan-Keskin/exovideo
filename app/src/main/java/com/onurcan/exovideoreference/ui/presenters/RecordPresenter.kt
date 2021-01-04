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
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.klinker.android.simple_videoview.SimpleVideoView
import com.onurcan.exovideoreference.appuser.AppUser
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.helper.FirebaseDbHelper
import com.onurcan.exovideoreference.ui.contracts.IRecord
import com.onurcan.exovideoreference.utils.showToast
import java.io.File
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class RecordPresenter : IRecord.PresenterRecord {

    override fun getVideoTask(
        file: File,
        context: Context,
        vidName: String,
        simpleVideoView: SimpleVideoView
    ) {
        val uri = Uri.fromFile(file)

        val feedRef = "UserFeed/Video/${AppUser.getUserId()}"
        val uploadsRef = "uploads/Shareable"
        val timeDate = DateFormat.getDateTimeInstance().format(Date())
        val millis = System.currentTimeMillis().toString()
        val feedPush = Constants.fFeedRef.push()
        val pKey = feedPush.key.toString()

        val fUploadsStorageRef =
            FirebaseStorage.getInstance().reference.child("uploads/${AppUser.getUserId()}")
                .child("Videos")
                .child(System.currentTimeMillis().toString() + ".mp4")

        try {
            val uploadTask = fUploadsStorageRef.putFile(uri)
            uploadTask.continueWith {
                if (!it.isSuccessful) {
                    it.exception?.let { t -> throw t }
                }
                fUploadsStorageRef.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(context, "Recording Ended")
                    val a = it.result.toString()
                    it.result!!.addOnSuccessListener { uploadTask ->
                        val videoUrl = uploadTask.toString()
                        showToast(context, "Saving to Video View.")
                        feedMapper(pKey, timeDate, millis, videoUrl, feedRef, vidName)
                        uploadMapper(pKey, timeDate, millis, videoUrl, uploadsRef, vidName)
                        simpleVideoView.start(videoUrl)
                    }.addOnFailureListener {
                        showToast(context, "Uploading error.")
                    }
                } else {
                    showToast(context, "Get Task error.")
                }
            }
        } catch (e: Exception) {
            e.message?.let { showToast(context, it) }
        }
    }

    private fun feedMapper(
        pKey: String,
        timeDate: String,
        timeMillis: String,
        vidUrl: String,
        feedPath: String,
        vidName: String
    ) {
        val feedMap: MutableMap<String, String> = HashMap()

        feedMap["shareStat"] = "1"
        feedMap["like"] = "0"
        feedMap["timeDate"] = timeDate
        feedMap["timeMillis"] = timeMillis
        feedMap["uploaderId"] = AppUser.getUserId()
        feedMap["videoUrl"] = vidUrl
        feedMap["videoName"] = vidName

        val mapFeed: MutableMap<String, Any> = HashMap()
        mapFeed["$feedPath/$pKey"] = feedMap
        FirebaseDbHelper.rootRef().updateChildren(mapFeed)
    }

    private fun uploadMapper(
        pKey: String,
        timeDate: String,
        timeMillis: String,
        vidUrl: String,
        uploadPath: String,
        vidName: String
    ) {
        val uploadMap: MutableMap<String, String> = HashMap()

        uploadMap["like"] = "0"
        uploadMap["timeDate"] = timeDate
        uploadMap["timeMillis"] = timeMillis
        uploadMap["uploaderId"] = AppUser.getUserId()
        uploadMap["videoUrl"] = vidUrl
        uploadMap["videoName"] = vidName

        val mapUpload: MutableMap<String, Any> = HashMap()
        mapUpload["$uploadPath/$pKey"] = uploadMap
        FirebaseDbHelper.rootRef().updateChildren(mapUpload)
    }
}