#import "Rndouyin.h"
#import <React/RCTEventDispatcher.h>
#import <React/RCTBridge.h>
#import <DouyinOpenSDK/DouyinOpenSDK-umbrella.h>
@implementation Rndouyin

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE()

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_REMAP_METHOD(multiply,
                 multiplyWithA:(nonnull NSNumber*)a withB:(nonnull NSNumber*)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    NSNumber *result = @([a floatValue] * [b floatValue]);
    
    resolve(result);
}

RCT_REMAP_METHOD(registerApp,
                 :(nonnull NSString*)DouYinNewClientKey
                 :(RCTPromiseResolveBlock)resolve
                 :(RCTPromiseRejectBlock)reject)
{
    BOOL res = [[DouyinOpenSDKApplicationDelegate sharedInstance] registerAppId:DouYinNewClientKey];
    if(res){
        resolve(@"ok");
    } else {
        resolve(@"fail");
    }
}


RCT_REMAP_METHOD(dyauth,
                 :(nonnull NSString*)scope
                 :(nonnull NSString*)scope1
                 :(nonnull NSString*)scope0
                 :(nonnull NSString*)state
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    Rndouyin *thiz = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        //Your main thread code goes in here
        NSLog(@"Im on the main thread");
        DouyinOpenSDKAuthRequest *req = [[DouyinOpenSDKAuthRequest alloc] init];
        req.permissions = [NSOrderedSet orderedSetWithObject: scope]; // @"user_info"
        if(![state isEqual:@""]) {
            req.state = state;
        }
        NSMutableOrderedSet *mutableOrderSet = [[NSMutableOrderedSet alloc] init];
        if(![scope1 isEqual:@""]) {
            NSArray *list1 = [scope1 componentsSeparatedByString:@","];
            for(NSString* sc1 in list1){
                [mutableOrderSet addObject:@{ @"permission": sc1, @"defaultChecked" : @"1" }];
            }
        }
        if(![scope0 isEqual:@""]) {
            NSArray *list0 = [scope0 componentsSeparatedByString:@","];
            for(NSString* sc0 in list0){
                [mutableOrderSet addObject:@{ @"permission": sc0, @"defaultChecked" : @"0" }];
            }
        }

        req.additionalPermissions = mutableOrderSet;
        
        UIViewController *vc = [UIApplication sharedApplication].keyWindow.rootViewController;
        
        [req sendAuthRequestViewController:vc completeBlock:^(DouyinOpenSDKAuthResponse * _Nonnull r) {
            NSString *alertString = nil;
            if (r.errCode == 0) {
                alertString = [NSString stringWithFormat:@"Author Success Code : %@, permission : %@",r.code, r.grantedPermissions];
            } else{
                alertString = [NSString stringWithFormat:@"Author failed code : %@, msg : %@",@(r.errCode), r.errString];
            }
            NSMutableDictionary *body = @{@"errCode":@(r.errCode)}.mutableCopy;
            body[@"errStr"] = r.errString;
            body[@"type"] = @"SendAuth.Resp";
            body[@"authCode"] = r.code;
            body[@"grantedPermissions"] = r.grantedPermissions;
            [thiz.bridge.eventDispatcher sendDeviceEventWithName:DouYinEventName body:body];
                    
            resolve(alertString);
        }];
    });
    
}


@end
