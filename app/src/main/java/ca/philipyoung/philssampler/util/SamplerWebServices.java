package ca.philipyoung.philssampler.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import ca.philipyoung.philssampler.R;

public class SamplerWebServices {
    private static final String TAG = "SamplerWebServices";
    private Context mContext;

    public SamplerWebServices(Context context) {
        this.mContext = context;
    }

    public Boolean getInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        Toast.makeText(mContext,mContext.getString(R.string.ws_no_internet),Toast.LENGTH_SHORT).show();
        return false;
    }
}
