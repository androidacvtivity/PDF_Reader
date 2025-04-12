package com.bancusoft.pdfreader;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lista fișierelor PDF
        ArrayList<String> pdfFiles = new ArrayList<>();
        pdfFiles.add("Codul_de_etică.pdf");
        pdfFiles.add("dec.pdf");  // Adaugă aici fișierele tale
        pdfFiles.add("feb.pdf");  // Adaugă aici fișierele tale
        pdfFiles.add("test.pdf");  // Adaugă aici fișierele tale

        // Crearea unui ListView pentru a vizualiza fișierele PDF
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pdfFiles);
        listView.setAdapter(adapter);

        // La click pe un fișier, deschidem PDF-ul
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFile = pdfFiles.get(position);
            Intent intent = new Intent(MainActivity.this, PdfViewActivity.class);
            intent.putExtra("filename", selectedFile);
            startActivity(intent);
        });
    }
}
