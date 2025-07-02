package com.aliyun.vodplayerview.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.aliyun.auiplayerserver.GetAuthInformation;
import com.aliyun.auiplayerserver.bean.AliyunVideoList;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.util.ToastUtils;
import com.aliyun.vodplayerview.R;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * URL播放方式的Fragment
 */
public class AliyunPlayerTypeUrlFragment extends PlayBaseFragment {

    private static final int REQ_CODE_PERMISSION = 0x1111;

    private EditText mUrlEditText;
    private String mUrlPath;
    private ImageView mQrCodeImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_url_player_type, container, false);
        mUrlEditText = view.findViewById(R.id.et_url);
        mQrCodeImageView = view.findViewById(R.id.iv_qrcode);
        initData();
        initListener();
        if(TextUtils.isEmpty(GlobalPlayerConfig.mUrlPath)){
            defaultPlayInfo();
        }
        return view;
    }

    private void getPlayUrlInfo(){
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayUrlInfo(new GetAuthInformation.OnGetUrlInfoListener() {
            @Override
            public void onGetUrlError(String msg) {
                if(getContext() != null){
                    ToastUtils.show(getContext(),msg);
                }
            }

            @Override
            public void onGetUrlSuccess(AliyunVideoList.VideoList dataBean) {
                if(dataBean != null){
                    List<AliyunVideoList.VideoList.VideoListItem> playInfoList = dataBean.getPlayInfoList();
                    if(playInfoList != null && playInfoList.size() > 0){
                        AliyunVideoList.VideoList.VideoListItem videoListItem = playInfoList.get(0);
                        mUrlEditText.setText(videoListItem.getPlayURL());
                        mUrlPath = videoListItem.getPlayURL();
                    }
                }
            }
        });
    }

    private void initData(){
        mUrlEditText.setText(GlobalPlayerConfig.mUrlPath);
    }

    private void initListener(){
        mQrCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AliyunPlayerTypeUrlFragment.this.getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(AliyunPlayerTypeUrlFragment.this.getActivity(),
                            new String[] {Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            }
        });
    }

    @Override
    public void defaultPlayInfo() {
        getPlayUrlInfo();
    }

    @Override
    public void confirmPlayInfo() {
        mUrlPath = mUrlEditText.getText().toString();
        GlobalPlayerConfig.mUrlPath = mUrlPath;
        GlobalPlayerConfig.URL_TYPE_CHECKED = true;
    }

    private void startCaptureActivityForResult() {
        jumpCapture(CaptureActivity.REQ_CODE);
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
        if (requestCode == CaptureActivity.REQ_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    //or do sth
                    mUrlEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                    break;
                case RESULT_CANCELED:
                    if (data != null) {
                        // for some reason camera is not working correctly
                        mUrlEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
