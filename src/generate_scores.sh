#!/bin/bash

JUDGEMENTS=../data/qrels.tsv
OUTPUT=../data/output

rm -rf $OUTPUT
mkdir $OUTPUT

for RANKER in "cosine" "QL" "phrase" "numviews" "linear"
do
    for QUERY in "bing" "data mining" "google" "salsa" "web search"
    do

	echo "**** Searching for $QUERY and using $RANKER ranker ****" 
	QUERY="${QUERY// /+}"

	if [ $RANKER == "cosine" ]
	then
	    SUFFIX=vsm.tsv
	elif [ $RANKER == "QL" ]
	then
	    SUFFIX=ql.tsv
	elif [ $RANKER == "phrase" ]
	then
	    SUFFIX=phrase.tsv
	elif [ $RANKER == "numviews" ]
	then
	    SUFFIX=numviews.tsv
	elif [ $RANKER == "linear" ]
	then
	    SUFFIX=linear.tsv
	else
	    exit
	fi
	
	QUESTION=/hw1.1-
	if [ $RANKER == "linear" ]
	then
	    QUESTION=/hw1.2-
	fi
	FILE=$OUTPUT$QUESTION$SUFFIX	
	if [ ! -e $FILE ]
	then
	    echo "Creating file... $FILE"
	    touch $FILE
	fi
	curl " http://linserv1.nyu.edu:25808/search?query=$QUERY&ranker=$RANKER&format=text" >> $FILE


	QUESTION=/hw1.3-
	FILE=$OUTPUT$QUESTION$SUFFIX	
	if [ ! -e $FILE ]
	then
	    echo "Creating file... "$FILE
	    touch $FILE
	fi
	curl " http://linserv1.nyu.edu:25808/search?query=$QUERY&ranker=$RANKER&format=text" | java edu.nyu.cs.cs2580.Evaluator $JUDGEMENTS >> $FILE

    done
done



