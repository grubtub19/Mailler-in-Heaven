import java.awt.*;

public class Damage extends Component{
    boolean enemy;
    int damage;
    int knockback;

    /**
     *
     * @param enemy
     * @param damage
     * @param knockback
     */
    public Damage(boolean enemy, int damage, int knockback) {
        super();
        this.enemy = enemy;
        this.damage = damage;
        this.knockback = knockback;
    }

    public void update(GameObject parent) {

    }

    public void lateUpdate(GameObject parent) {

    }

    public void draw(GameObject parent, Graphics g) {

    }

    @Override
    public String toString() {
        return "Damage{" +
                "enemy=" + enemy +
                ", damage=" + damage +
                ", containers=" + containers +
                '}';
    }
}
