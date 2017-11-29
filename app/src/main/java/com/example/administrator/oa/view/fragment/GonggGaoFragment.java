package com.example.administrator.oa.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.MainActivity;
import com.example.administrator.oa.view.bean.BumenDataBean;
import com.example.administrator.oa.view.bean.ContactsBean;
import com.example.administrator.oa.view.bean.ContactsInfoBean;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
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

/**
 * Author:lsh
 * Version: 1.0
 * Description:
 * Date: 2017/3/28
 */
public class GonggGaoFragment extends BaseFragment {

    @BindView(R.id.lianxiren)
    XXRecycleView mXxreLianxiren;
    private MainActivity mainActivity;
    private List<BumenDataBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<BumenDataBean> mAdapter;
    private XXDialog mPersonalMsg;
    @Override
    protected int getChildLayoutRes() {
        return R.layout.fragment_app;
    }

    @Override
    protected void initView(View childHeadView, RelativeLayout rlBaseheaderBack, RelativeLayout rlBaseheaderHeader,
                            RelativeLayout rlBaseheaderRight, View childView, LinearLayout myStatusBar) {
        ((TextView) rlBaseheaderHeader.getChildAt(0)).setText("联系人");
        mainActivity = (MainActivity) getActivity();
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        childHeadView.setVisibility(View.GONE);
        myStatusBar.setVisibility(View.GONE);
        initThisView();
    }

    // R.layout.item_lianxiren
//    if (!TextUtils.isEmpty(item)) {
//        String firstStr = String.valueOf(item.charAt(0));
//        holder.setText(R.id.iv, firstStr);
//        holder.setText(R.id.name, item);
//
//    }

    private void initThisView() {
        mXxreLianxiren.setLayoutManager(new LinearLayoutManager(mainActivity));
        mAdapter = new CommonRecyclerAdapter<BumenDataBean>(mainActivity,datas, R.layout.item_lianxiren_head) {
            private XXRecycleView mXxreContacts;

            @Override
            public void convert(CommonViewHolder holder, final BumenDataBean item, final int i, boolean b) {
                holder.setText(R.id.name_bumen, item.getBumen() + "");
                mXxreContacts = ((XXRecycleView) holder.getView(R.id.xxre));
                ImageView arrow = (ImageView) holder.getView(R.id.arrow);
                if (item.isStatus()) {
                    mXxreContacts.setVisibility(View.VISIBLE);
                    arrow.setBackgroundResource(R.drawable.arrow_down_gray);
                } else {
                    mXxreContacts.setVisibility(View.GONE);
                    arrow.setBackgroundResource(R.drawable.arrow_up_gray);
                }

                List<ContactsInfoBean> content = item.getContent();
                mXxreContacts.setLayoutManager(new LinearLayoutManager(mainActivity));
                final CommonRecyclerAdapter<ContactsInfoBean> mAdapter = new CommonRecyclerAdapter<ContactsInfoBean>(mainActivity,content, R.layout.item_lianxiren) {
                    @Override
                    public void convert(CommonViewHolder holder1, ContactsInfoBean item2, int position, boolean b) {
                        holder1.setText(R.id.name, item2.getName() + "");
                        Bitmap bitmap = stringToBitmap(item2.getAvatar());
                        ((ImageView) holder1.getView(R.id.iv)).setImageBitmap(bitmap);
                    }
                };
                mXxreContacts.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, final int index) {
                        mPersonalMsg = new XXDialog(mainActivity, R.layout.item_personalmsg) {
                            @Override
                            public void convert(DialogViewHolder dialogViewHolder) {
                                final ContactsInfoBean bean = mAdapter.getDatas().get(index);
                                Bitmap bitmap = stringToBitmap(bean.getAvatar());
                                ((ImageView) dialogViewHolder.getView(R.id.icon)).setImageBitmap(bitmap);
                                dialogViewHolder.setText(R.id.name, bean.getName()+"");
                                // 座机号
                                if (!TextUtils.isEmpty(bean.getTelephone())) {
                                    dialogViewHolder.setText(R.id.telephone, bean.getTelephone());
                                    // 添加打电话功能 by aawang 2017/11/21
                                    dialogViewHolder.getView(R.id.lxr_telephone).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //用intent启动拨打电话
                                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+bean.getTelephone()));
                                            startActivity(intent);
                                        }
                                    });
                                }
                                // 手机号
                                if (!TextUtils.isEmpty(bean.getCellphone())) {
                                    dialogViewHolder.setText(R.id.cellphone, bean.getCellphone());
                                    // 添加打电话功能 by aawang 2017/11/21
                                    dialogViewHolder.getView(R.id.lxr_cellphone).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //用intent启动拨打电话
                                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+bean.getCellphone()));
                                            startActivity(intent);
                                        }
                                    });
                                }
                                if (TextUtils.isEmpty(bean.getPositionName())) {
                                    dialogViewHolder.setText(R.id.zhiwei, "未提供职位信息");
                                } else {
                                    dialogViewHolder.setText(R.id.zhiwei, ""+bean.getPositionName());
                                }

                                if (TextUtils.isEmpty(bean.getEmail())) {
                                    dialogViewHolder.setText(R.id.email, "未提供电子邮箱");
                                } else {
                                    dialogViewHolder.setText(R.id.email, ""+bean.getEmail());
                                }

                            }
                        }.fromBottomToMiddle().showDialog();
                    }
                });

                holder.getView(R.id.name_bumen).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isStatus()) {
                            item.setStatus(false);
                            GonggGaoFragment.this.mAdapter.notifyDataSetChanged();
                        } else {
                            item.setStatus(true);
                            GonggGaoFragment.this.mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        };
        mXxreLianxiren.setAdapter(mAdapter);
        RequestServerContactsList();
    }

    /**
     * 请求网络接口
     */
    private void RequestServerContactsList()  {
        //创建请求队列
        RequestQueue msgQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<ContactsBean> request = new JavaBeanRequest(UrlConstance.URL_CONTACTS_LIST,
                RequestMethod.GET, ContactsBean.class);
        msgQueue.add(0, request, new OnResponseListener<ContactsBean>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ContactsBean> response) {
                Log.w("44442", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (null != mAdapter) {
                        for (BumenDataBean bean : response.get().getData()) {
                            bean.setStatus(false);
                        }
                        mAdapter.replaceAll(response.get().getData());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ContactsBean> response) {
                Toast.makeText(mainActivity, "请求信息失败", Toast.LENGTH_SHORT).show();
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
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
     * 字符串 转换Bitmap
     * @param str
     * @return
     */
    private Bitmap stringToBitmap(String str){
        byte[] input = null;
        input = Base64.decode(str, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(input, 0, input.length);
        return bitmap;
    }

    /**
     * 当界面重新展示时（fragment.show）,调用onrequest刷新界面
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            RequestServerContactsList();
        }
    }
}
