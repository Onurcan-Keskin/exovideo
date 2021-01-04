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

import android.app.Dialog
import android.content.Intent
import android.graphics.SurfaceTexture
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.huawei.hms.videokit.player.WisePlayer
import com.huawei.hms.videokit.player.common.PlayerConstants
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.ui.activities.MainActivity
import com.onurcan.exovideoreference.utils.NumberConvertor
import com.onurcan.exovideoreference.utils.expandView
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import contracts.OnInteract
import contracts.OnPlayWindowListener
import video.contracts.OnWisePlayerListener

class HmsGmsVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    OnPlayWindowListener, OnWisePlayerListener, OnInteract {

    val parent = itemView

    private val frameLayout: FrameLayout
    private val surfaceView: SurfaceView
    private val extender: ImageButton
    private val contentSocialLayout: LinearLayout
    private val contentInfoLayout: LinearLayout
    private val videoText: TextView
    private val contentControlLayout: LinearLayout
    private val r10s: ImageButton
    private val playBtn: ImageButton
    private val f10s: ImageButton
    private val seekBar: SeekBar
    private val lottieAnimationView: LottieAnimationView
    private val infoPanelBtn: ImageButton

     val sLovely:TextView
     val sMessage:TextView
     val sShare:TextView

    private var dialog = Dialog(parent.context, R.style.BlurTheme)

    private lateinit var vServiceProvider: TextView
    private lateinit var vVideoUrl : TextView
    private lateinit var vVideoSender : TextView
    private lateinit var vVideoSenderID : TextView
    private lateinit var vWidthHeight : TextView
    private lateinit var vPlayMode : TextView
    private lateinit var vBitrate : TextView
    private lateinit var vVideoLovely : TextView
    private lateinit var vVideoComments : TextView

    private var isPlaying = false
    private val mStopHandler = false

    private val player: WisePlayer? by lazy {
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
        //initialize()
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "Player init")
        frameLayout = parent.findViewById(R.id.frameLayout)
        surfaceView = parent.findViewById(R.id.surfaceView)

        extender = parent.findViewById(R.id.extend_control)
        contentSocialLayout = parent.findViewById(R.id.content_social)
        contentInfoLayout = parent.findViewById(R.id.content_info)
        videoText = parent.findViewById(R.id.video_name)
        contentControlLayout = parent.findViewById(R.id.content_control)
        r10s = parent.findViewById(R.id.r10s)
        playBtn = parent.findViewById(R.id.play_btn)
        f10s = parent.findViewById(R.id.f10s)
        seekBar = parent.findViewById(R.id.seekBar)
        lottieAnimationView = parent.findViewById(R.id.lottieView)
        infoPanelBtn = parent.findViewById(R.id.info_panel_btn)

        sLovely = parent.findViewById(R.id.lovely)
        sMessage = parent.findViewById(R.id.comment)
        sShare = parent.findViewById(R.id.share)

        lottieAnimationView.visibility = View.VISIBLE

