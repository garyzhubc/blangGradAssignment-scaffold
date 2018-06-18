#!/bin/bash
#SBATCH --account=def-bouchar3
#SBATCH --time=24:00:00
#SBATCH --job-name=perm
#SBATCH --mem=999999M
#SBATCH --output=%x-%j.out
nextflow run -resume permuted-clustering.nf
