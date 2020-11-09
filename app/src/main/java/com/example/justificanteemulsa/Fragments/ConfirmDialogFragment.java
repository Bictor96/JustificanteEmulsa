package com.example.justificanteemulsa.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.fragment.app.DialogFragment;

import com.example.justificanteemulsa.Classes.ItemData;
import com.example.justificanteemulsa.Classes.ItemState;
import com.example.justificanteemulsa.Classes.ItemType;
import com.example.justificanteemulsa.Classes.UserData;
import com.example.justificanteemulsa.Classes.Utils;
import com.example.justificanteemulsa.MainActivity;
import com.example.justificanteemulsa.R;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ConfirmDialogFragment extends DialogFragment {

    private UserData userData;
    private ItemData itemData;

    private Bitmap userSignature;
    private Bitmap workerSignature;

    private static final int SignatureWidth = 150;
    private static final int SignatureHeight = 75;
    private static final String DownloadPath = Environment.getExternalStorageDirectory() + "/Justificantes/";

    private static final int SAVE_PDF_REQUEST = 101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        this.userData = getArguments().getParcelable("EXTRA_USER_DATA");
        this.itemData = getArguments().getParcelable("EXTRA_ITEM_DATA");

        userSignature = Utils.LoadSignature(this.getActivity(), "user_bmp.png");
        workerSignature = Utils.LoadSignature(this.getActivity(), "worker_bmp.png");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        Dialog dialog = getDialog();
        dialog.setTitle(R.string.confirm_dialog_title);

        View view = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);

        populateItemType(view);
        populateItemState(view);
        populateCitizen(view);

        // Añadimos fecha
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        TextInputLayout dateView = view.findViewById(R.id.date_textview);
        dateView.getEditText().setText(dateFormat.format(now));

        ImageView userSignatureView = view.findViewById(R.id.signatureView);
        if (this.userSignature != null)
            userSignatureView.setImageBitmap(
                    Bitmap.createScaledBitmap(
                            userSignature,
                            SignatureWidth, SignatureHeight,
                            false));

        ImageView workerSignatureView = view.findViewById(R.id.workerSignatureView);
        if (workerSignature != null)
            workerSignatureView.setImageBitmap(
                    Bitmap.createScaledBitmap(workerSignature,
                            SignatureWidth, SignatureHeight,
                            false));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button confirmButton = view.findViewById(R.id.confirmButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
               CreatePdf();
            }
        });
    }


    private void hideButtons() {
        View view = getView();
        view.findViewById(R.id.cancelButton).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.confirmButton).setVisibility(View.INVISIBLE);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void CreatePdf() {
        // Ocultamos los botones para que no aparezcan en el pdf
        hideButtons();

        // Crear un documento nuevo
        PdfDocument document = generatePDF();

        // Comprobar si el directorio existe, si no, crearlo
        checkIfPdfFolderExist();

        // Guardar el documento

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            saveDocumentIntent();
        else {
            saveDocument(document);
        }
    }


    private void saveDocumentIntent() {
        String name = userData.getName().toLowerCase().replaceAll("\\s", "_");
        long timestamp = (System.currentTimeMillis() / 1000);
        String filename = "Justificante_" + timestamp + "_" + name + ".pdf";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        startActivityForResult(intent, SAVE_PDF_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SAVE_PDF_REQUEST) {
            Log("PDF Uri: " + data.toUri(Intent.URI_ALLOW_UNSAFE));

            PdfDocument document = generatePDF();

            try {
                document.writeTo(getContext().getContentResolver().openOutputStream(data.getData()));
                finalizeFragment();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                document.close();
            }

        }
    }

    private void saveDocument(PdfDocument document) {
        String targetPdf = generateFilename();
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(getActivity(), targetPdf + " generado con éxito.", Toast.LENGTH_LONG).show();
            finalizeFragment();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        finally {
            document.close();
        }
    }

    private PdfDocument generatePDF() {
        // Crear un documento nuevo
        PdfDocument document = new PdfDocument();

        // Crear PageInfo
        View view = this.getView();
        view.measure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(view.getMeasuredWidth(), view.getMeasuredHeight(), 1).create();

        // Crear Página
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);

        // Finalizamos la pagina
        document.finishPage(page);
        return document;
    }

    // Comprueba si existe directorio, sino, lo crea
    private void checkIfPdfFolderExist() {
        boolean exists = new File(getContext().getExternalFilesDir("resource/folder"), "Justificantes").exists();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("ConfirmFragment", "Is External Storage: " + Environment.isExternalStorageManager());
        }
        if (exists) {
            Log.d("ConfirmFragment", "Creating dir Justificantes");
            boolean wasCreated = new File(getContext().getExternalFilesDir("resource/folder"), "Justificantes").mkdirs();
            Log.d("ConfirmFragment", "Justificantes dir created: " + wasCreated);
        }
        else
            Log.d("ConfirmFragment", "Dir alrady exist");
    }

    private void finalizeFragment() {
        clearData();
        returnToMainActivity();
    }

    public void populateItemType(View view) {
        TextInputLayout type = view.findViewById(R.id.confirm_type);
        TextInputLayout brand = view.findViewById(R.id.confirm_brand);
        TextInputLayout serial = view.findViewById(R.id.confirm_ns);
        ItemType item = this.itemData.getItemType();

        type.getEditText().setFocusable(false);
        setTextLayoutOptions(type);
        setTextLayoutOptions(brand);
        setTextLayoutOptions(serial);

        type.getEditText().setText(item.getType());
        brand.getEditText().setText(item.getBrand());
        serial.getEditText().setText(item.getSerialNumber());
    }

    public void populateItemState(View view) {
        TextInputLayout state = view.findViewById(R.id.confirm_state);
        TextInputLayout description = view.findViewById(R.id.confirm_description);
        ItemState itemState = this.itemData.getItemState();

        state.getEditText().setText(itemState.getState());
        description.getEditText().setText(itemState.getStateDescription());

        setTextLayoutOptions(state);
        setTextLayoutOptions(description);
    }

    public void populateCitizen(View view) {
        TextInputLayout name = view.findViewById(R.id.confirm_name);
        TextInputLayout nif = view.findViewById(R.id.confirm_nif);
        TextInputLayout email = view.findViewById(R.id.confirm_email);
        TextInputLayout phone = view.findViewById(R.id.confirm_phone);

        setTextLayoutOptions(name);
        setTextLayoutOptions(nif);
        setTextLayoutOptions(email);
        setTextLayoutOptions(phone);

        name.getEditText().setText(this.userData.getName());
        nif.getEditText().setText(this.userData.getNIF());
        email.getEditText().setText(this.userData.getEmail());
        phone.getEditText().setText(this.userData.getPhoneNumber());
    }

    /**
     * Deshabilita el campo para que no salga resaltado y da color negro al texto
     * @param input
     */
    private void setTextLayoutOptions(TextInputLayout input) {
        input.getEditText().setEnabled(false);
        input.getEditText().setTextColor(getResources().getColor(R.color.colorBlack));
    }

    /**
     * Genera el nombre del archivo PDF en formato Justificante_nombre.pdf
     *
     * @return File Path (String)
     */
    private String generateFilename() {
        String name = userData.getName().toLowerCase().replaceAll("\\s", "_");
        long timestamp = (System.currentTimeMillis() / 1000);
        String strTimestamp = String.valueOf(timestamp);
        String filename = "Justificante_" + timestamp + "_" + name + ".pdf";

        Log.d("Justificante", "Path: " +  DownloadPath + filename);
        return DownloadPath + filename;
    }

    private void returnToMainActivity() {
        this.dismiss();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    private void clearData() {
        // Limpieza de SharedPreferences para evitar que reaparezcan los datos del usuario en UserDataActivity
        clearSharedPreferences();
        // Limpieza de bitmaps
        clearBitmaps();
    }

    private void clearSharedPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("SHARED_USER_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.apply();

        Log.d("ConfirmDialog", "SharedPreferences limpiadas");

    }

    private void clearBitmaps() {
        File bitmap = new File("user_bmp.png");
        boolean isDeleted = bitmap.delete();

        Log("Bitmap eliminado: " + isDeleted);
    }

    private void Log(String msg) {
        Log.d("ConfirmFragment", msg);
    }
}
