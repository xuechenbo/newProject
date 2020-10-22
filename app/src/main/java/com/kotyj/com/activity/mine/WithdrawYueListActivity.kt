package com.kotyj.com.activity.mine

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.OrientationHelper
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.WithdrawYueModel
import com.kotyj.com.utils.DateUtil
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.general_mlist.*
import kotlinx.android.synthetic.main.layout_title.*
import java.util.*

class WithdrawYueListActivity : BaseActivity() {
    private val mList = ArrayList<WithdrawYueModel>()
    lateinit var myAdapter: MyAdapter
    private var page = 1

    override fun initLayout(): Int {
        return R.layout.general_mlist
    }


    override fun initData() {
        tv_title.text = "转入记录"
        recyclerView.addItemDecoration(DividerItemDecoration(context, OrientationHelper.VERTICAL))
        myAdapter = MyAdapter(R.layout.item_withdraw_list, mList)
        myAdapter.run {
            bindToRecyclerView(recyclerView)
            setEmptyView(R.layout.layout_bank_empty, recyclerView)
        }

        initListener()
        loadData(page)

    }

    private fun initListener() {
        smartRefreshLayout.run {
            setOnRefreshListener {
                page = 1
                mList.clear()
                loadData(page)
            }
            setOnLoadmoreListener {
                page++
                loadData(page)
            }
        }
        iv_back.setOnClickListener { finish() }
    }

    class MyAdapter(layoutResId: Int, data: List<WithdrawYueModel>?) : BaseQuickAdapter<WithdrawYueModel, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: WithdrawYueModel) {
            helper.setGone(R.id.tv_status, false)
                    .setText(R.id.tv_date_start, DateUtil.formatDateToHM_points_s(item.create_time.time))
                    .setText(R.id.tv_money, "-${item.money}")


        }
    }

    private fun loadData(page: Int) {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", "990000")
        httpParams.put("42", merId)
        httpParams.put("40", page.toString() + "")
        httpParams.put("41", "10")
        OkClient.getInstance().post(httpParams, object : OkClient.EntityCallBack<BaseEntity>(context, BaseEntity::class.java) {
            override fun onError(response: Response<BaseEntity>) {
                super.onError(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishRefresh()
                smartRefreshLayout.finishLoadmore()

            }

            override fun onSuccess(response: Response<BaseEntity>) {
                super.onSuccess(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishRefresh()
                smartRefreshLayout.finishLoadmore()
                val model = response.body() ?: return
                if ("00" == model.str39) {
                    if (model.str57.isNotEmpty()) {
                        val list = getList<List<WithdrawYueModel>>(model.str57)
                        mList.addAll(list)
                        myAdapter!!.setNewData(mList)
                    }
                }
            }
        })
    }
}
