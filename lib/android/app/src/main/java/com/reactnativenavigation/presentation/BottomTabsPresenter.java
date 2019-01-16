package com.reactnativenavigation.presentation;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reactnativenavigation.anim.BottomTabsAnimator;
import com.reactnativenavigation.libs.ahbottomnavigation.AHBottomNavigation;
import com.reactnativenavigation.parse.AnimationsOptions;
import com.reactnativenavigation.parse.BottomTabsOptions;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.utils.UiUtils;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.viewcontrollers.bottomtabs.BottomTabFinder;
import com.reactnativenavigation.viewcontrollers.bottomtabs.TabSelector;
import com.reactnativenavigation.views.BottomTabs;
import com.reactnativenavigation.views.Component;
import com.reactnativenavigation.views.bottomTabs.CurvedNotchCenter;

import java.util.List;

public class BottomTabsPresenter {
    private Context context;
    private final BottomTabFinder bottomTabFinder;
    private final List<ViewController> tabs;
    private Options defaultOptions;
    private BottomTabs bottomTabs;
    private BottomTabsAnimator animator;
    private TabSelector tabSelector;
    private FrameLayout centerFabLayout;

    public BottomTabsPresenter(List<ViewController> tabs, Options defaultOptions) {
        this(null, tabs, defaultOptions);
    }

    public BottomTabsPresenter(Context context, List<ViewController> tabs, Options defaultOptions) {
        this.context = context;
        this.tabs = tabs;
        this.defaultOptions = defaultOptions;
        this.bottomTabFinder = new BottomTabFinder(tabs);
    }

