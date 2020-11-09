package com.example.justificanteemulsa.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.example.justificanteemulsa.Classes.Utils;
import com.example.justificanteemulsa.R;
import com.example.justificanteemulsa.Views.SignatureView;

import org.w3c.dom.Text;

public class SignatureFragment extends DialogFragment {
    private SignatureView signatureView;

    public SignatureFragment() {

    }

    public static SignatureFragment newInstance(String title, String filename, boolean showWarningText) {
        SignatureFragment fragment = new SignatureFragment();
        Bundle args = new Bundle();

        Log.d("SignatureFragment", "PDF Filename: " + filename);
        args.putString("title", title);
        args.putString("filename", filename);
        args.putBoolean("showWarningText", showWarningText);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        Bundle arguments = getArguments();

        // Titulo, texto de los botones e inflar vista
        setupDialog(alertBuilder, arguments);

        // Creamos dialogo y damos funcionalidad a los botones
        final AlertDialog alertDialog = alertBuilder.create();
        setupButtonsActions(alertDialog);

        return alertDialog;
    }

    public boolean isSignatureViewLoaded() {
        return this.signatureView != null;
    }

    public boolean isSignatureEmpty() {
        return this.signatureView.isEmpty();
    }

    private void setupDialog(AlertDialog.Builder builder, Bundle args) {
        setupTitle(builder, args);
        setupPositiveButton(builder);
        setupNegativeButton(builder);
        setupView(builder, args);
    }

    private void setupTitle(AlertDialog.Builder builder, Bundle args) {
        String title = args.getString("title");
        builder.setTitle(title);
    }

    private void setupView(AlertDialog.Builder builder, Bundle args) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_signature, null);

        this.signatureView = view.findViewById(R.id.signature_view);
        setupWarningText(view, args);

        builder.setView(view);
    }

    private void setupWarningText(View v, Bundle args) {
        boolean showWarningText = args.getBoolean("showWarningText");
        CardView warningView = v.findViewById(R.id.signature_warning_card);

        if (showWarningText)
            warningView.setVisibility(View.VISIBLE);
        else
            warningView.setVisibility(View.INVISIBLE);
    }

    private void setupPositiveButton(AlertDialog.Builder builder) {
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void setupNegativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton("Limpiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

        /*
        Aplicamos la funcionalidad al boton negativo en esta funcion para evitar
        que se cierre el dialogo al presionar.

        SIEMPRE llamar despues de AlertDialog.Builder.create()
     */

    private void setupButtonsActions(final AlertDialog alertDialog) {
        final String filename = getArguments().getString("filename");

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (signatureView.isEmpty()) {
                            Toast.makeText(getActivity(), "La firma esta en blanco.", Toast.LENGTH_SHORT).show();
                        } else {
                            saveSignature(filename);
                            dialog.dismiss();
                        }
                    }
                });

                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signatureView.clear();
                    }
                });
            }
        });

    }

    private void saveSignature(String filename) {
        // Cambiamos el color de fondo de la firma a blanco
        signatureView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        Bitmap bitmap = signatureView.GetSignatureBitmap();
        Utils.SaveSignature(getActivity(), filename, bitmap);
        // Se vuelve a poner el color en el habitual
        signatureView.setBackgroundColor(getResources().getColor(R.color.colorSignatureBackground));
    }
}
