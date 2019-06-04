package ca.philipyoung.philssampler;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ca.philipyoung.philssampler.ui.CanvasPrintDocument;
import ca.philipyoung.philssampler.util.AdvancedEncryptionSystemHelper;
import ca.philipyoung.philssampler.util.AlertReceiver;
import ca.philipyoung.philssampler.util.DataSamples;
import ca.philipyoung.philssampler.util.FileService;

public class ActivityDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ActivityDashboard";
    public static final String ROTATE_DOES = "rotate_does";
    private static final String ROTATE_TEXT_1 = "rotate_text_1";
    private static final String ROTATE_TEXT_2 = "rotate_text_2";
    private static final String ROTATE_TEXT_3 = "rotate_text_3";
    public static final String ROTATE_LIST = "rotate_list";
    public static final String ROTATE_GRID = "rotate_grid";
    public static final String ROTATE_PHOTO = "rotate_photo";
    public static final String ROTATE_IMAGE = "rotate_image";
    public static final String ROTATE_PRINT = "rotate_print";
    public static final String ROTATE_ENCRYPT = "rotate_encrypt";
    public static final String ROTATE_NOTIFY = "rotate_notify";
    public static final String ROTATE_API = "rotate_api";

    public static final String EXTRA_ACTIVITY = "extra_activity";

    // Returning from called activity
    public static final int RETURN_FROM_PERMISSION_LOCATION = 1;
    public static final int RETURN_FROM_PERMISSION_INTERNET = 2;
    public static final int RETURN_FROM_PERMISSION_STORAGE = 3;
    public static final int RETURN_FROM_PICK_IMAGE = 4;
    public static final int RETURN_FROM_CLICK_IMAGE = 5;
    public static final int RETURN_FROM_CLICK_IMAGE_HIRES = 6;

    private Context mContext;
    private byte[] bytsPhotoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ROTATE_DOES)) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                ViewGroup layout = findViewById(R.id.container);
                layout.removeAllViews();
                View view = inflater.inflate(R.layout.content_activity_rotate, layout);
                if (savedInstanceState.getBoolean(ROTATE_DOES, false)) {
                    view = findViewById(R.id.rotate_checkbox_text);
                    if (view instanceof CheckBox) ((CheckBox) view).setChecked(true);
                    if (savedInstanceState.containsKey(ROTATE_TEXT_1)) {
                        view = findViewById(R.id.rotate_text1);
                        if (view instanceof EditText)
                            ((EditText) view).setText(savedInstanceState.getString(ROTATE_TEXT_1, ""));
                    }
                    if (savedInstanceState.containsKey(ROTATE_TEXT_2)) {
                        view = findViewById(R.id.rotate_text2);
                        if (view instanceof EditText)
                            ((EditText) view).setText(savedInstanceState.getString(ROTATE_TEXT_2, ""));
                    }
                    if (savedInstanceState.containsKey(ROTATE_TEXT_3)) {
                        view = findViewById(R.id.rotate_text3);
                        if (view instanceof EditText)
                            ((EditText) view).setText(savedInstanceState.getString(ROTATE_TEXT_3, ""));
                    }
                }
            } else if (savedInstanceState.containsKey(ROTATE_LIST)) {
                showListView();
            } else if (savedInstanceState.containsKey(ROTATE_GRID)) {
                showGridView();
            } else if (savedInstanceState.containsKey(ROTATE_PHOTO)) {
                showPhotoView(savedInstanceState);
            } else if (savedInstanceState.containsKey(ROTATE_PRINT)) {
                showPrintView();
            } else if (savedInstanceState.containsKey(ROTATE_ENCRYPT)) {
                showEncryptView();
            } else if (savedInstanceState.containsKey(ROTATE_NOTIFY)) {
                showNotifyView();
            } else if (savedInstanceState.containsKey(ROTATE_API)) {
                showApiView();
            }
        } else if (getIntent().hasExtra(EXTRA_ACTIVITY)) {
            switch (getIntent().getStringExtra(EXTRA_ACTIVITY)) {
                case ROTATE_DOES:
                    showRotateView();
                    break;
                case ROTATE_LIST:
                    showListView();
                    break;
                case ROTATE_GRID:
                    showGridView();
                    break;
                case ROTATE_PHOTO:
                    showPhotoView();
                    break;
                case ROTATE_PRINT:
                    showPrintView();
                    break;
                case ROTATE_ENCRYPT:
                    showEncryptView();
                    break;
                case ROTATE_NOTIFY:
                    showNotifyView();
                    break;
                case ROTATE_API:
                    showApiView();
                    break;
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                    showNotifyView();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RETURN_FROM_CLICK_IMAGE) {
            ImageView view = findViewById(R.id.photo_element);
            if (resultCode == RESULT_OK) {
                unwrap_image(data, view);
            }
        }
        if ((requestCode == RETURN_FROM_PERMISSION_LOCATION ||
                requestCode == RETURN_FROM_PERMISSION_INTERNET ||
                requestCode == RETURN_FROM_PERMISSION_STORAGE) &&
                resultCode == RESULT_OK) {
            showApiView();
        }
    }

    @Override
    protected void onDestroy() {
        geoStopListener();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        View view = findViewById(R.id.rotate_checkbox_text);
        if (view instanceof CheckBox) {
            outState.putBoolean(ROTATE_DOES, ((CheckBox) view).isChecked());
            if (((CheckBox) view).isChecked()) {
                view = findViewById(R.id.rotate_text1);
                if (view instanceof EditText)
                    outState.putString(ROTATE_TEXT_1, ((EditText) view).getText().toString());
                view = findViewById(R.id.rotate_text2);
                if (view instanceof EditText)
                    outState.putString(ROTATE_TEXT_2, ((EditText) view).getText().toString());
                view = findViewById(R.id.rotate_text3);
                if (view instanceof EditText)
                    outState.putString(ROTATE_TEXT_3, ((EditText) view).getText().toString());
            }
        }
        view = findViewById(R.id.grid_items);
        if (view instanceof GridView) {
            outState.putBoolean(ROTATE_GRID, true);
        }
        view = findViewById(R.id.list_items);
        if (view instanceof ListView) {
            outState.putBoolean(ROTATE_LIST, true);
        }
        view = findViewById(R.id.photo_view);
        if (view instanceof LinearLayout) {
            outState.putBoolean(ROTATE_PHOTO, true);
            view = findViewById(R.id.photo_element);
            if (view instanceof ImageView) {
                outState.putByteArray(ROTATE_IMAGE, bytsPhotoImage);
            }
        }
        view = findViewById(R.id.print_view);
        if (view instanceof LinearLayout) {
            outState.putBoolean(ROTATE_PRINT, true);
        }
        view = findViewById(R.id.encrypt_view);
        if (view instanceof ScrollView) {
            outState.putBoolean(ROTATE_ENCRYPT, true);
        }
        view = findViewById(R.id.notify_view);
        if (view instanceof LinearLayout) {
            outState.putBoolean(ROTATE_NOTIFY, true);
        }
        view = findViewById(R.id.api_view);
        if (view instanceof LinearLayout) {
            outState.putBoolean(ROTATE_API, true);
        }
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_photo) {
            showPhotoView();

        } else if (id == R.id.nav_rotate) {
            showRotateView();

        } else if (id == R.id.nav_list) {
            switchListGridView(item);

        } else if (id == R.id.nav_print) {
            showPrintView();

        } else if (id == R.id.nav_api) {
            showApiView();

        } else if (id == R.id.nav_encryption) {
            showEncryptView();

        } else if (id == R.id.nav_notifications) {
            showNotifyView();

        } else if (id == R.id.nav_about) {
            showAboutView();

        } else if (id == R.id.nav_help) {
            showHelpView();

        } else if (id == R.id.nav_logoff) {
            if (item.getTitle().toString().equals(getString(R.string.nav_logoff))) {
                item.setTitle(R.string.nav_login);
                item.setIcon(R.drawable.ic_menu_sign_on);
            } else {
                item.setTitle(R.string.nav_logoff);
                item.setIcon(R.drawable.ic_menu_sign_off);
            }

        } else if (id == R.id.nav_exit) {
            cascadeExit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showPhotoView() {
        showPhotoView(null);
    }

    private void showPhotoView(Bundle savedInstanceState) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_photo, layout);
        ImageView imageView = view.findViewById(R.id.photo_element);
        if (imageView != null) {
            if (savedInstanceState != null && savedInstanceState.containsKey(ROTATE_IMAGE)) {
                bytsPhotoImage = savedInstanceState.getByteArray(ROTATE_IMAGE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = findViewById(R.id.photo_element);
                        if (imageView != null && bytsPhotoImage != null && bytsPhotoImage.length > 0) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytsPhotoImage, 0, bytsPhotoImage.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }, 50);
            } else {
                bytsPhotoImage = null;
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                     fetchPhoto();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, RETURN_FROM_CLICK_IMAGE);
                    }
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotifyView();
                }
            });
            fab.setImageResource(android.R.drawable.ic_popup_reminder);
        }

        setTitle(R.string.nav_photo);
    }

    private void showRotateView() {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_rotate, layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotifyView();
                }
            });
            fab.setImageResource(android.R.drawable.ic_popup_reminder);
        }
        setTitle(R.string.nav_rotate);
    }

    private void switchListGridView(MenuItem item) {
        DataSamples dataSamples = new DataSamples(mContext);
        DataSamples.DataRecord[] dataRecords = new DataSamples.DataRecord[2];
        if (item.getTitle().toString().equals(getString(R.string.nav_list))) {
            // User has selected to view List View
            showListView(item);
        } else {
            // User has selected to view Grid View
            showGridView(item);
        }
    }

    private void showGridView() {
        MenuItem item = ((NavigationView)findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_list);
        showGridView(item);
    }

    private void showGridView(MenuItem item) {
        if (item != null) {
            // Set the menu to toggle to Grid view
            item.setTitle(R.string.nav_list);
            item.setIcon(R.drawable.ic_menu_list);
        }
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_gridview, layout);
        SampleGridAdapter adapter = new SampleGridAdapter(mContext);
        GridView gridView = view.findViewById(R.id.grid_items);
        gridView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotifyView();
                }
            });
            fab.setImageResource(android.R.drawable.ic_popup_reminder);
        }
        setTitle(R.string.nav_grid);
    }

    private void showListView() {
        MenuItem item = ((NavigationView)findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_list);
        showListView(item);
    }

    private void showListView(MenuItem item) {
        if (item != null) {
            // Set the menu to toggle to Grid view
            item.setTitle(R.string.nav_grid);
            item.setIcon(R.drawable.ic_menu_grid);
        }
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_listview, layout);
        SampleListAdapter adapter = new SampleListAdapter(mContext);
        ListView listView = view.findViewById(R.id.list_items);
        listView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotifyView();
                }
            });
            fab.setImageResource(android.R.drawable.ic_popup_reminder);
        }
        setTitle(R.string.nav_list);
    }

    private void showNotifyView() {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_notifications, layout);
        Spinner spinner = view.findViewById(R.id.next_notification);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setNotifications();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    setNotifications();
                }
            });
        }
        spinner = view.findViewById(R.id.freq_notification);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setNotifications();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    setNotifications();
                }
            });
        }
        setTitle(R.string.nav_notifications);
    }

    private NotificationManager notificationManager;
    private static final int ACTIVITY_RECEIVER = 16;
    private Boolean isNotificationActive = false;

    private void setNotifications() {
        Spinner notifyNext = findViewById(R.id.next_notification),
                notifyFreq = findViewById(R.id.freq_notification),
                notifyDemo = findViewById(R.id.choose_demo_notification);
        Integer idNotification = R.string.notify_date_key;
        if (notifyNext != null && notifyFreq != null) {
            String[] strsNext = getResources().getStringArray(R.array.notification_next_list_values),
                    strsFreq = getResources().getStringArray(R.array.notification_freq_list_values),
                    strsDemo = getResources().getStringArray(R.array.notification_choose_demo_titles);

            Long alertTime = Calendar.getInstance().getTimeInMillis() + (long) (Integer.parseInt(strsNext[notifyNext.getSelectedItemPosition()]) * 1000),
                    alertInterval = (long) (Integer.parseInt(strsFreq[notifyFreq.getSelectedItemPosition()]) * 1000);
            String strExtra = null;
            if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_rotate))) {
                strExtra = ROTATE_DOES;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_list))) {
                strExtra = ROTATE_LIST;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_grid))) {
                strExtra = ROTATE_GRID;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_photo))) {
                strExtra = ROTATE_PHOTO;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_print))) {
                strExtra = ROTATE_PRINT;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_encryption))) {
                strExtra = ROTATE_ENCRYPT;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_notifications))) {
                strExtra = ROTATE_NOTIFY;
            } else if (strsDemo[notifyDemo.getSelectedItemPosition()].equals(getString(R.string.nav_api))) {
                strExtra = ROTATE_API;
            }
            Intent intent;
            if (strExtra != null)
                intent = new Intent(mContext, AlertReceiver.class).putExtra(EXTRA_ACTIVITY, strExtra);
            else
                intent = new Intent(mContext, AlertReceiver.class);
            PendingIntent alarmIntent;
            alarmIntent = PendingIntent.getBroadcast(mContext, ACTIVITY_RECEIVER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Integer.parseInt(strsNext[notifyNext.getSelectedItemPosition()]) > 0) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setInexactRepeating(
                            AlarmManager.RTC_WAKEUP,
                            alertTime,
                            alertInterval,
                            alarmIntent
                    );

                }
                isNotificationActive = true;
            } else {
                alarmIntent.cancel();
                isNotificationActive = false;
            }
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null && isNotificationActive) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopNotifications();
                }
            });
            fab.setImageResource(R.drawable.ic_menu_notification);
        } else if (fab != null) {
            fab.setImageResource(R.drawable.ic_menu_notifications_off);
        }
    }

    private void stopNotifications() {
        Intent intent = new Intent(mContext, AlertReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, ACTIVITY_RECEIVER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.cancel();
        isNotificationActive = false;
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null && isNotificationActive) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNotifications();
                }
            });
            fab.setImageResource(R.drawable.ic_menu_notifications_off);
        }
    }

    private void showPrintView() {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_print, layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Button button = view.findViewById(R.id.this_year);
            if (button != null) {
                button.setText(String.format(Locale.US, getString(R.string.print_annual_calendar), Calendar.getInstance()));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrintManager printManager = (PrintManager) mContext
                                .getSystemService(Context.PRINT_SERVICE);
                        String jobName = mContext.getString(R.string.app_name) +
                                " Document";
                        if (printManager != null) {
                            PrintAttributes printAttrs = new PrintAttributes.Builder().
                                    setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).
                                    setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE).
                                    setResolution(new PrintAttributes.Resolution("canvas", PRINT_SERVICE, CanvasPrintDocument.REPORT_DPI, CanvasPrintDocument.REPORT_DPI)).
                                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                                    build();
                            printManager.print(jobName, new CanvasPrintDocument(mContext, CanvasPrintDocument.CALENDAR_KEY, Calendar.getInstance()),
                                    printAttrs);
                        }
                    }
                });
            }
            button = view.findViewById(R.id.next_year);
            if (button != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, 1);
                button.setText(String.format(Locale.US, getString(R.string.print_annual_calendar), calendar));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrintManager printManager = (PrintManager) mContext
                                .getSystemService(Context.PRINT_SERVICE);
                        String jobName = mContext.getString(R.string.app_name) +
                                " Document";
                        if (printManager != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.YEAR, 1);
                            PrintAttributes printAttrs = new PrintAttributes.Builder()
                                    .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)        // print in greyscale
                                    .setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE)  // set default as landscape, if available (Minimum API 24??)
                                    .setResolution(new PrintAttributes.Resolution("canvas", PRINT_SERVICE, CanvasPrintDocument.REPORT_DPI, CanvasPrintDocument.REPORT_DPI))
                                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                                    .build();
                            printManager.print(jobName, new CanvasPrintDocument(mContext, CanvasPrintDocument.CALENDAR_KEY, calendar),
                                    printAttrs);
                        }
                    }
                });
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotifyView();
                }
            });
            fab.setImageResource(android.R.drawable.ic_popup_reminder);
        }
        setTitle(R.string.nav_print);
    }

    private static final String COORDINATES_KEY = "coordinate_text";
    private static final String COORDINATES_LAT_KEY = "coordinate_latitude";
    private static final String COORDINATES_LON_KEY = "coordinate_longitude";
    private static final String GPS_SWITCH_KEY = "gps_switch";
    private static final String GPS_SNAP_KEY = "gps_snap";
    private static final String GPS_GOOGLE_API_KEY = "@string/sampler_api_key";
    private static final Double GPS_MINIMUM_DISTANCE_DEGREES = 0.015;
    private static final Float GPS_MINIMUM_DISTANCE_METRES = 10.0f;
    private static final Long GPS_MINIMUM_TIME_MILLISECONDS = 6000L;
    private LocationListener locationListener;

    public void showApiView() {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_api, layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        View vwField = view.findViewById(R.id.gps_detail);
        if (vwField instanceof EditText) {
            ((EditText) vwField).setText(R.string.gps_dialog_disabled_message);
        }
        if (fab != null) {
            fab.setImageResource(R.drawable.ic_menu_geolocation_off);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (sharedPreferences != null && sharedPreferences.contains(GPS_SWITCH_KEY)) {
                // Check if GPS is on and is desired
                if (sharedPreferences.getBoolean(GPS_SWITCH_KEY, false)) {
                    geoSetListener();
                }
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    geoSwitch();
                }
            });
        }
        vwField = view.findViewById(R.id.gps_location);
        if (vwField instanceof EditText) {
            ((EditText) vwField).setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if(event.getAction()==KeyEvent.ACTION_UP){                    }
                    // try to change the location manually
                    String strField = ((EditText) v).getText().toString();
                    if (strField.matches("[0-9]+.[0-9]+[NS]\\s[0-9]+.[0-9]+[EW]")) {
                        String[] strsFields = strField.split("\\s");
                        Double dblLatitude = (strsFields[0].endsWith("N") ? 1 : -1) * Double.valueOf(strsFields[0].substring(0, strsFields[0].length() - 1)),
                                dblLongitude = (strsFields[1].endsWith("N") ? 1 : -1) * Double.valueOf(strsFields[1].substring(0, strsFields[1].length() - 1));
                        int intWidth = 200, intHeight = 200;
                        View vwMap = findViewById(R.id.map_image);
                        if (vwMap instanceof ImageView) {
                            intWidth = vwMap.getWidth();
                            intHeight = vwMap.getHeight();
                        }
                        fetchAddress(dblLatitude, dblLongitude);
                        fetchMap(dblLatitude, dblLongitude, intWidth, intHeight);
                    }
                    return false;
                }
            });
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileService.TRANSACTION_DONE);
        registerReceiver(downloadReceiver, intentFilter);
        setTitle(R.string.nav_api);
        int permissionFineGPS = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseGPS = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionInternet = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET);
        int permissionStorage = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionFineGPS != PackageManager.PERMISSION_GRANTED && permissionCoarseGPS != PackageManager.PERMISSION_GRANTED ||
                permissionInternet != PackageManager.PERMISSION_GRANTED ||
                permissionStorage != PackageManager.PERMISSION_GRANTED) {
            // Request permission to use Location, the Internet, and/or photos
            String strMessage = getString(R.string.general_dialog_permissions_message);
            if (permissionFineGPS != PackageManager.PERMISSION_GRANTED && permissionCoarseGPS != PackageManager.PERMISSION_GRANTED) {
                strMessage += "\n" + getString(R.string.gps_dialog_permissions_message);
            }
            if (permissionInternet != PackageManager.PERMISSION_GRANTED) {
                strMessage += "\n" + getString(R.string.ws_dialog_permissions_message);
            }
            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                strMessage += "\n" + getString(R.string.wes_dialog_permissions_message);
            }
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setMessage(strMessage)
                    .setPositiveButton(R.string.gps_dialog_permissions_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(
                                    (Activity) mContext,
                                    new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.INTERNET,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    },
                                    RETURN_FROM_PERMISSION_LOCATION);
                        }
                    })
                    .setNegativeButton(R.string.gps_dialog_permissions_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private void geoSetListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setImageResource(R.drawable.ic_menu_geolocation_on);
        }
        View view = findViewById(R.id.gps_detail);
        if (view instanceof EditText) {
            ((EditText) view).setText(R.string.gps_dialog_enabled_message);
        }
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        int permissionFineGPS = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseGPS = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionFineGPS == PackageManager.PERMISSION_GRANTED || permissionCoarseGPS == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                trackYourLocation(location);
            } else {
                if (view instanceof EditText) {
                    ((EditText) view).setText(R.string.gps_dialog_not_enabled_message);
                }
            }
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    trackYourLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    String fubar = provider;
                }

                @Override
                public void onProviderEnabled(String provider) {
                    String fubar = provider;
                }

                @Override
                public void onProviderDisabled(String provider) {
                    String fubar = provider;
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MINIMUM_TIME_MILLISECONDS, GPS_MINIMUM_DISTANCE_METRES, locationListener);

        }
    }

    public void geoSwitch() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean wasGPSOn = sharedPreferences.getBoolean(GPS_SWITCH_KEY, false);
        editor.putBoolean(GPS_SWITCH_KEY, !wasGPSOn);
        editor.apply();
        if (wasGPSOn) {
            geoStopListener();
        } else {
            geoSetListener();
        }
    }

    private void trackYourLocation(Location location) {
        if (location != null) {
            // get the current location, if it exists, and if it moved enough, save default location
            Double dblLatitude = location.getLatitude(),
                    dblLongitude = location.getLongitude();
            String strCoordinates = String.format(
                    Locale.US,
                    "%1$.6f%3$s %2$.6f%4$s",
                    Math.abs(dblLatitude),
                    Math.abs(dblLongitude),
                    dblLatitude > 0 ? "N" : "S",
                    dblLongitude > 0 ? "E" : "W"
            );
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (sharedPreferences != null &&
                    sharedPreferences.contains(COORDINATES_LAT_KEY) &&
                    sharedPreferences.contains(COORDINATES_LON_KEY)) {
                Float fltLat = sharedPreferences.getFloat(COORDINATES_LAT_KEY, Float.valueOf(Double.toString(dblLatitude)));
                Float fltLng = sharedPreferences.getFloat(COORDINATES_LON_KEY, Float.valueOf(Double.toString(dblLongitude)));
                Double dblDistanceDeg = Math.sqrt(Math.pow(fltLat - dblLatitude, 2) + Math.pow(fltLng - dblLongitude, 2)),
                        dblDistanceKm = dblDistanceDeg * 40000.0 / 360.0;
                Toast.makeText(mContext, String.format(
                        Locale.US,
                        "New Location(distance:%3$.5f): %1$.6f, %2$.6f",
                        dblLatitude,
                        dblLongitude,
                        dblDistanceKm),
                        (GPS_MINIMUM_TIME_MILLISECONDS < 15000L ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG)
                ).show();
                Log.d(TAG, "trackYourLocation: " + String.format(Locale.US, "New Location(distance:%3$.5f): %1$.6f, %2$.6f", dblLatitude, dblLongitude, dblDistanceKm));
            }
            View view = findViewById(R.id.gps_detail);
            view = findViewById(R.id.gps_location);
            if (view instanceof EditText) {
                ((EditText) view).setText(strCoordinates);
            }
            // Now go fetch the map and the address in two HTTP requests
            Intent intent;
            view = findViewById(R.id.map_image);
            if (view instanceof ImageView) {
                int intWidth = view.getWidth(),
                        intHeight = view.getHeight();
                if (intWidth < 25) intWidth = 25;
                if (intHeight < 25) intHeight = 25;
                fetchMap(dblLatitude, dblLongitude, intWidth, intHeight);
            }
            fetchAddress(dblLatitude, dblLongitude);

           /*
            //  Google Map SDK for Android is not appropriate for this particular application
            try {
                if (googleMap == null) {
                    SamplerWebServices webServices = new SamplerWebServices(mContext);
                    MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map_element);
                    map.getMapAsync(this);
                } else {
                    setupMap();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "trackYourLocation: " + e.getLocalizedMessage());
            }
            */
        }
    }

    private void fetchMap(Double latitude, Double longitude, int width, int height) {
        Intent intent = new Intent(mContext, FileService.class);
        intent.putExtra("url", String.format(Locale.US, mContext.getString(R.string.gps_url_map_image),
                latitude, longitude,
                getString(R.string.sampler_api_key),
                width, height)
        );
        intent.putExtra("file", "map");
        this.startService(intent);
    }

    private void fetchAddress(Double latitude, Double longitude) {
        Intent intent = new Intent(mContext, FileService.class);
        intent.putExtra("url", String.format(Locale.US, mContext.getString(R.string.gps_url_geo_address),
                latitude, longitude,
                getString(R.string.sampler_api_key))
        );
        intent.putExtra("file", "address");
        this.startService(intent);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Service Received");
            showFileContents("address");
            showFileContents("map");
        }
    };

