package com.kotyj.com.activity.login

import com.kotyj.com.R
import com.kotyj.com.activity.HomeNewActivity
import com.kotyj.com.base.BaseActivity
import org.jetbrains.anko.startActivity
import java.util.*

class TheBufferActivity : BaseActivity() {
    override fun initLayout(): Int = R.layout.act_thebuffer
    override fun initData() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity<HomeNewActivity>()
            }
        }, 2200)
    }
}