package com.photoroulette;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class BrowsePictureActivity extends Activity {

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    protected int counter = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = managedQuery(images,
                projection,
                "",
                null,
                ""
        );

        final ArrayList<String> imagesPath = new ArrayList<String>();
        if (cur.moveToFirst()) {

            int dataColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            do {
                imagesPath.add(cur.getString(dataColumn));
                Log.d("PATHHH",cur.getString(dataColumn));
            } while (cur.moveToNext());
        }
        cur.close();
        final Random random = new Random();
        final int count = imagesPath.size();

        int randomInt = random.nextInt(count-1);
        String randomImage = imagesPath.get(randomInt);
        Log.d("ESCOLHIDOOOO",imagesPath.get(randomInt));

        File imgFile = new  File(randomImage);
        if(imgFile.exists()){

            Bitmap myBitmap = convertBitmap(imgFile.getAbsolutePath());
            /*Matrix matrix = new Matrix();
            matrix.setRotate(90);
            Bitmap bmpRotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),myBitmap.getHeight(), matrix, false);*/

            ImageView myImage = (ImageView) findViewById(R.id.imageView);
            myImage.setImageBitmap(myBitmap);



        }



    }

    public static Bitmap convertBitmap(String path)   {

        Bitmap bitmap=null;
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inDither=false;                     //Disable Dithering mode
        bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage=new byte[32 * 1024];


        File file=new File(path);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(fs!=null)
            {
                bitmap=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

}