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

package com.onurcan.exovideoreference.ui.contracts

import android.content.Intent

interface LoginHelper {
    fun onLoginClick(loginType: LoginType)
    fun checkSilentSignIn()
    fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?)
    fun setCallback(loginHelperCallback: LoginHelperCallback)
    fun onLoginEmail(email: String, password: String, verifyCode: String)
    fun sendVerificationCode(email: String, buttonString: String, interval: Int)
}

interface LoginHelperCallback {
    fun onLoginSuccess(loginUserData: LoginUserData, loginUserInfoData: LoginUserInfoData)
    fun onLoginFail(errorMessage: String)
    fun updateUserDB(loginUserData: LoginUserData)
    fun updateUserInfoDB(loginUserInfoData: LoginUserInfoData)
    fun onSilentSignInFail()
    fun redirectToEmail()
    fun redirectToSignIn(signInIntent: Intent, requestCode: Int)
    fun onSilentSignInSuccess(loginUserData: LoginUserData)
}

enum class LoginType(val displayName: String) {
    GOOGLE_HUAWEI("Google or Huawei"),
    GOOGLE("Google"),
    HUAWEI("Huawei"),
    EMAIL("Email")
}

data class LoginUserData(
    val userId: String,
    val loginType: LoginType
)

data class LoginUserInfoData(
    val nameSurname: String,
    val email: String,
    val userId: String,
    val photoUrl: String
)