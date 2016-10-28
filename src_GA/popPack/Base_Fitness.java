package popPack;

import popPack.indPack.GA_Individual;

public interface Base_Fitness
{
	abstract public String generateFitnessDataText(GA_Individual ind);
	abstract public String generateFitnessDataLine(GA_Individual ind);
	abstract public String generateFitnessDataTableHeader();
}
