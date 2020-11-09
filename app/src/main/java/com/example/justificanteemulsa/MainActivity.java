package com.example.justificanteemulsa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.justificanteemulsa.Activities.UserDataActivity;
import com.example.justificanteemulsa.Classes.ItemData;
import com.example.justificanteemulsa.Classes.ItemState;
import com.example.justificanteemulsa.Classes.ItemType;
import com.example.justificanteemulsa.Classes.Utils;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends Activity {

    private Spinner stateSpinner;
    private EditText descriptionView;
    String[] stateItems;

    private static final int WRITE_REQUEST_CODE = 11;
    private static final int MANAGE_REQUEST_CODE = 111;
    private static final int PICK_PDF_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.stateSpinner = findViewById(R.id.stateSpinner);
        this.descriptionView = findViewById(R.id.state_input);
        this.stateItems = getResources().getStringArray(R.array.stateSpinner);
        Toolbar toolbar = findViewById(R.id.app_toolbar);


        this.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals(stateItems[2])) {
                    descriptionView.setVisibility(View.VISIBLE);
                } else {
                    descriptionView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, this.WRITE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(new String[] {Manifest.permission.MANAGE_EXTERNAL_STORAGE}, this.MANAGE_REQUEST_CODE);
            }
        }

        Button button = findViewById(R.id.nextButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout typeInput = findViewById(R.id.type_layout);
                boolean noErrors = true;
                String editTextString = typeInput.getEditText().getText().toString();
                if (editTextString.isEmpty()) {
                    typeInput.setError(getResources().getString(R.string.error_string));
                    noErrors = false;
                }
                else if (editTextString.length() <= 4) {
                    typeInput.setError("El texto introducido en este campo es demasiado corto.");
                    noErrors = false;
                }
                else {
                    typeInput.setError(null);
                }

                if (noErrors) {
                    Log.d("Input", "No errors");
                    openUserDataActivity();
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

        if (requestCode == PICK_PDF_REQUEST) {
            Utils.startPdfReaderIntent(this, data.getData());
        }
    }

    private void openPdfFolder() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Utils.getFolderUri(), "resource/folder");

        // Comprueba si hay alguna aplicación que pueda manejar este intent
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, getResources().getString(R.string.no_file_explorer_installed), Toast.LENGTH_LONG).show();
    }

    private void openUserDataActivity() {
        // Recopilar los datos introducidos
        ItemData itemData = GetDataFromViews();

        // Iniciar Actividad Datos de Ciudadano
        Intent intent = new Intent(this, UserDataActivity.class);
        intent.putExtra("EXTRA_ITEM_DATA", itemData);
        startActivity(intent);
    }

    private ItemData GetDataFromViews() {
        ItemType itemType = GetItemTypeData();
        ItemState itemState = GetItemStateData();

        return new ItemData(itemType, itemState);
    }

    private ItemType GetItemTypeData() {
        Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        TextInputLayout brandText = findViewById(R.id.type_layout);
        TextInputLayout serialText = findViewById(R.id.ns_layout);

        String type = typeSpinner.getSelectedItem().toString();
        String brand = brandText.getEditText().getText().toString();
        String serialNumber = serialText.getEditText().getText().toString();

        return new ItemType(type, brand, serialNumber);
    }

    private ItemState GetItemStateData() {
        Spinner stateSpinner = (Spinner) findViewById(R.id.stateSpinner);
        TextInputLayout stateText = findViewById(R.id.state_layout);

        String state = stateSpinner.getSelectedItem().toString();
        if (stateText.getEditText() != null)
            return new ItemState(state, stateText.getEditText().getText().toString());
        else
            return new ItemState(state);
    }
}