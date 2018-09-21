/*
 * Author: Susanne Schindler
 * Date: 25.10.2013
 * Email: Susanne.Schindler2@web.de
 * Copyright: Susanne Schindler
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Properties;
import java.util.ArrayList;
import java.text.DecimalFormat;

/**
 * main class to start a simulation
 */
public class StartSimulation {

    private static String inputFileName;
    private static String outputFileName_mal = "Output/output_mal";
    private static String outputFileName_fem = "Output/output_fem";
    private static String outputFileName_rank = "Output/output_rank";
 
    //private final String directory = "/home/sschindl/Forschung/Programme/PhenotypicHuntingEffect_Neuhaus/RutParticipationAdaptsToSexRatio/";

    /**
     * 
     * @param argv[] zero, one, two, or three parameters can be set
     * 1st param: filename of parameters
     * 2nd param: hunting pressure in percent (optional)
     * 3rd param: seed for random generator (optional)
     * 4th param: filename for output (optional)
     */
    public static void main (String argv[]) {
        Simulation sim;
        switch (argv.length) {
	case 1:
	    setInputFileName(argv[0]);
	    createOutputFiles();
	    MaleBighorn.setSurvivalRates(readMaleSurvivalFromFile());
	    FemaleBighorn.setSurvivalRates(readFemaleSurvivalFromFile());
	    MaleBighorn.setInitialHornLength(readInitialHornLengthFromFile());
	    //	    Bighorn.setInitialBaseCircumference(readInitialBaseCircumferenceFromFile());
	    MaleBighorn.setGrowthParams_horn(readMeanAnnuliFromFile(),readSDAnnuliFromFile());
	    //	    MaleBighorn.setGrowthParams_base(readMeanBaseIncrementsFromFile(),readSDBaseIncrementsFromFile());
	    MaleBighorn.setLegalityParams(readLegalityLengthParamsFromFile());
	    MaleBighorn.initializeArrays();
	    sim = new Simulation();
	    sim.setPopulationName(readPopulationFromFile());
	    sim.startSimulation();
	    break;
	case 2:
	    setInputFileName(argv[0]);
	    createOutputFiles();
	    MaleBighorn.setSurvivalRates(readMaleSurvivalFromFile());
	    FemaleBighorn.setSurvivalRates(readFemaleSurvivalFromFile());
	    MaleBighorn.setInitialHornLength(readInitialHornLengthFromFile());
	    //	    MaleBighorn.setInitialBaseCircumference(readInitialBaseCircumferenceFromFile());
	    MaleBighorn.setGrowthParams_horn(readMeanAnnuliFromFile(),readSDAnnuliFromFile());
	    //	    MaleBighorn.setGrowthParams_base(readMeanBaseIncrementsFromFile(),readSDBaseIncrementsFromFile());
	    MaleBighorn.setLegalityParams(readLegalityLengthParamsFromFile());
	    MaleBighorn.initializeArrays();
	    MalePopulation.setHuntingPressure(Double.parseDouble(argv[1]));
	    Simulation.setHuntingPressure(Double.parseDouble(argv[1]));
	    sim = new Simulation();
	    sim.setPopulationName(readPopulationFromFile());
	    sim.startSimulation();
	    break;
	case 3:
	    setInputFileName(argv[0]);
	    createOutputFiles();
	    MaleBighorn.setSurvivalRates(readMaleSurvivalFromFile());
	    FemaleBighorn.setSurvivalRates(readFemaleSurvivalFromFile());
	    MaleBighorn.setInitialHornLength(readInitialHornLengthFromFile());
	    //	    MaleBighorn.setInitialBaseCircumference(readInitialBaseCircumferenceFromFile());
	    MaleBighorn.setGrowthParams_horn(readMeanAnnuliFromFile(),readSDAnnuliFromFile());
	    //	    MaleBighorn.setGrowthParams_base(readMeanBaseIncrementsFromFile(),readSDBaseIncrementsFromFile());
	    MaleBighorn.setLegalityParams(readLegalityLengthParamsFromFile());
	    MaleBighorn.initializeArrays();
	    MalePopulation.setHuntingPressure(Double.parseDouble(argv[1]));
	    Simulation.setHuntingPressure(Double.parseDouble(argv[1]));
	    Simulation.setRandomSeed(Long.parseLong(argv[2]));
	    sim = new Simulation();
	    sim.setPopulationName(readPopulationFromFile());
	    sim.startSimulation();
	    break;
	case 4:
	    setInputFileName(argv[0]);
	    setOutputFileName(argv[3]);
	    createOutputFiles();
	    MaleBighorn.setSurvivalRates(readMaleSurvivalFromFile());
	    FemaleBighorn.setSurvivalRates(readFemaleSurvivalFromFile());
	    MaleBighorn.setInitialHornLength(readInitialHornLengthFromFile());
	    //	    MaleBighorn.setInitialBaseCircumference(readInitialBaseCircumferenceFromFile());
	    MaleBighorn.setGrowthParams_horn(readMeanAnnuliFromFile(),readSDAnnuliFromFile());
	    //	    MaleBighorn.setGrowthParams_base(readMeanBaseIncrementsFromFile(),readSDBaseIncrementsFromFile());
	    MaleBighorn.setLegalityParams(readLegalityLengthParamsFromFile());
	    MaleBighorn.initializeArrays();
	    MalePopulation.setHuntingPressure(Double.parseDouble(argv[1]));
	    Simulation.setHuntingPressure(Double.parseDouble(argv[1]));
	    Simulation.setRandomSeed(Long.parseLong(argv[2]));
	    sim = new Simulation();
	    sim.setPopulationName(readPopulationFromFile());
	    sim.startSimulation();
	    break;
	default:
	    System.out.println("StartSimulation: main: Invalid number of parameters.");
        }
    }