        initDialog()
        initUI(R.style.DialogSlide)
    }

    fun shareUri(uri:String){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type="text/html"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Share Video - Entertainment")
        sharingIntent.putExtra(Intent.EXTRA_TEXT,uri)
        parent.context.startActivity(Intent.createChooser(sharingIntent,"Share Video"))
    }

    fun initDialog(){
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.attributes.windowAnimations = R.style.DialogSlide
        dialog.setContentView(R.layout.dialog_video_info)
        dialog.setCanceledOnTouchOutside(true)

        vServiceProvider = dialog.findViewById(R.id.d_video_service_provider)
        vVideoUrl = dialog.findViewById(R.id.d_video_url)
        vVideoSender = dialog.findViewById(R.id.d_video_sender)
        vVideoSenderID = dialog.findViewById(R.id.d_video_sender_id)
        vWidthHeight = dialog.findViewById(R.id.d_video_width_height)
        vPlayMode = dialog.findViewById(R.id.d_video_play_mode)
        vBitrate = dialog.findViewById(R.id.d_video_bitrate)
        vVideoLovely = dialog.findViewById(R.id.d_video_given_lovelies)
        vVideoComments = dialog.findViewById(R.id.d_video_total_comments)
    }

    fun bindInformativeDialog() {

        val vExit = dialog.findViewById<TextView>(R.id.d_exit)

        vServiceProvider.text = parent.context.getString(R.string.hms_video)
        vWidthHeight.text = parent.context.getString(R.string.video_width_height_params,player?.videoWidth.toString(),player?.videoHeight.toString())
        vPlayMode.text = player?.playMode.toString()
        vBitrate.text = "${player?.currentStreamInfo?.bitrate?.toString()}"

        vExit.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun bindDialogInfo(vUrl:String, vSender:String, vSenderID:String, vLovely:String){
        vVideoUrl.text=vUrl
        vVideoSender.text=vSender
        vVideoSenderID.text=vSenderID
        vVideoLovely.text = NumberConvertor.prettyCount(vLovely.toLong())
    }

    fun bindComments(vComments:String){
        vVideoComments.text = NumberConvertor.prettyCount(vComments.toLong())
    }

    override fun bindVisibility() {
        extender.setOnClickListener {
            contentInfoLayout.expandView()
            contentControlLayout.expandView()
            contentSocialLayout.expandView()
            seekBar.expandView()

            if (seekBar.visibility == View.GONE)
                extender.setImageResource(R.drawable.ic_arrow_drop_up)
            else
                extender.setImageResource(R.drawable.ic_arrow_drop_down)
        }
    }

    override fun initUI(type: Int) {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "Player initUI")
        player?.setReadyListener(this)
        player?.setErrorListener(this)
        player?.setEventListener(this)
        player?.setResolutionUpdatedListener(this)
        player?.setLoadingListener(this)
        player?.setPlayEndListener(this)
        player?.setSeekEndListener(this)

        f10s.setOnClickListener {
            player?.seek(player?.currentTime!!+10000)
        }

        r10s.setOnClickListener {
            player?.seek(player?.currentTime!!-10000)
        }

        surfaceView.holder.addCallback(this)

        player?.cycleMode = PlayerConstants.CycleMode.MODE_NORMAL

        playBtn.setOnClickListener {
            changePlayState()
        }

        infoPanelBtn.setOnClickListener {
            bindInformativeDialog()
        }
    }

    override fun configureContentV() {
        showLogInfo(
            Constants.mHmsGmsVideoViewHolder,
            "Player width: ${player?.videoWidth} - Height: ${player?.videoHeight}"
        )
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "Player is playing: ${player?.isPlaying}")
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "Player play mode: ${player?.playMode}")
        showLogInfo(
            Constants.mHmsGmsVideoViewHolder, "Player bitrate: ${
                player?.currentStreamInfo?.bitrate?.toString()
            }"
        )
    }

    override fun configureControlV() {
        seekBar.max = player?.duration!!
        seekBar.progress = player?.currentTime!!
        seekBar.secondaryProgress = player?.bufferTime!!
    }

    override fun restartPlayer(videoUrl: String) {
        player?.reset()
        player?.setMute(false)
        readyPlayer(videoUrl, videoUrl)
        player?.setView(surfaceView)

        player?.resume(PlayerConstants.ResumeType.KEEP)
    }

    override fun changePlayState() {
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

    override fun setPauseView() {
        playBtn.setImageResource(R.drawable.ic_pause)
        lottieAnimationView.visibility=View.GONE
    }

    override fun setPlayView() {
        playBtn.setImageResource(R.drawable.ic_play)
        lottieAnimationView.visibility=View.VISIBLE
    }

    override fun readyPlayer(videoUrl: String, string: String) {
        player?.setPlayUrl(videoUrl)
        player?.ready()
        videoText.text = string
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        player?.setView(surfaceView)
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
        showLogError(Constants.mHmsGmsVideoViewHolder, "$p1 $p2")
        return false
    }

    override fun onReady(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "On Ready")
        player?.start()
        isPlaying = true
        val activity = MainActivity()
        activity.runOnUiThread {
            configureControlV()
            mHandler.postDelayed(runnable, Constants.DELAY_SECOND)
        }
    }

    override fun onEvent(p0: WisePlayer?, p1: Int, p2: Int, p3: Any?): Boolean {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "onEvent Listener")
        return false
    }

    override fun onPlayEnd(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "onPlayEnd")
        isPlaying = false
        setPlayView()
    }

    override fun onResolutionUpdated(p0: WisePlayer?, p1: Int, p2: Int) {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "onResolutionUpdate Listener")
    }

    override fun onSeekEnd(p0: WisePlayer?) {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "onSeekEnd Listener")
    }

    override fun onLoadingUpdate(p0: WisePlayer?, p1: Int) {
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "onLoading Update")
    }

    override fun onStartPlaying(p0: WisePlayer?) {
        lottieAnimationView.visibility = View.GONE
        showLogInfo(Constants.mHmsGmsVideoViewHolder, "onStartPlaying")
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