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

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.onurcan.exovideoreference.BuildConfig
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.appuser.AppUser
import com.onurcan.exovideoreference.databinding.ActivityProfileBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.helper.FirebaseDbHelper
import com.onurcan.exovideoreference.model.DataClass
import com.onurcan.exovideoreference.ui.contracts.IProfile
import com.onurcan.exovideoreference.ui.presenters.ProfilePresenter
import com.onurcan.exovideoreference.ui.viewHolders.ProfileGridViewHolder
import com.onurcan.exovideoreference.utils.LocaleHelper
import com.onurcan.exovideoreference.utils.NumberConvertor
import com.onurcan.exovideoreference.utils.SharedPrefsManager
import com.onurcan.exovideoreference.utils.showLogError
import com.r0adkll.slidr.Slidr
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity(), IProfile.ViewProfile {

    private lateinit var binding: ActivityProfileBinding

    private val profilePresenter: ProfilePresenter by lazy {
        ProfilePresenter()
    }

    private lateinit var mShared: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefsManager = SharedPrefsManager(this)
        if (sharedPrefsManager.loadLanguage() == "tr") {
            LocaleHelper.setLocale(this, "tr")
        } else {
            LocaleHelper.setLocale(this, "en")
        }
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Slidr.attach(this)
        onViewsCreate()
        populateViews()
        binding.profSettings.setOnClickListener {
            setPopup(sharedPrefsManager)
        }
    }

    override fun onViewsCreate() {
        binding.gridRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.gridRecycler.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        setupRecycler()
    }

    override fun populateViews() {
        Constants.fUserInfoDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val img = snapshot.child("photoUrl").value.toString()
                val name = snapshot.child("nameSurname").value.toString()
                Picasso.get().load(img).centerCrop().fit().into(binding.profileImage)
                supportActionBar?.title = getString(R.string.welcome, name)
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(Constants.mProfileActivity, error.message)
            }
        })

        Constants.fFeedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pTotal = NumberConvertor.prettyCount(snapshot.children.count())
                binding.profileTotalVideo.text = pTotal
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(Constants.mProfileActivity, error.toString())
            }
        })
    }

    override fun setPopup(sharedPrefsManager: SharedPrefsManager){
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val pw = inflater.inflate(R.layout.popup_settings,null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val pWind = PopupWindow(pw,width, height, focusable)

        val pTr = pw.findViewById<LinearLayout>(R.id.pop_tr)
        val pEn = pw.findViewById<LinearLayout>(R.id.pop_en)
        val pTitle = pw.findViewById<TextView>(R.id.pop_name)
        val pVer = pw.findViewById<TextView>(R.id.pop_version)
        val closePop = pw.findViewById<ImageButton>(R.id.pop_close)

        if(BuildConfig.FLAVOR == "gms")
            pTitle.text = getString(R.string.gms_video)
        else
            pTitle.text = getString(R.string.hms_video)

        pTr.setOnClickListener {
            sharedPrefsManager.setLanguage("tr")
            LocaleHelper.setLocale(this,"tr")
            recreate()
        }

        pEn.setOnClickListener {
            sharedPrefsManager.setLanguage("en")
            LocaleHelper.setLocale(this,"en")
            recreate()
        }

        try {
            val info = this.packageManager.getPackageInfo(this.packageName, 0)
            val version = info.versionName
            pVer.text= getString(R.string.app_version,version)
        }catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()}

        closePop.setOnClickListener { pWind.dismiss() }
        pWind.showAtLocation(findViewById(R.id.prof_settings),Gravity.TOP,120,0)
    }

    override fun setupRecycler() {
        val options = FirebaseRecyclerOptions.Builder<DataClass.ProfileVideoDataClass>()
            .setQuery(Constants.fFeedRef, DataClass.ProfileVideoDataClass::class.java).build()
        val adapterFire = object :
            FirebaseRecyclerAdapter<DataClass.ProfileVideoDataClass, ProfileGridViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ProfileGridViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.grid_video_item, parent, false)
                return ProfileGridViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ProfileGridViewHolder,
                position: Int,
                model: DataClass.ProfileVideoDataClass
            ) {
                val lisResUid = getRef(position).key
                val scale = this@ProfileActivity.applicationContext.resources.displayMetrics.density
                holder.vidFront.cameraDistance = 8000 * scale
                holder.vidBack.cameraDistance = 8000 * scale

                val frontAnim =
                    AnimatorInflater.loadAnimator(
                        this@ProfileActivity,
                        R.animator.front_animator
                    ) as AnimatorSet
                val backAnim = AnimatorInflater.loadAnimator(
                    this@ProfileActivity,
                    R.animator.back_animator
                ) as AnimatorSet

                val dbRef = FirebaseDbHelper.getVideoFeedItem(AppUser.getUserId(), lisResUid!!)
                dbRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mShared = snapshot.child("shareStat").value.toString()

                        when (mShared) {
                            "1" -> {
                                holder.share.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_hide,
                                    0,
                                    0,
                                    0
                                )
                                holder.share.text = getString(R.string.hide)

                                holder.share.setOnClickListener {
                                    profilePresenter.moveShowToHide(this@ProfileActivity, lisResUid)
                                }
                            }
                            "0" -> {
                                holder.share.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_show,
                                    0,
                                    0,
                                    0
                                )
                                holder.share.text = getString(R.string.share)

                                holder.share.setOnClickListener {
                                    profilePresenter.moveHideToShow(this@ProfileActivity, lisResUid)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showLogError(Constants.mProfileActivity, error.message)
                    }
                })

                holder.startSinglePlayer(model.videoUrl)

                holder.vidFront.setOnClickListener {
                    frontAnim.setTarget(holder.vidFront)
                    backAnim.setTarget(holder.vidBack)
                    frontAnim.start()
                    backAnim.start()
                    holder.vidFront.visibility = View.GONE
                    holder.vidBack.visibility = View.VISIBLE
                    holder.cancel.setOnClickListener {
                        frontAnim.setTarget(holder.vidBack)
                        backAnim.setTarget(holder.vidFront)
                        frontAnim.start()
                        backAnim.start()
                        holder.vidFront.visibility = View.VISIBLE
                        holder.vidBack.visibility = View.GONE
                    }
                }
                holder.bindImage(model.videoUrl)

                //holder.simpleVideo.setOnClickListener { holder.startStop() }
            }
        }
        adapterFire.startListening()
        binding.gridRecycler.adapter = adapterFire
    }
}