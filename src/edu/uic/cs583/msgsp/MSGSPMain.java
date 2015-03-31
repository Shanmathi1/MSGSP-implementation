/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uic.cs583.msgsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
/*
 * Created by: Stuti and Shanmathi
 * Date: Feb 2, 2015
 * Description: Implementation oF MS-GSP
 */

public class MSGSPMain {
	/*	Algorithm MS-GSP(S, MS)                                           // MS stores all MIS values
	1  M <-sort(I, MS);												 // according to MIS(i)’s stored in MS
	2  L <- init-pass(M, S);											// make the first pass over S
	3  F_1 <- {<{l}> | l  L, l.count/n >= MIS(l)}; 				   // n is the size of S
	4  for (k = 2; F k-1 != SDC; k++) do
	5  if k = 2 then
	6  C_k <- level2-candidate-gen-SPM(L)
	7  else C_k <- MScandidate-gen-SPM(F k_1 )
	8  endif
	9  for each data sequence s  S do
	10 for each candidate c  C k do
	11 if c is contained in s then
	12 c.count++
	13 if c’ is contained in s, where c’ is c after an occurrence of
	   c.minMISItem is removed from c then
	14 c.rest.count++										// c.rest: c without c.minMISItem
	15 endfor
	16 endfor
	17 F_k <- {c <- C_k | c.count/n >= MIS(c.minMISItem)}
	18 endfor
	19 return F <- U_k F_k ;*/

        // Each element in MS is a pair of itemID and its corresponding minimum support
        public static HashMap<Integer,Float> MS;  
        /*
         * S is the an array of transactions which is abstracted as TransactionData class.  
         * Each transaction is an array of itemsets which is abstracted as ItemSet class.  
         */
        public ArrayList<TransactionData> S; 
        /*
         * N is the number of transactions in S
         */
        public static int N;
        /*
         * SDC is support distance constraint
         */
        public static double SDC;
        /*
         * SUP stores the support count for each item using a HashMap
         */
        public static HashMap<Integer, Integer> SUP = new HashMap<Integer, Integer>();
        
        
        /*
    	 * Two parameterized constructor to read the input files: data.txt and
    	 * para.txt into static fields
    	 */
        MSGSPMain(String paraFileName, String dataFileName){
                MS=Input.readParameters(paraFileName);
                S=Input.readData(dataFileName);
        }
        
        /*
    	 * This the first method that is executed, it takes the data.txt and
    	 * para.txt as input using System.getProper
    	 */
         public static void main(String[] args) {
            String dataFilePath = System.getProperty("user.dir") + "/src/edu/uic/cs583/msgsp/data2.txt";
            String parameterFilePath = System.getProperty("user.dir") + "/src/edu/uic/cs583/msgsp/para2-2.txt";
                new MSGSPMain(parameterFilePath,dataFilePath).msgsp();             
        }
         
        public void msgsp(){
            
                
                LinkedList<Integer> M=sort(MS); // according to MIS(i)'s stored in MS
           

               
                ArrayList<Integer> L= initPass(M);   // make the first over S
                
                ArrayList<FrequentSeq> F=new ArrayList<FrequentSeq>(); //F1 U F2 U F3....U Fk 
            // In order to make the index here synchronized with that in the book(start from 1), let F0 be a empty FreqSequence  
                F.add(new FrequentSeq()); 
                
                
                F.add(initPrune(L));    //obtain F1 from L
                
                MSCandidateGen gen = new MSCandidateGen(); // Define a new instance of MSCandidateGen class
                Level2CandidateGen gen1 = new Level2CandidateGen();
                FrequentSeq Fk_1;
                for(int k=2; !(Fk_1=F.get(k-1)).sequences.isEmpty(); k++){
                        FrequentSeq Ck;
                        if(k==2)
                                Ck= gen1.level2CandidateGen(L); 
                        else
                                Ck=gen.candidateGen(Fk_1);

                                
                    for(TransactionData s: S)
                        for(TransactionData c: Ck.sequences){
                                if( c.containedIn(s))
                                        c.count++;
                                
                        }
                    
                    FrequentSeq Fk=new FrequentSeq();

                    for(TransactionData c: Ck.sequences) {
                        
                        if(c.count>=MS.get(c.getItems().get(c.minMISItem()))*N){
                           
                                Fk.sequences.add(c);
                                Fk.setCount(c.count);

                        }
                                
                                
                    }

                    F.add(Fk);
                                                 

                }
                F.remove(F.size()-1);
                

                
               
                // Print F
                int k=0;
                while(++k<F.size()){
                        FrequentSeq Fk=F.get(k);
                        System.out.println(k+"-Sequence");
                        System.out.println();
                        for(TransactionData tran: Fk.sequences) {
                            tran.print();
                            //System.out.println(tran.count);
                        }
                        
                        System.out.println();
                        System.out.println("The total number of "+k+"-Sequence = "+Fk.sequences.size());
                        System.out.println();
                }

                      
        }
        
