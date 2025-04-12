package com.bancusoft.pdfreader;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.io.InputStream;

public class PdfViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        // Obține numele fișierului PDF din intent
        String filename = getIntent().getStringExtra("filename");

        PDFView pdfView = findViewById(R.id.pdfView);

        try {
            InputStream inputStream = getAssets().open(filename);
            pdfView.fromStream(inputStream)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(true)
                    .spacing(10)
                    .load();
        } catch (IOException e) {
            Toast.makeText(this, "Eroare la deschiderea PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
