#!/bin/bash
#SBATCH --account=def-bouchar3
#SBATCH --time=4:00:00
#SBATCH --job-name=perm
#SBATCH --mem=999999M
#SBATCH --output=%x-%j.out
module load java
nextflow run -resume permuted-clustering.nf
