package com.kotyj.com.activity.mine

import android.os.Bundle
import butterknife.ButterKnife
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.act_my_client.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity

class MyClientActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
    }

    override fun initLayout(): Int {
        return R.layout.act_my_client
    }

    override fun initData() {
        tv_title.text = "我的社群"
        initListener()
        loadData()
    }


    fun initListener() {
        iv_back.setOnClickListener { finish() }

        ll1_zt.setOnClickListener {
            goClientDetail("10A", "10A")
        }
        ll1_team.setOnClickListener {
            goClientDetail("10A", "10B")
        }
        ll1_real.setOnClickListener {
            goClientDetail("10A", "10C")
        }
        ll1_vip.setOnClickListener {
            goClientDetail("10A", "10D")
        }

        ll2_zt.setOnClickListener {
            goClientDetail("10B", "10A")
        }
        ll2_team.setOnClickListener {
            goClientDetail("10B", "10B")
        }
        ll2_real.setOnClickListener {
            goClientDetail("10B", "10C")
        }
        ll2_vip.setOnClickListener {
            goClientDetail("10B", "10D")
        }


        ll3_zt.setOnClickListener {
            goClientDetail("10C", "10A")
        }
        ll3_team.setOnClickListener {
            goClientDetail("10C", "10B")
        }
        ll3_real.setOnClickListener {
            goClientDetail("10C", "10D")
        }
        ll3_vip.setOnClickListener {
            goClientDetail("10C", "10D")
        }

    }

    private fun goClientDetail(typeTop: String, type: String) {

        startActivity<MyClientDetailActivity>("typeTop" to typeTop, "type" to type)

    }


    private fun loadData() {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("42", merId)
        httpParams.put("3", "990006")
        OkClient.getInstance().post(httpParams, object : OkClient.EntityCallBack<BaseEntity>(context, BaseEntity::class.java) {
            override fun onError(response: Response<BaseEntity>) {
                super.onError(response)
                loadingDialog.dismiss()
            }

            override fun onSuccess(response: Response<BaseEntity>) {
                super.onSuccess(response)
                loadingDialog.dismiss()
                val model = response.body() ?: return
                if ("00" == model.str39) {
                    tv1_ztNum.text = model.str21
                    tv1_teamNum.text = model.str22
                    tv1_realNum.text = model.str23
                    tv1_vipNum.text = model.str24

                    tv2_ztNum.text = model.str25
                    tv2_teamNum.text = model.str26
                    tv2_realNum.text = model.str27
                    tv2_vipNum.text = model.str28

                    tv3_ztNum.text = model.str29
                    tv3_teamNum.text = model.str30
                    tv3_realNum.text = model.str31
                    tv3_vipNum.text = model.str32

                }
            }
        })
    }
}
