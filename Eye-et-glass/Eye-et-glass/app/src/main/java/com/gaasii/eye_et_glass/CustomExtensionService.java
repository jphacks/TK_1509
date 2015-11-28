package com.gaasii.eye_et_glass;

/**
 * Created by toshiyukiando on 2015/11/28.
 */

import android.content.Intent;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.DisplayInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationAdapter;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.util.List;



public final class CustomExtensionService extends ExtensionService {

    /** */
    public static CustomControl SmartEyeglassControl;
    /** */
    public static CustomExtensionService Object;

    /** */
    private static String Message = null;

    /** Creates a new instance. */
    public CustomExtensionService() {
        super(Constants.EXTENSION_KEY);
        Object = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.LOG_TAG, "onCreate: CustomExtensionService");
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new CustomRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    /**
     * Sets message to be shown when the SmartEyeglass app is ready.
     * Starts the app if it has not already started.
     */
    public void sendMessageToExtension(final String message) {
        Message = message;
        if (SmartEyeglassControl == null) {
            startSmartEyeglassExtension();
        } else {
            SmartEyeglassControl.requestExtensionStart();
        }
    }

    /**
     * You can use this method to kickstart your extension on SmartEyeglass
     * Host App.
     * This is useful if the user has not started your extension
     * since the SmartEyeglass was turned on.
     */
    public void startSmartEyeglassExtension() {
        Intent intent = new Intent(Control.Intents
                .CONTROL_START_REQUEST_INTENT);
        ExtensionUtils.sendToHostApp(getApplicationContext(),
                "com.sony.smarteyeglass", intent);
    }

    /**
     * Sends a  message to be shown in Android activity
     */
    public void sendMessageToActivity(final String message) {
        Intent intent = new Intent();
        intent.setClass(getBaseContext(), HelloWorldActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Message", message);
        startActivity(intent);
    }

    /**
     * Creates ControlExtension object for the accessory.
     * This creates the HelloWorldControl object after verifying
     * that the connected accessory is a SmartEyeglass.
     */
    @Override
    public ControlExtension createControlExtension(
            final String hostAppPackageName) {
        ScreenSize size = new ScreenSize(this);
        final int width = size.getWidth();
        final int height = size.getHeight();
        List<DeviceInfo> list = RegistrationAdapter.getHostApplication(
                this, hostAppPackageName).getDevices();
        for (DeviceInfo device : list) {
            for (DisplayInfo display : device.getDisplays()) {
                if (display.sizeEquals(width, height)) {
                    return new CustomControl(this,
                            hostAppPackageName, Message);
                }
            }
        }
        throw new IllegalArgumentException("No control for: "
                + hostAppPackageName);
    }
}
