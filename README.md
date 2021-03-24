# ScanPdf417 Library

## Setup

1. Add **idscan-public** maven repository to the **project** ```build.gradle``` file.
```
allprojects {
    repositories {
        ...
        maven {
            url 'https://www.myget.org/F/idscan-public/maven/'
        }
        ...
    }
}
```
2. Add the following to the **module** ```build.gradle``` file:
```
dependencies {
    ...
    implementation 'net.idscan.components.android:scanpdf417:4.1.0'
    ...
}
```

**Note** if you have already installed version before 3.0.0 you have to remove it from the project.

## Using

For scanning you need to call ```PDF417ScanActivity```:

```
Intent i = new Intent(MainActivity.this, PDF417ScanActivity.class);
i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, "** LICENSE KEY **");
startActivityForResult(i, SCAN_ACTIVITY_CODE);
```
**Note** need to replace ```** LICENSE KEY **``` by your **License Key**.

To process the result you need to override ```onActivityResult()``` of your Activity.

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);

  if (requestCode == SCAN_ACTIVITY_CODE) {
    switch (resultCode) {
      case PDF417ScanActivity.RESULT_OK:
        if (data != null) {
          PDF417Data result =
                  data.getParcelableExtra(PDF417ScanActivity.DOCUMENT_DATA);
          if (result != null) {
            // TODO: Handle the data.
          }
        }
        break;

    case PDF417ScanActivity.ERROR_RECOGNITION:
      if (data != null) {
        String desc = data.getStringExtra(MrzScanActivity.ERROR_DESCRIPTION);
        // TODO: Handle the error.
      }
      break;

    case PDF417ScanActivity.ERROR_INVALID_CAMERA_NUMBER:
    case PDF417ScanActivity.ERROR_CAMERA_NOT_AVAILABLE:
    case PDF417ScanActivity.ERROR_INVALID_CAMERA_ACCESS:
    case PDF417ScanActivity.RESULT_CANCELED:
      // TODO: Handle the error.
      break;
    }
  }
}
```

#### Error codes:
* ```ERROR_RECOGNITION``` **License Key** invalid or expired or any internal error.

* ```ERROR_CAMERA_NOT_AVAILABLE``` device has no camera.

* ```ERROR_INVALID_CAMERA_NUMBER``` invalid camera number is selected.

* ```ERROR_INVALID_CAMERA_ACCESS``` application cannot access the camera. For example, camera can be captured by the other application or application has no permission to use the camera.

## Customization

For customization **scanning activity** you need to extend PDF417ScanActivity and override some methods.

#### Custom Viewfinder

The **scanning activity** has the following structure:
![Viewfinder structure](/images/scan_view_structure.png)

By default, **Viewfinder** layer is a simple view with a frame. You can replace it with a custom view. For that you need to override ```getViewFinder(LayoutInflater inflater)``` method. Also, you can add any views to **Viewfinder** layer.
```
@Override
protected View getViewFinder(LayoutInflater inflater) {
  View v = inflater.inflate(R.layout.custom_viewfinder, null);

  // TODO: setup view.

  return v;
}
```

**Note** **Viewfinder** layer is drawn as an overlay above the **camera preview** layer, so it should has a transparent background color.

#### Select camera

You have two ways to select active camera in the **scanning activity**:

1. You can override ```selectCamera(int number_of_cameras)``` method and return the number of desired camera.
```
@Override
protected int selectCamera(int number_of_cameras) {
    // TODO: Return number of camera in range [0, number_of_cameras).
}
```
2. You can call ```setCamera(int id)``` method to change the current active camera.

#### Setup camera (Removed in v4.0.0)

You can setup camera settings by overriding ```customizeCamera(Camera camera, int camera_id)``` method. This method gets selected camera as input parameter and returns an implementation of ```ICameraCustomizer``` interface. This method is invoked every time when activity is recreated or current camera is changed.

```onCameraSetup(Camera camera, int format, int width, int height, CameraSettings settings)``` method of ```ICameraCustomizer``` interface is invoked every time when preview layout is changed. You can find reference implementation of ```ICameraCustomizer``` at ```CameraCustomizer.java```.


#### Handle scanned data

By default, when barcode is recognized it returns via ```onActivityResult``` method. But you can change this behavior by overriding ```onData(PDF417Data result)``` method. That is default implementation of this method:
```
protected void onData(@NonNull PDF417Data result) {
  finish(result);
}
```
But you can process scanned data in a different way. For example, you can display barcode on **Viewfiender** layer. Also you don't have to return the result immediately. Instead of, you can return scanned data at any time in future by calling ```void finish(PDF417Data result)``` method.

#### Flashlight

Since v2.0.6 version, you have two ways to control flashlight:

1. You can call **scanning activity** with ```EXTRA_FLASH_STATE``` parameter with ```true``` value and if flashlight is available it will be turned on.
```
Intent i = new Intent(MainActivity.this, PDF417ScanActivity.class);
i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, LIC_KEY);
i.putExtra(PDF417ScanActivity.EXTRA_FLASH_STATE, true);
startActivityForResult(i, SCAN_ACTIVITY_CODE);
```
2. You can change state of the flashlight by calling ```setFlashState(booelan state)```.
```
public class CustomScanActivity extends PDF417ScanActivity {
    ...
    void switchFlashlight() {
        setFlashState(!getFlashState());
    }
    ...
}
```
