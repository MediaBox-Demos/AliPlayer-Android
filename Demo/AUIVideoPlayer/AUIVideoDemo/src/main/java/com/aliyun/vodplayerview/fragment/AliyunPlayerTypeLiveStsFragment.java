package com.aliyun.vodplayerview.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.aliyun.auiplayerserver.GetAuthInformation;
import com.aliyun.auiplayerserver.bean.AliyunSts;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.util.ToastUtils;
import com.aliyun.vodplayerview.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * LiveSts播放方式的Fragment
 */
public class AliyunPlayerTypeLiveStsFragment extends PlayBaseFragment {

    private static final int REQ_CODE_PERMISSION = 0x1112;

    /**
     * STS相关信息
     */
    private EditText  mLiveStsUrlEditText,mStsRegionEditText,mStsAccessKeyIdEditText,
            mStsSecurityTokenEditText,mStsAccessKeySecretEditText,mStsDomainEditText,mStsAppEditText,mStsStreamEditText;

    private String mUrl,mRegion,mAccessKeyId,mSecurityToken,mAccessKeySecret,mLiveStsDomain,mLiveStsApp,mLiveStream;
    /**
     * 刷新
     */
    private TextView mRefreshTextView;
    private ImageView mQrCodeImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_live_sts_player_type, container, false);
        mLiveStsUrlEditText = view.findViewById(R.id.et_live_sts_url);
        mQrCodeImageView = view.findViewById(R.id.iv_qrcode);
        mRefreshTextView = view.findViewById(R.id.tv_refresh);
        mStsRegionEditText = view.findViewById(R.id.et_live_sts_region);
        mStsDomainEditText = view.findViewById(R.id.et_live_sts_domain);
        mStsAppEditText = view.findViewById(R.id.et_live_sts_app);
        mStsStreamEditText = view.findViewById(R.id.et_live_sts_stream);
        mStsAccessKeyIdEditText = view.findViewById(R.id.et_live_sts_access_key_id);
        mStsSecurityTokenEditText = view.findViewById(R.id.et_live_sts_security_token);
        mStsAccessKeySecretEditText = view.findViewById(R.id.et_live_sts_access_key_secret);
        initData();
        initListener();
        defaultPlayInfo();

        return view;
    }

    private void getVideoPlayStsInfo(){
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayLiveStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
            @Override
            public void onGetStsError(String errorMsg) {
                ToastUtils.show(getContext(),errorMsg);
            }

            @Override
            public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                if(dataBean != null){
                    mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
                    mStsAccessKeyIdEditText.setText(dataBean.getAccessKeyId());
                    mStsSecurityTokenEditText.setText(dataBean.getSecurityToken());
                    mStsAccessKeySecretEditText.setText(dataBean.getAccessKeySecret());
                    GlobalPlayerConfig.mLiveExpiration = dataBean.getExpiration();
                }
            }
        });
    }

    private void initData(){
        mStsDomainEditText.setText(GlobalPlayerConfig.mLiveStsDomain);
        mStsAppEditText.setText(GlobalPlayerConfig.mLiveStsApp);
        mStsStreamEditText.setText(GlobalPlayerConfig.mLiveStsStream);
        mLiveStsUrlEditText.setText(GlobalPlayerConfig.mLiveStsUrl);
        mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
        mStsAccessKeyIdEditText.setText(GlobalPlayerConfig.mLiveStsAccessKeyId);
        mStsSecurityTokenEditText.setText(GlobalPlayerConfig.mLiveStsSecurityToken);
        mStsAccessKeySecretEditText.setText(GlobalPlayerConfig.mLiveStsAccessKeySecret);
    }

    private void initListener(){
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrl = mLiveStsUrlEditText.getText().toString();
                if(TextUtils.isEmpty(mUrl)){
                    ToastUtils.show(getContext(),R.string.alivc_refresh_vid_empty);
                    return ;
                }
                getVideoPlayStsInfo();
            }
        });

        mQrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AliyunPlayerTypeLiveStsFragment.this.getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(AliyunPlayerTypeLiveStsFragment.this.getActivity(),
                            new String[] {Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            }
        });
    }

    private void startCaptureActivityForResult() {
        jumpCapture(CaptureActivity.REQ_CODE);
    }

    private void getInputContent(){
        mUrl = mLiveStsUrlEditText.getText().toString();
        mRegion = mStsRegionEditText.getText().toString();
        mAccessKeyId = mStsAccessKeyIdEditText.getText().toString();
        mSecurityToken = mStsSecurityTokenEditText.getText().toString();
        mAccessKeySecret = mStsAccessKeySecretEditText.getText().toString();
        mLiveStsDomain = mStsDomainEditText.getText().toString();
        mLiveStsApp = mStsAppEditText.getText().toString();
        mLiveStream = mStsStreamEditText.getText().toString();
    }

    private void setGlobaConfig(){
        getInputContent();

        GlobalPlayerConfig.mLiveStsUrl = mUrl;
        GlobalPlayerConfig.mRegion = mRegion;
        GlobalPlayerConfig.mLiveStsAccessKeyId = mAccessKeyId;
        GlobalPlayerConfig.mLiveStsSecurityToken = mSecurityToken;
        GlobalPlayerConfig.mLiveStsAccessKeySecret = mAccessKeySecret;
        GlobalPlayerConfig.mLiveStsDomain = mLiveStsDomain;
        GlobalPlayerConfig.mLiveStsApp = mLiveStsApp;
        GlobalPlayerConfig.mLiveStsStream = mLiveStream;
    }

    @Override
    public void defaultPlayInfo() {
        getVideoPlayStsInfo();
    }

    @Override
    public void confirmPlayInfo() {
        setGlobaConfig();
        GlobalPlayerConfig.LIVE_STS_TYPE_CHECKED = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    ToastUtils.show(this.getActivity(), getString(R.string.alivc_player_agree_camera_permission));
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        //or do sth
                        String url = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        mLiveStsUrlEditText.setText(url);
                        //切割URL
                        if(!TextUtils.isEmpty(url) && url.contains("://") && url.contains("?")){
                            String str = url.substring((url.indexOf("://") + 3), url.indexOf("?"));
                            String res;
                            if(str.contains("_")){
                                res = str.substring(0, str.lastIndexOf("_"));
                            }else{
                                res = str;
                            }

                            String[] split = res.split("/");
                            Log.e("AliyunPlayerView", " QR_Code : " + res);
                            if(split.length >= 3){
                                mStsDomainEditText.setText(split[0]);
                                mStsAppEditText.setText(split[1]);
                                if(split[2].contains(".")){
                                    split[2] = split[2].substring(0,split[2].indexOf("."));
                                }
                                mStsStreamEditText.setText(split[2]);
                            }
                        }
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            mLiveStsUrlEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }
}
