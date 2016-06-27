import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.LinkedList;
import java.awt.Point;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import lejos.robotics.navigation.Pose;

import javax.swing.JPanel;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.navigation.Pose;
import lejos.robotics.mapping.LineMap;
import lejos.geom.*;

public class PrintMap extends JPanel {
	private static double zoom = 2.0; // pixel per cm
	private static double grid = 10.0; // cm
	private double centerx = 0.0;
	private double centery = 0.0; // cm
	private Point mousePt;
	private LinkedList<Pose> pontos;
    private ImageMap map ;
    private LineMap lmap;
    private Pose reference = null;

	public PrintMap (LineMap map) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        this.map = new ImageMap();
        this.lmap = map;
		frame.add(this.map);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	public void addPose (Pose p) {
        pontos.add(p);
        map.repaint();
	}
	
	public void addRef(Pose p)
	{
		reference = p;
		map.repaint();
	}

	public void clear () {
        pontos.clear();
        map.repaint();
	}

    class ImageMap extends JPanel {
        /**
         * 
         */
        private final long serialVersionUID = 1L;
        private double zoom = 2.0; // pixel per cm
        private double grid = 10.0; // cm
        private double centerx = 0.0;
        private double centery = 0.0; // cm
        private Point mousePt;
        
        
        public void clear () {
            pontos.clear();
        }

        public ImageMap(){
            pontos = new LinkedList<Pose>();
            
            setBackground(Color.BLACK);
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    // TODO Auto-generated method stub
                    if(e.getWheelRotation()<0){
                        if (zoom < 10.0)
                            zoom *= 1.1;
                        repaint();
                    }
                    //Zoom out
                    if(e.getWheelRotation()>0){
                        if (zoom > 1.0)
                            zoom /= 1.1;
                        repaint();
                    }
                }
            });

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    mousePt = e.getPoint();
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionListener() {
                
                @Override
                public void mouseMoved(MouseEvent e) {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    centerx += e.getX() - mousePt.x;
                    centery += e.getY() - mousePt.y;
                    mousePt = e.getPoint();
                    repaint();
                }
            });
           
        }
    
        public void drawModel (Graphics g) {
            int width = (int) (getWidth()+centerx*2);
            int height = (int) (getHeight()+centery*2);

            for (Pose p: pontos) {
                //g.setColor(Color.getHSBColor((float) (p.getHeading()/360.0), 1, 1));
                g.setColor(Color.RED);

                //g.fillOval(width/2+(int)(p.getX()*zoom), height/2-(int)(p.getY()*zoom), 2, 2);
                double ang = Math.toRadians(p.getHeading());
                g.drawLine(
                    width/2+(int)(p.getX()*zoom),
                    height/2-(int)(p.getY()*zoom), 
                    width/2+(int)(p.getX()*zoom+Math.cos(ang)*zoom),
                    height/2-(int)(p.getY()*zoom-Math.sin(ang)*zoom)
                );

               g.drawLine(
                    width/2+(int)(p.getX()*zoom+zoom*Math.cos(ang)),
                    height/2-(int)(p.getY()*zoom-zoom*Math.sin(ang)),
                    width/2+(int)(p.getX()*zoom+0.6*zoom*Math.cos(Math.PI/8+ang)),
                    height/2-(int)(p.getY()*zoom-0.6*zoom*Math.sin(Math.PI/8+ang))
                );
                //System.out.prinln(p);
            }
            
            if(reference != null){
	            Pose p = reference;
	            g.setColor(Color.GREEN);
	
	            //g.fillOval(width/2+(int)(p.getX()*zoom), height/2-(int)(p.getY()*zoom), 2, 2);
	            double ang = Math.toRadians(p.getHeading());
	            g.drawLine(
	                width/2+(int)(p.getX()*zoom),
	                height/2-(int)(p.getY()*zoom), 
	                width/2+(int)(p.getX()*zoom+Math.cos(ang)*zoom),
	                height/2-(int)(p.getY()*zoom-Math.sin(ang)*zoom)
	            );
	
	           g.drawLine(
	                width/2+(int)(p.getX()*zoom+zoom*Math.cos(ang)),
	                height/2-(int)(p.getY()*zoom-zoom*Math.sin(ang)),
	                width/2+(int)(p.getX()*zoom+0.6*zoom*Math.cos(Math.PI/8+ang)),
	                height/2-(int)(p.getY()*zoom-0.6*zoom*Math.sin(Math.PI/8+ang))
	            );
            }

            
            g.setColor(Color.CYAN);
            for (Line l: lmap.getLines()) {
                g.drawLine(width/2+(int)(l.x1*zoom),height/2-(int)(l.y1*zoom), 
                        width/2+(int)(l.x2*zoom),height/2-(int)(l.y2*zoom));
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            int width = (int) (getWidth());
            int height = (int) (getHeight());
            int width2 = (int) (getWidth()+2*centerx);
            int height2 = (int) (getHeight()+2*centery);
            super.paintComponent(g);
        
            int start = 0;
            g.setColor(new Color(20, 20, 20));
            while (start < getWidth()/2) {
                g.drawLine(width/2+start, 0, width/2+start, getHeight());
                g.drawLine(width/2-start, 0, width/2-start, getHeight());
                start += grid*zoom;
            }
            start = 0;
            while (start < getHeight()/2) {
                g.drawLine(0, height/2+start, getWidth(), height/2+start);
                g.drawLine(0, height/2-start, getWidth(), height/2-start);
                start += grid*zoom;
            }
            g.setColor(Color.ORANGE);
            g.drawLine(width2/2, 0, width2/2, getHeight());
            g.drawLine(0, height2/2, getWidth(), height2/2); 
            
            drawModel(g);
        }

    }

	public static void main(String[] args)  {

        // Linhas do mapa
        Line[] maplines = new Line []{
            new Line(0, 0, 200, 0),
            new Line(0, 0, 0, 200),
            new Line(0, 200, 200, 200),
            new Line(200, 0, 200, 200)
        };

        // Área em que o mapa está contido
        Rectangle bounds = new Rectangle(0, 0, 200, 200);
        LineMap map = new LineMap(maplines, bounds);
        PrintMap pm = new PrintMap (map);

        for (int i = 0; i < 1000; i++) {
            pm.addPose
                (new Pose(200f*(float)Math.random(), 
                200f*(float)Math.random(), 
                (float)Math.random()*360f)
            );
        }
	}
}
