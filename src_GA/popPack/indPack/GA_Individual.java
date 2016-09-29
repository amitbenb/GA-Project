package popPack.indPack;

import popPack.BasicBuilder;

/**
 * 
 * @author amitbenb
 *
 */
abstract public class GA_Individual
{
	public static final int DEFAULT_SIZE_OF_MARKER_ARRAY = 200;

	public static int init_genome_size = 60;
	public static int genome_size_limit= 150;
	
	protected GA_Atom[] m_genome;
	protected int[] m_markers = new int[DEFAULT_SIZE_OF_MARKER_ARRAY];
	protected double m_staticFitness = 0;
	protected double m_dynamicFitness = 0;
	
	abstract public class GA_Atom
	{
		abstract public void mutateAtom();

		abstract public GA_Atom selfReplicate();
	}
	
	public GA_Individual()
	{
		for (int i = 0; i < m_markers.length; i++)
		{
			setMarker((int)(Math.random()*Integer.MAX_VALUE), i);
		}
	}
	
	public GA_Individual(GA_Individual ind)
	{
		m_markers = new int[ind.m_markers.length]; 
		for (int i = 0; i < m_markers.length; i++)
		{
			setMarker(ind.getMarker(i), i);
		}
	}
	

	
	abstract public GA_Individual selfReplicate();

	public double getStaticFitness()
	{
		return m_staticFitness;
	}

	public double getDynamicFitness()
	{
		return m_dynamicFitness;
	}

	public GA_Atom[] getGenome()
	{
		return m_genome;
	}
		
	public GA_Atom getGene(int idx)
	{
		return m_genome[idx];
	}
		
	public double getFitness()
	{
		return getStaticFitness();
	}

	public int[] getMarkerArray()
	{
		return m_markers;
	}
		
	public int getMarker(int idx)
	{
		return m_markers[idx];
	}
		
	public int getGenomeSize()
	{
		return m_genome.length;
	}
	
	// Setters
	public void setStaticFitness(double fitness)
	{
		m_staticFitness = fitness;
	}

	public void setDynamicFitness(double fitness)
	{
		m_dynamicFitness = fitness;
	}

	public void setGenome(GA_Atom[] genome)
	{
		m_genome = genome;
	}
	
	public void setFitness(double fitness)
	{
		setStaticFitness(fitness);
		setDynamicFitness(fitness);
	}

	public void setFitness(double sFitness, double dFitness)
	{
		setStaticFitness(sFitness);
		setDynamicFitness(dFitness);
	}
	
	protected void setGene(GA_Atom a, int idx)
	{
		m_genome[idx] = a;		
	}

	public void setMarkerArray(int[] arr)
	{
		m_markers = arr;
	}
		
	public void setMarker(int val, int idx)
	{
		m_markers[idx] = val;
	}
		
	


	abstract public void calculateFitness();

	abstract public double calculateBenchmark();

	public static void crossover(GA_Individual ind1, GA_Individual ind2)
	{
		if(ind1.m_markers.length == ind2.m_markers.length)
			// Mix genetic marker arrays in uniform XO manner.
			for (int i = 0; i < ind1.m_markers.length; i++)
			{
				if(Math.random() < 0.5)
				{
					// swap markers;
					int tmp = ind1.getMarker(i);
					ind1.setMarker(ind2.getMarker(i), i);
					ind2.setMarker(tmp, i);
				}
			}
		
		// For now just one default XO operator.
		twoPointCrossover(ind1, ind2);

		for (int i = 0; i < ind2.getGenome().length; i++)
		{
			if (ind2.getGenome()[i] == null)
				System.out.println("BBBB");
		}
		
	}

