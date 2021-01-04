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

package com.onurcan.exovideoreference.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.onurcan.exovideoreference.databinding.ActivityLoginBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.helper.EmailValidatorHelper
import com.onurcan.exovideoreference.utils.expandView
import com.onurcan.exovideoreference.utils.showLogDebug
import com.onurcan.exovideoreference.utils.showToast
import com.onurcan.exovideoreference.ui.contracts.LoginHelper
import com.onurcan.exovideoreference.ui.presenters.LoginContract
import com.onurcan.exovideoreference.ui.presenters.LoginPresenter
import login.HmsGmsLoginHelper

class LoginActivity : AppCompatActivity(), LoginContract {

    private lateinit var binding: ActivityLoginBinding

    private val hgLoginHelper: LoginHelper by lazy {
        HmsGmsLoginHelper(this)
    }

    private val helperBuild: HmsGmsLoginHelper by lazy {
        HmsGmsLoginHelper(this)
    }

    private var emailValidator = EmailValidatorHelper()

    private val presenter: LoginPresenter by lazy { LoginPresenter(this, hgLoginHelper) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        presenter.onViewsCreate()

        binding.layoutGh.emailTxt.addTextChangedListener(emailValidator)

        /* Send Verification */
        binding.layoutGh.verificationSendBtn.setOnClickListener {
            /* Test Email */
            if (emailValidator.isValid) {
                binding.layoutGh.verificationSendBtn.expandView()
                binding.layoutGh.verificationLoginBtn.expandView()
                helperBuild.sendVerificationCode(
                    binding.layoutGh.emailTxt.text?.trim().toString(),
                    binding.layoutGh.verificationSendBtn.text.toString(),
                    30
                )
                object : CountDownTimer((30 * 1000).toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.layoutGh.verificationTimer.text =
                            (millisUntilFinished / 1000).toString()
                        showLogDebug(Constants.mLoginActivity, "Verify Timer: $millisUntilFinished")
                    }

                    override fun onFinish() {
                        binding.layoutGh.verificationSendBtn.visibility = View.VISIBLE
                        binding.layoutGh.verificationLoginBtn.visibility = View.GONE
                        showToast(this@LoginActivity, "Verify Time Expired...")
                    }
                }.start()
            } else {
                binding.layoutGh.emailTxt.error = "Invalid Mail"
            }
        }

        /* Authorize */
        binding.layoutGh.verificationLoginBtn.setOnClickListener {
            presenter.onEmailClick()
            binding.layoutGh.verificationSendBtn.expandView()
            binding.layoutGh.verificationLoginBtn.expandView()
            showToast(this, binding.layoutGh.emailTxt.text?.trim().toString())
            helperBuild.onLoginEmail(
                binding.layoutGh.emailTxt.text?.trim().toString(),
                binding.layoutGh.passwordTxt.text?.trim().toString(),
                binding.layoutGh.verificationTxt.text?.trim().toString()
            )

        }

        /* HWID Login */
        binding.layoutGh.loginBtn.setOnClickListener {
            presenter.onSignInGHClick()
        }
    }

    override fun redirectToMain() {
        startActivity(Intent(this,MainActivity::class.java))
    }

    override fun restartApp() {
        recreate()
    }

    override fun redirectToSignIn(signInIntent: Intent, requestCode: Int) {
        startActivityForResult(signInIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        hgLoginHelper.onDataReceived(requestCode, resultCode, data)
    }
}