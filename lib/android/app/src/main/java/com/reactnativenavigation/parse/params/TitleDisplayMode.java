package com.reactnativenavigation.parse.params;

import android.support.annotation.NonNull;


import com.reactnativenavigation.libs.ahbottomnavigation.AHBottomNavigation;

import javax.annotation.Nullable;

public enum TitleDisplayMode {
    ALWAYS_SHOW(AHBottomNavigation.TitleState.ALWAYS_SHOW), SHOW_WHEN_ACTIVE(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE), ALWAYS_HIDE(AHBottomNavigation.TitleState.ALWAYS_HIDE), UNDEFINED(null);

    public static TitleDisplayMode fromString(String mode) {
        switch (mode) {
            case Constants.ALWAYS_SHOW:
                return ALWAYS_SHOW;
            case Constants.SHOW_WHEN_ACTIVE:
                return SHOW_WHEN_ACTIVE;
            case Constants.ALWAYS_HIDE:
                return ALWAYS_HIDE;
            default:
                return UNDEFINED;
        }
    }

    @Nullable private AHBottomNavigation.TitleState state;

    TitleDisplayMode(@Nullable AHBottomNavigation.TitleState state) {
        this.state = state;
    }

    public boolean hasValue() {
        return state != null;
    }

    public AHBottomNavigation.TitleState get(@NonNull AHBottomNavigation.TitleState defaultValue) {
        return state == null ? defaultValue : state;
    }

    @NonNull
    public AHBottomNavigation.TitleState toState() {
        if (state == null) throw new RuntimeException("TitleDisplayMode is undefined");
        return state;
    }

    private static class Constants {
        static final String ALWAYS_SHOW = "alwaysShow";
        static final String SHOW_WHEN_ACTIVE = "showWhenActive";
        static final String ALWAYS_HIDE = "alwaysHide";
    }
}
