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

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.databinding.ActivityMainBinding
import com.onurcan.exovideoreference.helper.*
import com.onurcan.exovideoreference.model.DataClass
import com.onurcan.exovideoreference.ui.adapters.MainVideoItemAdapter
import com.onurcan.exovideoreference.ui.contracts.IMain
import com.onurcan.exovideoreference.ui.presenters.MainPresenter
import com.onurcan.exovideoreference.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import nl.joery.animatedbottombar.AnimatedBottomBar
import video.HmsGmsVideoViewHolder

class MainActivity : AppCompatActivity(), IMain.ViewMain {

    private lateinit var binding: ActivityMainBinding

    lateinit var dialog: Dialog
    lateinit var dUrl: TextInputEditText

    lateinit var dFrontLyt: LinearLayout
    lateinit var dBackLyt: LinearLayout

    private val presenter: MainPresenter by lazy { MainPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                setTheme(R.style.Theme_ExoVideoReference_TransStatusBar)
                showLogInfo("Lifecycle Orientation", "Land")
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                setTheme(R.style.Theme_ExoVideoReference_TransStatusBar)
                showLogInfo("Lifecycle Orientation", "Port")
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        requestPermissions()
        binding.animatedBottomBar.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    /* Home */
                    0 -> {
                        presenter.restart(this@MainActivity)
                    }
                    /* Record */
                    1 -> {
                        setupDialog(this@MainActivity, R.style.BlurTheme)
                    }
                    /* Profile */
                    2 -> {
                        presenter.gotoProfile(this@MainActivity)
                    }
                }
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                super.onTabReselected(index, tab)
                when (index) {
                    0 -> {
                        presenter.restart(this@MainActivity)
                    }
                    1 -> {
                        setupDialog(this@MainActivity, R.style.BlurTheme)
                    }
                    2 -> {
                        presenter.gotoProfile(this@MainActivity)
                    }
                }
            }
        })
        binding.videoRecycler.layoutManager = LinearLayoutManager(this)
        binding.videoRecycler.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        setupRecycler()
        checkItems()
    }

    override fun setupRecycler() {
        showLogDebug(Constants.mMainActivity, "fSharedRef:  ${Constants.fSharedRef}")
        val options = FirebaseRecyclerOptions.Builder<DataClass.UploadsShareableDataClass>()
            .setQuery(Constants.fSharedRef, DataClass.UploadsShareableDataClass::class.java).build()
        val adapterFire = object :
            FirebaseRecyclerAdapter<DataClass.UploadsShareableDataClass, HmsGmsVideoViewHolder>(
                options
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): HmsGmsVideoViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_video, parent, false)
                return HmsGmsVideoViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: HmsGmsVideoViewHolder,
                position: Int,
                model: DataClass.UploadsShareableDataClass
            ) {
                val lisResUid = getRef(position).key
                var lovely = model.like.toInt()

                holder.sLovely.setOnClickListener {
                    lovely++
                    FirebaseDbHelper.getShareItem(lisResUid!!).child("like")
                        .setValue(lovely.toString())
                }

                holder.sLovely.text =
                    getString(R.string.lovely_counter, NumberConvertor.prettyCount(lovely))

                FirebaseDbHelper.getPostMessageRef(lisResUid!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            holder.bindComments(NumberConvertor.prettyCount(snapshot.childrenCount))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            showLogError(Constants.mMainActivity, error.toString())
                        }
                    })

                FirebaseDbHelper.getShareItem(lisResUid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val senderID = snapshot.child("uploaderId").value.toString()
                            FirebaseDbHelper.getUserInfo(senderID)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pName = snapshot.child("nameSurname").value.toString()
                                        holder.bindDialogInfo(
                                            model.videoUrl,
                                            pName,
                                            model.uploaderID,
                                            model.like
                                        )
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        showLogError(Constants.mMainActivity, error.toString())
                                    }
                                })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            showLogError(Constants.mMainActivity, error.toString())
                        }
                    })

                holder.sMessage.setOnClickListener {
                    startActivity(
                        Intent(this@MainActivity, PostMessageActivity::class.java)
                            .putExtra("listID", lisResUid)
                    )
                }
                holder.sShare.setOnClickListener { holder.shareUri(model.videoUrl) }
                holder.readyPlayer(model.videoUrl, model.videoName)
                holder.bindVisibility()
            }
        }
        adapterFire.startListening()
        val snapHelper = LinearSnapHelper()
        binding.videoRecycler.onFlingListener = null
        binding.videoRecycler.clearOnScrollListeners()
        snapHelper.attachToRecyclerView(binding.videoRecycler)
        binding.videoRecycler.adapter = adapterFire
    }

    override fun setupDialog(context: Context, type: Int) {
        dialog = Dialog(context, R.style.BlurTheme)
        dialog.window!!.attributes.windowAnimations = type
        dialog.setContentView(R.layout.dialog_record)
        dialog.setCanceledOnTouchOutside(true)

        val dRecord = dialog.findViewById<LinearLayout>(R.id.dialog_video)

        val dUrlLyt = dialog.findViewById<TextInputLayout>(R.id.dialog_input_layout)
        dUrl = dialog.findViewById(R.id.dialog_url)
        val dUrlBtn = dialog.findViewById<ImageButton>(R.id.dialog_send_url)

        val dYtLyt = dialog.findViewById<TextInputLayout>(R.id.dialog_yt_input_layout)
        val dYtUrl = dialog.findViewById<TextInputEditText>(R.id.dialog_yt_url)
        val dYtBtn = dialog.findViewById<ImageButton>(R.id.dialog_yt_send_url)

        dFrontLyt = dialog.findViewById(R.id.front_record)
        dBackLyt = dialog.findViewById(R.id.back_record)

        val circleFlip = dialog.findViewById<CircleImageView>(R.id.close_circle)
        val dFlip = dialog.findViewById<ImageButton>(R.id.dialog_flip)

        val dRecycler = dialog.findViewById<RecyclerView>(R.id.dialog_recycler)

        dRecycler.layoutManager = LinearLayoutManager(this)
        dRecycler.adapter = MainVideoItemAdapter(this, this)
        dRecycler.setHasFixedSize(true)

        val scale = this.applicationContext.resources.displayMetrics.density
        dFrontLyt.cameraDistance = 8000 * scale
        dBackLyt.cameraDistance = 8000 * scale

        val frontAnim =
            AnimatorInflater.loadAnimator(
                this,
                R.animator.front_animator
            ) as AnimatorSet
        val backAnim = AnimatorInflater.loadAnimator(
            this,
            R.animator.back_animator
        ) as AnimatorSet

        dFlip.setOnClickListener {
            FlipCard.flipFrontAnimator(frontAnim, dFrontLyt, backAnim, dBackLyt)
            circleFlip.setOnClickListener {
                FlipCard.flipBackAnimator(frontAnim, dFrontLyt, backAnim, dBackLyt)
            }
        }

        dUrlBtn.setOnClickListener {
            showLogInfo(Constants.mMainActivity, "Passing: " + dUrl.text.toString())
            val url = dUrl.text.toString()
            if (UrlValidatorHelper.isValidUrl(url))
                startActivity(
                    Intent(this, SinglePlayerActivity::class.java)
                        .putExtra("url", url)
                        .putExtra("type", 0)
                )
            else
                dUrlLyt.error = getString(R.string.error_url)
        }



        dYtBtn.setOnClickListener {
            showToast(
                this,
                dYtUrl.text.toString()
            )
            val url = dUrl.text.toString()
            if (YoutubeUrlValidatorHelper.isValidYoutube(url))
                startActivity(
                    Intent(this, SinglePlayerActivity::class.java)
                        .putExtra("ytUrl", url)
                        .putExtra("type", 1)
                )
            else
                dYtLyt.error = getString(R.string.error_url_yt)
        }

        dRecord.setOnClickListener {
            dialog.dismiss()
            presenter.gotoRecord(this)
        }

        dialog.show()
    }

    override fun checkItems(){
        Constants.fSharedRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()){
                    binding.lottieInc.root.visibility = View.GONE
                    binding.videoRecycler.visibility = View.VISIBLE
                } else{
                    binding.lottieInc.root.visibility = View.VISIBLE
                    binding.videoRecycler.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                showLogError(Constants.mMainActivity,error.toString())
            }
        })
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            2020
        )
    }
}