package online.laoliang.simplenote.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import online.laoliang.simplenote.R;

/**
 * 设置页面
 * Created by liang on 12/20.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button select_order;
    private Button auto_update;
    private ImageView auto_update_switch;

    private void findView() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        select_order = (Button) findViewById(R.id.select_order);
        select_order.setOnClickListener(this);
        auto_update = (Button) findViewById(R.id.auto_update);
        auto_update.setOnClickListener(this);
        auto_update_switch = (ImageView) findViewById(R.id.auto_update_switch);

        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        boolean auto_update_show = pref.getBoolean("auto_update_show", true);
        if (auto_update_show) {
            auto_update_switch.setImageResource(R.mipmap.on);
        } else {
            auto_update_switch.setImageResource(R.mipmap.off);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findView();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        switch (id) {
            case R.id.back:
                super.onBackPressed();
                break;
            case R.id.select_order:
                PopupMenu popup = new PopupMenu(this, view);
                popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem arg0) {
                        SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
                        switch (arg0.getItemId()) {
                            //新建便签在列表最前
                            case R.id.new_before:
                                editor.putString("select_order", "new_before");
                                editor.commit();
                                break;
                            //新建便签在列表最后
                            case R.id.new_back:
                                editor.putString("select_order", "new_back");
                                editor.commit();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                break;
            case R.id.auto_update:
                boolean auto_update_show = pref.getBoolean("auto_update_show", true);
                if (auto_update_show) {
                    auto_update_switch.setImageResource(R.mipmap.off);
                    editor.putBoolean("auto_update_show", false);
                    editor.commit();
                } else {
                    auto_update_switch.setImageResource(R.mipmap.on);
                    editor.putBoolean("auto_update_show", true);
                    editor.commit();
                }
                break;
        }
    }
}
