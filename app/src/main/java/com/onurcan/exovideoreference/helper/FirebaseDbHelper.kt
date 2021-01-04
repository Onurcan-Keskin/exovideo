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

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

object FirebaseDbHelper {

    fun rootRef() : DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    fun getUser(userID : String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("User").child(userID)
    }

    fun onDisconnect(db : DatabaseReference) : Task<Void> {
        return db.child("onlineStatus").onDisconnect().setValue(ServerValue.TIMESTAMP)
    }

    fun onConnect(db : DatabaseReference) : Task<Void> {
        return db.child("onlineStatus").setValue("true")
    }

    fun getUserInfo(userID : String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("UserInfo").child(userID)
    }

    fun getProfileFeed(userID : String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("UserFeed/Profile Pictures/$userID")
    }

    fun getVideoFeed(userID : String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("UserFeed/Video/$userID")
    }

    fun getVideoFeedItem(userID: String, listId:String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("UserFeed/Video/$userID/$listId")
    }

    fun getShared(): DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("uploads/Shareable")
    }

    fun getShareItem(listId: String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("uploads/Shareable/$listId")
    }

    fun getUnShared(): DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("uploads/UnShareable")
    }

    fun getUnSharedItem(listId: String): DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("uploads/UnShareable/$listId")
    }

    fun getNotShared(): DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("uploads/Unshareable")
    }

    fun getUserInfoRoot(): DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("UserInfo")
    }

    fun getPostMessageRootRef() : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("PostMessage")
    }

    fun getPostMessageRef(listID : String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("PostMessage/$listID")
    }

    fun getPostMessageUnderRef(postID : String, listID : String) : DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("PostMessageUnder/$postID/$listID/Message")
    }
}