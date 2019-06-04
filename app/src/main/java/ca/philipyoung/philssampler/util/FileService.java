package ca.philipyoung.philssampler.util;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Derek Banas.
 */

public class FileService extends IntentService {

    public static final String TRANSACTION_DONE = "ca.philipyoung.TRANSACTION_DONE";
    public static final String MAP_DONE = "ca.philipyoung.MAP_DONE";
    public static final String ADDRESS_DONE = "ca.philipyoung.ADDRESS_DONE";
    private static final String TAG = "FileService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FileService() {
        super(FileService.class.getName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FileService(String name) {
        super(name);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     *               This may be null if the service is being restarted after
     *               its process has gone away; see
     *               {@link Service#onStartCommand}
     *               for details.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {

            Log.d(TAG, "Service Started");

            String passedURL = null;
            if (intent != null) {
                passedURL = intent.getStringExtra("url");
            }

            downloadFile(passedURL,intent);

            Log.d(TAG, "Service Stopped");

            Intent i = new Intent(TRANSACTION_DONE);
            FileService.this.sendBroadcast(i);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onHandleIntent: "+ e.getLocalizedMessage());
        }
    }

    protected void downloadFile(String theURL,Intent intent) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()),
                fileName =  intent.getStringExtra("file");
        try {
            FileOutputStream outputStream =
                    openFileOutput(fileName, Context.MODE_PRIVATE);
            URL fileURL = new URL(theURL);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) fileURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength;

            while ((bufferLength=inputStream.read(buffer))>0) {
                outputStream.write(buffer, 0, bufferLength);
            }
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadFile: "+ e.getLocalizedMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadFile: "+ e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadFile: "+ e.getLocalizedMessage());
        }
    }
}