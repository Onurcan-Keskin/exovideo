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

package com.onurcan.exovideoreference.model

object DataClass {
    data class ProfileVideoDataClass(
        val shareStat: String = "",
        val like: String = "",
        val timeDate: String = "",
        val timeMillis: String = "",
        val uploaderID: String = "",
        val videoUrl: String = "",
        val videoName:String = ""
    )

    data class UploadsShareableDataClass(
        val like: String = "",
        val timeDate: String = "",
        val timeMillis: String = "",
        val uploaderID: String = "",
        val videoUrl: String = "",
        val videoName:String = ""
    )

    data class PostMessageDataClass(
        val comment : String = "",
        val comment_lovely : String = "",
        val commenter_ID : String = "",
        val commenter_image : String = "",
        val commenter_name : String = "",
        val time : String = "",
        val type : String = ""
    )
}