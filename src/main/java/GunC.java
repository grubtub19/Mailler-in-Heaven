import java.awt.*;

public abstract class GunC extends Component {

    public GunType type;
    public int num_bullets;
    /**
     * In units
     */
    public float hold_distance;
    /**
     * Velocity of the bullets
     */
    public int bullet_speed;
    /**
     * The velocity the holder is shot back at.
     */
    public int stagger;

    public GunC(float hold_distance, int bullet_speed) {
        super();
        this.hold_distance = hold_distance;
        this.bullet_speed = bullet_speed;
    }

    /**
     * The gun is fired and we already know we have enough ammo
     */
    public abstract void fire(GameObject container);

    /**
     * Updates position and rotation relative to the direction the gun is aimed
     * @param container
     */
    public void update(GameObject container) {

        Transform gun_transform = container.getComponent(TransformC.class).transform;
        GunControllerC gun_controller = container.parent.getComponent(GunControllerC.class);
        // Set the relative position as a rotation around the origin by some amount at some distance
        gun_transform.setPositionAsRotationAround(gun_controller.facing_direction, hold_distance);
        // Set the rotation as the direction the gun is aimed
        gun_transform.rotation = gun_controller.facing_direction;
    }

    public void lateUpdate(GameObject container) {
        StripImageC gun_image = container.getComponent(StripImageC.class);

        if(container.parent.getComponent(GunControllerC.class).facing_direction < 180) {
            gun_image.image_num = 0;
        } else {
            gun_image.image_num = 1;
        }
    }

    public void draw(GameObject container, Graphics g) {

    }
}
