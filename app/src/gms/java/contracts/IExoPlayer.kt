package contracts

interface IExoPlayer {
    fun readyPlayer(videoUrl: String?, name: String)
    fun releasePlayer()
}

interface OnInteract{
    fun shareUri(uri: String)
    fun initDialog()
    fun bindDialogInfo(vUrl:String, vSender:String, vSenderID:String, vLovely:String)
    fun bindInformativeDialog()
    fun readyPlayer(videoUrl: String, name: String)
    fun releasePlayer()
    fun initUI(type: Int)
}

interface ICallBacks {
    fun callbackObserver(obj: Any?)
    interface playerCallBack {
        fun onItemClickOnItem(albumId: Int?)
        fun onPlayingEnd()
    }
}