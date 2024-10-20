//
//  ModelHelper.h
//  Pods
//
//  Created by Alexandre Em on 24/05/2024.
//
#import "ModelHelperOptions.h"
@protocol ModelHelper

- (void)initialize;
- (void)clear;
- (const float *)predict: (UIImage*) image;
@end
