import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class ExtractLinks {
	
	public static void getMaps(HashMap<String,String> fileUrlMap, HashMap<String,String> urlFileMap)throws FileNotFoundException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/LATimesData/mapLATimesDataFile.csv"));
		String line;
		
		while( (line = br.readLine()) != null )
		{
			String tokens[] = line.split(",");
			fileUrlMap.put(tokens[0].trim(), tokens[1].trim());
			urlFileMap.put(tokens[1].trim(),tokens[0].trim());
		}
	}
	public static void main(String args[]) throws Exception{
		
		//Read file url mapping
		HashMap<String,String> fileUrlMap = new HashMap<String,String>();
		HashMap<String,String> urlFileMap = new HashMap<String,String>();
		getMaps(fileUrlMap, urlFileMap);
		
		String dirPath = "src/LATimesData/LATimesDownloadData/";
		File dir = new File(dirPath);
		Set<String> edges = new HashSet<String>();
		
		for(File file : dir.listFiles())
		{
			Document doc = Jsoup.parse(file,"UTF-8",fileUrlMap.get(file.getName()));
			Elements links = doc.select("a[href]");
			
			for(Element link : links)
			{
				String url = link.attr("abs:href").trim();
				if(urlFileMap.containsKey(url))
				{
					edges.add(file.getName()+ " " + urlFileMap.get(url));
				}
			}
		}
		
		PrintWriter writer = new PrintWriter(new File("edgeList.txt"));
		System.out.println("No. of edges: " + edges.size());
		for(String s: edges)
		{
			writer.println(s);
		}
		
		writer.flush();
		writer.close();
		
		
	}

}
