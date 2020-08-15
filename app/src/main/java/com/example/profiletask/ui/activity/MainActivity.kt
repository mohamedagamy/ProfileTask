package com.example.profiletask.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.profiletask.Constants.Companion.IMAGE_LINK
import com.example.profiletask.ui.adapter.ImagesAdapter
import com.example.profiletask.R
import com.example.profiletask.api.RetrofitBuilder
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    fun initViews(){
        getUserInfo()

    }

    fun getUserMedia(){
        val apiService = RetrofitBuilder.getApiService()
        val subscribe = apiService.getMediaInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).doAfterTerminate {

            }.toObservable().subscribe({
                val imagesAdapter = ImagesAdapter(this, it.data)
                imagesRecyclerview.adapter = imagesAdapter
                imagesAdapter.itemClickListener = { link ->
                    openActivity(link)
                }

            },{

            })
    }

    fun Context.openActivity(link:String){

        val intent = Intent(this, ImagePreviewActivity::class.java)
        intent.putExtra(IMAGE_LINK,link)
        startActivity(intent)
    }

    fun getUserInfo(){
        val apiService = RetrofitBuilder.getApiService()
        val subscribe = apiService.getUserInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).doAfterTerminate {
                getUserMedia()
            }.toObservable().subscribe({
                tvFollowersCount.text = it.data.counts.followers.toString()
                tvFollowingCount.text = it.data.counts.following.toString()
                tvPostsCount.text = it.data.counts.posts.toString()

                Picasso.get().load(it.data.profile_picture).into(ivProfilePic)
                tvLocation.text = it.data.location
                tvBioDesc.text = it.data.bio
                ivProfilePic.setOnClickListener{view ->
                    openActivity(it.data.profile_picture)
                }
            },{

            })
    }
}