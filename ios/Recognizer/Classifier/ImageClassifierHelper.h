//
//  ImageClassifierHelper.h
//  Pods
//
//  Created by Alexandre Em on 24/05/2024.
//

#import "ModelHelper.h"
#import "ImageClassifierHelperOptions.h"
#import "TFLTensorFlowLite.h"

@interface ImageClassifierHelper : NSObject <ModelHelper>

@property NSMutableArray *labels;
@property NSError *error;
@property NSString *modelPath;
@property TFLInterpreter *interpreter;
@property ImageClassifierHelperOptions *options;

- (id)initWithModel: (NSString*) model;
- (id)initWithModel: (NSString*) model options:(ImageClassifierHelperOptions*) options;

@end
