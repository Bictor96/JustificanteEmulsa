package com.example.justificanteemulsa.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.justificanteemulsa.Classes.ItemData;
import com.example.justificanteemulsa.Classes.UserData;
import com.example.justificanteemulsa.Classes.Utils;
import com.example.justificanteemulsa.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Pattern;

public class UserDataActivity extends Activity {

    private static final int PICK_PDF_REQUEST = 102;
    private ItemData itemData;
    private static final String PHONE_REGEX = "^[0-9]{9}";
    private static final String NIF_REGEX = "^[A-Za-z]?[0-9]{8}[A-Za-z]?$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        if (savedInstanceState == null) {
            loadUserDataFromSharedPreferences();
        }

        this.itemData = getIntent().getExtras().getParcelable("EXTRA_ITEM_DATA");
        Toolbar toolbar = findViewById(R.id.app_toolbar);
        View rootView = findViewById(android.R.id.content);

        final List<TextInputLayout> layouts = Utils.findViewsWithType(
                rootView, TextInputLayout.class);

        Button button = findViewById(R.id.next_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean noErrors = true;
                for (TextInputLayout textInputLayout : layouts) {
                    String editString = textInputLayout.getEditText().getText().toString();
                    boolean needed = textInputLayout.getTag() == null;
                    if (editString.isEmpty() && needed) {
                        textInputLayout.setError(getResources().getString(R.string.error_string));
                        noErrors = false;
                    } else {
                        if (!hasFormat(textInputLayout)) {
                            noErrors = false;
                        } else {
                            textInputLayout.setError(null);
                        }
                    }
                }

                if (noErrors) {
                    openWorkerSignatureActivity();
                }
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_open_folder:
                        openPdfFolder();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(data.getData(), "application/pdf");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException activityNotFoundException) {
                View layout = findViewById(R.id.user_activity_layout);
                Snackbar.make(layout, R.string.no_pdf_reader, Snackbar.LENGTH_LONG).show();
            }
        }
    }


    private void loadUserDataFromSharedPreferences() {
        SharedPreferences preferences = this.getSharedPreferences("SHARED_USER_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String name = preferences.getString("user_name", "");
        String nif = preferences.getString("user_nif", "");
        String phone = preferences.getString("user_phone", "");
        String email = preferences.getString("user_email", "");

        setUserDataToViews(new UserData(name, nif, phone, email));

        editor.clear();
        editor.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        UserData userData = GetUserDataFromViews();
        outState.putParcelable("EXTRA_USER_DATA", userData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        UserData userData = savedInstanceState.getParcelable("EXTRA_USER_DATA");
        setUserDataToViews(userData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = this.getSharedPreferences("SHARED_USER_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        saveUserDataToSharedPreferences(editor);
        editor.commit();
    }

    private boolean hasFormat(TextInputLayout input) {
        int id = input.getId();
        if (id == R.id.phone_layout) {
            boolean hasFormat = hasPhoneNumberFormat(input.getEditText().getText().toString());
            if (!hasFormat) {
                input.setError("El teléfono no tiene el formato requerido.");
            }

            return hasFormat;
        }
        else if (id == R.id.nif_layout) {
            boolean hasFormat = Pattern.matches(NIF_REGEX, input.getEditText().getText().toString());
            if (!hasFormat) {
                input.setError("El NIF no tiene el formato requerido.");
            }

            return hasFormat;
        }
        else if (id == R.id.name_layout) {
            boolean hasFormat = input.getEditText().getText().length() >= 5;
            if (!hasFormat) {
                input.setError("El nombre es demasiado corto.");
            }

            return hasFormat;
        }

        return true;
    }

    private boolean hasPhoneNumberFormat(String phone) {
        return Pattern.matches(this.PHONE_REGEX, phone.toString());
    }

    private void openPdfFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(Uri.parse(Environment.DIRECTORY_DOWNLOADS) ,"application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    public void openWorkerSignatureActivity() {
        UserData userData = GetUserDataFromViews();

        Bundle bundle = new Bundle();
        bundle.putParcelable("EXTRA_USER_DATA", userData);
        bundle.putParcelable("EXTRA_ITEM_DATA", itemData);

        // Iniciar Actividad Firma Trabajador
        Intent intent = new Intent(this, SignatureActivity.class);
        intent.putExtra("EXTRA_BUNDLE", bundle);
        startActivity(intent);
    }



    private UserData GetUserDataFromViews() {
        TextInputLayout nameText = findViewById(R.id.name_layout);
        TextInputLayout nifText = findViewById(R.id.nif_layout);
        TextInputLayout phoneText = findViewById(R.id.phone_layout);
        TextInputLayout emailText = findViewById(R.id.email_layout);

        String name = nameText.getEditText().getText().toString();
        String nif = nifText.getEditText().getText().toString();
        String phone = phoneText.getEditText().getText().toString();
        String email = emailText.getEditText().getText().toString();

        return new UserData(name, nif, phone, email);
    }

    private void saveUserDataToSharedPreferences(SharedPreferences.Editor editor) {
        UserData userData = GetUserDataFromViews();

        editor.putString("user_name", userData.getName());
        editor.putString("user_nif", userData.getNIF());
        editor.putString("user_phone", userData.getPhoneNumber());
        editor.putString("user_email", userData.getEmail());
    }

    private void setUserDataToViews(UserData userData) {
        TextInputLayout nameText = findViewById(R.id.name_layout);
        TextInputLayout nifText = findViewById(R.id.nif_layout);
        TextInputLayout phoneText = findViewById(R.id.phone_layout);
        TextInputLayout emailText = findViewById(R.id.email_layout);

        nameText.getEditText().setText(userData.getName());
        nifText.getEditText().setText(userData.getNIF());
        phoneText.getEditText().setText(userData.getPhoneNumber());
        emailText.getEditText().setText(userData.getEmail());
    }

}