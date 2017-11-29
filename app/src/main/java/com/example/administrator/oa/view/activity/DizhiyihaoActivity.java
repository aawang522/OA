package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.GoodsRegistrationBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
import com.example.administrator.oa.view.bean.ZuzhiUserListResponse;
import com.example.administrator.oa.view.bean.organization_structure.ChildrenBean;
import com.example.administrator.oa.view.bean.organization_structure.OrganizationResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.DialogViewHolder;
import com.luoshihai.xxdialog.XXDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class DizhiyihaoActivity extends HeadBaseActivity {
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.add)
    TextView mAdd;
    @BindView(R.id.goodsName)
    EditText mGoodsName;
    @BindView(R.id.wuping_guige)
    EditText mWupingGuige;
    @BindView(R.id.wuping_count)
    EditText mWupingCount;
    @BindView(R.id.beizhu)
    EditText mBeizhu;
//    @BindView(R.id.tv_buzhang)
//    TextView mTvBuzhang;
//    @BindView(R.id.ll_buzhang)
//    LinearLayout mLlBuzhang;
    @BindView(R.id.bumen_fuzeren)
    TextView mBumenFuzeren;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    private CommonRecyclerAdapter<GoodsRegistrationBean> mAdapter;
    private List<GoodsRegistrationBean> datas = new ArrayList<>();
    private String mSessionId;
    private String mUserName;
    private String mDepartmentName;
    private String processDefinitionId;
    private String mUserId;
    private String mDepartmentId;

    private XXDialog mxxDialog2;
    private XXDialog mxxUsersDialog;
    private String mBumenLeaderId = "";
    private String mFenguanLeaderId = "";

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_dizhiyihao;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("低值易耗品领用");
        initThisView();
    }

    private void initThisView() {

        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mName.setText(mUserName);
        mBumen.setText(mDepartmentName);

        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<GoodsRegistrationBean>(this, datas, R.layout.item_wupin_lingqu) {
            @Override
            public void convert(CommonViewHolder holder, final GoodsRegistrationBean item, int position, boolean b) {

                holder.setText(R.id.number, "( " + (position + 1) + " )");
                holder.setText(R.id.name, item.getGoods());
                holder.setText(R.id.wuping_guige, item.getFormat());
                holder.setText(R.id.wuping_count, item.getNum());
                holder.setText(R.id.beizhu, item.getRemarks());

                holder.getView(R.id.delet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.remove(item);
                    }
                });
            }
        };
        mXxre.setAdapter(mAdapter);

        checkFormCaoGao();
    }

    /**
     * 检测是否是从草稿箱界面跳转过来
     */
    private void checkFormCaoGao(){
        String businessKey = getIntent().getStringExtra("businessKey");
        if(!TextUtils.isEmpty(businessKey)){
            // 获取草稿信息
            RequestServerGetInfo(businessKey);
        }
    }

    /**
     * 获取草稿信息
     */
    private void RequestServerGetInfo(String businessKey) {
        String sessionId = SPUtils.getString(this, "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<QingjiaShenheResponse> request = new JavaBeanRequest<>(UrlConstance.URL_CAOGAOXIANG_INFO,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        Queue.add(0, request, new OnResponseListener<QingjiaShenheResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    //按顺序填写数据
//                    mBumen.setText(shenheBeen.get(0).getValue());
                    // TODO 人员id
//                    mBumenFuzeren.setText(shenheBeen.get(1).getValue());
////                    mName.setText(shenheBeen.get(2).getValue());
//                    mDate.setText(shenheBeen.get(3).getValue());

                    for (QingjiaShenheBean bean : shenheBeen) {
                        Log.d("Caogao", bean.getLabel());
                        Log.d("Caogao", bean.getValue());
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            // TODO 负责人
                            case "minister":
//                                mBianhao.setText(value);
                                break;
                            case "date":
                                mDate.setText(value);
                                break;
                        }
                    }

                    //垃圾后台，我只想说
                    //把good，format，num，remarks
                    ArrayList<QingjiaShenheBean> goods = new ArrayList<>();
                    ArrayList<QingjiaShenheBean> format = new ArrayList<>();
                    ArrayList<QingjiaShenheBean> num = new ArrayList<>();
                    ArrayList<QingjiaShenheBean> remarks = new ArrayList<>();

                    for (QingjiaShenheBean bean : shenheBeen) {
                        if (bean.getLabel().startsWith("goods")) {
                            goods.add(bean);
                        }
                        if (bean.getLabel().startsWith("format")) {
                            format.add(bean);
                        }
                        if (bean.getLabel().startsWith("num")) {
                            num.add(bean);
                        }
                        if (bean.getLabel().startsWith("remarks")) {
                            remarks.add(bean);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(DizhiyihaoActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    // R.id.ll_buzhang,
    @OnClick({R.id.ll_start, R.id.btn_caogao, R.id.btn_commit, R.id.add, R.id.ll_bumen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                selectDate(mDate, "");
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mDate.getText().toString().trim(), mDate.getText().toString().trim(),
                        mBumenFuzeren.getText().toString().trim()
                );
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
            case R.id.add:
                String name = mGoodsName.getText().toString().trim();
                String wuping_guige = mWupingGuige.getText().toString().trim();
                String wuping_count = mWupingCount.getText().toString().trim();
                String beizhu = mBeizhu.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "请输入物品名称", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(wuping_guige)) {
                    Toast.makeText(this, "请输入物品规格", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(wuping_count)) {
                    Toast.makeText(this, "请输入物品数量", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.add(new GoodsRegistrationBean(name, wuping_guige, wuping_count, beizhu));
                    mXxre.scrollToPosition(mAdapter.getItemCount() - 1);
                    mGoodsName.setText("");
                    mWupingGuige.setText("");
                    mWupingCount.setText("");
                    mBeizhu.setText("");
                }
                break;
//            case R.id.ll_buzhang:
//                List<String> datas_buzhang = new ArrayList();
//                datas_buzhang.add("是");
//                datas_buzhang.add("否");
//                chooseDate(datas_buzhang, mTvBuzhang, "是否需要部长审核");
//                break;
            case R.id.ll_bumen:
//                RequestServerGetZuzhi("请选择部门负责人",mBumenFuzeren);
                if("0".equals(mLlBumen.getTag())) {
                    mLlBumen.setTag("1");
                    RequestServerGetZuzhi(mLlBumen, mBumenFuzeren, "请选择部门负责人", null);
                }
                break;
        }
    }

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "领用人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumenFuzeren.getText().toString().trim())) {
            Toast.makeText(this, "请选择部门负责人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDate.getText().toString().trim())) {
            Toast.makeText(this, "请选择领用时间", Toast.LENGTH_SHORT).show();
        } else if (mAdapter.getDatas().size() < 1) {
            Toast.makeText(this, "请填写物品明细单，并确认添加", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvBuzhang.getText().toString().trim())) {
//            Toast.makeText(this, "请选择是否需要部长审核", Toast.LENGTH_SHORT).show();

        } else {
            // mTvBuzhang.getText().toString().trim(),
            RequestServerGoodsLingqu();
        }

    }

    /**
     * 请求您网络，提交数据
     */
    // String comment,
    private void RequestServerGoodsLingqu() {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"date\":" + "\"" + mDate.getText().toString().trim() + "\",")
                .append("\"minister_name\":" + "\"" + mBumenFuzeren.getText().toString().trim() + "\",")
                .append("\"minister\":" + "\"" + mBumenFuzeren.getTag().toString() + "\",");
//                .append("\"comment\":" + "\"" + comment + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mAdapter.getDatas().size() - 1) {
                GoodsRegistrationBean bean = mAdapter.getDatas().get(i);
                json.append("\"goods" + (i + 1) + "\":" + "\"" + bean.getGoods() + "\",")
                        .append("\"format" + (i + 1) + "\":" + "\"" + bean.getFormat() + "\",")
                        .append("\"num" + (i + 1) + "\":" + "\"" + bean.getNum() + "\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"" + bean.getRemarks() + "\",");
            } else {
                json.append("\"goods" + (i + 1) + "\":" + "\"\",")
                        .append("\"format" + (i + 1) + "\":" + "\"\",")
                        .append("\"num" + (i + 1) + "\":" + "\"\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("data", json.toString());
        Log.w("99999", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(DizhiyihaoActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(DizhiyihaoActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 请求您网络，提交数据
     */
    // String comment,
    private void RequestServerGoodsLingqu_Save(String time, String title, String bumenleader) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"date\":" + "\"" + time + "\",")
                .append("\"minister_name\":" + "\"" + mBumenFuzeren.getText().toString().trim() + "\",")
                .append("\"minister\":" + "\"" + mBumenFuzeren.getTag().toString() + "\",");
//                .append("\"comment\":" + "\"" + comment + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mAdapter.getDatas().size() - 1) {
                GoodsRegistrationBean bean = mAdapter.getDatas().get(i);
                json.append("\"goods" + (i + 1) + "\":" + "\"" + bean.getGoods() + "\",")
                        .append("\"format" + (i + 1) + "\":" + "\"" + bean.getFormat() + "\",")
                        .append("\"num" + (i + 1) + "\":" + "\"" + bean.getNum() + "\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"" + bean.getRemarks() + "\",");
            } else {
                json.append("\"goods" + (i + 1) + "\":" + "\"\",")
                        .append("\"format" + (i + 1) + "\":" + "\"\",")
                        .append("\"num" + (i + 1) + "\":" + "\"\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("data", json.toString());
        Log.w("99999", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(DizhiyihaoActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(DizhiyihaoActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
