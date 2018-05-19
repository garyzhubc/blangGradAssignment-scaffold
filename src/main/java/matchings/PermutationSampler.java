    // encapsulated
//    List<Permutation> perms = new ArrayList<Permutation>();
//    List<Double> logprobs = new ArrayList<Double>();
//    for (int i=0;i<permutation.componentSize();i++) {
//    		for (int j=0;j<permutation.componentSize();++j) {
//    			if (j==i && j!=permutation.componentSize()-1) j++;
//    			Collections.swap(permutation.getConnections(), i, j);
//    			Permutation perm = new Permutation(permutation.componentSize());
//    			perm.getConnections().clear();
//    			perm.getConnections().addAll(permutation.getConnections());
//    			perms.add(perm);
//    			System.out.print(perm.getConnections());
//    			System.out.println(logDensity());
//    			logprobs.add(new Double(logDensity()));
//    			Collections.swap(permutation.getConnections(), i, j);
//    		}
//    	}

    // encapsulated
//    List<Double> probs = new ArrayList<Double>();
//    Double min = Collections.min(logprobs);
//    for (Double logprob: logprobs) {
//    		probs.add(Math.exp((logprob-min)/2));
//    }
//    double sum = 0;
//    for (Double prob: probs) {
//    		sum += prob;
//    }
//    for (Double prob: probs) {
//		prob = prob/sum;
//    }
//    double[] prim_probs = new double[probs.size()];
//    for (int i = 0; i < prim_probs.length; i++) {
//    		prim_probs[i] = probs.get(i);                
//    }

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
//    /* startRem // Fill this. */
//    UnorderedPair<Integer, Integer> pair = Generators.distinctPair(rand, permutation.componentSize());
//    double logDensityBefore = logDensity();
//    Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
//    double logDensityAfter = logDensity();
//    double acceptPr = Math.min(1.0, Math.exp(logDensityAfter - logDensityBefore)); 
//    if (Generators.bernoulli(rand, acceptPr))
//      ;
//    else
//      Collections.swap(permutation.getConnections(), pair.getFirst(), pair.getSecond());
//    /* endRem */
	  
	  
	// How to start execute?
    // How to make new sampler, and its tests?
	  
	System.out.println("new iteration");
    
    // copy old
    List<Integer> conn = permutation.getConnections();
    double logprobpi = logDensity();
    
    System.out.println(conn);
    
    // get neighbourhood
    NeighbourhoodSpecifics nbs = getNeighbourhoodSpecifics();
    List<Permutation> perms = nbs.getPerms();
    List<Double> logprobs = nbs.getLogprobs();
    
    System.out.println(perms);
    System.out.println(logprobs);
    
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
    
    System.out.format("i=%d%nj=%d%n",i,j);
    
    primprobs = normalize(logprobs);
    double probQji = primprobs[j];
    
    System.out.format("logprobpj=%f%nlogprobpi=%f%nprobQji=%f%nprobQij=%f%n",logprobpj,logprobpi,probQji,probQij);
    
    // accept-reject
    double alpha = Math.min(1,Math.exp(logprobpj-logprobpi)*probQji/probQij);
    boolean p = rand.nextBernoulli(alpha);
    
    System.out.println(alpha);
    
    if (!p) {
    		System.out.println("reject proposal");
    		permutation.getConnections().clear();
    		permutation.getConnections().addAll(conn);
    }
    
    System.out.println(permutation.getConnections());
  }
  
  
  
  
  
  
  // encapsulated:
  
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
	    for (Double prob: probs) {
			prob = prob/sum;
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