# InformationRetrieval-SearchEngine

An assignment done as a requirment for CSCI 572 - Information Retrieval & Web Search Engines  
This project consists of three components:
1. Crawler
2. Indexer
3. Search Engine

## Crawler  
Used crawler4j to crawl the LATimes news website   
https://github.com/yasserg/crawler4j/releases  

Controller class - specifies all the configurations and starts the crawl  
Excel Class - Stores results in 3 spreadsheets  
MyCrawler - Overrides crawler4j functions to control the pages visited and record the results  

Seed URL: http://www.latimes.com/  
Number of threads: 7  
Crawl Depth: 16

Results:  
fetch_LATimes.csv -> pages it attemps to fecth (URL, status code)  
urls_LATimes.csv -> all the URLs including repeats that were discovered (URL, does it exist inside the website?)  
visit_LATimes.csv -> successfully downloaded pages (URL, size(bytes), # outlinks, content type)  
CrawlReport_LATimes.txt -> Final stats  


## Indexer  
Used the Dataproc service on Google Cloud Platform to create an nverted index of words from a collection of documents  
https://cloud.google.com/dataproc  
These documents were extracted from a subset of the Project Gutenberg corpus  

Mapper: maps words to Document ID  
Reducer: reduces it to word and (Document ID:frequency) index  

## Search Engine  
Built over Solr Search platform to query html documents from the LATimes website  
http://lucene.apache.org/solr/  
solrclient.php provides an interface for the user to enter thier query, specify the ranking method and get results  
Added functionality for spelling correction, autocomplete and snippets of each result  
Two ranking methods:  
1. Default (By Solr)  
2. Page Rank  

ExtractLinks.java uses JSoup to extract outgoing links from each document and creates an edgelist  
pageRank.py uses NetworkX creates a graph from the edge list and calculates the page rank for each document  
ExtractNewWords.java used to extract all words in the documents which is later used by Peter Norvig's spelling correction algorithm  
https://www.phpclasses.org/package/4859-PHP-Suggest-corrected-spelling-text-in-pure-PHP.html  

