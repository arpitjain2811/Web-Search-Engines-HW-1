package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Scanner;

class Ranker {
  private Index _index;
  

  public Ranker(String index_source){
    _index = new Index(index_source);
  }

  public Vector < ScoredDocument > runquery(String query){
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    for (int i = 0; i < _index.numDocs(); ++i){
      retrieval_results.add(runquery(query, i));
    }
    return retrieval_results;
  }

  public ScoredDocument runquery(String query, int did){

    // Build query vector
    Scanner s = new Scanner(query);
    Vector < String > qv = new Vector < String > ();
    while (s.hasNext()){
      String term = s.next();
      qv.add(term);
    }

    // Get the document vector. For hw1, you don't have to worry about the
    // details of how index works.
    Document d = _index.getDoc(did);
    Vector < String > dv = d.get_title_vector();

    // Score the document. Here we have provided a very simple ranking model,
    // where a document is scored 1.0 if it gets hit by at least one query term.
    double score = 0.0;
    for (int i = 0; i < dv.size(); ++i){
      for (int j = 0; j < qv.size(); ++j){
        if (dv.get(i).equals(qv.get(j))){
          score = 1.0;
          break;
        }
      }
    }
    s.close();
    return new ScoredDocument(did, d.get_title_string(), score);
  }
  
  public Vector < ScoredDocument > runquery_cosine(String query){
	  
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    for (int i = 0; i < _index.numDocs(); ++i){
      retrieval_results.add(runquery_cosine(query, i));
    }
    return retrieval_results;  
  }

  public ScoredDocument runquery_cosine(String query, int did){

    // Build query vector
    Scanner s = new Scanner(query);
    Vector < String > qv = new Vector < String > ();
    while (s.hasNext()){
      String term = s.next();
      qv.add(term);
    }

    Document d = _index.getDoc(did);

    double score = 0.0;
    
	for (int j = 0; j < qv.size(); ++j){
		score += d.get_term_tfidf(qv.get(j));  
	}
	score=score*((double)1/Math.sqrt(qv.size()));
	s.close();
    return new ScoredDocument(did, d.get_title_string(), score);
  }
	  
	  
  
  public Vector < ScoredDocument > runquery_phrase(String query){
	  
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    for (int i = 0; i < _index.numDocs(); ++i){
      retrieval_results.add(runquery_phrase(query, i));
    }
    return retrieval_results;
  }

  public ScoredDocument runquery_phrase(String query, int did){

    // Build query vector
    Scanner s = new Scanner(query);
    Vector < String > qv = new Vector < String > ();
    while (s.hasNext()){
      String term = s.next();
      qv.add(term);
    }


    Document d = _index.getDoc(did);
    Vector < String > dv = d.get_body_vector();

    double score = 0.0;
    
    
    if(qv.size()==1){

	score = d.get_Doc_Term_Freq(qv.get(0));
	score = score / dv.size();
    }
    else{
	for (int i = 0; i < dv.size()-1; ++i){
	    for (int j = 0; j < qv.size()-1; ++j){
		if ( (dv.get(i).equals(qv.get(j)) && (dv.get(i+1).equals(qv.get(j+1))) )){
		    score += 1.0;
		}
	    }
	}
	score = score / ((qv.size() - 1) * (dv.size() - 1));
    }
    s.close();
    return new ScoredDocument(did, d.get_title_string(), score);
  }
		  
  public Vector < ScoredDocument > runquery_numviews(String query){
	  
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    for (int i = 0; i < _index.numDocs(); ++i){
      retrieval_results.add(runquery_numviews(query, i));
    }
    return retrieval_results;
  }

  public ScoredDocument runquery_numviews(String query, int did){

    // Build query vector
    Scanner s = new Scanner(query);
    Vector < String > qv = new Vector < String > ();
    while (s.hasNext()){
      String term = s.next();
      qv.add(term);
    }

    Document d = _index.getDoc(did);

    double score = (1.0 / 19873.0) * d.get_numviews();
    s.close();

    return new ScoredDocument(did, d.get_title_string(), score);
  }	
			  
public Vector < ScoredDocument > runquery_linear(String query){
	  
	Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
	
	Vector < ScoredDocument > cosine_results = new Vector < ScoredDocument > ();
	Vector < ScoredDocument > ql_results = new Vector < ScoredDocument > ();
	Vector < ScoredDocument > phrase_results = new Vector < ScoredDocument > ();
	Vector < ScoredDocument > numviews_results = new Vector < ScoredDocument > ();
	
	cosine_results=runquery_cosine(query);
	phrase_results=runquery_phrase(query);
	ql_results=runquery_QL(query);
	numviews_results=runquery_numviews(query);
	    
	Double b_c=0.25;
	Double b_p=0.25;
	Double b_n=0.25;
	Double b_ql=0.25;
    
	Double score=0.0;
		
	for (int i = 0; i < _index.numDocs(); ++i){
	    
	    score = b_c*(cosine_results.get(i)._score)+b_p*(phrase_results.get(i)._score)+b_n*(numviews_results.get(i)._score)+b_ql*(ql_results.get(i)._score);
	    
	    retrieval_results.add(new ScoredDocument(i, cosine_results.get(i)._title, score));
	    
	} 
	
	return retrieval_results;
}


  public ScoredDocument runquery_QL(String query, int did){

    // Build query vector
    Scanner s = new Scanner(query);
    Vector < String > qv = new Vector < String > ();
    while (s.hasNext()){
      String term = s.next();
      qv.add(term);
    }

    Document d = _index.getDoc(did);
    Integer Total_Doc = qv.size() + d.get_title_vector().size() + d.get_body_vector().size();
    Integer Total_Corpus = qv.size() + d.termFrequency();
    Double score = 1.0;
    Double alpha = 0.8;

    for (int j = 0; j < qv.size(); ++j){
	Integer dtf = d.get_Doc_Term_Freq(qv.get(j));
	Integer tf = d.termFrequency(qv.get(j));

	score *= (alpha * (1 + dtf)) / Total_Doc  +  ((1 - alpha) * (1 + tf)) / Total_Corpus;  
    }

    s.close();

    return new ScoredDocument(did, d.get_title_string(), score);
  }	

  public Vector < ScoredDocument > runquery_QL(String query){
	  
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    for (int i = 0; i < _index.numDocs(); ++i){
      retrieval_results.add(runquery_QL(query, i));
    }
    return retrieval_results;  
  }


    
}
			  