/*
    private BroadcastReceiver imageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Service Received");
            showFileContents("map");
        }
    };
*/

    private void showFileContents(String strFile) {
        if (strFile.equals("address")) {
            // Display address
            String strAddress = mContext.getString(R.string.gps_dialog_enabled_message);
            StringBuilder stringBuilder;
            try {
                FileInputStream fileInputStream = this.openFileInput("address");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (stringBuilder.length() > 1) {
                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    JSONArray jsonArray, jsonArray1,
                            jsonResults = jsonObject.getJSONArray("results");
                    if (((JSONObject) jsonResults.get(0)).has("formatted_address"))
                        strAddress = ((JSONObject) jsonResults.get(0)).get("formatted_address").toString();
                    for (int i = 0; i < jsonResults.length(); i++) {
                        if (jsonResults.get(i) instanceof JSONObject) {
                            jsonObject = (JSONObject) jsonResults.get(i);
                            if (jsonObject.has("address_components") && jsonObject.get("address_components") instanceof JSONObject) {
                                jsonObject = (JSONObject) jsonObject.get("address_components");
                            } else if (jsonObject.has("address_components") && jsonObject.get("address_components") instanceof JSONArray) {
                                jsonArray = (JSONArray) jsonObject.get("address_components");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    if (jsonArray.get(j) instanceof JSONObject) {
                                        jsonObject = (JSONObject) jsonArray.get(j);
                                        if (jsonObject.has("types") && jsonObject.get("types") instanceof JSONArray) {
                                            jsonArray1 = (JSONArray) jsonObject.get("types");
                                            for (int k = 0; k < jsonArray.length(); k++) {
                                                if (jsonArray1.get(k) instanceof JSONArray) {
                                                    for (int l = 0; l < ((JSONArray) jsonArray1.get(k)).length(); l++) {
                                                        if (jsonArray1.get(l) instanceof JSONArray) {

                                                        } else if (jsonArray1.get(l) instanceof JSONObject) {
                                                            jsonObject = (JSONObject) jsonArray1.get(l);
                                                        }
                                                    }
                                                } else if (jsonArray1.get(k) instanceof JSONObject) {
                                                    jsonObject = (JSONObject) jsonArray1.get(k);
                                                } else if (jsonArray1.get(k).toString().equals("street_number") &&
                                                        ((JSONObject) jsonResults.get(i)).has("formatted_address")) {
                                                    strAddress = ((JSONObject) jsonResults.get(i)).get("formatted_address").toString();
                                                }
                                            }
                                        } else if (jsonObject.has("types") && jsonObject.get("types") instanceof JSONObject) {
                                            jsonObject = (JSONObject) jsonObject.get("types");
                                        }
                                    } else if (jsonArray.get(j) instanceof JSONArray) {
                                        jsonArray1 = (JSONArray) jsonArray.get(j);
                                    }
                                }
                            }
                        } else if (jsonResults.get(i) instanceof JSONArray) {
                            jsonArray = (JSONArray) jsonResults.get(i);
                            for (int j = 0; j < ((JSONArray) jsonResults.get(i)).length(); j++) {
                                if (jsonResults.get(j) instanceof JSONObject) {
                                    jsonObject = (JSONObject) jsonResults.get(j);
                                } else if (jsonResults.get(j) instanceof JSONArray) {
                                    jsonArray1 = (JSONArray) jsonResults.get(j);
                                }
                            }
                        }
                    }
                }
            } catch (
                    FileNotFoundException e) {
                e.printStackTrace();
            } catch (
                    UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (
                    IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((EditText) findViewById(R.id.gps_detail)).setText(strAddress);
        } else if (strFile.equals("map")) {
            // Display Image
            try {
                FileInputStream fileInputStream = mContext.openFileInput("map");
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                ((ImageView) findViewById(R.id.map_image)).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupMap();
    }

    private void setupMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
*/

    public void geoStopListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setImageResource(R.drawable.ic_menu_geolocation_off);
        }
        View view = findViewById(R.id.gps_detail);
        if (view instanceof EditText) {
            ((EditText) view).setText("GPS Disabled");
        }
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private final static String PREFERENCE_ENCRYPTION_CHARSET_KEY = "windows-1252"; // "US-ASCII";
    private static final byte[] PREFERENCE_ENCRYPTION_IV_KEY = "hlXQP2YHagw5BPO6".getBytes();

    private void showEncryptView() {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
        View view = inflater.inflate(R.layout.content_activity_encryption, layout);
        final EditText etInputCode = view.findViewById(R.id.input_code),
                etInputClear = view.findViewById(R.id.input_clear),
                etInputEncrypted = view.findViewById(R.id.input_encrypted),
                etInputDecode = view.findViewById(R.id.input_decrypt_code),
                etInputDecrypted = view.findViewById(R.id.input_decrypted);
        if (etInputCode != null && etInputClear != null && etInputEncrypted != null) {
            // With each keystroke run an encryption algorithm and decrypt when finished
            etInputCode.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showEncryptedText(etInputCode, etInputClear, etInputEncrypted, etInputDecode, etInputDecrypted);
                        }
                    }, 50);

                    return false;
                }
            });
            etInputClear.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showEncryptedText(etInputCode, etInputClear, etInputEncrypted, etInputDecode, etInputDecrypted);
                        }
                    }, 50);

                    return false;
                }
            });
            etInputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    showEncryptedText(etInputCode, etInputClear, etInputEncrypted, etInputDecode, etInputDecrypted);
                }
            });
            etInputClear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    showEncryptedText(etInputCode, etInputClear, etInputEncrypted, etInputDecode, etInputDecrypted);
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotifyView();
                }
            });
            fab.setImageResource(android.R.drawable.ic_popup_reminder);
        }
        setTitle(R.string.nav_encryption);
    }

    private void showEncryptedText(final EditText etInputCode, final EditText etInputClear, final EditText etInputEncrypted, final EditText etInputDecode, final EditText etInputDecrypted) {
        if (etInputCode.getText().toString().length() > 0 && etInputClear.getText().toString().length() > 0) {
            // The byte array will be converted over and over again to ASCII. To improve performance, get the character set just once.
            Charset charset = Charset.forName(PREFERENCE_ENCRYPTION_CHARSET_KEY);
            AdvancedEncryptionSystemHelper helper = new AdvancedEncryptionSystemHelper();
            String keyEncrypt = (etInputCode.getText().toString() + helper.generatedEncryptionKey()).substring(0, 16);
            try {
                helper = new AdvancedEncryptionSystemHelper(keyEncrypt.getBytes(), PREFERENCE_ENCRYPTION_IV_KEY);
                byte[] bytesEncrypted = helper.encrypt(etInputClear.getText().toString().getBytes());
                String strEncrypted = new String(bytesEncrypted, charset);
                etInputEncrypted.setText(strEncrypted);
                etInputDecode.setText(keyEncrypt);
                String strDecrypted = new String(helper.decrypt(bytesEncrypted), charset);
                // etInputDecrypted.setText(strDecrypted);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDecryptedText(etInputEncrypted, etInputDecode, etInputDecrypted);
                    }
                }, 50);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onKey: " + e.getLocalizedMessage());
            }

        }
    }

    private void showDecryptedText(EditText etInputEncrypted, EditText etInputDecode, EditText etInputDecrypted) {
        if (etInputDecode.getText().toString().length() > 0 && etInputEncrypted.getText().toString().length() > 0) {
            Charset charset = Charset.forName(PREFERENCE_ENCRYPTION_CHARSET_KEY);
            String keyEncrypt = (etInputDecode.getText().toString() + (new AdvancedEncryptionSystemHelper()).generatedEncryptionKey()).substring(0, 16);
            try {
                AdvancedEncryptionSystemHelper helper = new AdvancedEncryptionSystemHelper(keyEncrypt.getBytes(), PREFERENCE_ENCRYPTION_IV_KEY);
                byte[] bytesDecrypted = helper.decrypt(etInputEncrypted.getText().toString().getBytes(charset));
                String strDecrypted = new String(bytesDecrypted, charset);
                etInputDecrypted.setText(strDecrypted);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onKey: " + e.getLocalizedMessage());
            }
        }
    }

    private void safeDestroyFragment() {
        ViewGroup layout = findViewById(R.id.container);
        safeDestroyFragment(layout);
    }

    private void safeDestroyFragment(ViewGroup layout) {
        if (layout != null) {
            // Stop any listeners or services launched in retiring fragment.
            if (layout.findViewById(R.id.rotate_checkbox_text) instanceof CheckBox) {
            } else if (layout.findViewById(R.id.grid_items) instanceof GridView) {
            } else if (layout.findViewById(R.id.list_items) instanceof ListView) {
            } else if (layout.findViewById(R.id.photo_view) instanceof LinearLayout) {
            } else if (layout.findViewById(R.id.print_view) instanceof LinearLayout) {
            } else if (layout.findViewById(R.id.encrypt_view) instanceof ScrollView) {
            } else if (layout.findViewById(R.id.notify_view) instanceof LinearLayout) {
            } else if (layout.findViewById(R.id.api_view) instanceof LinearLayout) {
                if (downloadReceiver != null) {
                    unregisterReceiver(downloadReceiver);
//                    if (imageReceiver != null) {
//                        unregisterReceiver(imageReceiver);
                    geoStopListener();
//                    MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map_element);
//                    map.onDestroyView(new Ondes);
//                    }
                }
            }
            layout.removeAllViews();
        }
    }

    private void showAboutView() {
        Intent intent = new Intent(mContext, ActivityDocumentation.class)
                .putExtra(ActivityDocumentation.DOCUMENT_KEY, ActivityDocumentation.ABOUT_KEY);
        startActivity(intent);
    }

    private void showHelpView() {
        Intent intent = new Intent(mContext, ActivityDocumentation.class)
                .putExtra(ActivityDocumentation.DOCUMENT_KEY, ActivityDocumentation.HELP_KEY);
        startActivity(intent);
    }

    private void cascadeExit() {
        safeDestroyFragment();
        finish();
    }

    private void unwrap_image(Intent data, ImageView imageView) {
        if (data != null) {
            String strFileTest3;
            try {
                Bundle extras = data.getExtras();
                Integer intCompression = getResources().getInteger(R.integer.image_maximum_compression);
                Bitmap bitmap = (Bitmap) extras.get("data");
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    /** Danger. Resources intensive. **/
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    if (bytes != null && bytes.length > 0) bytsPhotoImage = bytes;
                }
                /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                File folder = getExternalFilesDir(IMAGE_FOLDER);
                File file = new File(folder, "IMG_"+timeStamp+".jpg");
                strFileTest3 = file.getPath();
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,intCompression,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Log.d("Original dimensions", bitmap.getWidth()+" "+bitmap.getHeight());
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                File file3 = new File(strFileTest3);
                if( file3.exists() ) {
                    // Re-sample bitmap for stash-card
                    StashImage stashImage = new StashImage(strFileTest3);
                    btmCatalog = stashImage.getStashCardImage(StashCardActivity.this);
                    imageView.setImageBitmap(btmCatalog);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Image Corrupt", Toast.LENGTH_LONG).show();
        }

    }

    public class SampleAdapter extends BaseAdapter {
        private Context sampleContext;

        public SampleAdapter(Context context) {
            this.sampleContext = context;
        }

        public int getCount() {
            return mImages.length;
        }

        public Object getItem(int idPosition) {
            return null;
        }

        public long getItemId(int idPosition) {
            return 0L;
        }

        public View getView(int idPosition, View view, ViewGroup viewGroup) {
            ImageView imageView = new ImageView(sampleContext);
            imageView.setImageResource(mImages[idPosition]);
            return imageView;
        }

        private Integer[] mImages = new Integer[]{
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow
        };

    }

    public class SampleListAdapter extends BaseAdapter {
        private Context sampleContext;
        private Integer sampleResource;
        private DataObject[] dataObjects;

        private class DataObject {
            private String strShort1;
            private String strShort2;
            private String strShort3;
            private String strDescription;
            private Integer image;

            public String getStrShort1() {
                return strShort1;
            }

            public void setStrShort1(String strShort1) {
                this.strShort1 = strShort1;
            }

            public String getStrShort2() {
                return strShort2;
            }

            public void setStrShort2(String strShort2) {
                this.strShort2 = strShort2;
            }

            public String getStrShort3() {
                return strShort3;
            }

            public void setStrShort3(String strShort3) {
                this.strShort3 = strShort3;
            }

            public String getStrDescription() {
                return strDescription;
            }

            public void setStrDescription(String strDescription) {
                this.strDescription = strDescription;
            }

            public Integer getImage() {
                return image;
            }

            public void setImage(Integer image) {
                this.image = image;
            }

        }

        public SampleListAdapter(Context context) {
            this.sampleContext = context;
            this.sampleResource = R.layout.fragment_activity_listview;
            dataObjects = new DataObject[mImages.length];
            for (int i = 0; i < mImages.length; i++) {
                dataObjects[i] = new DataObject();
                dataObjects[i].setImage(mImages[i]);
                dataObjects[i].setStrShort1(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " for %d", i + 1));
                dataObjects[i].setStrShort2(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " with %d", i + 1));
                dataObjects[i].setStrShort3(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " as %d", i + 1));
                dataObjects[i].setStrDescription(
                        String.format(Locale.US, mContext.getString(R.string.sample_long_text) + "\nPresented by %d", i + 1));
            }
        }

        public int getCount() {
            return mImages.length;
        }

        public Object getItem(int idPosition) {
            return null;
        }

        public long getItemId(int idPosition) {
            return 0L;
        }

        public View getView(int idPosition, View convertView, ViewGroup vgParent) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(sampleContext);
            // Unconditional layout inflation from view adapter:
            // Should use View Holder pattern (use recycled view passed into this method as the second parameter) for smoother scrolling,
            // but it is sufficient for this demo
            convertView = inflater.inflate(sampleResource, vgParent, false);
            view = convertView.findViewById(R.id.profile_id);
            if (view instanceof TextView)
                ((TextView) view).setText(String.format(Locale.US, "%d", idPosition));
            view = convertView.findViewById(R.id.profile_id_name);
            if (view instanceof TextView)
                ((TextView) view).setText(dataObjects[idPosition].getStrShort1());
            view = convertView.findViewById(R.id.profile_brief_one);
            if (view instanceof TextView)
                ((TextView) view).setText(dataObjects[idPosition].getStrShort2());
            view = convertView.findViewById(R.id.profile_brief_two);
            if (view instanceof TextView)
                ((TextView) view).setText(dataObjects[idPosition].getStrShort3());
            view = convertView.findViewById(R.id.profile_description);
            if (view instanceof TextView)
                ((TextView) view).setText(dataObjects[idPosition].getStrDescription());
            view = convertView.findViewById(R.id.profile_image);
            if (view instanceof ImageView) {
                try {
                    ((ImageView) view).setImageResource(dataObjects[idPosition].getImage());
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getView: " + e.getLocalizedMessage());
                }
            }
            return convertView;
        }

        private Integer[] mImages = new Integer[]{
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow
        };

    }

    public class SampleGridAdapter extends BaseAdapter {
        private Context sampleContext;
        private Integer sampleResource;
        private DataObject[] dataObjects;

        private class DataObject {
            private String strShort1;
            private String strShort2;
            private String strShort3;
            private String strDescription;
            private Integer image;

            public String getStrShort1() {
                return strShort1;
            }

            public void setStrShort1(String strShort1) {
                this.strShort1 = strShort1;
            }

            public String getStrShort2() {
                return strShort2;
            }

            public void setStrShort2(String strShort2) {
                this.strShort2 = strShort2;
            }

            public String getStrShort3() {
                return strShort3;
            }

            public void setStrShort3(String strShort3) {
                this.strShort3 = strShort3;
            }

            public String getStrDescription() {
                return strDescription;
            }

            public void setStrDescription(String strDescription) {
                this.strDescription = strDescription;
            }

            public Integer getImage() {
                return image;
            }

            public void setImage(Integer image) {
                this.image = image;
            }

        }

        public SampleGridAdapter(@NonNull Context context) {
            this.sampleContext = context;
            this.sampleResource = R.layout.fragment_activity_gridview;
            dataObjects = new DataObject[mImages.length];
            for (int i = 0; i < mImages.length; i++) {
                dataObjects[i] = new DataObject();
                dataObjects[i].setImage(mImages[i]);
                dataObjects[i].setStrShort1(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " for %d", i + 1));
                dataObjects[i].setStrShort2(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " with %d", i + 1));
                dataObjects[i].setStrShort3(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " as %d", i + 1));
                dataObjects[i].setStrDescription(
                        String.format(Locale.US, mContext.getString(R.string.sample_long_text) + "\nPresented by %d", i + 1));
            }
        }

        public SampleGridAdapter(@NonNull Context context, @LayoutRes int resource) {
            this.sampleContext = context;
            this.sampleResource = resource;
            dataObjects = new DataObject[mImages.length];
            for (int i = 0; i < mImages.length; i++) {
                dataObjects[i] = new DataObject();
                dataObjects[i].setImage(mImages[i]);
                dataObjects[i].setStrShort1(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " for %d", i + 1));
                dataObjects[i].setStrShort2(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " with %d", i + 1));
                dataObjects[i].setStrShort3(
                        String.format(Locale.US, mContext.getString(R.string.sample_short_text) + " as %d", i + 1));
                dataObjects[i].setStrDescription(
                        String.format(Locale.US, mContext.getString(R.string.sample_long_text) + "\nPresented by %d", i + 1));
            }
        }

        public int getCount() {
            return mImages.length;
        }

        public Object getItem(int idPosition) {
            return null;
        }

        public long getItemId(int idPosition) {
            return 0L;
        }

        public View getView(int idPosition, View convertView, ViewGroup vgParent) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(sampleContext);
            // Unconditional layout inflation from view adapter:
            // Should use View Holder pattern (use recycled view passed into this method as the second parameter) for smoother scrolling,
            // but it is sufficient for this demo
            convertView = inflater.inflate(sampleResource, vgParent, false);
            view = convertView.findViewById(R.id.profile_id);
            if (view instanceof TextView)
                ((TextView) view).setText(String.format(Locale.US, "%d", idPosition));
            view = convertView.findViewById(R.id.profile_id_name);
            if (view instanceof TextView)
                ((TextView) view).setText(dataObjects[idPosition].getStrShort1());
            view = convertView.findViewById(R.id.profile_image);
            if (view instanceof ImageView) {
                try {
                    ((ImageView) view).setImageResource(dataObjects[idPosition].getImage());
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getView: " + e.getLocalizedMessage());
                }
            }
            return convertView;
        }

        private Integer[] mImages = new Integer[]{
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow,
                android.R.drawable.sym_def_app_icon,
                R.drawable.ic_menu_api,
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_encryption,
                R.drawable.ic_menu_exit,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_grid,
                R.drawable.ic_menu_help,
                R.drawable.ic_menu_info,
                R.drawable.ic_menu_list,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_notification,
                R.drawable.ic_menu_print,
                R.drawable.ic_menu_rotate,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share,
                R.drawable.ic_menu_sign_off,
                R.drawable.ic_menu_sign_on,
                R.drawable.ic_menu_slideshow
        };

    }

    private class ListArrayAdapter extends ArrayAdapter<DataSamples.DataRecord> {
        private Context mContext;   // Link to the calling activity
        private Integer mResource;  // Link to the minicard inflater
        private ArrayList<DataSamples.DataRecord> mObject;

        class ViewHolder {
            TextView profile_id, profile_id_name, simple_brief_one, profile_description, profile_brief_two;
            ImageView profile_image;
        }

        public ListArrayAdapter(Context context, int resource) {
            super(context, resource);
            this.mContext = context;
            this.mResource = resource;
            this.mObject = new ArrayList<DataSamples.DataRecord>();
        }

        public ListArrayAdapter(Context context, int resource, ArrayList<DataSamples.DataRecord> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.mObject = objects;
        }
    }
}
