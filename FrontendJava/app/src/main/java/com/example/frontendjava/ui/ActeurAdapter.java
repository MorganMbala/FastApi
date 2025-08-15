package com.example.frontendjava.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontendjava.R;
import com.example.frontendjava.model.Acteur;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ActeurAdapter extends RecyclerView.Adapter<ActeurAdapter.ActeurViewHolder> {

    public interface OnActeurInteractionListener {
        void onEdit(Acteur acteur);
        void onDelete(Acteur acteur);
    }

    private final List<Acteur> data = new ArrayList<>();
    private final List<Acteur> full = new ArrayList<>();
    private OnActeurInteractionListener listener;

    public void setListener(OnActeurInteractionListener l) { this.listener = l; }

    public void setData(List<Acteur> nouveaux) {
        full.clear();
        if (nouveaux != null) full.addAll(nouveaux);
        applyFilterInternal(currentQuery);
    }

    private String currentQuery = "";

    public void filter(String query) {
        if (query == null) query = "";
        currentQuery = query.trim().toLowerCase();
        applyFilterInternal(currentQuery);
    }

    private void applyFilterInternal(String q) {
        List<Acteur> newList;
        if (q.isEmpty()) {
            newList = new ArrayList<>(full);
        } else {
            newList = new ArrayList<>();
            for (Acteur a : full) {
                if (String.valueOf(a.getId()).contains(q) ||
                        (a.getName() != null && a.getName().toLowerCase().contains(q)) ||
                        (a.getBio() != null && a.getBio().toLowerCase().contains(q))) {
                    newList.add(a);
                }
            }
        }
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return data.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return data.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }
            @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Acteur o = data.get(oldItemPosition); Acteur n = newList.get(newItemPosition);
                return o.getName().equals(n.getName()) && o.getBio().equals(n.getBio()) && ((o.getPicture()==null?"":o.getPicture()).equals(n.getPicture()==null?"":n.getPicture()));
            }
        });
        data.clear();
        data.addAll(newList);
        diff.dispatchUpdatesTo(this);
    }

    public Acteur getItem(int position){ return data.get(position); }

    @Override
    public long getItemId(int position) { return data.get(position).getId(); }

    public ActeurAdapter(){ setHasStableIds(true); }

    @NonNull
    @Override
    public ActeurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acteur, parent, false);
        return new ActeurViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActeurViewHolder holder, int position) {
        Acteur a = data.get(position);
        holder.textName.setText(a.getName());
        holder.textBio.setText(a.getBio());
        // Alternating neutral backgrounds (no violet)
        int bgRes = (position % 2 == 0) ? R.color.gray200 : R.color.gray100;
        holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.getContext(), bgRes));
        // Load image
        String url = a.getPicture();
        if (url == null || url.trim().isEmpty()) {
            holder.imagePicture.setImageResource(R.drawable.placeholder_actor);
        } else {
            Glide.with(holder.imagePicture.getContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder_actor)
                    .error(R.drawable.placeholder_actor)
                    .centerCrop()
                    .into(holder.imagePicture);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(a);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onDelete(a);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ActeurViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textBio;
        ImageView imagePicture;
        MaterialCardView card;
        ActeurViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView; // root
            textName = itemView.findViewById(R.id.textName);
            textBio = itemView.findViewById(R.id.textBio);
            imagePicture = itemView.findViewById(R.id.imagePicture);
        }
    }
}
