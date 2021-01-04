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

import com.onurcan.exovideoreference.helper.EmailValidatorHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the EmailValidator logic.
 */
class EmailValidatorTest {

    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(EmailValidatorHelper.isValidEmail("name@email.com"))
    }

    @Test fun emailValidator_CorrectEmailSubDomain_ReturnsTrue() {
        assertTrue(EmailValidatorHelper.isValidEmail("name@email.co.uk"))
    }

    @Test fun emailValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(EmailValidatorHelper.isValidEmail("name@email"))
    }

    @Test fun emailValidator_InvalidEmailDoubleDot_ReturnsFalse() {
        assertFalse(EmailValidatorHelper.isValidEmail("name@email..com"))
    }

    @Test fun emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(EmailValidatorHelper.isValidEmail("@email.com"))
    }

    @Test fun emailValidator_EmptyString_ReturnsFalse() {
        assertFalse(EmailValidatorHelper.isValidEmail(""))
    }

    @Test fun emailValidator_NullEmail_ReturnsFalse() {
        assertFalse(EmailValidatorHelper.isValidEmail(null))
    }

}