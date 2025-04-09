package com.bancusoft.pdfreader;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private PdfPagerAdapter adapter;
    private final List<String> pdfFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new PdfPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Încărcăm PDF-urile din assets
        loadPdfFromAssets();

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    String filename = pdfFiles.get(position);
                    String cleanTitle = filename.replace(".pdf", "");
                    tab.setText(cleanTitle);
                }
        ).attach();

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            PdfFragment currentFragment = (PdfFragment) adapter.getFragment(currentItem);
            currentFragment.showSearchDialog();
        });


    }

    private void loadPdfFromAssets() {
        try {
            AssetManager assetManager = getAssets();
            String[] files = assetManager.list("");
            if (files != null) {
                for (String filename : files) {
                    if (filename.endsWith(".pdf")) {
                        pdfFiles.add(filename);
                        adapter.addFragment(PdfFragment.newInstance(filename));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
