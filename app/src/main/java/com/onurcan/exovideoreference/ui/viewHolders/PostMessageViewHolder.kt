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

package com.onurcan.exovideoreference.ui.viewHolders

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.utils.NumberConvertor
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostMessageViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    val parent = itemView

    val mCommenterLayout : LinearLayout
    val mViewReply : LinearLayout
    private var mCommenterCircle : CircleImageView
    private var mCommenterName : TextView
    private var mCommentTime : TextView
    private var mComment : TextView

    var mLottieAnimationView : LottieAnimationView

    private var totalUnder : TextView

    val mReply : TextView
    val mLovely : TextView

    init {
        mCommenterLayout = parent.findViewById(R.id.single_bottom_text_sender_layout)
        mViewReply = parent.findViewById(R.id.single_bottom_view_reply)

        totalUnder = parent.findViewById(R.id.totalUnderTV)

        mCommenterCircle = parent.findViewById(R.id.single_bottom_text_sender_circle)
        mCommenterName = parent.findViewById(R.id.single_bottom_text_sender_name)
        mCommentTime = parent.findViewById(R.id.single_bottom_text_get_time_ago)
        mComment = parent.findViewById(R.id.single_bottom_text)

        mLottieAnimationView = parent.findViewById(R.id.lottie_post_lovely)

        mReply = parent.findViewById(R.id.single_bottom_reply)
        mLovely = parent.findViewById(R.id.single_bottom_lovely)
    }

    fun bindTotalUnder(string: String){
        val pTotal = NumberConvertor.prettyCount(string.toLong())
        totalUnder.visibility = View.VISIBLE
        totalUnder.text = pTotal
    }

    fun bindComments(circleImage : String,
                     name : String,
                     time : String,
                     item : String,
                     lovely : String){
        Picasso.get().load(circleImage).centerCrop().fit().into(mCommenterCircle)
        mCommenterName.text = name
        mCommentTime.text = time
        mComment.text = item
        val pLovely = NumberConvertor.prettyCount(lovely.toLong())
        mLovely.text = pLovely
    }

}