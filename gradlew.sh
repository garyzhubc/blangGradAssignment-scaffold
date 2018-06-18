#!/bin/bash
#SBATCH --account=def-bouchar3
#SBATCH --time=1:00:00
#SBATCH --job-name=gradlew-build
#SBATCH --mem=8000M
#SBATCH --output=%x-%j.out
./gradlew build
