package com.bancusoft.pdfreader;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDFView pdfView = findViewById(R.id.pdfView);

        try {
            InputStream inputStream = getAssets().open("Codul_de_etică.pdf");
            pdfView.fromStream(inputStream)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(true)
                    .spacing(10)
                    .load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
