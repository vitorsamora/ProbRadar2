import lejos.robotics.navigation.Pose;


public class Particle {
	public Pose pose;
	public float weight;
	
	public Particle(Pose pose) {
		this.pose = pose;
		this.weight = 1.0f;
	}
	
	public void applyMove(float angle) {
		pose.setHeading((float)((int)(pose.getHeading() + angle + 0.5f)%360));
	}
	
}
