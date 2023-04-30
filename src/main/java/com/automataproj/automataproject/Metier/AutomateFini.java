package com.automataproj.automataproject.Metier;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.MutableGraph;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public abstract class AutomateFini {
    protected String idAutomate;

    protected List<Character> alphabet;

    protected List<Etat> etats;

    protected List<Etat> etatsInit;

    protected List<Etat> etatsFinal;
    
    static  int dernierId=0;

    public AutomateFini()
    {
        etats = new ArrayList<>();
        etatsInit = new ArrayList<>();
        etatsFinal = new ArrayList<>();
        alphabet = new ArrayList<>();
    }

    public void setAlphabet(List<Character> alphabet) {
        this.alphabet = alphabet;
    }
    

    public List<Etat> getEtats() {
		return etats;
	}

	public void setEtats(List<Etat> etats) {
		this.etats = etats;
	}

	protected Etat findEtat(String idEtat)
    {
        for (Etat e: etats) {
            if (e.getIdEtat().equals(idEtat))
                return e;
        }
        return null;
    }

    public Renderer getAutomateImage()
    {
        MutableGraph g = graph("test").directed().graphAttr().with(Rank.dir(LEFT_TO_RIGHT)).toMutable();

        for (Etat etat : etats)
        {
            Shape sh = etat.isFinal() ? Shape.DOUBLE_CIRCLE : Shape.CIRCLE;

            if (etat.isInital())
                g.add(node("").with(Shape.NONE).link(node(etat.getIdEtat())));
            g.add(node(etat.getIdEtat()).with(sh));

            etat.getTransitionSortants().forEach((character, etatSortants) -> {
                for (Etat etatSort : etatSortants)
                {
                    g.add(
                        node(etat.getIdEtat())
                        .link(
                            to(node(etatSort.getIdEtat()))
                                    .with(Label.of(character != '\0' ? character.toString() : "ε"))
                        )
                    );
                }
            });
        }

        return Graphviz.fromGraph(g).width(1100).render(Format.PNG);
    }
    public void printAutomate(String filePath) throws IOException {
        if (etats.size() == 0)
        {
            System.err.println("Automate Vide !");
            return;
        }

        getAutomateImage().toFile(new File(filePath));
    }
    
    public List<Etat> etatsFinal(){
    	ArrayList <Etat> finale=new ArrayList <Etat>();
    	for(Etat e : this.etats) {
    		if(e.isFinal())
    			finale.add(e);
    	}
    	return finale;
    }
    
    public List<Etat> etatsInitial()
    {
    	ArrayList <Etat> finale=new ArrayList <Etat>();
    	for(Etat e : this.etats) {
    		if(e.isInital())
    			finale.add(e);
    	}
    	return finale;
    }
    
    public List<Etat> differennce(List <Etat> e, List <Etat> f){
    	ArrayList <Etat> res=new ArrayList <Etat>(e);
    	res.removeAll(f);
    	return res;
    }
    
    public String genererId() {
    	//dernierId = 0;
    	    dernierId++;
    	    return "q" + dernierId;
    }
    public abstract void ajouterEtat(String idEtat, TypeEtat type);
    public abstract void ajouterTransition(String idEtatDepart, Character c, String idEtatArrive);
}
