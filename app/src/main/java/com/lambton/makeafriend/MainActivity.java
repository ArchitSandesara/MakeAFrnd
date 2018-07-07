package com.lambton.makeafriend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danielcswain.makeafriend.R;
import com.quanturium.android.library.bottomsheetpicker.BottomSheetPickerFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Launcher/Main activity for the MakeAFrnd application.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username_field) EditText mUsernameField;
    @BindView(R.id.button_enter_chat) Button mEnterChatButton;

    public static final String SHARED_PREFS_FILE = "NearbyChatPreferences";
    public static final String USERNAME_KEY = "username";
    public static final String AVATAR_COLOUR_KEY = "avatar_colour";
    public static Context mainContext;
    public static SharedPreferences sharedPreferences;

    private static Integer sCurrentAvatarColour;

    private final static int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;

    private BottomSheetPickerFragment bottomSheetPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the viw and toolbar
       setContentView(R.layout.activity_main);
         ButterKnife.bind(this);

        // Get the main context
        mainContext = getApplicationContext();

        // Get any saved username and avatar colour from the shared preference file
        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, "");
        String avatarColour = sharedPreferences.getString(AVATAR_COLOUR_KEY, "");

        // Set the username field if the user had saved one previously
        if (!username.isEmpty() ){
            mUsernameField.setText(username);
            mUsernameField.setSelection(mUsernameField.getText().length());
        }

        // Set sCurrentAvatarColour to the default colour (currently md_pink_500
        sCurrentAvatarColour = ContextCompat.getColor(getApplicationContext(), R.color.md_pink_500);

      /*  if (savedInstanceState != null) {
            final BottomSheetPickerFragment fragment = (BottomSheetPickerFragment) getSupportFragmentManager().findFragmentByTag("picker");
            if (fragment != null) {
                fragment.setListener(this);
            }
        }*/
    }

   /* @OnClick(R.id.profile_image)
    public void onClickProfileImage(){

                // Here, thisActivity is the current activity
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
    }*/

  /*  private void showBottomSheetPicker() {
        bottomSheetPickerFragment = new BottomSheetPickerFragment.Builder()
                .setSelectionMode(BottomSheetPickerFragment.SelectionMode.IMAGES_AND_VIDEOS)
                .build();

        bottomSheetPickerFragment.setListener(this);
        bottomSheetPickerFragment.show(getSupportFragmentManager(), "picker");
    }*/

    /*@Override
    public void onFileLoad(ImageView imageView, Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.thumbnail_loading)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    @Override
    public void onFilesSelected(List<Uri> uriList) {
        if (bottomSheetPickerFragment != null) {
            bottomSheetPickerFragment.dismiss();
            Toast.makeText(getApplicationContext(), "# selected files: " + uriList.size(), Toast.LENGTH_LONG).show();
        }
    }
*/

       @OnClick(R.id.button_enter_chat)
        public void onClickButtinEnterChat(View view) {
            // Get the username and sCurrentAvatarColour
            String username = mUsernameField.getText().toString();
            String avatarColour = Integer.toHexString(sCurrentAvatarColour);

            // If the username is empty prompt the user and don't enter the chat
            if (username.isEmpty() || username.equals("")){
                Snackbar.make(mEnterChatButton, getString(R.string.error_empty_username), Snackbar.LENGTH_SHORT).show();
            } else {
                // Ensure the avatarColour string starts with # prior to sending and saving
                if (!avatarColour.startsWith("#")){
                    avatarColour = "#" + avatarColour;
                }

                // Save the username and sCurrentAvatarColour in the shared preferences
                storeUsernameAndAvatarColour(username, avatarColour);

                // Enter the chat with the username and avatarColour sent to the ChatActivity
                Intent enterChatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                enterChatIntent.putExtra(USERNAME_KEY, username);
                enterChatIntent.putExtra(AVATAR_COLOUR_KEY, avatarColour);
                startActivity(enterChatIntent);
            }
        }

    /**
     * Store the username and avatarColour selections in the SharedPreference file to persist the user's choices.
     */
    private void storeUsernameAndAvatarColour(String username, String avatarColour){
        // Store the values in the SharedPreferences file
        sharedPreferences.edit().putString(USERNAME_KEY, username).apply();
        sharedPreferences.edit().putString(AVATAR_COLOUR_KEY, avatarColour).apply();
    }
}
