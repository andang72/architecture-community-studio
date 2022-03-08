package architecture.studio.service;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier; 

import architecture.ee.service.Repository;


public class OpenCvVisionService implements VisionService {
    
    public enum Gender {
        MALE,
        FEMALE,
        NOT_RECOGNIZED
    }

    public static final Size IMAGE_DATA_SIZE_300x300 = new Size(300, 300);
    
    public static final Size IMAGE_DATA_SIZE_224X224 = new Size(224, 224); 

    protected Logger log = LoggerFactory.getLogger(getClass().getName());
 
    @Inject
	@Qualifier("repository")
	private Repository repository;
    
    private com.google.common.cache.LoadingCache<String, CascadeClassifier> cascadeClassifiers; 

    private com.google.common.cache.LoadingCache<MultiKey<String>, Net> deepLearningNetworks; 

	@PostConstruct
	public void initialize() throws Exception {
        createCascadeClassifierCache(10L, 1L); 
        createDeepLearningNetworkCache(10L, 1L); 
	}
    
    private void createCascadeClassifierCache (Long maximumSize, Long duration) {  
		cascadeClassifiers = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.HOURS).build(		
			new CacheLoader<String, CascadeClassifier>(){			
				public CascadeClassifier load(String path ) throws Exception { 
                    File file = repository.getConfigRoot().getFile(path);
                    log.debug("Create CascadeClassifier with ({}, {})", file.getAbsolutePath() );
                    return new CascadeClassifier(file.getAbsolutePath());
			    }
            }
		);
	}

    private void createDeepLearningNetworkCache (Long maximumSize, Long duration) {  
		deepLearningNetworks = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.HOURS).build(		
			new CacheLoader<MultiKey<String>, Net>(){			
				public Net load(MultiKey<String> key ) throws Exception {  
                    File proto = repository.getConfigRoot().getFile(key.getKey(0));
                    File model = repository.getConfigRoot().getFile(key.getKey(1)); 
                    log.debug("Create Deep Learning Network with ({}, {})", proto.getAbsolutePath(), model.getAbsolutePath() );
                    return Dnn.readNetFromCaffe( proto.getAbsolutePath(), model.getAbsolutePath()); 
			    }
            }
		);
	}

    private CascadeClassifier getHaarFaceCascadeClassifier() throws Exception {
        return cascadeClassifiers.get("data/haarcascades/haarcascade_frontalface_alt2.xml");
    }

    private CascadeClassifier getLbpFaceCascadeClassifier() throws Exception {
        return cascadeClassifiers.get("data/lbpcascades/lbpcascade_frontalface.xml");
    }

    private CascadeClassifier getEyesCascadeClassifier() throws Exception { 
        return cascadeClassifiers.get("data/haarcascades/haarcascade_eye_tree_eyeglasses.xml"); 
    }

    public void detect(File source , File target, VisionService.Classifier recognition ) throws Exception { 
       log.debug(String.format("soruce : %s, exists : %s", source.getPath(), source.exists() ));
       if( recognition == VisionService.Classifier.FACE || recognition == VisionService.Classifier.HAAR_FACE ){ 
            CascadeClassifier cascade1 = getHaarFaceCascadeClassifier();
            CascadeClassifier cascade2 = getEyesCascadeClassifier();
            log.debug("CascadeClassifier {}, {}", cascade1.empty() , cascade2.empty());
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = detectFace(src, cascade1 , cascade2 );
            Imgcodecs.imwrite(target.getPath(), dst); 
       }else  if( recognition == VisionService.Classifier.LBP_FACE ){ 
            CascadeClassifier cascade1 = getLbpFaceCascadeClassifier();
            CascadeClassifier cascade2 = getEyesCascadeClassifier();
            log.debug("CascadeClassifier {}, {}", cascade1.empty() , cascade2.empty());
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = detectFace(src, cascade1 , cascade2 );
            Imgcodecs.imwrite(target.getPath(), dst); 
       }else if (recognition == VisionService.Classifier.DNN_CAFFE_FACE){ 
            Mat src = Imgcodecs.imread(source.getPath()); 
            Net faceNet = deepLearningNetworks.get(new MultiKey<String>(new String[]{"data/caffe/deploy.prototxt.txt", "data/caffe/res10_300x300_ssd_iter_140000_fp16.caffemodel"})); 
            Mat dst = detectFace(src, faceNet , target); 
            Imgcodecs.imwrite(target.getPath(), dst); 
        }  
    }

    private Mat detectFace(Mat src, Net net, File target) {
        Mat frame = new Mat();
        Imgproc.cvtColor(src, frame, Imgproc.COLOR_RGBA2RGB);
        src.release(); 

        int frameWidth = frame.cols();
        int frameHeight = frame.rows();
        float confThreshold = 0.5f;
        int personObjectCount = 0;
        Scalar color= new Scalar(0, 0, 255) ; 

        Mat blob = Dnn.blobFromImage(frame, 1.0, IMAGE_DATA_SIZE_300x300, new Scalar(104.0, 177.0, 123.0, 0), false, false);
        log.debug("blob blob : {}",blob ); 
        log.debug("Computing object detections..."); 
        net.setInput(blob);  
        Mat detections = net.forward(); 
        detections = detections.reshape(1, (int) detections.total() / 7);
        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0]; 
            if (confidence < confThreshold)
                continue; 
            personObjectCount++;  
            //calculate position
            int xLeftBottom = (int) (detections.get(i, 3)[0] * frameWidth);
            int yLeftBottom = (int) (detections.get(i, 4)[0] * frameHeight);
            Point leftPosition = new Point(xLeftBottom, yLeftBottom);

            int xRightTop = (int) (detections.get(i, 5)[0] * frameWidth);
            int yRightTop = (int) (detections.get(i, 6)[0] * frameHeight);
            Point rightPosition = new Point(xRightTop, yRightTop);

            float centerX = (xLeftBottom + xRightTop) / 2;
            float centerY = (yLeftBottom - yRightTop) / 2;
            Point centerPoint = new Point(centerX, centerY);
            double classId = detections.get(i, 1)[0];  

            Rect rect = new Rect(leftPosition, rightPosition);            
            Mat face = frame.submat(rect);
            //Imgcodecs.imwrite(getTarget(target, String.format("face%s", i)), face); 
            Gender gender = predictGender(face);
            String age = predictAge(face);
            String label = String.format("%s(%s), %.2f%%", StringUtils.capitalize(gender.name().toLowerCase()), age, confidence * 100);
            Imgproc.putText(frame, label, new Point(xLeftBottom, yLeftBottom - 10), Imgproc.FONT_HERSHEY_SIMPLEX, .9, color, 2);
            Imgproc.rectangle(frame, rect, color, 3);
            //Imgproc.rectangle(frame, leftPosition, rightPosition, color, 3);
        }
        log.debug("detect persion : {}", personObjectCount);        
        return frame;
    }
    
    public String predictAge(Mat face) {  
        try {
            Net net = deepLearningNetworks.get(new MultiKey<String>(new String[]{"data/caffe/age.prototxt.txt", "data/caffe/dex_chalearn_iccv2015.caffemodel"}));
            Imgproc.resize(face, face, IMAGE_DATA_SIZE_224X224);
            Mat blob = Dnn.blobFromImage(face, 1.0, IMAGE_DATA_SIZE_224X224);
            net.setInput(blob);
            Mat prob = net.forward();
            MinMaxLocResult minMaxResutl = Core.minMaxLoc(prob);

            log.debug("1. Age prob : {} , total : {}, MinMaxLocResult - Loc:{} Max:{}, Loc:{} Min:{}", prob , prob.total(), minMaxResutl.maxLoc, minMaxResutl.maxVal, minMaxResutl.minLoc, minMaxResutl.minVal); 
            log.debug("2. Most dominant age class (not apparent age) : {}({})" , minMaxResutl.maxLoc.x, minMaxResutl.maxVal );
            double output_indexes [] = new double[101]; 
            for( int i = 0 ; i < prob.total(); i++ ){
                double age_dist = prob.get(0, i)[0];  
                output_indexes[i] = age_dist * i;
            } 
            double apparentPredictions = Math.round( Arrays.stream(output_indexes).sum() * 100.0) / 100.0;
            log.debug("3. Apparent age : {}", Arrays.stream(output_indexes).sum()); 

            return String.format("%.2f", apparentPredictions); 
        } catch (ExecutionException e) {
            log.error("Error when processing age", e);
        } 
        return "Unknown";    
    }
    
    public Gender predictGender(Mat face) {  
        try { 
            Net net = deepLearningNetworks.get(new MultiKey<String>(new String[]{"data/caffe/gender.prototxt.txt", "data/caffe/gender.caffemodel"})); 
            Imgproc.resize(face, face, IMAGE_DATA_SIZE_224X224);
            Mat blob = Dnn.blobFromImage(face, 1.0, IMAGE_DATA_SIZE_224X224);
            net.setInput(blob);
            Mat prob = net.forward();
            MinMaxLocResult minMaxResutl = Core.minMaxLoc(prob); 
            //Value at (0,0) corresponds to the probability of the face being a male and the value at (0,1) is the probability of being female.
            log.debug("Gender prob : {} , total : {}, MinMaxLocResult - Max Loc:{} = Max:{}, Min Loc:{} = Min:{}", prob , prob.total(), minMaxResutl.maxLoc, minMaxResutl.maxVal, minMaxResutl.minLoc, minMaxResutl.minVal); 
            
            if (minMaxResutl.maxLoc.x > 0 )
                return Gender.MALE;
            else
                return Gender.FEMALE; 
        } catch (Exception e) {
            log.error("Error when processing gender", e);
        }
        return Gender.NOT_RECOGNIZED;
    }
    
    private Mat detectFace(Mat frame, CascadeClassifier cascade1, CascadeClassifier cascade2) {
        Mat frameGray = new Mat();
        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray); 
        // -- Detect faces
        MatOfRect faces = new MatOfRect(); 
        //cascade1.detectMultiScale(frameGray, faces); 
        cascade1.detectMultiScale(frameGray, faces, 1.1, 2, 0| Objdetect.CASCADE_SCALE_IMAGE , new Size(30, 30), new Size(frameGray.width(), frameGray.height()) );  
        List<Rect> listOfFaces = faces.toList();
        for (Rect face : listOfFaces) {
            Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);
            Imgproc.rectangle(frame, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(0, 0, 255),  3);
            //Mat faceROI = frameGray.submat(face);
            // -- In each face, detect eyes
        }
        return frame;
    }

    private String getTarget(File file, String type ){
        String filename = file.getName(); 
        StringBuilder sb = new StringBuilder();
        sb.append(file.getParent()).append("/");
        sb.append( filename.substring(0, filename.lastIndexOf(".") ) );
        sb.append("_");
        sb.append(type);
        sb.append(filename.substring( filename.lastIndexOf(".") ));
        System.out.println( sb.toString() );
        return sb.toString();
    } 
}
