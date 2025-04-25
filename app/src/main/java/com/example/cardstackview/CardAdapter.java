package com.example.cardstackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cardstackview.databinding.CardBinding;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.myViewHolder> {
    List<CardActivity> cardList;


    public CardAdapter(List<CardActivity> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li=(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CardBinding binding=CardBinding.inflate(li);

        return new myViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.myViewHolder holder, int position) {
        CardActivity cardItem=cardList.get(position);
        //holder.binding.content.setText(cardItem.getContent());
        //holder.binding.image.setImageDrawable(cardItem.getImage());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        CardBinding binding;
        public myViewHolder(@NonNull CardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
