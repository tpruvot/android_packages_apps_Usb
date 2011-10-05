/*
 * Copyright (C) 2011 CyanogenDefy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.motorola.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;

public class UsbHostActivity extends AlertActivity
        implements DialogInterface.OnClickListener
{
    private static final String TAG = "UsbHostActivity";

    private BroadcastReceiver mUsbHostReceiver;

    public UsbHostActivity() {
        mUsbHostReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "onReceive(), received Intent -- " + action);
                if (action.equals(UsbService.ACTION_CABLE_DETACHED)) {
                    cableUnpluged();
                    finish();
                }
            }
        };
    }

    public void onClick(DialogInterface dialog, int which) {

        if (which == AlertActivity.BUTTON_POSITIVE) {
            cableUnpluged();
        }
        finish();
    }

    public void cableUnpluged() {
        Intent intent = new Intent(UsbService.ACTION_MODE_SWITCH_CONFIRM);
        intent.putExtra(UsbService.EXTRA_MODE_SWITCH_MODE, UsbService.USB_MODE_HID);
        sendBroadcast(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbService.ACTION_CABLE_DETACHED);

        registerReceiver(mUsbHostReceiver, intentFilter);

        mAlertParams.mIconId = com.android.internal.R.drawable.ic_dialog_usb;
        mAlertParams.mTitle = getString(R.string.host_confirm_title);

        mAlertParams.mMessage = getString(R.string.host_confirm_switch,
                getIntent().getStringExtra(UsbService.EXTRA_ERROR_MODE_STRING));

        mAlertParams.mPositiveButtonText = getString(com.android.internal.R.string.ok);
        mAlertParams.mPositiveButtonListener = this;
        setupAlert();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbHostReceiver);
        super.onDestroy();
    }
}
