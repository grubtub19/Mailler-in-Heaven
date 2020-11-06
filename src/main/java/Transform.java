public class Transform {
    public static int unitScale = 10;
    /**
     * In pixels
     */
    public Vector2D position;
    /**
     * In degrees
     */
    public float rotation;
    public float scale;

    public Transform() {
        position = new Vector2D(0,0);
        rotation = 0;
        scale = 1;
    }

    public Transform(Vector2D position) {
        this.position = position;
        rotation = 0;
        scale = 1;
    }

    public Transform(Vector2D position, float rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public float unitsToPixels(int units) {
        return scale * Transform.unitScale;
    }

    /**
     * Set's the object's position at a certain length from the origin at a certain angle.
     * 0 degrees is (1,0) and increases counter-clockwise.
     * @param rotation
     * @param length
     */
    public void setPositionAsRotationAround(float rotation, float length) {
        double radians = Math.toRadians(rotation);
        position.x = length * unitScale * (float) Math.cos(radians);
        position.y = length * unitScale * (float) Math.sin(radians);
    }

    public Transform transformRelativeTo(Transform parent_transform) {
        Transform result = new Transform();
        // Scale and Rotation are independent with uniform scaling
        result.scale = scale * parent_transform.scale;
        result.rotation = rotation + parent_transform.rotation;

        // Get unit rotation values
        double radians = Math.toRadians(parent_transform.rotation);
        float s = (float)Math.sin(radians);
        float c = (float)Math.cos(radians);

        // Move the coordinate plane so that the parent's position is at the origin
        result.position.x = position.x - parent_transform.position.x;
        result.position.y = position.y - parent_transform.position.y;

        // Perform rotation
        float xnew = result.position.x * c - result.position.y * s;
        float ynew = result.position.x * s + result.position.y * c;

        // Perform scale
        xnew = xnew * parent_transform.scale;
        ynew = ynew * parent_transform.scale;

        // Move coordinates back to original position
        result.position.x = xnew + parent_transform.position.x;
        result.position.y = ynew + parent_transform.position.y;

        return result;
    }
}
