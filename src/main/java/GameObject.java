import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GameObject {
	public HashMap<Class<? extends Component>, Component> components;
	public GameObject parent;
	public ArrayList<GameObject> children;

	/**
	 * Whether or not it will be drawn
	 */
	public boolean visible;

	/**
	 * Whether or not it will be updated
	 */
	public boolean is_active;

	public GameObject() {
		components = new HashMap<Class<? extends Component>, Component>();
		parent = null;
		children = new ArrayList<GameObject>();
		visible = true;
		is_active = true;

		// TransformC is a mandatory component
		addComponent(new TransformC(), TransformC.class);
	}

	public GameObject(GameObject parent) {
		components = new HashMap<Class<? extends Component>, Component>();
		this.parent = parent;
		children = new ArrayList<GameObject>();
		visible = true;
		// TransformC is a mandatory component
		addComponent(new TransformC(), TransformC.class);
	}

	/**
	 * Establishes a connection between the GameObject and the Component
	 * @param component
	 * @param type
	 */
	public void addComponent(Component component, Class<? extends Component> type) {
		if(components.containsKey(type)) {
			System.err.println("GameObject: " + this.toString() + ", already has component of type: " + type.toString() + ".");
		}
		components.put(type, component);
		component.addContainer(this);
	}

	public boolean hasComponent(Class<? extends Component> type) {
		return components.containsKey(type);
	}

	public void removeComponent(Class<? extends Component> type) {
		Component removed_component = components.remove(type);

		// Check if this GameObject had this type of Component
		if(removed_component == null) {
			System.err.println("GameObject: " + this.toString() + ", does not have component of type: " + type.toString() + ".");

		// Try to remove this GameObject from the Component's containers
		} else {
			removed_component.removeContainer(this);
		}
	}

	public void addChild(GameObject child) {
		children.add(child);
		child.parent = this;
	}

	public boolean hasChild(GameObject child) {
		return children.contains(child);
	}

	public void removeChild(GameObject child) {
		if(!children.remove(child)) {
			System.err.println("GameObject.removeChild(): specified child is not a child of this GameObject");
		} else {
			child.parent = null;
		}
	}

	/**
	 *
	 * @param type the class of the subclass of Component you want
	 * @param <T> the type of the subclass of Component you want
	 * @return Component of type {@code type} or null if no Component of that type exists
	 */
	public <T extends Component> T getComponent(Class<T> type) {
		return (T) components.get(type);
	}

	/**
	 * All transformations must occur here
	 */
	public void update() {
		if (is_active) {
			for (Component component: components.values()) {
				component.update(this);
			}
			for (GameObject child: children) {
				child.update();
			}
		}
	}

	public void collisionUpdate() {
		if (is_active) {
			for (GameObject child: children) {
				child.collisionUpdate();
			}
		}
	}

	/**
	 * All Audio playing/pausing must occur here.
	 * No Transformations.
	 * Update graphics here.
	 */
	public void lateUpdate() {
		if (is_active) {
			for (Component component : components.values()) {
				component.lateUpdate(this);
			}
			for (GameObject child: children) {
				child.lateUpdate();
			}
		}
	}

	public void draw(Graphics g) {
		if (visible) {
			for (Component component : components.values()) {
				component.draw(this, g);
			}
			for (GameObject child: children) {
				child.draw(g);
			}
		}
	}
}