        /*
         * @Func: Make the first pass over S, first scan the database to count the 
         * support for each item and then follow the sorted order in M to create
         * the candidate list L for generating F1
         * @Param: LinkedList M represents the ascending order of all the items according
         * to their MIS values
         * @return: the candidate list L for generating F1   
         */
        public ArrayList<Integer> initPass(LinkedList<Integer> M) {
                ArrayList<Integer> L=new ArrayList<Integer>();
                Iterator<Integer> it = M.iterator();    //get the iterator of MS's key set
                for (Integer i : MS.keySet()) { //initialize SUP, set each item's support count to 0
                        SUP.put(i, new Integer(0));
                }
                
                for (TransactionData tran : S) {
                        N++;
                        HashSet<Integer> items = tran.getItemsAsSet(); 
                        for (Integer id : items) {      
                                Integer count = SUP.get(id);
                                SUP.put(id, new Integer(count.intValue() + 1));
                        }
                }
                Integer minId = null;   //used to store the id of the first item who meets its MIS
                while (it.hasNext()) {  //find the first item who meets its MIS
                        Integer itemId = it.next();
                        if (SUP.get(itemId)*1.0/N >= MS.get(itemId).floatValue()) {
                                minId = itemId;
                                L.add(itemId);
                                break;
                        }
                }
                while (it.hasNext()) {  //find in the following items who meets item minId's MIS
                        Integer itemId = it.next();
                        if (SUP.get(itemId)*1.0/N >= MS.get(minId).floatValue()) {
                                L.add(itemId);
                        }
                }
                return L;
        }

        /*
         * @Func: Prune L get from function init-pass to get Frequent 1-sequences
         * @Param: L, the set of items' id obtained from init-pass
         * @return: Frequent 1-sequences
         */
        public FrequentSeq initPrune(ArrayList<Integer> L) {
                FrequentSeq F1 = new FrequentSeq();
                for (Integer itemId : L) {      //iterate all the items in L to find those who meets their own MIS
                        if (SUP.get(itemId)*1.0/N >= MS.get(itemId).floatValue()) { 
                           //Create a 1-sequence, and add it to F1
                                ItemSet is = new ItemSet();
                                is.items.add(itemId);
                                TransactionData tran = new TransactionData();
                                tran.itemSets.add(is);
                                tran.setCount(SUP.get(itemId));
                                
                                F1.addTransaction(tran);
                              
                        }
                }

                return F1;
                 
        }
        
        /*
         * @Func: Sort the items in ascending order according to their MIS values stored in MS
         * @Param: HashMap MS contains all pairs of itemID and its corresponding minimum support
         * @return: An ordered list of itemIDS.
         */
        public LinkedList<Integer> sort(HashMap<Integer,Float> MS){
                LinkedList<Integer> M=new LinkedList<Integer>();
                for(Integer itemID: MS.keySet()){
                        if(M.size()==0)
                                M.add(itemID);
                        else{
                                int i=0;
                                while(i<M.size()&&MS.get(M.get(i))<MS.get(itemID))
                                        i++;
                                
                                M.add(i,itemID);
                        }
            }
                              
                return M;
        }
        
        //public void sort(HashMap)
        
       

}