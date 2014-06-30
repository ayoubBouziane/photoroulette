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
import android.media.ExifInterface;
import android.widget.TextView;


//IMPORTS DO FACEBOOK - METER AQUI
import com.facebook.*;
import com.facebook.model.GraphUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

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
                Log.d("PATH",cur.getString(dataColumn));
            } while (cur.moveToNext());
        }
        cur.close();
        final Random random = new Random();
        final int count = imagesPath.size();

        int randomInt = random.nextInt(count-1);
        String randomImage = imagesPath.get(randomInt);
        Log.d("Chosen",imagesPath.get(randomInt));

        File imgFile = new  File(randomImage);
        if(imgFile.exists()){

            Bitmap myBitmap = convertBitmap(imgFile.getAbsolutePath());
            /*Matrix matrix = new Matrix();
            matrix.setRotate(90);
            Bitmap bmpRotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),myBitmap.getHeight(), matrix, false);*/

            ImageView myImage = (ImageView) findViewById(R.id.imageView);
            myImage.setImageBitmap(myBitmap);
            //String _orientation;

            try { //photo details like orientation, are stored in the exif file from the respective photo
                ExifInterface exif = new ExifInterface(imgFile.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int photoRotationInDegrees = exifToDegrees(rotation);
                Log.d("Orientation:", ""+photoRotationInDegrees+" Degree");
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if(session.isOpened()){
                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                TextView welcome = (TextView) findViewById(R.id.welcome);
                                welcome.setText("Hello " + user.getName() + "!");
                            }
                        }
                    }).executeAsync();
                }
            }
        });

    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
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
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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