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
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import net.idscan.components.android.camerareader.CameraSettings;
import net.idscan.components.android.camerareader.ICameraCustomizer;

import java.util.List;

/**
 * Camera customizer example.
 */
class CameraCustomizer implements ICameraCustomizer {
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
    CameraCustomizer(Display display, Camera.CameraInfo info) {
        if (display == null)
            throw new NullPointerException("Parameter 'display' should not be null.");
        if (info == null)
            throw new NullPointerException("Parameter 'info' should not be null.");

        _display = display;
        _info = info;
    }

    @Override
    public void onCameraSetup(Camera camera, int format, int width, int height, CameraSettings settings) {
        //calculate camera rotation.
        int rotate = getCameraOrientation(_info, _display);

        // Set camera orientation.
        try {
            camera.setDisplayOrientation(rotate);
            if (settings != null) {
                settings.orientation = rotate;
            }
        } catch (Exception ex) {
            Log.e(CameraCustomizer.class.getName(), "Can't setup camera display orientation.");
            ex.printStackTrace();
        }

        // Get preview size.
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> preview_sizes = params.getSupportedPreviewSizes();

        // Select best preview aspect.
        float bestAspect;
        if (rotate == 0 || rotate == 180) {
            bestAspect = (float) width / (float) height;
        } else {
            bestAspect = (float) height / (float) width;
        }
        Log.d(CameraCustomizer.class.getName(),
                String.format("Orientation: %d, Aspect: %f", rotate, bestAspect));

        // Select preview size.
        float bestScore = 0;
        Camera.Size best_preview_size = preview_sizes.get(0);
        for (Camera.Size s : preview_sizes) {
            float aspect = (float) s.width / (float) s.height;
            float aspectMiss = Math.abs(aspect - bestAspect) / Math.max(aspect, bestAspect);
            float size = (float) Math.sqrt(s.width * s.width + s.height * s.height);
            float score = (1.0f - aspectMiss) * size;

            Log.d(CameraCustomizer.class.getName(),
                    String.format("Preview score: [%d x %d] : %f", s.width, s.height, score));

            if (score > bestScore) {
                best_preview_size = s;
                bestScore = score;
            }
        }

        // Set preview parameters.
        try {
            params.setPreviewFormat(ImageFormat.NV21);
            params.setPreviewSize(best_preview_size.width, best_preview_size.height);
            camera.setParameters(params);
            Log.d(CameraCustomizer.class.getName(),
                    String.format("Best preview: %d x %d", best_preview_size.width, best_preview_size.height));
        } catch (Exception ex) {
            Log.e(CameraCustomizer.class.getName(), "Can't setup preview size.");
            ex.printStackTrace();
        }

        // Gets all available picture sizes.
        params = camera.getParameters();
        best_preview_size = params.getPreviewSize();
        List<Camera.Size> picture_sizes = params.getSupportedPictureSizes();

        // Select best picture size.
        bestAspect = (float) best_preview_size.width / (float) best_preview_size.height;
        Camera.Size best_picture_size = picture_sizes.get(0);
        for (Camera.Size s : picture_sizes) {
            float aspect = (float) s.width / (float) s.height;
            float aspectMiss = Math.abs(aspect - bestAspect) / Math.max(aspect, bestAspect);
            float size = (float) Math.sqrt(s.width * s.width + s.height * s.height);
            float score = (1.0f - aspectMiss) * size;

            Log.d(CameraCustomizer.class.getName(),
                    String.format("Picture score: [%d x %d] : %f", s.width, s.height, score));

            if (score > bestScore) {
                best_picture_size = s;
                bestScore = score;
            }
        }

        // Set image parameters.
        try {
            params.setPictureSize(best_picture_size.width, best_picture_size.height);
            camera.setParameters(params);
            Log.d(CameraCustomizer.class.getName(),
                    String.format("Best image: %d x %d", best_picture_size.width, best_picture_size.height));
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
    }

    private static int getCameraOrientation(Camera.CameraInfo cameraInfo, Display display) {
        //calculate camera rotation.
        int degrees = 0;
        switch (display.getRotation()) {
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
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotate = (cameraInfo.orientation + degrees) % 360;
            rotate = (360 - rotate) % 360;
        } else {
            rotate = (cameraInfo.orientation - degrees + 360) % 360;
        }

        return rotate;
    }
}
