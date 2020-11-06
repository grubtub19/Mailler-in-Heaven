import java.awt.Graphics;
import java.util.ArrayList;

/**
 * An abstract class for Components that GameObjects have. Not useful on its own.
 */
public abstract class Component {

    /**
     * List of GameObjects that contain this Component.
     * think about it this way
     */
    ArrayList<GameObject> containers;

    /**
     * Constructor
     */
    public Component() {
        containers = new ArrayList<GameObject>(1);
    }

    /**
     * Add a new parent GameObject to the list of containers
     * The GameObject calls this in its addComponent function
     * @param container
     */
    public void addContainer(GameObject container) {
        if(!containers.contains(container)) {
            containers.add(container);
        } else {
            System.out.println("addContainer(): This container is already added.");
        }
    }

    /**
     * Remove the gameObject from the list of GameObjects that contain this Component. Should be called when gameObject is removing
     * this component.
     * @param container
     */
    public void removeContainer(GameObject container) {
        // If the gameObject is not in the containers list already
        if(!containers.remove(container)) {

            // Throw an exception
            System.err.println("GameObject: " + container.toString() + ", is not this Component's: " + this.toString() + ", container.");
        }
    }

    public abstract void update(GameObject parent);

    /**
     * Used to calculate information taken from the update phase for use in the draw phase.
     * Don't change Tranforms here. Play
     * @param parent
     */
    public abstract void lateUpdate(GameObject parent);
    public abstract void draw(GameObject parent, Graphics g);
    public abstract String toString();
}

