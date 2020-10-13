package com.kotyj.com.activity.main

import android.support.design.widget.TabLayout
import butterknife.OnClick
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kotyj.com.R
import com.kotyj.com.adapter.NoticeListAdapter
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.NoticeModel
import com.kotyj.com.utils.ViewUtils
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.act_recycler_view.*
import kotlinx.android.synthetic.main.layout_title.*
import java.util.*


class NoticeListActivity : BaseActivity() {

    private val mList = ArrayList<NoticeModel>()
    private var page = 1
    private var type = "10B"
    private lateinit var mNoticeListAdapter: NoticeListAdapter


    override fun initLayout(): Int = R.layout.act_recycler_view

    override fun initData() {
        tv_title.text = "公告列表"
        iv_back.setOnClickListener { finish() }
        mNoticeListAdapter = NoticeListAdapter(mList)
        rv_list.adapter = mNoticeListAdapter
        initListener()
        loadData()
    }

    private fun initListener() {

        smartRefreshLayout.run {
            refreshHeader = ClassicsHeader(context)
            refreshFooter = ClassicsFooter(context)
            setOnRefreshListener {
                page = 1
                mList.clear()
                loadData(type)
            }
            setOnLoadmoreListener {
                page++
                loadData(type, page)
            }
        }

        tl_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                page = 1
                mList.clear()
                type = if (tab.position == 0) {
                    "10B"
                } else {
                    "10A"
                }
                mNoticeListAdapter.setNewData(mList)
                loadData(type, page)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        mNoticeListAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, view, position ->

            ViewUtils.showNoticeDetail(context, mList[position], object : ViewUtils.OnshowNoticeListener {
                override fun clickOk(noticeModel: NoticeModel) {
                    if (noticeModel.hasRead == 0) {
                        loadData(type, page, noticeModel.id)
                    }

                }
                override fun clickCancel() {

                }
            })
        }

    }

    private fun loadData(type: String = "10B", page: Int = 1, id: String = "") {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", "190103")
        httpParams.put("42", merNo)
        httpParams.put("43", type)
        if (id.isNotEmpty()) {
            httpParams.put("21", id)
        }
        httpParams.put("44", "$page")
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
                    if (id.isNotEmpty()) {
                        smartRefreshLayout.autoRefresh()
                        return
                    }
                    if (model.str60.isEmpty()) {
                        return
                    }

                    val list = getList<List<NoticeModel>>(model.str60)
                    mList.addAll(list)
                    mNoticeListAdapter.setNewData(mList)

                }
            }
        })
    }

    @OnClick(R.id.iv_back)
    fun onViewClicked() {
        ViewUtils.overridePendingTransitionBack(context)
    }
}
