package matchings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import bayonet.math.EffectiveSampleSize;
import blang.inits.Arg;
import blang.inits.experiments.Experiment;
import briefj.BriefIO;

/**
 * Implementation of https://github.com/alexandrebouchard/nedry/blob/master/src/main/java/mcli/ComputeESS.java
 */
public class PermutationESS extends Experiment 
{
  /**
   * directory of permutations.csv
   */
  @Arg 
  File csvFile;
  /**
   * size of each permutation
   */
  @Arg
  int groupSize;
  /**
   * number of permutations
   */
  @Arg
  int nGroups;
  /**
   * sampling time in millisecond
   */
  @Arg
  double samp_time;
  
  /**
   * returns ESS per second for all possible test functions that checks if a component is matched with another component
   */
  @Override
  public void run() 
  { 
    System.out.println("nGroups,groupSize,whichGroup,testFrom,testTo,essps");
    List<Double> samples = new ArrayList<>();
    List<Map<String,String>> data = Lists.newArrayList(BriefIO.readLines(csvFile).indexCSV().skip(0));
    for (int i=0;i<nGroups;i++) {
      for (int j=0;j<groupSize;j++) {
        for (int k=0;k<groupSize;k++) {
          samples.clear();
          for (int l=i*nGroups+j;l<data.size();l+=groupSize*nGroups) 
            samples.add(Integer.parseInt(data.get(l).get("value").trim())==k ? 1. : 0.);
          System.out.format("%d,%d,%d,%d,%d,%f\n",nGroups,groupSize,i,j,k,EffectiveSampleSize.ess(samples)/samp_time*1000);
        }
      }
    }
  }

  public static void main(String [] args) 
  {
    Experiment.startAutoExit(args);
  }
}