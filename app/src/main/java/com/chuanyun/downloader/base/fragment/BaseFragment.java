package com.chuanyun.downloader.base.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ThreadUtils;

public abstract class BaseFragment extends Fragment {

    public String TAG = getClass().getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ClassName", "onCreate: ------Fragment :" + getClass().getName());
        return onFragmentCreateView(inflater, container, savedInstanceState);
    }

    protected abstract View onFragmentCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void showToast(String msg) {
        ThreadUtils.runOnUiThread(()-> {
            Toast toast = Toast.makeText(getContext(),msg,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        });
    }

}
