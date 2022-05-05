package com.google.developers.mojimaster2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
* Created by Prajwal Maruti Waingankar on 06-05-2022, 02:30
* Copyright (c) 2021 . All rights reserved.
* Email: prajwalwaingankar@gmail.com
* Github: prajwalmw
*/

public class TimerCustomClass extends RelativeLayout {

    public TimerCustomClass(Context context) {
        super(context);
        init();
    }

    public TimerCustomClass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.timercustomview, this);
        TextView textView = findViewById(R.id.textview);
        ImageView imageView = findViewById(R.id.image);
        Button reset = findViewById(R.id.reset);
        Button start = findViewById(R.id.start);
    }
}
