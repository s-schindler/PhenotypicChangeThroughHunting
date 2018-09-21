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
public class MalePopulation {

    private LinkedList<MaleBighorn> rams;
    private Random randomGenerator;

    private static double fractionHunted = 40;
    private static double fractionRuttingMales = 70;
    private static Boolean rutParticipation = Boolean.TRUE;

    /**
     * constructor
     * 
     */
    public MalePopulation (long seed) {
	initiateLists();
	initiateRandomGenerator(seed);
	Simulation.setHuntingPressure(fractionHunted);
    }

    private void initiateLists(){
	rams = new LinkedList<MaleBighorn>();
   }

    private void initiateRandomGenerator(Long seed){
	randomGenerator = new Random(seed);
    }

    static void setHuntingPressure(double pressure){
	fractionHunted = pressure;
	Simulation.setHuntingPressure(pressure);
    }


    static void setRutParticipation(Boolean rut){
    	rutParticipation = rut;
    }

    static Boolean getRutParticipation(){
    	return rutParticipation;
    }

    public void addCohort(int cohortSize){
	for (int i=1; i<=cohortSize; i++)
	    rams.add(new MaleBighorn());
    }

    public void updateLegalRams(){
	/* for every non legal pop-member check legality and copy either to legal or notLegal pop*/
	for(MaleBighorn ram : rams)
	    ram.updateLegality();
    }


    public LinkedList<MaleBighorn> applyNaturalSelection(){
	double rand;

	/* if population is unhunted, we need the list of legal ram that died */
	/* naturally to be able to calculate life-expectancy of legal rams under no-hunting */
	LinkedList<MaleBighorn> deadLegalRams = new LinkedList<MaleBighorn>();

	/* depending on natural mortality some rams die */
	/* iterate through list of rams */
	for(Iterator<MaleBighorn> it =rams.iterator(); it.hasNext();){
	    /* get random number */
	    rand = randomGenerator.nextDouble();
	    /* if rand one is smaller than surv(age) prob then ram survives */
	    MaleBighorn ram = it.next();
	    if(rand >= MaleBighorn.getSurvivalAtAge(ram.getAge())){
		if(ram.getIsLegal())
		    deadLegalRams.add(ram);
		it.remove();
	    }
	}

	double[][] ranksOverLife = getRanksOfDeadRams(deadLegalRams);

	if (ranksOverLife != null)
	    StartSimulation.printToFile_rank(deadLegalRams.size(),MaleBighorn.getNumberOfAges(),ranksOverLife);

	return deadLegalRams;
    }

    public void growHorns(Boolean printOutput){
	MaleBighorn.resetTotalAndAgeSpecificHornGrowth();
	for(MaleBighorn ram : rams)
	    ram.growHorn(printOutput);
	MaleBighorn.setMeanHornGrowth(rams.size(),getAgeDistribution(rams));
    }

    public LinkedList<MaleBighorn> getAndRemoveHuntedRams(){
	/* create list of potentially hunted rams */
	LinkedList<MaleBighorn> huntedRams = new LinkedList<MaleBighorn>();
	/* get legal rams */
	LinkedList<MaleBighorn> legalRams = getLegalRams();
	/* go through list of legal rams and apply mortality through hunting*/
	for (Iterator<MaleBighorn> it =legalRams.iterator();it.hasNext();){
	    MaleBighorn ram = it.next();
	    if(randomGenerator.nextDouble()*100 <= getHuntingPressure()){
		huntedRams.add(ram);
		rams.remove(ram); /* remove ram from population */
	    }
	}
	/* return list of shot rams */
	return huntedRams;
    }

    public void ageing(){
	for(MaleBighorn ram : rams)
	    ram.ageing();
    }

    static double getHuntingPressure(){
	return fractionHunted;
    }

    public int getSize(){
	return rams.size();
    }

    public LinkedList<MaleBighorn> getRams(){return rams;}

    private LinkedList<MaleBighorn> getLegalRams(){
	LinkedList<MaleBighorn> legalRams = new LinkedList<MaleBighorn>();
	for(MaleBighorn ram : rams){
	    if(ram.getIsLegal())
		legalRams.add(ram);
	}
	return legalRams;
    }

