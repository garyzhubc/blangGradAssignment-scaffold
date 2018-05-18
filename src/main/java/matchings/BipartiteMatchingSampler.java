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
//	  System.out.print("\nnew iteration\n");
//	  System.out.print(matching.getConnections());
//	  System.out.print("\n");
	  
//	  matching.sampleUniform(rand);
//	  System.out.print(matching.getConnections()); // [-1, 2, 3, -1, -1]
//	  System.out.print(matching.free1()); // [0, 3, 4]
//	  System.out.print(matching.free2()); // [0, 1, 4]
//	  System.out.print("\n");
	  // empty matching - add one edge [-1 -1 -1] -> [2 -1 -1] : choose i uniformly then j uniformly, set i-th entry to j
	  // partial matching - add or delete one edge (or swap?) [2 3 -1] -> [-1 3 -1] or [-1 2 -1] (or [3 2 -1]?) : add or delete uniformly
	  // full matching - delete one edge (or swap?) [0 2 1] -> [-1 2 1] (or [0 1 2]?): choose i uniformly and set to -1
	  
	  // make copy
	  double log_prob_o = logDensity();
	  List<Integer> conn_o = new ArrayList<Integer>(matching.getConnections());
	  
	  // set quantities
	  List<Integer> conn = matching.getConnections();
	  List<Integer> fr1 = matching.free1();
	  List<Integer> fr2 = matching.free2();
	  int n = matching.componentSize();
	  int k = matching.free1().size();
	  int m = n-k;
	  List<Integer> unfr1 = new ArrayList<Integer>();
	  for (int p=0;p<n;p++) {
		  if (conn.get(p)!=-1)
			  unfr1.add(p);
	  }
//	  System.out.print(unfr1);
//	  System.out.print(n);
//	  System.out.print(k);
//	  System.out.print(m);
//	  System.out.print('\n');
	  
	  double log_prob_otn;
	  double log_prob_nto;
	  int i;
	  int j;
	  int l;
	  int q;
	  int s;
	  int t;
	  
	  // todo: simplify three cases to one
	  if (k==n) {
		  // empty matching
		  System.out.print("empty matching\n");
		  i = rand.nextInt(n);
		  j = rand.nextInt(n);
		  conn.set(i,j);
		  log_prob_otn = Math.log(1/Math.pow(n,2)); // todo: check
		  log_prob_nto = Math.log(1/(Math.pow(n-1,2)+1)); // todo: check
	  } else if (k==0) {
		  // full matching
		  System.out.print("full matching\n");
		  i = rand.nextInt(n);
		  conn.set(i,-1);
		  // both equals 1/n
		  log_prob_otn = 0; // todo: check
		  log_prob_nto = 0; // todo: check
	  } else {
		  // partial matching
		  System.out.print("partial matching\n");
//		  System.out.printf("k=%d\n",k);
//		  System.out.printf("m=%d\n",m);
		  i = rand.nextInt((int) Math.pow(k,2)+m);
//		  System.out.printf("k^2+m=%d\n",k^2+m);
//		  System.out.printf("i=%d\n",i);
//		  System.out.printf("m-1=%d\n",m-1);
		  if (i<=m-1) {
			  // delete an edge
			  // need: indices that are not -1 
			  System.out.print("delete an edge\n");
			  j = unfr1.get(i);
			  conn.set(i,-1);
			  // total: k^2+m
			  log_prob_otn = Math.log(1/(Math.pow(k,2)+m)); // todo: check
			  log_prob_nto = Math.log(1/(Math.pow(k-1,2)+m+1)); // todo: check
		  } else {
			  // add an edge
			  System.out.print("add an edge\n");
//			  System.out.printf("i=%d\n",i);
			  i = i-m;
//			  System.out.printf("i_special=%d\n",i);
//			  System.out.printf("k=%d\n",k);
			  s = i/k;
			  j = i%k;
//			  System.out.printf("s=%d\n",s);
//			  System.out.printf("j=%d\n",j);
//			  System.out.print(fr1);
//			  System.out.print("\n");
//			  System.out.print(fr2);
//			  System.out.print("\n");
			  l = fr1.get(s);
			  q = fr2.get(j);
//			  System.out.printf("l=%d\n",l);
//			  System.out.printf("q=%d\n",q);
			  conn.set(l,q);
			  log_prob_otn = Math.log(1/(Math.pow(k,2)+m)); // todo: check
			  log_prob_nto = Math.log(1/(Math.pow(k+1,2)+m-1)); // todo: check
		  }
	  }
	  // accept or reject
	  double log_prob_n = logDensity();
//	  System.out.printf("log_prob_n=%f\n",log_prob_n);
//	  System.out.printf("log_prob_o=%f\n",log_prob_o);
//	  System.out.printf("log_prob_nto=%f\n",log_prob_nto);
//	  System.out.printf("log_prob_otn=%f\n",log_prob_otn);

	  double alpha = Math.min(1,Math.exp(log_prob_n-log_prob_o+log_prob_nto-log_prob_otn));
//	  System.out.printf("alpha=%f\n",alpha);
	  boolean d = rand.nextBernoulli(alpha);
	  if (!d) {
		  // if don't accept, restore old connections
		  conn = conn_o;
//		  System.out.print(conn);
//		  System.out.print("\ndon't accept\n");
	  } 
//	  else {
//		  System.out.print(conn);
//		  System.out.print("\n");
//		  System.out.print("accept\n");
//	  }
//	  System.out.print(conn);
//	  System.out.print("\n");
  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
