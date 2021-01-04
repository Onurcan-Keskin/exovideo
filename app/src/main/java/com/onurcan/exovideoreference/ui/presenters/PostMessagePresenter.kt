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
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.helper.FirebaseDbHelper
import com.onurcan.exovideoreference.model.DataClass
import com.onurcan.exovideoreference.ui.contracts.IPostMessage
import com.onurcan.exovideoreference.ui.viewHolders.PostMessageViewHolder
import com.onurcan.exovideoreference.utils.showLogDebug

class PostMessagePresenter constructor(private val contract: IPostMessage.ViewPostMessage) :
    IPostMessage.PresenterPostMessage {

    override fun onViewsCreate() {
        contract.initViews()
        contract.initDB()
    }

    override fun setupRecycler(
        context: Context,
        currentID: String,
        query: Query,
        mRecycler: RecyclerView,
        postID: String
    ) {
        val mp = MediaPlayer.create(context, R.raw.heart_fall1)
        val options = FirebaseRecyclerOptions.Builder<DataClass.PostMessageDataClass>()
            .setQuery(query, DataClass.PostMessageDataClass::class.java).build()
        val adapterFire = object :
            FirebaseRecyclerAdapter<DataClass.PostMessageDataClass, PostMessageViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PostMessageViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_message_item, parent, false)
                return PostMessageViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: PostMessageViewHolder,
                position: Int,
                model: DataClass.PostMessageDataClass
            ) {
                val lisResId = getRef(position).key

                if (model.commenter_ID == currentID)
                    holder.bindComments(
                        model.commenter_image,
                        model.commenter_name + " (" + context.getString(R.string.me) + ")",
                        model.time,
                        model.comment,
                        model.comment_lovely
                    )
                else
                    holder.bindComments(
                        model.commenter_image,
                        model.commenter_name,
                        model.time,
                        model.comment,
                        model.comment_lovely
                    )
                var lovely = model.comment_lovely.toInt()
                val dbRef = FirebaseDbHelper.getPostMessageRef(postID).child(lisResId!!)
                holder.mLovely.setOnClickListener {
                    mp.start()
                    lovely++
                    dbRef!!.child("comment_lovely").setValue(lovely.toString())
                    holder.mLottieAnimationView.visibility = View.VISIBLE
                    holder.mLottieAnimationView.bringToFront()
                    holder.mLottieAnimationView.playAnimation()
                    showLogDebug(Constants.mPostMessagePresenter, "KEy: $lisResId")
                }
            }
        }
        adapterFire.startListening()
        mRecycler.adapter=adapterFire
    }


}
