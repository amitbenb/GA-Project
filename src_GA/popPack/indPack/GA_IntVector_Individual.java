package popPack.indPack;

import popPack.BasicBuilder;

public class GA_IntVector_Individual extends GA_Individual
{
	private int maxIntVal = Integer.MAX_VALUE;
	

	public class GA_Int_Atom extends GA_Atom
	{
		int gene = (int)(Math.random() * getMaxIntVal());
		
		public GA_Int_Atom()
		{
			gene = (int)(Math.random() * getMaxIntVal());
		}

		public GA_Int_Atom(int val)
		{
			gene = val;
		}
		
		public void mutateAtom()
		{
			gene = (int)(Math.random() * getMaxIntVal());
		}

		public GA_Atom selfReplicate()
		{
			return new GA_Int_Atom(gene);
		}
	}
	
	public GA_IntVector_Individual(int length)
	{
		super();
		m_genome = new GA_Int_Atom[length];
		for (int i = 0; i < m_genome.length; i++)
			m_genome[i] = new GA_Int_Atom();
		
		
		// TODO Auto-generated constructor stub
	}

	public GA_IntVector_Individual(GA_IntVector_Individual ind)
	{
		super(ind);
		maxIntVal = ind.maxIntVal;
		m_genome = new GA_Int_Atom[ind.getGenomeSize()];
		for (int i = 0; i < m_genome.length; i++)
			m_genome[i] = ind.getGene(i).selfReplicate();
		// TODO Auto-generated constructor stub
	}

	
	// Getters
	public int getMaxIntVal()
	{
		return maxIntVal;
	}
	
	@Override
	public GA_Individual selfReplicate()
	{
		return new GA_IntVector_Individual(this);
	}

	@Override
	public void calculateFitness()
	{
		throw new RuntimeException("No fitness function defined");
	}

	@Override
	public double calculateBenchmark()
	{
		throw new RuntimeException("No benchmark score defined");
	}

	@Override
	public void development(BasicBuilder builder)
	{
		// Nothing to do. No developmental process defined
	}

}
