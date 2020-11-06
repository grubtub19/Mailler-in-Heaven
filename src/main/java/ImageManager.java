import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In charge of loading and storing BufferedImages used in both Image and StripImage instances.
 * These BufferedImage's are shared among multiple Images or StripImages, so to avoid loading multiple of the same
 * BufferedImage, we centralize everything here.
 */
public class ImageManager {
    private static boolean instantiated = false;
    private Map<String, BufferedImage[]> image_map;
    private final String IMAGE_DIR = "Images/";

    public ImageManager() {
        if (instantiated) {
            System.err.println("PrintManager should only be initialized once.");
        }
        instantiated = true;
        image_map = new HashMap<String, BufferedImage[]>(8);
    }

    /**
     *
     * @param filename name of the image file including extension
     * @return null if the image has not been registered yet
     */
    public BufferedImage getImage(String filename) {
        BufferedImage[] images = image_map.get(filename);

        // If the filename is registered already
        if(images != null) {
            return images[0];
        } else {
            System.err.println("ImageManager.getImage(): image, " + filename + ", has not yet been registered.");
            return null;
        }
    }

    /**
     *
     * @param filename name of the image file including extension
     * @return null if the strip image has not been registered yet
     */
    public BufferedImage[] getStripImage(String filename) {
        BufferedImage[] images = image_map.get(filename);

        // If the filename is registered already
        if (images != null) {
            return images;
        } else {
            System.err.println("ImageManager.getImage(): image, " + filename + ", has not yet been registered.");
            return null;
        }
    }

    public void registerImage(String filename) {
        if(!image_map.containsKey(filename)) {
            BufferedImage[] image_array = new BufferedImage[1];

            // Load the entire image and assign it as the first item in our BufferedImage[]
            image_array[0] = loadImage(filename);

            // Add the new BufferedImage[] to the image_map
            image_map.put(filename, image_array);
        } else {
            System.err.println(filename + " is already registered.");
        }
    }

    public void registerStripImage(String filename, int number_of_images) {
        if(!image_map.containsKey(filename)) {
            BufferedImage[] image_array = new BufferedImage[number_of_images];

            // Load entire image (we slice it up later in this method)
            BufferedImage strip_image = loadImage(filename);

            if(number_of_images < 2) {
                System.err.println("Number of images should be > 1.");
                number_of_images = 1;
            }

            // Calculate the width of each individual image
            int image_width = (int) strip_image.getWidth() / number_of_images;

            // create local variables for cleaner code
            int image_height = strip_image.getHeight();
            int alpha = strip_image.getColorModel().getTransparency();

            Graphics2D stripGC;

            // each BufferedImage from the strip file is stored in strip[]
            for (int i = 0; i < number_of_images; i++) {
                image_array[i] = new BufferedImage(image_width, image_height, alpha);

                // create a graphics context
                stripGC = image_array[i].createGraphics();

                // copy image
                stripGC.drawImage(strip_image, 0, 0, image_width, image_height, i * image_width, 0,
                        (i * image_width) + image_width, image_height, null);
                stripGC.dispose();
            }

            // Add the new BufferedImage[] to the image_map
            image_map.put(filename, image_array);
        } else {
            System.err.println(filename + " is already registered.");
        }
    }

    private BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(ResourceLoader.load(IMAGE_DIR + filename));
        } catch (IOException e) {
            System.err.println("ImageManager.loadImage(): Could not find image file, " + filename);
            e.printStackTrace();
            return null;
        }
    }
}
