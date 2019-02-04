package com.reactnativenavigation.viewcontrollers.bottomtabs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reactnativenavigation.libs.ahbottomnavigation.AHBottomNavigation;
import com.reactnativenavigation.libs.ahbottomnavigation.AHBottomNavigationItem;
import com.reactnativenavigation.parse.BottomTabOptions;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.presentation.BottomTabPresenter;
import com.reactnativenavigation.presentation.BottomTabsPresenter;
import com.reactnativenavigation.presentation.Presenter;
import com.reactnativenavigation.react.EventEmitter;
import com.reactnativenavigation.utils.CommandListener;
import com.reactnativenavigation.utils.ImageLoader;
import com.reactnativenavigation.utils.ImageLoadingListenerAdapter;
import com.reactnativenavigation.utils.UiUtils;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.ParentController;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.views.BottomTabs;
import com.reactnativenavigation.views.Component;

import java.util.Collection;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static com.reactnativenavigation.utils.CollectionUtils.forEach;
import static com.reactnativenavigation.utils.CollectionUtils.map;

public class BottomTabsController extends ParentController implements AHBottomNavigation.OnTabSelectedListener, TabSelector {

	private BottomTabs bottomTabs;
	private List<ViewController> tabs;
    private EventEmitter eventEmitter;
    private ImageLoader imageLoader;
    private BottomTabsPresenter presenter;
    private BottomTabPresenter tabPresenter;
    private FrameLayout centerFabLayout;

    public BottomTabsController(Activity activity, List<ViewController> tabs, ChildControllersRegistry childRegistry, EventEmitter eventEmitter, ImageLoader imageLoader, String id, Options initialOptions, Presenter presenter, BottomTabsPresenter bottomTabsPresenter, BottomTabPresenter bottomTabPresenter) {
		super(activity, childRegistry, id, presenter, initialOptions);
        this.tabs = tabs;
        this.eventEmitter = eventEmitter;
        this.imageLoader = imageLoader;
        this.presenter = bottomTabsPresenter;
        this.tabPresenter = bottomTabPresenter;
        forEach(tabs, (tab) -> tab.setParentController(this));
    }

    @Override
    public void setDefaultOptions(Options defaultOptions) {
        super.setDefaultOptions(defaultOptions);
        presenter.setDefaultOptions(defaultOptions);
        tabPresenter.setDefaultOptions(defaultOptions);
    }

    @NonNull
	@Override
	protected ViewGroup createView() {
		RelativeLayout root = new RelativeLayout(getActivity());
		bottomTabs = createBottomTabs();
        centerFabLayout = createCenterFab();
        presenter.bindView(bottomTabs, this, centerFabLayout);
        tabPresenter.bindView(bottomTabs);
        bottomTabs.setOnTabSelectedListener(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		lp.addRule(ALIGN_PARENT_BOTTOM);
		lp.addRule(CENTER_HORIZONTAL);
		root.addView(bottomTabs, lp);
        bottomTabs.addItems(createTabs());
        if (centerFabLayout != null) {
            root.addView(centerFabLayout);
            bottomTabs.disableItemAtPosition((int)Math.floor(tabs.size()/2));
        }
        attachTabs(root);
        return root;
	}

    @NonNull
    protected BottomTabs createBottomTabs() {
        return new BottomTabs(getActivity());
    }

    private FrameLayout createCenterFab() {
        if (initialOptions.bottomTabsOptions.fabButton != null) {
            FrameLayout frameLayout = new FrameLayout(getActivity());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                frameLayout.setElevation(100);
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lp.addRule(ALIGN_PARENT_BOTTOM);
            lp.addRule(CENTER_HORIZONTAL);
            lp.width = (int)UiUtils.dpToPx(getActivity(), (float)initialOptions.bottomTabsOptions.fabButton.fabWidth.get(10));
            lp.height = (int)UiUtils.dpToPx(getActivity(), (float)initialOptions.bottomTabsOptions.fabButton.fabHeight.get(0));
            lp.setMargins(0,0,0, (int)UiUtils.dpToPx(getActivity(), (float)initialOptions.bottomTabsOptions.fabButton.marginBottom.get(0)));
            frameLayout.setLayoutParams(lp);
            ImageView imageView = new ImageView(getActivity());
            FrameLayout.LayoutParams fabLp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            imageView.setLayoutParams(fabLp);

            imageView.setTag("fabImageBg");
            if (initialOptions.bottomTabsOptions.fabButton.fabBackgroundImage.hasValue())

                imageLoader.loadIcon(getActivity(), initialOptions.bottomTabsOptions.fabButton.fabBackgroundImage.get(), new ImageLoadingListenerAdapter() {
                @Override
                public void onComplete(@NonNull Drawable drawable) {
                    imageView.setImageBitmap(((BitmapDrawable)drawable).getBitmap());
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventEmitter.emitBottomTabFABSelected();
                }
            });
            TextView textView = new TextView(getActivity());
            textView.setTag("textView");
            FrameLayout.LayoutParams tvLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            tvLp.gravity = Gravity.CENTER;
            textView.setLayoutParams(tvLp);

            textView.setText(initialOptions.bottomTabsOptions.fabButton.fabText.get(""));
            textView.setTextColor(initialOptions.bottomTabsOptions.fabButton.textColor.get(Color.WHITE));
            textView.setTextSize(UiUtils.dpToSp(getActivity(), initialOptions.bottomTabsOptions.fabButton.fontSize.get(12)));
            textView.setTypeface(initialOptions.bottomTabsOptions.fabButton.fontFamily);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setElevation(20);
            }

            frameLayout.addView(imageView);
            frameLayout.addView(textView);

            return frameLayout;
        }
        return null;
    }

