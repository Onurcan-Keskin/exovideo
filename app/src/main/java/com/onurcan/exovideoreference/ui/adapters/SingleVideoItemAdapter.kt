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

package com.onurcan.exovideoreference.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.ui.activities.SinglePlayerActivity
import video.HmsGmsVideoHelper

class SingleVideoItemAdapter(context: Context,activity: SinglePlayerActivity) :
    RecyclerView.Adapter<SingleVideoItemAdapter.ViewHolder>() {

    val act=activity
    private val dataUrlArray = context.resources.getStringArray(R.array.data_url)
    private val dataNameArray = context.resources.getStringArray(R.array.data_name)

    private val hmsGmsVideoHelper: HmsGmsVideoHelper by lazy {
        HmsGmsVideoHelper(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindVidItem(dataNameArray[position], dataUrlArray[position])

        holder.parent.setOnClickListener {
            hmsGmsVideoHelper.readyPlayer(dataUrlArray[position],dataNameArray[position])
            act.dialog.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return dataUrlArray.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parent = itemView

        var vidName: TextView
        var vidUrl: TextView

        init {
            vidName = parent.findViewById(R.id.s_name)
            vidUrl = parent.findViewById(R.id.s_url)
        }

        fun bindVidItem(vid_name: String, vid_url: String) {
            vidName.text = vid_name
            vidUrl.text = vid_url
        }
    }


}