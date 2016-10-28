package popPack;

import popPack.indPack.GA_Individual;

/**
 * 
 * @author amitbenb
 *
 */
public class GA_Population
{
	protected static double m_eliteRatio = 0.01;
	public static int tournamentSize = 2;
	public static int tournamentWinnerNum = 1;
	
	protected GA_Individual[] m_pop = new GA_Individual[0];
	
	protected Base_Fitness m_fitness = null;

	
	private int m_bestInd  = 0;
	protected double m_avgFitness = 0;
	protected double m_benchmarkScore = 0;

	protected static double m_mutProb = 0.1;
	protected static double m_xoProb = 1;

	protected static int m_archiveSize = 0; // Not used here.

	protected GA_Individual m_bestEver;
	protected double m_bestEverBenchmarkScore = -Double.MAX_VALUE/2;

	// Diversity maintaining params.
	public boolean m_crowdingFlag = true;
	public double m_neighborDistanceRatio = 0.40; 	// Maximal neighbor distance ratio.
//	public int m_neighborDistance; 		// Will be maximal neighbor distance.
	public int m_maxNumOfNeighbors = 8;
	
	
	public GA_Individual[] getPop()
	{
		return m_pop;
	}
	
	public int getPopSize()
	{
		return getPop().length;
	}
	
	public GA_Individual getIndividual(int i)
	{
		return m_pop[i];
	}
	
	public int getBestInd()
	{
		return m_bestInd;
	}

	public GA_Individual getBestIndividual()
	{
		return getPop()[getBestInd()];
	}

	public GA_Individual getBestEverIndividual()
	{
		return m_bestEver;
	}
	
	public double getBestEverBenchmarkScore()
	{
		return m_bestEverBenchmarkScore;
	}

	public double getBestFitness()
	{
		return getBestIndividual().getFitness();
	}

	public double getAvgFitness()
	{
		return m_avgFitness;
	}
	
	public double getApproxAvgFitness()
	{
		return (int)(100 * m_avgFitness) / 100.0;
	}
	
	public double getBenchmarkScore()
	{
		return m_benchmarkScore;
	}

	// Setters
	
	public void setPopulation(GA_Individual[] pop)
	{
		this.m_pop = pop;
	}
	
	public void setBuilder(BasicBuilder b)
	{
		// Nothing to do in this class.
		// Not abstract because not necessary for population to have a builder.
	}

	public void setFitness(BasicBuilder f)
	{
		// Nothing to do in this class.
		// Not abstract because not necessary for population to have a fitness object.
	}

	public void setInd(GA_Individual I, int i)
	{
		int pSize = getPopSize();
		if (0<=i && i<pSize)
			m_pop[i] = I;
//		else
//		{
//			i = i - pSize;	
//			if (0<=i && i<pSize)
//				m_archive[i] = I;
//			
//			else // Error.
//			{
//				System.out.print("Error. Index " + (i + pSize) + " is not in range.\n");
//				System.out.print("Please insert number between 0 and " + (pSize-1) + ".\n");
//			}
//		}
	}

	public void setBestInd(int idx)
	{
		m_bestInd = idx;
	}

	public void setBestEverIndividual(GA_Individual ind)
	{
		m_bestEver = ind;
	}

	public void setBestEverBenchmarkScore(double fit)
	{
		m_bestEverBenchmarkScore = fit;
	}

	public void setAvgFitness(double fit)
	{
		m_avgFitness = fit;
	}
	
	public void setBenchmarkScore(double bench)
	{
		m_benchmarkScore = bench;
	}

	public void evaluation()
	{
		int count = 0;
		setAvgFitness(0);
		for (int i = 0; i < m_pop.length; i++)
		{
////		m_pop[i].buildPhenotype();
			m_pop[i].calculateFitness();
//			System.out.println(m_pop[i].getFitness());
			
			double fit = m_pop[i].getFitness();
			
			

//			m_pop[i] = m_pop[i].selfReplicate();
////		m_pop[i].buildPhenotype();
//			m_pop[i].calculateFitness();
//			if (fit!=m_pop[i].getFitness())
//				count++;

			setAvgFitness(getAvgFitness() + fit);

			if (i==0 || fit > getBestIndividual().getFitness())
			{
				setBestInd(i);
			}
		}
		setAvgFitness(getAvgFitness() / getPopSize());
		
		double benchmarkScore = getBestIndividual().calculateBenchmark();
		setBenchmarkScore(benchmarkScore);
		
		if (getBenchmarkScore() > getBestEverBenchmarkScore())
		{
			setBestEverIndividual(getBestIndividual().selfReplicate());
			setBestEverBenchmarkScore(benchmarkScore);
		}
		
		
		// Debug if/
		if (count>0)
			System.out.println("Fitness is funky: " + count);
		
	}

	public void selection()
	{
		int eliteSize = (int)(getPopSize() * GA_Population.m_eliteRatio);
//		System.out.println(eliteSize);
		GA_Individual[] elite = new GA_Individual[eliteSize];
		selectElite(elite);
		tournamentSelection(elite);
	}

	protected void selectElite(GA_Individual[] elite)
	{
		GA_Individual[] new_pop = new GA_Individual[getPopSize()];
		
		for (int i = 0; i < new_pop.length; i++)
		{
			new_pop[i] = getPop()[i];
		}
		qSort(new_pop);

		for (int i = 0; i < elite.length; i++)
		{
			elite[i] = new_pop[i].selfReplicate();
		}
		
		return;
		
	}

