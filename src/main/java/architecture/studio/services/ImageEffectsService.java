package architecture.studio.services;

import java.io.File;

public interface ImageEffectsService {
    
    enum Effects {
        BLUR, 
        GRAYSCALE, 
        STYLIZATION, 
        EROSION, 
        SKETCHER, 
        CARTOONIZER,  
        PAINTING,
        GRAYSCALE_PENCIL_SKETCH , 
        PENCIL_SKETCH, 
        HDR , 
        INVERT ,
        SEPIA, 
        SHARP , 
        BINARY,
        OILPAINTING, 
        WATERCOLOR,
        FACE_DETECT
    }

    public static final String IMAGE_ANNOTATION_DIR = "annotation";

    public static final String IMAGE_EFFECT_DIR= "effects";

    public File getImageEffectDir() ;
    
    public File getImageAnnotationDir() ;
    
    public void effect (File source , File target , Effects effect);

}
