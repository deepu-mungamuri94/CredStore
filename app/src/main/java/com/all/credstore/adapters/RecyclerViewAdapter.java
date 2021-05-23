package com.all.credstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.all.credstore.models.Credentials;
import com.all.credstore.R;
import com.all.credstore.utils.Constants;
import com.all.credstore.utils.Util;
import com.all.credstore.activities.ViewCredentialActivity;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Credentials> credentials;
    public RecyclerViewAdapter(List<Credentials> credentials) {
        this.credentials = credentials;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Credentials model = credentials.get(position);
        holder.setData(model);
    }

    @Override
    public int getItemCount() {
        return credentials.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView lockImageView;
        private TextView tagTextView;
        private TextView urlTextView;
        private TextView commentTextView;
        private int recordId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lockImageView = itemView.findViewById(R.id.item_img);
            tagTextView = itemView.findViewById(R.id.item_text_tag);
            urlTextView = itemView.findViewById(R.id.item_text_url);
            commentTextView = itemView.findViewById(R.id.item_text_comment);
            itemView.setOnClickListener(this);
        }

        public void setData(Credentials model) {
            lockImageView.setImageResource(model.getImageResourceId());
            tagTextView.setText((Util.isEmpty(model.getTag()) ? "-" : model.getTag()));
            urlTextView.setText((Util.isEmpty(model.getUrl()) ? "-" : model.getUrl()));
            commentTextView.setText(Util.isEmpty(model.getComment()) ? "-" : model.getComment().replaceAll("\n", ", "));
            this.recordId = model.getId();
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            Intent detailPage = new Intent(context, ViewCredentialActivity.class);
            detailPage.putExtra(Constants.RECORD_ID_REQUEST_ATTR_NAME, this.recordId);
            context.startActivity(detailPage);
        }
    }
}
