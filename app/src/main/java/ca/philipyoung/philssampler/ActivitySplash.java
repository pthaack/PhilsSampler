package ca.philipyoung.philssampler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivitySplash extends AppCompatActivity {
    private static final String TAG = "ActivitySplash";
    private static final Long lngDelay = 1500L;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_splash);

        launchDashboard();
    }

    private void launchDashboard() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, ActivityDashboard.class);
                startActivity(intent);
                finish();
            }
        }, lngDelay);
    }
}
