package com.kotyj.com.activity.mine

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import butterknife.ButterKnife
import butterknife.OnClick
import com.kotlin.com.wkyk.web.AgentWebActivity
import com.kotyj.com.R
import com.kotyj.com.activity.login.LoginNewActivity
import com.kotyj.com.activity.login.PasswordChangeActivity
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.utils.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity

class SettingActivity : BaseActivity() {
    var phone: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
    }

    override fun initLayout(): Int {
        return R.layout.activity_setting
    }

    override fun initData() {
        tv_title.text = "我的设置"
        tv_version.text = "V ${CommonUtils.getAppVersionName(this)}"
        tv_cache.text = DataCleanManager.getTotalCacheSize(context)
        phone = StorageCustomerInfo02Util.getInfo("phone", context)
        phoneNum.text = "手机号 ${CommonUtils.translateShortNumber(phone!!, 3, 4)}"
        LogUtils.i("normal" + StorageAppInfoUtil.getBooleanInfo("aliAuth", context))


        initListener()
    }

    override fun onResume() {
        super.onResume()
        val payPsd = StorageCustomerInfo02Util.getInfo("payPsd", context)
        if (payPsd.isEmpty() || payPsd == "null") {
            tv_pay_status!!.text = "去设置"
        } else {
            tv_pay_status!!.text = "去修改"
        }
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        rl_phone.setOnClickListener {
            startActivity<ReplacePhoneActivity>()
        }
        rl_pass_login.setOnClickListener {
            startActivity<PasswordChangeActivity>("isLogin" to true, "title" to "登录密码")
        }
        rl_pay_psw.setOnClickListener {
            startActivity<PasswordChangeActivity>("isLogin" to false, "title" to "支付密码")
        }
        rl_clear_cache.setOnClickListener {
            ViewUtils.showDialogStandard(context, "清除缓存？", "清除缓存后，你的手机可用储存空间将会增加", "", "确认 ", object : ViewUtils.OnshowDialogStandard {
                override fun clickOk() {
                    DataCleanManager.clearAllCache(context)
                    ViewUtils.makeToast(context, "缓存清除成功", 500)
                    tv_cache.text = "0KB"
                }

                override fun clickCancel() {

                }
            }).show()
        }
        tv_xy.setOnClickListener {
            startActivity<AgentWebActivity>("title" to "用户协议",
                    "url" to "http://xinjuejia.llyzf.cn/xinjuejia_register_protocol.html")
        }
        tv_ys.setOnClickListener {
            startActivity<AgentWebActivity>("title" to "隐私政策",
                    "url" to "http://xinjuejia.llyzf.cn/xinjuejia_privacy_policy.html")
        }
        btn_exit.setOnClickListener {
            showExitDialog(context)
        }
    }

    private fun showExitDialog(activity: Activity) {
        val confirmBt: Button
        val cancleBt: Button
        val mydialog = Dialog(activity, R.style.MyProgressDialog)
        mydialog.setContentView(R.layout.dialog_exit)
        mydialog.setCanceledOnTouchOutside(false)
        confirmBt = mydialog.findViewById<View>(R.id.bt_cancelPlan) as Button
        cancleBt = mydialog.findViewById<View>(R.id.bt_suspendCancel) as Button
        confirmBt.setOnClickListener(View.OnClickListener {
            if (CommonUtils.isFastDoubleClick()) {
                return@OnClickListener
            }
            //清除存储终端信息的缓存数据
            if (StorageCustomerInfo02Util.clearKey(context)) {
                goLogin()
            } else {
                ViewUtils.makeToast(context, "数据清除失败", 500)
            }
        })
        cancleBt.setOnClickListener { mydialog.dismiss() }
        mydialog.show()
    }

    private fun goLogin() {
        val intent = Intent(context, LoginNewActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}
