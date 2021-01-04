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

package login

import android.content.Context
import android.content.Intent
import com.huawei.agconnect.auth.*
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.onurcan.exovideoreference.helper.*
import com.onurcan.exovideoreference.ui.contracts.*
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.showToast
import com.onurcan.exovideoreference.utils.toSafeString
import java.util.*

class HmsGmsLoginHelper(context: Context) : LoginHelper {

    private val huaweiRequestSignIn = 1002
    private val emailRequestSignIn = 1003
    private val cntx = context

    private var mAuthParams: HuaweiIdAuthParams =
        HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setId()
            .setProfile()
            .setEmail()
            .createParams()

    override fun sendVerificationCode(email: String, buttonString: String, interval: Int) {
        var btn = buttonString
        val settings = VerifyCodeSettings.newBuilder()
            .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(interval)
            .locale(Locale("tr", "TR"))
            .build()
        val task = EmailAuthProvider.requestVerifyCode(email, settings)
        task.addOnSuccessListener {
            showToast(cntx, "Please wait verification code, and then type it and press Login")

        }.addOnFailureListener {
            showToast(cntx, it.message!!)
        }
    }

    override fun onLoginEmail(email: String, password: String, verifyCode: String) {
        val emailBuilder = EmailUser.Builder()
        emailBuilder.setEmail(email)
        emailBuilder.setVerifyCode(verifyCode)
        emailBuilder.setPassword(password)

        val emailUser = emailBuilder.build()
        AGConnectAuth.getInstance().createUser(emailUser).addOnSuccessListener {
            val user = it.user
            val loginUserData = convertEmailUserData(user)
            val loginInfoData = convertEmailUserInfoData(user)
            loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
            showToast(cntx, "login success")
        }.addOnFailureListener {
            showToast(cntx, "login failed, ${it.message}")
            if (it.message!!.contains(Constants.ALREADY_REGISTERED)) {
                val credential =
                    EmailAuthProvider.credentialWithVerifyCode(email, password, verifyCode)
                setupRegistry(credential)
            } else {
                showToast(cntx, "login failed, Terribly")
            }
        }
    }

    private fun setupRegistry(credential: AGConnectAuthCredential) {
        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
            val user = it.user
            val loginUserData = convertEmailUserData(user)
            val loginInfoData = convertEmailUserInfoData(user)
            loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
            showToast(cntx, "login success / credential")
        }.addOnFailureListener {
            showToast(cntx, "${Constants.ERROR_EMAIL_AUTH_FAILED}")
        }
    }

    private var mAuthManager: HuaweiIdAuthService =
        HuaweiIdAuthManager.getService(context, mAuthParams)

    private lateinit var loginHelperCallback: LoginHelperCallback

    override fun setCallback(loginHelperCallback: LoginHelperCallback) {
        this.loginHelperCallback = loginHelperCallback
    }

    private fun convertHuaweiAccountToLoginUserData(huaweiAccount: AuthHuaweiId): LoginUserData {
        return LoginUserData(
            userId = huaweiAccount.unionId.toSafeString(),
            loginType = LoginType.HUAWEI
        )
    }

    private fun convertEmailUserData(user: AGConnectUser): LoginUserData {
        return LoginUserData(
            userId = user.providerId.toSafeString(),
            loginType = LoginType.EMAIL
        )
    }

    private fun convertHuaweiAccountToLoginInfoData(huaweiAccount: AuthHuaweiId): LoginUserInfoData {
        return LoginUserInfoData(
            nameSurname = huaweiAccount.displayName.toSafeString(),
            email = huaweiAccount.email.toSafeString(),
            userId = huaweiAccount.unionId.toSafeString(),
            photoUrl = huaweiAccount.avatarUriString.toSafeString()
        )
    }

    private fun convertEmailUserInfoData(user: AGConnectUser): LoginUserInfoData {
        return LoginUserInfoData(
            nameSurname = user.displayName.toSafeString(),
            email = user.email.toSafeString(),
            userId = user.providerId.toSafeString(),
            photoUrl = user.photoUrl.toSafeString()
        )
    }

    override fun onLoginClick(loginType: LoginType) {
        loginHelperCallback.redirectToSignIn(mAuthManager.signInIntent, huaweiRequestSignIn)
    }

    override fun checkSilentSignIn() {
        val task: Task<AuthHuaweiId> = mAuthManager.silentSignIn()
        task.addOnSuccessListener {
            loginHelperCallback.onSilentSignInSuccess(
                convertHuaweiAccountToLoginUserData(it)
            )
            showLogInfo(Constants.mHmsLogin, "silentSignIn success")
        }.addOnFailureListener { e ->
            loginHelperCallback.onSilentSignInFail()
            showLogError(Constants.mHmsLogin, e.message!!)
        }
    }

    override fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?) {
        /* Huawei ID Login */
        if (requestCode == huaweiRequestSignIn) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                showLogInfo(
                    Constants.mHmsLogin, huaweiAccount.displayName + " signIn success."
                            + "\nAccessToken: " + huaweiAccount.accessToken
                )
                val loginUserData = LoginUserData(
                    userId = huaweiAccount?.unionId.toSafeString(),
                    loginType = LoginType.HUAWEI
                )
                val loginInfoData = LoginUserInfoData(
                    nameSurname = huaweiAccount?.displayName.toSafeString(),
                    email = huaweiAccount?.email.toSafeString(),
                    userId = huaweiAccount?.unionId.toSafeString(),
                    photoUrl = huaweiAccount?.photoUriString.toSafeString()
                )
                loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
            } else {
                showLogError(
                    Constants.mHmsLogin,
                    "signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                )
            }
        }
    }
}