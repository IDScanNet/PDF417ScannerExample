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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import net.idscan.android.pdf417scanner.PDF417ScanActivity;

public class MainActivity extends AppCompatActivity {
    private final static int SCAN_ACTIVITY_CODE = 0x001;
    private final static String LIC_KEY = "xGEz0VTiEFVVESdoPMMDjeu6j7QwmJZpj6WjHBw2SDCKwzsv7fCbY0E+7w81VuhHHhAB3RESxiUCzF9c/uAK/PBPQMUxZiYGyZKI9A76hhlytAJCc0uTyGHhPQAXaO2mZK+wJzIsOm5hVTmAyYnYsbxOl2giQDBV9iyGR/mP/m8=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.tv_result)).setMovementMethod(new ScrollingMovementMethod());

        ((TextView)findViewById(R.id.tv_version)).setText("Version: " +
                net.idscan.android.pdf417scanner.Version.getVersion());

        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PDF417ScanActivity.class);
                i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, LIC_KEY);
                startActivityForResult(i, SCAN_ACTIVITY_CODE);
            }
        });

        findViewById(R.id.btn_scan_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CustomScanActivity.class);
                i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, LIC_KEY);
                startActivityForResult(i, SCAN_ACTIVITY_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_ACTIVITY_CODE) {
            TextView tv_result = (TextView) findViewById(R.id.tv_result);

            switch (resultCode) {
                case PDF417ScanActivity.RESULT_OK:
                    if (data != null)
                        tv_result.setText(data.getStringExtra(PDF417ScanActivity.BARCODE_DATA));
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

                case PDF417ScanActivity.ERROR_INVALID_LICENSE_KEY:
                    tv_result.setText("Invalid license key.");
                    break;

                default:
                    tv_result.setText("Undefined error.");
                    break;
            }
        }
    }
}
