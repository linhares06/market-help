package com.felipe.markethelper;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.felipe.markethelper.adapter.Item;
import com.felipe.markethelper.adapter.Market;
import com.felipe.markethelper.adapter.MarketsAdapter;
import com.felipe.markethelper.database.DBManager;
import com.felipe.markethelper.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MarketPurchaseActivity extends AppCompatActivity implements MarketsAdapter.DeleteButtonListener, MarketsAdapter.SelectButtonListener {

    public static final String MARKET_ID = "MARKET_ID";
    public static final String PATTERN_DD_MM_YYYY = "dd/MM/yyyy";

    private DBManager dbManager;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_purchase);

        ArrayList<Market> marketArray = retrieveMarketList();
        MarketsAdapter adapter = new MarketsAdapter(this, marketArray);
        adapter.setDeleteButtonListener(MarketPurchaseActivity.this);
        adapter.setSelectButtonListener(MarketPurchaseActivity.this);

        listView = (ListView) findViewById(R.id.listViewMarkets);
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View popupView = inflatePopupAddOrUpdate(view);

                Button buttonAddMarket = (Button) popupView.findViewById(R.id.buttonAddItem);
                buttonAddMarket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText name = (EditText)popupView.findViewById(R.id.editTextName);

                        if (validateFields(name)) {

                            Long marketId = insertMarket(name);
                            Toast.makeText(getApplicationContext(), R.string.regitered_market, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplication(), MainActivity.class);

                            intent.putExtra(MARKET_ID, Long.valueOf(marketId).toString());
                            startActivity(intent);

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.insert_market_name, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private ArrayList<Market> retrieveMarketList() {

        dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursor = dbManager.fetchMarkets();

        ArrayList<Market> marketList = new ArrayList<Market>();

        while(!cursor.isAfterLast()) {

            marketList.add(new Market(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.MARKET_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.MARKET_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.MARKET_DATE))));

            cursor.moveToNext();
        }

        return marketList;
    }

    private Long insertMarket(EditText nameEditText) {

        String name = nameEditText.getText().toString();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_DD_MM_YYYY);
        String date = simpleDateFormat.format(c);

        return  dbManager.insertMarket(name, date);
    }

    private boolean validateFields(EditText textValueName) {

        if (textValueName.getText().toString().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private View inflatePopupAddOrUpdate (View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_market, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupView;
    }

    private void deleteMarket(long _id) {
        dbManager.deleteMarket(_id);
    }

    @Override
    public void onDeleteButtonClickListener(Long marketId) {
        deleteMarket(marketId);
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onSelectButtonClickListener(Long marketId) {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        intent.putExtra(MARKET_ID, Long.valueOf(marketId).toString());
        startActivity(intent);
    }
}
