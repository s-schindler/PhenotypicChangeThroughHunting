/*
 * Author: Susanne Schindler
 * Date: 8.3.2016
 * Email: Susanne.Schindler2@web.de
 * Copyright: Susanne Schindler
 */

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Arrays;

/**
 * 
 */
public class MaleBighorn {
    private int age;
    private double hornLength;
    private Boolean isLegal;
    private int ageAtLegality;
    private double relativeMatingRank; // 1 for top ram, 0 for average ram, -1 for lowest ram
    private double[] rankOverLife;

    private static double[] ageSpecificSurvival;
    private static double meanInitialHornLength;
    private static double SDInitialHornLength;
    private static double[] meanAgeSpecificAnnuliIncrement;
    private static double[] SDAgeSpecificAnnuliIncrement;
    private static double[] legality_length;
    private static double totalHornGrowthLength_withRut = 0;
    private static double totalHornGrowthLength_withoutRut = 0;
    private static double meanHornGrowthLength_withRut = 0;
    private static double meanHornGrowthLength_withoutRut = 0;
    private static double[] ageSpecificHornGrowthLength_withRut;
    private static double[] ageSpecificHornGrowthLength_withoutRut;
    private static double[] meanAgeSpecificHornGrowthLength_withRut;
    private static double[] meanAgeSpecificHornGrowthLength_withoutRut;

    private static final int minAge = 2;
    private static final int maxAge = 19;
    private static final int numberOfAges = maxAge - minAge;
    private static final double defaultRelativeMatingRank = 0;
    private static final double epsilon = 0.0000001;
    private static final double maxGrowthLengthReduction = 0.25; // in [%]
    //   private static final double minGrowthLengthTranslation = 3; // in [cm]

    private static Random randomGenerator;

    /**
     * constructor
     * 
     */
    public MaleBighorn () {
	setDefaults();
	double rand = randomGenerator.nextGaussian();
	hornLength = getInitialHornLength(rand);
    }

    private void setDefaults(){
	age = minAge;
	isLegal = Boolean.FALSE;
	ageAtLegality =-1;
	rankOverLife = new double[numberOfAges];
	Arrays.fill(rankOverLife,-2);
	setRelativeMatingRank(defaultRelativeMatingRank);
    }

    static void initializeArrays(){
	ageSpecificHornGrowthLength_withRut = new double[numberOfAges];
	ageSpecificHornGrowthLength_withoutRut = new double[numberOfAges];
	meanAgeSpecificHornGrowthLength_withRut = new double[numberOfAges];
	meanAgeSpecificHornGrowthLength_withoutRut = new double[numberOfAges];
    }
    


    private double getInitialHornLength(double rand){
	return Math.max(0,rand*SDInitialHornLength+meanInitialHornLength);
    }

    static void initateRandomGenerator(long seed){
	randomGenerator = new Random(seed);
    }

    static void setSurvivalRates(double[] array){
	ageSpecificSurvival = new double[numberOfAges];
	try{
	    for (int a = 0; a<numberOfAges; a++)
		ageSpecificSurvival[a] = array[a];
	}catch(Exception e){
	    System.out.println("Bighorn:setSurvivalRates:Array dimension error. "+e.getMessage());
	}
    }

    static void setInitialHornLength(double[] array){
	try{
	    meanInitialHornLength = array[0];
	    SDInitialHornLength = array[1];
	}catch(Exception e){
	    System.out.println("Bighorn:setInitialHornLength:Array dimension error. "+e.getMessage());
	}
    }

    static void setGrowthParams_horn(double[] arrayMeans,double[] arraySDs){
	meanAgeSpecificAnnuliIncrement = new double[numberOfAges];
	SDAgeSpecificAnnuliIncrement = new double[numberOfAges];
	try{
	    for (int a = 0; a<numberOfAges; a++){
		meanAgeSpecificAnnuliIncrement[a] = arrayMeans[a];
		SDAgeSpecificAnnuliIncrement[a] = arraySDs[a];
	    }
	}catch(Exception e){
	    System.out.println("Bighorn:setGrowthParams_horn:Array dimension error. "+e.getMessage());
	}

    }

    static void setLegalityParams(double[] paramsL){
	legality_length = new double[2];
	try{
	    for (int a = 0; a<2; a++){
		legality_length[a] = paramsL[a];
	    }
	}catch(Exception e){
	    System.out.println("Bighorn:setLegalityParams:Array dimension error. "+e.getMessage());
	}
    }

