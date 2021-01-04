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

package com.onurcan.exovideoreference.ui.presenters

import android.content.Intent
import com.onurcan.exovideoreference.appuser.AppUser
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.toSafeString
import com.onurcan.exovideoreference.ui.contracts.*
import com.onurcan.exovideoreference.helper.FirebaseDbHelper

class LoginPresenter constructor(
    private val loginContract: LoginContract,
    private val iLoginHelper: LoginHelper,
): LoginHelperCallback {

    fun onViewsCreate(){
        iLoginHelper.setCallback(this)
        iLoginHelper.checkSilentSignIn()
    }

    fun onEmailClick(){
        iLoginHelper.setCallback(this)
    }

    fun onSignInGHClick(){
        iLoginHelper.onLoginClick(LoginType.GOOGLE_HUAWEI)
    }

    override fun onLoginSuccess(
        loginUserData: LoginUserData,
        loginUserInfoData: LoginUserInfoData
    ) {
        iLoginHelper.setCallback(this)
        AppUser.setUserId(loginUserData.userId)
        updateUserDB(loginUserData)
        updateUserInfoDB(loginUserInfoData)
    }

    override fun onLoginFail(errorMessage: String) {
        showLogError(Constants.mMainPresenter,errorMessage)
    }

    override fun updateUserDB(loginUserData: LoginUserData) {
        val userDbRef = FirebaseDbHelper.getUser(loginUserData.userId)
        val userMap = HashMap<String, String>()
        userMap["tokenID"] = loginUserData.userId
        userMap["signInMethod"] = loginUserData.loginType.displayName

        userDbRef.setValue(userMap).addOnCompleteListener {
            if (it.isSuccessful)
                loginContract.redirectToMain()
            else
                showLogError(Constants.mMainPresenter,"on update fail")
        }
    }


    override fun updateUserInfoDB(loginUserInfoData: LoginUserInfoData) {
        val userInfoDbRef = FirebaseDbHelper.getUserInfo(loginUserInfoData.userId)
        val userInfoMap = HashMap<String, String>()
        userInfoMap["email"] = loginUserInfoData.email.toSafeString()
        userInfoMap["nameSurname"] = loginUserInfoData.nameSurname.toSafeString()
        userInfoMap["photoUrl"] = loginUserInfoData.photoUrl.toSafeString()
        userInfoMap["userID"] = loginUserInfoData.userId.toSafeString()

        userInfoDbRef.setValue(userInfoMap).addOnCompleteListener {
            if (it.isSuccessful)
                showLogInfo(Constants.mMainPresenter, "on update success")
            else
                showLogError(Constants.mMainPresenter, "on update fail")
        }
    }

    override fun onSilentSignInFail() {

    }

    override fun redirectToEmail() {

    }

    override fun redirectToSignIn(signInIntent: Intent, requestCode: Int) {
        loginContract.redirectToSignIn(signInIntent,requestCode)
    }

    override fun onSilentSignInSuccess(loginUserData: LoginUserData) {
        AppUser.setUserId(loginUserData.userId)
        loginContract.redirectToMain()
    }

}

interface LoginContract{
    fun redirectToMain()
    fun restartApp()
    fun redirectToSignIn(signInIntent: Intent, requestCode: Int)
}