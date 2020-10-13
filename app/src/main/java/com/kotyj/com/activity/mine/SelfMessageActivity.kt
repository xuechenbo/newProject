package com.kotyj.com.activity.mine

import android.view.View
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.utils.GlideUtils
import com.kotyj.com.utils.StorageCustomerInfo02Util
import com.kotyj.com.utils.Utils
import com.kotyj.com.utils.ViewUtils
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.act_self_message.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity

class SelfMessageActivity : BaseActivity() {
    override fun initLayout(): Int = R.layout.act_self_message

    override fun initData() {
        tv_title.text = "个人资料"
        tv_right.text = "保存"
        val name = StorageCustomerInfo02Util.getInfo("merchantCnName", context)
        val level = StorageCustomerInfo02Util.getInfo("level", context)
        val phone = StorageCustomerInfo02Util.getInfo("phone", context)
        val nickname = StorageCustomerInfo02Util.getInfo("nickname", context)

        et_nic.setText(nickname)
        tv_name.text = name
        tv_phone.text = phone
        tv_leveName.text = Utils.GetLeveName(level)
        iv_back.setOnClickListener { finish() }
        iv_head.setOnClickListener {
            startActivity<MyHeaduCropActivity>()
        }


        et_nic.setOnFocusChangeListener { _, b ->
            tv_right.visibility = if (b) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        tv_right.run {
            setOnClickListener {
                et_nic?.run { loadData(et_nic.text.toString()) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val headImage = StorageCustomerInfo02Util.getInfo("headImage", context)
        GlideUtils.loadAvatar(context, headImage, iv_head)
    }

    private fun loadData(name: String) {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", "990011")
        httpParams.put("43", name)
        httpParams.put("42", merId)
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
                    StorageCustomerInfo02Util.putInfo(context, "nickname", name)
                    ViewUtils.makeToast(context, "修改成功", 1200)
                }
            }
        })
    }
}