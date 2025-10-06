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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.idscan.components.android.scanpdf417.PDF417ScanActivity;
import net.idscan.components.android.scanpdf417.PDF417ScanActivity.PDF417Data;

public class MainActivity extends AppCompatActivity {
    private final static int SCAN_ACTIVITY_CODE = 0x001;

    private final static int REQUEST_CAMERA_PERMISSIONS_DEFAULT = 0x100;
    private final static int REQUEST_CAMERA_PERMISSIONS_CUSTOM = 0x101;

    private final static String LIC_KEY = "yJeYeqHDzU1yKMw5wOWQ/7KOI8ubF2jWDpLfPUr0jKtu28UL2uzF18CrXdnKywyqATw/QfNBO5OOtX7uDIGTS3y8ha9GcsJtUvDP31e8M5gR34jfp76EujJtVDjYpFTSw5FlyXkNc3EFtKec4vMoT5rSmZJzx1vugTRuWNyfyXU=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewGroup content = findViewById(R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.rightMargin = insets.right;
            mlp.topMargin = insets.top;
            mlp.bottomMargin = insets.bottom;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        ((TextView) findViewById(R.id.tv_result)).setMovementMethod(new ScrollingMovementMethod());

        ((TextView) findViewById(R.id.tv_version)).setText("Version: " +
                net.idscan.components.android.scanpdf417.Version.getVersion());

        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDefaultScanView();
            }
        });

        findViewById(R.id.btn_scan_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomScanView();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSIONS_DEFAULT:
                if (checkCameraPermissions()) {
                    showDefaultScanView();
                }
                break;

            case REQUEST_CAMERA_PERMISSIONS_CUSTOM:
                if (checkCameraPermissions()) {
                    showCustomScanView();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_ACTIVITY_CODE) {
            TextView tv_result = findViewById(R.id.tv_result);

            switch (resultCode) {
                case PDF417ScanActivity.RESULT_OK:
                    if (data != null) {
                        PDF417Data result = data.getParcelableExtra(PDF417ScanActivity.DOCUMENT_DATA);
                        if (result != null) {
                            tv_result.setText(new String(result.barcodeData));
                        }
                    }

                    break;

                case PDF417ScanActivity.ERROR_INVALID_CAMERA_NUMBER:
                    tv_result.setText("Invalid camera number.");
                    break;

                case PDF417ScanActivity.ERROR_CAMERA_NOT_AVAILABLE:
                    tv_result.setText("Camera not available.");
                    break;

                case PDF417ScanActivity.ERROR_INVALID_CAMERA_ACCESS:
                    tv_result.setText("Invalid camera access.");
                    break;

                case PDF417ScanActivity.ERROR_RECOGNITION:
                    tv_result.setText(data.getStringExtra(PDF417ScanActivity.ERROR_DESCRIPTION));
                    break;

                case PDF417ScanActivity.RESULT_CANCELED:
                    break;

                default:
                    tv_result.setText("Undefined error.");
                    break;
            }
        }
    }

    private void showDefaultScanView() {
        if (checkCameraPermissions()) {
            Intent i = new Intent(MainActivity.this, PDF417ScanActivity.class);
            i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, LIC_KEY);
            startActivityForResult(i, SCAN_ACTIVITY_CODE);
        } else {
            requestCameraPermissions(REQUEST_CAMERA_PERMISSIONS_DEFAULT);
        }
    }

    private void showCustomScanView() {
        if (checkCameraPermissions()) {
            Intent i = new Intent(MainActivity.this, CustomScanActivity.class);
            i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, LIC_KEY);
            startActivityForResult(i, SCAN_ACTIVITY_CODE);
        } else {
            requestCameraPermissions(REQUEST_CAMERA_PERMISSIONS_CUSTOM);
        }
    }

    private boolean checkCameraPermissions() {
        int status = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return (status == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermissions(int requestCode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                requestCode);
    }
}
