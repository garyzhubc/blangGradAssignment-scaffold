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
   * Implementation of Locally-balanced Proposal by G. Zanella 2017.
   */
  public void execute(Random rand) {
    double log_Pi = logDensity();
    double[] log_probs_nbh = new double[(permutation.componentSize()*(permutation.componentSize()-1))/2];
    update_log_probs_nbh(log_probs_nbh, log_Pi);
    double sum_log_probs_nbh = NumericalUtils.logAdd(log_probs_nbh);
    double[] probs_nbh = new double[(permutation.componentSize()*(permutation.componentSize()-1))/2];
    for (int i=0;i<(permutation.componentSize()*(permutation.componentSize()-1))/2;i++) 
      probs_nbh[i] = Math.exp(log_probs_nbh[i]-sum_log_probs_nbh);
    int fwd_idx = rand.nextCategorical(probs_nbh);
    double Qij = probs_nbh[fwd_idx];
    int bwd_idx = (int) ((-(2*(permutation.componentSize()+1)-3)+Math.sqrt(Math.pow(2*(permutation.componentSize()+1)-3,2)-8*fwd_idx))/(-2));
    Collections.swap(permutation.getConnections(),bwd_idx,fwd_idx-(2*permutation.componentSize()-bwd_idx-1)*bwd_idx/2+bwd_idx+1);
    double log_Pj = logDensity();
    update_log_probs_nbh(log_probs_nbh, log_Pj);
    sum_log_probs_nbh = NumericalUtils.logAdd(log_probs_nbh);
    double Qji = Math.exp(log_probs_nbh[fwd_idx]-sum_log_probs_nbh);
    if (!rand.nextBernoulli(Math.min(1,Math.exp(log_Pj-log_Pi+Math.log(Qji)-Math.log(Qij))))) 
      Collections.swap(permutation.getConnections(),bwd_idx,fwd_idx-(2*permutation.componentSize()-bwd_idx-1)*bwd_idx/2+bwd_idx+1);
  }
  
  /**
   * Update neighborhood log-probabilities
   */
  private void update_log_probs_nbh(double[] log_probs_nbh, double log_P) {
    int idx = 0;
    for (int i=0;i<permutation.componentSize();i++) {
      for (int j=i+1;j<permutation.componentSize();++j) {
        Collections.swap(permutation.getConnections(),i,j);
        log_probs_nbh[idx] = bal_fun(log_P);
        Collections.swap(permutation.getConnections(),i,j);
        idx++;
      }
    }
  }
  
  /**
   * Balancing function
   */
  private double bal_fun(double log_P) {
//    return Math.min(0,logDensity()-log_P);  
//    return logDensity()-NumericalUtils.logAdd(logDensity(),log_P); 
    return logDensity()/2.0;  
//    return Math.max(0,logDensity()-log_P);  
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
