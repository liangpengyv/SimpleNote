package online.laoliang.simplenote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import online.laoliang.simplenote.R;

/**
 * 建议反馈的WebView页面
 * Created by liang on 12/21.
 */
public class FeedbackActivity extends Activity implements View.OnClickListener {

    private Button back;
    private WebView feed_back;

    private void findView() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        feed_back = (WebView) findViewById(R.id.feed_back);
        feed_back.getSettings().setJavaScriptEnabled(true);
        feed_back.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        feed_back.setWebViewClient(new WebViewClient());
        feed_back.loadUrl("http://form.mikecrm.com/Vgc2aM");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        findView();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.back:
                super.onBackPressed();
                break;
        }
    }
}
