package com.example.t_star.STUDENTACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S_class_dashboard_translator extends AppCompatActivity {

    private Spinner spinnerSourceLang, spinnerTargetLang;
    private EditText edtInputText;
    private TextView txtOutput;
    private ProgressBar progressBar;

    private Translator translator;
    private Map<String, String> languageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_translator);

        setupToolbar();
        initViews();
        setupLanguages();
        setupSpinners();

        findViewById(R.id.btnTranslate).setOnClickListener(v -> translateText());
    }

    /* ---------------- TOOLBAR ---------------- */

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarTranslator);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            startActivity(
                    new Intent(
                            S_class_dashboard_translator.this,
                            S_Class_Dashboard_Activity.class
                    )
            );
            finish();
        });
    }

    /* ---------------- INIT VIEWS ---------------- */

    private void initViews() {
        spinnerSourceLang = findViewById(R.id.spinnerSourceLang);
        spinnerTargetLang = findViewById(R.id.spinnerTargetLang);
        edtInputText = findViewById(R.id.edtInputText);
        txtOutput = findViewById(R.id.txtOutput);
        progressBar = findViewById(R.id.progressBar);
    }

    /* ---------------- LANGUAGE SETUP ---------------- */

    private void setupLanguages() {
        languageMap = new HashMap<>();

        languageMap.put("English", TranslateLanguage.ENGLISH);
        languageMap.put("Hindi", TranslateLanguage.HINDI);
        languageMap.put("Marathi", TranslateLanguage.MARATHI);
        languageMap.put("German",TranslateLanguage.GERMAN);
        languageMap.put("Korean", TranslateLanguage.KOREAN);
        languageMap.put("Chinese", TranslateLanguage.CHINESE);
    }

    private void setupSpinners() {

        List<String> languageList =
                new ArrayList<>(languageMap.keySet());

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item_black,
                        languageList
                );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_black
        );

        spinnerSourceLang.setAdapter(adapter);
        spinnerTargetLang.setAdapter(adapter);

        spinnerSourceLang.setSelection(0);
        spinnerTargetLang.setSelection(1);
    }

    /* ---------------- TRANSLATION ---------------- */

    private void translateText() {

        String inputText =
                edtInputText.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter text to translate",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String sourceLang =
                languageMap.get(
                        spinnerSourceLang.getSelectedItem().toString()
                );

        String targetLang =
                languageMap.get(
                        spinnerTargetLang.getSelectedItem().toString()
                );

        if (sourceLang.equals(targetLang)) {
            Toast.makeText(
                    this,
                    "Source and target languages cannot be same",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        txtOutput.setText("");

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(sourceLang)
                        .setTargetLanguage(targetLang)
                        .build();

        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused ->
                        translator.translate(inputText)
                                .addOnSuccessListener(result -> {
                                    txtOutput.setText(result);
                                    progressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(
                                            this,
                                            "Translation failed",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })
                )
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            this,
                            "Language model download failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    /* ---------------- CLEANUP ---------------- */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (translator != null) {
            translator.close();

        }

    }
}