/*
 * Copyright (c) 2017 IDScan.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Support: support@idscan.net
 */

package net.idscan.android.pdf417scannerexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.idscan.components.android.scanpdf417.PDF417ScanActivity;

public class CustomScanActivity extends PDF417ScanActivity {
    private static final String _PREFS_NAME = "CustomScanActivitySettings";

    private int _number_of_cameras = 0;
    private int _current_camera = 0;
    private PDF417Data _data;

    private Button _btn_confirm;
    private TextView _tv_scanned_data;

    @Override
    protected int selectCamera(int number_of_cameras) {
        _number_of_cameras = number_of_cameras;

        // Get the last selected camera.
        SharedPreferences pref = getSharedPreferences(_PREFS_NAME, Context.MODE_PRIVATE);
        _current_camera = pref.getInt("camera", 0);

        if (_current_camera >= _number_of_cameras) {
            _current_camera = 0;
            pref.edit()
                    .putInt("camera", _current_camera)
                    .apply();
        }

        return _current_camera;
    }

    @Override
    protected View getViewFinder(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.custom_viewfinder, null);

        View old_vf = super.getViewFinder(inflater);
        if (old_vf != null) {
            FrameLayout old_vf_layout = v.findViewById(R.id.old_vf);
            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            old_vf_layout.addView(old_vf, p);
        }

        _tv_scanned_data = v.findViewById(R.id.tv_scanned_data);

        _btn_confirm = v.findViewById(R.id.btn_confirm);
        _btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(_data);
            }
        });

        v.findViewById(R.id.btn_next_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Select next camera.
                _current_camera++;
                if (_current_camera >= _number_of_cameras)
                    _current_camera = 0;

                if (setCamera(_current_camera)) {
                    // Save the last selected camera.
                    getSharedPreferences(_PREFS_NAME, Context.MODE_PRIVATE).edit()
                            .putInt("camera", _current_camera)
                            .apply();
                }
            }
        });

        v.findViewById(R.id.btn_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlashState(!getFlashState());
            }
        });

        return v;
    }

    @Override
    protected void onData(@NonNull PDF417Data result) {
        _data = result;
        _tv_scanned_data.setText(new String(_data.barcodeData));
        _btn_confirm.setVisibility(View.VISIBLE);
    }
}
