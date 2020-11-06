public class Ammo {
    private float global_ammo;
    private float global_max_ammo;



    public Ammo(float max_ammo) {
        this.global_max_ammo = max_ammo;
        global_ammo = max_ammo;

    }

    public void addGlobal(float global_add) {
        global_ammo += global_add;
    }

    /**
     * Converts the current ammo from global ammo base to individual ammo base
     * No rounding
     * @param max_ammo
     * @return current ammo in base max_ammo
     */
    public float convertCurrentAmmo(int max_ammo) {
         return (global_ammo / (global_max_ammo / max_ammo));
    }

    /**
     * If a gun with some max_ammo can fire with the current amount of global ammo.
     * @param max_ammo
     * @return
     */
    public boolean canFire(int max_ammo) {
        return ((int) convertCurrentAmmo(max_ammo) >= 1);
    }

    /**
     * If this gun can fire with the current amount of global ammo.
     * @param gun_comp
     * @return
     */
    public boolean canFire(GunC gun_comp) {
        return ((int) convertCurrentAmmo(gun_comp.num_bullets) >= 1);
    }

    public void removeBullet(GunC gun_comp) {
        global_ammo -= (int) convertCurrentAmmo(gun_comp.num_bullets);
    }
}
