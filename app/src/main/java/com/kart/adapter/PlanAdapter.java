package com.kart.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kart.R;
import com.kart.model.AccessOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    Context context;
    private final ArrayList<HashMap<String, String>> list;
    private JSONObject mainObj = new JSONObject();
    List<AccessOptions> accessOptions = new ArrayList<>();
    int estimateSize;

    public PlanAdapter(Context context, ArrayList<HashMap<String, String>> list, JSONObject mainObj, List<AccessOptions> accessOptionsList) {
        super();
        this.context = context;
        this.list = list;
        this.mainObj = mainObj;
        accessOptions = accessOptionsList;
        estimateSize = list.get(0).size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.plans_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            for (int k = 0; k < mainObj.getJSONArray("Headers").length(); k++) {
                LinearLayout parent = new LinearLayout(context);
                parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                parent.setOrientation(LinearLayout.HORIZONTAL);
                parent.setGravity(Gravity.CENTER);
                parent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                LinearLayout linearLayout = new LinearLayout(context);
                if (k == 0) {
                    if (estimateSize == 2) {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(600, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else if (estimateSize == 3) {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else if (estimateSize > 3) {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(450, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    TextView myTextView = new TextView(context);
                    TextView myTextViewDesc = new TextView(context);

                    Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_bold);
                    myTextView.setTypeface(typeface);
                    myTextView.setTextSize(14);

                    Typeface typeface2 = ResourcesCompat.getFont(context, R.font.roboto_regular);
                    myTextViewDesc.setTypeface(typeface2);
                    myTextViewDesc.setTextSize(12);

                    if (position == 0) {
                        myTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        myTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        holder.rootLayout.setBackgroundResource(R.drawable.curve);

                        myTextViewDesc.setVisibility(View.GONE);
                    } else {
                        myTextViewDesc.setVisibility(View.VISIBLE);
                    }

                    if (position == 0) {
                        myTextView.setPadding(25, 10, 5, 10);
                        myTextViewDesc.setPadding(25, 5, 5, 5);
                        myTextView.setText(list.get(position).get(mainObj.getJSONArray("Headers").getString(k)));
                    } else {
                        myTextView.setPadding(25, 5, 5, 0);
                        myTextViewDesc.setPadding(25, 0, 5, 5);
                        String keyHeader = "";
                        String description = "";

                        String key = list.get(position).get(mainObj.getJSONArray("Headers").getString(k));

                        for (AccessOptions accessOptions : accessOptions) {
                            if (accessOptions.getKey().equalsIgnoreCase(key)) {
                                keyHeader = accessOptions.getKey();
                                description = accessOptions.getValue();
                                break;
                            }
                        }

                        myTextView.setText(keyHeader);
                        myTextViewDesc.setText(description);
                    }

                    linearLayout.addView(myTextView);
                    linearLayout.addView(myTextViewDesc);

                } else {
                    if (estimateSize == 2) {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else if (estimateSize == 3) {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(250, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else if (estimateSize > 3) {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(210, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    TextView myTextView = new TextView(context);
                    ImageView imageView = new ImageView(context);

                    myTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_bold);
                    myTextView.setTypeface(typeface);
                    myTextView.setTextSize(14);

                    myTextView.setPadding(5, 5, 5, 5);
                    imageView.setPadding(5, 5, 5, 5);

                    if (position == 0) {
                        myTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        myTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        holder.rootLayout.setBackgroundResource(R.drawable.curve);
                    } else {
                        myTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    }

                    if (list.get(position).get(mainObj.getJSONArray("Headers").getString(k)).equalsIgnoreCase("Yes")) {
                        myTextView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);

                        imageView.setImageResource(R.drawable.ic_baseline_done_24);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    } else if (list.get(position).get(mainObj.getJSONArray("Headers").getString(k)).equalsIgnoreCase("No")) {
                        myTextView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);

                        imageView.setImageResource(R.drawable.ic_baseline_clear_24);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    } else {
                        myTextView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);

                        myTextView.setText(list.get(position).get(mainObj.getJSONArray("Headers").getString(k)));
                    }
                    linearLayout.addView(myTextView);
                    linearLayout.addView(imageView);
                }
                parent.addView(linearLayout);
                holder.rootLayout.addView(parent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