    public void setRelativeMatingRank(double newRank){
	if (newRank >=-1 && newRank<=1)
	    relativeMatingRank = newRank;
	else{
	    if (Math.abs(newRank -1) < epsilon) // close to 1, that is small departures over 1 will be tolerated and mapped to 1
		relativeMatingRank = Math.min(1,newRank);

	    else if (Math.abs(newRank+1) < epsilon) // close to -1, that is small departures under -1 will be tolerated and mapped to -1
		relativeMatingRank = Math.max(-1,newRank);
	    else{
		System.out.println("Bighorn:setRelativeMatingRank:Out of bounds: "+newRank);
		relativeMatingRank = defaultRelativeMatingRank;
	    }
	}
	rankOverLife[age-minAge] = relativeMatingRank;
    }


    public void clearRank(){
	relativeMatingRank = defaultRelativeMatingRank;
    }


    static void resetTotalAndAgeSpecificHornGrowth(){
	totalHornGrowthLength_withRut = 0;
	totalHornGrowthLength_withoutRut = 0;
	meanHornGrowthLength_withRut = 0;
	meanHornGrowthLength_withoutRut = 0;
	ageSpecificHornGrowthLength_withRut = new double[numberOfAges];
	ageSpecificHornGrowthLength_withoutRut = new double[numberOfAges];
	meanAgeSpecificHornGrowthLength_withRut = new double[numberOfAges];
	meanAgeSpecificHornGrowthLength_withoutRut = new double[numberOfAges];
    }

    static void setMeanHornGrowth(int normFactor,int[] ageSpecificCounts){
	meanHornGrowthLength_withRut =         totalHornGrowthLength_withRut/normFactor;	    
	meanHornGrowthLength_withoutRut =      totalHornGrowthLength_withoutRut/normFactor;	     
	for(int a=0; a<numberOfAges; a++){
	    if(ageSpecificCounts[a] != 0){
		meanAgeSpecificHornGrowthLength_withRut[a] =        ageSpecificHornGrowthLength_withRut[a]/ageSpecificCounts[a];
		meanAgeSpecificHornGrowthLength_withoutRut[a] =     ageSpecificHornGrowthLength_withoutRut[a]/ageSpecificCounts[a];
	    }
	}
   }

    static double[] getMeanHornGrowthArrayLength(){
	double[] returnArray = new double[2+2*numberOfAges];

	returnArray[0] = meanHornGrowthLength_withoutRut;
	for(int a=1; a<numberOfAges+1; a++)
	    returnArray[a] = meanAgeSpecificHornGrowthLength_withoutRut[a-1];

	returnArray[numberOfAges+1] = meanHornGrowthLength_withRut;
	for(int a=numberOfAges+2; a<2*numberOfAges+2; a++)
	    returnArray[a] = meanAgeSpecificHornGrowthLength_withRut[a-numberOfAges-2];

	return returnArray;
    }

    static private void increaseTotalHornGrowthLength_withRut(double incr){
	totalHornGrowthLength_withRut = totalHornGrowthLength_withRut +incr;
    }

    static private void increaseTotalHornGrowthLength_withoutRut(double incr){
	totalHornGrowthLength_withoutRut = totalHornGrowthLength_withoutRut +incr;
    }

    static private void increaseAgeSpecificHornGrowthLength_withRut(int age,double incr){
	ageSpecificHornGrowthLength_withRut[age-minAge] = ageSpecificHornGrowthLength_withRut[age-minAge] +incr;
    }

    static private void increaseAgeSpecificHornGrowthLength_withoutRut(int age,double incr){
	ageSpecificHornGrowthLength_withoutRut[age-minAge] = ageSpecificHornGrowthLength_withoutRut[age-minAge] +incr;
    }

    static double getDefaultRelativeMatingRank(){
	return defaultRelativeMatingRank;
    }

    public double getRelativeMatingRank(){
	return relativeMatingRank;
    }

    public double[] getRankOverLife(){
	return rankOverLife;
    }

    public double getGrowthFactorLength(){
	return -relativeMatingRank*maxGrowthLengthReduction;
    }

    public Boolean updateLegality(){
	if(!isLegal){
	    if(randomGenerator.nextDouble() <= getProbOfBeingLegal_length(hornLength)){
		isLegal = Boolean.TRUE;
		ageAtLegality = age;
	    }
	    else
		isLegal = Boolean.FALSE;
	}
	return isLegal;
    }

 
    public Boolean getIsLegal(){
	return this.isLegal;
    }

