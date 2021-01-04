package video

import android.content.res.Configuration
import android.net.Uri
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.databinding.ActivitySinglePlayerBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.ui.activities.SinglePlayerActivity
import com.onurcan.exovideoreference.utils.showLogInfo
import contracts.IExoPlayer

class HmsGmsVideoHelper(context: SinglePlayerActivity):IExoPlayer {

    private val cntx = context
    private var binding: ActivitySinglePlayerBinding

    private var playerView: PlayerView

    private val videoName: TextView
    private val linSocial: LinearLayout

    private val lottieAnimationView: LottieAnimationView

    private lateinit var player: SimpleExoPlayer
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    var dataSourceFactory: DefaultDataSourceFactory

    init {
        binding = ActivitySinglePlayerBinding.inflate(cntx.layoutInflater)
        val view = binding.root
        cntx.setContentView(view)
        lottieAnimationView = cntx.findViewById(R.id.lottieView)
        lottieAnimationView.visibility = View.VISIBLE
        videoName = cntx.findViewById(R.id.video_name)
        linSocial = cntx.findViewById(R.id.content_social)
        playerView = binding.layoutVideoIncluder.playerView
        dataSourceFactory = DefaultDataSourceFactory(
            cntx,
            Util.getUserAgent(cntx, "ExoVideo"),
            ExoManager.BANDWIDTH_METER
        )
        linSocial.visibility=View.GONE
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(cntx, "exop")
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    override fun readyPlayer(videoUrl: String?, name: String) {
        player = SimpleExoPlayer.Builder(cntx).build()
        playerView.player = player
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        val mediaSource = buildMediaSource(Uri.parse(videoUrl))
        player.setMediaItem(mediaItem)

        videoName.text = name

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

        player.playWhenReady = playWhenReady
        lottieAnimationView.visibility = View.GONE
        player.seekTo(currentWindow, playbackPosition)
        player.prepare(mediaSource,false,false)

        showLogInfo(Constants.mHmsGmsVideoHelper,videoUrl!!)
    }

    fun playYoutube(downloadUrl:String){
        player = SimpleExoPlayer.Builder(cntx).build()
        playerView.player = player
        playStream(downloadUrl)
    }

    fun playStream(urlToPlay: String) {
        val uriString = urlToPlay
        var mp4VideoUri: Uri = Uri.parse(uriString)
        val videoSource: MediaSource
        val filenameArray = urlToPlay.split("\\.".toRegex()).toTypedArray()
        if (uriString.toUpperCase().contains("M3U8")) {
            videoSource =  HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(mp4VideoUri))
        } else {
            mp4VideoUri = Uri.parse(urlToPlay)
            videoSource = ExtractorMediaSource(
                mp4VideoUri, dataSourceFactory, DefaultExtractorsFactory(),
                null, null
            )
        }

        // Prepare the player with the source.
        player.prepare(videoSource)
        player.playWhenReady = true
    }

    fun onConfig(config:Configuration){}

    override fun releasePlayer() {
        playWhenReady = player.playWhenReady
        playbackPosition = player.currentPosition
        currentWindow = player.currentWindowIndex
        player.release()
    }
}