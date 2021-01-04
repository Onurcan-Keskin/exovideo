package video

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.utils.NumberConvertor
import contracts.OnInteract

class HmsGmsVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnInteract {

    val parent = itemView

    var playerView: PlayerView
    private val contentInfoLayout: LinearLayout
    private val videoName: TextView

    val sLovely: TextView
    val sMessage: TextView
    val sShare: TextView

    private var dialog = Dialog(parent.context, R.style.BlurTheme)

    private lateinit var vServiceProvider: TextView
    private lateinit var vVideoUrl: TextView
    private lateinit var vVideoSender: TextView
    private lateinit var vVideoSenderID: TextView
    private lateinit var vWidthHeight: TextView
    private lateinit var vPlayMode: TextView
    private lateinit var vBitrate: TextView
    private lateinit var vVideoLovely: TextView
    private lateinit var vVideoComments: TextView

    private val lottieAnimationView: LottieAnimationView

    private var infoPanelBtn: ImageButton

    private lateinit var player: SimpleExoPlayer
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    init {
        playerView = parent.findViewById(R.id.playerView)
        contentInfoLayout = parent.findViewById(R.id.content_info)
        videoName = parent.findViewById(R.id.video_name)

        lottieAnimationView = parent.findViewById(R.id.lottieView)

        sLovely = parent.findViewById(R.id.lovely)
        sMessage = parent.findViewById(R.id.comment)
        sShare = parent.findViewById(R.id.share)
        infoPanelBtn = parent.findViewById(R.id.info_panel_btn)

        lottieAnimationView.visibility = View.VISIBLE

        initDialog()
        initUI(R.style.DialogSlide)
    }

    override fun shareUri(uri: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/html"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Video - Entertainment")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, uri)
        parent.context.startActivity(Intent.createChooser(sharingIntent, "Share Video"))
    }

    override fun initDialog() {
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

    override fun bindDialogInfo(vUrl: String, vSender: String, vSenderID: String, vLovely: String) {
        vVideoUrl.text = vUrl
        vVideoSender.text = vSender
        vVideoSenderID.text = vSenderID
        vVideoLovely.text = NumberConvertor.prettyCount(vLovely.toLong())
    }

    override fun bindInformativeDialog() {

        val vExit = dialog.findViewById<TextView>(R.id.d_exit)

        vServiceProvider.text = parent.context.getString(R.string.gms_video)
        vWidthHeight.text = parent.context.getString(
            R.string.video_width_height_params,
            playerView.width.toString(), playerView.width.toString()
        )
        vPlayMode.text = playerView.player.toString()
        vBitrate.text = "${player.audioStreamType}"

        vExit.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun initUI(type: Int) {
        infoPanelBtn.setOnClickListener {
            bindInformativeDialog()
        }
    }

    fun bindComments(vComments:String){
        vVideoComments.text = NumberConvertor.prettyCount(vComments.toLong())
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(parent.context, "exop")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    override fun readyPlayer(videoUrl: String, name: String) {
        player = SimpleExoPlayer.Builder(parent.context).build()
        playerView.player = player
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player.setMediaItem(mediaItem)

        videoName.text = name

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

        player.playWhenReady = playWhenReady
        lottieAnimationView.visibility = View.GONE
        player.seekTo(currentWindow, playbackPosition)
        player.prepare()
    }

    override fun releasePlayer() {
        playWhenReady = player.playWhenReady
        playbackPosition = player.currentPosition
        currentWindow = player.currentWindowIndex
        player.release()
    }

    fun bindVisibility() {}
}