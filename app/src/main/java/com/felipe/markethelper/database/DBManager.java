package com.felipe.markethelper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {

        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertItem(String name, String price, String brand, Integer quantity, Long market_id) {

        ContentValues contentValue = new ContentValues();

        contentValue.put(DatabaseHelper.ITEM_NAME, name);
        contentValue.put(DatabaseHelper.ITEM_PRICE, price);
        contentValue.put(DatabaseHelper.ITEM_BRAND, brand);
        contentValue.put(DatabaseHelper.ITEM_QUANTITY, quantity);
        contentValue.put(DatabaseHelper.ITEM_MARKET_ID, market_id);

        database.insert(DatabaseHelper.TABLE_NAME_ITEM, null, contentValue);
    }

    public Long insertMarket(String name, String date) {

        ContentValues contentValue = new ContentValues();

        contentValue.put(DatabaseHelper.MARKET_NAME, name);
        contentValue.put(DatabaseHelper.MARKET_DATE, date);

        return database.insert(DatabaseHelper.TABLE_NAME_MARKET, null, contentValue);
    }

    public Cursor fetchItems(String marketId) {

        String[] columns = new String[] { DatabaseHelper.ITEM_ID, DatabaseHelper.ITEM_NAME, DatabaseHelper.ITEM_PRICE, DatabaseHelper.ITEM_BRAND, DatabaseHelper.ITEM_QUANTITY };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ITEM, columns, DatabaseHelper.ITEM_MARKET_ID + " = " + marketId, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor fetchMarkets() {

        String[] columns = new String[] { DatabaseHelper.MARKET_ID, DatabaseHelper.MARKET_NAME, DatabaseHelper.MARKET_DATE };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_MARKET, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public int updateItem(long _id, String name, String price, String brand, Integer quantity) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.ITEM_NAME, name);
        contentValues.put(DatabaseHelper.ITEM_PRICE, price);
        contentValues.put(DatabaseHelper.ITEM_BRAND, brand);
        contentValues.put(DatabaseHelper.ITEM_QUANTITY, quantity);

        int i = database.update(DatabaseHelper.TABLE_NAME_ITEM, contentValues, DatabaseHelper.ITEM_ID + " = " + _id, null);

        return i;
    }

    public int updateItemQuantity(long _id, Integer quantity) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.ITEM_QUANTITY, quantity);

        int i = database.update(DatabaseHelper.TABLE_NAME_ITEM, contentValues, DatabaseHelper.ITEM_ID + " = " + _id, null);

        return i;
    }

    public void deleteItem(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME_ITEM, DatabaseHelper.ITEM_ID + "=" + _id, null);
    }

    public void deleteMarket(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME_MARKET, DatabaseHelper.MARKET_ID + "=" + _id, null);
    }
}