    private static void setInputFileName(String name){
	inputFileName = name;
    }

    private static void setOutputFileName(String name){
	outputFileName_mal = name+"_mal";
	outputFileName_fem = name+"_fem";
	outputFileName_rank = name+"_rank";
    }

    private static String readPopulationFromFile(){
	String popName = "Population not specified";	    
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    popName = prop.getProperty("population");
	}              
	catch (Exception e) {
	    System.out.println("StartSimulation:readPopulationFromFile: " + e.getMessage());
	}
	return popName;
    }


    private static double[] readMaleSurvivalFromFile(){
	double[] survArray = new double[MaleBighorn.getNumberOfAges()];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    for (int i=MaleBighorn.getMinAge(); i<MaleBighorn.getMaxAge();i++){
		String propName = "MaleSurvivalAtAge"+i;
		survArray[i-MaleBighorn.getMinAge()] = Double.parseDouble(prop.getProperty(propName));
	    }      
	}        
	catch (Exception e) {
	    System.out.println("StartSimulation:readMaleSurvivalFromFile: " + e.getMessage());
	}
	return survArray;
    }



    private static double[] readFemaleSurvivalFromFile(){
	double[] survArray = new double[FemaleBighorn.getNumberOfAges()];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    for (int i=FemaleBighorn.getMinAge(); i<FemaleBighorn.getMaxAge();i++){
		String propName = "FemaleSurvivalAtAge"+i;
		survArray[i-FemaleBighorn.getMinAge()] = Double.parseDouble(prop.getProperty(propName));
	    }      
	}        
	catch (Exception e) {
	    System.out.println("StartSimulation:readFemaleSurvivalFromFile: " + e.getMessage());
	}
	return survArray;
    }


    private static double[] readInitialHornLengthFromFile(){
	double[] initialHornLengthStats  = new double[2];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    initialHornLengthStats[0] = Double.parseDouble(prop.getProperty("MeanLengthAtAge2"));
	    initialHornLengthStats[1] = Double.parseDouble(prop.getProperty("SDLengthAtAge2"));
	}      
	catch (Exception e) {
	    System.out.println("StartSimulation:readInitialHornLengthFromFile: " + e.getMessage());
	}
	return initialHornLengthStats;
    }

    private static double[] readMeanAnnuliFromFile(){
	double[] meanAnnuli  = new double[MaleBighorn.getNumberOfAges()];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    for (int i=MaleBighorn.getMinAge(); i<MaleBighorn.getMaxAge();i++){
		String propName = "MeanAnnulusAtAge"+i;
		meanAnnuli[i-MaleBighorn.getMinAge()] = Double.parseDouble(prop.getProperty(propName));
	    }      
	}        
	catch (Exception e) {
	    System.out.println("StartSimulation:readMeanAnnuliFromFile: " + e.getMessage());
	}
	return meanAnnuli;
    }

    private static double[] readSDAnnuliFromFile(){
	double[] SDAnnuli  = new double[MaleBighorn.getNumberOfAges()];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    for (int i=MaleBighorn.getMinAge(); i<MaleBighorn.getMaxAge();i++){
		String propName = "SDAnnulusAtAge"+i;
		SDAnnuli[i-MaleBighorn.getMinAge()] = Double.parseDouble(prop.getProperty(propName));
	    }      
	}        
	catch (Exception e) {
	    System.out.println("StartSimulation:readSDAnnuliFromFile: " + e.getMessage());
	}
	return SDAnnuli;
    }

    private static double[] readInitialBaseCircumferenceFromFile(){
	double[] initialBaseStats  = new double[2];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    initialBaseStats[0] = Double.parseDouble(prop.getProperty("MeanCircumferenceAtAge4"));
	    initialBaseStats[1] = Double.parseDouble(prop.getProperty("SDCircumferenceAtAge4"));
	}      
	catch (Exception e) {
	    System.out.println("StartSimulation:readInitialBaseCircumferenceFromFile: " + e.getMessage());
	}
	return initialBaseStats;
    }

    private static double[] readMeanBaseIncrementsFromFile(){
	double[] meanBaseIncr  = new double[MaleBighorn.getNumberOfAges()];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    for (int i=MaleBighorn.getMinAge(); i<MaleBighorn.getMaxAge();i++){
		String propName = "MeanBaseIncrementAtAge"+i;
		meanBaseIncr[i-MaleBighorn.getMinAge()] = Double.parseDouble(prop.getProperty(propName));
	    }      
	}        
	catch (Exception e) {
	    System.out.println("StartSimulation:readMeanBaseIncrementsFromFile: " + e.getMessage());
	}
	return meanBaseIncr;
    }



    private static double[] readLegalityLengthParamsFromFile(){
	double[] paramsLength  = new double[2];
	try {
	    Properties prop = new Properties();
	    prop.load(new FileInputStream(inputFileName));
	    String propName = "LegalInterceptLength";
	    paramsLength[0] = Double.parseDouble(prop.getProperty(propName));
	    propName = "LegalSlopeLength";
	    paramsLength[1] = Double.parseDouble(prop.getProperty(propName));
	    }      
	catch (Exception e) {
	    System.out.println("StartSimulation:readLegalityLengthParamsFromFile: " + e.getMessage());
	}
	return paramsLength;
    }


    static void printToScreen(int arraySize, int[] array){
	/* print on screen */
	System.out.println("\nAge:");
	for (int i=0; i<arraySize; i++)
	    System.out.print((i+MaleBighorn.getMinAge())+"\t");
	System.out.println("\nCount:");
	printToStream(arraySize,array,new BufferedWriter((new OutputStreamWriter(System.out))));
    }


    static void printToScreen(int arraySize, double[] array){
	/* print on screen */
	System.out.println("\nAge:");
	for (int i=0; i<arraySize; i++)
	    System.out.print((i+MaleBighorn.getMinAge())+"\t");
	System.out.println("\nStats:");
	printToStream(arraySize,array,new BufferedWriter((new OutputStreamWriter(System.out))));
    }

    static void printStatsToScreen(int arraySizes,int[] array1,double[] array2, double[] array3,double[] array4, double[] array5){
	/* print on screen */
	System.out.println("\nAge:");
	for (int i=0; i<arraySizes; i++)
	    System.out.print((i+MaleBighorn.getMinAge())+"\t");
	System.out.println("\nCount:");
	printToStream(arraySizes,array1,new BufferedWriter((new OutputStreamWriter(System.out))));
	System.out.println("Mean horn length:");
	printToStream(arraySizes,array2,new BufferedWriter((new OutputStreamWriter(System.out))));
	System.out.println("SD horn length:");
	printToStream(arraySizes,array3,new BufferedWriter((new OutputStreamWriter(System.out))));
	System.out.println("Mean base circumference:");
	printToStream(arraySizes,array4,new BufferedWriter((new OutputStreamWriter(System.out))));
	System.out.println("SD base circumference:");
	printToStream(arraySizes,array5,new BufferedWriter((new OutputStreamWriter(System.out))));
    }

    private static void createOutputFiles(){
	try {
	    File file1 = new File(outputFileName_mal);
	    File file2 = new File(outputFileName_fem);
	    File file3 = new File(outputFileName_rank);
	    if(file1.exists())
		file1.delete(); // delete file to write to empty file
	    if(file2.exists())
		file2.delete(); // delete file to write to empty file
	    if(file3.exists())
		file3.delete(); // delete file to write to empty file
	    /* (re)create it  */
	    file1.createNewFile();
	    file2.createNewFile();
	    file3.createNewFile();
	}catch (Exception e) {
	    System.out.println("StartSimulation:createOutputFiles:"+e.getMessage());
	}
    }
 
    static void printToFile_mal(int arraySize, int[] array1,double[] array2, double[] array5,int intNumber){
	/* output to file */
	try {
	    int arraySize2 = 4*arraySize+2;
	    double[] doubleArray = new double[arraySize2];
	    System.arraycopy(array2,0,doubleArray,0,2*arraySize);
	    System.arraycopy(array5,0,doubleArray,2*arraySize,2*arraySize+1);

	    printToStream(arraySize,array1,arraySize2,doubleArray,intNumber,new BufferedWriter(new FileWriter(new File(outputFileName_mal),Boolean.TRUE)));
	} catch (Exception e) {
	    System.out.println("StartSimulation:printToFile_mal:"+e.getMessage());
	}
    }
 
    static void printToFile_rank(int matrixDim1, int matrixDim2, double[][] matrix){
	/* output to file */
	try {
	    printToStream(matrixDim1,matrixDim2,matrix,new BufferedWriter(new FileWriter(new File(outputFileName_rank),Boolean.TRUE)));
	} catch (Exception e) {
	    System.out.println("StartSimulation:printToFile_rank:"+e.getMessage());
	}
    }
 
 
    static void printToFile_fem(int arraySize, int[] array1){
	/* output to file */
	try {
	    printToStream(arraySize,array1,new BufferedWriter(new FileWriter(new File(outputFileName_fem),Boolean.TRUE)));
	} catch (Exception e) {
	    System.out.println("StartSimulation:printToFile_fem:"+e.getMessage());
	}
    }
 
 



    private static void printToStream(int arraySize1, int[] array1, int arraySize2, double[] array2, int number, Writer bs){
	try{
	    for (int i=0; i<arraySize1; i++)
		bs.write(array1[i]+"\t");

	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(4);	
	    for (int i=0; i<arraySize2; i++)
		bs.write(df.format(array2[i])+"\t");

	    bs.write(number+"\n");

	    bs.flush();
	}catch (Exception e) {
	    System.out.println("StartSimulation:printToStream:"+e.getMessage());
	}
    }

    private static void printToStream(int arraySize, int[] array,Writer bs){
	try{
	    for (int i=0; i<arraySize; i++)
		bs.write(array[i]+"\t");
	    bs.write("\n");
	    bs.flush();
	}catch (Exception e) {
	    System.out.println("StartSimulation:printToStream:"+e.getMessage());
	}
    }

    private static void printToStream(int arraySize, double[] array,Writer bs){
	try{
	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(4);	
	    for (int i=0; i<arraySize; i++)
		bs.write(df.format(array[i])+"\t");
	    bs.write("\n");
	    bs.flush();
	}catch (Exception e) {
	    System.out.println("StartSimulation:printToStream:"+e.getMessage());
	}
    }

    private static void printToStream(int matrixDim1, int matrixDim2, double[][] matrix, Writer bs){
	try{
	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(4);	
	    for (int i=0; i<matrixDim1; i++){
		for (int j=0; j<matrixDim2; j++)
		    bs.write(df.format(matrix[i][j])+"\t");
		bs.write("\n");
	    }
	    bs.flush();
	}catch (Exception e) {
	    System.out.println("StartSimulation:printToStream:"+e.getMessage());
	}
    }

}



