package com.kotyj.com.activity.mine

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.OrientationHelper
import com.alibaba.fastjson.JSONArray
import com.kotyj.com.R
import com.kotyj.com.adapter.WithdrawListAdapter
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.WithdrawModel
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.general_mlist.*
import kotlinx.android.synthetic.main.layout_title.*
import java.util.*

class WithdrawListActivity : BaseActivity() {

    lateinit var mWithdrawListAdapter: WithdrawListAdapter
    private val mList = ArrayList<WithdrawModel>()


    override fun initLayout(): Int {
        return R.layout.general_mlist
    }

    override fun initData() {
        tv_title.text = "提现记录"
        recyclerView.addItemDecoration(DividerItemDecoration(context, OrientationHelper.VERTICAL))
        mWithdrawListAdapter = WithdrawListAdapter(mList)
        mWithdrawListAdapter.run {
            bindToRecyclerView(recyclerView)
            setEmptyView(R.layout.layout_bank_empty, recyclerView)
        }
        initListener()
        loadData()
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        smartRefreshLayout.run {
            isEnableLoadmore = false
            setOnRefreshListener {
                loadData()
            }
        }
    }

    private fun loadData() {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", "190125")
        httpParams.put("42", merNo)
        OkClient.getInstance().post(httpParams, object : OkClient.EntityCallBack<BaseEntity>(context, BaseEntity::class.java) {
            override fun onError(response: Response<BaseEntity>) {
                super.onError(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishRefresh()
            }

            override fun onSuccess(response: Response<BaseEntity>) {
                super.onSuccess(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishRefresh()
                val model = response.body() ?: return

                if ("00" == model.str39) {
                    val list = JSONArray.parseArray(model.str57, WithdrawModel::class.java)
                    mList.addAll(list)
                    mWithdrawListAdapter!!.setNewData(mList)
                }
            }
        })
    }
}
