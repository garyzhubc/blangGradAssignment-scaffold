package matchings;

import java.util.List;

import bayonet.distributions.Random;

import blang.core.LogScaleFactor;
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
  /**
   * This is an implementation of Metropolis-Hasting algorithm with uniform proposals:
   * - If the the match is empty, then match i,j uniformly
   * - If the match is not full, then add or delete an edge uniformly
   * - If the match is full,then delete one edge uniformly
   */
  public void execute(Random rand) {
    double log_prob_o = logDensity();
    int n = matching.componentSize(); int k = matching.free1().size(); 
    int j = Integer.MIN_VALUE; int l = Integer.MIN_VALUE;
    double log_prob_otn,log_prob_nto;    
    int i = rand.nextInt(k*k+n-k);
    if (k!=0) {
      if (i<=n-k-1) {
        l = matching.engaged1().get(i);
        j = matching.getConnections().get(l);
        matching.getConnections().set(l,BipartiteMatching.FREE);
        log_prob_otn = -Math.log(Math.pow(k,2)+n-k); log_prob_nto = -Math.log(Math.pow(k+1,2)+n-k-1); 
      } else {
        matching.getConnections().set(matching.free1().get((i-n+k)/k),matching.free2().get((i-n+k)%k));
        log_prob_otn = -Math.log(Math.pow(k,2)+n-k); log_prob_nto = -Math.log(Math.pow(k-1,2)+n-k+1); 
      }
    } else {
      j = matching.getConnections().get(i);
      matching.getConnections().set(i,BipartiteMatching.FREE);
      log_prob_otn = 0; log_prob_nto = 0; 
    }
    if (!rand.nextBernoulli(Math.min(1,Math.exp(logDensity()-log_prob_o+log_prob_nto-log_prob_otn)))) {
      if (k!=0) {
        if (i<=n-k-1) 
          matching.getConnections().set(l,j); 
        else  
          matching.getConnections().set(matching.free1().get((i-n+k)/k),matching.free2().get((i-n+k)%k));
      } else {
        matching.getConnections().set(i,j);
      }
    }
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
