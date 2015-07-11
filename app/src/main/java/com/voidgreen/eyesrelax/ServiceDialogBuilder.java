package com.voidgreen.eyesrelax;

import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager;

/**
 * Created by VOID on 11-07-15.
 */
public class ServiceDialogBuilder extends AlertDialog.Builder {

    public ServiceDialogBuilder(Context context) {
        super(context);}

    @Override
    public AlertDialog create() {
        AlertDialog dialog = super.create();
        //Change dialog window type from TYPE_CHANGED to TYPE_SYSTEM_ALERT
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return dialog;
    }

    @Override
    public AlertDialog show()   {
        return super.show();
    }}