	public static void twoPointCrossover(GA_Individual ind1, GA_Individual ind2)
	{
		GA_Atom[] genome1 = ind1.getGenome();
		GA_Atom[] genome2 = ind2.getGenome();
		
		// Choosing close XO points to slow code growth
		double rand = Math.random();
		double noise = Math.random() / 5;
		int idx1 = (int)((rand + noise*(1-rand)) * (genome1.length+1));
		int idx2 = (int)((rand - noise*(rand)) * (genome2.length+1));
		
		GA_Atom[] newGenome1 = new GA_Atom[idx1 + (genome2.length - idx2)];
		GA_Atom[] newGenome2 = new GA_Atom[idx2 + (genome1.length - idx1)];
		
		//TODO Fixed?
		for (int i = 0; i < newGenome1.length; i++)
		{
			if(i<idx1)
			{
				newGenome1[i] = genome1[i].selfReplicate();
			}
			else // (i>=idx1)
			{
				newGenome1[i] = genome2[i-idx1+idx2].selfReplicate();
			}
		}
		
		for (int i = 0; i < newGenome2.length; i++)
		{
			if(i<idx2)
			{
				newGenome2[i] = genome2[i].selfReplicate();
			}
			else // (i>=idx2)
			{
				newGenome2[i] = genome1[i-idx2+idx1].selfReplicate();
			}
		}

//		newGenome1 = new GA_Atom[genome1.length];
//		newGenome2 = new GA_Atom[genome2.length];
//		for (int i = 0; i < newGenome1.length; i++)
//			newGenome1[i] = genome1[i];
//		for (int i = 0; i < newGenome2.length; i++)
//			newGenome2[i] = genome2[i];

		if (newGenome1.length <= GA_Individual.genome_size_limit)
			ind1.setGenome(newGenome1);
		if (newGenome2.length <= GA_Individual.genome_size_limit)
			ind2.setGenome(newGenome2);
		
	}

	public void mutate(double mutProb)
	{
		// Differentiate genetic markers.
		int idx = (int)(Math.random()*m_markers.length);
		setMarker((int)(Math.random()*Integer.MAX_VALUE), idx);
		
		// Just one mutation option at present.
//		if(Math.random() < mutProb)
//			mutateRandomAtom();
		uniformMutation(mutProb);
	}

	private void uniformMutation(double mutProb)
	{
		int offset = (int)(Math.random() * getGenome().length);

		for (int i = 0; i < getGenome().length; i++)
		{
			if (Math.random() < mutProb)
				performMutation((i + offset) % getGenome().length);
		}
		
	}

	/**
	 * Perform a mutation around a specific location.
	 * 
	 * @param idx Index of gene involved in mutation.
	 */
	private void performMutation(int idx)
	{
		double rand = Math.random();
		
		if (rand < 1/2)
			getGenome()[idx].mutateAtom();
		else // (if rand < 1)
			performGeneCopy(idx);
	}

	/**
	 * A small gene makes a copy of itself and overwrites some other gene.
	 * 
	 * @param idx Index of the beginning of the gene.
	 */
	private void performGeneCopy(int idx)
	{
		int geneLength = (int)(Math.random()*5)+1;
		if (idx+geneLength >= getGenome().length)
			geneLength = getGenome().length - idx;
		GA_Atom[] tempGene = new GA_Atom[geneLength];

		for (int i = 0; i < tempGene.length; i++)
		{
			tempGene[i] = getGenome()[idx+i].selfReplicate();
		}
		
		int idx2 = (int)(Math.random() * getGenome().length);
		for (int i = 0; i < tempGene.length && idx2 + i <getGenome().length; i++)
		{
			setGene(tempGene[i],idx2+i);
		}
	}

	private void mutateRandomAtom()
	{
		int idx = (int)(Math.random() * getGenome().length);
		getGenome()[idx].mutateAtom();
	}

	abstract public void development(BasicBuilder builder);

	/**
	 * 
	 * @param ind Individual to compare with.
	 * @return The edit distance between the marker vectors.
	 */
	public int markerDistance(GA_Individual ind)
	{
		int distance = 0;
		for (int i = 0; i < m_markers.length; i++)
		{
			if (this.getMarker(i)!=ind.getMarker(i))
				distance++;
		}
		return distance;
	}

	// This is for debugging purposes at the moment.
	public void buildPhenotype()
	{
		// TODO Auto-generated method stub
	}

}
