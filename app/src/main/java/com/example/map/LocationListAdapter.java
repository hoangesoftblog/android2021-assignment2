package com.example.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationViewHolder> {
    private List<Location> mListLocation;

    public LocationListAdapter(List<Location> mListLocation) {
        this.mListLocation = mListLocation;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = mListLocation.get(position);
        if (location == null){
            return;
        }
        holder.tv_name.setText("Name"+location.getName());
        holder.tv_owner.setText("Name"+location.getOwner());
    }

    @Override
    public int getItemCount() {
        if (mListLocation != null){
            return mListLocation.size();
        }
        return 0;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_owner;
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_owner = itemView.findViewById(R.id.tv_owner);
        }
    }
}
