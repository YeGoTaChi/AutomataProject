package com.automataproj.automataproject.Metier;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class AFD extends AutomateFini {

    public AFD()
    {

    }

    public static List<Etat> delta(Character c, Etat q)
    {
        return q.getNextState(c);
    }

    public static Etat deltaStar(String word, Etat q)
    {
        List<Etat> stateList;
        while (!word.isEmpty())
        {
            char c = word.charAt(0);
            word = word.substring(1);

            stateList = delta(c, q);
            if (stateList.size() != 1)
            {
                System.err.println("Chemin non deterministe ! (Echec de reconnaisance)");
                return null;
            }
            q = stateList.get(0);
        }
        return q;
    }

    @Override
    public String ajouterEtat(String idEtat, TypeEtat type) {
        Etat etat = new Etat(idEtat, type);

        if (findEtat(idEtat) != null)
            return "Etat deja existe !";
        if (etat.isInital() && etatsInit.size() > 0)
            return "Impossible d'avoir plus qu'une etat initiale pour AFD";
        etats.add(etat);
        if (etat.isInital())
            etatsInit.add(etat);
        if (etat.isFinal())
            etatsFinal.add(etat);
        return null;
    }

    @Override
    public String ajouterTransition(String idEtatDepart, Character c, String idEtatArrive)
    {
        Etat etatDep = findEtat(idEtatDepart);
        Etat etatArr = findEtat(idEtatArrive);

        if (etatArr == null || etatDep == null)
            return "L'un des etat inexsitante !";
        if (!alphabet.contains(c))
            return "Character n'appartient pas au alphabet d'AFD";
        if (etatDep.getNextState(c) != null)
            return "AFD : Transition deja exist avec le symbole (" + c + ")  !";
        return etatDep.addTransition(c, etatArr);
    }

    public boolean reconnaissanceMot(String mot)
    {
        for (char c: mot.toCharArray()) {
            if (!alphabet.contains(c))
            {
                System.err.println("Un character n'existe pas dans l'alphabet !");
                return false;
            }
        }
        return (deltaStar(mot, etatsInit.get(0)).isFinal());
    }

    public AFD minimiser()
    {
	this.dernierId=0;
    	AFD af = new AFD();
    	af.setAlphabet(this.alphabet);
    	//Elimination des Etat Inaccessibles:
    	Stack <Etat> q = new  Stack<Etat>();
    	q.addAll(this.etats);
    	//System.out.println(q.size());
    	for(Etat etat: this.etats)
    	{
    		for(Character symbole: this.alphabet)
    		{
    			List<Etat> suivant = etat.getNextState(symbole);
    			if(suivant!=null&&q.containsAll(suivant)) q.removeAll(suivant);
    		}
    	}
    	List <Etat> etats=this.differennce(this.etats, q);
    	af.setEtats(etats);
    	//divi
    	
    	//division:
    	List <Etat> etatsFinaux = af.etatsFinal();
    	List <Etat> etatNonFinaux = af.differennce(af.etats, etatsFinaux);
    	List <List<Etat>> partition = new ArrayList <List<Etat>>();
    	partition.add(etatNonFinaux);
    	partition.add(etatsFinaux);
    	List <List<Etat>> partition1 = new ArrayList <List<Etat>>();
    	for(Character symbole: af.alphabet)
    	{
    		for(List<Etat> part: partition) {
    			List<Etat> precedent = trouverPartition(partition, part.get(0).getNextState(symbole).get(0));
    			List<Etat> lista = new ArrayList<Etat>();
    			for(Etat etat : part)
    			{
    				if(trouverPartition(partition, etat.getNextState(symbole).get(0))!= precedent&& etat!=null)
    				{
    					lista.add(etat);
    				}
    			}
    			if(!lista.isEmpty())
    				partition1.add(lista);
    			
    		}
    	}
    	for(List<Etat> part:partition1)
    	{
    		for(Etat etat: part)
    		{
    			
    			List<Etat>	partiti = trouverPartition(partition, etat);
    			partiti.remove(etat);
    		}
    		partition.add(part);
    	}
    	//Reassemblage:
    	//Creation des etat:
    	af.etats.clear();
    	for (List<Etat> part : partition) {
            Etat nouvelEtat = new Etat(genererId(),TypeEtat.MID);
            int init=0;
            int fin=0;
            for(Etat etat : part)
            {
            	if(etat.isInital()) {
            		init++;
            	}
            	if(etat.isFinal())
            	{
            		fin++;
            	}
            	
            }
            if(init==1)
            {
            	if(fin>1) nouvelEtat.setType(TypeEtat.INIT_FINAL);
            	else nouvelEtat.setType(TypeEtat.INIT);
            }
            else {
            	if(fin>1) nouvelEtat.setType(TypeEtat.FINAL);
                
            }
            af.etats.add(nouvelEtat);
            System.out.println(init+" "+fin);
            
        }
    	
    	//creation des transition:
    	int i=0;
    	for(Etat etat:af.etats)
    	{
    		for(Character symbole: af.alphabet)
    		{
    			int index = partition.indexOf(trouverPartition(partition, partition.get(i).get(0).getNextState(symbole).get(0)))+1;
    			af.ajouterTransition(etat.getIdEtat(), symbole,"q"+index );
    		}
    		i++;
    	}

    	return af;
    }

    
    private List<Etat> trouverPartition(List <List<Etat>> partitions,Etat etat)
    {
    	for (List<Etat> partition : partitions) {
            if (partition.contains(etat)) {
                return partition;
            }
        }
        return null;
    }
    

    public AFD unionAutomata(AFD M) {
    	AFD prodM = new AFD();
    	prodM.setAlphabet(alphabet);
    	for (Etat e : this.etats)
    	{
    		for (Etat pe : M.etats)
    		{
    			prodM.ajouterEtat(e.getIdEtat() + pe.getIdEtat(), getTypeUnion(e,pe));
    		}
    	}
    	for (Etat etat : this.etats)
    	{
    		for (Etat petat : M.etats)
    		{			 
    			etat.getTransitionSortants().forEach((character, etatSortants) -> {
    				for (Etat petatSortants : petat.getTransitionSortants().get(character))
    					prodM.ajouterTransition(etat.getIdEtat() + petat.getIdEtat(), character, 
    							prodM.findEtat(etatSortants.get(0).getIdEtat() + petatSortants.getIdEtat()).getIdEtat());
    			});
    		}
    		
    	}
    	return prodM;
    }
    
    public AFD intersectAutomata(AFD M) {
    	AFD prodM = new AFD();
    	prodM.setAlphabet(alphabet);
    	for (Etat e : this.etats)
    	{
    		for (Etat pe : M.etats)
    		{
    			prodM.ajouterEtat(e.getIdEtat() + pe.getIdEtat(), getTypeIntersect(e,pe));
    		}
    	}
    	for (Etat etat : this.etats)
    	{
    		for (Etat petat : M.etats)
    		{			 
    			etat.getTransitionSortants().forEach((character, etatSortants) -> {
    				for (Etat petatSortants : petat.getTransitionSortants().get(character))
    					prodM.ajouterTransition(etat.getIdEtat() + petat.getIdEtat(), character, 
    							prodM.findEtat(etatSortants.get(0).getIdEtat() + petatSortants.getIdEtat()).getIdEtat());
    			});
    		}
    		
    	}
    	return prodM;
    }
    
    private TypeEtat getTypeUnion(Etat e1, Etat e2) {
    	
    	if (e1.isInital() && e2.isInital())
    	{
    		if(e1.getType()==TypeEtat.INIT && e2.getType()==TypeEtat.INIT)
    			return TypeEtat.INIT;
    		if(e1.getType()==TypeEtat.INIT_FINAL && e2.getType()==TypeEtat.INIT_FINAL)
    			return TypeEtat.INIT_FINAL;		 
    		return TypeEtat.INIT_FINAL; 
    	}
    	if (e1.isFinal() || e2.isFinal())
    	{
    		if(e1.getType()==TypeEtat.FINAL && e2.getType()==TypeEtat.FINAL)
    			return TypeEtat.FINAL;
    		return TypeEtat.FINAL;
    	} 
    	return TypeEtat.MID;
    }
    
	private TypeEtat getTypeIntersect(Etat e1, Etat e2) {
    	
    	if (e1.isInital() && e2.isInital())
    	{
    		if(e1.getType()==TypeEtat.INIT && e2.getType()==TypeEtat.INIT)
    			return TypeEtat.INIT;
    		if(e1.getType()==TypeEtat.INIT_FINAL && e2.getType()==TypeEtat.INIT_FINAL)
    			return TypeEtat.INIT_FINAL;		 
    		return TypeEtat.INIT_FINAL; 
    	}
    	if (e1.isFinal() || e2.isFinal())
    	{
    		if(e1.getType()==TypeEtat.FINAL && e2.getType()==TypeEtat.FINAL)
    			return TypeEtat.FINAL;
    	} 
    	return TypeEtat.MID;
    }
    
    public AFD ComplementAFD() {   	
    	AFD complementM = new AFD();
    	complementM.setAlphabet(alphabet);
    	for (Etat e : this.etats)
    	{
    		complementM.ajouterEtat(e.getIdEtat(),getTypeComplement(e));
    	}
    	for (Etat e : this.etats)
    	{
    		e.getTransitionSortants().forEach((character, etatSortants) -> {
    			complementM.ajouterTransition(e.getIdEtat(), character, 
    					complementM.findEtat(etatSortants.get(0).getIdEtat()).getIdEtat());
    		});   
    	}
    	return complementM;
    }
 
	 private TypeEtat getTypeComplement(Etat etat) {
		 
		if(!etat.isFinal())
		{
			if(etat.getType()==TypeEtat.INIT)
				return TypeEtat.INIT_FINAL;
			return TypeEtat.FINAL;
		}
		else
		{
			if(etat.getType()==TypeEtat.INIT_FINAL)
				return TypeEtat.INIT;
			return TypeEtat.MID;
		}
	 }
	  
	 public AFND imageMirror() {
		 AFND primeM = new AFND();
		 primeM.setAlphabet(alphabet);
		 for (Etat e : this.etats) {
			 primeM.ajouterEtat(e.getIdEtat(), getTypeMirror(e));
		 }
		 for (Etat e : this.etats)
	    	{
	    		e.getTransitionSortants().forEach((character, etatSortants) -> {
	    			primeM.ajouterTransition(primeM.findEtat(etatSortants.get(0).getIdEtat()).getIdEtat(), character, 
	    					e.getIdEtat());
	    		});   
	    	}
		 return primeM;
	 }
	 
	 private TypeEtat getTypeMirror(Etat e) {
		 if(e.isInital()) {
			 if(e.getType()==TypeEtat.INIT_FINAL)
				 return TypeEtat.INIT_FINAL;
			 return TypeEtat.FINAL;
		 }
		 if(e.isFinal()) {
			 return TypeEtat.INIT;
		 }
		 return TypeEtat.MID;
	 }
   

    /*public int compterMotsAcceptes(AFD afd, int limite, Etat etatInitial) {
        int compteur = 0;
        Stack<Etat> etatsCourants = new Stack<>();
        etatsCourants.push(etatInitial);
        while (!etatsCourants.empty()) {
            Etat etatCourant = etatsCourants.pop();
            if (etatCourant.isFinal()) {
                compteur++;
            }
            if (limite > 0) {
                for (char symbole : afd.getAlphabet()) {
                    Etat etatSuivant = etatCourant.getNextState(symbole).get(0);
                        etatsCourants.push(etatSuivant);
                }
            }
                limite--;
        }
        return compteur;
    }
    */

    public List<String> generateAcceptedWords(int maxLength) {
        List<String> acceptedWords = new ArrayList<>();
        generateAcceptedWordsHelper(this, this.getEtatsInit().get(0), "", maxLength, acceptedWords);
        return acceptedWords;
    }

    private void generateAcceptedWordsHelper(AFD afd, Etat currentState, String currentWord, int maxLength, List<String> acceptedWords) {
        if (reconnaissanceMot(currentWord) && currentWord.length() <= maxLength) {
            acceptedWords.add(currentWord);
        }
        if (currentWord.length() < maxLength) {
            /*for (char c : afd.getAlphabet()) {
                Etat nextState = currentState.getNextState(c).get(0);
                generateAcceptedWordsHelper(afd, nextState, currentWord + c, maxLength, acceptedWords);}
                */
            currentState.getTransitionSortants().forEach((c, etatsSortants) -> {
                generateAcceptedWordsHelper(afd, etatsSortants.get(0) , currentWord + c, maxLength , acceptedWords);
            });

        }



    }
    
    public String brzozowski() {
        // Étape 1 : Inverser les transitions pour obtenir un AFND
        AFND reversed = this.imageMirror();

        // Étape 2 : Déterminiser l'AFND inversé
        AFD determinized = reversed.determiniser_2();

        // Étape 3 : Inverser les transitions de l'AFD déterminisé
        AFND reversedDeterminized = determinized.imageMirror();

        // Étape 4 : Déterminiser à nouveau l'AFND inversé déterminisé
        AFD determinizedReversedDeterminized = reversedDeterminized.determiniser_2();

        // Étape 5 : Construire l'expression régulière correspondante à partir de l'AFD inversé déterminisé
        String regex = determinizedReversedDeterminized.toRegex();
        regex = this.simplifyRegex(regex);

        return regex;
    }

    
    
    public String toRegex() {
        // Tableau pour stocker les expressions régulières entre les états
        String[][] expressions = new String[etats.size()][etats.size()];

        // Initialisation des expressions régulières entre les états
        for (int i = 0; i < etats.size(); i++) {
            for (int j = 0; j < etats.size(); j++) {
                if (i == j) {
                    expressions[i][j] = "ε"; // Expression régulière pour les boucles sur un état
                } else {
                    expressions[i][j] = ""; // Expression régulière vide par défaut
                }
            }
        }

        // Construction des expressions régulières entre les états en utilisant les transitions sortantes
        for (Etat etat : etats) {
            int i = etats.indexOf(etat);
            for (Character symbol : etat.getTransitionSortants().keySet()) {
                List<Etat> destinations = etat.getTransitionSortants().get(symbol);
                for (Etat destination : destinations) {
                    int j = etats.indexOf(destination);
                    String transitionExpression = symbol.toString();
                    if (!expressions[i][j].isEmpty()) {
                        transitionExpression = expressions[i][j] + " + " + transitionExpression; // Concaténation avec l'expression existante
                    }
                    expressions[i][j] = transitionExpression;
                }
            }
        }

        // Construction de l'expression régulière finale en utilisant l'algorithme de Warshall
        for (int k = 0; k < etats.size(); k++) {
            for (int i = 0; i < etats.size(); i++) {
                for (int j = 0; j < etats.size(); j++) {
                    if (!expressions[i][k].isEmpty() && !expressions[k][j].isEmpty()) {
                        String kExpression = "(" + expressions[i][k] + ")(" + expressions[k][k] + ")*(" + expressions[k][j] + ")";
                        if (!expressions[i][j].isEmpty()) {
                            expressions[i][j] = expressions[i][j] + " + " + kExpression; // Concaténation avec l'expression existante
                        } else {
                            expressions[i][j] = kExpression;
                        }
                    }
                }
            }
        }

        // Récupération de l'expression régulière finale entre l'état initial et les états finaux
        int initialIndex = etats.indexOf(etatsInit.get(0));
        String regex = "";
        for (Etat etatFinal : etatsFinal) {
            int finalIndex = etats.indexOf(etatFinal);
            if (!expressions[initialIndex][finalIndex].isEmpty()) {
                if (!regex.isEmpty()) {
                    regex += " + ";
                }
                regex += expressions[initialIndex][finalIndex];
            }
        }

        return regex;
    }
    
    
    public String simplifyRegex(String regex) {
        // Éliminer les boucles vides
        regex = regex.replaceAll("\\(ε\\)\\*", "");

        // Réduire les doublons
        Pattern pattern = Pattern.compile("(\\b\\w+\\b)(?=.*\\b\\1\\b)");
        Matcher matcher = pattern.matcher(regex);
        while (matcher.find()) {
            String duplicate = matcher.group();
            regex = regex.replace(duplicate, matcher.group(1));
        }

        // Utiliser les opérations d'union et de concaténation
        regex = regex.replaceAll("(\\(.*?\\))( \\+ \\1)+", "$1");

        // Réorganiser les termes
        regex = regex.replaceAll("(\\w+)( \\+ \\1)+", "($1)");

        return regex;
    }

}
