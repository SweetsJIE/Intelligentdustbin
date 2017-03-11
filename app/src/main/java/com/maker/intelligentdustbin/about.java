package com.maker.intelligentdustbin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by sweets on 17/2/18.
 */

public class About extends Activity {

    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        imageButton = (ImageButton) findViewById(R.id.BackButton1);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static void changeAboutPage(Context context) {
        Intent intent = new Intent(context, About.class);
        context.startActivity(intent);
    }

}
