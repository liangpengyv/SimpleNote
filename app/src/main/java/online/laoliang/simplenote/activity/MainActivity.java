package online.laoliang.simplenote.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import online.laoliang.simplenote.R;
import online.laoliang.simplenote.db.SimpleNoteDB;
import online.laoliang.simplenote.model.Note;
import online.laoliang.simplenote.util.CheckForUpdateUtil;

public class MainActivity extends Activity implements View.OnClickListener {

    //左滑菜单
    private Button menu_left;
    //新建笔记按钮
    private Button add_note;
    //笔记分组标题
    private TextView note_title;

    //左滑抽屉布局
    private DrawerLayout drawer_layout;
    private View grouping_list;
    private Button all_note;
    private Button discard_note;
    private Button wait_note;
    private Button wish_note;
    private Button setting;
    private Button about;

    //便签列表
    private ListView note_list;

    //Note实体类
    private Note note = new Note();
    //数据库操作对象
    private SimpleNoteDB simpleNoteDB;

    private void findView() {
        menu_left = (Button) findViewById(R.id.menu_left);
        menu_left.setOnClickListener(this);
        add_note = (Button) findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        note_title = (TextView) findViewById(R.id.note_title);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        grouping_list = findViewById(R.id.grouping_list);

        all_note = (Button) findViewById(R.id.all_note);
        all_note.setOnClickListener(this);
        discard_note = (Button) findViewById(R.id.discard_note);
        discard_note.setOnClickListener(this);
        wait_note = (Button) findViewById(R.id.wait_note);
        wait_note.setOnClickListener(this);
        wish_note = (Button) findViewById(R.id.wish_note);
        wish_note.setOnClickListener(this);
        setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(this);
        about = (Button) findViewById(R.id.about);
        about.setOnClickListener(this);

        note_list = (ListView) findViewById(R.id.note_list);

        simpleNoteDB = SimpleNoteDB.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置contentFeature,可使用切换动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide_right = TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
        Transition slide_left = TransitionInflater.from(this).inflateTransition(R.transition.slide_left);
        //退出时使用
        getWindow().setExitTransition(slide_left);
        //第一次进入时使用
        getWindow().setEnterTransition(slide_left);
        //再次进入时使用
        getWindow().setReenterTransition(slide_left);

        setContentView(R.layout.activity_main);

        findView();

        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        //是否为首次启动应用
        boolean first_start = pref.getBoolean("first_start", true);
        if(first_start){
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
        //加载便签列表
        String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
        initNoteList(nonce_grouping);
        //如果设置为自动检查更新，则执行检查更新操作
        boolean auto_update_show = pref.getBoolean("auto_update_show", true);
        if(auto_update_show){
            CheckForUpdateUtil.checkForUpdateInFIR(this, true);
        }

    }

    /**
     * 重载Activity的onStart方法，用于在每次Activity由不可见变为可见的时候重新加载ListView
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
        initNoteList(nonce_grouping);
    }

    /**
     * 初始化当前便签列表
     *
     * @param nonce_grouping
     */
    private void initNoteList(final String nonce_grouping) {
        if (nonce_grouping.equals("回收站")) {
            add_note.setBackground(null);
            add_note.setText("清空");
            add_note.setTextSize(16);
            add_note.setTextColor(Color.rgb(255, 255, 255));
        } else {
            add_note.setText("");
            add_note.setBackgroundResource(R.mipmap.add_note);
        }
        note_title.setText(nonce_grouping);
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        String select_order = pref.getString("select_order", "new_before");
        String select_order_show;
        if (select_order.equals("new_before")) {
            select_order_show = "id desc";
        } else {
            select_order_show = "id";
        }
        final List<String> noteList = simpleNoteDB.selectNote(nonce_grouping, select_order_show);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.item_note_list, noteList);
        note_list.setAdapter(adapter);

        //便签列表建立点击监听事件（单击进入编辑页面）
        note_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text_content = noteList.get(i);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("add_or_edit", "edit");
                intent.putExtra("text_content", text_content);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                finish();
            }
        });

        //便签列表建立长按监听事件（长按提示删除便签）
        note_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                if (!nonce_grouping.equals("回收站")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("移动到回收站？");
                    builder.setCancelable(true);
                    builder.setPositiveButton("移至回收站", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.updateNoteGrouping(noteList.get(i), "回收站");
                            initNoteList(nonce_grouping);
                        }
                    });
                    builder.setNegativeButton("直接删除！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.deleteNote(noteList.get(i));
                            initNoteList(nonce_grouping);
                        }
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("恢复便签？");
                    builder.setCancelable(true);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.updateNoteGrouping(noteList.get(i), "全部便签");
                            initNoteList(nonce_grouping);
                        }
                    });
                    builder.setNegativeButton("No，彻底删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.deleteNote(noteList.get(i));
                            initNoteList(nonce_grouping);
                        }
                    });
                    builder.show();
                }
                //长按事件后不想继续执行点击事件时，应返回true
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
        int id = view.getId();
        switch (id) {
            case R.id.menu_left:
                drawer_layout.openDrawer(grouping_list);
                break;
            case R.id.add_note:
                SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
                if (!nonce_grouping.equals("回收站")) {
                    Intent editIntent = new Intent(this, EditActivity.class);
                    editIntent.putExtra("add_or_edit", "add");
                    startActivity(editIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("清空回收站？");
                    builder.setCancelable(true);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.deleteNote(null);
                            initNoteList("回收站");
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.all_note:
                editor.putString("nonce_grouping", "全部便签");
                editor.commit();
                initNoteList("全部便签");
                drawer_layout.closeDrawers();
                break;
            case R.id.discard_note:
                editor.putString("nonce_grouping", "回收站");
                editor.commit();
                initNoteList("回收站");
                drawer_layout.closeDrawers();
                break;
            case R.id.wait_note:
                editor.putString("nonce_grouping", "待办清单");
                editor.commit();
                initNoteList("待办清单");
                drawer_layout.closeDrawers();
                break;
            case R.id.wish_note:
                editor.putString("nonce_grouping", "长期愿望");
                editor.commit();
                initNoteList("长期愿望");
                drawer_layout.closeDrawers();
                break;
            case R.id.setting:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                drawer_layout.closeDrawers();
                break;
            case R.id.about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                drawer_layout.closeDrawers();
                break;
        }
    }
}
