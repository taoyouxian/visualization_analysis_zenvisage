package edu.uiuc.zenvisage.service.nlp;

import java.util.List;

public class Tuple {
	int start_idx,end_idx;
	
	/*Creates a tuple*/
	public Tuple(int start_idx , int end_idx){
		this.start_idx= start_idx;
		this.end_idx = end_idx;
	}
	
	/*Prints a tuple*/
	public void print(){
		System.out.println("Tuple is ( "+start_idx+" , " +end_idx+ " )");
	}
	
	/*Switches from old format to indexes from the segment*/
	public static Tuple[] toRealIndexes(Tuple[] tuples , List<Segment> s1){
		Tuple[] result = new Tuple[tuples.length];
		List<Integer> realIndexes = SdlMain.getIndexes(s1);
		for(int i = 0 ; i < tuples.length ; i++){
			result[i] = new Tuple(realIndexes.get(tuples[i].start_idx),realIndexes.get(tuples[i].end_idx));
		}
		return result;
	}
}
		