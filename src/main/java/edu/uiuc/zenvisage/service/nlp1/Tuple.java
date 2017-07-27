package edu.uiuc.zenvisage.service.nlp1;

import java.util.ArrayList;
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
	
	/*Example: {0,1,2,3} => [(0,1),(1,2),(2,3)]*/
	public static Tuple[] toTuple(List<Integer> indexes){
		Tuple[] result = new Tuple[indexes.size()-1];
		for(int i = 0 ; i < result.length ; i++){
			result[i] = new Tuple(indexes.get(i),indexes.get(i+1));
		}
		return result;
	}	
	
	/*Deep copy of list of Tuple*/
	public static ArrayList<Tuple> clone(ArrayList<Tuple> original){
		ArrayList<Tuple> copy = new ArrayList<>();
		for(Tuple tuple : original){
			copy.add(new Tuple(tuple.start_idx,tuple.end_idx));
		}
		return copy;
	}
}
		