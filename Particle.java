import lejos.robotics.navigation.Pose;


public class Particle {
	public Pose pose;
	public float weight;
	
	public Particle(Pose pose) {
		this.pose = new Pose(pose.getX(), pose.getY(), pose.getHeading());
		this.weight = 1.0f;
	}
	
	public void applyMove(float angle) {
		
		float oldHeading = pose.getHeading();
		oldHeading = oldHeading < 0? 360 + oldHeading : oldHeading;
		
		pose.setHeading((float)((int)((oldHeading + angle + 0.5f)%360)));
		
	}
	
}
