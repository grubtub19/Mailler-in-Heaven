import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class HealthBar extends Component {
	HealthC health;
	float y_offset = 20;
	float width = 40;
	float height = 10;
	Rectangle2D.Float currHealthRect;									//the green
	Rectangle2D.Float missingHealthRect;									//the red part
	protected int displayTimeTotal = 240;								//how long in seconds to display the Healthbar after the Entity is hit
	protected int displayTimer = 0;									//counter for Healthbar
	protected boolean visible;

	/**
	 * creates a Healthbar for the Entity
	 * @param
	 */
	public HealthBar (GameObject parent, int y_offset) {
		super(parent);
		this.y_offset = y_offset;
		currHealthRect = new Rectangle2D.Float();
		missingHealthRect = new Rectangle2D.Float();
		visible = false;
	}

	private float getLostHealthWidth(GameObject parent) {
		HealthC health = parent.getComponent(HealthC.class);
		float currHealthPercent = (float)health.currHP / (float)health.maxHP;
		float lostHealthPercent = 1 - currHealthPercent;
		return lostHealthPercent * width;
	}
	
	/**
	 * will shows the Healthbar for an amount of time until it disappears
	 */
	public void show() {
		visible = true;
		displayTimer = 0;
	}

	/**
	 * updates everything about the Healthbar.
	 */
	public void update(GameObject parent){
		TransformC transformC = parent.getComponent(TransformC.class);
		double x_left = transformC.position.x - width / 2.0f;
		double y_top = transformC.position.y - y_offset;
		currHealthRect.setRect(x_left, y_top, width, height);
		missingHealthRect.setRect(x_left, y_top, getLostHealthWidth(parent), height);

		if(visible) {
			displayTimer += ;
			if(displayTimer > displayTimeTotal) {
				visible = false;
			}
		}
	}

	/**
	 *  Decides to display the Healthbar or not and draws it
	 * @param g Graphics
	 */
	public void draw(GameObject parent, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if(visible) {
			g2.setColor(Color.black);
			g2.setStroke(new BasicStroke(4));
			g2.drawRect((int) currHealthRect.x, (int) currHealthRect.y, (int) currHealthRect.getWidth(), (int) currHealthRect.getHeight());
			g2.setColor(Color.green);
			g2.fillRect((int) currHealthRect.x, (int) currHealthRect.y, (int) currHealthRect.getWidth(), (int) currHealthRect.getHeight());
			g2.setColor(Color.red);
			g2.fillRect((int) missingHealthRect.x, (int) missingHealthRect.y, (int) missingHealthRect.getWidth(), (int) missingHealthRect.getHeight());
		}
	}

	@Override
	public String toString() {
		return "HealthBar{" +
				"y_offset=" + y_offset +
				", width=" + width +
				", height=" + height +
				", currHealthRect=" + currHealthRect +
				", missingHealthRect=" + missingHealthRect +
				", displayTimeTotal=" + displayTimeTotal +
				", displayTimer=" + displayTimer +
				", containers=" + containers +
				'}';
	}
}
