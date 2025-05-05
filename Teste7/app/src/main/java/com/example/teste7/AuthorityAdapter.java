package com.example.teste7;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AuthorityAdapter extends RecyclerView.Adapter<AuthorityAdapter.ViewHolder> {

    private List<Authority> authorityList;
    private Context context;

    public AuthorityAdapter(List<Authority> authorityList, Context context) {
        this.authorityList = authorityList;
        this.context = context;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.authorityIcon);
            name = itemView.findViewById(R.id.authorityName);
        }
    }

    @Override
    public AuthorityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_authority, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuthorityAdapter.ViewHolder holder, int position) {
        Authority authority = authorityList.get(position);
        holder.icon.setImageResource(authority.getIconResId());
        holder.name.setText(authority.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", authority.getName());
            intent.putExtra("icon", authority.getIconResId());
            intent.putExtra("phone", authority.getPhone());
            intent.putExtra("description", authority.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return authorityList.size();
    }
}

