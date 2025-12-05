package com.example.sportine.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {
    private Paint paintCyan;
    private Paint paintGreen;
    private Path pathTopCyan;
    private Path pathTopGreen;
    private Path pathBottomCyan;
    private Path pathBottomGreen;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintCyan = new Paint();
        paintCyan.setColor(0xFFB3E5FC);
        paintCyan.setStyle(Paint.Style.FILL);
        paintCyan.setAntiAlias(true);

        paintGreen = new Paint();
        paintGreen.setColor(0xFFC8E6C9);
        paintGreen.setStyle(Paint.Style.FILL);
        paintGreen.setAntiAlias(true);

        pathTopCyan = new Path();
        pathTopGreen = new Path();
        pathBottomCyan = new Path();
        pathBottomGreen = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Onda superior cyan
        pathTopCyan.reset();
        pathTopCyan.moveTo(0, 0);
        pathTopCyan.quadTo(width * 0.25f, 60, width * 0.5f, 40);
        pathTopCyan.quadTo(width * 0.75f, 20, width, 50);
        pathTopCyan.lineTo(width, 0);
        pathTopCyan.close();
        canvas.drawPath(pathTopCyan, paintCyan);

        // Onda superior verde
        pathTopGreen.reset();
        pathTopGreen.moveTo(0, 30);
        pathTopGreen.quadTo(width * 0.25f, 80, width * 0.5f, 60);
        pathTopGreen.quadTo(width * 0.75f, 40, width, 70);
        pathTopGreen.lineTo(width, 0);
        pathTopGreen.lineTo(0, 0);
        pathTopGreen.close();
        canvas.drawPath(pathTopGreen, paintGreen);

        // Onda inferior cyan
        pathBottomCyan.reset();
        pathBottomCyan.moveTo(0, height);
        pathBottomCyan.quadTo(width * 0.25f, height - 60, width * 0.5f, height - 40);
        pathBottomCyan.quadTo(width * 0.75f, height - 20, width, height - 50);
        pathBottomCyan.lineTo(width, height);
        pathBottomCyan.close();
        canvas.drawPath(pathBottomCyan, paintCyan);

        // Onda inferior verde
        pathBottomGreen.reset();
        pathBottomGreen.moveTo(0, height - 30);
        pathBottomGreen.quadTo(width * 0.25f, height - 80, width * 0.5f, height - 60);
        pathBottomGreen.quadTo(width * 0.75f, height - 40, width, height - 70);
        pathBottomGreen.lineTo(width, height);
        pathBottomGreen.lineTo(0, height);
        pathBottomGreen.close();
        canvas.drawPath(pathBottomGreen, paintGreen);
    }
}