package matchings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.FluentIterable;
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
//    System.out.println("nGroups,groupSize,whichGroup,testFrom,testTo,ess_per_sec");
//    List<Double> samples = new ArrayList<>(); 
//    for (int i=0;i<nGroups*groupSize;i++) {
//      for (int j=0;j<groupSize;j++) {
//        int k=0;
//        samples.clear();
//        for (Map<String,String> line : BriefIO.readLines(csvFile).indexCSV().skip(0)) {
//          if (k%(groupSize*nGroups)==i%groupSize) 
//            samples.add(Integer.parseInt(line.get("value").trim())==j ? 1. : 0.);
//          k++;
//        }
//        System.out.format("%d,%d,%d,%d,%d,%f\n",nGroups,groupSize,i/groupSize,i%groupSize,j,EffectiveSampleSize.ess(samples)/runtime);
//      }
//    }
    
    System.out.println("nGroups,groupSize,whichGroup,testFrom,testTo,ess_per_sec");
    List<Double> samples = new ArrayList<>();
    List<Map<String,String>> data = Lists.newArrayList(BriefIO.readLines(csvFile).indexCSV().skip(0));
    int m = data.size();
    for (int i=0;i<nGroups;i++) {
      for (int j=0;j<groupSize;j++) {
        for (int k=0;k<groupSize;k++) {
          int l=i*nGroups+j;
          samples.clear();
          while (l<m) {
            samples.add(Integer.parseInt(data.get(l).get("value").trim())==k ? 1. : 0.);
            l+=groupSize*nGroups;
          }
          System.out.format("%d,%d,%d,%d,%d,%f\n",nGroups,groupSize,i,j,k,EffectiveSampleSize.ess(samples)/runtime);
        }
      }
    }
  }

  public static void main(String [] args) 
  {
    Experiment.startAutoExit(args);
  }
}