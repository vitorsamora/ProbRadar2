import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class ReadMeasures {
	
	FileInputStream fstream;
	BufferedReader br;
	
	public ReadMeasures(String file)
	{
		try{
			fstream = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fstream));
		}
		catch(Exception e)
		{
			System.out.println("Problem while reading measures");
			System.out.println(e.getMessage());
		}
	}
	
	public int getNext()
	{
		int resp = 0;
		String strLine;
		try {
			strLine = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;	
		}
		if(strLine == null) return -1;
		String splStr[] = strLine.split("\t");
		resp = Integer.parseInt(splStr[1]);
		return resp;
	}

}
