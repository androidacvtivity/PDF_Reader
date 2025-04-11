package com.bancusoft.pdfreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagePdfFragment extends Fragment {
    private static final String ARG_FILENAME = "pdf_filename";
    private String pdfFilename;
    private Context context;
    private PhotoView photoView;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static ImagePdfFragment newInstance(String filename) {
        ImagePdfFragment fragment = new ImagePdfFragment();
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
        View view = inflater.inflate(R.layout.fragment_image_pdf, container, false);
        photoView = view.findViewById(R.id.photoView);
        loadPdfPageAsync(0);
        return view;
    }

    private void loadPdfPageAsync(int pageIndex) {
        executor.execute(() -> {
            try {
                File file = new File(context.getCacheDir(), pdfFilename);
                if (!file.exists()) {
                    InputStream asset = context.getAssets().open(pdfFilename);
                    FileOutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = asset.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    asset.close();
                    output.close();
                }

                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);

                if (pdfRenderer.getPageCount() > pageIndex) {
                    if (currentPage != null) currentPage.close();
                    currentPage = pdfRenderer.openPage(pageIndex);

                    int scale = 6; // mărim bitmapul pentru pan complet
                    int width = currentPage.getWidth() * scale;
                    int height = currentPage.getHeight() * scale;

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    requireActivity().runOnUiThread(() -> {
                        photoView.setImageBitmap(bitmap);
                        photoView.setMinimumScale(1f);
                        photoView.setMediumScale(2.5f);
                        photoView.setMaximumScale(8f);
                        photoView.setAllowParentInterceptOnEdge(false);
                        photoView.setZoomable(true);
                    });

                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(context, "Pagina nu există în PDF.", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(context, "Eroare la afișarea PDF-ului.", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
            executor.shutdown();
        } catch (Exception ignored) {}
    }
}
