#!/usr/bin/env nextflow

deliverableDir = 'deliverables/' + workflow.scriptName.replace('.nf','')

nGroups = 2
minGroupSize = 3
maxGroupSize = 5

process build {
  cache false
  output:
    file 'jars_hash' into jars_hash
    file 'classpath' into classpath    
  """
  set -e
  current_dir=`pwd`
  cd ../../../..
  ./gradlew build
  ./gradlew printClasspath | grep CLASSPATH-ENTRY | sort | sed 's/CLASSPATH[-]ENTRY //' > \$current_dir/temp_classpath
  for file in `ls build/libs/*jar`
  do
    echo `pwd`/\$file >> \$current_dir/temp_classpath
  done
  cd -
  touch jars_hash
  for jar_file in `cat temp_classpath`
  do
    shasum \$jar_file >> jars_hash
  done
  cat temp_classpath | paste -sd ":" - > classpath
  """
}

jars_hash.into {
  jars_hash1
  jars_hash2
}

classpath.into {
  classpath1
  classpath2
  classpath3
}

process generateData {
  cache 'deep'
  input:
    each i from minGroupSize..maxGroupSize
    file classpath1
    file jars_hash1
  output:
    file "generated$i" into data
  """
  set -e
  java -cp `cat classpath` -Xmx2g matchings.PermutedClustering \
    --experimentConfigs.managedExecutionFolder false \
    --experimentConfigs.saveStandardStreams false \
    --experimentConfigs.recordExecutionInfo false \
    --experimentConfigs.recordGitInfo false \
    --model.nGroups $nGroups \
    --model.groupSize $i \
    --engine Forward
  mv samples generated$i
  """
}

process runInference {
  cache 'deep'
  input:
    each i from minGroupSize..maxGroupSize
    file data from data.collect()
    file classpath2
    file jars_hash2
  output:
    file "samples/permutations${i}.csv" into permutations
    file "monitoring/runningTimeSummary${i}.tsv" into runningTimeSummary
  """
  set -e 
  tail -n +2 generated${i}/observations.csv | awk -F "," '{print \$2, ",", \$3, ",", \$4}' | sed 's/ //g' > data.csv
  java -cp `cat classpath` -Xmx2g matchings.PermutedClustering \
    --initRandom 123 \
    --experimentConfigs.managedExecutionFolder false \
    --experimentConfigs.saveStandardStreams false \
    --experimentConfigs.recordExecutionInfo false \
    --experimentConfigs.recordGitInfo false \
    --model.nGroups $nGroups \
    --model.groupSize $i \
    --model.observations.file data.csv \
    --engine PT \
    --engine.nScans 2_000 \
    --engine.nThreads MAX \
    --engine.nChains 8
  mv samples/permutations.csv samples/permutations${i}.csv
  mv monitoring/runningTimeSummary.tsv monitoring/runningTimeSummary${i}.tsv
  """   
}

process calculateESS {
  cache 'deep'
  input:
    each i from minGroupSize..maxGroupSize
    file permutations from permutations.collect()
    file runningTimeSummary from runningTimeSummary.collect()
    file classpath3
  output:
    file "executionInfo/ess_per_sec${i}.txt" into ess_per_sec
  """
  set -e
  java -cp `cat classpath` -Xmx2g matchings.PermutationESS \
    --csvFile permutations${i}.csv \
    --groupSize $i \
    --nGroups $nGroups \
    --runtime 1
  mv executionInfo/stdout.txt executionInfo/ess_per_sec${i}.txt
  """
} 