	protected void tournamentSelection(GA_Individual[] elite)
	{
		int tourSize = Math.min(GA_Population.tournamentSize,getPopSize() - elite.length);
		int stepSize = Math.min(GA_Population.tournamentWinnerNum,tourSize);
		
		// Indexes for individuals participating in tournament.
		int[] idxsOfCandidates = new int[tourSize];
		GA_Individual[] new_pop = new GA_Individual[getPopSize()]; 

		int i=0;
		
		for (i = 0; i < elite.length; i++)
		{
			new_pop[i] = elite[i].selfReplicate();
		}
		
		for(i=elite.length; i<getPopSize(); i+=stepSize)
		{
			for (int j = 0; j < idxsOfCandidates.length; j++)
			{
				// Find candidate.
				idxsOfCandidates[j] = (int)(Math.floor(Math.random() * getPopSize()));
				
				// Make sure candidate doesn't already have too many neighbors
				// in selected new_pop (unless you fail 40 times).
				for (int k = 0; k < 40 && m_crowdingFlag
						&& tooManyNeighbors(new_pop, getIndividual(idxsOfCandidates[j])); k++)
				{
					idxsOfCandidates[j] = (int)(Math.floor(Math.random() * getPopSize()));
//					if (k==30) System.out.println("Zeeep!");
				}
				
				// Don't choose the same candidate twice
				for (int k = 0; k < j; k++)
				{
					if(idxsOfCandidates[j] == idxsOfCandidates[k])
					{
//						idxsOfCandidates[j] = (int)(Math.floor(Math.random() * getPopSize()));
//						k = -1;
						j=j-1;
						k=j; // get out of loop;
					}
				}
			}
			
			GA_Individual[] tournament = new GA_Individual[tourSize];
			for (int j = 0; j < idxsOfCandidates.length; j++)
			{
				tournament[j]=getIndividual(idxsOfCandidates[j]);
			}
			
			qSort(tournament);	// Sort tournament by fitness

			// 
			int numOfWinners = Math.min(stepSize, getPopSize() - i);
			for(int j = 0; j < numOfWinners && i+j < getPopSize(); j++)
			{
				new_pop[i+j] = tournament[j].selfReplicate();
			}
		}
		
		setPopulation(new_pop);
	}

	private boolean tooManyNeighbors(GA_Individual[] pop, GA_Individual ind)
	{
		int maxNeighborDistance = (int)(ind.getMarkerArray().length * m_neighborDistanceRatio);
		int numOfNeighbors = 0;
		for (int i = 0; i < pop.length && pop[i]!=null; i++)
		{
			if(pop[i].markerDistance(ind) <= maxNeighborDistance)
				numOfNeighbors++;
		}
		
		return numOfNeighbors > m_maxNumOfNeighbors;
	}

	/**
	 * sorts population in order of descending fitness;
	 * 
	 * @param pop Population to be sorted.
	 */
	protected void qSort(GA_Individual[] pop)
	{
		//TODO: Improve sorting function.
		// Bubble-sort, awful.
		GA_Individual tmp = null;
		for (int i = 0; i < pop.length - 1; i++)
		{
//			for (int j = 0; j < pop.length - i - 1; j++)
			for (int j = 0; j < pop.length - 1; j++)
			{
				if (pop[j].getFitness() < pop[j+1].getFitness())
				{
					tmp = pop[j];
					pop[j] = pop[j + 1];
					pop[j + 1] = tmp;
					
				}
			}
			
		}
		
//		boolean sorted = true;
//		for (int i = 0; sorted && i < pop.length - 1; i++)
//		{
//			if (pop[i].getFitness() < pop[i + 1].getFitness())
//				sorted = false;
//		}
//		
//		if(!sorted)
//			System.out.println("Not Sorted?!?");
	}

	public void procreation()
	{
		int i,j;
		int eliteSize = (int)(getPopSize()*GA_Population.m_eliteRatio);
//		System.out.println(eliteSize);
		GA_Individual[] new_pop = new GA_Individual[getPopSize()]; 
		
		for(i=0; i<eliteSize; i++)
		{
			new_pop[i] = getIndividual(i).selfReplicate();
		}
		
		for(i=eliteSize; i<getPopSize(); i++)
		{
			new_pop[i] = null;
		}
		
		// XO loop.
		for(i=eliteSize; i<getPopSize(); i+=2)
		{
			int ind1 =  (int)(Math.random() * getPopSize());		// first to cross over.
			int ind2 =  (int)(Math.random() * getPopSize());		// second to cross over.

			// Find your first candidate (that wasn't used yet).
			for(;getIndividual(ind1%getPopSize())==null; ind1++);
			ind1 = ind1 % getPopSize();		// Update candidate.
			
//			if (i == getPopSize()) 
//				break;						// No more individuals
//			else
			{
				new_pop[i] = getIndividual(ind1).selfReplicate();
//				if (new_pop[i] == null)
//					System.out.println("NULL!");
				setInd(null,ind1);
			}

			// Find your second candidate (that wasn't used yet).
			for(;getIndividual(ind2%getPopSize())==null; ind2++);
			ind2 = ind2 % getPopSize();		// Update candidate.
			
//			if (j == getPopSize()) 
//				break;						// No more individuals
//			else
			if(i+1<getPopSize())
			{
				new_pop[i+1] = getIndividual(ind2).selfReplicate();
				setInd(null,ind2);
			}

			if (i+1<getPopSize() && Math.random() < m_xoProb)	// XO happens in this probability.
			{
				crossover(new_pop[i], new_pop[i+1]);
			}
		}
		
		// Mutation loop.
		for(i=eliteSize; i<getPopSize(); i++)
		{
			new_pop[i].mutate(m_mutProb);
		}
		
		setPopulation(new_pop);
	}

	protected void crossover(GA_Individual ind1, GA_Individual ind2)
	{
		GA_Individual.crossover(ind1, ind2);
	}

	
}
