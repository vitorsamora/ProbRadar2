import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;

public class Map {
	public Map() {
		
	}
	
	public LineMap readMap(String file) throws NumberFormatException, IOException {
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		ArrayList<Line> lines = new ArrayList<Line>();
		Rectangle bounds = new Rectangle(0, 0, 0, 0);
		boolean firstLine = true;
		
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			String splStr[] = strLine.split(",");
			if (firstLine) {
				bounds = new Rectangle(
						Float.parseFloat(splStr[0]), Float.parseFloat(splStr[1]),
						Float.parseFloat(splStr[2]), Float.parseFloat(splStr[3])
				);
				firstLine = false;
			}
			else {
				lines.add(new Line(
						Float.parseFloat(splStr[0]), Float.parseFloat(splStr[1]),
						Float.parseFloat(splStr[2]), Float.parseFloat(splStr[3]))
				);
			}
		}
		br.close();
		LineMap myMap = new LineMap( (Line[]) toArray(lines), bounds);
		return myMap;
	}
	
	private Line[] toArray(ArrayList<Line> list)
	{
		Line[] array = new Line[list.size()];
		int i = 0;
		System.out.println("******Map*********:");
		for(Line l : list)
		{
			System.out.println(l.x1+","+l.y1+"---------"+l.x2+","+l.y2);
			array[i++] = l;
		}
		
		return array;
		
	}
}