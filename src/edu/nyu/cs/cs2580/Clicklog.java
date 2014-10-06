package edu.nyu.cs.cs2580;

import java.util.Date;



// @CS2580: this class should not be changed.
class Clicklog implements Comparable<Clicklog> {
  public int _sessionId;
  public String _query;
  public int _did;
  public String _action;
  public String _timeStamp;

  Clicklog(int sID,String q,int did, String action, String d){
	  _sessionId=sID;
	  _query=q;
	  _did = did;
	  _action = action;
	  _timeStamp = d;
    
  }

  String asString(){
    return new String(
    		Integer.toString(_sessionId)+"\t"+_query+"\t"+Integer.toString(_did) + "\t" + _action + "\t" + _timeStamp);
  }

@Override
public int compareTo(Clicklog o) {
	
	// TODO Auto-generated method stub
	return Double.compare(o._sessionId,this._sessionId);
}
}