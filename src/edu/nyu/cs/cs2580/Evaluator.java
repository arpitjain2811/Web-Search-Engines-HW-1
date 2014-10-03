package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Vector;
import java.util.HashMap;
import java.util.Scanner;

class Evaluator {

  public static void main(String[] args) throws IOException {
	 
    HashMap < String , HashMap < Integer , Double > > relevance_judgments =
      new HashMap < String , HashMap < Integer , Double > >();
    if (args.length < 1){
      System.out.println("need to provide relevance_judgments");
      return;
    }
    String p = args[0];
    // first read the relevance judgments into the HashMap
    readRelevanceJudgments(p,relevance_judgments);
    // now evaluate the results from stdin
    evaluateStdin(relevance_judgments);
    
  }

  public static void readRelevanceJudgments(
    String p,HashMap < String , HashMap < Integer , Double > > relevance_judgments){
    try {
      BufferedReader reader = new BufferedReader(new FileReader(p));
      try {
        String line = null;
        while ((line = reader.readLine()) != null){
          // parse the query,did,relevance line
          Scanner s = new Scanner(line).useDelimiter("\t");
          String query = s.next();
          int did = Integer.parseInt(s.next());
          String grade = s.next();
          double rel = 0.0;
          // convert to binary relevance
          if ((grade.equals("Perfect")) ||
            (grade.equals("Excellent")) ||
            (grade.equals("Good"))){
            rel = 1.0;
          }
          if (relevance_judgments.containsKey(query) == false){
            HashMap < Integer , Double > qr = new HashMap < Integer , Double >();
            relevance_judgments.put(query,qr);
          }
          HashMap < Integer , Double > qr = relevance_judgments.get(query);
          qr.put(did,rel);
        }
      } finally {
        reader.close();
      }
    } catch (IOException ioe){
      System.err.println("Oops " + ioe.getMessage());
    }
  }

  public static void evaluateStdin(
    HashMap < String , HashMap < Integer , Double > > relevance_judgments)
  {
    // only consider one query per call
	  HashMap < Double , Double > num_rel_at =new HashMap < Double , Double >();
	  HashMap < Double , Double > P_recall_points =new HashMap < Double , Double >();
	  String query=null;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      
      String line = null;
      double RR = 0.0;
      double AP= 0.0;
      double N = 1.0;
      Double RecRank=null;
      
      while (((line = reader.readLine()) != null)){
        Scanner s = new Scanner(line).useDelimiter("\t");
        query = s.next();
        
        int did = Integer.parseInt(s.next());
        
      	String title = s.next();
      	double rel = Double.parseDouble(s.next());
      	if (relevance_judgments.containsKey(query) == false){
      	  throw new IOException("query not found");
      	}
      	HashMap < Integer , Double > qr = relevance_judgments.get(query);
      	if (qr.containsKey(did) != false){
      	  RR += qr.get(did);
      	  if(qr.get(did)==1.0)
      	  {
      		  if(RecRank==null)
      		  {
      			  RecRank=1/N;
      		  }
      		  
      	  P_recall_points.put(N, RR/N);
      	  AP+=RR/N;
      	  }
      	}
      	
      	num_rel_at.put(N, RR);
      	++N;
      	
      	
      }
      
      AP=AP/RR;
      
      Double P_1,P_5,P_10,R_1,R_5,R_10;
      
      System.out.println("Evaluations");
      System.out.println("Precision");
      
      P_1=num_rel_at.get(1.0)/1.0;
      P_5=num_rel_at.get(5.0)/5.0;
      P_10=	num_rel_at.get(10.0)/10.0;	  
      
      System.out.println(Double.toString(num_rel_at.get(1.0)/1.0)+"\t"+Double.toString(num_rel_at.get(5.0)/5.0)+"\t"+Double.toString(num_rel_at.get(10.0)/10.0));

      
      HashMap < Integer , Double > qr = relevance_judgments.get(query);
      
      Double total_rel=0.0;
      
      
      
      for(Integer i:qr.keySet())
      {
    	  if(qr.get(i)==1.0)
    	  {
    		  total_rel++;
    	  }
    	  
      }
      
      
      
      System.out.println("Recall");
      System.out.println(Double.toString(num_rel_at.get(1.0)/total_rel)+"\t"+Double.toString(num_rel_at.get(5.0)/total_rel)+"\t"+Double.toString(num_rel_at.get(10.0)/total_rel));

      R_1=num_rel_at.get(1.0)/total_rel;
      R_5=num_rel_at.get(5.0)/total_rel;
      R_10=num_rel_at.get(10.0)/total_rel;
      
      Double alpha=0.5;
      Double F_1 = 1/(alpha*(1/P_1)+(1-alpha)*(1/R_1));
      Double F_5 = 1/(alpha*(1/P_5)+(1-alpha)*(1/R_5));
      Double F_10 = 1/(alpha*(1/P_10)+(1-alpha)*(1/R_10));
      
      System.out.println("F score");
      System.out.println(F_1+"\t"+F_5+"\t"+F_10);
      
      System.out.println("Average Precision");
      System.out.println(Double.toString(AP));
      
      System.out.println("Reciprocal rank");
      System.out.println(Double.toString(RecRank));
      
      for(Double i:P_recall_points.keySet())
      {
    	  
    	  System.out.println(i+"\t"+P_recall_points.get(i));
      }
      
      System.out.println(Double.toString(RR/N));
    } catch (Exception e){
      System.err.println("Error:" + e.getMessage());
    }
  }
}
