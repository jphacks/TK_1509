package com.gaasii.eye_et_glass;

/**
 * Created by toshiyukiando on 2015/11/28.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gaasii.eye_et_grass.R;
import com.sonyericsson.extras.liveware.aef.registration.Registration;

/**
 * The Hello World activity provides a button on the phone that starts
 * the  SmartEyeglass app.
 *
 * For demonstration, this also displays messages sent in the intent.
 */
public final class HelloWorldActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonelayout);

        // When button is clicked, run the SmartEyeglass app
        Button btnGlass = (Button) findViewById(R.id.btnglass);
        btnGlass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                startExtension();
            }
        });

        /*
         * Check if activity was started with a message in the intent
         * If there is a message, show it as a Toast message
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString("Message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                    .show();
        }

        /*
         * Make sure ExtensionService of your SmartEyeglass app has already
         * started.
         * This is normally started automatically when user enters your app
         * on SmartEyeglass, although you can initialize it early using
         * request intent.
         */
        if (CustomExtensionService.Object == null) {
            Intent intent = new Intent(Registration.Intents
                    .EXTENSION_REGISTER_REQUEST_INTENT);
            Context context = getApplicationContext();
            intent.setClass(context, CustomExtensionService.class);
            context.startService(intent);
        }
    }

    /**
     *  Start the app with the message "Hello SmartEyeglass"
     */
    public void startExtension() {
        // Check ExtensionService is ready and referenced
        if (CustomExtensionService.Object != null) {
            CustomExtensionService.Object
                    .sendMessageToExtension("Hello SmartEyeglass");
        }
    }
}
