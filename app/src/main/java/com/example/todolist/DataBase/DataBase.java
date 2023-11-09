package com.example.todolist.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DataBase extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME = "todoList.db";
    private static final int DB_VERSION = 4;

    // Current Tasks
    private static final String TABLE_NAME_1 = "StickyList";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_UNIQUE_ID = "uniqueId";


    // Completed Tasks
    private static final String TABLE_NAME_2 = "CompletedTasks";
    private static final String COLUMN_ID_COMPLETED = "id";
    private static final String COLUMN_TITLE_COMPLETED = "title";

    // InComplete Tasks
    private static final String TABLE_NAME_3 = "InCompleteTasks";
    private static final String COLUMN_ID_INCOMPLETE = "id";
    private static final String COLUMN_TITLE_INCOMPLETE = "title";


    // due dates for tasks
    private static final String TABLE_NAME_4 = "TasksDueDates";
    private static final String COLUMN_ID_DUE_DATES = "id";

    private static final String COLUMN_UNIQUE_ID_4 = "uniqueId";
    private static final String COLUMN_REQUEST_ID_4 = "requestId";

    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_HOURS = "hours";
    private static final String COLUMN_MINUTES = "minutes";
    private static final String COLUMN_DATES = "dates";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_TYPE = "type";





    public DataBase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query1 = "CREATE TABLE " + TABLE_NAME_1 +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_UNIQUE_ID + " TEXT" +
                "); ";

        String query2 = "CREATE TABLE " + TABLE_NAME_2 +
                " (" + COLUMN_ID_COMPLETED + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                     + COLUMN_TITLE_COMPLETED + " TEXT NOT NULL " +
                "); ";

        String query3 = "CREATE TABLE " + TABLE_NAME_3 +
                " (" + COLUMN_ID_INCOMPLETE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                     + COLUMN_TITLE_INCOMPLETE + " TEXT NOT NULL " +
                "); ";

        String query4 = "CREATE TABLE " + TABLE_NAME_4 +
                " (" + COLUMN_ID_DUE_DATES + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                     + COLUMN_UNIQUE_ID_4 + " TEXT, "
                     + COLUMN_REQUEST_ID_4 + " INTEGER, "
                     + COLUMN_DATES + " INTEGER NOT NULL, "
                     + COLUMN_MONTH + " INTEGER NOT NULL, "
                     + COLUMN_YEAR + " INTEGER NOT NULL, "
                     + COLUMN_TIME + " TIME, "
                     + COLUMN_HOURS + " INTEGER NOT NULL, "
                     + COLUMN_MINUTES + " INTEGER NOT NULL, "
                     + COLUMN_TYPE + " TEXT NOT NULL " +
                "); ";

        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_4);
        onCreate(db);
    }

    public void addTask(String title,String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_UNIQUE_ID,uniqueId);


        long result = db.insert(TABLE_NAME_1,null,cv);

        if(result == -1){
            Toast.makeText(context, "Failed to add!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addDueDatesTasks(String uniqueId,int requestId,int hour,int minute,int date,int month,int year,String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_UNIQUE_ID_4,uniqueId);
        cv.put(COLUMN_REQUEST_ID_4,requestId);
        cv.put(COLUMN_DATES,date);
        cv.put(COLUMN_MONTH,month);
        cv.put(COLUMN_YEAR,year);
//        cv.put(COLUMN_TIME,time);
        cv.put(COLUMN_HOURS,hour);
        cv.put(COLUMN_MINUTES,minute);
        cv.put(COLUMN_TYPE,type);

        long result = db.insert(TABLE_NAME_4,null,cv);

        if(result == -1){
            Toast.makeText(context, "Failed to add!", Toast.LENGTH_SHORT).show();
        }
    }


    public void addCompletedTask(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE_COMPLETED,title);

        long result = db.insert(TABLE_NAME_2,null,cv);

        if(result == -1){
            Toast.makeText(context, "Failed to add!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addInCompleteTask(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE_INCOMPLETE,title);

        long result = db.insert(TABLE_NAME_3,null,cv);

        if(result == -1){
            Toast.makeText(context, "Failed to add!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor getUpcomingTask(String uniqueId){
        SQLiteDatabase db = this.getReadableDatabase();

        String [] column = {COLUMN_REQUEST_ID_4,COLUMN_DATES,COLUMN_MONTH,COLUMN_YEAR,COLUMN_HOURS,COLUMN_MINUTES,COLUMN_TYPE};
        String [] arge = {uniqueId};

        Cursor cursor = db.query(
                TABLE_NAME_4,
                column,
                COLUMN_UNIQUE_ID_4 + " = ?",
                arge,
                null,
                null,
                COLUMN_YEAR +" ASC, " + COLUMN_MONTH + " ASC, " + COLUMN_DATES + " ASC",
                "1"
        );

        return  cursor;
    }


    public Cursor getTypeOfDueTasks(String uniqueId){
        SQLiteDatabase db = this.getReadableDatabase();

        String [] column = {COLUMN_UNIQUE_ID_4,COLUMN_REQUEST_ID_4};
        String [] args = {uniqueId};

        Cursor cursor = db.query(
                TABLE_NAME_4,
                column,
                COLUMN_UNIQUE_ID_4 + " = ?",
                args,
                null,
                null,
                null
        );

        return cursor;
    }

    public Cursor readDueTasks(){
        String query = " SELECT * FROM " + TABLE_NAME_4;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public Cursor readTask(){
        String query = "SELECT * FROM " +  TABLE_NAME_1;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public Cursor readCompletedTask(){
        String query = "SELECT * FROM " + TABLE_NAME_2;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

    public Cursor readInCompleteTask(){
        String query = "SELECT * FROM " + TABLE_NAME_3;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

    public void updateDueDate(int date,int month,int year,int requestId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATES,date);
        cv.put(COLUMN_MONTH,month);
        cv.put(COLUMN_YEAR,year);

        String [] whereArgs = {String.valueOf(requestId)};
        db.update(TABLE_NAME_4,cv,COLUMN_REQUEST_ID_4 + " = ?",whereArgs);
    }

    public void updateNote(String id,String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,title);

        long result = db.update(TABLE_NAME_1,cv,COLUMN_ID+"=?",new String[]{id});

        if(result == -1){
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
    }

    public void deleteTasks(String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME_1,COLUMN_UNIQUE_ID + " = ?",new String[]{uniqueId});
        if(result == -1){
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteDueTasks(String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME_4,COLUMN_UNIQUE_ID_4 + " = ?",new String[]{uniqueId});
        if(result == -1){
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
