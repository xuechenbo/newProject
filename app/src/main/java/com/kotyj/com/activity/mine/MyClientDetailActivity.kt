package com.kotyj.com.activity.mine

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.ButterKnife
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kotyj.com.R
import com.kotyj.com.adapter.ClientListAdapter
import com.kotyj.com.base.BaseActivity
import com.kotyj.com.model.BaseEntity
import com.kotyj.com.model.ClientModel
import com.kotyj.com.utils.CommonUtils
import com.kotyj.com.utils.PermissionUtil
import com.kotyj.com.utils.getList
import com.kotyj.com.utils.okgo.OkClient
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.act_my_client_detail.*
import kotlinx.android.synthetic.main.layout_title.*
import java.util.*

class MyClientDetailActivity : BaseActivity() {

    private val mList = ArrayList<ClientModel>()
    private lateinit var mClientListAdapter: ClientListAdapter
    private var pageIndex = 1
    private var type: String? = null
    private var typeTop: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
    }

    override fun initLayout(): Int {
        return R.layout.act_my_client_detail
    }

    override fun initData() {
        typeTop = intent.getStringExtra("typeTop")
        type = intent.getStringExtra("type")
        tv_title.text = when (type) {
            "10A" -> "直推"
            "10B" -> "团队"
            "10C" -> "实名用户"
            else -> "VIP用户"

        }
        rv_list.layoutManager = LinearLayoutManager(context)
        mClientListAdapter = ClientListAdapter(mList)
        mClientListAdapter.setEmptyView(R.layout.layout_bank_empty, rv_list)
        rv_list.adapter = mClientListAdapter

        initListener()
        loadData()
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }

        smartRefreshLayout.setOnRefreshListener {
            pageIndex = 1
            mList.clear()
            loadData()
        }


        smartRefreshLayout.setOnLoadmoreListener {
            pageIndex++
            loadData()
        }

        mClientListAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            if (PermissionUtil.CALL_PHONE(context)) {
                when (view.id) {
                    R.id.takePhone -> mList[position].phone?.run { showCallServerDialog(this) }
                }
            }
        }
    }

    private fun showCallServerDialog(phone: String) {
        val confirmBt: Button
        val cancleBt: Button
        val mydialog = Dialog(context, R.style.MyProgressDialog)
        mydialog.setContentView(R.layout.callserver_dialog)
        mydialog.setCanceledOnTouchOutside(false)
        val phonenum = mydialog.findViewById<TextView>(R.id.phoneNum)
        val tvTitle = mydialog.findViewById<TextView>(R.id.tv_dialogTitle)
        tvTitle.text = "拨打电话"
        phonenum.text = phone
        confirmBt = mydialog.findViewById(R.id.bt_cancelPlan)
        cancleBt = mydialog.findViewById(R.id.bt_suspendCancel)
        confirmBt.setOnClickListener(View.OnClickListener {
            if (CommonUtils.isFastDoubleClick2()) {
                return@OnClickListener
            }
            mydialog.dismiss()
            val serviceNumber = phone.replace("-", "")
            val phoneIntent = Intent("android.intent.action.CALL", Uri.parse("tel:$serviceNumber"))
            startActivity(phoneIntent)
        })
        cancleBt.setOnClickListener { mydialog.dismiss() }

        mydialog.show()
    }

    private fun loadData() {
        loadingDialog.show()
        val httpParams = OkClient.getParamsInstance().params
        httpParams.put("3", "990007")
        httpParams.put("40", pageIndex)
        httpParams.put("41", "10")
        httpParams.put("42", merId)
        httpParams.put("43", type)
        httpParams.put("44", typeTop)
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
                    val list = getList<List<ClientModel>>(model.str57)
//                    val list = JSONArray.parseArray(model.str57, ClientModel::class.java)
                    if (list != null && list.isNotEmpty()) {
                        mList.addAll(list)
                    }
                    mClientListAdapter!!.setNewData(mList)
                }
            }
        })
    }
}
