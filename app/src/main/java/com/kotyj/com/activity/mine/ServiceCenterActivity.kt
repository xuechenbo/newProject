package com.kotyj.com.activity.mine

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.ServiceModel
import com.kotyj.com.utils.CommonUtils
import com.kotyj.com.utils.PermissionUtil
import com.kotyj.com.utils.StorageCustomerInfo02Util
import com.kotyj.com.utils.ViewUtils
import kotlinx.android.synthetic.main.act_service_center.*
import kotlinx.android.synthetic.main.layout_title.*


class ServiceCenterActivity : BaseActivity() {
    private var serviceModel: ServiceModel? = null

    override fun initLayout(): Int {
        return R.layout.act_service_center
    }

    override fun initData() {
        tv_title.text = "我的客服"
        val serviceKF = StorageCustomerInfo02Util.getInfo("serviceKF", context)
        serviceModel = Gson().fromJson(serviceKF, ServiceModel::class.java)
        Log.e("sss", serviceKF)
        tv_service_phone.text = "联系电话：" + serviceModel!!.phone1
        tv_service_phone1.text = "联系电话：" + serviceModel!!.phone2
        tv_wechat.text = "官方微信号：" + serviceModel!!.wx1
        tv_wechat1.text = "官方微信号：" + serviceModel!!.wx2
        tv_qq.text = "官方QQ号：" + serviceModel!!.qq1
        tv_qq1.text = "官方QQ号：" + serviceModel!!.qq2
        initListener()
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        tv_service_phone.setOnClickListener {
            if (!PermissionUtil.CALL_PHONE(context)) {
                return@setOnClickListener
            }
            showCallServerDialog(serviceModel!!.phone1)
        }
        tv_service_phone1.setOnClickListener {
            if (!PermissionUtil.CALL_PHONE(context)) {
                return@setOnClickListener
            }
            showCallServerDialog(serviceModel!!.phone2)
        }
        btn_wechat_copy.setOnClickListener {
            coptString(serviceModel!!.wx1)
        }
        btn_wechat_copy1.setOnClickListener {
            coptString(serviceModel!!.wx2)
        }
        btn_qq_copy.setOnClickListener {
            coptString(serviceModel!!.qq1)
        }
        btn_qq_copy1.setOnClickListener {
            coptString(serviceModel!!.qq2)
        }
    }


    private fun coptString(content: String) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", content)
        cm.primaryClip = mClipData
        ViewUtils.makeToast(context, "复制成功", 1000)
    }

    private fun showCallServerDialog(phone: String) {
        val confirmBt: Button
        val cancleBt: Button
        val mydialog = Dialog(context, R.style.MyProgressDialog)
        mydialog.setContentView(R.layout.callserver_dialog)
        mydialog.setCanceledOnTouchOutside(false)
        val phonenum = mydialog.findViewById<View>(R.id.phoneNum) as TextView

        phonenum.text = phone
        confirmBt = mydialog.findViewById<View>(R.id.bt_cancelPlan) as Button
        cancleBt = mydialog.findViewById<View>(R.id.bt_suspendCancel) as Button
        confirmBt.setOnClickListener(View.OnClickListener {
            if (CommonUtils.isFastDoubleClick2()) {
                return@OnClickListener
            }
            mydialog.dismiss()
            val serviceNumber = phone.replace("-", "")
            val phoneIntent = Intent(
                    "android.intent.action.CALL", Uri.parse("tel:$serviceNumber"))
            startActivity(phoneIntent)
        })
        cancleBt.setOnClickListener { mydialog.dismiss() }

        mydialog.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this)
    }
}
