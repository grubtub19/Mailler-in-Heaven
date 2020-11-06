import java.awt.*;
import java.awt.geom.Point2D;

public class RigidBody extends Component {
    Vector2D velocity;
    Vector2D acceleration;
    float mass;
    float drag;

    public RigidBody(float mass, float drag) {
        super();
        velocity = new Vector2D(0,0);
        acceleration = new Vector2D(0,0);
        this.mass = mass;
        this.drag = drag;
    }

    public void update(GameObject parent) {

        // Update velocity
        velocity.add(acceleration.getMultiplied(Game.get().time_manager.game_delta_time));

        // Update drag
        float drag_magnitude = velocity.getMagnitudeSquared() * drag * Game.get().time_manager.game_delta_time;
        // drag_magnitude * -velocity.normal
        Vector2D drag_vector = velocity.getNormalized().reverse().multiply(drag_magnitude);
        // velocity = velocity - drag_vector
        velocity.subtract(drag_vector);

        // Update position
        Vector2D position = parent.getComponent(TransformC.class).transform.position;
        position.x += velocity.x * Game.get().time_manager.game_delta_time;
        position.y += velocity.y * Game.get().time_manager.game_delta_time;
    }

    public void lateUpdate(GameObject parent) {

    }

    public void draw(GameObject parent, Graphics g) {

    }

    @Override
    public String toString() {
        return "RigidBody{" +
                "velocity=" + velocity +
                ", acceleration=" + acceleration +
                ", mass=" + mass +
                ", drag=" + drag +
                ", containers=" + containers +
                '}';
    }
}
