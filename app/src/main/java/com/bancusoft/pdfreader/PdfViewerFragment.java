package com.bancusoft.pdfreader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.IOException;
import java.io.InputStream;

public class PdfViewerFragment extends Fragment {

    private static final String ARG_FILENAME = "pdf_filename";
    private String pdfFilename;
    private PDFView pdfView;

    public static PdfViewerFragment newInstance(String filename) {
        PdfViewerFragment fragment = new PdfViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILENAME, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pdfFilename = getArguments().getString(ARG_FILENAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
        pdfView = view.findViewById(R.id.pdfView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pdfView != null && pdfFilename != null) {
            try {
                InputStream inputStream = requireContext().getAssets().open(pdfFilename);
                pdfView.fromStream(inputStream)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .spacing(10)
                        .enableAnnotationRendering(true)
                        .load();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Eroare la reîncărcarea fișierului PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
