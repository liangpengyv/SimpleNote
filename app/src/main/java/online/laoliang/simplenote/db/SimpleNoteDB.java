package online.laoliang.simplenote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import online.laoliang.simplenote.model.Note;

/**
 * 封装常用数据库操作
 * Created by liang on 12/15.
 */
public class SimpleNoteDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "simple_note";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static SimpleNoteDB simpleNoteDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     *
     * @param context
     */
    private SimpleNoteDB(Context context) {
        SimpleNoteOpenHelper simpleNoteOpenHelper = new SimpleNoteOpenHelper(context, DB_NAME, null, VERSION);
        db = simpleNoteOpenHelper.getWritableDatabase();
    }

    /**
     * 获取SimpleNoteDB的实例
     *
     * @param context
     * @return
     */
    public synchronized static SimpleNoteDB getInstance(Context context) {
        if (simpleNoteDB == null) {
            simpleNoteDB = new SimpleNoteDB(context);
        }
        return simpleNoteDB;
    }

    /**
     * 新建便签（insert）
     *
     * @param note
     */
    public void insertNote(Note note) {
        if (note != null) {
            ContentValues values = new ContentValues();
            values.put("text_content", note.getTextContent());
            values.put("grouping", note.getGrouping());
            db.insert("Note", null, values);
        }
    }

    /**
     * 删除便签（delete）
     *
     * @param text_content
     */
    public void deleteNote(String text_content) {
        if (text_content != null) {
            db.delete("Note", "text_content = ?", new String[]{text_content});
        } else {
            db.delete("Note", "grouping = ?", new String[]{"回收站"});
        }

    }

    /**
     * 修改便签内容（update）
     *
     * @param new_text_content
     * @param old_text_content
     */
    public void updateNote(String new_text_content, String old_text_content) {
        ContentValues values = new ContentValues();
        values.put("text_content", new_text_content);
        db.update("Note", values, "text_content = ?", new String[]{old_text_content});
    }

    /**
     * 修改便签分组（update）
     *
     * @param text_content
     * @param new_grougping
     */
    public void updateNoteGrouping(String text_content, String new_grougping) {
        ContentValues values = new ContentValues();
        values.put("grouping", new_grougping);
        db.update("Note", values, "text_content = ?", new String[]{text_content});
    }

    /**
     * 展示便签（select）
     *
     * @param grouping
     * @return
     */
    public List<String> selectNote(String grouping, String orderby) {
        List<String> list = new ArrayList<String>();
        Cursor cursor;
        if (grouping.equals("全部便签")) {
            cursor = db.query("Note", new String[]{"text_content"}, "grouping != ?", new String[]{"回收站"}, null, null, orderby);
        } else {
            cursor = db.query("Note", new String[]{"text_content"}, "grouping = ?", new String[]{grouping}, null, null, orderby);
        }
        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                list.add(i, cursor.getString(cursor.getColumnIndex("text_content")));
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

}
