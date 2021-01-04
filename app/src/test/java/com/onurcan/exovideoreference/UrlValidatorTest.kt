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

import com.onurcan.exovideoreference.helper.UrlValidatorHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun urlValidator_CorrectUrl_RunsTrue(){
        assertTrue(UrlValidatorHelper.isValidUrl("https://firebasestorage.googleapis.com/v0/b/exovideo-581e7.appspot.com/o/uploads%2FhPFhbAu3J5gWj5YfwTV4SrWY8pg2%2FVideos%2F1607932346690.mp4?alt=media&token=2fccf5fd-d8a5-4e40-88b8-0bc116c8be86"))
    }

    @Test
    fun urlValidator_InvalidCertificate_RunsFalse(){
        assertFalse(UrlValidatorHelper.isValidUrl("www.youtube.com/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun urlValidator_InvalidIdentifier_RunsFalse(){
        assertFalse(UrlValidatorHelper.isValidUrl("https://youtube.com/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun urlValidator_InvalidDomain_RunsFalse(){
        assertFalse(UrlValidatorHelper.isValidUrl("https://www.com/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun urlValidator_InvalidExtension_RunsFalse(){
        assertFalse(UrlValidatorHelper.isValidUrl("https://www.youtube/watch?v=Yr8xDSPjII8&list=RDMMYr8xDSPjII8&start_radio=1"))
    }

    @Test
    fun urlValidator_EmptyUrl_RunsFalse(){
        assertFalse(UrlValidatorHelper.isValidUrl(""))
    }

    @Test
    fun urlValidator_NullUrl_RunsFalse(){
        assertFalse(UrlValidatorHelper.isValidUrl(null))
    }
}