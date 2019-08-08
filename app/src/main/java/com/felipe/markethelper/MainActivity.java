package com.felipe.markethelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.felipe.markethelper.adapter.Item;
import com.felipe.markethelper.adapter.ItemsAdapter;
import com.felipe.markethelper.database.DBManager;
import com.felipe.markethelper.database.DatabaseHelper;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemsAdapter.DeleteButtonListener, ItemsAdapter.QuantityButtonListener {

    private DBManager dbManager;
    private ListView listView;
    private BigDecimal sum = BigDecimal.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String marketId = intent.getStringExtra(MarketPurchaseActivity.MARKET_ID);

        ArrayList<Item> itemArray = retrieveItemList(marketId);
        ItemsAdapter adapter = new ItemsAdapter(this, itemArray);
        //TODO: entender pq a MainActivity funciona quando o valor esperado é uma interface específica
        adapter.setDeleteButtonListener(MainActivity.this);
        adapter.setQuantityButtonListener(MainActivity.this);

        listView = (ListView) findViewById(R.id.listViewItems);
        listView.setAdapter(adapter);

        TextView textViewTotal = findViewById(R.id.textViewTotal);
        textViewTotal.setText("Total: " + sum.toString());

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView itemId = view.findViewById(R.id.itemId);
                TextView itemName = view.findViewById(R.id.itemName);
                TextView itemPrice = view.findViewById(R.id.itemPrice);
                TextView itemBrand = view.findViewById(R.id.itemBrand);
                TextView itemQuantity = view.findViewById(R.id.itemQuantity);

                final View popupView = inflatePopupAddOrUpdate(view);

                EditText editTextId = (EditText)popupView.findViewById(R.id.editTextId);
                EditText editTextName = (EditText)popupView.findViewById(R.id.editTextName);
                EditText editTextPrice = (EditText)popupView.findViewById(R.id.editTextPrice);
                EditText editTextBrand = (EditText)popupView.findViewById(R.id.editTextBrand);
                EditText editTextQuantity = (EditText)popupView.findViewById(R.id.editTextQuantity);

                editTextId.setText(itemId.getText());
                editTextName.setText(itemName.getText());
                editTextPrice.setText(itemPrice.getText());
                editTextBrand.setText(itemBrand.getText());
                editTextQuantity.setText(itemQuantity.getText());

                Button buttonAddItem = (Button) popupView.findViewById(R.id.buttonAddItem);
                buttonAddItem.setText(R.string.edit);
                buttonAddItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText id = (EditText)popupView.findViewById(R.id.editTextId);
                        EditText name = (EditText)popupView.findViewById(R.id.editTextName);
                        EditText price = (EditText)popupView.findViewById(R.id.editTextPrice);
                        EditText brand = (EditText)popupView.findViewById(R.id.editTextBrand);
                        EditText quantity = (EditText)popupView.findViewById(R.id.editTextQuantity);

                        if (validateFields(name)) {

                            updateItem(id, name, price, brand, quantity);
                            Toast.makeText(getApplicationContext(), R.string.edited_item, Toast.LENGTH_SHORT).show();

                            finish();
                            startActivity(getIntent());

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.insert_item_name, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                return true;
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View popupView = inflatePopupAddOrUpdate(view);

                Button buttonAddItem = (Button) popupView.findViewById(R.id.buttonAddItem);
                buttonAddItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText name = (EditText)popupView.findViewById(R.id.editTextName);
                        EditText price = (EditText)popupView.findViewById(R.id.editTextPrice);
                        EditText brand = (EditText)popupView.findViewById(R.id.editTextBrand);
                        EditText quantity = (EditText)popupView.findViewById(R.id.editTextQuantity);

                        if (validateFields(name)) {

                            insertItem(name, price, brand, quantity, Long.valueOf(marketId));
                            Toast.makeText(getApplicationContext(), R.string.regitered_item, Toast.LENGTH_SHORT).show();
                            //TODO: Entender e alterar como dar refresh na lista sem precisar chamar a activity novamente
                            finish();
                            startActivity(getIntent());

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.insert_item_name, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        FloatingActionButton fabOk = findViewById(R.id.fabOk);
        fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplication(), MarketPurchaseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void deleteItem(long _id) {
        dbManager.deleteItem(_id);
    }

    private void updateItemQuantity(long _id, Integer quantity) {
        dbManager.updateItemQuantity(_id, quantity);
    }

    private View inflatePopupAddOrUpdate (View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_item, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupView;
    }

    private ArrayList<Item> retrieveItemList(String marketId) {

        dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursor = dbManager.fetchItems(marketId);

        ArrayList<Item> itemList = new ArrayList<Item>();

        while(!cursor.isAfterLast()) {

            Sum(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_PRICE)), cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_QUANTITY)));

            itemList.add(new Item(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ITEM_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_PRICE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_BRAND)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_QUANTITY))));

            cursor.moveToNext();
        }

        return itemList;
    }

    private void insertItem(EditText nameEditText, EditText priceEditText, EditText brandEditText, EditText quantityEditText, Long marketId) {

        String name = nameEditText.getText().toString();
        String price = priceEditText.getText().toString();
        if (price.equals("")) {
            price = "0";
        }
        String brand = brandEditText.getText().toString();
        Integer quantity = 1;
        if (!quantityEditText.getText().toString().equals("")) {
            quantity = Integer.parseInt(quantityEditText.getText().toString());
        }
        dbManager.insertItem(name, price, brand, quantity, marketId);
    }

    private void updateItem(EditText idEditText, EditText nameEditText, EditText priceEditText, EditText brandEditText, EditText quantityEditText) {

        Long id = Long.parseLong(idEditText.getText().toString());
        String name = nameEditText.getText().toString();
        String price = priceEditText.getText().toString();
        if (price.equals("")) {
            price = "0";
        }
        String brand = brandEditText.getText().toString();
        Integer quantity = Integer.parseInt(quantityEditText.getText().toString());
        if (quantity.equals("")) {
            quantity = 1;
        }

        dbManager.updateItem(id, name, price, brand, quantity);
    }

    private boolean validateFields(EditText textValueName) {

        if (textValueName.getText().toString().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private BigDecimal Sum(String price, Integer quantity) {

        BigDecimal value = BigDecimal.valueOf(quantity).multiply(new BigDecimal(price));
        sum = sum.add(value);

        return sum;
    }

    @Override
    public void onDeleteButtonClickListener(Long itemId) {
        deleteItem(itemId);
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onQuantityButtonClickListener(Long itemId, Integer quantity) {

        if (!(quantity == 0)) {

            updateItemQuantity(itemId, quantity);
            finish();
            startActivity(getIntent());
        }
    }
}
