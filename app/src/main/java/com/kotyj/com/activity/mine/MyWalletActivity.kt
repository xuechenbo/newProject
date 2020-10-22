package com.kotyj.com.activity.mine

import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.UserInfoModel
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.act_my_wallet.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity

class MyWalletActivity : BaseActivity() {

    var totalMoney: String? = null
    var withdrawalMoney: String? = null
    override fun initLayout(): Int = R.layout.act_my_wallet

    override fun initData() {
        tv_title.text = "我的钱包"
        initListener()
    }

    private fun goEarnList(title: String, type: String) {
        startActivity<EarningsListActivity>("title" to title, "type" to type)
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }

        rl_todayArea.setOnClickListener {
            //TODO 今日收益
            goEarnList("今日收益", "10A")
        }
        rl_totalArea.setOnClickListener {
            goEarnList("累计收益", "10B")
        }
        //转入余额
        tv_yue.setOnClickListener {
            //            startActivity<WithdrawalActivity>("type" to true, "title" to "转入余额", "money" to totalMoney)
            startActivity<WithdrawalKotActivity>("type" to true, "title" to "转入余额", "money" to totalMoney)

        }
        //TODO 收益提现
        tv_earn_detail.setOnClickListener {
            //            startActivity<WithdrawalActivity>("title" to "收益提现", "money" to withdrawalMoney)
            startActivity<WithdrawalKotActivity>("title" to "收益提现", "money" to withdrawalMoney)
        }
        //交易记录
        tv_Payrecord.setOnClickListener {
            startActivity<TradeListActivity>()
        }
    }


    private fun requestData() {
        val httpParams = OkClient.getParamsInstance().params
        httpParams.put("3", "190112")
        httpParams.put("42", merNo)
        OkClient.getInstance().post(httpParams, object : OkClient.EntityCallBack<BaseEntity>(context, BaseEntity::class.java) {
            override fun onSuccess(response: Response<BaseEntity>) {
                val model = response.body() ?: return
                if ("00" == model.str39) {
                    val list = getList<List<UserInfoModel>>(model.str57)
                    if (list.isEmpty()) {
                        return
                    }

                    val userInfoModel = list[0]
                    tv_todayMoney.text = model.str44
                    tv_totalMoney.text = model.str46

                    //可转入
                    totalMoney = userInfoModel.totalMoney
                    //可提现
                    withdrawalMoney = userInfoModel.withdrawalMoney


                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        requestData()
    }

}