package com.example.aqil.angkotcustomerside;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AngkotAdapter extends RecyclerView.Adapter<AngkotAdapter.ViewHolder> {

    ArrayList<Angkot> listAngkot = new ArrayList<>();

    public AngkotAdapter(Context context) {
        this.context = context;
    }

    Context context;


    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_angkot_layout, parent, false);
        return new ViewHolder(itemRow);
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.nomorAngkot.setText(listAngkot.get(position).getNomorAngkot());
        holder.tujuan.setText(listAngkot.get(position).getTujuan());
    }

    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + listAngkot.size());
        return listAngkot.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView nomorAngkot, tujuan;


        public ViewHolder(View itemView) {
            super(itemView);
            nomorAngkot = (TextView) itemView.findViewById(R.id.nomorAngkot);
            tujuan = (TextView) itemView.findViewById(R.id.tujuan);

        }
    }

    public ArrayList<Angkot> getListAngkot() {
        return listAngkot;
    }

    public void setListAngkot(ArrayList<Angkot> listAngkot) {
        this.listAngkot = listAngkot;
    }
}
