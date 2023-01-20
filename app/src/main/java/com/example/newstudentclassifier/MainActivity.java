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

public class MainActivity extends AppCompatActivity
{
    private NfcAdapter nfcAdapter = null;
    private DatabaseHelper db = null;

    private TextView textView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(getApplicationContext());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        // Vérifiez si l'appareil prend en charge NFC
        if (nfcAdapter == null)
        {
            Toast.makeText(this, "Cet appareil ne prend pas en charge NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled())
        {
            textView.setText(R.string.activate_nfc);
        }

        // Gestion de l'événement de lecture NFC
        handleIntent(getIntent());
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Ecoute des événements NFC
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Arrêt de l'écoute des événements NFC
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {
            // Récupération des données NFC
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String message = new String(tag.getId(), StandardCharsets.UTF_8);

            if (!db.checkUser(message))
            {
                textView.setText(R.string.name_please);

                editText.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

                button.setOnClickListener(v ->
                {
                    String name = editText.getText().toString();
                    db.addUser(name, message);
                    textView.setText(String.format("Bienvenue %s", name));
                    editText.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                });

            }
            else
            {
                textView.setText(String.format("Id utilisateur : %s\nNom utilisateur : %s", message, db.getUserNameFromId(message)));
                editText.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
            }
        }
    }
}