    static int getNumberOfAges(){
	return numberOfAges;
    }

    static int getMinAge(){
	return minAge;
    }

    static int getMaxAge(){
	return maxAge;
    }

    static double getMaxGrowthLengthReduction(){
	return maxGrowthLengthReduction;
    }

    // static double getMinGrowthLengthTranslation(){
    // 	return minGrowthLengthTranslation;
    // }

    public int getAge(){
	return this.age;
    }

    public int getAgeAtLegality(){
	return this.ageAtLegality;
    }

    public double getHornLength(){
	return this.hornLength;
    }

    static double getSurvivalAtAge(int a){
	return ageSpecificSurvival[a-minAge];
    }

    static double getLegalityInterceptLength(){
	return legality_length[0];
    }

    static double getLegalitySlopeLength(){
	return legality_length[1];
    }


    static double getProbOfBeingLegal_length(double length){
	return 1/(1+Math.exp(-(legality_length[0]+length*legality_length[1])));
    }


    public void growHorn(Boolean printOutput){
        /* draw random number */
	double rand = randomGenerator.nextGaussian();

	/* for the statistics */
	double incrementLength_withRut = getAnnulusIncrement(age,rand,getGrowthFactorLength());
	double incrementLength_withoutRut = getAnnulusIncrement(age,rand,0);

	/* record growth increments for the statistics */
	increaseTotalHornGrowthLength_withRut(incrementLength_withRut);
	increaseTotalHornGrowthLength_withoutRut(incrementLength_withoutRut);
	increaseAgeSpecificHornGrowthLength_withRut(age,incrementLength_withRut);
	increaseAgeSpecificHornGrowthLength_withoutRut(age,incrementLength_withoutRut);

	/* actual horn growth */
	hornLength = hornLength + incrementLength_withRut;

	if(printOutput)
	    System.out.println(" ,"+age+", " +hornLength+", "+relativeMatingRank+", "+getGrowthFactorLength()+", "+incrementLength_withRut+", "+incrementLength_withoutRut);
    }


     private static double getAnnulusIncrement(int age,double rand,double growthFactor){
    	 //System.out.println("without = "+Math.max(0,rand*SDAgeSpecificAnnuliIncrement[age-minAge]+meanAgeSpecificAnnuliIncrement[age-minAge])+", with = "+Math.max(0,rand*SDAgeSpecificAnnuliIncrement[age-minAge]+meanAgeSpecificAnnuliIncrement[age-minAge]*(1+growthFactor))+", 1+gF = "+(1+growthFactor));
    	    return  Math.max(0,rand*SDAgeSpecificAnnuliIncrement[age-minAge]+meanAgeSpecificAnnuliIncrement[age-minAge]*(1+growthFactor));
    }


   public void ageing(){
	age++;
    }

    static void printSurvivalFct(){
	DecimalFormat df = new DecimalFormat();
	df.setMaximumFractionDigits(4);	
	System.out.println("Age-specific survival function of rams\nAge\tProb to survive to the next year");
	for(int a=0; a<numberOfAges; a++)
	    System.out.println((a+minAge)+"\t"+df.format(ageSpecificSurvival[a]));
    }

    static void printHornStats(){
	DecimalFormat df = new DecimalFormat();
	df.setMaximumFractionDigits(4);	
	System.out.println("Horn length distribution of 2-year old rams\nMean = "+df.format(meanInitialHornLength)+"\tSD = "+df.format(SDInitialHornLength));
	System.out.println("Age-specific annuli statistics of rams\nAge\tMean\tSD");
	for(int a=0; a<numberOfAges; a++)
	    System.out.println((a+minAge)+"\t"+df.format(meanAgeSpecificAnnuliIncrement[a])+"\t"+df.format(SDAgeSpecificAnnuliIncrement[a]));
	System.out.println("Age-specific horn base statistics of rams\nAge\tMean\tSD");
    }

    static void printLegalityParams(){
	DecimalFormat df = new DecimalFormat();
	df.setMaximumFractionDigits(4);	
	System.out.println("Parameters for the probability of being legal as a function of horn length\nIntercept = "+df.format(legality_length[0])+", Slope = "+df.format(legality_length[1]));
    }
}



