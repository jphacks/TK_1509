package com.gaasii.eye_et_glass;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import com.gaasii.eye_et_grass.R;
import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sony.smarteyeglass.extension.util.CameraEvent;
import com.sony.smarteyeglass.extension.util.ControlCameraException;
import com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils;
import com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import android.net.http.AndroidHttpClient;


import AsyncHttpRequest.AsyncHttpRequest;
import httpposttask.HttpPostListener;
import httpposttask.HttpPostTask;


/**
 * Created by toshiyukiando on 2015/11/28.
 */
public final class Eye_etCameraControl extends ControlExtension implements HttpPostListener {

    private static final int SMARTEYEGLASS_API_VERSION = 3;
    public final int width;
    public final int height;


    /**
     * The application context.
     */
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

    //-----------------------------
    private int touchIvent = 1;

    final static private String TAG = "HttpPost";

    public Eye_etCameraControl(final Context context, final String hostAppPackageName) {
        super(context, hostAppPackageName);
        this.context = context;

        //final SmartEyeglassEventListener listener = new MySmartEyeglassEventListener();


        SmartEyeglassEventListener listener = new SmartEyeglassEventListener() {
            // When camera operation has succeeded
            // handle result according to current recording mode
            @Override
            public void onCameraReceived(final CameraEvent event) {
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

        saveFolder = new File(Environment.getExternalStorageDirectory(), "SampleCameraExtension");
        saveFolder.mkdir();

        width = context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_control_width);
        height = context.getResources().getDimensionPixelSize(R.dimen.smarteyeglass_control_height);


    }


    @Override
    public void onTouch(final ControlTouchEvent event) {

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

                utils.requestCameraCapture();
                //touchIvent = 1;

            }
            //touchIvent = 1;
            /*
            else {
                if (!cameraStarted) {
                    initializeCamera();
                } else {
                    cleanupCamera();
                }
                //updateDisplay();
            }
            */
        }

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
    public void onResume() {
        // Note: Setting the screen to be always on will drain the accessory
        // battery. It is done here solely for demonstration purposes.
        setScreenState(Control.Intents.SCREEN_STATE_ON);
        pointX = context.getResources().getInteger(R.integer.POINT_X);
        pointY = context.getResources().getInteger(R.integer.POINT_Y);

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

    // Clean up any open files and reset mode when app is paused.
    @Override
    public void onPause() {
        // Stop camera.
        if (cameraStarted) {
            Log.d(Constants.LOG_TAG, "onPause() : stopCamera");
            cleanupCamera();
        }
    }

    // Clean up data structures on termination.
    @Override
    public void onDestroy() {
        utils.deactivate();
    }

    /**
     * Received camera event and operation each event.
     *
     * @param event
     */
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
            String fileName = saveFilePrefix + String.format("%04d", saveFileIndex);
            //HttpPostTask task = new HttpPostTask("http://amicry.com/img/jphacks.php");
            //HttpPostTask task = new HttpPostTask("http://gaasii.com/Anyfiles/imgupload/upload_jphacks.php");
            //HttpPostTask task = new HttpPostTask("http://yuji.website:3002/api/v1/meals");
            //task.addText("param1", fileName);//画像ファイル名
            //task.addText("user_id", "1");
            //task.addImage("image.jpg", data);//画像データ
            //task.addImage("image", data);

            //byte[] encode = Base64.encode(data, Base64.DEFAULT);

            //task.addImage("image", encode);
            //String a = new String(data, Charset.forName("UTF-8"));
            //task.addText("image", new String(data, Charset.forName("UTF-8")));


            /*
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] _bArray = bos.toByteArray();
            String image64 = Base64.encodeToString(_bArray, Base64.DEFAULT);
            String _imageBinary = "data:image/jpeg;base64," + image64;
            task.addText("image", _imageBinary);

            Log.d("IMAGE:::::::", _imageBinary);
*/
/*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 大阪の天気予報XMLデータ
                        URL url = new URL("http://yuji.website:3002/api/v1/meals");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        String str = InputStreamToString(con.getInputStream());
                        Log.d("HTTP", str);
                    } catch(Exception ex) {
                        System.out.println(ex);
                    }
                }
            }).start();

*/
            final String str = "";
            //String str = postMultipart("http://yuji.website:3002/api/v1/meals", data, "image");
            AsyncHttpRequest post = new AsyncHttpRequest();
            post.setImage(data);
            post.execute(data);

            Log.d("response::::", str);
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

    /*

    private void updateDisplay()
    {
        Bitmap displayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        displayBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        Canvas canvas = new Canvas(displayBitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(16);
        paint.setColor(Color.WHITE);
        // Update layout according to the camera mode
        switch (recordingMode) {
            case SmartEyeglassControl.Intents.CAMERA_MODE_STILL:
                canvas.drawText("Tap to capture : STILL", pointX, pointY, paint);
                break;
            case SmartEyeglassControl.Intents.CAMERA_MODE_STILL_TO_FILE:
                canvas.drawText("Tap to capture : STILL TO FILE", pointX, pointY, paint);
                break;
            case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_HIGH_RATE:
            case SmartEyeglassControl.Intents.CAMERA_MODE_JPG_STREAM_LOW_RATE:
                if (cameraStarted) {
                    canvas.drawText("JPEG Streaming...", pointBaseX, pointY, paint);
                    canvas.drawText("Tap to stop.", pointBaseX, (pointY * 2), paint);
                    canvas.drawText("Frame Number: " + Integer.toString(saveFileIndex), pointBaseX, (pointY * 3), paint);
                } else {
                    canvas.drawText("Tap to start JPEG Stream.", pointBaseX, pointY, paint);
                }
                break;
            default:
                canvas.drawText("wrong recording type.", pointBaseX, pointY, paint);
        }

        utils.showBitmap(displayBitmap);
    }

    */


    @Override
    public void postCompletion(byte[] response) {
        Log.i(TAG, "post completion!");
        Log.i(TAG, new String(response));
    }

    public void postFailure() {
        Log.i(TAG, "post failure");
    }


    public String postMultipart(String url, byte[] image, String params) {
        HttpClient client = new DefaultHttpClient();
        String str = "";

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entity.setCharset(Charset.forName("UTF-8"));
        try {
            // 画像をセット
            // 第一引数：パラメータ名
            // 第二引数：画像データ
            // 第三引数：画像のタイプ。jpegかpngかは自由
            // 第四引数：画像ファイル名。ホントはContentProvider経由とかで取って来るべきなんだろうけど、今回は見えない部分なのでパス
            entity.addBinaryBody("avater", image, ContentType.create("image/png"), "hoge.png");
            url = "http://example.com/image.json";

            // 画像以外のデータを送る場合はaddTextBodyを使う
            ContentType textContentType = ContentType.create("application/json","UTF-8");
            entity.addTextBody("auth_token", params, textContentType);

            HttpPost post = new HttpPost(url);
            post.setEntity(entity.build());

            HttpResponse httpResponse = client.execute(post);
            str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            Log.i("HTTP status Line", httpResponse.getStatusLine().toString());
            Log.i("HTTP response", new String(str));
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }
}



/*
class MySmartEyeglassEventListener extends SmartEyeglassEventListener {
    @Override
    public void onCameraReceived(CameraEvent event) {
        Log.d("NEW LISTENER", "onCameraReceived");
    }
    @Override
    public void onCameraErrorReceived(int error) {
    }
    @Override
    public void onCameraReceivedFile(String filePath) {
    }

}
*/