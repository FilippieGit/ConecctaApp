package com.example.cardstackview;

import android.graphics.drawable.Drawable;

public class CardActivity {
    String content;
    Drawable image;

    public CardActivity(Drawable image, String content) {
        this.image = image;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Drawable getImage() {
        return image;
    }
}
