package com.example.cardstackview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cardstackview.databinding.CardBinding;
import java.util.List;

public class Card_Adapter  extends RecyclerView.Adapter<Card_Adapter.myViewHolder> {
    List<card> cardList;


    public Card_Adapter(List<card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public Card_Adapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li=(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CardBinding binding=CardBinding.inflate(li);

        return new myViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Card_Adapter.myViewHolder holder, int position) {
        card cardItem=cardList.get(position);
        holder.binding.content.setText(cardItem.getContent());
        holder.binding.image.setImageDrawable(cardItem.getImage());
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
