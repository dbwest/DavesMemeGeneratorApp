package com.example.west.doge2;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

public class MemeHelper {
    static public String formatMemeText(String meme, String topText, String bottomText) {
        HashMap<String, String> specialCharMap = new HashMap<>();
        specialCharMap.put("\\?", "~q");
        specialCharMap.put("%", "~p");
        specialCharMap.put("#", "~h");
        specialCharMap.put("\\/", "~s");
        specialCharMap.put("\"", "''");
        specialCharMap.put(" ", "-");
        specialCharMap.put("-", "--");
        specialCharMap.put("_", "__");

        for(Map.Entry<String, String> entry : specialCharMap.entrySet())
        {
            meme = meme.replaceAll(entry.getKey(), entry.getValue());
            topText = topText.replaceAll(entry.getKey(), entry.getValue());
            bottomText = bottomText.replaceAll(entry.getKey(), entry.getValue());
        }

        return meme + "/" + topText + "/" + bottomText;
    }

    @NonNull
    static public String getURLString(ImageView mImageView) {
        return "https://memegen.link/wonka/you-are/a-meme.jpg?height="
                + mImageView.getHeight()
                + "&width="
                + mImageView.getWidth();
    }

    @NonNull
    static public String getURLString(String meme, String topText, String bottomText, ImageView mImageView) {

        String formattedMemeText = MemeHelper.formatMemeText(meme, topText, bottomText);

        return "https://memegen.link/"
                + formattedMemeText
                + ".jpg?height=" + mImageView.getHeight()
                + "&width=" + mImageView.getWidth();
    }
}
