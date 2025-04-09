package com.bancusoft.pdfreader;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PdfFragment extends Fragment {
    private static final String ARG_FILENAME = "pdf_filename";
    private String pdfFilename;
    private Context context;
    private PDFView pdfView;
    private int totalPages = 0;
    private View highlightView;
    private final Map<Integer, String> pageTexts = new HashMap<>();

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
        highlightView = view.findViewById(R.id.highlightView);
        pdfView = view.findViewById(R.id.pdfView);
        loadPdf();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (pdfView != null && pdfFilename != null) {
            loadPdf(); // reîncarcă PDF-ul când revii la tab
        }
    }


    private void loadPdf() {
        try {
            InputStream inputStream = context.getAssets().open(pdfFilename);
            pdfView.fromStream(inputStream)
                    .spacing(10)
                    .enableAnnotationRendering(true)
                    .onLoad(nbPages -> {
                        totalPages = nbPages;
                        extractTextFromPdf();
                    })
                    .onPageChange((page, pageCount) -> {})
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
                boolean found = false;
                for (Map.Entry<Integer, String> entry : pageTexts.entrySet()) {
                    if (entry.getValue().toLowerCase().contains(keyword)) {
                        // Sari la pagina
                        pdfView.jumpTo(entry.getKey(), true);

                        // Animație de puls și evidențiere
                        if (highlightView != null) {
                            Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
                            highlightView.startAnimation(pulse);
                            highlightView.setVisibility(View.VISIBLE);

                            highlightView.postDelayed(() -> {
                                highlightView.clearAnimation();
                                highlightView.setVisibility(View.GONE);
                            }, 3000);
                        }

                        // Mesaj
                        Toast.makeText(context, "📌 Text găsit la pagina " + (entry.getKey() + 1), Toast.LENGTH_SHORT).show();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Toast.makeText(context, "❌ Textul nu a fost găsit.", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Anulează", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    private void extractTextFromPdf() {
        try {
            InputStream is = context.getAssets().open(pdfFilename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder fullText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fullText.append(line).append("\n");
            }
            pageTexts.clear();
            pageTexts.put(0, fullText.toString()); // toată căutarea e într-o pagină logică 0
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
