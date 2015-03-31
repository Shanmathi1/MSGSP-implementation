package edu.uic.cs583.msgsp;

public class MSCandidateGen {

	 public FrequentSeq candidateGen(FrequentSeq F) {
         FrequentSeq C = new FrequentSeq();
         for (TransactionData tr1 : F.sequences){
                 for (TransactionData tr2 : F.sequences) {
                         TransactionData s1 = tr1.copy();
                         TransactionData s2 = tr2.copy();
                         int condition = checkCondition(s1, s2);
                         if (condition != 0) {
                                 C.addFrequentSequence(joinSequences(s1, s2, condition));
                         }
                 }
         }
         return prune(C,F); //F means F(k-1)?
 }
 

 private int checkCondition(TransactionData s1, TransactionData s2) {
         int result = 0;
         int i = partition(s1);
         if(testPair(s1, s2, i))
                 result = i;
         else if(testPair(s1, s2, 3))
                 result = 3;
         return result;
 }
 /*
  * This method partitions the k-1 length frequent sequence F into three
  * subset s1, s2, and s3.
  * s1 is those whose first item has the minimum MIS.
  * s2 is those whose last item has the minimum MIS.
  * s3 is the rest.
  */
 private int partition(TransactionData tran) {
         int result = 0;
                 Integer firstItem = tran.getFirstItem();
                 Integer lastItem = tran.getLastItem();
                 if (tran.isSmallest(firstItem, 0))
                         result = 1;
                 else if (tran.isSmallest(lastItem, 1))
                         result = 2;
         return result;
 }
 
 /*
  * This method finds the corresponding join sequences for s1, s2, and s3.
  * Parameter tran is the transaction in s1, s2, or s3, which will be found 
  * a pair for.
  * Parameter i indicates which of the three, s1, s2, and s3, does tran belong to. 
  */
 private boolean testPair(TransactionData tran, TransactionData tr, int i) {
         boolean result = false;
         switch (i) {
         case 1:
                 if (tran.specialEqualTo(tr, 1, tr.getItems().size()-1) &&
                		 MSGSPMain.MS.get(tran.getFirstItem()).doubleValue() < MSGSPMain.MS.get(tr.getLastItem()).doubleValue() &&
                                 Math.abs(MSGSPMain.SUP.get(tran.getItems().get(1)).intValue() - MSGSPMain.SUP.get(tr.getLastItem()).intValue()) <= MSGSPMain.SDC*MSGSPMain.N)
                         result = true;
                 break;
         case 2:
                 if (tran.specialEqualTo(tr, tran.getItems().size()-2, 0) &&
                		 MSGSPMain.MS.get(tr.getFirstItem()).doubleValue() > MSGSPMain.MS.get(tran.getLastItem()).doubleValue() &&
                                 Math.abs(MSGSPMain.SUP.get(tran.getItems().get(tran.getItems().size()-2)).intValue() - MSGSPMain.SUP.get(tr.getFirstItem()).intValue()) <= MSGSPMain.SDC*MSGSPMain.N)
                         result = true;
                 break;
         case 3:
                 if (tran.specialEqualTo(tr, 0, tr.getItems().size()-1) &&
                                 Math.abs(MSGSPMain.SUP.get(tran.getFirstItem()).intValue() - MSGSPMain.SUP.get(tr.getLastItem()).intValue()) <= MSGSPMain.SDC*MSGSPMain.N)
                         result = true;
                 break;
         }
         return result;
 }
 
