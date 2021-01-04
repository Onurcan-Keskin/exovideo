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

package com.onurcan.exovideoreference

import com.onurcan.exovideoreference.helper.YoutubeUrlValidatorHelper
import org.junit.Assert
import org.junit.Test

class YoutubeUrlValidatorTest {

    @Test
    fun youtubeUrlValidator_CorrectUrl_RunsTrue(){
        Assert.assertTrue(YoutubeUrlValidatorHelper.isValidYoutube("https://www.youtube.com/watch?v=xm3cl0zNgbc"))
    }

    @Test
    fun youtubeUrlValidator_InvalidCertificate_RunsFalse(){
        Assert.assertTrue(YoutubeUrlValidatorHelper.isValidYoutube("www.youtube.com/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun youtubeUrlValidator_Identifier_RunsTrue(){
        Assert.assertTrue(YoutubeUrlValidatorHelper.isValidYoutube("https://youtube.com/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun youtubeUrlValidator_Extension_RunsTrue(){
        Assert.assertTrue(YoutubeUrlValidatorHelper.isValidYoutube("https://www.youtube/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun youtubeUrlValidator_InvalidDomain_RunsFalse(){
        Assert.assertFalse(YoutubeUrlValidatorHelper.isValidYoutube("https://www.com/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun youtubeUrlValidator_EmptyUrl_RunsFalse(){
        Assert.assertFalse(YoutubeUrlValidatorHelper.isValidYoutube(""))
    }

    @Test
    fun youtubeUrlValidator_NullUrl_RunsFalse(){
        Assert.assertFalse(YoutubeUrlValidatorHelper.isValidYoutube(null))
    }
}