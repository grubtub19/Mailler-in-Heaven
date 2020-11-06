import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image extends Component {
	private String filename;
	public float alpha;
	int i = 0;
	int x_offset = 0;
	int y_offset = 0;
	
	public Image(String filename) {
		super();
		this.filename = filename;
		this.alpha = 1;
		Game.get().image_manager.registerImage(filename);
	}

	public Image(String filename, float alpha) {
		super();
		this.filename = filename;
		this.alpha = alpha;
		Game.get().image_manager.registerImage(filename);
	}

	public BufferedImage get() {
		return Game.get().image_manager.getImage(filename);
	}

	public int width() {
		return get().getWidth();
	}

	public int height() {
		return get().getHeight();
	}


	/**
	 * Sets the offset of the image with the center of the images as the anchor point.
	 * @param x horizontal position of the center of the image
	 * @param y vertical position of the center of the image
	 */
	public void setOffset(int x, int y) {
		x_offset = (int) (x - width() / 2.0f);
		y_offset = (int) (y - height() / 2.0f);
	}

	/**
	 * Sets the offset of the image with the top left corner of the images as the anchor point.
	 * @param x horizontal position of the left edge of the image
	 * @param y vertical position of the top edge of the image
	 */
	public void setTopLeftOffset(int x, int y) {
		x_offset = x;
		y_offset = y;
	}

	public String toString() {
		return null;
	}

	public void update(GameObject parent) {

	}

	public void lateUpdate(GameObject parent) {

	}

	public void draw(GameObject parent, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Transform transform = parent.getComponent(TransformC.class).absolute_transform;
		int x = (int) transform.position.x + x_offset;
		int y = (int) transform.position.y + y_offset;

		g2.rotate(Math.toRadians(transform.rotation), transform.position.x, transform.position.y);
		g2.scale(transform.scale, transform.scale);

		// Draw the image depending on alpha
		if(alpha < 1.0f && alpha <= 0.0f) {

			// backup the old composite
			Composite c = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

			g2.drawImage(get(), x, y, null);

			// Restore the old composite so it doesn't mess up future rendering
			g2.setComposite(c);
		} else if (alpha == 1) {
			g2.drawImage(get(), x, y, null);
		} else {
			g2.drawImage(get(), x, y, null);
			System.err.println("Image.draw(): alpha value, " + alpha + ", must be between 0 and 1.");
		}

		g2.scale(1 / transform.scale, 1 / transform.scale);
		g2.rotate(-Math.toRadians(transform.rotation), transform.position.x, transform.position.y);
	}
}
