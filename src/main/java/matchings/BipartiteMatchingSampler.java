package matchings;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
//	  System.out.print(matching.getConnections());
//	  matching.sampleUniform(rand);
//	  System.out.print(matching.getConnections());
//	  System.out.print("\n");
	  List<Integer> conn = matching.getConnections();
	  List<Integer> fr1 = matching.free1();
	  List<Integer> fr2 = matching.free2();
	  int n = conn.size();
	  int k = fr1.size();
	  int m = n-k;
//	  System.out.print(factorial(3));
	  double logprob_old = logDensity();
	  List<Integer> conn_old = new ArrayList<Integer>(conn);
	  // copy the right way?
//	  Collections.copy(conn_old, conn);
//	  for (int i=0;i<n;i++) {
//		   conn_old.add(conn.get(i));
//	  }
	  int i = rand.nextInt(n-1);
//	  int j = rand.nextInt(n-2);
//	  if (i>=j)
//		  j += 1;
	  int j = rand.nextInt(n-1);
	  int ith_val = conn.get(i);
	  int jth_val = conn.get(j);
	  double logprob_nto;
	  double logprob_otn;
	  System.out.print(conn);
	  System.out.print(i);
	  System.out.print(j);
	  if (ith_val==BipartiteMatching.FREE) {
		  if (jth_val==BipartiteMatching.FREE) {
			  // both free: match i with j
			  conn.set(i,jth_val);
//			  logprob_otn = 1/binom(n,2);
//			  logprob_nto = ;
			  System.out.print("action1");
		  } else {
			  // only i is free: set j free
			  conn.set(j,BipartiteMatching.FREE);
//			  logprob_otn = 1/binom(n,2);
//			  logprob_nto = ;
			  System.out.print("action2");
		  }
	  } else {
		  if (jth_val==BipartiteMatching.FREE) {
			  // only j is free: set i free
			  conn.set(i,BipartiteMatching.FREE);
//			  logprob_otn = 1/binom(n,2);
//			  logprob_nto = ;
			  System.out.print("action3");
		  } else {
			  // both are unfree: swap(i,j)
			  rand.nextBernoulli(.5);
			  Collections.swap(conn,i,j);
//			  logprob_otn = 1/binom(n,2);
//			  logprob_nto = ;
			  System.out.print("action4");
		  }
	  }
//	  double alpha = Math.min(1,Math.exp(logDensity()-logprob_old)*Math.exp(logprob_nto-logprob_otn));
	  double alpha = Math.min(1,Math.exp(logDensity()-logprob_old));
	  if (!rand.nextBernoulli(alpha)) {
		  conn = conn_old;
		  System.out.print("no action");
	  } else {
		  System.out.print("action");
	  }
	  System.out.print(conn);
	  System.out.print("\n");
  }
  
  private double binom(int n, int k) {
	  // to be lognormalized
	  if (k>n)
		  throw new ArithmeticException("binom(n,k) where k>n!");
	  return factorial(n)/(factorial(k)*factorial(n-k));
  }
  
  private int factorial(int n) {
	  // to be lognormalized
	  if (n<0)
		  throw new ArithmeticException("factorial <0!");
	  int prod = 1;
	  for (int i=2;i<=n;i++)
		  prod *= i;
	  return prod;
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
