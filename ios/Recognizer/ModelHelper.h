//
//  ModelHelper.h
//  Pods
//
//  Created by Alexandre Em on 24/05/2024.
//

@protocol ModelHelper

@property NSData* results;

- (void)initialize;
- (void)clear;
- (void)predict: (UIImage*) image;
@end
