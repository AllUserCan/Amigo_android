package com.control.amigo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.amigo.R;

public class FloatWindowBigView extends LinearLayout {

	public static int viewWidth;
	public static int viewHeight;
	private ImageButton back;

	public FloatWindowBigView(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		
		back = (ImageButton) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �孵餈����蝘駁憭扳瘚桃�嚗�撱箏��祆筑蝒�
				FloatWindowManager.removeBigWindow(context);
				FloatWindowManager.createSmallWindow(context);
			}
		});
		
		
		
		
	}
}
