package matchings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;
import briefj.collections.UnorderedPair;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class PermutationSampler implements Sampler {
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
  public void execute(Random rand) {
    // copy old
    List<Integer> conn = permutation.getConnections();
    double logprobpi = logDensity();
    
    // get neighbourhood
    NeighbourhoodSpecifics nbs = getNeighbourhoodSpecifics();
    List<Permutation> perms = nbs.getPerms();
    List<Double> logprobs = nbs.getLogprobs();
    
    // normalize
    double[] primprobs = normalize(logprobs);
    
    // sample
    int j = rand.nextCategorical(primprobs);
    double probQij = primprobs[j];
    double logprobpj = logprobs.get(j);
    Permutation perm = perms.get(j);
    
    // calculate reverse probabilities
    permutation.getConnections().clear();
    permutation.getConnections().addAll(perm.getConnections());
    
    // get new neighbourhood
    nbs = getNeighbourhoodSpecifics();
    perms = nbs.getPerms();
    logprobs = nbs.getLogprobs();
    primprobs = normalize(logprobs);
    
    // normalize
    int i = 0;
    while (i<perms.size()) {
    		if (perms.get(i).equals(perm)) {
    			break;
    		}
    		i++;
    }
    primprobs = normalize(logprobs);
    double probQji = primprobs[j];
    
    // accept-reject
    double alpha = Math.min(1,Math.exp(logprobpj-logprobpi)*probQji/probQij);
    boolean p = rand.nextBernoulli(alpha);
    
    if (!p) {
    		permutation.getConnections().clear();
    		permutation.getConnections().addAll(conn);
    }  
}
  private final class NeighbourhoodSpecifics {
	    private final List<Permutation> perms;
	    private final List<Double> logprobs;
	    public NeighbourhoodSpecifics(List<Permutation> first, List<Double> second) {
	        this.perms = first;
	        this.logprobs = second;
	    }
	    public List<Permutation> getPerms() {
	        return perms;
	    }
	    public List<Double> getLogprobs() {
	        return logprobs;
	    }
	}
  
  private NeighbourhoodSpecifics getNeighbourhoodSpecifics() {
	    List<Permutation> perms = new ArrayList<Permutation>();
	    List<Double> logprobs = new ArrayList<Double>();
	    
	    // load itself
	    Permutation self = new Permutation(permutation.componentSize());
	    self.getConnections().clear();
	    self.getConnections().addAll(permutation.getConnections());
	    perms.add(self);
	    logprobs.add(logDensity());
	    
	    // collect neighbours obtained by swap
	    for (int i=0;i<permutation.componentSize();i++) {
	    		for (int j=i+1;j<permutation.componentSize();++j) {
	    			Collections.swap(permutation.getConnections(), i, j);
	    			Permutation perm = new Permutation(permutation.componentSize());
	    			perm.getConnections().clear();
	    			perm.getConnections().addAll(permutation.getConnections());
	    			perms.add(perm);
	    			logprobs.add(new Double(logDensity()));
	    			Collections.swap(permutation.getConnections(), i, j);
	    		}
	    	}
	    return new NeighbourhoodSpecifics(perms,logprobs);
  }
  
  private double[] normalize(List<Double> logprobs) {
	    List<Double> probs = new ArrayList<Double>();
	    Double min = Collections.min(logprobs);
	    for (Double logprob: logprobs) {
	    		probs.add(Math.exp((logprob-min)/2));
	    }
	    double sum = 0;
	    for (Double prob: probs) {
	    		sum += prob;
	    }
	    for (int i=0;i<probs.size();i++) {
	    		probs.set(i, new Double(probs.get(i).doubleValue()/sum));
	    }
	    double[] prim_probs = new double[probs.size()];
	    for (int i = 0; i < prim_probs.length; i++) {
	    		prim_probs[i] = probs.get(i);                
	    }
	    return prim_probs;
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}