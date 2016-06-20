
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lejos.geom.Point;
import lejos.robotics.mapping.*;
import lejos.robotics.navigation.Pose;

public class ProbRadar2 {
	ArrayList<Point> possiblePoints;
	float[][] probPoints;
	int[][] expectedMeasures;
	MasterRobot master;
	Map map;
	int sizeAng = 5;
	LineMap myMap;
	float WIDTH, HEIGHT;
	int M = 1000;
	final static boolean VERBOSE = false;
	ReadMeasures rm;
	
	public static void main(String[] args)
	{
		ProbRadar2 bla = new ProbRadar2();
		try {
			bla.doSomething();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	
	
	public ProbRadar2(){
		master = new MasterRobot();
		//master.connect();
		map = new Map();
		try {
			myMap = map.readMap("map");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		possiblePoints = new ArrayList<Point>();
		
		
		
		possiblePoints.add(new Point(100, 27));
		possiblePoints.add(new Point(31, 156));
		possiblePoints.add(new Point(102, 156));
		
		rm = new ReadMeasures("p2.txt");
		
		WIDTH= myMap.getBoundingRect().width;
		HEIGHT= myMap.getBoundingRect().height;
	}
	
	public void doSomething() throws NumberFormatException, IOException
	{
		Particle[] particles = new Particle[M];
		int angle = 5;
		
		
		if(VERBOSE) System.out.println("Doing something~~~~~~~ ");
		
		
		//Inicialização
		Random rand = new Random();
		for(int i = 0; i < M; i++)
		{
			Pose p;
			Point point;
			do{	
			p = new Pose(WIDTH*rand.nextFloat(), HEIGHT*rand.nextFloat(), rand.nextInt(360));
			point = new Point((int)p.getX(), (int)p.getY());
			}while(!myMap.inside(point));
			particles[i] = new Particle(p);
		}
		
		
		for(int k = 0; k < 360/angle; k++)
		{
			//Percepção
			float range = getReading();
			float soma = 0.0f;
			for (int i = 0; i < M; i++) {
				particles[i].weight = sonarProbability(range, particles[i].pose);
				soma += particles[i].weight;
			}
			Particle[] oldParticles = particles;
			particles = new Particle[M];
			for (int i = 0; i < M; i++) {			
				float u = rand.nextFloat();
				float prob = 0.0f;
				for (int j = 0; j < M; j++) {
					if (prob > u) break; 
					else prob += oldParticles[j].weight/soma;
					particles[i] = new Particle(oldParticles[j].pose);
				}
			}
			
			
			
			/*
			//Localização
			Pose pose = new Pose(0, 0, 0);
			for (int i = 0; i < M; i++) {
				pose.setLocation(pose.getX() + particles[i].pose.getX(), 
						pose.getY() + particles[i].pose.getY());
				pose.setHeading(pose.getHeading() + particles[i].pose.getHeading());
			}
			pose.setLocation(pose.getX()/M, pose.getY()/M);
			pose.setHeading(pose.getHeading()/M);
			*/		
			
			//Predição
			
			move(angle);
			for (int i = 0; i < M; i++) {
				particles[i].applyMove(angle);
			}
		}
		
		
		printMaxPoint(particles);
		
			
	}
	
	private Particle[] diversificate(Particle[] particlesExt)
	{
		//Diversificação
		int N = 4;
		Random rand = new Random();
		//gerar M-M/N partículas distribuídas de acordo com a crença atual
		Particle[] oldParticles = particlesExt;
		Particle[] particles = new Particle[M];
		for (int i = 0; i < M - M/N; i++) {
			int j = rand.nextInt(M);
			particles[i] = new Particle(oldParticles[j].pose);
		}
		//gerar M/N partículas uniformemente distribuídas
		for (int i = M/N; i < M; i++) {
			Pose p;
			Point point;
			do{	
				p = new Pose(WIDTH*rand.nextFloat(), HEIGHT*rand.nextFloat(), rand.nextInt(360));
				point = new Point((int)p.getX(), (int)p.getY());
			}while(!myMap.inside(point));
			particles[i] = new Particle(p);
		}
		
		return particles;
		
	}
	
	
	private void move(int angle)
	{
		//master.rotate(-angle);
	}
	
	private int getReading()
	{
		//return master.range();
		return rm.getNext();	
	}
	
	private float sonarProbability(float range, Pose pose) {
		float expec = 255;
		try {
			expec = this.shouldReturnThisAt(pose);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return probMeasureSonar((int)range,(int) expec );
	}
	
	
	private float shouldReturnThisAt(Pose tmppose) throws NumberFormatException, IOException
	{
		
		float theta = tmppose.getHeading();
		theta = theta < 0 ? 360 + theta : theta;
		//Pose mypose = new Pose(x, y, theta);
		int cone = 30;
		
		
		float mindist = Float.POSITIVE_INFINITY; 
		for (int angulo =- cone/2; angulo <= cone/2; angulo++) { 
		tmppose.setHeading(	theta - angulo); 
		float dist = myMap.range(tmppose); 
		if (dist > 0 && dist < 	mindist	) 
			mindist = dist; 
		} // mindist = 11
		return mindist;
	}
	
	private float probMeasureSonar(int read, int expected)
	{
		if(read == 255) return 0.01f;
		return getFromGaussDistrib(read-expected);
	}
	
	private float getFromGaussDistrib(int diff)
	{
		float var = 5;
		float prob = (float) Math.exp(-diff*diff/(2*var));
		if(Math.abs(diff) > 3*Math.sqrt(var)) prob = 0.01f;
		return prob;
	}
	/*
	private void printMaxProb()
	{
		int p = 0, thetaMax = 0;
		float maxprob = this.probPoints[p][thetaMax];
		for(int i = 0; i < possiblePoints.size(); i++)
		{
			for(int theta = 0; theta < 360/this.sizeAng; theta++)
			{
				if(VERBOSE) System.out.println("prob["+i+"]["+theta+"] = "+ probPoints[i][theta]);
				if(this.probPoints[i][theta] > maxprob)
				{
					maxprob = probPoints[i][theta];
					thetaMax = theta;
					p = i;
				}
			}
		}
		
		System.out.println("**MAX PROB** Point:"+p+" Theta:"+thetaMax*this.sizeAng);
	}
	*/
	
	private void printMaxPoint(Particle[] p)
	{
		double x = 0, y = 0, heading = 0;
		for(int i = 0; i < M; i++)
		{
			x += p[i].pose.getX(); y += p[i].pose.getY();
			heading += p[i].pose.getHeading();
		}
		x /= M; y /= M; heading /= M;
		System.out.println("**MEAN POINT** X:"+x+" Y:"+y+" THETA:"+heading);
	}
	
	private void printProbPoints()
	{
		float[] probPoint = new float[possiblePoints.size()];
		for(int i = 0; i < possiblePoints.size(); i++)
		{
			probPoint[i] = 0;
			for(int theta = 0; theta < 360/this.sizeAng; theta++)
			{
				probPoint[i] += probPoints[i][theta];
			}
		}
		System.out.println("-------Probabilities of each point--------");
		for(int i = 0; i < possiblePoints.size(); i++)
			System.out.println("Point "+i+" prob: "+probPoint[i]);
		
		
	}
	
	
	

}