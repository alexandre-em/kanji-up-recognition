//
//  ImageClassifierHelperOptions.h
//  Pods
//
//  Created by Alexandre Em on 25/05/2024.
//
#import "ModelHelperOptions.h"

#define DEFAULT_THRESHOLD 0.004
#define DEFAULT_MAX_RESULT 20
#define DEFAULT_NB_THREADS 0

#define DEFAULT_IMAGE_WIDTH 64;
#define DEFAULT_IMAGE_HEIGHT 64;

@interface ImageClassifierHelperOptions: ModelHelperOptions

@property int maxResults;
@property int nbThreads;
@property float threshold;

// Input format
@property int imageWidth;
@property int imageHeight;

@end
