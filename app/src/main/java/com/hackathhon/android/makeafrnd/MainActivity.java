package com.hackathhon.android.makeafrnd;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hackathhon.android.library.bottomsheetpicker.BottomSheetPickerFragment;
import com.hackathhon.android.makeafrnd.bottomsheetpicker.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Launcher/Main activity for the MakeAFrnd application.
 */
public class MainActivity extends AppCompatActivity implements BottomSheetPickerFragment.BottomSheetPickerListener {

private final static int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;

private BottomSheetPickerFragment bottomSheetPickerFragment;
    @BindView(com.hackathhon.android.makeafrnd.bottomsheetpicker.R.id.username_field) EditText etUserName;
    @BindView(com.hackathhon.android.makeafrnd.bottomsheetpicker.R.id.button_enter_chat) Button btnChatNow;
    @BindView(com.hackathhon.android.makeafrnd.bottomsheetpicker.R.id.profile_image) CircleImageView imgProfilePic;
    public static final String SHARED_PREFS_FILE = "NearbyChatPreferences";
    public static final String USERNAME_KEY = "username";
    public static final String AVATAR = "avatar";
    public static Context mainContext;


    public static SharedPreferences sharedPreferences;
    String username;
    String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the viw and toolbar
       setContentView(com.hackathhon.android.makeafrnd.bottomsheetpicker.R.layout.activity_main);
         ButterKnife.bind(this);

        // Get the main context
        mainContext = getApplicationContext();

        // Get any saved username and avatar colour from the shared preference file
        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME_KEY, "");
        avatar = sharedPreferences.getString(AVATAR, "");

        if (savedInstanceState != null) {
            final BottomSheetPickerFragment fragment = (BottomSheetPickerFragment) getSupportFragmentManager().findFragmentByTag("picker");
            if (fragment != null) {
                fragment.setListener(this);
            }
        }

        // Set the username field if the user had saved one previously
        if (!username.isEmpty()){
            etUserName.setText(username);
            etUserName.setSelection(etUserName.getText().length());
        }

        if (!avatar.isEmpty()){
            try{
                byte[] decodedString = Base64.decode(avatar.getBytes(), Base64.DEFAULT);
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgProfilePic.setImageBitmap(avatarBitmap);
            } catch (IllegalArgumentException e){
                imgProfilePic.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            }
        }

    }

    @OnClick(R.id.button_enter_chat)
    public void onClickChatNow(View view){
        String username = etUserName.getText().toString().trim();
       // String avatarColour = Integer.toHexString(sCurrentAvatarColour);
        imgProfilePic.buildDrawingCache();
        Bitmap bmap = imgProfilePic.getDrawingCache();

        // If the username is empty prompt the user and don't enter the chat
        if (username.isEmpty() || username.equals("")){
            Snackbar.make(btnChatNow, getString(R.string.error_empty_username), Snackbar.LENGTH_SHORT).show();
        } else {

            String profilePic= getEncoded64ImageStringFromBitmap(bmap);

            // Save the username and sCurrentAvatarColour in the shared preferences
            storeUsernameAndAvatarColour(username, profilePic);

            // Enter the chat with the username and avatarColour sent to the ChatActivity
            Intent enterChatIntent = new Intent(getApplicationContext(), ChatActivity.class);
            enterChatIntent.putExtra(USERNAME_KEY, username);
            enterChatIntent.putExtra(AVATAR, profilePic);
            startActivity(enterChatIntent);
        }
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteFormat = stream.toByteArray();
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

        @OnClick(com.hackathhon.android.makeafrnd.bottomsheetpicker.R.id.profile_image)
        public void onClickProfileImage(View view){

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Permission required to access files", Toast.LENGTH_SHORT).show();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
                }
            } else {
                showBottomSheetPicker();
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showBottomSheetPicker();
                } else {
                    Toast.makeText(MainActivity.this, "Permission required to access files", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showBottomSheetPicker() {
        bottomSheetPickerFragment = new BottomSheetPickerFragment.Builder()
                .setSelectionMode(BottomSheetPickerFragment.SelectionMode.IMAGES)
                .build();

        bottomSheetPickerFragment.setListener(this);
        bottomSheetPickerFragment.show(getSupportFragmentManager(), "picker");
    }

    @Override
    public void onFileLoad(ImageView imageView, Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(com.hackathhon.android.makeafrnd.bottomsheetpicker.R.drawable.thumbnail_loading)
                .centerCrop()
                .crossFade()
                .into(imageView);

    }

    @Override
    public void onFilesSelected(List<Uri> uriList) {
        if (bottomSheetPickerFragment != null) {
            bottomSheetPickerFragment.dismiss();
            getImage(uriList.get(0));
           }
    }

    public void getImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imgProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Store the username and avatarColour selections in the SharedPreference file to persist the user's choices.
     */
    private void storeUsernameAndAvatarColour(String username, String avatar){
        // Store the values in the SharedPreferences file
        sharedPreferences.edit().putString(USERNAME_KEY, username).apply();
        sharedPreferences.edit().putString(AVATAR, avatar).apply();
    }
}
