package com.bancusoft.pdfreader;

import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextHighlightFinder extends PDFTextStripper {

    private final String keyword;
    private final List<HighlightInfo> highlights = new ArrayList<>();

    public static class HighlightInfo {
        public final int page;
        public final float x;
        public final float y;
        public final float width;
        public final float height;

        public HighlightInfo(int page, float x, float y, float width, float height) {
            this.page = page;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public TextHighlightFinder(String keyword) throws IOException {
        this.keyword = keyword.toLowerCase();
    }

    public List<HighlightInfo> getHighlights() {
        return highlights;
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        int currentPage = getCurrentPageNo();
        StringBuilder currentString = new StringBuilder();
        int startIndex = -1;

        for (int i = 0; i < textPositions.size(); i++) {
            TextPosition tp = textPositions.get(i);
            currentString.append(tp.getUnicode());

            String content = currentString.toString().toLowerCase();
            if (content.endsWith(keyword)) {
                startIndex = i - keyword.length() + 1;

                if (startIndex >= 0 && startIndex + keyword.length() <= textPositions.size()) {
                    TextPosition start = textPositions.get(startIndex);
                    TextPosition end = textPositions.get(startIndex + keyword.length() - 1);

                    float x = start.getXDirAdj();
                    float y = start.getYDirAdj();
                    float width = end.getXDirAdj() + end.getWidth() - x;
                    float height = start.getHeightDir();

                    highlights.add(new HighlightInfo(currentPage, x, y, width, height));
                }

                currentString.setLength(0); // reset
            }
        }
    }
}
