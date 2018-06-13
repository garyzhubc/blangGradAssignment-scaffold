require(ggplot2)
data = read.csv("ess_per_sec_aggregated.csv",header = TRUE)
p = ggplot(data, aes(x=groupSize, y=log(ess_per_sec), group=groupSize)) +
  geom_boxplot()
ggsave("ess_per_sec.pdf")