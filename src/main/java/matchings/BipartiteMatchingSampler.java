package matchings;

import java.util.List;

import bayonet.distributions.Multinomial;
import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class BipartiteMatchingSampler implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable BipartiteMatching matching;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;

  @Override
  public void execute(Random rand) {
    // Fill this. 
	  BipartiteMatching match = matching;
	  double logf = logDensity();
	  List<LogScaleFactor> numF = numericFactors;
	  matching.sampleUniform(rand);   
//	  System.out.print(match.getConnections());
//	  System.out.print(matching.getConnections());
//	  System.out.print('\n');
	  
	  if (!Generators.bernoulli(rand,Math.min(1,Math.exp(logDensity()-logf)))) {
		  matching = match;
		  numericFactors = numF;
	  }
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
