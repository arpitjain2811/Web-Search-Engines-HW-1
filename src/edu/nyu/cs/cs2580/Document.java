package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

// @CS2580: This is a simple implementation that you will be changing
// in homework 2.  For this homework, don't worry about how this is done.
class Document {
  public int _docid;

  private static HashMap < String , Integer > _dictionary = new HashMap < String , Integer >();
  public static Vector < String > _rdictionary = new Vector < String >();
  private static HashMap < Integer , Integer > _df = new HashMap < Integer , Integer >();
  private static HashMap < Integer , Integer > _tf = new HashMap < Integer , Integer >();
  private static int _total_tf = 0;
  
  
  private Vector < Integer > _body;
  private Vector < Integer > _title;
  private HashMap <Integer, Integer> _doc_tf;
  public HashMap<Integer, Double> _doc_tfidf=new HashMap<Integer,Double>();
  private String _titleString;
  private int _numviews;
  
  public static int documentFrequency(String s){
    return _dictionary.containsKey(s) ? _df.get(_dictionary.get(s)) : 0;
  }

  public static int termFrequency(String s){
    return _dictionary.containsKey(s) ? _tf.get(_dictionary.get(s)) : 0;
  }

  public static int termFrequency(){
    return _total_tf;
  }
  
  public Document(int did, String content){
    Scanner s = new Scanner(content).useDelimiter("\t");

    _titleString = s.next();
    _title = new Vector < Integer >();
    _body = new Vector < Integer >();
    _doc_tf = new HashMap < Integer, Integer >();

    readTermVector(_titleString, _title);
    _doc_tf.clear()
    readTermVector(s.next(), _body);
    
    HashSet < Integer > unique_terms = new HashSet < Integer >();
    int old_tf = 0;
    for (int i = 0; i < _title.size(); ++i){
      int idx = _title.get(i);
      unique_terms.add(idx);
      //int old_tf = _tf.get(idx);
      _tf.put(idx, old_tf + 1);
      old_tf = _tf.get(idx);
      _total_tf++;
    }
    old_tf = 0;
    for (int i = 0; i < _body.size(); ++i){
      int idx = _body.get(i);
      unique_terms.add(idx);
      //old_tf = _tf.get(idx);
      _tf.put(idx, old_tf + 1);
      old_tf = _tf.get(idx);
      _total_tf++;
    }
    for (Integer idx : unique_terms){
      if (_df.containsKey(idx)){
        int old_df = _df.get(idx);
        _df.put(idx,old_df + 1);
      }
    }
    _numviews = Integer.parseInt(s.next());
    _docid = did;
  }
  
  public String get_title_string(){
    return _titleString;
  }

  public int get_numviews(){
    return _numviews;
  }

  public Vector < String > get_title_vector(){
    return getTermVector(_title);
  }

  public Vector < String > get_body_vector(){
    return getTermVector(_body);
  }


  public int get_Doc_Term_Freq(String s){
    return (_dictionary.containsKey(s) & _doc_tf.containsKey(_dictionary.get(s)) ) ? _doc_tf.get(_dictionary.get(s)) : 0;
  }
  
  public Double get_term_tfidf(String s){
	  
	  
    return (_dictionary.containsKey(s) & _doc_tfidf.containsKey(_dictionary.get(s)) ) ? _doc_tfidf.get(_dictionary.get(s)) : 0.0;
  }

  public void set_tfidf(int N)
  {
	  Double total=0.0;
	  
	 // Calculate tf*idf 
	 for(Integer key:_doc_tf.keySet())
	 {
		 Integer tf=_doc_tf.get(key);
		 Integer df=_df.get(key);
		 
		 Double idf=(1 + Math.log((double) N/df)/Math.log(2));

		 _doc_tfidf.put(key, idf*tf);
		 total+=idf*tf*tf*idf;
		 
		
	 }
	 
	 //Normalize
	 
	 for(Integer key:_doc_tf.keySet())
	 {
		 _doc_tfidf.put(key, _doc_tfidf.get(key)/Math.sqrt(total));		
	 }

  }
  

  private Vector < String > getTermVector(Vector < Integer > tv){
    Vector < String > retval = new Vector < String >();
    for (int idx : tv){
      retval.add(_rdictionary.get(idx));
    }
    return retval;
  }

  private void readTermVector(String raw,Vector < Integer > tv){
    Scanner s = new Scanner(raw);
    while (s.hasNext()){
      String term = s.next();
      int idx = -1;
      if (_dictionary.containsKey(term)){
        idx = _dictionary.get(term);
      } else {
        idx = _rdictionary.size();
        _rdictionary.add(term);
        _dictionary.put(term, idx);
        _tf.put(idx,0);
        _df.put(idx,0);
      }
      if (_doc_tf.containsKey(idx)) {
        int old_doc_tf = _doc_tf.get(idx);
        _doc_tf.put(idx,old_doc_tf + 1);
      } else {
        _doc_tf.put(idx, 1);
      }
      
     //_doc_tfidf.put(idx,0.0);
      
      
      tv.add(idx);
    }
    return;
  }
}
