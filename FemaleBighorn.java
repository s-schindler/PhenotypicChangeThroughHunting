/*
 * Author: Susanne Schindler
 * Date: 8.3.2016
 * Email: Susanne.Schindler2@web.de
 * Copyright: Susanne Schindler
 */

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 
 */
public class FemaleBighorn {
    private int age;

    private static double[] ageSpecificSurvival;

    private static final int minAge = 2;
    private static final int maxAge = 19;
    private static final int numberOfAges = maxAge - minAge;
    private static final double epsilon = 0.0000001;

    private static Random randomGenerator;

    /**
     * constructor
     * 
     */
    public FemaleBighorn () {
	setDefaults();
	double rand = randomGenerator.nextGaussian();
    }

    private void setDefaults(){
	age = minAge;
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
	    System.out.println("FemaleBighorn:setSurvivalRates:Array dimension error. "+e.getMessage());
	}
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

    public int getAge(){
	return this.age;
    }

    static double getSurvivalAtAge(int a){
	return ageSpecificSurvival[a-minAge];
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

}



