public class GunFactory {
    private static boolean instantiated = false;

    public GunFactory() {
        if (instantiated) {
            System.err.println("AudioManager should only be initialized once.");
        }
        instantiated = true;
    }

    /**
     * A gun is a GameObject that is a child of the player GameObject. The gun_obj has the following
     * Components: Damage, AudioComponent, GunComponent, StripImage
     * This method checks and creates the necessary components for a player_obj to use a gun_obj
     *
     * @param player_obj GameObject with a GunController Component
     * @param gun_type
     * @param enemy
     * @param damage
     * @param knockback
     * @return the gun GameObject that is a child of the player_obj
     */
    public GameObject giveGun(GameObject player_obj, GunType gun_type, boolean enemy, int damage, int knockback) {
        // Adds a GunController to the player_obj if it doesn't have one yet.
        if(!player_obj.hasComponent(GunControllerC.class)) {
            System.err.println("GunFactory.giveGun(): player_obj does not have a GunController.");
            player_obj.addComponent(new GunControllerC(), GunControllerC.class);
        }

        GameObject gun_obj;

        // Creates a gun GameObject depending on the GunType
        switch (gun_type) {
            case shotgun:
                gun_obj = createShotgun(enemy, damage, knockback);
                break;
            case laser:
                gun_obj = createLaser(enemy, damage, knockback);
                break;
            default:
                System.err.println("GunFactory.giveGun(): not a valid GunType");
                gun_obj = createShotgun(enemy, damage, knockback);
                break;
        }

        // Adds the gun_obj to the player_object and connects the GunController
        player_obj.addChild(gun_obj);

        return gun_obj;
    }

    private GameObject createShotgun(boolean enemy, int damage, int knockback) {
        // Construct the gun GameObject
        GameObject gun_obj = new GameObject();

        // Construct and add GunComponent, Damage, StripImage, and AudioComponent to the gun_obj
        gun_obj.addComponent(new ShotgunC(), GunC.class);
        gun_obj.addComponent(new Damage(gun_obj, enemy, damage, knockback), Damage.class);
        gun_obj.addComponent(new StripImageC("gun.png", 2), StripImageC.class);
        gun_obj.addComponent(new AudioComponent("shotgun.wav"), AudioComponent.class);

        return gun_obj;
    }

    private GameObject createLaser(boolean enemy, int damage, int knockback) {
        // Construct the gun GameObject
        GameObject gun_obj = new GameObject();

        // Construct and add GunComponent, Damage, StripImage, and AudioComponent to the gun_obj
        gun_obj.addComponent(new LasergunC(), GunC.class);
        gun_obj.addComponent(new Damage(gun_obj, enemy, damage, knockback), Damage.class);
        gun_obj.addComponent(new StripImageC("gun.png", 2), StripImageC.class);
        gun_obj.addComponent(new AudioComponent("laser.wav"), AudioComponent.class);

        return gun_obj;
    }
}
