package com.automataproj.automataproject.Metier;

import java.util.List;

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
    public void ajouterEtat(String idEtat, TypeEtat type) {
        Etat etat = new Etat(idEtat, type);

        if (findEtat(idEtat) != null)
        {
            System.err.println("Etat deja existe !");
            return;
        }
        if (etat.isInital() && etatsInit.size() > 0)
        {
            System.err.println("Impossible d'avoir plus qu'une etat initiale pour AFD");
            return;
        }
        etats.add(etat);
        if (etat.isInital())
            etatsInit.add(etat);
        if (etat.isFinal())
            etatsFinal.add(etat);
    }

    @Override
    public void ajouterTransition(String idEtatDepart, Character c, String idEtatArrive)
    {
        Etat etatDep = findEtat(idEtatDepart);
        Etat etatArr = findEtat(idEtatArrive);

        if (etatArr == null || etatDep == null)
        {
            System.err.println("L'un des etat inexsitante !");
            return;
        }
        if (!alphabet.contains(c))
        {
            System.err.println("Character n'appartient pas au alphabet d'AFD");
            return;
        }
        if (etatDep.getNextState(c) != null)
        {
            System.err.println("AFD : Transition deja exist avec le symbole (" + c + ")  !");
            return;
        }
        etatDep.addTransition(c, etatArr);
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
   
}
