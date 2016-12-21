package online.laoliang.simplenote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库
 * Created by liang on 12/15.
 */
public class SimpleNoteOpenHelper extends SQLiteOpenHelper {

    /**
     * 创建note表
     */
    public static final String CREATE_NOTE = "create table Note (" +
            "id integer primary key autoincrement, " +
            "text_content text, " +
            "grouping text)";

    public SimpleNoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建Note表
        sqLiteDatabase.execSQL(CREATE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
