import java.awt.*;
import java.util.ArrayList;

/**
 * Only the curr_gun is active
 */
public class GunControllerC extends Component {

	private ArrayList<GameObject> guns;
	public GameObject curr_gun;
	public Ammo ammo;
	private Timer ammo_display_timer;
	/**
	 * Rotation in degrees starting from the right
	 */
	public float facing_direction;

	public GunControllerC() {
		curr_gun = null;
		ammo = new Ammo(100);
		ammo_display_timer = new Timer(300, false);
		guns = new ArrayList<GameObject>(1);
		curr_gun = null;
	}

	/**
	 * Adds a new gun. Sets as the curr_gun if it is the first. Disables it otherwise.
	 * @param container
	 * @param gun_type
	 */
	public void addGun(GameObject container, GunType gun_type) {
		//TODO give the guns different damage stats than just inheriting the stats of the container
		Damage damage = container.getComponent(Damage.class);
		GameObject gun_obj = Game.get().gun_factory.giveGun(container, gun_type, damage.enemy, damage.damage, damage.knockback);

		// If there is already a gun
		if (curr_gun != null) {
			// disable the new gun
			gun_obj.is_active = false;

		// If this is the first gun
		} else {
			// Make it the curr gun
			curr_gun = gun_obj;
		}

		guns.add(gun_obj);
	}

	public boolean isVisible() {
		return ammo_display_timer.isActive();
	}

	/**
	 * this changes between loadout
	 */
	public void changeType() {
		// Deactivate the current gun
		curr_gun.is_active = false;

		// Select the next gun in the list to become the new curr_gun
		int curr_index = guns.indexOf(curr_gun);
		curr_gun = guns.get((curr_index + 1) % guns.size());

		// Make the new curr_gun active
		curr_gun.is_active = true;
	}
	
	/**
	 * adds global ammo. And show's the ammo for some more time
	 * @param global_add
	 */
	public void addAmmo(float global_add) {
		ammo.addGlobal(global_add);
		ammo_display_timer.restart();
	}
	
	/**
	 * shoots the gunController
	 */
	public void fire(GameObject container) {
		System.out.println("shot the gunController");

		// If we are holding a gun
		if (curr_gun != null) {

			// Display the gun
			ammo_display_timer.restart();

			GunC gun_comp = curr_gun.getComponent(GunC.class);

			// If the current gun's ammo requirement is met
			if (ammo.canFire(gun_comp)) {

				// Measure the amount of ammo a single bullet from the current gun takes
				// Remove ammo equivalent to one shot from the current gun
				ammo.removeBullet(gun_comp);

				// Fire the gun
				gun_comp.fire(container);
			}
		}
	}

	/**
	 * Adds the gameObject as a gun. TODO check for the same GunType?
	 * @param gun_obj must have the Gun Component
	 */
	public void addGun(GameObject gun_obj) {
		guns.add(gun_obj);
	}

	public GunType getCurrType() {
		return curr_gun.getComponent(GunC.class).type;
	}

	/**
	 * draws the gunController
	 */
	public void draw(Graphics g) {

	}

	//TODO fix link references
	public void update(GameObject container) {

	}

	//TODO fix link references
	public void lateUpdate(GameObject container) {

	}

	public void draw(GameObject container, Graphics g) {

	}

	public String toString() {
		return null;
	}
}