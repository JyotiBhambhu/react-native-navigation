#import "RNNBasePresenter.h"
#import "RNNEventEmitter.h"

@interface RNNTabBarPresenter : RNNBasePresenter

@property (nonatomic, strong) RNNEventEmitter *eventEmitter;
- (void)applyOptionsOnInit:(RNNNavigationOptions *)initialOptions eventEmitter:(RNNEventEmitter *)eventEmitter;
@end
