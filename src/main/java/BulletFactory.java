public class BulletFactory {	//player will also be able to shoot a gunController that has very limited ammo and recharges on hitting the enemy



	public BulletFactory() {

	}

	public GameObject createBullet(GunType gun_type, boolean enemy) {
		GameObject bullet_obj = new GameObject();

		// Create bullet with different properties per GunType
		if (gun_type == GunType.shotgun) {
			createShotgunBullet(bullet_obj, enemy);
		} else if (gun_type == GunType.laser) {
			createLaserBullet(bullet_obj, enemy);
		}

		/**
		 * The second image of the strip is always the enemy version.
		 */
		if(enemy) {
			bullet_obj.getComponent(StripImageC.class).image_num = 1;
		} else {
			bullet_obj.getComponent(StripImageC.class).image_num = 0;
		}
		return bullet_obj;
	}

	private GameObject createLaserBullet(GameObject bullet_obj, boolean enemy) {
		bullet_obj.addComponent(new Hitbox(0.5f), Hitbox.class);
		bullet_obj.addComponent(new RigidBody(1, 0), RigidBody.class);
		bullet_obj.addComponent(new Damage(enemy, 10, 1), Damage.class);
		bullet_obj.addComponent(new StripImageC("bullet.png", 2), StripImageC.class);
		return bullet_obj;
	}


	private GameObject createShotgunBullet(GameObject bullet_obj, boolean enemy) {
		bullet_obj.addComponent(new Hitbox(0.5f), Hitbox.class);
		bullet_obj.addComponent(new RigidBody(1, 0), RigidBody.class);
		bullet_obj.addComponent(new Damage(enemy, 20, 1), Damage.class);
		bullet_obj.addComponent(new StripImageC("bullet.png", 2), StripImageC.class);
		return bullet_obj;
	}
}