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

package video

import android.content.res.Configuration
import android.graphics.SurfaceTexture
import android.os.Handler
import android.view.SurfaceHolder
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.huawei.hms.videokit.player.WisePlayer
import com.huawei.hms.videokit.player.common.PlayerConstants
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.databinding.ActivitySinglePlayerBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.ui.activities.SinglePlayerActivity
import com.onurcan.exovideoreference.utils.expandView
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import contracts.OnPlayWindowListener
import video.contracts.OnWisePlayerListener

class HmsGmsVideoHelper(context: SinglePlayerActivity) : OnPlayWindowListener,
    OnWisePlayerListener {

    val cntxt = context
    val binding: ActivitySinglePlayerBinding

    private var isPlaying = false
    private val mStopHandler = false

    private val player: WisePlayer? by lazy {
        showLogInfo(Constants.mHmsGmsVideoHelper, "Player WisePlayerInit")
        WisePlayerInit.createPlayer()
    }

    private val mHandler: Handler by lazy { Handler() }

    private val runnable: Runnable by lazy {
        Runnable {
            configureContentV()
            configureControlV()
            if (!mStopHandler) {
                mHandler.postDelayed(runnable, Constants.DELAY_SECOND)
            }
        }
    }

    init {
        binding = ActivitySinglePlayerBinding.inflate(cntxt.layoutInflater)
        val view = binding.root
        cntxt.setContentView(view)
        initUI()
    }

    private fun initUI() {
        showLogInfo(Constants.mHmsGmsVideoHelper, "Player initUI")
        binding.layoutVideoIncluder.surfaceView.holder.addCallback(this)
        binding.layoutVideoIncluder.surfaceView.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        cntxt.findViewById<LinearLayout>(R.id.content_social).visibility = View.GONE

        player?.setReadyListener(this)
        player?.setErrorListener(this)
        player?.setEventListener(this)
        player?.setResolutionUpdatedListener(this)
        player?.setLoadingListener(this)
        player?.setPlayEndListener(this)
        player?.setSeekEndListener(this)

        binding.layoutVideoIncluder.f10s.setOnClickListener {
            player?.seek(player?.currentTime!!+10000)
        }

        binding.layoutVideoIncluder.r10s.setOnClickListener {
            player?.seek(player?.currentTime!!-10000)
        }

        player?.cycleMode = PlayerConstants.CycleMode.MODE_NORMAL

        binding.layoutVideoIncluder.extendControl.setOnClickListener {
            binding.layoutVideoIncluder.contentInfo.expandView()
            binding.layoutVideoIncluder.contentControl.expandView()
            binding.layoutVideoIncluder.seekBar.expandView()

            if (binding.layoutVideoIncluder.seekBar.visibility == View.GONE)
                binding.layoutVideoIncluder.extendControl.setImageResource(R.drawable.ic_arrow_drop_up)
            else
                binding.layoutVideoIncluder.extendControl.setImageResource(R.drawable.ic_arrow_drop_down)
        }

        binding.layoutVideoIncluder.playBtn.setOnClickListener {
            changePlayState()
        }
    }

    private fun configureContentV() {
        showLogInfo(
            Constants.mHmsGmsVideoHelper,
            "Player width: ${player?.videoWidth} - Height: ${player?.videoHeight}"
        )
        showLogInfo(Constants.mHmsGmsVideoHelper, "Player is playing: ${player?.isPlaying}")
        showLogInfo(Constants.mHmsGmsVideoHelper, "Player play mode: ${player?.playMode}")
        showLogInfo(
            Constants.mHmsGmsVideoHelper, "Player bitrate: ${
                player?.currentStreamInfo?.bitrate?.toString()
            }"
        )
    }

    private fun configureControlV() {
        binding.layoutVideoIncluder.seekBar.max = player?.duration!!
        binding.layoutVideoIncluder.seekBar.progress = player?.currentTime!!
        binding.layoutVideoIncluder.seekBar.secondaryProgress = player?.bufferTime!!
    }

    private fun changePlayState() {
        if (isPlaying) {
            player?.pause()
            isPlaying = false
            setPlayView()
        } else {
            player?.start()
            isPlaying = true
            setPauseView()
        }
    }

    fun restartPlayer(videoUrl: String) {
        player?.reset()
        player?.setMute(false)
        readyPlayer(videoUrl, videoUrl)
        player?.setView(binding.layoutVideoIncluder.surfaceView)

        player?.resume(PlayerConstants.ResumeType.KEEP)
    }

    fun playYoutube(url: String) {}

    fun onConfig(config: Configuration) {}

    fun releasePlayer() {
        showLogInfo(Constants.mHmsGmsVideoHelper, "Player releasePlayer")
        player?.setErrorListener(null)
        player?.setEventListener(null)
        player?.setResolutionUpdatedListener(null)
        player?.setReadyListener(null)
        player?.setLoadingListener(null)
        player?.setPlayEndListener(null)
        player?.setSeekEndListener(null)
        player?.release()
    }

    private fun setPlayView() {
        binding.layoutVideoIncluder.playBtn.setImageResource(R.drawable.ic_play)
        binding.layoutVideoIncluder.lottieView.visibility=View.VISIBLE
    }

    private fun setPauseView() {
        binding.layoutVideoIncluder.playBtn.setImageResource(R.drawable.ic_pause)
        binding.layoutVideoIncluder.lottieView.visibility=View.GONE
    }

    fun readyPlayer(videoUrl: String, string: String) {
        player?.setPlayUrl(videoUrl)
        player?.ready()
        binding.layoutVideoIncluder.videoName.text = string
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        player?.setView(binding.layoutVideoIncluder.surfaceView)
        player?.resume(PlayerConstants.ResumeType.KEEP)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        player?.setSurfaceChange()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        player?.suspend()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        player?.resume(PlayerConstants.ResumeType.KEEP)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

    override fun onError(p0: WisePlayer?, p1: Int, p2: Int): Boolean {
        showLogError(Constants.mHmsGmsVideoHelper, "$p1 $p2")
        return false
    }

    override fun onReady(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoHelper, "On Ready")
        player?.start()
        isPlaying = true
        cntxt.runOnUiThread {
            configureControlV()
            mHandler.postDelayed(runnable, Constants.DELAY_SECOND)
        }
    }

    override fun onEvent(p0: WisePlayer?, p1: Int, p2: Int, p3: Any?): Boolean {
        showLogInfo(Constants.mHmsGmsVideoHelper, "onEvent Listener")
        return false
    }

    override fun onPlayEnd(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoHelper, "onPlayEnd")
        isPlaying = false
        setPlayView()
    }

    override fun onResolutionUpdated(p0: WisePlayer?, p1: Int, p2: Int) {
        showLogInfo(Constants.mHmsGmsVideoHelper, "onResolutionUpdate Listener")
    }

    override fun onSeekEnd(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoHelper, "onSeekEnd Listener")
    }

    override fun onLoadingUpdate(p0: WisePlayer?, p1: Int) {
        showLogInfo(Constants.mHmsGmsVideoHelper, "onLoading Update")
    }

    override fun onStartPlaying(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoHelper, "onStartPlaying")
        binding.layoutVideoIncluder.lottieView.visibility=View.GONE
    }

    // TODO Set SeekBar
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let { player?.seek(it) }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let { player?.seek(it) }
    }


}