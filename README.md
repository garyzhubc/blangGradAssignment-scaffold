[![Build Status](https://travis-ci.org/garyzhubc/blangGradAssignment-scaffold.png?branch=master)](https://travis-ci.org/garyzhubc/blangGradAssignment-scaffold)

# Assessing MCMC Scalability Using Blang

## Overview

- `Permutation.xtend` Permutations linked to Locally Balanced sampler
- `PermutationSamplerLocallyBalanced.java` Locally Balanced sampler
- `PermutationSampler.java` Naive Metropolis sampler
- `PermutationESS.java` Calculate ESS per iteraiton 
- `PermutedClustering.bl` Anynomous game model 

## Setup

- Click on `Fork` (top right).
- Clone the forked repo into your Blang IDE's workspace folder.
- `cd` into the cloned repo, then type `./gradlew eclipse`.
- Import the project into eclipse.

## Run Pipe-line

- Install [nedry](https://github.com/alexandrebouchard/nedry)
- `cd` into nextflow, then `./nextflow run permuted-clustering.nf -resume | nf-monitor --open true` 
