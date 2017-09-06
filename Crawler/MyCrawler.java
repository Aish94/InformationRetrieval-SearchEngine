import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	
	/*Limit your crawler so it only visits HTML, doc, pdf and different image format URLs and record the
	meta data for those file types*/
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|jsp|pl|php|py|rb|json|cgi|dll|rss"
		+ "mp2|mp4|mp3|swf|asp|zip|gz|mid|wav|avi|mov|ram|m4v|rm|smil|wmv|wma|rar|xml))$");
		 
	ExcelClass excel;
	 	 
	@Override
	public void onStart()
	{
		excel = (ExcelClass) myController.getCustomData();
	}
		 
		    
	/**
		 * This method receives two parameters. The first parameter is the page
		 * in which we have discovered this new url and the second parameter is
		 * the new url. You should implement this function to specify whether
		 * the given url should be crawled or not (based on your crawling logic).
		 * In this example, we are instructing the crawler to ignore urls that
		 * have css, js, git, ... extensions and to only accept urls that start
		 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
		 * referringPage parameter to make the decision.
	*/
		 
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		String href = url.getURL().toLowerCase();
			 
		return !FILTERS.matcher(href).matches() && href.startsWith("http://www.latimes.com/");
	}
		 
	/**
		  * This function is called when a page is fetched and ready
		  * to be processed by your program.
	*/
	@Override
	public void visit(Page page) 
	{
		String url = page.getWebURL().getURL();
		url.replaceAll(",", "-");
		System.out.println("URL: " + url);	//replace , with '-'
			  
		String content_type = page.getContentType().split(";")[0];
		System.out.println("Content Type: " + content_type);
			  
		String size = ""+ page.getContentData().length;
		System.out.println("Document Size in bytes: " + size);
			  
		if (page.getParseData() instanceof HtmlParseData) 
		{
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				  
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			int outlinks_no = 0;
			for(WebURL link : links)
			{
				String outgoing_url = link.getURL();
				outgoing_url.replaceAll(",", "-");
				if(!FILTERS.matcher(outgoing_url).matches())
				{
					outlinks_no++;
					String indicator = "";
					if(outgoing_url.startsWith("http://www.latimes.com/"))
						indicator = "OK";
					else
						indicator = "N_OK";
					try {
						excel.writeToAll(outgoing_url,indicator);
					} catch (IOException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
				  
			String outlinks = "" + outlinks_no;
			System.out.println("Number of outgoing links: " + outlinks);
			System.out.println("******************************************");
				  
			try {
				excel.writeToSuccessful(url, size,""+links.size(),outlinks, content_type);
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
					excel.writeToSuccessful(url, size,"0", "0", content_type);
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}	//For non html types
		}
	}
		  
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) 
	{
		super.handlePageStatusCode(webUrl, statusCode, statusDescription);
		try {
			excel.writeToFetched(webUrl.getURL().replaceAll(",", "-"),""+statusCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
