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

package com.onurcan.exovideoreference.ui.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.appuser.AppUser
import com.onurcan.exovideoreference.databinding.ActivityPostMessageBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.helper.FirebaseDbHelper
import com.onurcan.exovideoreference.ui.contracts.IPostMessage
import com.onurcan.exovideoreference.ui.presenters.PostMessagePresenter
import com.onurcan.exovideoreference.utils.showLogError
import com.r0adkll.slidr.Slidr
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class PostMessageActivity : AppCompatActivity(), IPostMessage.ViewPostMessage {

    private lateinit var binding: ActivityPostMessageBinding
    private val presenter:PostMessagePresenter by lazy {
        PostMessagePresenter(this)
    }

    private val currentID = AppUser.getUserId()

    private lateinit var postMessageDBRef: DatabaseReference
    private lateinit var query: Query
    private lateinit var postUploadsRef: DatabaseReference

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mp: MediaPlayer

    private lateinit var commenterName: String
    private lateinit var commenterImg: String

    private lateinit var commentGroupID: String
    private lateinit var commentID: String

    private var listID :String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title=getString(R.string.thread)
        Slidr.attach(this)
        presenter.onViewsCreate()

        binding.sendButton.setOnClickListener {
            sendComments()
            binding.sendInput.setText("")
        }
    }

    override fun initViews() {
        listID = intent.getStringExtra("listID")

        mp = MediaPlayer.create(this, R.raw.message_send)
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout =true
        mLayoutManager.stackFromEnd = true
        binding.messageList.layoutManager = mLayoutManager
    }

    override fun initDB(){
        postUploadsRef = FirebaseDbHelper.getShareItem(listID!!)
        postMessageDBRef = FirebaseDbHelper.getPostMessageRef(listID!!)
        query =postMessageDBRef.orderByChild("comment_lovely")
        presenter.setupRecycler(this,currentID,query,binding.messageList,listID!!)

        FirebaseDbHelper.getUserInfo(currentID).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commenterName = snapshot.child("nameSurname").value.toString()
                commenterImg = snapshot.child("photoUrl").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(Constants.mPostMessageActivity,error.toString())
            }
        })
    }

    override fun sendComments() {
        val comments = binding.sendInput.text.toString()
        if (comments!=""){
            mp.start()
            val commentPush = FirebaseDbHelper.rootRef()
                .child("PostMessage")
                .child(listID!!)
                .child(currentID).push()
            commentGroupID = commentPush.key.toString()

            val cmPsh = FirebaseDbHelper.rootRef()
                .child("PostMessage")
                .child(listID!!).push()
            commentID = cmPsh.key.toString()

            val commentsRef = "PostMessage/$listID/$commentID"
            val commentUnderRef = "PostMessageUnder/$listID/$commentID"

            val commentMap: MutableMap<String,String> = HashMap()
            commentMap["comment"] = comments
            commentMap["time"] = DateFormat.getDateTimeInstance().format(Date())
            commentMap["commenter_name"] = commenterName
            commentMap["commenter_image"] = commenterImg
            commentMap["commenter_ID"] = currentID
            commentMap["type"] = "text"
            commentMap["comment_lovely"] = "0"

            val mapGroup:MutableMap<String,Any> = HashMap()
            mapGroup[commentUnderRef] = commentMap
            FirebaseDbHelper.rootRef().updateChildren(mapGroup)

            val mapContent: MutableMap<String,Any> = HashMap()
            mapContent[commentsRef] = commentMap
            FirebaseDbHelper.rootRef().updateChildren(mapContent)
        }
    }
}