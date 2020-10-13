package com.kotyj.com.activity.main

import android.support.v7.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotyj.com.R
import com.kotyj.com.activity.`fun`.OperateDetailActivity
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.OperateModel
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.act_newbie_guide.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity

class NewbieGuideActivity : BaseActivity() {
    lateinit var myAdapter: MyAdapter

    lateinit var mList: List<OperateModel>
    override fun initLayout(): Int = R.layout.act_newbie_guide

    override fun initData() {
        mList = ArrayList()
        tv_title.text = "新手指南"
        iv_back.setOnClickListener { finish() }
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        myAdapter = MyAdapter(R.layout.item_newguide, mList)
        recyclerView.adapter = myAdapter

        myAdapter.setOnItemClickListener { _, _, position ->
            startActivity<OperateDetailActivity>("model" to mList[position], "title" to "新手指南")
        }

        getData()
    }

    private fun getData() {
        val httpParams = HttpParams()
        httpParams.put("3", "792001")
        httpParams.put("21", "10A")
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
                    mList = getList(model.str57)
                    myAdapter.setNewData(mList)
                }
            }
        })
    }


    class MyAdapter(res: Int, list: List<OperateModel>) : BaseQuickAdapter<OperateModel, BaseViewHolder>(res, list) {
        override fun convert(helper: BaseViewHolder, item: OperateModel) {
            helper.setText(R.id.tv_t, item.title)
        }

    }
}