    public void outputStats(LinkedList<MaleBighorn> killedRams, int noRuttingMales){
	int[] ageDistr = getAgeDistribution(rams);
	double[] hornLength = getAgeSpecificMeanAndSDHornLength(ageDistr,rams);
	double[] meanGrowthLength = MaleBighorn.getMeanHornGrowthArrayLength();
	double[][] ranksOverLife = getRanksOfDeadRams(killedRams);

	StartSimulation.printToFile_mal(MaleBighorn.getNumberOfAges(),ageDistr,hornLength,meanGrowthLength,noRuttingMales);

	if (ranksOverLife != null)
	    StartSimulation.printToFile_rank(killedRams.size(),MaleBighorn.getNumberOfAges(),ranksOverLife);
    }



    private static int[] getAgeDistribution(LinkedList<MaleBighorn>list){
	int arraySize = MaleBighorn.getNumberOfAges();
	int[] ageArray = new int[arraySize];
	/* get histogramm */
	for (Iterator<MaleBighorn> it=list.iterator();it.hasNext();){
	    ageArray[it.next().getAge()-MaleBighorn.getMinAge()]++;
	}
	return ageArray;
    }

    private static int[] getYearsOfLegalityDistr(LinkedList<MaleBighorn>list){
	int arraySize = MaleBighorn.getNumberOfAges();
	int[] ageArray = new int[arraySize];
	/* get histogramm */
	for (Iterator<MaleBighorn> it=list.iterator();it.hasNext();){
	    MaleBighorn ram = it.next();
	    ageArray[ram.getAge()-ram.getAgeAtLegality()]++;
	}
	return ageArray;
    }

    private static double[] getAgeSpecificMeanHornLength(int[] ageDistr,LinkedList<MaleBighorn>list){
	int arraySize = MaleBighorn.getNumberOfAges();
	double[] sumHornLengths = new double[arraySize];
	double[] meanHornLength = new double[arraySize];

	for(Iterator<MaleBighorn> it=list.iterator();it.hasNext();){
	    MaleBighorn ram = it.next();
	    int ageIndex = ram.getAge()-MaleBighorn.getMinAge();
	    sumHornLengths[ageIndex] = sumHornLengths[ageIndex]+ram.getHornLength();
	}
	for(int a=0;a<arraySize;a++)
	    if(ageDistr[a]>0)
		meanHornLength[a] = sumHornLengths[a]/ageDistr[a];
	return meanHornLength;
    }

    private static double[] getAgeSpecificSDHornLength(double[] meanLengths,int[] ageDistr,LinkedList<MaleBighorn>list){
	int arraySize = MaleBighorn.getNumberOfAges();
	double[] devHornLengths = new double[arraySize];
	double[] SDHornLength = new double[arraySize];
	for(Iterator<MaleBighorn> it=list.iterator();it.hasNext();){
	    MaleBighorn ram = it.next();
	    int ageIndex = ram.getAge()-MaleBighorn.getMinAge();
	    devHornLengths[ageIndex] = devHornLengths[ageIndex]+Math.pow(ram.getHornLength()-meanLengths[ageIndex],2);
	}
	for(int a=0;a<arraySize;a++)
	    if(ageDistr[a]>1)
		SDHornLength[a] = Math.sqrt(devHornLengths[a]/(ageDistr[a]-1));
	return SDHornLength;
    }

    private static double[] getAgeSpecificMeanAndSDHornLength(int[] ageDistr,LinkedList<MaleBighorn>list){
	double[] meanArray = getAgeSpecificMeanHornLength(ageDistr,list);
	double[] SDarray = getAgeSpecificSDHornLength(meanArray,ageDistr,list);
	double[] combinedArray = new double[2*MaleBighorn.getNumberOfAges()];
	System.arraycopy(meanArray,0,combinedArray,0,MaleBighorn.getNumberOfAges());
	System.arraycopy(SDarray,0,combinedArray,MaleBighorn.getNumberOfAges(),MaleBighorn.getNumberOfAges());
	return combinedArray;
    }


    private double[][] getRanksOfDeadRams(LinkedList<MaleBighorn>list){
	if (list.size() > 0){
	    // create return matrix
	    double[][] matrixOfRanks = new double[list.size()][MaleBighorn.getNumberOfAges()];
	    // go through list and record rank over lifetime
	    int count = 0;
	    for(Iterator<MaleBighorn> it = list.iterator(); it.hasNext();){
		// get rank over life
		double[] rankOverLife = it.next().getRankOverLife();
		// record it in matrix
		for (int a = 0; a<MaleBighorn.getNumberOfAges(); a++)
		    matrixOfRanks[count][a] = rankOverLife[a];
		// increase ram count
		count++;
	    }
	    return matrixOfRanks;
	}
	else
	    return null;
    }


