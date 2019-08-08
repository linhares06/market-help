package com.felipe.markethelper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME_ITEM = "ITEM";
    public static final String TABLE_NAME_MARKET = "MARKET";

    // Table Item columns
    public static final String ITEM_ID = "_id";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_PRICE = "price";
    public static final String ITEM_BRAND = "brand";
    public static final String ITEM_QUANTITY = "quantity";
    public static final String ITEM_MARKET_ID = "market_id";

    // Table Market columns
    public static final String MARKET_ID = "_id";
    public static final String MARKET_NAME = "name";
    public static final String MARKET_DATE = "date";

    // Database Information
    static final String DB_NAME = "MARKET_HELPER.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table ITEM query
    public static final String CREATE_TABLE_ITEM = "create table " + TABLE_NAME_ITEM + "("
            + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ITEM_NAME + " TEXT NOT NULL, "
            + ITEM_PRICE + " TEXT, "
            + ITEM_BRAND + " TEXT, "
            + ITEM_QUANTITY + " INTEGER DEFAULT 1 NOT NULL, "
            + ITEM_MARKET_ID + " INTEGER NOT NULL, "
            + "CONSTRAINT fk_items FOREIGN KEY (" + ITEM_MARKET_ID + ") REFERENCES " + TABLE_NAME_MARKET + "(" + MARKET_ID + ") ON DELETE CASCADE);";

    // Creating table MARKET query
    public static final String CREATE_TABLE_MARKET = "create table " + TABLE_NAME_MARKET + "("
            + MARKET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MARKET_NAME + " TEXT NOT NULL, "
            + MARKET_DATE + " TEXT NOT NULL );";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MARKET);
        db.execSQL(CREATE_TABLE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MARKET);
        onCreate(db);
    }
}