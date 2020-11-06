public class RandomTimer extends Timer {
    public float a;
    public float b;

    public RandomTimer(float a, float b) {
        super(newMax(a, b));
        this.a = a;
        this.b = b;

    }

    private static int newMax(float a, float b) {
        return (int) (a * Math.random() + b);
    }

    public void start() {
        active = true;
        timer = newMax(a, b);
    }
}
