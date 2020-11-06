public class LasergunC extends GunC {

    public LasergunC() {
        super(1, 1);
    }

    @Override
    public void fire(GameObject container) {
        //TODO Add bullets to relevant list of bullets (or gameobjects)

        //System.out.println("Player = " + link.getCenterPosX() + ", GunController = " + getCenterPosX());

        // Tell the AudioComponent to play the firing sound
        container.getComponent(AudioComponent.class).playAudio(audio_filename, false);

        nH.bulletList.add(new BulletFactory(nH, this, xB, yB, getCenterPosX(), getCenterPosY(), link.getShootingDirection()-90, vel, ally, aD0, 0));
        link.setXVel(-stagger * Math.cos(link.getPointTheta()));
        link.setYVel(-stagger * Math.sin(link.getPointTheta()));
    }
}
