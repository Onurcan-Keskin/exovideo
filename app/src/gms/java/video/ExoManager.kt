package video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import contracts.ICallBacks
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class ExoManager private constructor(mContext: Context) {
    var playerView: PlayerView
    var dataSourceFactory: DefaultDataSourceFactory
    var uriString = ""
    var mPlayList: ArrayList<String>? = null
    var playlistIndex = 0
    var listner: ICallBacks.playerCallBack? = null
    private val mPlayer: SimpleExoPlayer
    fun setPlayerListener(mPlayerCallBack: ICallBacks.playerCallBack?) {
        listner = mPlayerCallBack
    }

    fun playStream(urlToPlay: String) {
        uriString = urlToPlay
        var mp4VideoUri: Uri = Uri.parse(uriString)
        val videoSource: MediaSource
        val filenameArray = urlToPlay.split("\\.".toRegex()).toTypedArray()
        if (uriString.toUpperCase().contains("M3U8")) {
            videoSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(mp4VideoUri))
        } else {
            mp4VideoUri = Uri.parse(urlToPlay)
            videoSource = ExtractorMediaSource(
                mp4VideoUri, dataSourceFactory, DefaultExtractorsFactory(),
                null, null
            )
        }


        // Prepare the player with the source.
        mPlayer.prepare(videoSource)
        mPlayer.playWhenReady = true
    }

    fun setPlayerVolume(vol: Float) {
        mPlayer.volume = vol
    }

    @JvmName("setUriString1")
    fun setUriString(uri: String) {
        uriString = uri
    }

    fun setPlaylist(uriArrayList: ArrayList<String>?, index: Int, callBack: ICallBacks.playerCallBack?) {
        mPlayList = uriArrayList
        playlistIndex = index
        listner = callBack
        playStream(mPlayList!![playlistIndex])
    }

    fun playerPlaySwitch() {
        if (uriString !== "") {
            mPlayer.playWhenReady = !mPlayer.playWhenReady
        }
    }

    fun stopPlayer(state: Boolean) {
        mPlayer.playWhenReady = !state
    }

    fun destroyPlayer() {
        mPlayer.stop()
    }

    val isPlayerPlaying: Boolean
        get() = mPlayer.playWhenReady

    fun readURLs(url: String?): ArrayList<String>? {
        if (url == null) return null
        val allURls = ArrayList<String>()
        return try {
            val urls = URL(url)
            val `in` = BufferedReader(
                InputStreamReader(
                    urls
                        .openStream()
                )
            )
            var str: String
            while (`in`.readLine().also { str = it } != null) {
                allURls.add(str)
            }
            `in`.close()
            allURls
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        /**
         * declare some usable variable
         */
        val BANDWIDTH_METER = DefaultBandwidthMeter()
        private const val TAG = "ExoPlayerManager"
        private var mInstance: ExoManager? = null

        /**
         * Return ExoPlayerManager instance
         * @param mContext
         * @return
         */
        fun getSharedInstance(mContext: Context): ExoManager? {
            if (mInstance == null) {
                mInstance = ExoManager(mContext)
            }
            return mInstance
        }
    }

    /**
     * private constructor
     * @param mContext
     */
    init {
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector)
        playerView = PlayerView(mContext)
        playerView.useController = true
        playerView.requestFocus()
        playerView.player = mPlayer
        val mp4VideoUri: Uri = Uri.parse(uriString)
        dataSourceFactory = DefaultDataSourceFactory(
            mContext,
            Util.getUserAgent(mContext, "ExoVideo"),
            BANDWIDTH_METER
        )
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mp4VideoUri)
        mPlayer.prepare(videoSource)
        mPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
                showLogInfo(TAG, "onTimelineChanged: ")
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
                showLogInfo(TAG, "onTracksChanged: ")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                showLogInfo(TAG, "onLoadingChanged: ")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                showLogInfo(TAG, "onPlayerStateChanged: ")
                if (playbackState == 4 && mPlayList != null && playlistIndex + 1 < mPlayList!!.size) {
                    showLogError(TAG, "Song Changed...")
                    playlistIndex++
                    listner?.onItemClickOnItem(playlistIndex)
                    playStream(mPlayList!![playlistIndex])
                } else if (playbackState == 4 && mPlayList != null && playlistIndex + 1 == mPlayList!!.size) {
                    mPlayer.playWhenReady = false
                }
                if (playbackState == 4 && listner != null) {
                    listner!!.onPlayingEnd()
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                showLogInfo(TAG, "onRepeatModeChanged: ")
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                showLogInfo(TAG, "onShuffleModeEnabledChanged: ")
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                showLogInfo(TAG, "onPlayerError: ")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                showLogInfo(TAG, "onPositionDiscontinuity: ")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                showLogInfo(TAG, "onPlaybackParametersChanged: ")
            }

            override fun onSeekProcessed() {
                showLogInfo(TAG, "onSeekProcessed: ")
            }
        })
    }
}
