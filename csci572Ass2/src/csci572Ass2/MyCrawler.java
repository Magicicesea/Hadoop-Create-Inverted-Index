
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package csci572Ass2;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;

// io lib
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MyCrawler extends WebCrawler {
	//fetch storage variable
    private static int no_row = 0;
    private static StringBuilder fetch_result = new StringBuilder();
    //download storage variable
    private static int download_row = 0;
    private static StringBuilder download_result = new StringBuilder();
    //url storage variable
    private static int url_row = 0;
    private static StringBuilder url_result = new StringBuilder();
	
    private static final Logger logger = LoggerFactory.getLogger(MyCrawler.class);
    

    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(gif|jpg|png|tif|jpeg|tiff" +
        "|html|doc|pdf))$");

    CrawlStat myCrawlStat;
    
    private static StringBuilder fetch_NewSite = new StringBuilder();

    public MyCrawler() {
        myCrawlStat = new CrawlStat();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return FILTERS.matcher(href).matches() && href.startsWith("https://www.dailymail.co.uk/");
    }

    
    //successfully download pages from url
    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        myCrawlStat.incProcessedPages();
        StringBuilder tmp = new StringBuilder();
        String url = page.getWebURL().getURL();
        String content_type = page.getContentType();
        //tmp.append(page.getWebURL().getURL()+',');

        //html document format
        if (page.getParseData() instanceof HtmlParseData) {
        	
            HtmlParseData parseData = (HtmlParseData) page.getParseData();
            //ParseData parseData = page.getParseData();
            Set<WebURL> links = page.getParseData().getOutgoingUrls();
            
            for(WebURL link : links) {
            	StringBuilder tmp_link = new StringBuilder();
            	tmp_link.append(link.getURL()+",");
            	if(link.getURL().toLowerCase().startsWith("https://www.dailymail.co.uk/")) {
            		tmp_link.append("OK"+ ",");
            	}else {
            		tmp_link.append("N_OK"+",");
            	}
            	tmp_link.append("\n");
            	urls_Newsite(tmp_link,false);
            	
            }
            int outLinks = links.size();
            myCrawlStat.incTotalLinks(links.size());
            try {
                myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
                //file size in bytes
                int file_size = parseData.getText().getBytes("UTF-8").length;

                tmp.append(url+",");
                tmp.append(file_size+",");
                tmp.append(outLinks+",");
                tmp.append(content_type+",");
                tmp.append("\n");
                visit_Newsite(tmp,false);
                
                //tmp.append(parseData.getText().getBytes("UTF-8").length+',');
            } catch (UnsupportedEncodingException ignored) {
                // Do nothing
            }
        }
        
        //pdf document format
        else if (page.getParseData() instanceof BinaryParseData) {
        	
            BinaryParseData parseData = (BinaryParseData) page.getParseData();
            //ParseData parseData = page.getParseData();
            Set<WebURL> links = page.getParseData().getOutgoingUrls();
            
            for(WebURL link : links) {
            	StringBuilder tmp_link = new StringBuilder();
            	tmp_link.append(link.getURL()+",");
            	if(link.getURL().toLowerCase().startsWith("https://www.dailymail.co.uk/")) {
            		tmp_link.append("OK"+ ",");
            	}else {
            		tmp_link.append("N_OK"+",");
            	}
            	tmp_link.append("\n");
            	urls_Newsite(tmp_link,false);
            	
            }
            int outLinks = links.size();
            myCrawlStat.incTotalLinks(links.size());
            try {
            	int file_size = parseData.toString().getBytes("UTF-8").length;
            	
                myCrawlStat.incTotalTextSize(parseData.toString().getBytes("UTF-8").length);
                //file size in bytes
                //int file_size = parseData.getText().getBytes("UTF-8").length;
                
                

                tmp.append(url+",");
                tmp.append(file_size+",");
                tmp.append(outLinks+",");
                tmp.append(content_type+",");
                tmp.append("\n");
                visit_Newsite(tmp,false);
                
                //tmp.append(parseData.getText().getBytes("UTF-8").length+',');
            } catch (UnsupportedEncodingException ignored) {
                // Do nothing
            }
        }
        
        // We dump this crawler statistics after processing every 50 pages
        if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
            dumpMyData();
        }
    }

    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpMyData();
    }
    
    @Override
    public void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
    	//logger.info(webUrl.toString() + statusCode);
    	StringBuilder tmp = new StringBuilder();
    	tmp.append(webUrl.toString()+',');
    	tmp.append(statusCode+',');
    	tmp.append('\n');
    	fetch_Newsite(tmp,false);
    }
    
    //input should be row which is ready for print
    public static void fetch_Newsite(StringBuilder info, Boolean flag) {
    	
    	no_row ++;
    	if(info != null) {
        	fetch_result.append(info);
    	}

    	if(no_row % 50 == 0 || flag) {
    	    try (PrintWriter writer = new PrintWriter(new FileWriter("/Users/jianfeiwang/desktop/csci572Ass2/fetch_dailynews.csv",true))) {
    	    	
    	        writer.write(fetch_result.toString());
    	        fetch_result = new StringBuilder();
    	        writer.close();

    	      } catch (Exception e) {
    	        System.out.println(e.getMessage());
    	      }
    		
    	}
    }
    
    //input should be row which is ready for print
    public static void visit_Newsite(StringBuilder info, Boolean flag) {
    	
    	download_row ++;
    	if(info != null) {
        	download_result.append(info);
    	}

    	if(no_row % 50 == 0 || flag) {
    	    try (PrintWriter writer = new PrintWriter(new FileWriter("/Users/jianfeiwang/desktop/csci572Ass2/visit_NewsSite.csv",true))) {
    	    	
    	        writer.write(download_result.toString());
    	        download_result = new StringBuilder();
    	        writer.close();

    	      } catch (Exception e) {
    	        System.out.println(e.getMessage());
    	      }
    		
    	}
    }
    
    //input should be row which is ready for print
    public static void urls_Newsite(StringBuilder info, Boolean flag) {
    	
    	url_row ++;
    	if(info != null) {
    		url_result.append(info);
    	}

    	if(url_row % 50 == 0 || flag) {
    	    try (PrintWriter writer = new PrintWriter(new FileWriter("/Users/jianfeiwang/desktop/csci572Ass2/url_NewsSite.csv",true))) {
    	    	
    	        writer.write(url_result.toString());
    	        url_result = new StringBuilder();
    	        writer.close();

    	      } catch (Exception e) {
    	        System.out.println(e.getMessage());
    	      }
    		
    	}
    }
    
    
    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        fetch_Newsite(null,true);
        visit_Newsite(null,true);
        urls_Newsite(null,true);
        
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
        logger.info("Crawler {} > Total Text Size: {}", id, myCrawlStat.getTotalTextSize());
    }
    
}