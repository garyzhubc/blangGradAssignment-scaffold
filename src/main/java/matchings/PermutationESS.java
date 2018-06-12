package matchings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bayonet.math.EffectiveSampleSize;
import blang.inits.Arg;
import blang.inits.experiments.Experiment;
import briefj.BriefIO;

/**
 * Implementation of https://github.com/alexandrebouchard/nedry/blob/master/src/main/java/mcli/ComputeESS.java
 */
public class PermutationESS extends Experiment 
{
  @Arg 
  File csvFile;
  
  @Arg
  int groupSize;
  
  @Arg
  int nGroups;
  
  @Arg
  double runtime;
  
  @Override
  public void run() 
  {
    for (int i=0;i<groupSize;i++) {
      for (int j=0;j<groupSize;j++) {
        List<Double> samples = new ArrayList<>();
        int k = 0;
        for (Map<String,String> line : BriefIO.readLines(csvFile).indexCSV().skip(0)) {
          if (k%(groupSize*nGroups)==i) 
            samples.add(Integer.parseInt(line.get("value").trim())==j ? 1. : 0.);
          k++;
        }
        System.out.println(EffectiveSampleSize.ess(samples)/runtime);
      }
    }
  }

  public static void main(String [] args) 
  {
    Experiment.startAutoExit(args);
  }
}