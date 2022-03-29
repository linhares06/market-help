package com.felipe.markethelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.felipe.markethelper.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<Item> {

    private Button deleteButton;
    private Button increaseButton;
    private Button decreaseButton;

    private DeleteButtonListener deleteButtonListener;
    private QuantityButtonListener quantityButtonListener;

    public ItemsAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Item item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            deleteButton = (Button) convertView.findViewById(R.id.buttonDelete);
            increaseButton = (Button) convertView.findViewById(R.id.buttonAddQuantity);
            decreaseButton = (Button) convertView.findViewById(R.id.buttonDecreaseQuantity);
        }

        TextView textViewId = (TextView) convertView.findViewById(R.id.itemId);
        TextView textViewName = (TextView) convertView.findViewById(R.id.itemName);
        TextView textViewPrice = (TextView) convertView.findViewById(R.id.itemPrice);
        TextView textViewBrand = (TextView) convertView.findViewById(R.id.itemBrand);
        TextView textViewQuantity = (TextView) convertView.findViewById(R.id.itemQuantity);
        TextView textViewTotal = (TextView) convertView.findViewById(R.id.total);
        TextView textViewLowerPrice = (TextView) convertView.findViewById(R.id.itemLowerPrice);
        TextView textViewLowerPriceMarket = (TextView) convertView.findViewById(R.id.itemLowerPriceMarketName);
        TextView textViewLowerPriceDate = (TextView) convertView.findViewById(R.id.itemLowerPriceDate);

        textViewId.setText(Long.toString(item.id));
        textViewName.setText(item.name);
        textViewPrice.setText(item.price.toString());
        textViewBrand.setText(item.brand);
        textViewQuantity.setText(Integer.toString(item.quantity));
        textViewTotal.setText(item.total.toString());
        textViewLowerPrice.setText(item.lowPrice);
        textViewLowerPriceMarket.setText(item.market);
        textViewLowerPriceDate.setText(item.date);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View parentView = (View)view.getParent().getParent();

                TextView itemIdTextView = parentView.findViewById(R.id.itemId);
                Long itemId = Long.parseLong(itemIdTextView.getText().toString());

                if (deleteButtonListener != null) {
                    deleteButtonListener.onDeleteButtonClickListener(itemId);
                }
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                increaseOrDecreaseQuantity(view, true);
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                increaseOrDecreaseQuantity(view, false);
            }
        });

        return convertView;
    }

    private void increaseOrDecreaseQuantity(View view, Boolean increase) {

        View parentView = (View)view.getParent().getParent();

        TextView itemIdTextView = parentView.findViewById(R.id.itemId);
        TextView quantityTextView = parentView.findViewById(R.id.itemQuantity);

        Long itemId = Long.parseLong(itemIdTextView.getText().toString());
        Integer quantity = Integer.parseInt(quantityTextView.getText().toString());

        if (increase) {
            quantity++;
        } else {
            quantity--;
        }

        if (quantityButtonListener != null) {
            quantityButtonListener.onQuantityButtonClickListener(itemId, quantity);
        }
    }

    public interface DeleteButtonListener {
        void onDeleteButtonClickListener(Long itemId);
    }

    public interface QuantityButtonListener {
        void onQuantityButtonClickListener(Long itemId, Integer quantity);
    }

    public void setDeleteButtonListener(DeleteButtonListener listener) {
        this.deleteButtonListener = listener;
    }

    public void setQuantityButtonListener(QuantityButtonListener listener) {
        this.quantityButtonListener = listener;
    }
}
