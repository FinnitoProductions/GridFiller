import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



public class RobotTest {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    private static Robot r;
    
    private static final int SCREEN_HEIGHT = 899;
    private static final int SCREEN_WIDTH = 1439; 
    
    private static final String SCREENSHOT_FILE_NAME = "saved.png";
    
    public static void main(String[] args)
        throws Exception
    {
        r = new Robot();
        
        while (true) {
	        Thread.sleep(5000);
	        takeScreenshot();
	        r.mouseMove(0, 0);
	        List<Shape> shapes = getShapes(processImage(SCREENSHOT_FILE_NAME));
	        clickOnShapes(shapes);
        }
    }
    
    public static void takeScreenshot () throws Exception {
        playCameraSound();
        Thread.sleep(2000);
        ImageIO.write(r.createScreenCapture(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)), "png", new File("saved.png"));
    }
    
    public static List<MatOfPoint> filterContours (List<MatOfPoint> contours, int minArea, int maxArea, int minPerimeter, int maxPerimeter, double minAreaToPerimSq, double maxAreaToPerimSq, Shape.ShapeType shape) {
    	List<MatOfPoint> newContours = new ArrayList<MatOfPoint>();
    	newContours.addAll(contours);
    	
        for (int i = 0; i < newContours.size(); i++) {
        	MatOfPoint contour = newContours.get(i);
 
        	double area = Imgproc.contourArea(contour);
        	double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
        	double areaToPerimSq = area / (perimeter * perimeter);
        	
        	if (area < minArea || perimeter < minPerimeter || area > maxArea || perimeter > maxPerimeter || areaToPerimSq < minAreaToPerimSq || areaToPerimSq > maxAreaToPerimSq || Shape.ShapeType.getShape(area, perimeter) != shape) {
        		
        		newContours.remove(i);
        		i--;
        	}
        }
        
        return newContours;
    }
    
    public static void playCameraSound () { playSound("camfocus.wav"); }
    
    public static List<MatOfPoint> processImage (String filename) {
        Mat m = Imgcodecs.imread(filename, Imgcodecs.IMREAD_GRAYSCALE);
        
        Core.inRange(m, new Scalar(254, 254, 254), new Scalar(255, 255, 255), m);
        Imgcodecs.imwrite("savedRangeFiltered.png", m);
        
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(m,contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        
        contours = filterContours(contours, 10, Integer.MAX_VALUE, 15, Integer.MAX_VALUE, 0.061, 0.064, Shape.ShapeType.SQUARE);
        Imgproc.drawContours(m, contours, -1, new Scalar(100, 100, 100), 3);
        Imgcodecs.imwrite("savedcontours.png", m);
        
        return contours;
    }
    
    public static List<Shape> getShapes (List<MatOfPoint> contours) {
        Map<Shape, Integer> shapes = new TreeMap<Shape, Integer>();
        List<Shape> shapeDuplicates = new ArrayList<Shape>();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            
            MatOfPoint2f  newContour = new MatOfPoint2f(contour.toArray());
            Rect rect = Imgproc.boundingRect(newContour);

            double perimeter = Imgproc.arcLength(newContour, true);
            
            Shape s = new Shape (area, perimeter, rect.x, rect.y);
            
            if (!shapes.keySet().contains(s)) {shapes.put(s, 0);}
            
            shapes.put(s, shapes.get(s) + 1);
            shapeDuplicates.add(s);
        }
        
        return shapeDuplicates;
    }
    
    public static void clickOnShapes (List<Shape> shapes) throws Exception {
        for (Shape s : shapes) {
            r.mouseMove(s.getX(), s.getY());
        	r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        	Thread.sleep(5);
        	r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }
    
    public static synchronized void playSound(final String url) {
    	      try {
    	        Clip clip = AudioSystem.getClip();
    	        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
    	          new File(url));
    	        clip.open(inputStream);

    	        clip.start(); 
    	      } catch (Exception e) {
    	        e.printStackTrace();
    	      }
    	      
    }
}