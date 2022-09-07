package architecture.studio.services;

import java.io.File;

public interface VisionService {
    
    enum Classifier {
        FACE,
        LBP_FACE,
        HAAR_FACE,
        DNN_CAFFE_FACE,
        EYES
    }; 
    
    public void detect(File source , File target, Classifier classifier ) throws Exception;

}