    public void setDefaultOptions(Options defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    public void bindView(BottomTabs bottomTabs, TabSelector tabSelector) {
        this.bottomTabs = bottomTabs;
        this.tabSelector = tabSelector;
        this.centerFabLayout = null;
        animator = new BottomTabsAnimator(bottomTabs);
    }

    public void bindView(BottomTabs bottomTabs, TabSelector tabSelector, FrameLayout centerFabLayout) {
        this.bottomTabs = bottomTabs;
        this.tabSelector = tabSelector;
        this.centerFabLayout = centerFabLayout;
        animator = new BottomTabsAnimator(bottomTabs);
    }

    public void applyLayoutParamsOptions(Options options, int tabIndex) {
        Options withDefaultOptions = options.copy().withDefaultOptions(defaultOptions);
        applyDrawBehind(withDefaultOptions.bottomTabsOptions, tabIndex);
    }

    public void mergeOptions(Options options) {
        mergeBottomTabsOptions(options.bottomTabsOptions, options.animations);
    }

    public void applyOptions(Options options) {
        Options withDefaultOptions = options.copy().withDefaultOptions(defaultOptions);
        applyBottomTabsOptions(withDefaultOptions.bottomTabsOptions, withDefaultOptions.animations);
    }

    public void applyChildOptions(Options options, Component child) {
        int tabIndex = bottomTabFinder.findByComponent(child);
        if (tabIndex >= 0) {
            Options withDefaultOptions = options.copy().withDefaultOptions(defaultOptions);
            applyBottomTabsOptions(withDefaultOptions.bottomTabsOptions, withDefaultOptions.animations);
            applyDrawBehind(withDefaultOptions.bottomTabsOptions, tabIndex);
        }
    }

    public void mergeChildOptions(Options options, Component child) {
        mergeBottomTabsOptions(options.bottomTabsOptions, options.animations);
        int tabIndex = bottomTabFinder.findByComponent(child);
        if (tabIndex >= 0) mergeDrawBehind(options.bottomTabsOptions, tabIndex);
    }

    private void mergeBottomTabsOptions(BottomTabsOptions options, AnimationsOptions animations) {
        if (options.titleDisplayMode.hasValue()) {
            bottomTabs.setTitleState(options.titleDisplayMode.toState());
        }
        if (options.backgroundColor.hasValue()) {
            bottomTabs.setBackgroundColor(options.backgroundColor.get());
        }
        if (options.currentTabIndex.hasValue()) {
            int tabIndex = options.currentTabIndex.get();
            if (tabIndex >= 0) tabSelector.selectTab(tabIndex);
        }
        if (options.testId.hasValue()) {
            bottomTabs.setTag(options.testId.get());
        }
        if (options.currentTabId.hasValue()) {
            int tabIndex = bottomTabFinder.findByControllerId(options.currentTabId.get());
            if (tabIndex >= 0) tabSelector.selectTab(tabIndex);
        }
        if (options.visible.isTrue()) {
            if (options.animate.isTrueOrUndefined()) {
                animator.show(animations);
            } else {
                bottomTabs.restoreBottomNavigation(false);
            }
        }
        if (options.visible.isFalse()) {
            if (options.animate.isTrueOrUndefined()) {
                animator.hide(animations);
            } else {
                bottomTabs.hideBottomNavigation(false);
            }
        }
    }

    private void applyDrawBehind(BottomTabsOptions options, @IntRange(from = 0) int tabIndex) {
        ViewGroup tab = tabs.get(tabIndex).getView();
        MarginLayoutParams lp = (MarginLayoutParams) tab.getLayoutParams();
        if (options.drawBehind.isTrue()) {
            lp.bottomMargin = 0;
        }
        if (options.visible.isTrueOrUndefined() && options.drawBehind.isFalseOrUndefined()) {
            if (bottomTabs.getHeight() == 0) {
                UiUtils.runOnPreDrawOnce(bottomTabs, () -> lp.bottomMargin = bottomTabs.getHeight());
            } else {
                lp.bottomMargin = bottomTabs.getHeight();
            }
        }
    }

    private void mergeDrawBehind(BottomTabsOptions options, int tabIndex) {
        ViewGroup tab = tabs.get(tabIndex).getView();
        MarginLayoutParams lp = (MarginLayoutParams) tab.getLayoutParams();
        if (options.drawBehind.isTrue()) {
            lp.bottomMargin = 0;
        }
        if (options.visible.isTrue() && options.drawBehind.isFalse()) {
            if (bottomTabs.getHeight() == 0) {
                UiUtils.runOnPreDrawOnce(bottomTabs, () -> lp.bottomMargin = bottomTabs.getHeight());
            } else {
                lp.bottomMargin = bottomTabs.getHeight();
            }
        }
    }

    private void applyBottomTabsOptions(BottomTabsOptions options, AnimationsOptions animationsOptions) {
        bottomTabs.setTitleState(options.titleDisplayMode.get(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE));
        if (options.notchRadius.get(0) > 0) {
            bottomTabs.setBackground(new CurvedNotchCenter(Color.WHITE, options.notchRadius.get(0)));
        } else {
            bottomTabs.setBackgroundColor(options.backgroundColor.get(Color.WHITE));
        }
        if (options.currentTabIndex.hasValue()) {
            int tabIndex = options.currentTabIndex.get();
            if (tabIndex >= 0) tabSelector.selectTab(tabIndex);
        }
        if (options.testId.hasValue()) bottomTabs.setTag(options.testId.get());
        if (options.currentTabId.hasValue()) {
            int tabIndex = bottomTabFinder.findByControllerId(options.currentTabId.get());
            if (tabIndex >= 0) tabSelector.selectTab(tabIndex);
        }
        if (options.visible.isTrueOrUndefined()) {
            if (options.animate.isTrueOrUndefined()) {
                animator.show(animationsOptions);
            } else {
                bottomTabs.restoreBottomNavigation(false);
            }
            centerFabLayout.setVisibility(View.VISIBLE);
        }
        if (options.visible.isFalse()) {
            if (options.animate.isTrueOrUndefined()) {
                animator.hide(animationsOptions);
            } else {
                bottomTabs.hideBottomNavigation(false);
            }
            centerFabLayout.setVisibility(View.GONE);
        }
        if (options.elevation.hasValue()) {
            bottomTabs.setUseElevation(true, options.elevation.get().floatValue());
        }

    }
}
