package vn.edu.tdmu.fit.g1.hackathonandroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NguyenHuyLinh on 3/26/2016.
 */
public class Database extends SQLiteOpenHelper {
    public Database(Context context){
        super(context,"hackathon.sqlite",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    public Cursor DB_GetData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public void DB_QueryData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
