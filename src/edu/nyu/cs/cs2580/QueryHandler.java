package edu.nyu.cs.cs2580;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;

class QueryHandler implements HttpHandler {
  private static String plainResponse =
      "Request received, but I am not smart enough to echo yet!\n";

  private Ranker _ranker;
  public Integer SessionID=0;
  public static Vector<Clicklog> clicklogs=new Vector<>();

  public QueryHandler(Ranker ranker){
    _ranker = ranker;
  }

  public static Map<String, String> getQueryMap(String query){  
    String[] params = query.split("&");  
    Map<String, String> map = new HashMap<String, String>();  
    for (String param : params){  
      String name = param.split("=")[0];  
      String value = param.split("=")[1];  
      map.put(name, value);  
    }
    return map;  
  } 
  
  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equalsIgnoreCase("GET")){  // GET requests only.
      return;
    }
    
    
    
    // Print the user request header.
    Headers requestHeaders = exchange.getRequestHeaders();
    System.out.print("Incoming request: ");
    for (String key : requestHeaders.keySet()){
      System.out.print(key + ":" + requestHeaders.get(key) + "; ");
    }
    System.out.println();
    String queryResponse = "";  
    String uriQuery = exchange.getRequestURI().getQuery();
    String uriPath = exchange.getRequestURI().getPath();
    Vector < ScoredDocument > sds = null;
    String responseheadertype=null;
    
    if ((uriPath != null) && (uriQuery != null)){
    	
    	
      if (uriPath.equals("/search")){
        Map<String,String> query_map = getQueryMap(uriQuery);
        Set<String> keys = query_map.keySet();
        responseheadertype="text/plain";
        String type=null;
        if(keys.contains("format"))
        {
        	type= query_map.get("format");
        	
        	if(type.equals("html"))
        	{
        		responseheadertype="text/html";
        	}
        	
        }
        
        if(keys.contains("click"))
        {
        	Integer click_did=Integer.parseInt(query_map.get("click"));
        	
        	for(int i=0;i<clicklogs.size();i++)
        	{
        		Clicklog cl=clicklogs.get(i);
        		if(cl._did==click_did)
        		{
        			cl._action="click";
        			Date dc=new Date();
        			
        			cl._timeStamp=dc.toString();
        		}
        		clicklogs.set(i, cl);
        	}
        	

        	
        	
        }
        	
        	
        if (keys.contains("query")){
          if (keys.contains("ranker")){
            String ranker_type = query_map.get("ranker");
            
            if (SessionID >0){
            	FileWriter writer = new FileWriter("../data/output/hw1.4-log.tsv",true);
            	
            	for(int i=0;i<clicklogs.size();i++)
            	{
            		writer.write(clicklogs.get(i).asString());
            		writer.write("\n");
            	}
            	writer.close();
            	clicklogs.clear();
            	
            }
            
            SessionID++;
            String query=query_map.get("query");
            query=query.replace('+', ' ');
            query_map.put("query", query);
            
            
            // @CS2580: Invoke different ranking functions inside your
            // implementation of the Ranker class.
            if (ranker_type.equals("cosine")){
            	
            	 sds = _ranker.runquery_cosine(query_map.get("query"));
            	 

            } else if (ranker_type.equals("QL")){
		
		sds = _ranker.runquery_QL(query_map.get("query"));
              
            } else if (ranker_type.equals("phrase")){
            	
            	sds = _ranker.runquery_phrase(query_map.get("query"));
            	
            }
            	else if (ranker_type.equals("numviews")){
            	
            	sds = _ranker.runquery_numviews(query_map.get("query"));
            	
            }
            
            else if (ranker_type.equals("linear")){
            	
            	sds = _ranker.runquery_linear(query_map.get("query"));
            	
            } else {
              queryResponse = (ranker_type+" not implemented.");
            }
          } else {
            // @CS2580: The following is instructor's simple ranker that does not
            // use the Ranker class.
             sds = _ranker.runquery(query_map.get("query"));
            Iterator < ScoredDocument > itr = sds.iterator();
            while (itr.hasNext()){
              ScoredDocument sd = itr.next();
              if (queryResponse.length() > 0){
                queryResponse = queryResponse + "\n";
              }
              queryResponse = queryResponse + query_map.get("query") + "\t" + sd.asString();
            }
            if (queryResponse.length() > 0){
              queryResponse = queryResponse + "\n";
            }
          }
          
          
      Collections.sort(sds);
      
//	  ScoredDocument maxDoc = Collections.min(sds);
//	  double max_val = 1.0; //maxDoc._score;
//	  System.out.println(max_val);
//	  for (int i = 0; i < sds.size(); ++i){
//	      sds.get(i)._score /= max_val; 
//	  }
          Iterator < ScoredDocument > itr = sds.iterator();
          
          if(type.equals("text"))
          {
        	while (itr.hasNext()){
            ScoredDocument sd = itr.next();
            if (queryResponse.length() > 0){
              queryResponse = queryResponse + "\n";
            }
            
            queryResponse = queryResponse + query_map.get("query") + "\t" + sd.asString();
          }
          if (queryResponse.length() > 0){
            queryResponse = queryResponse + "\n";
          }
          }
          else
          {
       queryResponse="<!DOCTYPE html><html><head><script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script><script type=\"text/javascript\">function logclicks(d){$.get( \"http://localhost:25808/search?click=\"+d.getAttribute(\"data-id\"));}</script><meta content=\"text/html; charset=windows-1252\" http-equiv=\"content-type\"><title>respo</title></head><body>&nbsp; <img style=\"width: 147px; height: 55px;\" src=\"data:image/png;base64,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D2JAeCCJrngkQMsu6hWP2QfewjthxOO4TY/Px4ykwEkRcTMikYrHvjewjWcexIDwQBJ98SiESEzrjsclB9/GWw/cNuDX2vzEfEzvdSZyiBE563iMCh14vSjetx7bHuScBw0eJ9GH8EBy1fI/4DOkQ29vhNSD0NLGAFbHsAuI8ewTAelz47K6d/D6f9xy1q+35YkFuOzXL2A70iA7mkLbLRew2gf3ycmVAQR80Dtbo/q9VxLSAACyqy2sn6Vm5sFSPAfq6IndAVFc6d3xmFTxJj5+MLrPPE5ZB4utRwAZD56B8EwEwmLHsLwiSw8CUof0ufGTw+/g9aW3DPolP3xyAUoSjTMRYbX3+/ztsAR8EM5kCBMelxvJ914GfEaU7fH9H3hzJ8Nacg3U7MIeL6Abl08HvDiv4k18vDzK49HXOui6sR6MBwPCiBgHEmF3AsPxlyDgh95aj6sOv4t1S26J2Mt+tGIBpib6jAO+GPx20ztbobc1QHqORfV7L93t0FvrjTOlfka9lNRsqDnFEM6kE38YDEBva8DkijexdXn0z3n0uQ5Sh97exHhE44iDlJI3yyciIp6BEBERA0JERAwIERExIERERAwIERExIERExIAQEREDQkREDAgREREDQkREDAgRETEgRETEgBAREQNCRETEgBAREQNCREQMCBERMSBERMSAEBERMSBERMSAEBERA0JERAwIERExIERERAwIERExIERExIAQEREDQkREDAgREREDQkREDAgREUWb/wdiK/V9ZcxdOwAAAABJRU5ErkJggg==\"alt=\"\"><div style=\"text-align: left;\"><form enctype=\"multipart/form-data\" method=\"GET\" action=\"http://localhost:25808/search\"name=\"queryform\"><input style=\"font-family: monospace;\"autocomplete=\"on\"size=\"70\"name=\"query\"value=\""+query_map.get("query")+"\"type=\"text\">&nbsp;&nbsp;<select name=\"ranker\" style=\"height:25px; width:100px\"><option value=\"cosine\">Cosine</option><option value=\"QL\">QL</option><option value=\"phrase\">Phrase</option><option value=\"numviews\">Numviews</option><option value=\"linear\">Linear</option></select><select name=\"format\" style=\"height:25px; width:100px\"><option value=\"html\">HTML</option><option value=\"text\">text</option></select><input value=\"Search\" style=\"height:25px; width:100px\" type=\"submit\"><br></form></div>";             
       queryResponse=queryResponse+"<table style=\"width: 100%\" border=\"1\"><tbody>";   
      	while (itr.hasNext()){
            ScoredDocument sd = itr.next();
            
            try
            {
            Date d= new Date();
            Clicklog c=new Clicklog(SessionID, query_map.get("query"),sd._did,"render",d.toString());
            clicklogs.addElement(c);
            }
            catch(Exception e) {
            	System.out.println(e.toString());
            	
            }
            queryResponse = queryResponse +"<tr><td>"+Integer.toString(sd._did)+"<br></td><td>"+"<a id=\""+Integer.toString(sd._did)+"\" data-id=\""+Integer.toString(sd._did)+"\" href=\"#\" onclick=\"logclicks(this);\">"+ sd._title+"</a><br></td><td>"+Double.toString(sd._score)+"<br></td></tr>";
          }
          
          queryResponse=queryResponse+"</tbody></table></body></html>";
        }
          // Construct a simple response.

        }
        
       
      }
    }
    
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", responseheadertype);
    exchange.sendResponseHeaders(200, 0);  // arbitrary number of bytes
    OutputStream responseBody = exchange.getResponseBody();
    responseBody.write(queryResponse.getBytes());
    responseBody.close();

  }
}
