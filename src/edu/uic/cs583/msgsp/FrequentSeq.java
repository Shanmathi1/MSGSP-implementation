package edu.uic.cs583.msgsp;
import java.util.HashSet;

/*
 * This is the class used to abstract L, Fk, and Ck in MS-GSP
 * All of them are sets of transactions, so simply define one structure
 * to represent all of them. 
 */
public class FrequentSeq {
        public HashSet<TransactionData> sequences = new HashSet<TransactionData>();
        private Integer count;
        FrequentSeq(){
                sequences=new HashSet<TransactionData>();
                count = 0;
        }
        
        FrequentSeq(HashSet<TransactionData> sequence) {
                sequences.addAll(sequence);
                count = 0;
        }
        
        public void addTransaction(TransactionData tran) {
                sequences.add(tran);
        }
        
        public void addFrequentSequence(FrequentSeq fs) {
                sequences.addAll(fs.sequences);
        }
        
        public void incrementCount(){
		this.count = this.count + 1;
	}
        
        	public Integer getCount(){
		return this.count;
	}
		
	public void setCount(Integer _count){
		this.count = _count;
	}
	
}
