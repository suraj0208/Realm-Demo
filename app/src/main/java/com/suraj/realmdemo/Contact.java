package com.suraj.realmdemo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by suraj on 5/2/17.
 */
public class Contact {
    String name;
    Drawable picture;

    public Contact(String name,Drawable picture) {
        this.name = name;
        this.picture = picture;
    }

    public Drawable getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }
}
