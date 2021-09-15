package com.example.twilliovideocallingapp;

import android.content.Context;
import android.widget.Toast;

public class ToastClass {
   Context context;
    public  ToastClass(Context context)
    {
        this.context=context;
    }
    public void setToast(String str) {
        Toast.makeText(this.context, str, Toast.LENGTH_SHORT).show();
    }
}
