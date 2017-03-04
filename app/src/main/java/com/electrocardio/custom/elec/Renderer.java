package com.electrocardio.custom.elec;

import android.graphics.Color;
import android.graphics.Typeface;

import java.io.Serializable;


public class Renderer implements Serializable {

    private String siatChartLabel = "";
    private boolean siatShowLabel = false;
    private float siatChartTextSize = 24;
    public static final int NO_COLOR = 0;
    public static final int BACKGROUND_COLOR = 0xFFF0F0F0;
    public static final int TEXT_COLOR = Color.BLACK;
    private static final Typeface REGULAR_TEXT_FONT = Typeface
            .create(Typeface.SERIF, Typeface.NORMAL);
    private String siatTextTypefaceName = REGULAR_TEXT_FONT.toString();
    private int siatTextTypefaceStyle = Typeface.NORMAL;
    private Typeface siatTextTypeface;
    private int siatBackgroundColor;
    private boolean siatShowAxes = true;
    private int siatAxesColor = Color.RED;
    private int siatLineStep = 1;
    private boolean siatScrollable = true;

    public String getSiatChartLabel() {
        return siatChartLabel;
    }

    public void setSiatChartLabel(String siatChartLabel) {
        this.siatChartLabel = siatChartLabel;
    }


    public float getSiatChartTextSize() {
        return siatChartTextSize;
    }

    public void setSiatChartTextSize(float siatChartTextSize) {
        this.siatChartTextSize = siatChartTextSize;
    }

    public static Typeface getRegularTextFont() {
        return REGULAR_TEXT_FONT;
    }


    public String getSiatTextTypefaceName() {
        return siatTextTypefaceName;
    }


    public void setSiatTextTypefaceName(String siatTextTypefaceName) {
        this.siatTextTypefaceName = siatTextTypefaceName;
    }

    public int getSiatTextTypefaceStyle() {
        return siatTextTypefaceStyle;
    }


    public void setSiatTextTypefaceStyle(int siatTextTypefaceStyle) {
        this.siatTextTypefaceStyle = siatTextTypefaceStyle;
    }

    public int getSiatAxesColor() {
        return siatAxesColor;
    }


    public void setSiatAxesColor(int siatAxesColor) {
        this.siatAxesColor = siatAxesColor;
    }

    public int getSiatBackgroundColor() {
        return siatBackgroundColor;
    }


    public void setSiatBackgroundColor(int siatBackgroundColor) {
        this.siatBackgroundColor = siatBackgroundColor;
    }

    public Typeface getSiatTextTypeface() {
        return siatTextTypeface;
    }


    public void setSiatTextTypeface(Typeface siatTextTypeface) {
        this.siatTextTypeface = siatTextTypeface;
    }


    public boolean isSiatShowAxes() {
        return siatShowAxes;
    }

    public void setSiatShowAxes(boolean siatShowAxes) {
        this.siatShowAxes = siatShowAxes;
    }

    public int getSiatLineStep() {
        return siatLineStep;
    }


    public void setSiatLineStep(int siatLineStep) {
        if(siatLineStep<1) siatLineStep=1;
        if(siatLineStep>10) siatLineStep=10;
        this.siatLineStep = siatLineStep;
    }

    public boolean isSiatScrollable() {
        return siatScrollable;
    }


    public void setSiatScrollable(boolean siatScrollable) {
        this.siatScrollable = siatScrollable;
    }


    public boolean isSiatShowLabel() {
        return siatShowLabel;
    }


    public void setSiatShowLabel(boolean siatShowLabel) {
        this.siatShowLabel = siatShowLabel;
    }
}
