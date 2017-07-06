package com.pixelplex.qtum.ui.fragment.SendFragment.QrCodeRecognitionFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;
import com.pixelplex.qtum.R;
import com.pixelplex.qtum.ui.fragment.SendFragment.SendFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QrCodeRecognitionFragment extends Fragment implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mZXingScannerView;

    @BindView(R.id.camera_container)
    LinearLayout mLinearLayout;

    public static QrCodeRecognitionFragment newInstance() {

        Bundle args = new Bundle();

        QrCodeRecognitionFragment fragment = new QrCodeRecognitionFragment();
        fragment.setArguments(args);
        return fragment;
    }


//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mZXingScannerView = new ZXingScannerView(getContext());
//        mZXingScannerView.setResultHandler(this);
//        return mZXingScannerView;
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mZXingScannerView = new ZXingScannerView(getContext());
        mZXingScannerView.setResultHandler(this);
        mLinearLayout.addView(mZXingScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SendFragment) getTargetFragment()).qrCodeRecognitionToolBar();
        mZXingScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((SendFragment) getTargetFragment()).sendToolBar();
        mZXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result.getText());
            ((SendFragment) getTargetFragment()).onResponse(jsonObject.getString("publicAddress"),
                    jsonObject.getDouble("amount"));
            getActivity().onBackPressed();
        } catch (JSONException e) {
            try {
                if (jsonObject != null) {
                    ((SendFragment) getTargetFragment()).onResponse(jsonObject.getString("publicAddress"),
                            0.0);
                }
                getActivity().onBackPressed();
            } catch (JSONException e1) {
                ((SendFragment) getTargetFragment()).onResponseError();
                getActivity().onBackPressed();
            }
        }
    }
}