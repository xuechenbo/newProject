package com.kotyj.com.activity.mine

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotyj.com.R
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.ImtegerModel
import com.kotyj.com.utils.DateUtil
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.act_integer_list.*
import kotlinx.android.synthetic.main.general_mlist.recyclerView
import kotlinx.android.synthetic.main.general_mlist.smartRefreshLayout
import kotlinx.android.synthetic.main.layout_title.*

class MyIntegerActivity : BaseActivity() {

    lateinit var myAdapter: MyAdapter
    var mList = ArrayList<ImtegerModel>()

    var page = 1

    override fun initLayout(): Int {
        return R.layout.act_integer_list
    }

    override fun initData() {
        tv_title.text = "我的积分"
        myAdapter = MyAdapter(mList)
        recyclerView.adapter = myAdapter
        initListener()
        loadData()
    }


    private fun initListener() {
        iv_back.setOnClickListener { finish() }

        smartRefreshLayout.run {
            refreshHeader = ClassicsHeader(context)
            refreshFooter = ClassicsFooter(context)
            setOnLoadmoreListener {
                page++
                loadData(page)
            }
            setOnRefreshListener {
                mList.clear()
                page = 1
                loadData()
            }
        }
    }

    private fun loadData(page: Int = 1) {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", "898906")
        httpParams.put("40", page)
        httpParams.put("41", "10")
        httpParams.put("42", merId)
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

                    tv_remaiInteger.text = "剩余积分:${model.str28}"
                    tv_total_Integer.text = "总积分:${model.str29}"
                    if (model.str30.isEmpty() || model.str30 == "[]") {
                        return
                    }
                    val list = getList<List<ImtegerModel>>(model.str30)
                    mList.addAll(list)
                    myAdapter.setNewData(mList)
                }
            }
        })
    }


    class MyAdapter(list: List<ImtegerModel>) : BaseQuickAdapter<ImtegerModel, BaseViewHolder>(R.layout.item_trade_list, list) {

        override fun convert(helper: BaseViewHolder, item: ImtegerModel) {
            helper.setText(R.id.tv_name, item.type)
                    .setText(R.id.tv_date, DateUtil.formatDate_Sdftwo(item.CREATE_TIME.time))
                    .setText(R.id.tv_money, "${if (item.status == "增加") "+" else "-"}${item.POINT}")
        }

    }


}