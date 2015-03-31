package edu.uic.cs583.msgsp;

import java.util.ArrayList;

public class Level2CandidateGen {

	public FrequentSeq level2CandidateGen(ArrayList<Integer> L) {
        FrequentSeq C2 = new FrequentSeq();
        
        //System.out.println("Inside level 2");
        //find the item who meets its MIS, then compare the following items with this item's MIS
        for (int i=0; i < L.size(); i++) {      
                if (MSGSPMain.SUP.get(L.get(i))*1.0/MSGSPMain.N < MSGSPMain.MS.get(L.get(i)).floatValue())
                        continue;
                else {
                        for (int j=i; j < L.size(); j++) {
                                if (MSGSPMain.SUP.get(L.get(j))*1.0/MSGSPMain.N >= MSGSPMain.MS.get(L.get(i)).floatValue()) {
                                        /*
                                         * to add the transaction containing two item a and b into the 
                                         * frequent sequence, we need to add two sequences <{a}, {b}> and 
                                         * <{a, b}>
                                         */
                                        //Whether the order within a sequence has been maintained??
                                        if (Math.abs(MSGSPMain.SUP.get(L.get(i)).intValue() - MSGSPMain.SUP.get(L.get(j)).intValue()) <= MSGSPMain.SDC * MSGSPMain.N)
                                        {
                                                ItemSet is = new ItemSet();
                                                if(L.get(i) <= L.get(j)) {
                                                        is.items.add(L.get(i));
                                                        is.items.add(L.get(j));
                                                }
                                                else {
                                                        is.items.add(L.get(j));
                                                        is.items.add(L.get(i));
                                                }
                                                if (! L.get(i).equals(L.get(j))) {
                                                        TransactionData tran = new TransactionData();
                                                        tran.itemSets.add(is);                                  
                                                        C2.addTransaction(tran);        //tran is <{a, b}>
                                                }
                                                ItemSet is1 = new ItemSet();
                                                is1.items.add(L.get(i));
                                                ItemSet is2 = new ItemSet();
                                                is2.items.add(L.get(j));
                                                TransactionData tran2 = new TransactionData();
                                                tran2.itemSets.add(is1);
                                                tran2.itemSets.add(is2);
                                                C2.addTransaction(tran2);       //tran2 is <{a}, {b}>
                                                ItemSet is3 = new ItemSet();
                                                is3.items.add(L.get(j));
                                                ItemSet is4 = new ItemSet();
                                                is4.items.add(L.get(i));
                                                if (! L.get(i).equals(L.get(j))) {
                                                        TransactionData tran3 = new TransactionData();
                                                        tran3.itemSets.add(is3);
                                                        tran3.itemSets.add(is4);
                                                        C2.addTransaction(tran3);       //tran3 is <{b}, {a}>
                                                }
                                        }
                                }
                        }
                }
        }
        return C2;
}
}
