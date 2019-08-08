package com.felipe.markethelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.felipe.markethelper.R;

import java.util.ArrayList;

public class MarketsAdapter  extends ArrayAdapter<Market> {

    private Button deleteButton;
    private Button selectButton;

    private DeleteButtonListener deleteButtonListener;
    private SelectButtonListener selectButtonListener;

    public MarketsAdapter(Context context, ArrayList<Market> markets) {
        super(context, 0, markets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Market market = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_market, parent, false);

            deleteButton = (Button) convertView.findViewById(R.id.buttonDelete);
            selectButton = (Button) convertView.findViewById(R.id.buttonSelect);
        }

        TextView textViewId = (TextView) convertView.findViewById(R.id.marketId);
        TextView textViewName = (TextView) convertView.findViewById(R.id.marketName);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.marketDate);

        textViewId.setText(Long.toString(market.id));
        textViewName.setText(market.name);
        textViewDate.setText(market.date);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View parentView = (View)view.getParent().getParent();

                TextView itemIdTextView = parentView.findViewById(R.id.marketId);
                Long marketId = Long.parseLong(itemIdTextView.getText().toString());

                if (deleteButtonListener != null) {
                    deleteButtonListener.onDeleteButtonClickListener(marketId);
                }
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View parentView = (View)view.getParent().getParent();

                TextView itemIdTextView = parentView.findViewById(R.id.marketId);
                Long marketId = Long.parseLong(itemIdTextView.getText().toString());

                if (selectButtonListener != null) {
                    selectButtonListener.onSelectButtonClickListener(marketId);
                }
            }
        });

        return convertView;
    }

    public interface DeleteButtonListener {
        void onDeleteButtonClickListener(Long marketId);
    }

    public void setDeleteButtonListener(DeleteButtonListener listener) {
        this.deleteButtonListener = listener;
    }

    public interface SelectButtonListener {
        void onSelectButtonClickListener(Long marketId);
    }

    public void setSelectButtonListener(SelectButtonListener listener) {
        this.selectButtonListener = listener;
    }
}
