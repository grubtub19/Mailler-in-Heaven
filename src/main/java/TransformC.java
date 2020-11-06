import java.awt.*;

public class TransformC extends Component {

    public Transform transform;
    public Transform absolute_transform;

    public TransformC() {
        super();
        transform.position = new Vector2D(0,0);
        transform.rotation = 0;
        transform.scale = 1;
    }

    public TransformC(Vector2D position) {
        super();
        transform.position = position;
        transform.rotation = 0;
        transform.scale = 1;
    }

    public TransformC(Vector2D position, float rotation, float scale) {
        super();
        transform.position = position;
        transform.rotation = rotation;
        transform.scale = scale;
    }

    public void updateAbsoluteTransform(GameObject container) {
        if(container.parent != null) {
            // Get the parent GameObject's absolute transform
            Transform parent_transform = container.parent.getComponent(TransformC.class).absolute_transform;
            absolute_transform = transform.transformRelativeTo(parent_transform);
        }
    }

    public void update(GameObject container) {

    }

    public void lateUpdate(GameObject container) {
        updateAbsoluteTransform(container);
    }

    public void draw(GameObject container, Graphics g) {

    }

    @Override
    public String toString() {
        return "TransformC{" +
                "transform=" + transform +
                ", absolute_transform=" + absolute_transform +
                ", containers=" + containers +
                '}';
    }
}
