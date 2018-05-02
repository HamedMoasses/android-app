package eu.h2020.sc.ui;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.utils.Utils;

public class FaqActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        this.initWebView();
        initToolBar(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDrawer(getString(R.string.faq));
    }

    private void initWebView() {
        showProgressDialog();
        WebView webView = (WebView) findViewById(R.id.faq_activity_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(getString(R.string.url_faq));
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                dismissDialog();
            }
        });
    }
}
