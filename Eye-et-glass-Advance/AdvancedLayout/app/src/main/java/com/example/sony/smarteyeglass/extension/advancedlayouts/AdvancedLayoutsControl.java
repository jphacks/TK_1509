/*
Copyright (c) 2011, Sony Mobile Communications Inc.
Copyright (c) 2014, Sony Corporation

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Mobile Communications Inc.
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.sony.smarteyeglass.extension.advancedlayouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sony.smarteyeglass.extension.util.CameraEvent;
import com.sony.smarteyeglass.extension.util.ControlCameraException;
import com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils;
import com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.control.Control.Intents;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import AsyncHttpRequest.AsyncHttpRequest;

/**
 * AdvancedLayouts displays a swipe-able gallery, based on a string array.
 */
public final class AdvancedLayoutsControl extends ControlExtension {

    /**
     * String array with dummy data to be displayed in gallery.
     */
    private static final String[] GALLERY_CONTENT = {
            "calorie",
            "nutrition",
            "allergy"};
    /**
     * Dummy message.
     */
    private static final String DETAIL_MESSAGE = " %s ";
    private static int calorie_sum = 0;
    private static int calorie_this = 0;
    private static int carb_this = 0;
    private static int fat_this = 0;
    private static int mineral_this = 0;
    private static int protein_this = 0;
    private static int vitamin_this = 0;

    private static int calorie_per = 0;

    private static int nutrition_1 = 0;
    private static int nutrition_2 = 0;
    private static int nutrition_3 = 0;
    private static int nutrition_4 = 0;
    private static int nutrition_5 = 0;


    /**
     * Instance of the Control Utility class.
     */

    /**
     * Uses SmartEyeglass API version
     */
    private static final int SMARTEYEGLASS_API_VERSION = 1;
    /**
     * The position of the displayed item inside the array/database.
     */
    private int lastPosition = 0;
    /**
     * Flag for list view or detailed view.
     */
    private boolean showingDetail;

    /**
     * Creates Advanced Layout control.
     *
     * @param context            The application context.
     * @param hostAppPackageName The package name;
     */
    //---------------------------元CameraControl--------------------------------//
    private final Context context;
    /**
     * Instance of the Control Utility class.
     */
    private final SmartEyeglassControlUtils utils;


    private boolean saveToSdcard = false;
    private boolean saveToServer = false;
    private boolean cameraStarted = false;
    private int saveFileIndex;
    private int saveFileIndex2;
    private int recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_STILL;
    //private int recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE;
    private String saveFilePrefix;
    private File saveFolder;
    private int pointX;
    private int pointY;
    private int pointBaseX;
    public final int width;
    public final int height;

    int state_num = 0;

    //-----------------------------
    private int touchIvent = 1;

    String json_str;

    private String json_data;

    final static private String TAG = "HttpPost";

    State state = new State();


