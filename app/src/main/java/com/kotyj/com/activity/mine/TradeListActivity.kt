package com.kotyj.com.activity.mine

import android.os.Bundle
import butterknife.ButterKnife
import com.kotyj.com.R
import com.kotyj.com.adapter.QueryNewAdapter
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.QueryModel
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.act_trade.*
import kotlinx.android.synthetic.main.layout_title.*
import org.jetbrains.anko.startActivity
import java.util.*

class TradeListActivity : BaseActivity() {

    private var pageIndex = 1
    private val mList = ArrayList<QueryModel>()
    lateinit var mQueryAdapter: QueryNewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
    }

    override fun initLayout(): Int {
        return R.layout.act_trade
    }

    override fun initData() {
        tv_title!!.text = "交易明细"
        mQueryAdapter = QueryNewAdapter(mList)
        mQueryAdapter.run {
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.layout_bank_empty, rv_list)
        }
        initListener()
        loadData()
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        smartRefreshLayout.run {
            refreshHeader = ClassicsHeader(context)
            refreshFooter = ClassicsFooter(context)

            setOnRefreshListener {
                pageIndex = 1
                mList.clear()
                loadData()
            }
            setOnLoadmoreListener {
                pageIndex++
                loadData()
            }
        }
        mQueryAdapter.setOnItemClickListener { _, _, position ->
            startActivity<TradeRecordDetailActivity>("detail" to mList[position])
        }
    }


    private fun loadData() {
        loadingDialog.show()
        val httpParams = HttpParams()
        httpParams.put("3", "990005")
        httpParams.put("42", merId)
        httpParams.put("40", pageIndex.toString())
        httpParams.put("41", "10")
        OkClient.getInstance().post(httpParams, object : OkClient.EntityCallBack<BaseEntity>(context, BaseEntity::class.java) {
            override fun onError(response: Response<BaseEntity>) {
                super.onError(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishLoadmore()
                smartRefreshLayout.finishRefresh()
            }

            override fun onSuccess(response: Response<BaseEntity>) {
                super.onSuccess(response)
                loadingDialog.dismiss()
                smartRefreshLayout.finishLoadmore()
                smartRefreshLayout.finishRefresh()
                val model = response.body() ?: return

                if ("00" == model.str39) {
                    val list = getList<List<QueryModel>>(model.str57)
                    mList.addAll(list)
                    mQueryAdapter!!.setNewData(mList)

                }
            }
        })
    }
}
