package RandomStuff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Detailed {
	public static void main (String[] args) {
		
		String lin = "<td><a href=\"/cgi-bin/change_to_html.cgi?num=AAVM00000000\">Summary result file</a></td>";
		//a.split("rec=");
		 lin = lin.split("href=")[1];
		 lin = lin.split(">")[0];
		System.out.println(lin.replace("\"", ""));
		List<List<Integer>> count = new ArrayList<List<Integer>>();
		count.add(0, new ArrayList<Integer>());
		//count.get(0).add(34);
		System.out.println(count.get(0).size());
		/*
		File file = new File("/Users/irene/Desktop/AAVM000000.html"); 
		  
		  BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String st = "";
		String[] strings;
		int n;
		String temp;
		String[] seq;
		List<String> tail_name = new ArrayList<String>();
		List<String> sequence = new ArrayList<String>();
		 try {
				while ((st = br.readLine()) != null) { //bacteroide
					strings = st.split("<");
					for(int i=0; i< strings.length; i++) {
						if(strings[i].contains("tail fiber")) {
							System.out.println(strings[i].split(">")[1]);
							tail_name.add(strings[i].split(">")[0]);
							for(n=1; n<strings.length-i; n++) {
								if(strings[i+n].contains("target")) {
									temp = strings[i+n].split("seq=")[1];
									seq = temp.split("&amp;");
									sequence.add(seq[1].split(" rel")[0].replace("\"", "")+" "+seq[0]);
									System.out.println(seq[1].split(" rel")[0].replace("\"", "")+" "+seq[0]);
									break;
								}
							}
							break;
						}
					}
				}
				br.close();
				
		  
			
				
			} catch (IOException e) {
				
			}
	
	}*/
	}
	
}
