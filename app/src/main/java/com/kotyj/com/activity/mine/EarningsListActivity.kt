package com.kotyj.com.activity.mine

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.EarningModel
import com.kotyj.com.utils.CommonUtils
import com.kotyj.com.utils.DateUtil
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.general_mlist.*
import kotlinx.android.synthetic.main.layout_title.*

class EarningsListActivity : BaseActivity() {

    lateinit var myAdapter: MyAdapter
    var mList = ArrayList<EarningModel>()
    var page = 1

    override fun initLayout(): Int = R.layout.general_mlist

    override fun initData() {
        tv_title.text = intent.getStringExtra("title")

        myAdapter = MyAdapter(R.layout.item_earning_layout, mList)
        myAdapter.setEmptyView(R.layout.layout_bank_empty, recyclerView)
        recyclerView.adapter = myAdapter
        initListener()
        requestData()
    }

    fun initListener() {
        iv_back.setOnClickListener { finish() }
        smartRefreshLayout.run {
            setOnLoadmoreListener {
                page++
                requestData(page)
            }
            setOnRefreshListener {
                page = 1
                mList.clear()
                requestData()
            }
        }
    }

    fun requestData(page: Int = 1) {
        loadingDialog.show()
        val httpParams = OkClient.getParamsInstance().params
        httpParams.put("3", "990004")
        httpParams.put("42", merId)
        httpParams.put("40", "$page")
        httpParams.put("41", "10")
        httpParams.put("43", intent.getStringExtra("type"))
        OkClient.getInstance().post(httpParams, object : OkClient.EntityCallBack<BaseEntity>(context, BaseEntity::class.java) {
            override fun onSuccess(response: Response<BaseEntity>) {
                loadingDialog.dismiss()
                smartRefreshLayout.finishLoadmore()
                smartRefreshLayout.finishRefresh()
                val model = response.body() ?: return
                if ("00" == model.str39) {
                    val list = getList<List<EarningModel>>(model.str57)
                    mList.addAll(list)
                    myAdapter.setNewData(mList)
                }
            }

            override fun onError(response: Response<BaseEntity>?) {
                super.onError(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishLoadmore()
                smartRefreshLayout.finishRefresh()
            }
        })
    }

    class MyAdapter(res: Int, list: List<EarningModel>) : BaseQuickAdapter<EarningModel, BaseViewHolder>(res, list) {
        override fun convert(helper: BaseViewHolder, item: EarningModel) {
            helper.setText(R.id.tv_time, DateUtil.formatDateToHM_points_s(item.creatE_TIME.time))
                    .setText(R.id.tv_phone, item.phone?.run { CommonUtils.translateShortNumber(this, 3, 4) })
                    .setText(R.id.tv_typeName, item.cde_name)
                    .setText(R.id.tv_money, "+${item.trX_AMT}")
        }

    }


}