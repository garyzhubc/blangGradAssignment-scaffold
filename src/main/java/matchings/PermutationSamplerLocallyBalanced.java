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
   * Implementation of Informed proposals for local MCMC in discrete spaces. https://arxiv.org/abs/1711.07424.
   * propose kernel: K(x,y) = 1_{B(x)\x}(y)
   * informed correction: \sqrt(\Pi(x))
   */
  public void execute(Random rand) {
    double log_Pi = logDensity();
    double[] log_probs_halved = new double[(permutation.componentSize()*(permutation.componentSize()-1))/2];
    int idx = 0;
    int[][] idx_swapped = new int[(permutation.componentSize()*(permutation.componentSize()-1))/2][2];
    for (int i=0;i<permutation.componentSize();i++) {
      for (int j=i+1;j<permutation.componentSize();++j) {
        Collections.swap(permutation.getConnections(),i,j);
        log_probs_halved[idx] = logDensity()/2;
        idx_swapped[idx][0] = i;
        idx_swapped[idx][1] = j;
        Collections.swap(permutation.getConnections(),i,j);
        idx++;
      }
    }
    double sum = NumericalUtils.logAdd(log_probs_halved);
    double[] probs = new double[(permutation.componentSize()*(permutation.componentSize()-1))/2];
    for (int i=0;i<(permutation.componentSize()*(permutation.componentSize()-1))/2;i++) {
      probs[i] = Math.exp(log_probs_halved[i]-sum);
    }
    int k = rand.nextCategorical(probs);
    double Qij = probs[k];
    Collections.swap(permutation.getConnections(),idx_swapped[k][0],idx_swapped[k][1]);
    log_probs_halved[0] = logDensity()/2;
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
    if (!rand.nextBernoulli(Math.min(1,Math.exp(logDensity()-log_Pi)*Qji/Qij))) {
      Collections.swap(permutation.getConnections(),idx_swapped[k][0],idx_swapped[k][1]); 
    }
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
