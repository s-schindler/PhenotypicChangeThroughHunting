/*
 * Author: Susanne Schindler
 * Date: 8.3.2016
 * Email: Susanne.Schindler2@web.de
 * Copyright: Susanne Schindler
 */


import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


/**
 * 
 */
public class FemalePopulation {

    private LinkedList<FemaleBighorn> ewes;
    private Random randomGenerator;

    /**
     * constructor
     * 
     */
    public FemalePopulation (long seed) {
	initiateLists();
	initiateRandomGenerator(seed);
    }

    private void initiateLists(){
	ewes = new LinkedList<FemaleBighorn>();
   }

    private void initiateRandomGenerator(Long seed){
	randomGenerator = new Random(seed);
    }

    public void addCohort(int cohortSize){
	for (int i=1; i<=cohortSize; i++)
	    ewes.add(new FemaleBighorn());
    }

    public void applyNaturalSelection(){
	double rand;

	/* depending on natural mortality some ewes die */
	/* iterate through list of ewes */
	for(Iterator<FemaleBighorn> it =ewes.iterator(); it.hasNext();){
	    /* get random number */
	    rand = randomGenerator.nextDouble();
	    /* if rand one is smaller than surv(age) prob then ram survives */
	    FemaleBighorn ewe = it.next();
	    if(rand >= FemaleBighorn.getSurvivalAtAge(ewe.getAge())){
		it.remove();
	    }
	}
    }


    public void ageing(){
	for(FemaleBighorn ewe : ewes)
	    ewe.ageing();
    }

    public int getSize(){
	return ewes.size();
    }

    public LinkedList<FemaleBighorn> getEwes(){return ewes;}

    public void outputStats(){
	int[] ageDistr = getAgeDistribution(ewes);

	StartSimulation.printToFile_fem(FemaleBighorn.getNumberOfAges(),ageDistr);

    }



    private static int[] getAgeDistribution(LinkedList<FemaleBighorn>list){
	int arraySize = FemaleBighorn.getNumberOfAges();
	int[] ageArray = new int[arraySize];
	/* get histogramm */
	for (Iterator<FemaleBighorn> it=list.iterator();it.hasNext();){
	    ageArray[it.next().getAge()-FemaleBighorn.getMinAge()]++;
	}
	return ageArray;
    }



    public void printPopulation(){
	for(Iterator<FemaleBighorn> it = ewes.iterator(); it.hasNext();){
	    FemaleBighorn ewe = it.next();
	    System.out.println(ewe+", hL="+ewe.getAge());
	}
    }

 
    public  void printList(LinkedList<FemaleBighorn> listOfEwes){
	for(Iterator<FemaleBighorn> it = listOfEwes.iterator(); it.hasNext();){
	    FemaleBighorn ewe = it.next();
	    System.out.println(ewe+",\t aged = "+(ewe.getAge()));
	}
    }


 
}

   



