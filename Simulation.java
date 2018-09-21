/*
 * Author: Susanne Schindler
 * Date: 25.10.2013
 * Email: Susanne.Schindler2@web.de
 * Copyright: Susanne Schindler
 */



import java.util.LinkedList;
import java.util.Random;
import java.util.Iterator;

/**
 * 
 */
public class Simulation {
    private MalePopulation malePopulation;
    private FemalePopulation femalePopulation;
    private LinkedList<MaleBighorn> harvestedPopulation; // collects the hunted individuals of the current hunting season (not all ever harvested rams)
    private LinkedList<MaleBighorn> deadLegalRams; // collects the legal rams that died through natural causes

    private int initialCohortSize;
    private int numberTimeSteps;
    private double cohortDeviation;

    private Random randomGenerator;

    private static Long randomSeed = new Long(2202);
    private static double fractionHunted = 0; //change this parameter in Population.java
    private static final String defaultPopName = "Arbitrary Population";

    private String popName = defaultPopName;


    /**
     * constructor
     * 
     */
    public Simulation() {
	initiateRandomGenerator();
	initiatePopulations();
	initiateLists();
	setParameters();
    }

    private void initiatePopulations(){
	malePopulation = new MalePopulation(randomGenerator.nextLong());
	femalePopulation = new FemalePopulation(randomGenerator.nextLong());
    }

    private void initiateLists(){
	harvestedPopulation = new LinkedList<MaleBighorn>();
	deadLegalRams = new LinkedList<MaleBighorn>();
    }


    public void setPopulationName(String name){
	popName = name;
    }

    static void setHuntingPressure(double pressure){
	fractionHunted = pressure;
    }

    private void setParameters(){
	initialCohortSize = 100;
	//initialCohortSize = 5;
	cohortDeviation = initialCohortSize/10;
	numberTimeSteps = 150;
	//	numberTimeSteps = 50;
    }

    static void setRandomSeed(long seed){
	randomSeed = seed;
   }

    private void initiateRandomGenerator(){
	randomGenerator = new Random(randomSeed);
	MaleBighorn.initateRandomGenerator(randomGenerator.nextLong());
	FemaleBighorn.initateRandomGenerator(randomGenerator.nextLong());
    }

    public void startSimulation(){
	int numberRuttingMales=0;

	System.out.println(" Simulation for "+ popName+" starts.\n Iterating...");
	// no hunting for the first years
	for(int i=1; i<Math.min(MaleBighorn.getNumberOfAges(),numberTimeSteps); i++){
 	    System.out.print("\r "+i);
	    malePopulation.addCohort(getRandomCohortSize());
  	    femalePopulation.addCohort(getRandomCohortSize());
  	    malePopulation.updateLegalRams();
	    if(fractionHunted < 0.01)
		malePopulation.outputStats(deadLegalRams,numberRuttingMales);
	    else
		malePopulation.outputStats(harvestedPopulation,numberRuttingMales);
	    femalePopulation.outputStats();
	    if (malePopulation.getRutParticipation()) 
		numberRuttingMales = malePopulation.rankAllRams(femalePopulation.getSize());
	    deadLegalRams.clear();
	    deadLegalRams = malePopulation.applyNaturalSelection();
	    femalePopulation.applyNaturalSelection();	    
	    malePopulation.growHorns(Boolean.FALSE);
	    malePopulation.clearRanks();
	    malePopulation.ageing();
	    femalePopulation.ageing();
	}
	// now rams are potentially present in all age classes 
	// hunting starts
	for(int i=MaleBighorn.getNumberOfAges(); i<=numberTimeSteps; i++){
	    System.out.print("\r "+i);
	    malePopulation.addCohort(getRandomCohortSize());
	    femalePopulation.addCohort(getRandomCohortSize());
  	    malePopulation.updateLegalRams();
	    if(fractionHunted < 0.01)
		malePopulation.outputStats(deadLegalRams,numberRuttingMales);
	    else
		malePopulation.outputStats(harvestedPopulation,numberRuttingMales);
	    femalePopulation.outputStats();
	    updateHarvestedPopulation();
	    if (malePopulation.getRutParticipation()) 
		numberRuttingMales = malePopulation.rankAllRams(femalePopulation.getSize());
	    deadLegalRams.clear();
	    deadLegalRams = malePopulation.applyNaturalSelection();
	    femalePopulation.applyNaturalSelection();
	    malePopulation.growHorns(Boolean.FALSE);
	    malePopulation.clearRanks();
	    malePopulation.ageing();
	    femalePopulation.ageing();
	}
	System.out.println("\n Simulation ends.");
   }

    private int updateHarvestedPopulation(){
	// empty list
	harvestedPopulation.clear();
	// add newly hunted
	harvestedPopulation.addAll(malePopulation.getAndRemoveHuntedRams());
	return harvestedPopulation.size();
    }

    private int getRandomCohortSize(){
	/* draw number from distribution with mean <initialCohortSize> and SD <cohortDeviation>*/
	return Math.max(0,(int)Math.round(randomGenerator.nextGaussian()*cohortDeviation+initialCohortSize));
    }

    private void printHarvestedPopulation(){
	System.out.println("number hunted = "+harvestedPopulation.size());
	for(Iterator<MaleBighorn> it=harvestedPopulation.iterator();it.hasNext();){
	    MaleBighorn ram = it.next();
	    System.out.println(ram+" aged = "+(ram.getAge())+", hL= "+(ram.getHornLength()));
	}
    }

    private void printOldPopulation(){
	for(Iterator<MaleBighorn> it=harvestedPopulation.iterator();it.hasNext();){
	    MaleBighorn ram = it.next();
	    if(ram.getAge() >= 8)
		System.out.println(ram+"\t aged = "+(ram.getAge())+",\t becameLegalAged= "+(ram.getAgeAtLegality()));
	}
    }
}



