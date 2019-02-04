#import "RNNTabBarPresenter.h"
#import "UITabBarController+RNNOptions.h"
#import <React/RCTConvert.h>

@interface RNNTabBarPresenter()

@property(nonatomic, strong) UIButton *FABButton;
@property(nonatomic, strong) UIView *FABContainerView;

@end

@implementation RNNTabBarPresenter

- (void)applyOptionsOnInit:(RNNNavigationOptions *)initialOptions eventEmitter:(RNNEventEmitter *)eventEmitter {
	
	_eventEmitter = eventEmitter;
	UITabBarController* tabBarController = self.bindedViewController;
	
	Dictionary *fabButtonDictionary = initialOptions.bottomTabs.fabButton;
	NSMutableDictionary *fabButtonOptions = nil;
	if ([fabButtonDictionary hasValue] && initialOptions.bottomTabs.visible.get) {
		fabButtonOptions = (NSMutableDictionary*)[fabButtonDictionary get];
		[self setupFABButtonWithOptions:fabButtonOptions onTabBarController:tabBarController initialOptions:initialOptions];
	}
	
	[tabBarController rnn_setCurrentTabIndex:[initialOptions.bottomTabs.currentTabIndex getWithDefaultValue:0]];
}

- (void)setupFABContainerView: (NSMutableDictionary*)fabButtonDict onTabBarController: (UITabBarController*)tabBarController withInitialOptions: (RNNNavigationOptions *)initialOptions {
	
	_FABContainerView = [[UIView alloc] init];
	CGFloat FABButtonHeight = [[fabButtonDict valueForKey:@"FABHeight"] floatValue];
	
	CGFloat screenWidth = tabBarController.view.frame.size.width;
	CGFloat screenHeight = tabBarController.view.frame.size.height;
	
	CGFloat tabBarHeight = tabBarController.tabBar.frame.size.height;
	CGFloat bottomPadding = 10.0;
	
	CGFloat notchRadius = [initialOptions.bottomTabs.notchRadius getWithDefaultValue: 50.0];
	
	if (@available(iOS 11.0, *)) {
		UIWindow *window = UIApplication.sharedApplication.keyWindow;
		bottomPadding = window.safeAreaInsets.bottom;
	}
	
	_FABContainerView.frame = CGRectMake((screenWidth/2) - notchRadius, screenHeight - tabBarHeight - (FABButtonHeight/2) - bottomPadding, notchRadius *2, tabBarHeight + FABButtonHeight/2);
	
	
	[_FABContainerView setBackgroundColor:[UIColor clearColor]];
	
	UIView *topView = [[UIView alloc] initWithFrame:CGRectMake(0, (FABButtonHeight/2) - 0.3, notchRadius * 2, 1)];
	[topView setBackgroundColor:[UIColor whiteColor]];
	[_FABContainerView addSubview:topView];
	
	
	UIView *notchView = [[UIView alloc] initWithFrame:CGRectMake(0, FABButtonHeight/2, notchRadius * 2, notchRadius + 1.0)];
	[notchView setBackgroundColor:[UIColor clearColor]];
	
	[_FABContainerView addSubview:notchView];
	
	
	UIBezierPath* bezierPath = [UIBezierPath bezierPath];
	[bezierPath moveToPoint: CGPointMake(0, 0)];
	[bezierPath addArcWithCenter:CGPointMake(notchRadius, 0) radius:notchRadius startAngle:3.14 endAngle:0 clockwise:NO];
	
	
	CAShapeLayer *layer = [CAShapeLayer layer];
	layer.path = bezierPath.CGPath;
	layer.lineWidth = 0.5;
	
	layer.strokeColor = [UIColor lightGrayColor].CGColor;
	layer.fillColor = [UIColor clearColor].CGColor;
	[notchView.layer addSublayer:layer];
	notchView.layer.masksToBounds = YES;
	
	[tabBarController.view addSubview:_FABContainerView];
	
	
	
}