    public void printPopulation(){
	for(Iterator<MaleBighorn> it = rams.iterator(); it.hasNext();){
	    MaleBighorn ram = it.next();
	    System.out.println(ram+", hL="+ram.getHornLength()+", rank="+ram.getRelativeMatingRank()+", gF="+ram.getGrowthFactorLength()+", legal="+ram.getIsLegal());
	}
    }

    public void printLegalPopulation(){
	LinkedList<MaleBighorn> legalRams = getLegalRams();
	for(Iterator<MaleBighorn> it = legalRams.iterator(); it.hasNext();){
	    MaleBighorn ram = it.next();
	    System.out.println(ram+",\t aged = "+(ram.getAge())+",\t hL="+ram.getHornLength()+", rank="+ram.getRelativeMatingRank()+", gF="+ram.getGrowthFactorLength()+", legal="+ram.getIsLegal());
	}
    }

    public  void printList(LinkedList<MaleBighorn> listOfRams){
	for(Iterator<MaleBighorn> it = listOfRams.iterator(); it.hasNext();){
	    MaleBighorn ram = it.next();
	    //System.out.println(ram+",\t aged = "+(ram.getAge())+",\t hL="+ram.getHornLength()+", hC="+ram.getBaseCircumference()+", aL="+ram.getAgeAtLegality());
	    System.out.println(ram+",\t aged = "+(ram.getAge())+",\t hL="+ram.getHornLength()+", rank="+ram.getRelativeMatingRank()+", gF="+ram.getGrowthFactorLength());
	}
    }


    public int rankAllRams(int numberFemales){
	/* sort rams to descending horn length */
        Collections.sort(rams,new HornComparison());
	int numberRuttingMales = assignRelativeMatingRanks(rams,numberFemales);
	return numberRuttingMales;
    }

    /**
     * This procedure assigns a relative mating rank (ranging from 
     * 1 (ram with longest horns) over 0 (ram of mean horn length) to  
     * -1 (ram with shortest horns). 
     */
    public int assignRelativeMatingRanks(LinkedList<MaleBighorn> rankedRams,int numberFemales){
        /* get number of ewes that participate in the rut*/
	int numberEwesInRut = numberFemales;
	if (numberFemales > 0) // to ensure that at least one ewe participates 
	    numberEwesInRut = Math.max(1,(int)Math.floor(fractionRuttingMales/100*numberFemales));

	/* get number of rams that participate in the rut*/
	int numberRamsInRut = Math.min(rankedRams.size(),numberEwesInRut);
	/* get number of rams that do not participate in the rut*/
	int numberRamsFeed = rankedRams.size() - numberRamsInRut;
	/* calculate increment, such that ranks are evenly spread between 1 and -1 */
        double rankIncrInRut = 1;
	double rankIncrFeed = 1;
	if (numberRamsInRut > 0) 
	    rankIncrInRut = 1/((double)numberRamsInRut);
	if (numberRamsFeed > 0) 
	    rankIncrFeed = 1/((double)numberRamsFeed);

	/* set highest possible rank*/
	double rank = Double.NEGATIVE_INFINITY;
	if (numberRamsInRut > 0)
	    rank = 1; // start from the top when there is at least one rutting male
	else 
	    rank = 0; // start from 0 when there is no rutting male


	/* go through list and assing each ram its relative rank */
	for(Iterator<MaleBighorn> it = rankedRams.iterator(); it.hasNext();){
	    it.next().setRelativeMatingRank(rank);
	    if (rank > 0)
		rank = rank-rankIncrInRut;
	    else
		rank = rank-rankIncrFeed;
	}

	return numberRamsInRut;
    }


    /**
     * This procedure assigns the default mating rank 0 
     * to all rams. 
     */
    public void clearRanks(){
	/* go through list and assing each ram the default rank */
	for(Iterator<MaleBighorn> it = rams.iterator(); it.hasNext();)
	    it.next().clearRank();
    }

}
 
class HornComparison implements Comparator<MaleBighorn>{
 
    @Override
    public int compare(MaleBighorn ram1, MaleBighorn ram2) {
        if(ram1.getHornLength() < ram2.getHornLength()){
            return 1;
        } else {
            return -1;
        }
    }
}

   



