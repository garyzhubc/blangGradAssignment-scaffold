package matchings;

import java.util.Collections;
import java.util.List;

import bayonet.distributions.Random;
import bayonet.math.NumericalUtils;

import blang.core.LogScaleFactor;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class PermutationSamplerLocallyBalanced implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable Permutation permutation;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;

  @Override
  /**
   * Efficient implementation of Locally-balanced Proposal by G. Zanella 2017.
   */
  public void execute(Random rand) {
    double log_Pi = logDensity();
    double[] log_probs_halved = new double[(permutation.componentSize()*(permutation.componentSize()-1))/2];
    int idx = 0;
    for (int i=0;i<permutation.componentSize();i++) {
      for (int j=i+1;j<permutation.componentSize();++j) {
        Collections.swap(permutation.getConnections(),i,j);
        log_probs_halved[idx] = logDensity()/2;
        Collections.swap(permutation.getConnections(),i,j);
        idx++;
      }
    }
    double sum = NumericalUtils.logAdd(log_probs_halved);
    double[] probs = new double[(permutation.componentSize()*(permutation.componentSize()-1))/2];
    for (int i=0;i<(permutation.componentSize()*(permutation.componentSize()-1))/2;i++) 
      probs[i] = Math.exp(log_probs_halved[i]-sum);
    int k = rand.nextCategorical(probs);
    double Qij = probs[k];
    int l = (int) ((-(2*(permutation.componentSize()+1)-3)+Math.sqrt(Math.pow(2*(permutation.componentSize()+1)-3,2)-8*k))/(-2));
    Collections.swap(permutation.getConnections(),l,k-(2*permutation.componentSize()-l-1)*l/2+l+1);
    idx = 0;
    for (int i=0;i<permutation.componentSize();i++) {
      for (int j=i+1;j<permutation.componentSize();++j) {
        Collections.swap(permutation.getConnections(),i,j);
        log_probs_halved[idx] = logDensity()/2;
        Collections.swap(permutation.getConnections(),i,j);
        idx++;
      }
    }
    sum = NumericalUtils.logAdd(log_probs_halved);
    double Qji = Math.exp(log_probs_halved[k]-sum);
    if (!rand.nextBernoulli(Math.min(1,Math.exp(logDensity()-log_Pi+Math.log(Qji)-Math.log(Qij))))) 
      Collections.swap(permutation.getConnections(),l,k-(2*permutation.componentSize()-l-1)*l/2+l+1);
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
