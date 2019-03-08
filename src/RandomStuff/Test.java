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

		String kw1 = "Bacteroide";
		String kw2 =  "tail fiber";
		String kw3 = "tail fiber";
		String st = "";
		URL oracle;
		BufferedReader in;
		String inputline = "";
		String addr = "";
		URL url;
		BufferedReader reading;
		String lines = "";
		String[] strings;
		int n;
		String temp;
		String[] seq;
		List<String> bac_name = new ArrayList<String>();
		List<String> tail_name = new ArrayList<String>();
		List<String> sequence_info = new ArrayList<String>();
		List<String> sequence = new ArrayList<String>();
		List<String> possible_phage = new ArrayList<String>();
		String g;
		int tail=0;
		int poss=0;
		List<String> bacs = new ArrayList<String>();
		int total_bac=0;
		try {
			while ((st = br.readLine()) != null) { //bacteroide
				if(st.contains(kw1)) {
					total_bac++;
					//System.out.println(st);
					String href = br.readLine();
					href = href.split("href=")[1];
					href = href.split("target")[0];
					oracle = new URL(href.replace("\"", ""));
					in = new BufferedReader(new InputStreamReader(oracle.openStream()));
					while ((inputline = in.readLine()) != null) {
						if(inputline.contains("Detailed")) { //detailed file
							inputline =inputline.split("href=")[1];
							inputline =inputline.split(">Detail")[0];
							inputline = "http://phast.wishartlab.com"+ inputline;
							url = new URL(inputline.replace("\"", ""));
							reading = new BufferedReader(new InputStreamReader(url.openStream()));
							while ((lines = reading.readLine()) != null) {
								strings = lines.split("<");
								for(int i=0; i< strings.length; i++) {
									//check if a promoter type exist
									if(strings[i].contains("promoter")){
										System.out.println("yes!");
									}
									if(strings[i].contains(kw2) || strings[i].contains(kw3)) {
										tail += 1;
										tail_name.add(strings[i].split(">")[1].split("&nbsp")[0]);
										for(n=1; n<strings.length-i; n++) {
											if(strings[i+n].contains("seq=")) {
												temp = strings[i+n].split("seq=")[1];
												seq = temp.split("&amp;");
												g=seq[1].split(" rel")[0]+" "+seq[0];
												g= g.replace("\"", "").replace(">", "").replace("Click","").replace("'", "");
												sequence_info.add(g.split("rec=")[1].split(" ")[0]);
												sequence.add(g.split("rec=")[1].split(" ")[1]);
												break;
											}
										}
									}
								}

							} 


						}else if(inputline.contains("Summary result file")) { //Summary result file

							addr = inputline.split("href=")[1];
							addr = addr.split(">")[0];
							addr = "http://phast.wishartlab.com"+addr;
							url = new URL(addr.replace("\"", ""));
							reading = new BufferedReader(new InputStreamReader(url.openStream()));

							while ((lines = reading.readLine()) != null) {
								if(!lines.contains("Total")&&lines.contains("phage")) {

									for(int f=0; f<lines.split(">").length;f++) {
										if(lines.split(">")[f].contains("phage") && !lines.split(">")[f].contains("prediction")&&!lines.split(">")[f].contains("region")) {
											poss++;
											possible_phage.add(lines.split(">")[f].split("&nb")[0]);
										}
									}
									break;
								}

							}
						}

					}
				}
				while(tail!=0) {
					bac_name.add(st.split("<td>")[1].split("</td>")[0].replace(",", ""));
					tail -= 1;
				}
				while(poss!=0) {
					bacs.add(st.split("<td>")[1].split("</td>")[0].replace(",", ""));
					poss -= 1;
				}
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// calculate repeats for detailed file
		List<List<Integer>> c = new ArrayList<List<Integer>>();
		List<Integer> ri= new ArrayList<Integer>();
		for(int d=0; d<possible_phage.size(); d++) {
			c.add(d, new ArrayList<Integer>());// initialize inner arrayList
			if(!ri.contains(d)) {
				for(int e=possible_phage.size()-1; e>d; e--) {
					if(possible_phage.get(d).toString().split(":")[0].equals(possible_phage.get(e).toString().split(":")[0])) { // find repeat
						c.get(d).add(e); //add repeat index
						ri.add(e);
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
		System.out.println(bac_name.size());
		System.out.println(total_bac);
		System.out.println("The ratio of bacteroides with tail fibers/ total bacteroides is "+ (double) bac_name.size()/total_bac);

		// write as csv file
		try {
			PrintWriter writer = new PrintWriter(new File("detailed.csv"));
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
					for(int k=0; k< tail_name.get(j).toString().split(":").length-1; k++) {
						sb.append(tail_name.get(j).toString().split(":")[k]);
					}
					int rest = tail_name.get(j).toString().split(":").length-1;
					sb.append(',');
					sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[0]);
					sb.append(',');
					sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[1]);
					sb.append(',');
					sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[2]);
					sb.append(',');
					sb.append("1");
					sb.append(',');
					sb.append(bac_name.get(j).toString());
					sb.append(',');
					sb.append(sequence_info.get(j).toString());
					sb.append(',');
					sb.append(sequence.get(j).toString());
					sb.append('\n');

				}else {
					if(!repeat_index.contains(j)) {
						for(int k=0; k<tail_name.get(j).toString().split(":").length-1; k++) {
							sb.append(tail_name.get(j).toString().split(":")[k]);
						}
						int rest=tail_name.get(j).toString().split(":").length-1;
						sb.append(',');
						sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[0]);
						sb.append(',');
						sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[1]);
						sb.append(',');
						sb.append(tail_name.get(j).toString().split(":")[rest].split(";")[2]);
						sb.append(',');
						sb.append(Integer.toString(count.get(j).size()+1));
						sb.append(',');
						sb.append(bac_name.get(j).toString());
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
								sb.append(bac_name.get(count.get(j).get(z)).toString());
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
			PrintWriter writer = new PrintWriter(new File("summary.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("phage name");
			sb.append(',');
			sb.append("count");
			sb.append(',');
			sb.append("Bacteroide name");
			sb.append('\n');
			for(int j=0; j<sequence.size();j++) {
				if(!ri.contains(j) && c.get(j).size() == 0) {
					sb.append(possible_phage.get(j).toString());
					sb.append(',');
					sb.append("1");
					sb.append(',');
					sb.append(bacs.get(j).toString());
					sb.append('\n');

				}else {
					if(!ri.contains(j)) {
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

		// write fasta file 
		try {
			File out = new File("seq.fasta");
			FASTAFileWriter writer = new FASTAFileWriter(out);
			for(int l=0; l<tail_name.size(); l++) {
				String header = "";
				for(int k=0; k< tail_name.get(l).toString().split(":").length-1; k++) {
					header += tail_name.get(l).toString().split(":")[k];
				}
				int rest = tail_name.get(l).toString().split(":").length-1;
				header += " ";
				header += tail_name.get(l).toString().split(":")[rest].split(";")[0];
				header += " ";
				header += tail_name.get(l).toString().split(":")[rest].split(";")[1];
				header += " ";
				header += tail_name.get(l).toString().split(":")[rest].split(";")[2];
				header += " - ";
				header += bac_name.get(l).toString();
				writer.write(new FASTAElementImpl(header, sequence.get(l).toString()));
				/*
					int w=0;
					for(int i=0; i< sequence.get(l).toString().length(); i++) {
						if((i+1) % 80 == 0 || sequence.get(l).toString().length()-i < 80) {
							writer.write(sequence.get(l).toString().substring(w*80, i));
							w++;
						}
					}*/
				//FASTAElement e = new FASTAElementImpl(header, bac_name.get(l).toString(), sequence.get(l).toString());
				//writer.write(e);

			}
			writer.close();


		}
		catch(IOException e) {

		}



	}


}
