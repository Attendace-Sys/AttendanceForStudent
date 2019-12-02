package com.project.attendanceforstudent.DBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper {
    public static  final String DATABASE_NAME =  "SaveBitmap.db";
    public static  final String TABLE_NAME =  "Gallery";
    public static  final String ID_NAME =  "ID";
    public static  final String COL_1 =  "NAME";
    public static  final String COL_2 =  "DATA";
    public static  final int DB_VER =  1;

    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DB_VER);
    }

    public int addBitmap(String name, byte[] image) throws SQLiteException
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,name);
        contentValues.put(COL_2,image);
        long result = database.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
                return -1;
            }
        else return 1;
    }
    public int addStudentImage(String studentID, byte[] image) throws SQLiteException
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,studentID);
        contentValues.put(COL_2,image);
        long result = database.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return -1;
        }
        else return 1;
    }
    public byte[] getBitmapByName(String name)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selected = {COL_1,COL_2};
        qb.setTables(TABLE_NAME);
        Cursor cursor = qb.query(database,selected,"Name = ?",new String[] {name},null,null,null);
        byte[] result = null;
        if (cursor.moveToFirst())
        {
            do{
                result = cursor.getBlob(cursor.getColumnIndex(COL_2));
            } while(cursor.moveToNext());
        }
        return result;
    }
}