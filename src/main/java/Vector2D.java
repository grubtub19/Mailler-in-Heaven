public class Vector2D {

    public float x;
    public float y;

    public Vector2D() { }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v) {
        set(v);
    }

    public Vector2D set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D set(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vector2D setZero() {
        x = 0;
        y = 0;
        return this;
    }

    public float[] getComponents() {
        return new float[]{x, y};
    }

    public float getMagnitude() {
        return (float)Math.sqrt(x * x + y * y);
    }

    public float getMagnitudeSquared() {
        return (x * x + y * y);
    }

    public float distanceSq(float vx, float vy) {
        vx -= x;
        vy -= y;
        return (vx * vx + vy * vy);
    }

    public float distanceSq(Vector2D v) {
        float vx = v.x - this.x;
        float vy = v.y - this.y;
        return (vx * vx + vy * vy);
    }

    public float distance(float vx, float vy) {
        vx -= x;
        vy -= y;
        return (float)Math.sqrt(vx * vx + vy * vy);
    }

    public float distance(Vector2D v) {
        float vx = v.x - this.x;
        float vy = v.y - this.y;
        return (float)Math.sqrt(vx * vx + vy * vy);
    }

    public float getAngle() {
        return (float)Math.atan2(y, x);
    }

    public Vector2D normalize() {
        float magnitude = getMagnitude();
        x /= magnitude;
        y /= magnitude;
        return this;
    }

    public Vector2D getNormalized() {
        float magnitude = getMagnitude();
        return new Vector2D(x / magnitude, y / magnitude);
    }

    public static Vector2D toCartesian(float magnitude, float angle) {
        return new Vector2D(magnitude * (float)Math.cos(angle), magnitude * (float)Math.sin(angle));
    }

    public Vector2D add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vector2D add(float vx, float vy) {
        this.x += vx;
        this.y += vy;
        return this;
    }

    public static Vector2D add(Vector2D v1, Vector2D v2) {
        return new Vector2D(v1.x + v2.x, v1.y + v2.y);
    }

    public Vector2D getAdded(Vector2D v) {
        return new Vector2D(this.x + v.x, this.y + v.y);
    }

    public Vector2D subtract(Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public Vector2D subtract(float vx, float vy) {
        this.x -= vx;
        this.y -= vy;
        return this;
    }

    public static Vector2D subtract(Vector2D v1, Vector2D v2) {
        return new Vector2D(v1.x - v2.x, v1.y - v2.y);
    }

    public Vector2D getSubtracted(Vector2D v) {
        return new Vector2D(this.x - v.x, this.y - v.y);
    }

    public Vector2D multiply(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2D getMultiplied(float scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public Vector2D divide(float scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    public Vector2D getDivided(float scalar) {
        return new Vector2D(x / scalar, y / scalar);
    }

    public Vector2D getPerp() {
        return new Vector2D(-y, x);
    }

    public float dot(Vector2D v) {
        return (this.x * v.x + this.y * v.y);
    }

    public float dot(float vx, float vy) {
        return (this.x * vx + this.y * vy);
    }

    public static float dot(Vector2D v1, Vector2D v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public float cross(Vector2D v) {
        return (this.x * v.y - this.y * v.x);
    }

    public float cross(float vx, float vy) {
        return (this.x * vy - this.y * vx);
    }

    public static float cross(Vector2D v1, Vector2D v2) {
        return (v1.x * v2.y - v1.y * v2.x);
    }

    public float project(Vector2D v) {
        return (this.dot(v) / this.getMagnitude());
    }

    public float project(float vx, float vy) {
        return (this.dot(vx, vy) / this.getMagnitude());
    }

    public static float project(Vector2D v1, Vector2D v2) {
        return (dot(v1, v2) / v1.getMagnitude());
    }

    public Vector2D getProjectedVector(Vector2D v) {
        return this.getNormalized().getMultiplied(this.dot(v) / this.getMagnitude());
    }

    public Vector2D getProjectedVector(float vx, float vy) {
        return this.getNormalized().getMultiplied(this.dot(vx, vy) / this.getMagnitude());
    }

    public static Vector2D getProjectedVector(Vector2D v1, Vector2D v2) {
        return v1.getNormalized().getMultiplied(Vector2D.dot(v1, v2) / v1.getMagnitude());
    }

    public Vector2D rotateBy(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float rx = x * cos - y * sin;
        y = x * sin + y * cos;
        x = rx;
        return this;
    }

    public Vector2D getRotatedBy(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    public Vector2D rotateTo(float angle) {
        set(toCartesian(getMagnitude(), angle));
        return this;
    }

    public Vector2D getRotatedTo(float angle) {
        return toCartesian(getMagnitude(), angle);
    }

    public Vector2D setMagnitude(float magnitude) {
        float curr_mag = (float)Math.sqrt(x * x + y * y);
        x = x * magnitude / curr_mag;
        y = y * magnitude / curr_mag;
        return this;
    }

    public Vector2D reverse() {
        x = -x;
        y = -y;
        return this;
    }

    public Vector2D getReversed() {
        return new Vector2D(-x, -y);
    }

    @Override
    public Vector2D clone() {
        return new Vector2D(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vector2D) {
            Vector2D v = (Vector2D) obj;
            return (x == v.x) && (y == v.y);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Vector2d[" + x + ", " + y + "]";
    }
}