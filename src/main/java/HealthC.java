import java.awt.*;

public class HealthC extends Component{

    int maxHP;
    int currHP;

    public HealthC(int haxHP) {
        super();
        this.maxHP = maxHP;
        this.currHP = maxHP;
    }

    @Override
    public String toString() {
        return "Health{" +
                "maxHP=" + maxHP +
                ", currHP=" + currHP +
                ", containers=" + containers +
                '}';
    }

    public void update(GameObject parent) {

    }

    public void lateUpdate(GameObject parent) {

    }

    public void draw(GameObject parent, Graphics g) {

    }
}
