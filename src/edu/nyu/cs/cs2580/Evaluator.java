package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.HashMap;
import java.util.Scanner;

class Evaluator {

  public static void main(String[] args) throws IOException {
	 
    HashMap < String , HashMap < Integer , Double > > relevance_judgments =
      new HashMap < String , HashMap < Integer , Double > >();
    
    HashMap < String , HashMap < Integer , Double > > relevance_gain =
    	      new HashMap < String , HashMap < Integer , Double > >();
    
    if (args.length < 1){
      System.out.println("need to provide relevance_judgments");
      return;
    }
    String p = args[0];
    // first read the relevance judgments into the HashMap
    readRelevanceJudgments(p,relevance_judgments,relevance_gain);
    // now evaluate the results from stdin
    evaluateStdin(relevance_judgments,relevance_gain);
    
  }

  public static void readRelevanceJudgments(
    String p,HashMap < String , HashMap < Integer , Double > > relevance_judgments, HashMap<String, HashMap<Integer, Double>> relevance_gain){
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
          double gain=0.0;
          // convert to binary relevance
          if ((grade.equals("Perfect")) ||
            (grade.equals("Excellent")) ||
            (grade.equals("Good"))){
            rel = 1.0;
          }
          
          if(grade.equals("Perfect"))
          {
        	 gain=10.0; 
          }
          
          if(grade.equals("Excellent"))
          {
        	  gain=7.0;
          }
          
          if(grade.equals("Good"))
          {
        	  gain=5.0;
          }
          
          if(grade.equals("Fair"))
          {
        	  gain=1.0;
          }
          
          if(grade.equals("Bad"))
          {
        	  gain=0.0;
          }
          
          
          
          if (relevance_judgments.containsKey(query) == false){
            HashMap < Integer , Double > qr = new HashMap < Integer , Double >();
            HashMap < Integer , Double > qg = new HashMap < Integer , Double >();
            
            relevance_judgments.put(query,qr);
            relevance_gain.put(query, qg);
          }
          HashMap < Integer , Double > qr = relevance_judgments.get(query);
          HashMap < Integer , Double > qg = relevance_gain.get(query);
          qr.put(did,rel);
          qg.put(did, gain);
        }
      } finally {
        reader.close();
      }
    } catch (IOException ioe){
      System.err.println("Oops " + ioe.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
public static void evaluateStdin(
    HashMap < String , HashMap < Integer , Double > > relevance_judgments, HashMap<String, HashMap<Integer, Double>> relevance_gain)
  {
    // only consider one query per call
	  HashMap < Double , Double > P_at =new HashMap < Double , Double >();
	  HashMap < Double , Double > R_at =new HashMap < Double , Double >();
	  HashMap < Double , Double > NDCG =new HashMap < Double , Double >();
	  HashMap < Double , Double > P_recall_points =new HashMap < Double , Double >();
	  String query=null;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      
      String line = null;
      double RR = 0.0;
      double AP= 0.0;
      double N = 1.0;
      double DCG=0.0;
      Double RecRank=null;
      Double total_rel=0.0;
      Boolean onlyonce=true;
      
      
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
      	HashMap < Integer , Double > qg = relevance_gain.get(query);
      	
      	if(onlyonce)
      	{
      	for(Integer i:qr.keySet())
        {
      	  if(qr.get(i)==1.0)
      	  {
      		  total_rel++;
      	  }
      	  
        }
      	onlyonce=false;
      	}
      	
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
      	
      	if(qg.containsKey(did))
      	{
      		DCG += qg.get(did)/(Math.log(N+1)/Math.log(2));
      		
      	}
      	
      	
      	P_at.put(N, RR/N);
      	R_at.put(N, RR/total_rel);
      	NDCG.put(N, DCG);
      	++N;
      	
      	
      }
      
      
      Double P_1,P_5,P_10,R_1,R_5,R_10,NDCG_1,NDCG_5,NDCG_10,DCG_1,DCG_5,DCG_10,IDCG_1,IDCG_5,IDCG_10;
      System.out.println("Evaluations");
      
      
      
    //*************** Precision ****************************************

      System.out.println("Precision");
      
      P_1=P_at.get(1.0);
      P_5=P_at.get(5.0);
      P_10=	P_at.get(10.0);
    		  
      System.out.println(Double.toString(P_1)+"\t"+Double.toString(P_5)+"\t"+Double.toString(P_10));

      //*************** Recall ****************************************
      
      System.out.println("Recall");
      
      R_1=R_at.get(1.0);
      R_5=R_at.get(5.0);
      R_10=R_at.get(10.0);
      
      System.out.println(Double.toString(R_1)+"\t"+Double.toString(R_5)+"\t"+Double.toString(R_10));

      //*************** F score ****************************************
      Double alpha=0.5;
      Double F_1 = 1/(alpha*(1/P_1)+(1-alpha)*(1/R_1));
      Double F_5 = 1/(alpha*(1/P_5)+(1-alpha)*(1/R_5));
      Double F_10 = 1/(alpha*(1/P_10)+(1-alpha)*(1/R_10));
      
      System.out.println("F score");
      System.out.println(F_1+"\t"+F_5+"\t"+F_10);
      
      //*************** Average Precision ****************************************
      
      System.out.println("Average Precision");
      
      AP=AP/RR;
      System.out.println(Double.toString(AP));
      
    //*************** Reciprocal Rank ****************************************
      System.out.println("Reciprocal rank");
      System.out.println(Double.toString(RecRank));
      
      
      //*************** NDGC ****************************************
      HashMap < Integer , Double > qg = relevance_gain.get(query);
      Vector<Double> ideal = new Vector<Double>(qg.size());

      for(Integer i:qg.keySet())
      {
    	  ideal.add(qg.get(i));   	  
      }	
      
      Comparator<Double> comparator = Collections.reverseOrder();
      Collections.sort(ideal,comparator);
      

      double dcg=0.0;
      Vector<Double> IDCG=new Vector<Double>(ideal.size());
      
      for(int i=0;i<ideal.size();i++)
      {
    	  
    	  dcg += ideal.get(i)/(Math.log(i+1+1)/Math.log(2));
    	  IDCG.add(dcg);
  
      }
      
      IDCG_1=IDCG.get(0);
      
      if(IDCG.get(4) != null)
      {
    	  IDCG_5=IDCG.get(4);
      }
      else
      {
    	  IDCG_5=IDCG.lastElement();
      }
      
      if(IDCG.get(9) != null)
      {
    	  IDCG_10=IDCG.get(9);
      }
      else
      {
    	  IDCG_10=IDCG.lastElement();
      }
      

      DCG_1= NDCG.get(1.0);
      DCG_5=NDCG.get(5.0);
      DCG_10=NDCG.get(10.0);
      
      NDCG_1=DCG_1/IDCG_1;
      NDCG_5=DCG_5/IDCG_5;
      NDCG_10=DCG_10/IDCG_10;
      
      System.out.println("NDCG");
      System.out.println(NDCG_1+"\t"+NDCG_5+"\t"+NDCG_10);
      
    //*************** Precision at Recall points ****************************************
      System.out.println("Precision at Recall Points");
      System.out.println("Point"+"\t"+"Precision"+"\t"+"Recall");
      Map<Double , Double> treeMap = new TreeMap<Double , Double>(P_recall_points);
      
      for(Double i:treeMap.keySet())
      {
    	  
    	  System.out.println(i+"\t"+P_at.get(i)+"\t"+R_at.get(i));
      }
      
    } catch (Exception e){
      System.err.println("Error:" + e.getMessage());
    }
  }
}
