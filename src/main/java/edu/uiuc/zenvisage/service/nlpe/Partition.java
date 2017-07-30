package edu.uiuc.zenvisage.service.nlpe;

import java.util.ArrayList;
import java.util.List;

public class Partition {
	int start_idx,end_idx;
	
	/*Creates a partition*/
	public Partition(int start_idx , int end_idx){
		this.start_idx= start_idx;
		this.end_idx = end_idx;
	}
	
	/*Prints a partition*/
	public void print(){
		System.out.println("Partition is ( "+start_idx+" , " +end_idx+ " )");
	}
	
	/*Switches from old format to indexes from the segment*/
	public static Partition[] toRealIndexes(Partition[] partitions , List<Segment> s1){
		Partition[] result = new Partition[partitions.length];
		List<Integer> realIndexes = SdlMain.getIndexes(s1);
		for(int i = 0 ; i < partitions.length ; i++){
			result[i] = new Partition(realIndexes.get(partitions[i].start_idx),realIndexes.get(partitions[i].end_idx));
		}
		return result;
	}
	
	/*Example: {0,1,2,3} => [(0,1),(1,2),(2,3)]*/
	public static Partition[] toPartition(List<Integer> indexes){
		Partition[] result = new Partition[indexes.size()-1];
		for(int i = 0 ; i < result.length ; i++){
			result[i] = new Partition(indexes.get(i),indexes.get(i+1));
		}
		return result;
	}	
	
	/*Deep copy of list of Partition*/
	public static ArrayList<Partition> clone(ArrayList<Partition> original){
		ArrayList<Partition> copy = new ArrayList<>();
		for(Partition partition : original){
			copy.add(new Partition(partition.start_idx,partition.end_idx));
		}
		return copy;
	}
}
		