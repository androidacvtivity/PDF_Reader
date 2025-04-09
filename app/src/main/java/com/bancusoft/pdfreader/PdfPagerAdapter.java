package com.bancusoft.pdfreader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class PdfPagerAdapter extends FragmentStateAdapter {

    private final List<PdfFragment> fragments = new ArrayList<>();

    public PdfPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void addFragment(PdfFragment fragment) {
        fragments.add(fragment);
    }

    public PdfFragment getFragment(int position) {
        return fragments.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
