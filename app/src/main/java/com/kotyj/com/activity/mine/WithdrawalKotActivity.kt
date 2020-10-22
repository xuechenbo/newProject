package com.kotyj.com.activity.mine

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.kotyj.com.MyApplication
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.utils.EditTextUtil
import com.kotyj.com.utils.StorageCustomerInfo02Util
import com.kotyj.com.utils.Utils
import com.kotyj.com.utils.ViewUtils
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_withdrawal.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class WithdrawalKotActivity : BaseActivity() {
    private var loanAmount: String? = null
    private var type: Boolean = false


    override fun initLayout(): Int {
        return R.layout.activity_withdrawal
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        tv_title.text = intent.getStringExtra("title")
        tv_right.run {
            setCompoundDrawables(resources.getDrawable(R.drawable.look_m).also {
                it.setBounds(0, 0, it.minimumWidth, it.minimumHeight)
            }, null, null, null)
            visibility = View.VISIBLE
        }
        loanAmount = intent.getStringExtra("money")
        type = intent.getBooleanExtra("type", false)
        val bankAccounts = StorageCustomerInfo02Util.getInfo("bankAccount", context)
        val bankName = StorageCustomerInfo02Util.getInfo("bankDetail", context)
        //TODO true 转入余额
        when (type) {
            true -> {
                tv_right.text = "查看记录"
                bank_llt.visibility = View.GONE
                btn_submit.text = "确认转入"
                tv_money.text = "可转余额:$loanAmount"
                textView01.text = "转入金额"
            }
            else -> {
                tv_right.text = "提现记录"
                tv_money.text = "可用余额:$loanAmount"
            }
        }
        if (bankAccounts.isNotEmpty()) {
            val str = bankAccounts.substring(bankAccounts.length - 4, bankAccounts.length)
            tv_bank_account.text = "尾号$str"
        }
        bank_tv.text = bankName
        Utils.initBankCodeColorIcon(MyApplication.bankNameList[bankName], bank_iv, context)
        et_money.addTextChangedListener(textWatcher)

        initListener()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            EditTextUtil.keepTwoDecimals(et_money, 8)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        btn_submit.setOnClickListener {
            when {
                et_money.text.isEmpty() -> toast("金额为空")
                else -> submit()
            }
        }
        tv_right.setOnClickListener {
            if (type) startActivity<WithdrawYueListActivity>()
            else startActivity<WithdrawListActivity>()

        }
    }

    private fun submit() {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", if (type) "990001" else "190888")
        //转入
        if (type) {
            httpParams.put("43", et_money.text.toString())
            httpParams.put("42", merId)
        } else {
            httpParams.put("5", et_money.text.toString())
            httpParams.put("42", merNo)
        }
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
                    ViewUtils.makeToast(context, if (type) "转入成功" else "提现成功", 500)
                    finish()
                }
            }
        })
    }


}