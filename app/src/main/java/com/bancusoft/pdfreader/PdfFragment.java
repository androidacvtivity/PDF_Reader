package com.bancusoft.pdfreader;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.InputStream;

public class PdfFragment extends Fragment {
    private PDFView pdfView;
    private static final String ARG_FILENAME = "pdf_filename";
    private String pdfFilename;
    private Context context;

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
        pdfView = view.findViewById(R.id.pdfView); // păstrăm referința
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (pdfView != null && pdfFilename != null) {
            try {
                InputStream inputStream = context.getAssets().open(pdfFilename);
                pdfView.fromStream(inputStream)
                        .spacing(10)
                        .enableAnnotationRendering(true)
                        .load();
            } catch (Exception e) {
                Toast.makeText(context, "Eroare la reîncărcarea fișierului PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
