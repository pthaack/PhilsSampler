package ca.philipyoung.philssampler.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.philipyoung.philssampler.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WebPrintDocument {
    /*
    Adapted from printContent by Techotopia: Retrieved from "https://www.techotopia.com/index.php/Printing_with_the_Android_Printing_Framework"
    */
    Context mContext;
    WebView myWebView;
    String strJobName, strDocumentName;

    public WebPrintDocument(Context context) {
        this.mContext = context;
        this.strJobName = mContext.getString(R.string.app_name) + " Document";
        this.strDocumentName = "MyDocument";
    }

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

        webView.loadDataWithBaseURL(null, strWebContent, "text/HTML", "UTF-8", null);
        myWebView = webView;

    }

    private void createWebPrintJob(WebView webView, @Nullable String fileName) {

        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter(fileName != null ? fileName : this.strDocumentName);
        String jobName = this.strJobName;

        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }
}