 /*
  * This method joins s1, s2, or s3 with their corresponding pairs to 
  * form the k-length frequent sequence candidates.
  * Parameter i indicates for which of the three, s1, s2, and s3, are we
  * going to join their pairs.
  * Parameter fs is s1, s2, or s3.
  */
 private FrequentSeq joinSequences(TransactionData tran, TransactionData tr, int i) {
         FrequentSeq fs = new FrequentSeq();
         TransactionData candidate = new TransactionData();
         switch(i) {
         case 1:
                         TransactionData trans = tran.copy();
                         if (tr.itemSets.get(tr.itemSets.size()-1).items.size() == 1) {
                                 candidate = new TransactionData();
                                 candidate.itemSets.addAll(trans.itemSets);
                                 candidate.itemSets.add(tr.itemSets.get(tr.itemSets.size()-1));
                                 fs.addTransaction(candidate);
                                 if (trans.itemSets.size()==2 && trans.getItems().size()==2 && trans.getLastItem().toString().compareTo(tr.getLastItem().toString()) < 0) {
                                         candidate = new TransactionData();
                                         candidate.itemSets.addAll(trans.copy().itemSets);
                                         candidate.itemSets.get(candidate.itemSets.size()-1).items.add(tr.copy().getLastItem());
                                         fs.addTransaction(candidate);
                                 }
                         }
                         else if (trans.getItems().size() > 2 ||(trans.itemSets.size()==1 && trans.getItems().size()==2 && trans.getLastItem().toString().compareTo(tr.getLastItem().toString()) < 0)) {
                                 candidate = new TransactionData();
                                 candidate.itemSets.addAll(trans.itemSets);
                                 candidate.itemSets.get(candidate.itemSets.size()-1).items.add(tr.getLastItem());
                                 fs.addTransaction(candidate);
                         }
                 
                 break;
         case 2:
                         trans = tran.copy();
                         if (tr.reverse().itemSets.get(tr.reverse().itemSets.size()-1).items.size() == 1) {
                                 candidate = new TransactionData();
                                 candidate.itemSets.addAll(trans.reverse().itemSets);
                                 candidate.itemSets.add(tr.reverse().itemSets.get(tr.reverse().itemSets.size()-1));
                                 fs.addTransaction(candidate.reverse());
                                 if (trans.reverse().itemSets.size()==2 && trans.reverse().getItems().size()==2 && trans.reverse().getLastItem().toString().compareTo(tr.reverse().getLastItem().toString()) < 0) {
                                         candidate = new TransactionData();
                                         candidate.itemSets.addAll(trans.copy().reverse().itemSets);
                                         candidate.itemSets.get(candidate.itemSets.size()-1).items.add(tr.copy().reverse().getLastItem());
                                         fs.addTransaction(candidate.reverse());
                                 }
                         }
                         else if (trans.reverse().getItems().size() > 2 ||(trans.reverse().itemSets.size()==1 && trans.reverse().getItems().size()==2 && trans.reverse().getLastItem().toString().compareTo(tr.reverse().getLastItem().toString()) < 0)) {
                                 candidate = new TransactionData();
                                 candidate.itemSets.addAll(trans.reverse().itemSets);
                                 candidate.itemSets.get(candidate.itemSets.size()-1).items.add(tr.reverse().getLastItem());
                                 fs.addTransaction(candidate.reverse());
                         }
                 break;
         case 3:
                         trans = tran.copy();
                         if (tr.itemSets.get(tr.itemSets.size()-1).items.size() == 1) {
                                 candidate = new TransactionData();
                                 candidate.itemSets.addAll(trans.itemSets);
                                 candidate.itemSets.add(tr.itemSets.get(tr.itemSets.size()-1));
                                 fs.addTransaction(candidate);
                         }
                         else {
                                 candidate = new TransactionData();
                                 candidate.itemSets.addAll(trans.itemSets);
                                 candidate.itemSets.get(candidate.itemSets.size()-1).items.add(tr.getLastItem());
                                 fs.addTransaction(candidate);
                         }
                 
                 break;
         }
         return fs;
 }
 
 /*
  * The prune step in MScandidate-gen-SPM function
  */
 private FrequentSeq prune(FrequentSeq fs, FrequentSeq fk_1) {
         Integer minItem; // Item that has the minimum MIS in a sequence
         FrequentSeq fsPruned=new FrequentSeq(); // Frequent sequence set after prune step
         for(TransactionData t: fs.sequences){
                 minItem=new Integer(t.minMISItem());
                 boolean frequent=true; // indicator of if t is frequent or not
                 for(int i=0;i<t.itemSets.size();i++){ // walk through the itemsets of a sequence
                         if(t.itemSets.get(i).items.contains(minItem)){ // if the itemset contains the item with min MIS
                                 TransactionData copy=t.copy();
                                 for(int k=0;k<t.itemSets.size();k++){
                                         if(!frequent) 
                                                 break;
                                         for(Integer item: t.itemSets.get(k).items){
                                                 if(!(k==i&&item==minItem)){ //except the minItem
                                                         copy.itemSets.get(k).items.remove(item); // generate a k-1 subsequence 
                                                         if(!frequent(copy, fk_1)){//if this subsequence is not frequent, then the sequence is not frequent either.
                                                                 frequent=false;
                                                                 break;
                                                         }
                                                 }
                                         }
                                 }
                                 if(!frequent)// if a sequence is already infrequent the no need to continue check the remaining itemsets
                                         break;
                         }
                 }
                 if(frequent) //if this sequence is frequent, add to the result
                         fsPruned.sequences.add(t);
         }
         return fsPruned;
 }
 
 
 /*
  * This method is used to determine if a k-1 length subsequence is frequent
  */
 private boolean frequent(TransactionData t, FrequentSeq fk_1){
         boolean frequent=false; 
         for(TransactionData freq: fk_1.sequences)
                 if(t.containedIn(freq)){
                         frequent=true; //there is one sequence in F(k-1) includes the subsequence
                         break;
                 }
         return frequent;
 }
}
