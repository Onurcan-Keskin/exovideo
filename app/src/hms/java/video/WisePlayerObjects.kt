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

import android.content.Context
import com.huawei.hms.videokit.player.InitFactoryCallback
import com.huawei.hms.videokit.player.WisePlayer
import com.huawei.hms.videokit.player.WisePlayerFactory
import com.huawei.hms.videokit.player.WisePlayerFactoryOptions
import com.huawei.hms.videokit.player.common.PlayerConstants
import com.onurcan.exovideoreference.utils.showLogDebug
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.showLogWarning


object PlayController {
    var isIsSurfaceVew = true
        private set
    var videoType = 0
    var isIsMute = false
        private set
    var playMode = PlayerConstants.PlayMode.PLAY_MODE_NORMAL
    var bandwidthSwitchMode = 0
    var isInitBitrateEnable = false
    var initType = 0
    var initBitrate = 0
    var initWidth = 0
    var initHeight = 0
    var minBitrate = 0
    var maxBitrate = 0
    var isCycleMode = false
    var volume = 1.0f
        set(volume) {
            field = if (volume >= 1.0f) {
                1.0f
            } else if (volume <= 0.0f) {
                0.0f
            } else {
                volume
            }
        }

    fun setIsSurfaceVew(isSurfaceVew: Boolean) {
        isIsSurfaceVew = isSurfaceVew
    }

    fun setIsMute(isMute: Boolean) {
        isIsMute = isMute
    }
}

object WisePlayerInit {

    lateinit var wisePlayerFactory: WisePlayerFactory

    fun initialize(context: Context) {
        // TODO Initializing of Wise Player Factory
        val factoryOptions = WisePlayerFactoryOptions.Builder().setDeviceId("xxx").build()
        // In the multi-process scenario, the onCreate method in Application is called multiple times.
        // The app needs to call the WisePlayerFactory.initFactory() API in the onCreate method of the app process (named "app package name")
        // and WisePlayer process (named "app package name:player").
        WisePlayerFactory.initFactory(context, factoryOptions, object : InitFactoryCallback {
            override fun onSuccess(factory: WisePlayerFactory) {
                showLogInfo("WisePlayerInit","WisePlayerInit Success")
                wisePlayerFactory = factory
            }

            override fun onFailure(errorCode: Int, msg: String) {
                showLogError("WisePlayerInit", "onFailure: $errorCode - $msg")
            }
        })
    }

    fun createPlayer(): WisePlayer? {
        //TODO Initializing of Wise Player Instance
        return if (::wisePlayerFactory.isInitialized) {
            wisePlayerFactory.createWisePlayer()
        } else {
            null
        }
    }

}

