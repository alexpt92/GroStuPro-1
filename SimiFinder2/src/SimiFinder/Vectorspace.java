package SimiFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

class SortStream implements Comparable<SortStream>{
	String Stream;
	double Simi;
	public String getString(){
		return this.Stream;
	}
 public double getval(){
	 return this.Simi;
 }
	SortStream(double val, String str) {
		this.Simi=val;
		this.Stream = str;
}
	@Override
	public int compareTo(SortStream o) {
		// TODO Auto-generated method stub
		return new Double(o.Simi).compareTo(Simi);
	}


	}
	


class vectorspace {
	ArrayList<String> Vokabular = new ArrayList<String>();
	ArrayList<String> GVorkommen = new ArrayList<String>();
	ArrayList<Double> Simis = new ArrayList<Double>();
	Map<String, Integer> GesamtVorkommen = new HashMap<String, Integer>(); // Key ist der Begriff, Value das gesamte Vorkommen
	Map<String, Counter> Vocabulary = new HashMap<String, Counter>();
	Map<String, Integer> MaxVorkommen = new HashMap<String, Integer>();
	Map<String, Map<Term, Double>> AllDocVec = new HashMap<String, Map<Term, Double>>();
	Map<String, Map<Term, Double>> AllQVec = new HashMap<String, Map<Term, Double>>();
	Map<String, Map<String, Double>> SimiMap = new HashMap<String, Map<String, Double>>();

	
	public double Simiwert(Map<Term, Double> Q, Map<Term, Double> D){
		
		double Simi=0;
		for (Iterator<Entry<Term, Double>> it = Q
				.entrySet().iterator(); it.hasNext();){
			Map.Entry<Term, Double> entry = it.next();
		if(!D.containsKey(entry.getKey())){}else{Simi=Simi+(D.get(entry.getKey())*Q.get(entry.getKey()));}
		}
		
		return Simi/10;
	}
	
	
	void getGesamtVorkommen(){
		try{
			
			String line;
			BufferedReader br = new BufferedReader(new FileReader("/home/alex/workspace/gsp/STUPROAKTUELL/gesamtvorkommen.txt"));
			
			while ((line = br.readLine()) != null){
				if (!line.equals(" "))GVorkommen.add(line);
			}
			br.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		for (int j=0;j<Vokabular.size();j++){
			int y = Integer.parseInt(GVorkommen.get(j));
			GesamtVorkommen.put(Vokabular.get(j), y);	
			
				

	}}
	void AnfrageVR(Map<String, Map<String, LinkedTerm>> localMap,  Map<String, Integer> globalTermCounter, Map<String,Integer> MaxVorkommenMap){

		
		MaxVorkommen=MaxVorkommenMap;
		vocabular();
		System.out.println("Vokabular gelesen");
		getGesamtVorkommen();
		System.out.println("GesamtVorkommen gelesen");
		System.out.println("maximale Haeufigkeiten berechnet");
		System.out.println(Vokabular.size());
		System.out.println("Berechne Vektoren...");
		SetAllVector(localMap.size(), localMap, MaxVorkommenMap);
		System.out.println("Simisberechnung");
		Map<String, Map<String, LinkedTerm>> localtmp= new HashMap<String, Map<String, LinkedTerm>>();
		localtmp=localMap; 
		for (Iterator<Entry<String, Map<String, LinkedTerm>>> it = localMap
				.entrySet().iterator(); it.hasNext();){
			Map.Entry<String, Map<String, LinkedTerm>> entry = it.next();
			Map<String, Double> SimiDocMap = new HashMap<String, Double>();
			for (Iterator<Entry<String, Map<String, LinkedTerm>>> it2 = localtmp
					.entrySet().iterator(); it2.hasNext();){
				Map.Entry<String, Map<String, LinkedTerm>> entry2 = it2.next();
				double W = Simiwert(AllQVec.get(entry.getKey()), AllDocVec.get(entry2.getKey()));
				if(W>0.1){
					SimiDocMap.put(entry2.getKey(), W);}
			}SimiMap.put(entry.getKey(), SimiDocMap);}
		  try { 
			  PrintStream ps;
			  ps = new PrintStream(new File("Simi.txt"));
			  ps.println("<out>");
		  for (Entry<String, Map<String, Double>> entry3 : SimiMap.entrySet()){
			  ps.println("<stream key=\"" + entry3.getKey() + "\">");
		//	  ps.println("<<<<<<<<< " + entry3.getKey() + " >>>>>>>>>");
				ArrayList<SortStream> Sorted = new ArrayList<SortStream>();
		  for(Entry<String, Double> entry4 : entry3.getValue().entrySet()){
			  SortStream tmpStream = new SortStream(entry4.getValue(),entry4.getKey());
				if(!tmpStream.getString().equals(entry3.getKey())){
					if (tmpStream.getval()>0.0){Sorted.add(tmpStream);}
				}
		  }				Collections.sort(Sorted);
		  int i=1;
		  	  for (SortStream s: Sorted){ps.println("<entry name=\"" + s.getString() + "\" ratio=\""  + s.getval() + "\"\\>");i++; if (i>10){break;}}
			  ps.println("</stream>"); 
			  ps.println("");
			}ps.println("</out>"); ps.close();}catch (FileNotFoundException e) {
			e.printStackTrace();
		}			
		System.out.println("Simi printed.");
	}
	
	
	
	public int maxHaeufigkeit(Map<String, Map<String, LinkedTerm>> localMap, String stream){
		int max= 0;

	for(Iterator<Entry<String, LinkedTerm>> it = localMap.get(stream).entrySet().iterator(); it.hasNext();){
		Map.Entry<String, LinkedTerm> entry = it.next();

		int tf=vorkommenImDokument(entry.getKey(), stream, localMap);
		if(tf>max)max=tf;
		}if(!MaxVorkommen.containsKey(stream)){MaxVorkommen.put(stream, max);}		return max;
		
		  }

	
	void maxHaeufigkeitAll(Map<String, Map<String, LinkedTerm>>localMap){
		int i=0;
		  for (Iterator<Entry<String, Map<String, LinkedTerm>>> it = localMap
					.entrySet().iterator(); it.hasNext();){
				Map.Entry<String, Map<String, LinkedTerm>> entry = it.next();
				System.out.println("max Stream " + i);
				i++;
			maxHaeufigkeit(localMap, entry.getKey());	
		  }
		  

		  
	}
	


	
	public double wqk(int tfqk, int tfqi, int N, int nk){
		double erg=(0.5+((0.5*tfqk)/(tfqi)))*Math.log(N/nk);
			return erg;
		}
	
	
	void SetAllVector(int counter, Map<String, Map<String, LinkedTerm>> localMap, Map<String, Integer> MaxVorkommen){
		int N =counter; 
		int maxT=0;
		int j=1;
			for (Iterator<Entry<String, Map<String, LinkedTerm>>> it = localMap
	.entrySet().iterator(); it.hasNext();){
				Map.Entry<String, Map<String, LinkedTerm>> entry = it.next();
				Map<Term, Double> TermMap = new HashMap<Term, Double>();
				Map<Term, Double> QTermMap = new HashMap<Term, Double>();
				j++;
				System.out.println(j);
				maxT=MaxVorkommen.get(entry.getKey());

				for (Iterator<Map.Entry<String, LinkedTerm>> tmpIterator = entry.getValue().entrySet().iterator(); tmpIterator.hasNext();){
						Entry<String, LinkedTerm> entry2 = tmpIterator.next();
						int nk= GesamtVorkommen.get(entry2.getValue().globalTerm.term); //Anzahl der Dokumente die den Begriff enthalten
						int tf=vorkommenImDokument(entry2.getValue().globalTerm.term,entry.getKey(), localMap); //Häufigkeit des Begriffs im Dokument
						TermMap.put(entry2.getValue().globalTerm, matching(entry.getValue(), N, nk, tf ));
						QTermMap.put(entry2.getValue().globalTerm, wqk(tf, maxT, N, nk));}
						AllDocVec.put(entry.getKey(),TermMap);
						AllQVec.put(entry.getKey(), QTermMap);

				}		 		

	
	public double matching(Map<String, LinkedTerm> aktuellerStream, int counter, int nk, int tf){//berechnet die Relevanz (wdk) eines Dokumentes für k
		double Nenner=0;		//Summe von i=1 bis t (tf*log(N/ni))^2
		for (Iterator<Map.Entry<String, LinkedTerm>> tmpIterator = aktuellerStream.entrySet().iterator(); tmpIterator.hasNext();){
			Entry<String, LinkedTerm> entry = tmpIterator.next();
			Nenner=Nenner+Math.pow(tf*Math.log(counter/(double) GesamtVorkommen.get(entry.getKey())),2);

			}
		double wdk = (tf*Math.log(counter/nk))/(Math.sqrt(Nenner));
		return wdk;
}
	
	
	public int vorkommenGesamt(String k, Map<String, Map<String, LinkedTerm>> localMap){ 
		//gibt die Anzahl der Dokumente wieder, die k enthalten
		int v= 0;	
		for (Iterator<Entry<String, Map<String, LinkedTerm>>> it = localMap
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, LinkedTerm>> entry = it.next();
			for (Iterator<Map.Entry<String, LinkedTerm>> tmpIterator = entry.getValue().entrySet().iterator(); tmpIterator.hasNext();){
				Entry<String, LinkedTerm> entry2 = tmpIterator.next();
	if (entry2.getValue().globalTerm.term.equals(k)){

			v++;
				}}}
				return v;
	}
	
	void vorkommenAllGesamt(Map<String, Map<String, LinkedTerm>> localMap)
	{		
	for(int i=0;i<Vokabular.size();i++){			
		int counter = 0;	
		for (Iterator<Entry<String, Map<String, LinkedTerm>>> it = localMap
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, LinkedTerm>> entry = it.next();
			for (Iterator<Map.Entry<String, LinkedTerm>> tmpIterator = entry.getValue().entrySet().iterator(); tmpIterator.hasNext();){
				Entry<String, LinkedTerm> entry2 = tmpIterator.next();				
				if (entry2.getValue().globalTerm.term.equals(Vokabular.get(i))){
					counter++;
					break;
					}	
				}
			GesamtVorkommen.put(Vokabular.get(i), counter);

			}
	}
	try {
		PrintStream ps = new PrintStream(new File("gesamtvorkommen.txt"));
		for (int i=0; i<Vokabular.size();i++ ) {
			ps.println(GesamtVorkommen.get(Vokabular.get(i)));
		}
		System.out.println("gesamtvorkommenFile printed.");
		ps.close();

	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
	public int vorkommenImDokument(String k, String stream, Map<String, Map<String, LinkedTerm>> localMap){ 

			if(localMap.get(stream).containsKey(k)){
				if (localMap.get(stream).get(k).globalTerm.term.equals(k)){
					return localMap.get(stream).get(k).localTerm.counter.getVal();
					}
			}			
			return 0;
	}
	
	
	void vocabular(){		
		try{
			
			String line;
			BufferedReader br = new BufferedReader(new FileReader("/home/alex/workspace/gsp/STUPROAKTUELL/vokabular.txt"));
			
			while ((line = br.readLine()) != null){
				if (!line.equals(" "))Vokabular.add(line);
			}
			br.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
			
	
}
}


class Vocabular{
	
	void Setvocabular(Map<String, Map<String, LinkedTerm>> localMap, Map<String, Term> globalMap){		
//gibt die Anzahl der verschiedenen Begriffe aller Dokumente zurück & erstellt das ListArray Vokabular.
		ArrayList<String> Vokabular = new ArrayList<String>();
		for (Iterator<Entry<String, Map<String, LinkedTerm>>> it = localMap
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Map<String, LinkedTerm>> entry = it.next();
			for (Iterator<Map.Entry<String, LinkedTerm>> it2 = entry.getValue()
					.entrySet().iterator(); it2.hasNext();) {
				Entry<String, LinkedTerm> entry2 = it2.next();
		Vokabular.add(entry2.getValue().globalTerm.term);
		}}
		try {
			PrintStream ps = new PrintStream(new File("vokabular.txt"));
			for (int i=0; i<Vokabular.size();i++ ) {
				ps.println(Vokabular.get(i));
			}
			System.out.println("File printed.");
			ps.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
