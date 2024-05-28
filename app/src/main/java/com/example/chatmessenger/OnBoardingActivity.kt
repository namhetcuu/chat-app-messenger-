package com.example.chatmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.chatmessenger.activities.SignInActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingActivity : AppCompatActivity() {

    private val onBoardingPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            when(position){
                0 -> {
                    skipBtn.text = "Skip"
                    skipBtn.visible()
                    nextBtn.visible()
                    previousBtn.gone()
                }
                paperList.size - 1 -> {
                    skipBtn.text = "Get started"
                    skipBtn.visible()
                    nextBtn.gone()
                    previousBtn.visible()
                }
                else -> {
                    skipBtn.text = "Skip"
                    skipBtn.visible()
                    nextBtn.visible()
                    previousBtn.visible()
                }
            }
        }
    }

    private val paperList = arrayListOf(
        Page("Step 1",R.drawable.banner_1,"là một nền tảng xã hội và giao tiếp miễn phí giúp dễ dàng kết nối giữa người mới đến và người địa phương thông qua việc chia sẻ thông tin trực tuyến","#FFEB38"),
        Page("Step 2",R.drawable.banner_2,"là một nền tảng xã hội và giao tiếp miễn phí giúp dễ dàng kết nối giữa người mới đến và người địa phương thông qua việc chia sẻ thông tin trực tuyến","#03A9F4"),
        Page("Finish",R.drawable.banner_3,"là một nền tảng xã hội và giao tiếp miễn phí giúp dễ dàng kết nối giữa người mới đến và người địa phương thông qua việc chia sẻ thông tin trực tuyến","#FF9800")
    )

    lateinit var onBoardingViewPager2: ViewPager2
    lateinit var skipBtn: Button
    lateinit var nextBtn: Button
    lateinit var previousBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        onBoardingViewPager2 = findViewById(R.id.onBoardingViewPager2)
        skipBtn = findViewById(R.id.skipBtn)
        nextBtn = findViewById(R.id.nextBtn)
        previousBtn = findViewById(R.id.previousBtn)

        onBoardingViewPager2.apply {
            adapter = OnBoardingAdapter(this@OnBoardingActivity,paperList)
            registerOnPageChangeCallback(onBoardingPageChangeCallback)
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout,onBoardingViewPager2){tab, position -> }.attach()

        nextBtn.setOnClickListener{
            if(onBoardingViewPager2.currentItem < onBoardingViewPager2.adapter!!.itemCount-1){
                onBoardingViewPager2.currentItem+=1
            }else{
                homeScreenIntent()
            }
        }
        skipBtn.setOnClickListener{
            homeScreenIntent()
        }
        previousBtn.setOnClickListener{
            if(onBoardingViewPager2.currentItem > 0){
                onBoardingViewPager2.currentItem -=1
            }
        }

    }

    override fun onDestroy() {
        onBoardingViewPager2.unregisterOnPageChangeCallback(onBoardingPageChangeCallback)
        super.onDestroy()
    }

    private fun homeScreenIntent() {
        val homeIntent = Intent(this,SignInActivity::class.java)
        startActivity(homeIntent)
        finish()
    }
}