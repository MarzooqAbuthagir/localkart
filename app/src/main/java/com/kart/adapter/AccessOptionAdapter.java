package com.kart.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kart.R;
import com.kart.model.AccessOptions;

import java.util.List;

public class AccessOptionAdapter extends RecyclerView.Adapter<AccessOptionAdapter.ViewHolder> {
    private Context con;
    private List<AccessOptions> arrayList;

    public AccessOptionAdapter(Context context, List<AccessOptions> accessOptionsList) {
        super();
        con = context;
        arrayList = accessOptionsList;
    }

    @NonNull
    @Override
    public AccessOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.access_option_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessOptionAdapter.ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.layMain.setBackgroundColor(con.getResources().getColor(R.color.light_pink));
        } else {
            holder.layMain.setBackgroundColor(con.getResources().getColor(R.color.white));
        }
        holder.tvAccessKey.setText(arrayList.get(position).getKey());
        holder.tvAccessValue.setText(arrayList.get(position).getValue());

        holder.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (arrayList.get(position).getKey()) {
                    case "Website":
                    case "Facebook":
                    case "Digital VCard":
                        Uri uri = Uri.parse("googlechrome://navigate?url=" + arrayList.get(position).getValue());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            con.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            con.startActivity(intent);
                        }
                        break;

                    case "WhatsApp":
                        String url = "https://api.whatsapp.com/send?phone=+91" + arrayList.get(position).getValue();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        con.startActivity(i);
                        break;

                    case "Phone":
                    case "Mobile":
                    case "Alternate Number":
                    case "COD":
                        String telPhone = "tel:" + arrayList.get(position).getValue();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(telPhone));
                        con.startActivity(callIntent);
                        break;

                    case "Email":
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                                    Uri.fromParts("mailto", arrayList.get(position).getValue(), null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            con.startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
                        } catch (android.content.ActivityNotFoundException e) {
                            System.out.println("There is no email client installed.");
                        }
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layMain;
        public TextView tvAccessKey;
        public TextView tvAccessValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layMain = itemView.findViewById(R.id.lay_main);
            tvAccessKey = itemView.findViewById(R.id.tv_access_key);
            tvAccessValue = itemView.findViewById(R.id.tv_access_value);
        }
    }
}
