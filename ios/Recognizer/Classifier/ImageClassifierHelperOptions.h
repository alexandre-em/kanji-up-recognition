//
//  ImageClassifierHelperOptions.h
//  Pods
//
//  Created by Alexandre Em on 25/05/2024.
//
#import "ModelHelperOptions.h"


@interface ImageClassifierHelperOptions: NSObject<ModelHelperOptions>

@property float threshold;
@property int maxResults;
@property int nbThreads;

@property int imageWidth;
@property int imageHeight;

@end
