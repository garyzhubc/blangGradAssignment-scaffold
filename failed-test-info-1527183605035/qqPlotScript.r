#!/usr/bin/env Rscript
require("ggplot2")
require("readr")
f  <- sort((read_csv("PermutedClustering.ALL.TestPermutedClustering$$Lambda$6-1028060977.fData.csv", col_names = FALSE))$X1)
fp <- sort((read_csv("PermutedClustering.ALL.TestPermutedClustering$$Lambda$6-1028060977.fpData.csv", col_names = FALSE))$X1)
plot <- ggplot() + geom_point(aes(x = f, y = fp), size = 0.001) + geom_abline(intercept = 0, slope = 1)
ggsave("PermutedClustering.ALL.TestPermutedClustering$$Lambda$6-1028060977.plot.pdf", plot)
