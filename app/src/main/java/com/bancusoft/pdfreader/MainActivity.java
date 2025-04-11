package com.bancusoft.pdfreader;

import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PdfPagerAdapter adapter;
    private List<String> pdfFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new PdfPagerAdapter(this);
        viewPager.setAdapter(adapter);

        loadPdfFilesFromAssets();

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(getPdfName(pdfFiles.get(position)))
        ).attach();
    }

    private void loadPdfFilesFromAssets() {
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("");
            for (String file : files) {
                if (file.endsWith(".pdf")) {
                    pdfFiles.add(file);
                    adapter.addFragment(PdfViewerFragment.newInstance(file));
                }
            }
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String getPdfName(String filename) {
        return filename.replace(".pdf", "");
    }
}
