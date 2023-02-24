
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNSmsAutoReadSpec.h"

@interface SmsAutoRead : NSObject <NativeSmsAutoReadSpec>
#else
#import <React/RCTBridgeModule.h>

@interface SmsAutoRead : NSObject <RCTBridgeModule>
#endif

@end
