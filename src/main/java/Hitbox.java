import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Hitbox extends Component {
    public Rectangle area;


    public final static double[] xArray = {};
    public final static double[] yArray = {};
    public final static Polygon poly = new Polygon();

    /**
     * Construct a square hitbox
     * @param size in units
     */
    public Hitbox(float size) {
        super();
        area = createSquarePolygon(size);
    }

    /**
     * Construct a hitbox given a polygon
     * @param area
     */
    public Hitbox(Rectangle area) {
        super();
        this.area = area;
    }

    /**
     * Create a square Polygon to be used as the area of this Hitbox.
     * @param size in units
     * @return
     */
    private Rectangle createSquarePolygon(float size) {
        // Calculate a square
        int int_size = (int)((size * Transform.unitScale) / 2.0f);
        int[] x_points = { -int_size, int_size, int_size, -int_size };
        int[] y_points = { -int_size, -int_size, int_size, int_size };

        return new Rectangle(-int_size, -int_size, int_size * 2, int_size * 2);
    }

    /**
     * TODO copy from other file
     * @param other
     * @return
     */
    public boolean collides(Hitbox other) {
        return area.intersects(other.area);
    }

    public String toString() {
        return null;
    }

    public void update(GameObject parent) {

    }

    public void lateUpdate(GameObject parent) {

    }

    public void draw(GameObject parent, Graphics g) {

    }
}
