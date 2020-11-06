public class ShotgunC extends GunC {

    public ShotgunC() {
        super(1, 1);
    }

    public void fire() {
        //TODO Add bullets to relevant list of bullets (or gameobjects)

        shotgun.get(nextSound).play(false);
        if(nextSound > 4) {
            nextSound = 0;
        } else {
            nextSound++;
        }
        nH.bulletList.add(new BulletFactory(nH, this, xB2, yB2, getCenterPosX(), getCenterPosY(), link.getShootingDirection()-90 + Math.random() * (9) - 20, vel, ally, aD1,1));
        nH.bulletList.add(new BulletFactory(nH, this, xB2, yB2, getCenterPosX(), getCenterPosY(), link.getShootingDirection()-90 + Math.random() * (9) - 12, vel, ally, aD1,1));
        nH.bulletList.add(new BulletFactory(nH, this, xB2, yB2, getCenterPosX(), getCenterPosY(), link.getShootingDirection()-90 + Math.random() * (9) - 4, vel, ally, aD1,1));
        nH.bulletList.add(new BulletFactory(nH, this, xB2, yB2, getCenterPosX(), getCenterPosY(), link.getShootingDirection()-90 + Math.random() * (9) + 4, vel, ally, aD1,1));
        nH.bulletList.add(new BulletFactory(nH, this, xB2, yB2, getCenterPosX(), getCenterPosY(), link.getShootingDirection()-90 + Math.random() * (9) + 12, vel, ally, aD1,1));
        link.setXVel(-stagger * Math.cos(link.getPointTheta()));
        link.setYVel(-stagger * Math.sin(link.getPointTheta()));
    }

}
