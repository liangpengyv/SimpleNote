package online.laoliang.simplenote.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import online.laoliang.simplenote.R;
import online.laoliang.simplenote.util.CheckForUpdateUtil;

/**
 * 关于页面
 * Created by liang on 12/20.
 */
public class AboutActivity extends Activity implements View.OnClickListener {

    private Button back;
    private TextView version;
    private Button check_welcome;
    private Button check_version;
    private Button feed_back;
    private Button project_address;
    private Button qq_qun;

    private void findView() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        version = (TextView) findViewById(R.id.version);
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        String versionCode;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            versionCode = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionCode = "N/A";
        }
        version.setText("Version：" + versionCode);
        check_welcome = (Button) findViewById(R.id.check_welcome);
        check_welcome.setOnClickListener(this);
        check_version = (Button) findViewById(R.id.check_version);
        check_version.setOnClickListener(this);
        feed_back = (Button) findViewById(R.id.feed_back);
        feed_back.setOnClickListener(this);
        project_address = (Button) findViewById(R.id.project_address);
        project_address.setOnClickListener(this);
        qq_qun = (Button) findViewById(R.id.qq_qun);
        qq_qun.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findView();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.back:
                super.onBackPressed();
                break;
            case R.id.check_welcome:
                Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
                startActivity(welcomeIntent);
                super.onBackPressed();
                break;
            case R.id.check_version:
                CheckForUpdateUtil.checkForUpdateInFIR(this, false);
                break;
            case R.id.feed_back:
                Intent feedBackIntent = new Intent(this, FeedbackActivity.class);
                startActivity(feedBackIntent);
                break;
            case R.id.project_address:
                Intent addressIntent = new Intent(Intent.ACTION_VIEW);
                addressIntent.setData(Uri.parse("https://github.com/liangpengyv/SimpleNote"));
                startActivity(addressIntent);
                break;
            case R.id.qq_qun:
                Intent qqIntent = new Intent(Intent.ACTION_VIEW);
                qqIntent.setData(Uri.parse("http://jq.qq.com/?_wv=1027&k=2AgKvcH"));
                startActivity(qqIntent);
                break;
        }
    }
}
