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

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import net.idscan.android.pdf417scanner.PDF417ScanActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Camera customizer example.
 */
class CameraCustomizer implements PDF417ScanActivity.ICameraCustomizer {
    /**
     * Display parameters.
     */
    private final Display _display;
    /**
     * Information about camera.
     */
    private final Camera.CameraInfo _info;

    /**
     * Constructor.
     */
    public CameraCustomizer(Display display, Camera.CameraInfo info) {
        if (display == null)
            throw new NullPointerException("Parameter 'display' should not be null.");
        if (info == null)
            throw new NullPointerException("Parameter 'info' should not be null.");

        _display = display;
        _info = info;
    }

    @Override
    public void onCameraSetup(Camera camera, int format, int width, int height, CameraSettings settings) {
        // Gets preview size.
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> preview_sizes = params.getSupportedPreviewSizes();
        Collections.sort(preview_sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return rhs.width - lhs.width;
            }
        });

        // Select best preview size.
        float best_aspect = (width > height) ? (float) width / (float) height : (float) height / (float) width;
        Camera.Size best_preview_size = preview_sizes.get(0);
        for (Camera.Size s : preview_sizes) {
            if (s.width < best_preview_size.width)
                break;

            float ba = (float) best_preview_size.width / (float) best_preview_size.height;
            float sa = (float) s.width / (float) s.height;

            if (Math.abs(best_aspect - ba) > Math.abs(best_aspect - sa))
                best_preview_size = s;
        }

        // Set preview parameters.
        try {
            params.setPreviewFormat(ImageFormat.NV21);
            params.setPreviewSize(best_preview_size.width, best_preview_size.height);
            camera.setParameters(params);
        } catch (Exception ex) {
            Log.e(CameraCustomizer.class.getName(), "Can't setup preview size.");
            ex.printStackTrace();
        }

        // Get all available picture sizes.
        params = camera.getParameters();
        best_preview_size = params.getPreviewSize();
        List<Camera.Size> picture_sizes = params.getSupportedPictureSizes();
        Collections.sort(picture_sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return rhs.width - lhs.width;
            }
        });

        // Select best picture size.
        best_aspect = (float) best_preview_size.width / (float) best_preview_size.height;
        Camera.Size best_picture_size = picture_sizes.get(0);
        for (Camera.Size s : picture_sizes) {
            float ba = (float) best_picture_size.width / (float) best_picture_size.height;
            float sa = (float) s.width / (float) s.height;

            if (Math.abs(best_aspect - ba) > Math.abs(best_aspect - sa))
                best_picture_size = s;
        }

        Log.d("Best preview size", String.format("%d x %d", best_preview_size.width, best_preview_size.height));
        Log.d("Best picture size", String.format("%d x %d", best_picture_size.width, best_picture_size.height));

        // Set image parameters.
        try {
            params.setPictureSize(best_picture_size.width, best_picture_size.height);
            camera.setParameters(params);
        } catch (Exception ex) {
            Log.e(CameraCustomizer.class.getName(), "Can't setup picture size.");
            ex.printStackTrace();
        }

        // Setup autofocus.
        params = camera.getParameters();
        List<String> focus_modes = params.getSupportedFocusModes();
        if (focus_modes != null) {
            if (focus_modes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                // Set focus mode.
                try {
                    camera.setParameters(params);
                } catch (Exception ex) {
                    Log.e(CameraCustomizer.class.getName(), "Can't setup autofocus.");
                    ex.printStackTrace();
                }
            }
        }

        //calculate camera rotation.
        int degrees = 0;
        switch (_display.getRotation()) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break; //Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break; //Landscape right
        }
        int rotate;
        if (_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotate = (_info.orientation + degrees) % 360;
            rotate = (360 - rotate) % 360;
        } else
            rotate = (_info.orientation - degrees + 360) % 360;

        if (settings != null)
            settings.orientation = rotate;

        // Set camera orientation.
        try {
            camera.setDisplayOrientation(rotate);
        } catch (Exception ex) {
            Log.e(CameraCustomizer.class.getName(), "Can't setup display orientation.");
            ex.printStackTrace();
        }
    }
}
