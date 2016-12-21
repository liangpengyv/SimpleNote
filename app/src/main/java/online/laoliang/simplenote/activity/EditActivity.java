package online.laoliang.simplenote.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import online.laoliang.simplenote.R;
import online.laoliang.simplenote.db.SimpleNoteDB;
import online.laoliang.simplenote.model.Note;

/**
 * 笔记编辑页面
 * Created by liang on 12/18.
 */
public class EditActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button delete;
    private TextView note_title;
    private EditText edit_view;

    //Note实体类
    private Note note = new Note();
    //数据库操作对象
    private SimpleNoteDB simpleNoteDB;

    //修改便签时，用来保存旧内容
    private String old_text_content;

    private void findView() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        note_title = (TextView) findViewById(R.id.note_title);
        edit_view = (EditText) findViewById(R.id.edit_view);
        //失去焦点
        edit_view.setFocusable(true);
        edit_view.setFocusableInTouchMode(true);
        edit_view.clearFocus();
        edit_view.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点
                    delete.setVisibility(View.GONE);
                } else {
                    // 失去焦点
                    delete.setVisibility(View.VISIBLE);
                }
            }
        });
        simpleNoteDB = SimpleNoteDB.getInstance(this);
    }

    private void initWindows() {
        Intent intent = getIntent();
        if (intent.getStringExtra("add_or_edit").equals("add")) {
            //新建便签
            SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
            String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
            note_title.setText(nonce_grouping);
            delete.setVisibility(View.GONE);
        } else {
            //修改便签
            old_text_content = intent.getStringExtra("text_content");
            edit_view.setText(old_text_content);
            note_title.setText("编辑");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置contentFeature,可使用切换动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide_right = TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
        Transition slide_left = TransitionInflater.from(this).inflateTransition(R.transition.slide_left);
        //退出时使用
        getWindow().setExitTransition(slide_right);
        //第一次进入时使用
        getWindow().setEnterTransition(slide_right);
        //再次进入时使用
        getWindow().setReenterTransition(slide_right);

        setContentView(R.layout.activity_edit);
        findView();
        initWindows();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.back:
                Intent intent = getIntent();
                if (intent.getStringExtra("add_or_edit").equals("add")) {
                    //新建便签
                    String text_content = edit_view.getText().toString();
                    if (!text_content.equals("")) {
                        note.setTextContent(text_content);
                        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                        String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
                        note.setGrouping(nonce_grouping);
                        simpleNoteDB.insertNote(note);
                    }
                } else {
                    //修改便签
                    String new_text_content = edit_view.getText().toString();
                    if (!new_text_content.equals("")) {
                        simpleNoteDB.updateNote(new_text_content, old_text_content);
                    }
                }
                Intent saveIntent = new Intent(this, MainActivity.class);
                startActivity(saveIntent, ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());
                finish();
                break;
            case R.id.delete:
                SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
                if (nonce_grouping.equals("回收站")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    builder.setTitle("彻底删除吗？");
                    builder.setCancelable(true);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.deleteNote(old_text_content);
                            Intent deleteIntent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(deleteIntent, ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    builder.setTitle("移动到回收站？");
                    builder.setCancelable(true);
                    builder.setPositiveButton("移至回收站", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.updateNoteGrouping(old_text_content, "回收站");
                            Intent deleteIntent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(deleteIntent, ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());
                            finish();
                        }
                    });
                    builder.setNegativeButton("直接删除！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            simpleNoteDB.deleteNote(old_text_content);
                            Intent deleteIntent = new Intent(EditActivity.this, MainActivity.class);
                            startActivity(deleteIntent, ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());
                            finish();
                        }
                    });
                    builder.show();
                }
                break;
        }
    }

    /**
     * 重写Back键逻辑
     */
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (intent.getStringExtra("add_or_edit").equals("add")) {
            //新建便签
            String text_content = edit_view.getText().toString();
            if (!text_content.equals("")) {
                note.setTextContent(text_content);
                SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                String nonce_grouping = pref.getString("nonce_grouping", "全部便签");
                note.setGrouping(nonce_grouping);
                simpleNoteDB.insertNote(note);
            }
        } else {
            //修改便签
            String new_text_content = edit_view.getText().toString();
            if (!new_text_content.equals("")) {
                simpleNoteDB.updateNote(new_text_content, old_text_content);
            }
        }
        Intent saveIntent = new Intent(this, MainActivity.class);
        startActivity(saveIntent, ActivityOptions.makeSceneTransitionAnimation(EditActivity.this).toBundle());
        finish();
    }
}
