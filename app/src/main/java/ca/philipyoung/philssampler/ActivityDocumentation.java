package ca.philipyoung.philssampler;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import ca.philipyoung.philssampler.ui.WebPrintDocument;

public class ActivityDocumentation extends AppCompatActivity {
    private static final String TAG = "ActivityDocumentation";

    public static final String DOCUMENT_KEY = "document";
    public static final String EULA_KEY = "eula";
    public static final String PRIVACY_KEY = "privacy";
    public static final String ABOUT_KEY = "about";
    public static final String HELP_KEY = "help";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_documentation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView textView;
        String strView;
        FloatingActionButton fab;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            fab.setVisibility(View.GONE);

        if (getIntent().hasExtra(DOCUMENT_KEY)) {
            switch (getIntent().getStringExtra(DOCUMENT_KEY)) {
                case HELP_KEY:
                    setContentView(R.layout.activity_documentation);
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    setTitle("Help for Demo");
                    textView = (TextView) findViewById(R.id.document);
                    textView.setText(Html.fromHtml(mContext.getString(R.string.documentation_help_html), new ImageGetter(), null));
                    fab = (FloatingActionButton) findViewById(R.id.fab);
                    if (fab != null)
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    printDocument(getTitle().toString(), mContext.getString(R.string.documentation_help_html), "phil_sampler_help");
                                }
                            }
                        });
                    break;
                case ABOUT_KEY:
                    setContentView(R.layout.activity_documentation);
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    setTitle("About this Demo");
                    textView = (TextView) findViewById(R.id.document);
                    textView.setText(Html.fromHtml(mContext.getString(R.string.documentation_about_html)));
                    fab = (FloatingActionButton) findViewById(R.id.fab);
                    if (fab != null)
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    printDocument(getTitle().toString(), mContext.getString(R.string.documentation_about_html), "phil_sampler_about");
                                }
                            }
                        });
                    break;
                case EULA_KEY:
                    setContentView(R.layout.activity_documentation);
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    setTitle("End User License Agreement");
                    textView = (TextView) findViewById(R.id.document);
                    textView.setText(Html.fromHtml(mContext.getString(R.string.documentation_eula_html)));
                    fab = (FloatingActionButton) findViewById(R.id.fab);
                    if (fab != null)
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    printDocument(getTitle().toString(), textView.getText().toString(), "phil_sampler_eula");
                                }
                            }
                        });
                    break;
                case PRIVACY_KEY:
                    setContentView(R.layout.activity_documentation);
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    setTitle("Privacy Policy for Demo");
                    textView = (TextView) findViewById(R.id.document);
                    textView.setText(Html.fromHtml(mContext.getString(R.string.documentation_privacy_html)));
                    fab = (FloatingActionButton) findViewById(R.id.fab);
                    if (fab != null)
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                }
                                printDocument(getTitle().toString(), textView.getText().toString(), "phil_sampler_privacy");
                            }
                        });
                    break;
            }
        }

    }

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;

            id = getResources().getIdentifier(source, "drawable", getPackageName());

            if (id == 0) {
                // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
                id = getResources().getIdentifier(source, "drawable", "android");
            }

            if (id == 0) {
                // prevent a crash if the resource still can't be found
                return null;
            }
            else {
                Drawable d = getResources().getDrawable(id);
                d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                return d;
            }
        }

    }
    /*
   Adapted from PrintDocumentAdapter by Techotopia: Retrieved from "https://www.techotopia.com/index.php/Printing_with_the_Android_Printing_Framework"
   */
    WebView myWebView;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void printDocument(@Nullable final String title, @NonNull final String htmlBody, @NonNull final String fileName) {
        String strWebContent = (title != null && !title.isEmpty() ? "<h1>" + title + "</h1>" : "") + htmlBody;
        WebView webView = new WebView(mContext);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view,
                                                    String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view, fileName);
                myWebView = null;
            }
        });

        webView.loadDataWithBaseURL("file:///android_res/drawable/", strWebContent, "text/HTML", "UTF-8", null);
        myWebView = webView;

        /*
        Toast.makeText(mContext, "Coming soon...", Toast.LENGTH_LONG).show();
          */
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createWebPrintJob(WebView webView, @Nullable String fileName) {

        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter(fileName != null ? fileName : "MyDocument");
        String jobName = getString(R.string.app_name) + " Document";

        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }
}