    @Override
    public void applyOptions(Options options) {
        super.applyOptions(options);
        bottomTabs.disableItemsCreation();
        presenter.applyOptions(options);
        tabPresenter.applyOptions();
        bottomTabs.enableItemsCreation();
        this.options.bottomTabsOptions.clearOneTimeOptions();
        this.initialOptions.bottomTabsOptions.clearOneTimeOptions();
    }

    @Override
    public void mergeOptions(Options options) {
        presenter.mergeOptions(options);
        super.mergeOptions(options);
    }

    @Override
    public void applyChildOptions(Options options, Component child) {
        super.applyChildOptions(options, child);
        presenter.applyChildOptions(resolveCurrentOptions(), child);
        performOnParentController(parentController ->
                ((ParentController) parentController).applyChildOptions(
                        this.options.copy()
                                .clearBottomTabsOptions()
                                .clearBottomTabOptions(),
                        child
                )
        );
    }

    @Override
    public void mergeChildOptions(Options options, ViewController childController, Component child) {
        super.mergeChildOptions(options, childController, child);
        presenter.mergeChildOptions(options, child);
        tabPresenter.mergeChildOptions(options, child);
        performOnParentController(parentController ->
                ((ParentController) parentController).mergeChildOptions(options.copy().clearBottomTabsOptions(), childController, child)
        );
    }

    @Override
	public boolean handleBack(CommandListener listener) {
		return !tabs.isEmpty() && tabs.get(bottomTabs.getCurrentItem()).handleBack(listener);
	}

    @Override
    public void sendOnNavigationButtonPressed(String buttonId) {
        getCurrentChild().sendOnNavigationButtonPressed(buttonId);
    }

    @Override
    protected ViewController getCurrentChild() {
        return tabs.get(bottomTabs.getCurrentItem());
    }

    @Override
    public boolean onTabSelected(int index, boolean wasSelected) {
        eventEmitter.emitBottomTabSelected(bottomTabs.getCurrentItem(), index);
        if (wasSelected) return false;
        selectTab(index);
        return false;
	}

	private List<AHBottomNavigationItem> createTabs() {
		if (tabs.size() > 5) throw new RuntimeException("Too many tabs!");
        return map(tabs, tab -> {
            BottomTabOptions options = tab.resolveCurrentOptions().bottomTabOptions;
            return new AHBottomNavigationItem(
                    options.text.get(""),
                    imageLoader.loadIcon(getActivity(), options.icon.get()),
                    imageLoader.loadIcon(getActivity(), options.selectedIcon.get(" ")),
                    options.testId.get("")
            );
        });
	}

    private void attachTabs(RelativeLayout root) {
        for (int i = 0; i < tabs.size(); i++) {
            ViewGroup tab = tabs.get(i).getView();
            tab.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            Options options = resolveCurrentOptions();
            presenter.applyLayoutParamsOptions(options, i);
            if (i != 0) tab.setVisibility(View.INVISIBLE);
            root.addView(tab);
        }
    }

    public int getSelectedIndex() {
		return bottomTabs.getCurrentItem();
	}

	@NonNull
	@Override
	public Collection<ViewController> getChildControllers() {
		return tabs;
	}

    @Override
    public void selectTab(final int newIndex) {
        getCurrentView().setVisibility(View.INVISIBLE);
        bottomTabs.setCurrentItem(newIndex, false);
        getCurrentView().setVisibility(View.VISIBLE);
    }

    @NonNull
    private ViewGroup getCurrentView() {
        return tabs.get(bottomTabs.getCurrentItem()).getView();
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public BottomTabs getBottomTabs() {
        return bottomTabs;
    }
}
