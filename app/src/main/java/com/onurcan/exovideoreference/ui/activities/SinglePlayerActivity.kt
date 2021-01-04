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

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.databinding.ActivitySinglePlayerBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.ui.adapters.SingleVideoItemAdapter
import com.onurcan.exovideoreference.ui.contracts.ISinglePlayer
import com.onurcan.exovideoreference.ui.presenters.SinglePlayerPresenter
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.showToast
import com.r0adkll.slidr.Slidr
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_single_player.*
import video.HmsGmsVideoHelper

class SinglePlayerActivity : AppCompatActivity(), ISinglePlayer.ViewSingle {

    private lateinit var binding: ActivitySinglePlayerBinding

    private var url: String? = ""
    private var ytUrl: String? = ""
    private var type: Int? = 0
    private var ytUri = ""

    private val YOUTUBE_VIDEO_ID = "uZnWUZW1hQo"
    private val BASE_URL = "https://www.youtube.com"
    private val mYoutubeLink = "$BASE_URL/watch?v=$YOUTUBE_VIDEO_ID"

    lateinit var dialog: Dialog
    private lateinit var dCancel: CircleImageView
    private lateinit var dRecycler: RecyclerView

    private val hmsGmsVideoHelper: HmsGmsVideoHelper by lazy {
        HmsGmsVideoHelper(this)
    }

    private val presenter: SinglePlayerPresenter by lazy {
        SinglePlayerPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ExoVideoReference_TransStatusBar)
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        Slidr.attach(this)
        presenter.onViewsCreate()

        type = intent.getIntExtra("type", 0)
        when (type) {
            0 -> {
                showLogInfo(Constants.mSinglePlayerActivity, "url")
                url = intent.getStringExtra("url")
                hmsGmsVideoHelper.readyPlayer(url!!, url!!)
            }
            1 -> {
                showToast(this, "yt")
                ytUrl = intent.getStringExtra("ytUrl")
                extractYoutubeUrl()
            }
        }

        sp_more.setOnClickListener {
            dialog.show()
            dRecycler.layoutManager = LinearLayoutManager(this)
            val adapter = SingleVideoItemAdapter(this,this)
            dRecycler.adapter = adapter
            dRecycler.setHasFixedSize(true)
        }
        dCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun init() {
        initDialog()
    }

    @SuppressLint("StaticFieldLeak")
    override fun extractYoutubeUrl() {
        object : YouTubeExtractor(this) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                val iTags: List<Int> = listOf(22, 137, 18)
                for (iTag in iTags) {
                    val ytFile = ytFiles?.get(iTag)
                    if (ytFile != null) {
                        val dUrl = ytFile.url
                        if (dUrl != null && dUrl.isNotEmpty()) {
                            ytUri = Uri.parse(dUrl).toString()
                            showToast(this@SinglePlayerActivity, ytUri)
                        } else {
                            showLogError(Constants.mSinglePlayerActivity, "dUrl: Error null")
                        }
                    } else {
                        showLogError(Constants.mSinglePlayerActivity, "ytFile: Error null")
                    }
                }
                if (ytFiles != null) {
                    hmsGmsVideoHelper.playYoutube(ytFiles[17].url)
                    showToast(this@SinglePlayerActivity, ytFiles[17].url.toString())
                } else {
                    showLogError(Constants.mSinglePlayerActivity, "ytFiles: Error null")
                }
            }
        }.extract(mYoutubeLink, true, true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hmsGmsVideoHelper.onConfig(newConfig)
    }

    override fun initDialog() {
        dialog = Dialog(this, R.style.BlurTheme)
        dialog.window!!.attributes.windowAnimations = R.style.DialogSlide
        dialog.setContentView(R.layout.dialog_more)
        dialog.setCanceledOnTouchOutside(true)

        dCancel = dialog.findViewById(R.id.d_close_dialog)
        dRecycler = dialog.findViewById(R.id.d_video_item_recycler)
    }

    override fun onPause() {
        super.onPause()
        //hmsGmsVideoHelper.restartPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        hmsGmsVideoHelper.releasePlayer()
    }
}