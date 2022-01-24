package com.gm.utilities;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.gm.R;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.msgText);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(e.getX() + "" + e.getY()); // set the entry-value as the display text
    }


}