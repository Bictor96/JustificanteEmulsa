package com.example.justificanteemulsa.Activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.justificanteemulsa.Classes.ItemData;
import com.example.justificanteemulsa.Classes.UserData;
import com.example.justificanteemulsa.Fragments.ConfirmDialogFragment;
import com.example.justificanteemulsa.Fragments.SignatureFragment;
import com.example.justificanteemulsa.R;
import com.example.justificanteemulsa.Views.SignatureView;

public class SignatureActivity extends FragmentActivity {

    private UserData userData;
    private ItemData itemData;
    private SignatureFragment userSignatureFragment;
    private SignatureFragment workerSignatureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        Bundle extras = getIntent().getBundleExtra("EXTRA_BUNDLE");
        userData = extras.getParcelable("EXTRA_USER_DATA");
        itemData = extras.getParcelable("EXTRA_ITEM_DATA");

        this.userSignatureFragment = SignatureFragment.newInstance(getResources().getString(R.string.user_sign_text), "user_bmp.png", true);
        this.workerSignatureFragment = SignatureFragment.newInstance(getResources().getString(R.string.worker_sign_text), "worker_bmp.png", false);

        Button userSignatureButton = findViewById(R.id.user_signature_button);
        userSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignatureFragment.show(getSupportFragmentManager(), "signature_dialog");
            }
        });

        final Button workerSignatureButton = findViewById(R.id.worker_signature_button);
        workerSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workerSignatureFragment.show(getSupportFragmentManager(), "signature_dialog");
            }
        });


        Button finalizeButton = findViewById(R.id.finalize_button);
        finalizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConfirmDialog();
            }
        });
    }

    private void startConfirmDialog() {

        if (signaturesNotCalled() || areSignaturesEmpty()) {
            Toast.makeText(this, "Ambas firmas deben ser rellenadas.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("EXTRA_USER_DATA", userData);
        bundle.putParcelable("EXTRA_ITEM_DATA", itemData);

        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.setArguments(bundle);

        confirmDialogFragment.show(this.getSupportFragmentManager(), "dialog");
    }

    private boolean signaturesNotCalled() {
        return !(this.workerSignatureFragment.isSignatureViewLoaded() && this.userSignatureFragment.isSignatureViewLoaded());
    }

    private boolean areSignaturesEmpty() {
        return workerSignatureFragment.isSignatureEmpty() && userSignatureFragment.isSignatureEmpty();
    }
}