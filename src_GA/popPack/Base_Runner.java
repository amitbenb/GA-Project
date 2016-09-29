package popPack;


import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

abstract public class Base_Runner
{
	public static boolean DEBUG_OUTPUT = true;

	public static FileWriter fitOut;
	public static FileWriter smallOut;
	public static FileWriter dataOut;
	
	public static String mainDir = new String("Z:\\");
	public static String ParameterFilePath = new String("Z:\\parameters.txt");
	
	public static GA_Population savePop = null;

	public static int NUMBER_OF_EXPERIMENTS = 1; // Number of experiments.

	public static int NUMBER_OF_STAGES = 1;
	
	public static Base_Runner[] runningStages;

	public String popDirFullPath = null;
	public String popDirPartPath = new String("pop\\");
	
	public double ELITE_RATIO;
	
	public int SIZE_OF_POPULATION = 400;
	public int NUMBER_OF_GENERATIONS = 200;
	
	public boolean FITNESS_STOP_FLAG = false;
	public boolean BENCHMARK_STOP_FLAG = false;
	public double FITNESS_STOP_THRESHOLD = 0.0;
	public double BENCHMARK_STOP_THRESHOLD = 0.0;

	public int TYPE_OF_MUTATION = 0;
	public double MUTATION_PROB = 0.02;
	public int TYPE_OF_CROSSOVER = 0;
	public double CROSSOVER_PROB = 0.8;
	
	public int SELECTION_TYPE = 1; // 1 is tournament Selection
	public int TOURNAMENT_SIZE = 2;
	public int NUMBER_OF_TOUR_WINNERS = 1;
	
	public boolean USE_CROWDING_FLAG = false; 
	public double MAXIMAL_NEIGHBOR_DISTANCE_RATIO = 0.4;
	public int MAXIMUM_NEIGHBOR_NUMBER = 10;

	public String FITNESS_CLASS_NAME;
	public Base_Fitness fitnessObj;

	public double WEIGHT_OF_CORRECT_TEST = 0.8;

	public static void collectParameters(String fileName) throws Exception
	{
		throw new RuntimeException();
	}

	
	/**
	 * Randomizing order of input array.
	 * 
	 * @param arr
	 */
	public static void RandomizeArray(Object[] arr)
	{
		for (int i = 0; i < arr.length - 1; i++)
		{
			int j = i + (int)(Math.random()*Integer.MAX_VALUE) % (arr.length - i - 1);
			
			// Swap
			Object tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
	}

	public static String getFileIdentifier(int expNumber, int stageNumber)
	{
		String retVal = new String("");
		
		// Only if multiple experiments add this counter;
		if(NUMBER_OF_EXPERIMENTS != 1)
		{
			retVal = retVal + "_";
			if (expNumber<100)
				retVal = retVal + "0";
			if (expNumber<10)
				retVal = retVal + "0";
			retVal = retVal + expNumber;
		}
	
		retVal = retVal + "_";
		if (stageNumber<100)
			retVal = retVal + "0";
		if (stageNumber<10)
			retVal = retVal + "0";
		retVal = retVal + stageNumber;
		
		return retVal;
	}


	public void collectEvoParameters(String fileName, int idx)throws Exception
	{
		Scanner inF = new Scanner(new File(mainDir + fileName));
	
		Base_Runner r = runningStages[idx];
	
		inF.nextLine(); // Getting rid of non-data line.
		r.SIZE_OF_POPULATION = inF.nextInt();
		inF.nextLine();	// Clear line
	
		inF.nextLine(); // Getting rid of non-data line.
		r.NUMBER_OF_GENERATIONS = inF.nextInt();
		inF.nextLine();	// Clear line
	
		inF.nextLine(); // Getting rid of non-data line.
		r.FITNESS_STOP_FLAG = inF.nextBoolean();
		if (r.FITNESS_STOP_FLAG)
			r.FITNESS_STOP_THRESHOLD = inF.nextInt();
		inF.nextLine();	// Clear line
		
		inF.nextLine(); // Getting rid of non-data line.
		r.BENCHMARK_STOP_FLAG = inF.nextBoolean();
		if (r.BENCHMARK_STOP_FLAG)
			r.BENCHMARK_STOP_THRESHOLD = inF.nextInt();
		inF.nextLine();	// Clear line
		
		inF.nextLine();	// Getting rid of non-data line.
		r.ELITE_RATIO = inF.nextDouble();
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.TYPE_OF_MUTATION = inF.nextInt(); // Not useful at the moment
		r.MUTATION_PROB = inF.nextDouble(); 
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.TYPE_OF_CROSSOVER = inF.nextInt(); // Not useful at the moment
		r.CROSSOVER_PROB = inF.nextDouble();
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.SELECTION_TYPE = inF.nextInt();
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.TOURNAMENT_SIZE = inF.nextInt();
		r.NUMBER_OF_TOUR_WINNERS = inF.nextInt();
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.USE_CROWDING_FLAG = inF.nextBoolean();
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.MAXIMAL_NEIGHBOR_DISTANCE_RATIO = inF.nextDouble();
		inF.nextLine();	// Clear line
	
		inF.nextLine();	// Getting rid of non-data line.
		r.MAXIMUM_NEIGHBOR_NUMBER = inF.nextInt();
		inF.nextLine();	// Clear line
	
		inF.close();
	}

	abstract public void collectFitParameters(String fileName, int idx) throws Exception;
}
