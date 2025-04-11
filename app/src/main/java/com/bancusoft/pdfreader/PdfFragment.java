package com.bancusoft.pdfreader;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.bancusoft.pdfreader.TextHighlightFinder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PdfFragment extends Fragment {
    private static final String ARG_FILENAME = "pdf_filename";
    private String pdfFilename;
    private Context context;
    private PDFView pdfView;
    private HighlightOverlayView highlightOverlay;

    public static PdfFragment newInstance(String filename) {
        PdfFragment fragment = new PdfFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILENAME, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context ctx) {
        super.onAttach(ctx);
        this.context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pdfFilename = getArguments().getString(ARG_FILENAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf, container, false);
        pdfView = view.findViewById(R.id.pdfView);
        highlightOverlay = view.findViewById(R.id.highlightOverlay);
        loadPdf();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pdfView != null && pdfFilename != null) {
            loadPdf();
        }
    }

    private void loadPdf() {
        try {
            InputStream inputStream = context.getAssets().open(pdfFilename);
            pdfView.fromStream(inputStream)
                    .enableAnnotationRendering(true)
                    .enableAntialiasing(true)
                    .spacing(10)
                    .onLoad(nbPages -> {})
                    .load();
        } catch (Exception e) {
            Toast.makeText(context, "Eroare la deschiderea fișierului PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Căutare text PDF");

        final EditText input = new EditText(context);
        input.setHint("Introdu textul căutat");
        builder.setView(input);

        builder.setPositiveButton("Caută", (dialog, which) -> {
            String keyword = input.getText().toString().trim().toLowerCase();
            if (!keyword.isEmpty()) {
                try {
                    InputStream inputStream = context.getAssets().open(pdfFilename);
                    PDDocument document = PDDocument.load(inputStream);
                    TextHighlightFinder finder = new TextHighlightFinder(keyword);
                    finder.setSortByPosition(true);
                    finder.setStartPage(1);
                    finder.setEndPage(document.getNumberOfPages());
                    finder.getText(document);
                    List<TextHighlightFinder.HighlightInfo> results = finder.getHighlights();
                    document.close();

                    if (!results.isEmpty()) {
                        TextHighlightFinder.HighlightInfo first = results.get(0);
                        pdfView.jumpTo(first.page - 1, true);

                        RectF rect = new RectF(
                                first.x,
                                first.y,
                                first.x + first.width,
                                first.y + first.height
                        );
                        float zoom = pdfView.getZoom();
                        highlightOverlay.setHighlight(rect, zoom);
                        highlightOverlay.postDelayed(() -> highlightOverlay.clearHighlight(), 3000);

                        Toast.makeText(context, "Text găsit la pagina " + first.page, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Textul nu a fost găsit.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Eroare la căutare în PDF.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Anulează", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void savePdfToDownloads() {
        try {
            InputStream in = context.getAssets().open(pdfFilename);
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outFile = new File(downloadsDir, pdfFilename);

            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            Toast.makeText(context, "✅ PDF salvat în: " + outFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "❌ Eroare la salvarea fișierului", Toast.LENGTH_SHORT).show();
        }
    }
}