- (void)setupFABButtonWithOptions: (NSMutableDictionary*)fabButton onTabBarController: (UITabBarController*)tabBarController initialOptions: (RNNNavigationOptions *)initialOptions {
	
	
	
	[self setupFABContainerView:fabButton onTabBarController:tabBarController withInitialOptions:initialOptions];
	
	_FABButton = [[UIButton alloc] init];
	[_FABButton addTarget:self
				   action:@selector(FABButtonTapped)
		 forControlEvents:UIControlEventTouchUpInside];
	
	CGFloat FABButtonWidth = [[fabButton valueForKey:@"FABWidth"] floatValue];
	CGFloat FABButtonHeight = [[fabButton valueForKey:@"FABHeight"] floatValue];
	CGFloat notchRadius = [initialOptions.bottomTabs.notchRadius getWithDefaultValue: 50.0];
	CGFloat bottomPadding = 10.0;
	
	if (@available(iOS 11.0, *)) {
		UIWindow *window = UIApplication.sharedApplication.keyWindow;
		bottomPadding = window.safeAreaInsets.bottom;
	}
	
	_FABButton.frame = CGRectMake(notchRadius - (FABButtonWidth/2), 0 , FABButtonWidth, FABButtonHeight);
	
	//Clip/Clear the other pieces whichever outside the rounded corner
	_FABButton.clipsToBounds = YES;
	
	//half of the width
	_FABButton.layer.cornerRadius = FABButtonWidth/2.0f;
	
	Image *fabBackgroundImage = [ImageParser parse:fabButton key:@"FABBackgroundImage"];
	UIImage *backgroundImage = [fabBackgroundImage getWithDefaultValue:nil];
	[_FABButton setBackgroundImage:backgroundImage forState:UIControlStateNormal];
	
	//set Button Title
	[_FABButton setTitle:[[TextParser parse:fabButton key:@"FABText"] getWithDefaultValue:@"Sell car"] forState:UIControlStateNormal];
	
	//set Button Title color
	[_FABButton setTitleColor:[[ColorParser parse:fabButton key:@"FABTextColor"] getWithDefaultValue:[UIColor whiteColor]] forState:UIControlStateNormal];
	
	//set Button Title fontfamily
	_FABButton.titleLabel.font = [UIFont fontWithName:[[TextParser parse:fabButton key:@"FABFontFamily"] getWithDefaultValue:[UIFont systemFontOfSize:12.0]] size:[[fabButton valueForKey:@"FABFontSize"] floatValue]];
	
	[_FABContainerView addSubview:_FABButton];
}

- (void)FABButtonTapped {
	[_eventEmitter sendBottomTabFABSelected];
}

- (void)applyOptions:(RNNNavigationOptions *)options {
	UITabBarController* tabBarController = self.bindedViewController;
	
	[tabBarController rnn_setTabBarTestID:[options.bottomTabs.testID getWithDefaultValue:nil]];
	[tabBarController rnn_setTabBarBackgroundColor:[options.bottomTabs.backgroundColor getWithDefaultValue:nil]];
	[tabBarController rnn_setTabBarTranslucent:[options.bottomTabs.translucent getWithDefaultValue:NO]];
	[tabBarController rnn_setTabBarHideShadow:[options.bottomTabs.hideShadow getWithDefaultValue:NO]];
	[tabBarController rnn_setTabBarStyle:[RCTConvert UIBarStyle:[options.bottomTabs.barStyle getWithDefaultValue:@"default"]]];
	
	[_FABContainerView setHidden:!options.bottomTabs.visible.get];
	[_FABButton setHidden:!options.bottomTabs.visible.get];
	[tabBarController rnn_setTabBarVisible:[options.bottomTabs.visible getWithDefaultValue:YES]];
}

- (void)mergeOptions:(RNNNavigationOptions *)newOptions currentOptions:(RNNNavigationOptions *)currentOptions defaultOptions:(RNNNavigationOptions *)defaultOptions {
	[super mergeOptions:newOptions currentOptions:currentOptions defaultOptions:defaultOptions];
	
	UITabBarController* tabBarController = self.bindedViewController;
	
	if (newOptions.bottomTabs.currentTabIndex.hasValue) {
		[tabBarController rnn_setCurrentTabIndex:newOptions.bottomTabs.currentTabIndex.get];
		[newOptions.bottomTabs.currentTabIndex consume];
	}
	
	if (newOptions.bottomTabs.currentTabId.hasValue) {
		[tabBarController rnn_setCurrentTabID:newOptions.bottomTabs.currentTabId.get];
		[newOptions.bottomTabs.currentTabId consume];
	}
	
	if (newOptions.bottomTabs.testID.hasValue) {
		[tabBarController rnn_setTabBarTestID:newOptions.bottomTabs.testID.get];
	}
	
	if (newOptions.bottomTabs.backgroundColor.hasValue) {
		[tabBarController rnn_setTabBarBackgroundColor:newOptions.bottomTabs.backgroundColor.get];
	}
	
	if (newOptions.bottomTabs.barStyle.hasValue) {
		[tabBarController rnn_setTabBarStyle:[RCTConvert UIBarStyle:newOptions.bottomTabs.barStyle.get]];
	}
	
	if (newOptions.bottomTabs.translucent.hasValue) {
		[tabBarController rnn_setTabBarTranslucent:newOptions.bottomTabs.translucent.get];
	}
	
	if (newOptions.bottomTabs.hideShadow.hasValue) {
		[tabBarController rnn_setTabBarHideShadow:newOptions.bottomTabs.hideShadow.get];
	}
	
	if (newOptions.bottomTabs.visible.hasValue) {
		[_FABContainerView setHidden:!newOptions.bottomTabs.visible.get];
		[_FABButton setHidden:!newOptions.bottomTabs.visible.get];
		[tabBarController rnn_setTabBarVisible:newOptions.bottomTabs.visible.get];
	}
	
}

@end