    public AdvancedLayoutsControl(final Context context,
                                  final String hostAppPackageName) {
        super(context, hostAppPackageName);
        this.context = context;



        SmartEyeglassEventListener listener = new SmartEyeglassEventListener() {
            // When camera operation has succeeded
            // handle result according to current recording mode
            @Override
            public void onCameraReceived(final CameraEvent event) {
                Log.d("CAMERA_RECIEVE", "getcmara");

                switch (recordingMode) {
                    case SmartEyeglassControl.Intents.CAMERA_MODE_STILL:
                        Log.d(Constants.LOG_TAG, "Camera Event coming: " + event.toString());
                        break;
                    case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE:
                        Log.d(Constants.LOG_TAG, "Stream Event coming: " + event.toString());
                    case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE:
                        Log.d(Constants.LOG_TAG, "Stream Event coming: " + event.toString());
                        break;
                    default:
                        break;
                }
                if (touchIvent == 0) {
                    cameraEventOperation(event);
                    touchIvent = 1;
                    state_num = 1;
                }
            }

            // Called when camera operation has failed
            // We just log the error
            @Override
            public void onCameraErrorReceived(final int error) {
                Log.d(Constants.LOG_TAG, "onCameraErrorReceived: " + error);
            }

            // When camera is set to record image to a file,
            // log the operation and clean up
            @Override
            public void onCameraReceivedFile(final String filePath) {
                Log.d(Constants.LOG_TAG, "onCameraReceivedFile: " + filePath);
                //updateDisplay();
            }
        };



        utils = new SmartEyeglassControlUtils(hostAppPackageName, listener);
        utils.setRequiredApiVersion(SMARTEYEGLASS_API_VERSION);
        utils.activate(context);
        utils.activate(context);

        saveFolder = new File(Environment.getExternalStorageDirectory(), "SampleCameraExtension");
        saveFolder.mkdir();

        width = context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_control_width);
        height = context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_control_height);
    }


    private void initializeCamera() {
        try {
            Time now = new Time();
            now.setToNow();
            // Start camera with filepath if recording mode is Still to file
            if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
                String filePath = saveFolder + "/" + saveFilePrefix + String.format("%04d", saveFileIndex) + ".jpg";
                saveFileIndex++;
                utils.startCamera(filePath);
            } else {
                // Start camera without filepath for other recording modes
                Log.d(Constants.LOG_TAG, "startCamera ");
                utils.startCamera();
            }
        } catch (ControlCameraException e) {
            Log.d(Constants.LOG_TAG, "Failed to register listener", e);
        }
        Log.d(Constants.LOG_TAG, "onResume: Registered listener");

        cameraStarted = true;
    }

    private void cleanupCamera() {
        utils.stopCamera();
        cameraStarted = false;
    }


    @Override
    public void onPause() {
        // Stop camera.
        if (cameraStarted) {
            Log.d(Constants.LOG_TAG, "onPause() : stopCamera");
            cleanupCamera();
        }
    }

    @Override
    public void onDestroy() {
        utils.deactivate();
    }

    @Override
    public void onResume() {
        int startPosition = 0;
        showingDetail = false;

        // Set the layout.
        showLayout(R.layout.smarteyeglass_layout_test_gallery, null);
        // Layout sets the list.
        sendListCount(R.id.gallery, GALLERY_CONTENT.length);
        // If requested, move to the correct position in the list.
        lastPosition = startPosition;
        // Set location of the beginning.
        sendListPosition(R.id.gallery, startPosition);
        // For scrollable text view.
        utils.sendTextViewLayoutId(R.id.calory);

        //---------------Camera-----------------------//


        // Note: Setting the screen to be always on will drain the accessory
        // battery. It is done here solely for demonstration purposes.
        setScreenState(Control.Intents.SCREEN_STATE_ON);
        pointX = context.getResources().getInteger(R.integer.POINT_X);
        pointY = context.getResources().getInteger(R.integer.POINT_Y);


        //showLayout(R.layout.camera_textview, null);
        //utils.sendTextViewLayoutId(R.id.calory);


        Time now = new Time();
        now.setToNow();
        saveFilePrefix = "samplecamera_" + now.format2445() + "_";
        saveFileIndex = 0;


        // Read the settings for the extension.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //saveToSdcard = prefs.getBoolean(context.getString(R.string.preference_key_save_to_sdcard), true);
        saveToSdcard = prefs.getBoolean(context.getString(R.string.preference_key_save_to_sdcard), false);
        int recMode = Integer.parseInt(prefs.getString(context.getString(R.string.preference_key_recordmode), "2"));
        int preferenceId = R.string.preference_key_resolution_still;


        /*
        switch (recMode) {
            case 0: // recording mode is still
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_STILL;
                break;
            case 1: // recording mode is still to file
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE;
                break;
            case 2: // recording mode is JPGStream Low
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE;
                preferenceId = R.string.preference_key_resolution_movie;
                break;
            case 3: // recording mode is JPGStream High
                recordingMode = SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE;
                preferenceId = R.string.preference_key_resolution_movie;
                break;
        }
        */

        // Get and show quality parameters
        int jpegQuality = Integer.parseInt(prefs.getString(
                context.getString(R.string.preference_key_jpeg_quality), "1"));
        int resolution = Integer.parseInt(prefs.getString(
                context.getString(preferenceId), "6"));

        // Set the camera mode to match the setup
        utils.setCameraMode(jpegQuality, resolution, recordingMode);

        cameraStarted = false;
        //updateDisplay();
        initializeCamera();

    }

    @Override
    public void onRequestListItem(
            final int layoutReference, final int listItemPosition) {
        Log.d(Constants.LOG_TAG,
                "onRequestListItem() - position " + listItemPosition);
        if (layoutReference == -1 || layoutReference != R.id.gallery) {
            return;
        }
        if (listItemPosition == -1) {
            return;
        }
        ControlListItem item = createControlListItem(listItemPosition);
        if (item == null) {
            return;
        }
        sendListItem(item);
    }

    @Override
    public void onListItemSelected(final ControlListItem listItem) {
        super.onListItemSelected(listItem);
        // We save the last "selected" position, this is the current visible
        // list item index. The position can later be used on resume
        lastPosition = listItem.listItemPosition;
    }

    @Override
    public void onListItemClick(
            final ControlListItem listItem,
            final int clickType,
            final int itemLayoutReference) {
        Log.d(Constants.LOG_TAG,
                "Item clicked. Position " + listItem.listItemPosition
                        + ", itemLayoutReference " + itemLayoutReference
                        + ". Type was: "
                        + (clickType == Intents.CLICK_TYPE_SHORT ? "SHORT" : "LONG"));
        lastPosition = listItem.listItemPosition;

        if (clickType == Intents.CLICK_TYPE_LONG) {
            return;
        }

        // Dummy message.
        Bundle messageBundle = new Bundle();
        messageBundle.putInt(Intents.EXTRA_LAYOUT_REFERENCE, R.id.gallery);
        messageBundle.putString(Intents.EXTRA_TEXT,
                "No:" + Integer.toString(listItem.listItemPosition + 1));

        List<Bundle> list = Collections.singletonList(messageBundle);
        utils.moveLowerLayer(R.layout.smarteyeglass_layout_test_detail,
                list.toArray(new Bundle[list.size()]));
        showingDetail = true;
    }

    @Override
    public void onKey(
            final int action, final int keyCode, final long timeStamp) {

        if (state_num == 1){
            calorie_sum += calorie_this;
            /*
            nutrition_1 += fat_this;
            nutrition_2 += carb_this;
            nutrition_3 += protein_this;
            nutrition_4 += vitamin_this;
            nutrition_5 += mineral_this;
            */
            state_num = 0;
        }else{
            state_num = 0;
        }

        //Log.d("KEYCODE", keyCode);

        if (action != Intents.KEY_ACTION_RELEASE){
            return;
        }


        if (action != Intents.KEY_ACTION_RELEASE
                || keyCode != Control.KeyCodes.KEYCODE_BACK) {
            Log.d("onKey", "KEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            return;
        }



        Log.d(Constants.LOG_TAG, "onKey() - back button intercepted.");
        if (!showingDetail) {
            stopRequest();
            return;
        }
        state_num = 0;
        showingDetail = false;
        ControlListItem item = createControlListItem(lastPosition);
        utils.moveUpperLayer(item.dataXmlLayout, item.layoutData);
        showLayout(R.layout.smarteyeglass_layout_test_gallery, null);
        sendListCount(R.id.gallery, GALLERY_CONTENT.length);
        sendListPosition(R.id.gallery, lastPosition);
        utils.sendTextViewLayoutId(R.id.calory);
        sendListItem(item);
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
        if (state_num == 1){
            /*
            calorie_sum += calorie_this;
            nutrition_1 += fat_this;
            nutrition_2 += carb_this;
            nutrition_3 += protein_this;
            nutrition_4 += vitamin_this;
            nutrition_5 += mineral_this;
            */
            state_num = 0;
        }else{
            state_num = 0;
        }

        if (event.getAction() != Control.TapActions.SINGLE_TAP){
            return;
        }
        if (event.getAction() == Control.TapActions.SINGLE_TAP) {
            Log.d("onTouch Ivent !!!!!!", "touchIvent" + touchIvent);
            touchIvent = 0;
            Log.d("onTouch Ivent !!!!!!", "touchIvent" + touchIvent);

            if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL ||
                    recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE) {
                if (!cameraStarted) {
                    initializeCamera();
                }
                Log.d(Constants.LOG_TAG, "Select button pressed -> cameraCapture()");

                Log.d("requestCameraCapture", "requestNow");
                utils.requestCameraCapture();

            }
        }

    }


    @Override
    public void onTap(final int action, final long timeStamp) {
        if (action != Control.TapActions.SINGLE_TAP) {
            return;
        }
        Log.d(Constants.LOG_TAG, "tapactions:" + action);
        if (!showingDetail) {
            return;
        }
        // Show detail view
        utils.moveLowerLayer(R.layout.smarteyeglass_layout_test_gallery, null);
        sendListCount(R.id.gallery, GALLERY_CONTENT.length);
        // If requested, move to the correct position in the list.
        sendListPosition(R.id.gallery, lastPosition);
        // For scrollable text view
        utils.sendTextViewLayoutId(R.id.calory);

        showingDetail = false;
    }

    /**
     * Creates {@code ControlListItem} object for the specified position.
     *
     * @param position The position.
     * @return The {@code ControlListItem} object.
     */
    private ControlListItem createControlListItem(final int position) {
        Log.d(Constants.LOG_TAG, "position = " + position);

        // Creates the repeating dummy text.
        /*String text = String.format(DETAIL_MESSAGE, GALLERY_CONTENT[position]);
        StringBuilder b = new StringBuilder(text);
        for (int i = 0; i < position; ++i) {
            b.append(b);
        }*/
        if (position == 0) {
            ControlListItem item = new ControlListItem();
            item.layoutReference = R.id.gallery;
            item.dataXmlLayout = R.layout.smarteyeglass_item_gallery;
            item.listItemId = position;
            item.listItemPosition = position;

            List<Bundle> list = new ArrayList<Bundle>();

            // Body data.
            Bundle calBundle = new Bundle();
            calBundle.putInt(Intents.EXTRA_LAYOUT_REFERENCE, R.id.calory);
            calBundle.putString(Intents.EXTRA_TEXT, String.valueOf(calorie_this));
            list.add(calBundle);

            Bundle perBundle = new Bundle();
            perBundle.putInt(Intents.EXTRA_LAYOUT_REFERENCE, R.id.per);
            if(calorie_per>=100) {
                perBundle.putString(Intents.EXTRA_TEXT, String.valueOf(calorie_per - 100));
            }else{
                perBundle.putString(Intents.EXTRA_TEXT, String.valueOf(calorie_per));
            }
            list.add(perBundle);

            if(calorie_per>=100){
                Bundle imageBundle = new Bundle();
                imageBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView2);
                imageBundle.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getCaloriesImageId(1)));
                list.add(imageBundle);
            }

            // Prepare a bundle to update the ImageView for the button icon.
            Bundle imageBundle2 = new Bundle();
            imageBundle2.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView3);
            imageBundle2.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getCalorieImageId(calorie_per)));
            list.add(imageBundle2);

            // Display the view elements from layout.xml
            //showLayout(R.layout.smarteyeglass_item_gallery, list.toArray(new Bundle[list.size()]));

            item.layoutData = list.toArray(new Bundle[list.size()]);
            return item;
        } else if (position == 1) {
            ControlListItem item = new ControlListItem();
            item.layoutReference = R.id.gallery;
            item.dataXmlLayout = R.layout.smarteyeglass_item_gallery1;
            item.listItemId = position;
            item.listItemPosition = position;

            List<Bundle> list = new ArrayList<Bundle>();

            Bundle imageBundle1 = new Bundle();
            imageBundle1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView1);
            imageBundle1.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getNutrition1ImageId(nutrition_1)));
            list.add(imageBundle1);

            Bundle imageBundle2 = new Bundle();
            imageBundle2.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView2);
            imageBundle2.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getNutrition2ImageId(nutrition_2)));
            list.add(imageBundle2);

            Bundle imageBundle3 = new Bundle();
            imageBundle3.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView3);
            imageBundle3.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getNutrition3ImageId(nutrition_3)));
            list.add(imageBundle3);

            Bundle imageBundle4 = new Bundle();
            imageBundle4.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView4);
            imageBundle4.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getNutrition4ImageId(nutrition_4)));
            list.add(imageBundle4);

            Bundle imageBundle5 = new Bundle();
            imageBundle5.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView5);
            imageBundle5.putString(Control.Intents.EXTRA_DATA_URI, getUriString(state.getNutrition5ImageId(nutrition_5)));
            list.add(imageBundle5);

            item.layoutData = list.toArray(new Bundle[list.size()]);
            return item;
        } else {
            ControlListItem item = new ControlListItem();
            item.layoutReference = R.id.gallery;
            item.dataXmlLayout = R.layout.smarteyeglass_item_gallery2;
            item.listItemId = position;
            item.listItemPosition = position;

            List<Bundle> list = new ArrayList<Bundle>();

            item.layoutData = list.toArray(new Bundle[list.size()]);
            return item;
        }
    }



    //no
    private void cameraEventOperation(CameraEvent event) {
        if (event.getErrorStatus() != 0) {
            Log.d(Constants.LOG_TAG, "error code = " + event.getErrorStatus());
            return;
        }

        if (event.getIndex() != 0) {
            Log.d(Constants.LOG_TAG, "not oparate this event");
            return;
        }

        Bitmap bitmap = null;
        byte[] data = null;

        if ((event.getData() != null) && ((event.getData().length) > 0)) {
            data = event.getData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        if (bitmap == null) {
            Log.d(Constants.LOG_TAG, "bitmap == null");
            return;
        }

        saveToSdcard = true;
        if (saveToSdcard == true) {
            String fileName = saveFilePrefix + String.format("%04d", saveFileIndex) + ".jpg";
            new SavePhotoTask(saveFolder, fileName).execute(data);
            saveFileIndex++;
        }

        try {
            // sdcardフォルダを指定
            String SDFile = android.os.Environment.getExternalStorageDirectory().getPath();
//                    + "/Android/data/"+getPackageName();
            File root = new File(SDFile);

            // 保存処理開始
            FileOutputStream fos = null;
            fos = new FileOutputStream(new File(root, "a.jpg"));

            // jpegで保存
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 保存処理終了
            fos.close();
        } catch (Exception e) {
            Log.e("Error", "" + e.toString());
        }


        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/dir/");
        if (dir.exists()) {
            File file = new File(dir.getAbsolutePath() + "/test.jpg");
            if (file.exists()) {
                Bitmap _bm = BitmapFactory.decodeFile(file.getPath());

            } else {
                //存在しない
            }
        }

        //------------------Server upload-------------------------//
        saveToServer = true;
        if (saveToServer == true) {

            AsyncHttpRequest post = new AsyncHttpRequest(new AsyncHttpRequest.AsyncCallback() {
                public void onPreExecute() {
                    // do something
                }

                public void onPostExecute(String[] result) {
                    // do something
                    // result にjsonが入ってる。
                    try {
                        JSONObject json = new JSONObject(result[0]);

                        JSONArray datas = json.getJSONArray("data");

                        JSONObject data = datas.getJSONObject(0);
                        String Item_name = data.getString("itemName");
                        //--------------------------------------------
                        JSONObject Nutition = data.getJSONObject("nutrition");
                        calorie_this = Nutition.getInt("calorie");
                        carb_this = Nutition.getInt("carb");
                        fat_this = Nutition.getInt("fat");
                        mineral_this = Nutition.getInt("mineral");
                        protein_this = Nutition.getInt("protain");
                        vitamin_this = Nutition.getInt("vitamin");


                        //String data1 = json.getString("data");


                        json_data = Item_name;
                        //Log.d("json_data", data1 + )

                        ControlListItem item = createControlListItem(pointBaseX);
                        //showLayout(R.layout.camera_textview, null);
                        //sendListCount(R.id.gallery, GALLERY_CONTENT.length);
                        //sendListPosition(R.id.gallery, lastPosition);
                        utils.sendTextViewLayoutId(R.id.calory);
                        sendListItem(item);
                    }catch (JSONException e) {
                    }
                    try {
                        JSONObject json2 = new JSONObject(result[1]);

                        //Log.d("sssssssssss",  result[1]);
                        JSONObject data2 = json2.getJSONObject("data");
                        //JSONArray data2s = json2.getJSONArray("data");
                        //JSONObject data2 = data2s.getJSONObject(0);
                        calorie_per = data2.getInt("rate_cal");
                        nutrition_1 = data2.getInt("rate_fat");
                        nutrition_2 = data2.getInt("rate_carb");
                        nutrition_3 = data2.getInt("rate_protain");
                        calorie_sum = data2.getInt("intake_cal");
                        //nutrition_4 = data2.getInt("rate_")

                        Log.d("Calorie per", "" + calorie_per);
                    }catch (JSONException e) {
                        Log.e("JSONPERSEEROORO", e.toString());
                    }
                    //Log.d("onPostExecute", result);


                }

                public void onCancelled() {
                    // do something
                }
            });
            post.setImage(data);
            post.execute(data);
            //   json_str = post.getJson_string();

            //Log.d("response::::", this.json_str);
            //task.setListener(Eye_etCameraControl.this);
            //task.execute();
            saveFileIndex2++;



        }


        if (recordingMode == SmartEyeglassControl.Intents.CAMERA_MODE_STILL) {
            Bitmap basebitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            basebitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            Canvas canvas = new Canvas(basebitmap);
            Rect rect = new Rect(0, 0, width, height);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            canvas.drawBitmap(bitmap, rect, rect, paint);

            utils.showBitmap(basebitmap);
            return;
        }

        Log.d(Constants.LOG_TAG, "Camera frame was received : #" + saveFileIndex);
        //updateDisplay();
    }

    private String getUriString(final int id) {
        return ExtensionUtils.getUriString(context, id);
    }

}
