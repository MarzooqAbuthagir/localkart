package com.kart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kart.R;
import com.kart.model.AccessOptions;

import java.util.ArrayList;

public class SubcriptionAdapter extends ArrayAdapter<AccessOptions> {
    private Context con;

    public SubcriptionAdapter(Context context, ArrayList<AccessOptions> accessOptionListValue) {
        super(context, 0, accessOptionListValue);
        con = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.subscription_list_item, parent, false);
        }
        final AccessOptions accessOptions = getItem(position);
        TextView tvValue = listitemView.findViewById(R.id.tv_value);
        TextView tvKey = listitemView.findViewById(R.id.tv_key);
        assert accessOptions != null;
        tvValue.setText(accessOptions.getValue());
        tvKey.setText(accessOptions.getKey());
        return listitemView;
    }
}
