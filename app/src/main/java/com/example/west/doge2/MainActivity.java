package com.example.west.doge2;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1232141512;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner mMeme = (Spinner) findViewById(R.id.theSpinner);

        askForPermission();

        populateMemeChoices(mMeme);

        String url = getURLString();
        makeMemeImage(url);
    }

    @NonNull
    private String getURLString() {
        ImageView mImageView = (ImageView)findViewById(R.id.imageView);
        return "https://memegen.link/wonka/you-are/a-meme.jpg?height="
                + mImageView.getHeight()
                + "&width="
                + mImageView.getWidth();
    }

    @NonNull
    private String getURLString(String meme, String topText, String bottomText) {
        ImageView mImageView = (ImageView)findViewById(R.id.imageView);

        String formattedMemeText = formatMemeText(meme, topText, bottomText);

        return "https://memegen.link/"
                + formattedMemeText
                + ".jpg?height=" + mImageView.getHeight()
                + "&width=" + mImageView.getWidth();
    }

    private String formatMemeText(String meme, String topText, String bottomText) {
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

    public void shareIt(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageView mImageView = (ImageView)findViewById(R.id.imageView);
        Bitmap bmp = loadBitmapFromView(mImageView);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    public void genMeme(View view) {
        Spinner mMeme = (Spinner) findViewById(R.id.theSpinner);
        EditText mTopText = (EditText) findViewById(R.id.topText);
        EditText mBottomText = (EditText) findViewById(R.id.bottomText);
        ImageView mImageView = (ImageView)findViewById(R.id.imageView);

        String topText = mTopText.getText().toString();
        String bottomText = mBottomText.getText().toString();
        String meme = mMeme.getSelectedItem().toString();

        String url = getURLString(meme, topText, bottomText);

        makeMemeImage(url);
    }

    private void askForPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    }

    private void populateMemeChoices(Spinner mMeme) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.memes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mMeme.setAdapter(adapter);
    }

    private static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    private void makeMemeImage(String url) {
        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute(url);
    }

}
