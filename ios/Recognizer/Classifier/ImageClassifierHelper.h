//
//  ImageClassifierHelper.h
//  Pods
//
//  Created by Alexandre Em on 24/05/2024.
//

#import "ModelHelper.h"
#import "ImageClassifierHelperOptions.h"
#import "TFLTensorFlowLite.h"

@interface ImageClassifierHelper : NSObject <ModelHelper> {
    NSMutableArray *labels;
    NSError *error;
    TFLInterpreter *interpreter;
    ImageClassifierHelperOptions *options;
}

@end
