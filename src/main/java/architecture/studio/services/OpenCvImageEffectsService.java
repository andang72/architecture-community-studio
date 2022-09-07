package architecture.studio.services;

import java.io.File;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.opencv.xphoto.Xphoto;

public class OpenCvImageEffectsService extends AbstractImageEffectsService {

	@PostConstruct
	public void initialize() throws Exception {
		log.debug("Loading libiray {} ...", Core.NATIVE_LIBRARY_NAME );
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        log.debug("Welcome to OpenCV {}", Core.VERSION);
        test();
	}

    private void test(){
        try{
            Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
            log.debug( "mat = {}" , mat.dump() );
        }catch(Throwable ignore){
            log.warn(ignore.getMessage(), ignore );
        }
    }

    public void effect (File source , File target , Effects effect){

        if( effect == Effects.GRAYSCALE){
            log.debug("effects grayscale");
            int grayscale = Imgcodecs.IMREAD_GRAYSCALE;
            Mat src = Imgcodecs.imread( source.getPath(), grayscale ); 
            //Normalize image (Contrast streching) 
            Core.normalize(src, src, 0, 255, Core.NORM_MINMAX); 
            Imgcodecs.imwrite(target.getPath(), src);
        }else if (effect == Effects.BLUR ){ 
            int apertureLinearSize = 7 ;
            Mat src = Imgcodecs.imread(source.getPath());
            Mat gray = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.medianBlur(gray, gray, apertureLinearSize );
            Mat edges = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.adaptiveThreshold(gray, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 10);
            Mat color = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.bilateralFilter(src, color, 12, 250, 250) ;   
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            Core.bitwise_and(color, color, dst );  
            Imgcodecs.imwrite(target.getPath(), dst); 
        }else if (effect == Effects.CARTOONIZER ){ 
            Mat src = Imgcodecs.imread(source.getPath());
            
            //1. converting the image into gray-scale
            Mat gray = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

            //2. Applying median blur on the Image
            Mat blur = new Mat(src.rows(), src.cols(), src.type()); 
            Imgproc.medianBlur(gray, blur, 1);

            //3.applying adaptive threshold to use it as a mask
            Mat edges = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.adaptiveThreshold(blur, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 9 );
    
            Mat color = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.bilateralFilter(src, color, 9, 200, 200);

            //4. cartoonize
            Mat dst = new Mat(src.rows(), src.cols(), src.type());
            Core.bitwise_and(color, color, dst, edges);
            Imgcodecs.imwrite(target.getPath(), dst );

        }else if (effect == Effects.EROSION ){ 
            Mat src = Imgcodecs.imread(source.getPath());
            //1. Creating destination matrix
            Mat dst = new Mat(src.rows(), src.cols(), src.type());
            //2. Preparing the kernel matrix object
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 3, 3) );
            //3. Applying erosion on the Image
            Imgproc.erode(src, dst, kernel);
            Imgcodecs.imwrite(target.getPath(), dst);

        }else if (effect == Effects.OILPAINTING ){  
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            oilPaintingEffect(src, dst);
            Imgcodecs.imwrite(target.getPath(), dst ); 
        }else if (effect == Effects.PAINTING ){ 

            Mat src = Imgcodecs.imread(source.getPath());
            //1. Preparing the kernel matrix object
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(6,6));

            //2. apply morphology open to smooth the outline,
            Mat morph = new Mat(src.rows(), src.cols(), src.type()); 
            Imgproc.morphologyEx(src, morph, Imgproc.MORPH_OPEN, kernel);

            //3. brighten dark regions
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            Core.normalize(morph, dst, 20, 255, Core.NORM_MINMAX );
            Xphoto.oilPainting(src, dst, 10, 1);
            Imgcodecs.imwrite(target.getPath(), dst );

        }else if (effect == Effects.SKETCHER ){  

            Mat src = Imgcodecs.imread(source.getPath());
            // 1. gray image 
            Mat gray = new Mat(src.rows(), src.cols(), src.type()); 
            // 2. Normalize image (Contrast streching) 
            Core.normalize(gray, gray, 0, 255, Core.NORM_MINMAX);  
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);  
            Mat blur = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.GaussianBlur(gray, blur, new Size(0.0, 0.0), new Float(3.0)); 
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            Core.divide(gray, blur, dst, 255.0);  
            Imgcodecs.imwrite(target.getPath(), dst);

        }else if (effect == Effects.GRAYSCALE_PENCIL_SKETCH ){  
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst1 = new Mat(src.rows(), src.cols(), src.type()); // gray
            Mat dst2 = new Mat(src.rows(), src.cols(), src.type()); // color
            
            Float sigma_s = new Float(60);
            Float sigma_r = new Float(0.07);
            Float shade_factor = new Float(0.1);

            Photo.pencilSketch(src, dst1, dst2, sigma_s, sigma_r, shade_factor);
            Imgcodecs.imwrite(target.getPath(), dst1);

        }else if (effect == Effects.PENCIL_SKETCH ){  
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst1 = new Mat(src.rows(), src.cols(), src.type()); // gray
            Mat dst2 = new Mat(src.rows(), src.cols(), src.type()); // color
            
            Float sigma_s = new Float(60);
            Float sigma_r = new Float(0.07);
            Float shade_factor = new Float(0.1);

            Photo.pencilSketch(src, dst1, dst2, sigma_s, sigma_r, shade_factor);
            Imgcodecs.imwrite(target.getPath(), dst2);
        }else if (effect == Effects.HDR ){  
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = new Mat(src.rows(), src.cols(), src.type()); 
            Photo.detailEnhance(src, dst, new Float(12) , new Float(0.15) );
            Imgcodecs.imwrite(target.getPath(), dst);
        }else if (effect == Effects.INVERT ){  
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = new Mat(src.rows(), src.cols(), src.type()); 
            Core.bitwise_not(src, dst);
            Imgcodecs.imwrite(target.getPath(), dst); 
        }else if (effect == Effects.STYLIZATION ){ 
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            Photo.stylization(src, dst, 150, new Float(0.25));
            Imgcodecs.imwrite(target.getPath(), dst );
        }else if (effect == Effects.BINARY){
            Mat src = Imgcodecs.imread(source.getPath());
             
            //1. converting the image into gray-scale
            Mat gray = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

            //2. converting the image into binary
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            Imgproc.threshold(gray, dst, 200, 500, Imgproc.THRESH_BINARY); 
            Imgcodecs.imwrite(target.getPath(), dst );
        }else if ( effect == Effects.WATERCOLOR ){
            Mat src = Imgcodecs.imread(source.getPath());
            Mat dst = new Mat(src.rows(), src.cols(), src.type());  
            waterColorEffect(src, dst);
            Imgcodecs.imwrite(target.getPath(), dst );
        }

    } 

    private void waterColorEffect (Mat src, Mat dst){
        float sigma_s = new Float(60) ;
        float sigma_r = new Float(0.6) ;
        Photo.stylization(src, dst, sigma_s, sigma_r ); 
    } 

    private void oilPaintingEffect (Mat src, Mat dst){
        int size = 7 ;
        int dynRatio = 1 ;
        Xphoto.oilPainting(src, dst, size, dynRatio );
    } 

}