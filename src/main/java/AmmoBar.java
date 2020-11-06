import java.awt.*;
import java.util.ArrayList;

/**
 * TODO Currently doesn't initialize the bars until changeBarCount() is run since it would require the container gameObject
 */
public class AmmoBar extends Component {


    private int width;
    private int height;
    private ArrayList<Polygon> bars;
    private int bar_count;
    private Timer visible_timer;
    /**
     * in units
     */
    private static int offset_y = 2;

    public AmmoBar() {
        super();
        visible_timer = new Timer(500);
        width = 40;
        height = 10;
        bar_count = 0;
    }

    private void changeBarCount(GameObject container, int max_ammo) {
        bars.clear();
        // Calculate the width of every ammo square and space between squares
        float solid_width = 0.75f * width / max_ammo;
        float spacing_width = 0.25f * width / (max_ammo - 1);

        // Calculate the leftmost edge of the entire bars
        float leftmost = -width / 2.0f;

        // The top/bottom edge of the entire bars is the top/bottom edge of each square
        int top_edge = getYOffsetPixels(container);
        int bottom_edge = top_edge + height;

        // For each ammo square in the ammo bars
        for (int i = 0; i < max_ammo; i++) {

            // Calculate the x values for the right and left edge of the current square
            int right_edge = (int) (leftmost + (i + 1) * solid_width + i * spacing_width);
            int left_edge = (int) (leftmost + i * solid_width + i * spacing_width);

            // Create a list of x and y coordinates for this square
            int[] x_points = { left_edge, right_edge, right_edge, left_edge };
            int[] y_points = { top_edge, top_edge, bottom_edge, bottom_edge };

            // Create a Polygon with these coordinates and add it to the bars
            Polygon box = new Polygon(x_points, y_points, 4);
            bars.add(box);
        }
    }

    /**
     * The distance in pixels from the center of the player_obj to the top of this AmmoBar
     * @param container
     * @return
     */
    private int getYOffsetPixels(GameObject container) {
        return (int) container.getComponent(TransformC.class).transform.unitsToPixels(offset_y);
    }

    /**
     * translate every point in all polygons by and x and y value
     * @param x
     * @param y
     */
    private void translateBar(int x, int y) {
        for (Polygon poly: bars) {
            for (int i = 0; i < poly.npoints; i++) {
                poly.xpoints[i] += x;
                poly.ypoints[i] += y;
            }
        }
    }

    public boolean isVisible() {
        return visible_timer.isActive();
    }

    public void setVisible() {
        visible_timer.start();
    }

    public void update(GameObject parent) {

    }

    public void lateUpdate(GameObject container) {
        GunControllerC gun_controller = container.getComponent(GunControllerC.class);
        if (gun_controller != null) {
            int curr_gun_num_bullets = gun_controller.curr_gun.getComponent(GunC.class).num_bullets;

            // If a new gun with a different max ammo is active
            if (curr_gun_num_bullets != bar_count) {
                // Change the number of bars to display
                changeBarCount(container, curr_gun_num_bullets);
            }
        } else {
            System.err.println("AmmoBar.lateUpdate(): container has no GunController Component.");
        }

        Transform transform = container.getComponent(TransformC.class).transform;

        // Calculate distance between where the top left corner of the bars is and where it should be
        float y = (transform.position.y + getYOffsetPixels(container)) - (float) bars.get(0).getBounds2D().getMinY();
        float x = (transform.position.x - width / 2.0f) - (float) bars.get(0).getBounds2D().getMinX();

        // Translate every polygon by this value
        translateBar((int) x, (int) y);
    }

    public void draw(GameObject container, Graphics g) {
        if(isVisible()) {
            // Get the current ammo relative to the bars count
            GunControllerC gun_controller = container.getComponent(GunControllerC.class);
            float current_ammo;
            if (gun_controller != null) {
                current_ammo = gun_controller.ammo.convertCurrentAmmo(bar_count);
            } else {
                System.err.println("AmmoBar.draw(): container has no GunController Component. Setting ammo to 0.");
                current_ammo = 0;
            }

            // Draw the squares in different colors depending on how much ammo there is.
            for(int i = 0; i < bar_count; i++) {
                // If this ammo square is Empty
                if(i < current_ammo) {
                    g.setColor(Color.decode("#686868"));
                    g.fillPolygon(bars.get(i));
                // If 0 - 20% of this ammo square is full
                } else if (i + 0.2 > current_ammo) {
                    g.setColor(Color.decode("#2972e8"));
                    g.fillPolygon(bars.get(i));
                // If 20 - 40% of this ammo square is full
                } else if (i + 0.4 > current_ammo) {
                    g.setColor(Color.decode("#4076ce"));
                    g.fillPolygon(bars.get(i));
                // If 40 - 60% of this ammo square is full
                } else if (i + 0.6 > current_ammo) {
                    g.setColor(Color.decode("#597db7"));
                    g.fillPolygon(bars.get(i));
                // If 60 - 80% of this ammo square is full
                } else if (i + 0.8 > current_ammo) {
                    g.setColor(Color.decode("#65799b"));
                    g.fillPolygon(bars.get(i));
                // If 80 - 100% of this ammo square is full
                } else if (i + 1 > current_ammo) {
                    g.setColor(Color.decode("#697282"));
                    g.fillPolygon(bars.get(i));
                // Else this ammo square is full
                } else {
                    g.setColor(Color.decode("#ff00ee"));
                    g.fillPolygon(bars.get(i));
                }
            }
        }
    }

    public String toString() {
        return null;
    }
}
