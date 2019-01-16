package com.reactnativenavigation.parse;

import android.graphics.Typeface;
import android.util.Log;

import com.reactnativenavigation.parse.params.Colour;
import com.reactnativenavigation.parse.params.NullColor;
import com.reactnativenavigation.parse.params.NullNumber;
import com.reactnativenavigation.parse.params.NullText;
import com.reactnativenavigation.parse.params.Number;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.parse.parsers.ColorParser;
import com.reactnativenavigation.parse.parsers.NumberParser;
import com.reactnativenavigation.parse.parsers.TextParser;
import com.reactnativenavigation.utils.TypefaceLoader;

import org.json.JSONObject;

import javax.annotation.Nullable;

public class BottomTabCenterFab {

    public Number fabHeight = new NullNumber();
    public Number fabWidth = new NullNumber();
    public Text fabText = new NullText();
    public Colour fabBackgroundColor = new NullColor();
    public Colour textColor = new NullColor();
    public Number fontSize = new NullNumber();
    public Number marginBottom = new NullNumber();
    @Nullable public Typeface fontFamily;

    public static BottomTabCenterFab parse(TypefaceLoader typefaceManager, JSONObject json) {
        BottomTabCenterFab options = new BottomTabCenterFab();
        if (json == null) return options;

        options.fabHeight = NumberParser.parse(json, "FABHeight");
        options.fabWidth = NumberParser.parse(json, "FABWidth");
        options.fabText = TextParser.parse(json, "FABText");
        options.fabBackgroundColor = ColorParser.parse(json, "FABBackgroundColor");
        options.textColor = ColorParser.parse(json, "FABTextColor");
        options.fontSize = NumberParser.parse(json, "FABFontSize");
        options.marginBottom = NumberParser.parse(json, "FABMarginBottom");
        options.fontFamily = typefaceManager.getTypeFace(json.optString("FABFontFamily", ""));

        return options;

    }

    void mergeWith(final BottomTabCenterFab other) {
        if (other.fabHeight.hasValue()) fabHeight = other.fabHeight;
        if (other.fabWidth.hasValue()) fabWidth = other.fabWidth;
        if (other.fabText.hasValue()) fabText = other.fabText;
        if (other.fabBackgroundColor.hasValue()) fabBackgroundColor = other.fabBackgroundColor;
        if (other.textColor.hasValue()) textColor = other.textColor;
        if (other.fontSize.hasValue()) fontSize = other.fontSize;
        if (other.fontFamily!=null) fontFamily = other.fontFamily;
        if (other.marginBottom.hasValue()) marginBottom = other.marginBottom;
    }

    void mergeWithDefault(final BottomTabCenterFab defaultOptions) {
        if (!fabHeight.hasValue()) fabHeight = defaultOptions.fabHeight;
        if (!fabWidth.hasValue()) fabWidth = defaultOptions.fabWidth;
        if (!fabText.hasValue()) fabText = defaultOptions.fabText;
        if (!fabBackgroundColor.hasValue()) fabBackgroundColor = defaultOptions.fabBackgroundColor;
        if (!textColor.hasValue()) textColor = defaultOptions.textColor;
        if (!fontSize.hasValue()) fontSize = defaultOptions.fontSize;
        if (!marginBottom.hasValue()) marginBottom = defaultOptions.marginBottom;
        if (fontFamily == null) fontFamily = defaultOptions.fontFamily;
    }
}
