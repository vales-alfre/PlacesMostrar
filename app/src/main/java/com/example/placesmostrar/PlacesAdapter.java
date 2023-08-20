package com.example.placesmostrar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;


import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private  List<String> lista;
    private  String image;
    private  Context ctx;

    public PlacesAdapter(List<String> lista, String image, Context ctx) {
        this.lista = lista;
        this.image = image;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String placeInfo = lista.get(position);
        holder.Name.setText(placeInfo);
        Picasso.get().load(image).into(holder.Image);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;
        TextView Name;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.Image);
            Name = itemView.findViewById(R.id.Name);
        }
    }
}
