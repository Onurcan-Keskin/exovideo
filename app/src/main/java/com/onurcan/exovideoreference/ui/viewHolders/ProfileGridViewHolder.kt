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

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.klinker.android.simple_videoview.SimpleVideoView
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.ui.activities.SinglePlayerActivity
import com.onurcan.exovideoreference.utils.expandView
import java.util.*

class ProfileGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View = itemView

    private val simpleVideo: SimpleVideoView
    val ppButton: ImageView

    val vidBack: LinearLayout
    val vidFront: RelativeLayout
    val bigPlayer: TextView
    val cancel: TextView
    val delete: TextView
    val share: TextView

    fun bindImage(videoUrl: String) {
        simpleVideo.start(Uri.parse(videoUrl))
    }

    fun startStop() {
        if (simpleVideo.isPlaying) {
            simpleVideo.pause()
            ppButton.visibility = View.VISIBLE
            ppButton.setImageResource(R.drawable.ic_pause)
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    ppButton.expandView()
                }
            }, 3000)
        } else {
            simpleVideo.play()
            ppButton.visibility = View.VISIBLE
            ppButton.setImageResource(R.drawable.ic_play)
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    ppButton.expandView()
                }
            }, 3000)
        }
    }

    fun startSinglePlayer(videoUrl: String) {
        bigPlayer.setOnClickListener {
            parent.context.startActivity(
                Intent(parent.context, SinglePlayerActivity::class.java)
                    .putExtra("url", videoUrl)
                    .putExtra("type", 0)
            )
        }
    }

    init {
        simpleVideo = parent.findViewById(R.id.grid_video)
        ppButton = parent.findViewById(R.id.pp_button)
        vidBack = parent.findViewById(R.id.vid_back)
        vidFront = parent.findViewById(R.id.vid_front)
        bigPlayer = parent.findViewById(R.id.big_player)
        cancel = parent.findViewById(R.id.cancel)
        delete = parent.findViewById(R.id.delete)
        share = parent.findViewById(R.id.share)
    }


}