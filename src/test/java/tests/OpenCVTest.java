package tests;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

public class OpenCVTest {
    
    @Test
    public void testMAT(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
        System.out.println( "mat = " + mat.dump() );
    }

    private String WORKING_DIR = "/Users/donghyuck.son/Downloads";
    private String SOURCE_IMAGE = "B178EFC2-3459-4116-A240-6483D4FD450E.jpeg";

    private String getSource(){
        StringBuilder sb = new StringBuilder();
        sb.append(WORKING_DIR).append("/").append(SOURCE_IMAGE);
        return sb.toString();
    }

    private String getTarget(String type ){
        StringBuilder sb = new StringBuilder();
        String str = SOURCE_IMAGE;
        sb.append(WORKING_DIR).append("/");
        sb.append( str.substring(0, str.lastIndexOf(".") ) );
        sb.append("_");
        sb.append(type);
        sb.append(str.substring( str.lastIndexOf(".") ));
        System.out.println( sb.toString() );
        return sb.toString();
    }


    @Test
    public void testImageGray(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        int grayscale = Imgcodecs.IMREAD_GRAYSCALE;
        Mat src = Imgcodecs.imread(getSource(), grayscale); 
        //Normalize image (Contrast streching) 
        Core.normalize(src, src, 0, 255, Core.NORM_MINMAX); 
        Imgcodecs.imwrite(getTarget("greyscale"), src);
    }

    @Test
    public void testImageErosion(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 

        //Reading image 
        Mat src = Imgcodecs.imread(getSource());

        //Creating destination matrix
        Mat dst = new Mat(src.rows(), src.cols(), src.type());

        //Preparing the kernel matrix object
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 3, 3) );

        //Applying erosion on the Image
        Imgproc.erode(src, dst, kernel);
        Imgcodecs.imwrite(getTarget("erosion"), dst);
    }

    @Test
    public void testImageBlur(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 

        //Reading image 
        Mat src = Imgcodecs.imread(getSource());
        Mat gray = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 7);

        Mat edges = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.adaptiveThreshold(gray, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 10);

        Mat color = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.bilateralFilter(src, color, 12, 250, 250) ;
         

        Mat blur = new Mat(src.rows(), src.cols(), src.type());  
        Core.bitwise_and(color, color, blur );

        Imgcodecs.imwrite(getTarget("blur"), blur);
    }


    @Test
    public void testImageStylization(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 

        //Reading image 
        Mat src = Imgcodecs.imread(getSource());
        Mat cartoon = new Mat(src.rows(), src.cols(), src.type());  
        Photo.stylization(src, cartoon, 150, new Float(0.25));
        Imgcodecs.imwrite(getTarget("style"), cartoon);

    }


    public void testImageCartoon(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 

        //Reading image 
        Mat src = Imgcodecs.imread(getSource());

        Mat gray = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        Mat blur = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.medianBlur(src, blur, 7 );

        Mat fl = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.bilateralFilter(fl,gray,9, 9, 7); 
        

        Mat edges = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.adaptiveThreshold(blur, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 2 );

        Imgproc.cvtColor(src,src,Imgproc.COLOR_GRAY2RGB);  
        Mat cartoon = new Mat(src.rows(), src.cols(), src.type());  

        Core.bitwise_and(src,edges,cartoon);
        Imgcodecs.imwrite(getTarget("cartoon"), edges);
    }

    @Test
    public void testImageSketch(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 

        //Reading image 
        Mat src = Imgcodecs.imread(getSource());

        
        // gray image 
        Mat gray = new Mat(src.rows(), src.cols(), src.type());  
         //Normalize image (Contrast streching) 
         Core.normalize(gray, gray, 0, 255, Core.NORM_MINMAX); 

        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY); 
        Mat blur = new Mat(src.rows(), src.cols(), src.type());  
        Imgproc.GaussianBlur(gray, blur, new Size(0.0, 0.0), new Float(3.0));
    
        Mat sketch = new Mat(src.rows(), src.cols(), src.type());  
        Core.divide(gray, blur, sketch, 255.0); 
        Imgcodecs.imwrite(getTarget("sketch"), sketch);
    }

}
