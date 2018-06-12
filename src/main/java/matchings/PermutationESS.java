package matchings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bayonet.math.EffectiveSampleSize;
import blang.inits.Arg;
import blang.inits.DefaultValue;
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
  
  @Arg 
  Optional<String> field = Optional.empty();
  
  @Arg @DefaultValue("1")
  int moment        = 1;

  @Override
  public void run() 
  {
    List<Double> samples = new ArrayList<>();
    int i = 0;
    for (Map<String,String> line : BriefIO.readLines(csvFile).indexCSV().skip(0)) {
      if (i%(groupSize*nGroups)==0) {
        if (Integer.parseInt(line.get("value").trim())==0) {
          samples.add(1.);
        } else {
          samples.add(0.);
        }
      }
      i++;
    }
    
    System.out.println(moment == 1 ?
      EffectiveSampleSize.ess(samples)/runtime :
      EffectiveSampleSize.ess(samples, x -> Math.pow(x, moment))/runtime);
  }

  public static void main(String [] args) 
  {
    Experiment.startAutoExit(args);
  }
}