package RandomStuff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.impl.FASTAElementImpl;
import net.sf.jfasta.impl.FASTAFileImpl;
import net.sf.jfasta.impl.FASTAFileWriter;
import net.sf.kerner.utils.io.UtilIO;

import java.io.*;

public class Test {
	public static void main (String[] args) {
		File file = new File("/Users/irene/Documents/Phast/PHAST.html"); 
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String kw1 = "Lactobacillus"; //Bacteroides
		String kw2 = "tail fiber";
		String kw3 = "tail protein";
		
		//Summary File
		List<String> possible_phage = new ArrayList<String>();
		List<String> bacs = new ArrayList<String>();
		
		//Detailed File
		List<String> filter_name = new ArrayList<String>();
		List<String> tail_name = new ArrayList<String>();
		List<String> sequence_info = new ArrayList<String>();
		List<String> sequence = new ArrayList<String>();

		int total_kw1 = 0;
		
		try {
			String st;
			while ((st = br.readLine()) != null) { //Reading the database
				
				if(st.contains(kw1)) { //find items matching with keyword 1 (kw1)
					total_kw1++;
					int matches=0;
					int poss=0;
					String href = br.readLine();
					href = href.split("href=")[1].split("target")[0].replace("\"", "");
					URL oracle = new URL(href);
					BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
					String inputline;
		
					while ((inputline = in.readLine()) != null) {  //At the kw1 data page
						
						if(inputline.contains("Summary result file")) { //Find matches in Summary result file
	
							String addr = inputline.split("href=")[1].split(">")[0];
							addr = "http://phast.wishartlab.com"+addr;
							URL url = new URL(addr.replace("\"", ""));
							BufferedReader reading = new BufferedReader(new InputStreamReader(url.openStream()));
							String lines;
							
							while ((lines = reading.readLine()) != null) { //Find matches in Summary result file
								if(!lines.contains("Total") && lines.contains("phage")) { 
									for(int f=0; f<lines.split(">").length;f++) {
										if(lines.split(">")[f].contains("phage") && !lines.split(">")[f].contains("prediction")&&!lines.split(">")[f].contains("region")) {
											poss++;
											possible_phage.add(lines.split(">")[f].split("&nb")[0].replace(",", "").replace("bps</b", ""));
										}
									}
									break;
								}

							}
						}
						
						else if(inputline.contains("Detailed")) { 	//Find URL of detailed file for the matching item
							inputline = inputline.split("href=")[1].split(">Detail")[0].replace("\"", "");
							inputline = "http://phast.wishartlab.com"+ inputline;
							
							URL url = new URL(inputline);
							BufferedReader reading = new BufferedReader(new InputStreamReader(url.openStream()));
							String lines;
							
							while ((lines = reading.readLine()) != null) { //Find matches in detailed file page
								String[] parsed = lines.split("<");
								for(int i=0; i< parsed.length; i++) { 
									
									if(parsed[i].contains("promoter")){ //check if a promoter type exist
										System.out.println("yes!");
									}
									
									if(parsed[i].contains(kw2) || parsed[i].contains(kw3)) { //find items matching with keyword 2 and 3 (kw2. kw3)
										//System.out.println(parsed[i]);
										matches += 1;
										tail_name.add(parsed[i].split(">")[1].split("&nbsp")[0].replace(",", ";"));
										
										for(int n=1; n<parsed.length-i; n++) { // find matches' sequence
											if(parsed[i+n].contains("seq=")) {
												String[] seq = parsed[i+n].split("seq=")[1].split("&amp;");
												String g=seq[1].split(" rel")[0]+" "+seq[0];
												g= g.replace("\"", "").replace(">", "").replace("Click","").replace("'", "");
												sequence_info.add(g.split("rec=")[1].split(" ")[0]); //Sequence Description
												sequence.add(g.split("rec=")[1].split(" ")[1]); //Sequence by itself
												break;
											}
										}
									}
								}
							} 

						}

					}
					while(matches!=0) {
						filter_name.add(st.split("<td>")[1].split("</td>")[0].replace(",", ""));
						matches -= 1;
					}
					while(poss!=0) {
						
						bacs.add(st.split("<td>")[1].split("</td>")[0].replace(",", "").replace(".", ""));
						poss -= 1;
					}
				}

			}
			


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		System.out.println(sequence_info.size());
		System.out.println(sequence.size());
		//System.out.println(possible_phage.size());
		//System.out.println(bacs.size());
		for (int o=0; o<filter_name.size(); o++) {

			System.out.println(sequence_info.get(o));
			System.out.println(sequence.get(o));
			//System.out.println(possible_phage.get(o));
			//System.out.println(bacs.get(o));
			
			
			
		}
		*/
		// calculate repeats for Summary file
		List<List<Integer>> c = new ArrayList<List<Integer>>(); // the index of the repeats corresponding the first appearance
		List<Integer> ri= new ArrayList<Integer>(); // includes index of all repeats
		for(int d=0; d<possible_phage.size(); d++) {
			c.add(d, new ArrayList<Integer>());// initialize inner arrayList
			if(!ri.contains(d)) {
				for(int i=d+1; i< possible_phage.size(); i++) {
					if(possible_phage.get(d).toString().split(":")[0].equals(possible_phage.get(i).toString().split(":")[0])) { // find repeat
						c.get(d).add(i); //add repeat index
						ri.add(i);
					};  
				}
			}
		}
		
		// calculate repeats for detailed file
		List<List<Integer>> count = new ArrayList<List<Integer>>();
		List<Integer> repeat_index = new ArrayList<Integer>();
		for(int x=0; x<tail_name.size(); x++) {
			count.add(x, new ArrayList<Integer>());// initialize inner arrayList
			if(!repeat_index.contains(x)) {
				for(int y=tail_name.size()-1; y>x; y--) {
					if(tail_name.get(x).toString().split(":")[0].equals(tail_name.get(y).toString().split(":")[0])) { // find repeat
						count.get(x).add(y); //add repeat index
						repeat_index.add(y);
					};  
				}
			}
		}
		System.out.println(tail_name.size());
		System.out.println(total_kw1);
		

		System.out.println("The ratio of " + kw2 + " " + kw3 + " / " + kw1 + " is "+ (double) filter_name.size()/total_kw1);
		
		// write matches from detailed file as csv file
		try {
			PrintWriter writer = new PrintWriter(new File(kw1 +" detailed.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("phage name");
			sb.append(',');
			sb.append("key word");
			sb.append(',');
			sb.append("bacteria code");
			sb.append(',');
			sb.append("phage code");
			sb.append(',');
			sb.append("count");
			sb.append(',');
			sb.append("Bacteroide name");
			sb.append(',');
			sb.append("sequence information");
			sb.append(',');
			sb.append("sequence number");
			sb.append('\n');
			for(int j=0; j<sequence.size();j++) {
				if(!repeat_index.contains(j) && count.get(j).size() == 0) {
					if(tail_name.get(j).toString().split(":").length == 1) {
						sb.append("");
						sb.append(',');
					}else {
						for(int k=0; k< tail_name.get(j).toString().split(":").length-1; k++) {
							sb.append(tail_name.get(j).toString().split(":")[k]);
						}
						sb.append(',');
					}
					
					int rest = tail_name.get(j).toString().split(":").length-1;
					int len = tail_name.get(j).toString().split(":")[rest].split(";").length;
					
					for (int q=0; q< len; q++) {
						sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[q]);
						sb.append(',');
					}
					if (len <3) {
						for(int w=0; w<3-len; w++) {
							sb.append("");
							sb.append(',');
						}
					}
					/*
					System.out.println(rest);
					System.out.println(tail_name.get(j).toString().split(":")[rest].split(";")[0]);
					System.out.println(tail_name.get(j).toString().split(":")[rest].split(";")[1]);
					System.out.println(tail_name.get(j).toString().split(":")[rest].split(";")[2]);
					
					sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[1]);
					sb.append(',');
					sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[2]);
					sb.append(',');
					*/
					sb.append("1");
					sb.append(',');
					sb.append(filter_name.get(j).toString());
					sb.append(',');
					sb.append(sequence_info.get(j).toString());
					sb.append(',');
					sb.append(sequence.get(j).toString());
					sb.append('\n');

				}else {
					
					if(!repeat_index.contains(j)) {
						if(tail_name.get(j).toString().split(":").length == 1) {
							sb.append("");
							sb.append(',');
						}else {
							for(int k=0; k< tail_name.get(j).toString().split(":").length-1; k++) {
								sb.append(tail_name.get(j).toString().split(":")[k]);
							}
							sb.append(',');
						}
					
						
						int rest=tail_name.get(j).toString().split(":").length-1;
						int len = tail_name.get(j).toString().split(":")[rest].split(";").length;
						
						for (int q=0; q< len; q++) {
							sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[q]);
							sb.append(',');
						}
						if (len <3) {
							for(int w=0; w<3-len; w++) {
								sb.append("");
								sb.append(',');
							}
						}

						sb.append(Integer.toString(count.get(j).size()+1));
						sb.append(',');
						sb.append(filter_name.get(j).toString());
						sb.append(',');
						sb.append(sequence_info.get(j).toString());
						sb.append(',');
						sb.append(sequence.get(j).toString());
						sb.append('\n');

						for(int z=0; z<count.get(j).size();z++) {
							if(sequence.get(j).equals(sequence.get(count.get(j).get(z)))) {

								continue;
							}else {
								for(int p=0; p<5; p++) {
									sb.append("");
									sb.append(',');
								}
								sb.append(filter_name.get(count.get(j).get(z)).toString());
								sb.append(',');
								sb.append(sequence_info.get(count.get(j).get(z)).toString());
								sb.append(',');
								sb.append(sequence.get(count.get(j).get(z)).toString());
								sb.append('\n');

							}
						}

					}else {
						continue;
					}

				}

			}
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// write from summary file
		try {
			PrintWriter writer = new PrintWriter(new File(kw1 +" summary.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("phage name");
			sb.append(',');
			sb.append("count");
			sb.append(',');
			sb.append("Bacteroide name");
			sb.append('\n');
			for(int j=0; j<possible_phage.size();j++) {
				if(ri.contains(j)) {
					continue;
				}
				if(c.get(j).size() == 0) {

					sb.append(possible_phage.get(j).toString());
					sb.append(',');
					sb.append("1");
					sb.append(',');
					sb.append(bacs.get(j).toString());
					sb.append('\n');

				}else {

					sb.append(possible_phage.get(j).toString());
					sb.append(',');
					sb.append(Integer.toString(c.get(j).size()+1));
					sb.append(',');
					sb.append(bacs.get(j).toString());
					sb.append('\n');
					for(int z=0; z<c.get(j).size();z++) {
						for(int p=0; p<2; p++) {
							sb.append("");
							sb.append(',');
						}
						sb.append(bacs.get(c.get(j).get(z)).toString());
						sb.append('\n');
					}
					
				}

			}
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// write fasta file
		List<String> headers = new ArrayList<String>();
		try {
			File out = new File(kw1 + " seq.fasta");
			FASTAFileWriter writer = new FASTAFileWriter(out);
			for(int l=0; l<tail_name.size(); l++) {
				String header = "";
				for(int k=0; k< tail_name.get(l).toString().split(":").length-1; k++) {
					header += tail_name.get(l).toString().split(":")[k];
				}
				int rest = tail_name.get(l).toString().split(":").length-1;
				header += "-";
				for(int i=0; i< tail_name.get(l).toString().split(":")[rest].split(";").length; i++) {
					header += tail_name.get(l).toString().split(":")[rest].split(";")[i];
					header += "-";
					
				}
				header += filter_name.get(l).toString();
				if(!headers.contains(header)) {
					writer.write(new FASTAElementImpl(header.replace(" ", "_"), sequence.get(l).toString()));
					headers.add(header);
				}
				

			}
			writer.close();


		}
		catch(IOException e) {

		}


	}


}
