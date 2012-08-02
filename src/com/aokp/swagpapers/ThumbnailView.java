
package com.aokp.swagpapers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ThumbnailView extends RelativeLayout {

    TextView name, author;
    ImageView thumbnail = null;

    public ThumbnailView(Context context) {
        this(context, null);
    }

    public ThumbnailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbnailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init();
    }

    void init() {
        setVisibility(View.INVISIBLE);
        View v = View.inflate(getContext(), R.layout.thumbnail, null);
        addView(v);
        name = (TextView) findViewById(R.id.name);
        author = (TextView) findViewById(R.id.author);
        thumbnail = (ImageView) findViewById(R.id.thumb);
    }

    public ImageView getThumbnail() {
        return thumbnail;
    }

    public TextView getName() {
        return name;
    }

    public TextView getAuthor() {
        return author;
    }

}
