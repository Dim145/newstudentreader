package com.example.newstudentclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vérifiez si l'appareil prend en charge NFC
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Cet appareil ne prend pas en charge NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Veuillez activer NFC.", Toast.LENGTH_LONG).show();
        }

        // Gestion de l'événement de lecture NFC
        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ecoute des événements NFC
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Arrêt de l'écoute des événements NFC
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            // Récupération des données NFC
            Tag tag = intent.getParcelableExtra("android.nfc.extra.TAG");
            System.out.println(tag);
            String message = new String(tag.getId(), StandardCharsets.UTF_8);

            TextView textView = (TextView) findViewById(R.id.textView);
            EditText editText = (EditText) findViewById(R.id.editText);
            Button button = (Button) findViewById(R.id.button);
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            if(!db.checkUser(message)) {
                textView.setText("Entrez votre nom pls");
                editText.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString();
                        db.addUser(name, message);
                        textView.setText("Bienvenue " + name);
                        editText.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                textView.setText("Id utilisateur : " + message + "\nNom utilisateur : " + db.getUserNameFromId(message));
            }
        }
    }
}