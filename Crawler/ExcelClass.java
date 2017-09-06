import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class ExcelClass {
	
	/* 3 CSV Files to store results */
    CSVWriter fetched;
    CSVWriter successful;
    CSVWriter all;
	
	int fetched_rownum;
	int successful_rownum;
	int all_rownum;
	
	ExcelClass()throws Exception
	{
			initFecthed();
			initSuccessful();
			initAll();
	}
	
	/* Methods to Initialize CSV Files */
	public void initFecthed() throws IOException
	{
		fetched_rownum = 1;
		
		fetched = new CSVWriter(new FileWriter("fetch_LATimes.csv"));
		String [] record = "URL,HTTP Status Code".split(",");
	    fetched.writeNext(record);
	}
	
	public void initSuccessful() throws IOException
	{
		successful_rownum = 1;
		
		successful = new CSVWriter(new FileWriter("visit_LATimes.csv"));
		String [] record = "URL,Size,# of outlinks(unfiltered),# of outlinks(filtered),Content Type".split(",");
		successful.writeNext(record);
	}
	
	public void initAll() throws IOException
	{
		all_rownum = 1;
		
		 all = new CSVWriter(new FileWriter("urls_LATimes.csv"));
		 String[] record = "URL,Indicator".split(",");
		 all.writeNext(record);
	}
	
	/* Methods to write to CSV FIles */
	public synchronized void writeToFetched(String URL, String status) throws IOException
	{
		String[] record = {URL,status};
		fetched.writeNext(record);
		fetched_rownum++;
		System.out.println("Fecthed row num : "+fetched_rownum);
	}
	
	public synchronized void writeToSuccessful(String URL, String size,String unfiltered_outlinks,String filtered_outlinks,String content_type) throws IOException
	{
		String[] record = {URL,size,unfiltered_outlinks,filtered_outlinks,content_type};
		successful.writeNext(record);
		successful_rownum++;
		System.out.println("Successful row num : "+successful_rownum);
	}
	
	public synchronized void writeToAll(String URL, String indicator) throws IOException
	{
		String[] record = {URL,indicator};
		all.writeNext(record);
	}
	
	/* Close all CSV Files */
	public void closeFiles() throws Exception
	{
		fetched.close();
		successful.close();
		all.close();
	}
}
