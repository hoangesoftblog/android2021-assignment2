package com.example.map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationViewHolder> {
    private List<Location> mListLocation;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private ClickListener clickListener;

    public LocationListAdapter(List<Location> mListLocation, Context context, ClickListener clickListener) {
        this.mListLocation = mListLocation;
        this.context = context;
        this.clickListener = clickListener;
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
        holder.tv_name.setText("Name: "+location.getName());
        holder.tv_owner.setText("Number of participants: "+ (location.getMemberIDs() == null ? 0 : location.getMemberIDs().size()));

        holder.Update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.Update_button.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true, 1200)
                        .create();
                View view = dialogPlus.getHolderView();

                EditText name = view.findViewById(R.id.txtName);
                Button btnUpdate = view.findViewById(R.id.btnUpdate);

                name.setText(location.getName());

                btnUpdate.setOnClickListener((View buttonView) -> {
                    int pos = holder.getAdapterPosition();
                    Location location1 = mListLocation.get(pos);
                    location1.setName(name.getText().toString());
                    clickListener.onUpdateButtonClicked(pos);
                });

                dialogPlus.show();
            }
        });
        holder.Delete_button.setOnClickListener((View v) -> {
                clickListener.onDeleteButtonClicked(holder.getAdapterPosition());
        });
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


        Button Update_button, Delete_button;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_owner = itemView.findViewById(R.id.tv_owner);

            Update_button = (Button)itemView.findViewById(R.id.Update_button);
            Delete_button = (Button)itemView.findViewById(R.id.Delete_button);
        }
    }

    public interface ClickListener {
        void onUpdateButtonClicked(int position);
        void onDeleteButtonClicked(int position);
    }
}
