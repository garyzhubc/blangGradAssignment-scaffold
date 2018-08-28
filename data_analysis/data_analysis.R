# TODO: batch process everything
# TODO: save Ising plot
# TODO: make function and reuse code if possible

#/////////////////////////////////////////////////////////////////////
#### run this before everything
#/////////////////////////////////////////////////////////////////////

setwd("/Users/garyzhu/Desktop/blang/deliverables")

#/////////////////////////////////////////////////////////////////////
#### power from 1 to 1/9
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

lb1 = read.csv("esspi/ess_per_iter_aggregated_1.csv")
lb2 = read.csv("esspi/ess_per_iter_aggregated_2.csv")
lb3 = read.csv("esspi/ess_per_iter_aggregated_3.csv")
lb4 = read.csv("esspi/ess_per_iter_aggregated_4.csv")
lb5 = read.csv("esspi/ess_per_iter_aggregated_5.csv")
lb6 = read.csv("esspi/ess_per_iter_aggregated_6.csv")
lb7 = read.csv("esspi/ess_per_iter_aggregated_7.csv")
lb8 = read.csv("esspi/ess_per_iter_aggregated_8.csv")
lb9 = read.csv("esspi/ess_per_iter_aggregated_9.csv")

lb1 = na.omit(lb1)
lb2 = na.omit(lb2)
lb3 = na.omit(lb3)
lb4 = na.omit(lb4)
lb5 = na.omit(lb5)
lb6 = na.omit(lb6)
lb7 = na.omit(lb7)
lb8 = na.omit(lb8)
lb9 = na.omit(lb9)

lb1_agg = aggregate(lb1["esspi"], list(lb1$groupSize), mean)
lb2_agg = aggregate(lb2["esspi"], list(lb2$groupSize), mean)
lb3_agg = aggregate(lb3["esspi"], list(lb3$groupSize), mean)
lb4_agg = aggregate(lb4["esspi"], list(lb4$groupSize), mean)
lb5_agg = aggregate(lb5["esspi"], list(lb5$groupSize), mean)
lb6_agg = aggregate(lb6["esspi"], list(lb6$groupSize), mean)
lb7_agg = aggregate(lb7["esspi"], list(lb7$groupSize), mean)
lb8_agg = aggregate(lb8["esspi"], list(lb8$groupSize), mean)
lb9_agg = aggregate(lb9["esspi"], list(lb9$groupSize), mean)

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  labs(color="n") +
  geom_point(data=lb1_agg, aes(x=log(lb1_agg$Group.1), y=log(lb1_agg$esspi), colour='1')) +
  geom_point(data=lb2_agg, aes(x=log(lb2_agg$Group.1), y=log(lb2_agg$esspi), colour='2')) +
  geom_point(data=lb3_agg, aes(x=log(lb3_agg$Group.1), y=log(lb3_agg$esspi), colour='3')) +
  geom_point(data=lb4_agg, aes(x=log(lb4_agg$Group.1), y=log(lb4_agg$esspi), colour='4')) +
  geom_point(data=lb5_agg, aes(x=log(lb5_agg$Group.1), y=log(lb5_agg$esspi), colour='5')) +
  geom_point(data=lb6_agg, aes(x=log(lb6_agg$Group.1), y=log(lb6_agg$esspi), colour='6')) +
  geom_point(data=lb7_agg, aes(x=log(lb7_agg$Group.1), y=log(lb7_agg$esspi), colour='7')) +
  geom_point(data=lb8_agg, aes(x=log(lb8_agg$Group.1), y=log(lb8_agg$esspi), colour='8')) +
  geom_point(data=lb9_agg, aes(x=log(lb9_agg$Group.1), y=log(lb9_agg$esspi), colour='9'))
plot

ggsave("esspi/esspi.pdf")

#/////////////////////////////////////////////////////////////////////
#### groupSize from 3 to 9, single permutation
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

lbg3 = read.csv("esspig/ess_per_iter_aggregated_g3.csv")
lbg4 = read.csv("esspig/ess_per_iter_aggregated_g4.csv")
lbg5 = read.csv("esspig/ess_per_iter_aggregated_g5.csv")
lbg6 = read.csv("esspig/ess_per_iter_aggregated_g6.csv")
lbg7 = read.csv("esspig/ess_per_iter_aggregated_g7.csv")
lbg8 = read.csv("esspig/ess_per_iter_aggregated_g8.csv")
lbg9 = read.csv("esspig/ess_per_iter_aggregated_g9.csv")

lbg3 = na.omit(lbg3)
lbg4 = na.omit(lbg4)
lbg5 = na.omit(lbg5)
lbg6 = na.omit(lbg6)
lbg7 = na.omit(lbg7)
lbg8 = na.omit(lbg8)
lbg9 = na.omit(lbg9)

lbg3_agg = aggregate(lbg3["esspi"], list(lbg3$groupSize), mean)
lbg4_agg = aggregate(lbg4["esspi"], list(lbg4$groupSize), mean)
lbg5_agg = aggregate(lbg5["esspi"], list(lbg5$groupSize), mean)
lbg6_agg = aggregate(lbg6["esspi"], list(lbg6$groupSize), mean)
lbg7_agg = aggregate(lbg7["esspi"], list(lbg7$groupSize), mean)
lbg8_agg = aggregate(lbg8["esspi"], list(lbg8$groupSize), mean)
lbg9_agg = aggregate(lbg9["esspi"], list(lbg9$groupSize), mean)

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  labs(color="nGroups") +
  geom_point(data=lbg3_agg, aes(x=log(lbg3_agg$Group.1), y=log(lbg3_agg$esspi), colour='3')) +
  geom_point(data=lbg4_agg, aes(x=log(lbg4_agg$Group.1), y=log(lbg4_agg$esspi), colour='4')) +
  geom_point(data=lbg5_agg, aes(x=log(lbg5_agg$Group.1), y=log(lbg5_agg$esspi), colour='5')) +
  geom_point(data=lbg6_agg, aes(x=log(lbg6_agg$Group.1), y=log(lbg6_agg$esspi), colour='6')) +
  geom_point(data=lbg7_agg, aes(x=log(lbg7_agg$Group.1), y=log(lbg7_agg$esspi), colour='7')) +
  geom_point(data=lbg8_agg, aes(x=log(lbg8_agg$Group.1), y=log(lbg8_agg$esspi), colour='8')) +
  geom_point(data=lbg9_agg, aes(x=log(lbg9_agg$Group.1), y=log(lbg9_agg$esspi), colour='9'))
  
plot
ggsave('esspig/esspig.pdf')

#/////////////////////////////////////////////////////////////////////
#### balancing function: max, min, sqrt, baker, naive
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

lbmin = na.omit(read.csv("esspibf/ess_per_iter_min.csv"))
lbmax = na.omit(read.csv("esspibf/ess_per_iter_max.csv"))
lbbaker = na.omit(read.csv("esspibf/ess_per_iter_baker.csv"))
lbsqrt = na.omit(read.csv("esspibf/ess_per_iter_sqrt.csv"))

lbmin = aggregate(lbmin["esspi"], list(lbmin$groupSize), mean)
lbmax = aggregate(lbmax["esspi"], list(lbmax$groupSize), mean)
lbbaker = aggregate(lbbaker["esspi"], list(lbbaker$groupSize), mean)
lbsqrt = aggregate(lbsqrt["esspi"], list(lbsqrt$groupSize), mean)

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  geom_point(data=lbmin, aes(x=log(lbmin$Group.1), y=log(lbmin$esspi), colour='min')) +
  geom_point(data=lbmax, aes(x=log(lbmax$Group.1), y=log(lbmax$esspi), colour='max')) +
  geom_point(data=lbbaker, aes(x=log(lbbaker$Group.1), y=log(lbbaker$esspi), colour='baker')) +
  geom_point(data=lbsqrt, aes(x=log(lbsqrt$Group.1), y=log(lbsqrt$esspi), colour='sqrt'))
  
plot
ggsave('esspibf/esspibf.pdf')

#/////////////////////////////////////////////////////////////////////
#### esspi 100 to 150
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

large = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_lb.csv"))
large = subset(large, abs(mean - 0.5) < 0.4)
low_q = aggregate(large["esspi"], list(large$groupSize), quantile, probs=0.05)
high_q = aggregate(large["esspi"], list(large$groupSize), quantile, probs=0.95)
large_agg = aggregate(large["esspi"], list(large$groupSize), mean)
large_agg["low_q"] = low_q$esspi
large_agg["high_q"] = high_q$esspi

large1 = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_nlb.csv"))
large1 = subset(large1, abs(mean - 0.5) < 0.4)
low_q1 = aggregate(large1["esspi"], list(large1$groupSize), quantile, probs=0.05)
high_q1 = aggregate(large1["esspi"], list(large1$groupSize), quantile, probs=0.95)
large_agg1 = aggregate(large1["esspi"], list(large1$groupSize), mean)
large_agg1["low_q"] = low_q1$esspi
large_agg1["high_q"] = high_q1$esspi

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  labs(color='') +
  geom_ribbon(data=large_agg, aes(x=log(Group.1), ymax=log(high_q), ymin=log(low_q)), alpha=0.05, fill='red') +
  geom_point(data=large_agg, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi), colour='LB')) +
  geom_ribbon(data=large_agg1, aes(x=log(Group.1), ymax=log(high_q), ymin=log(low_q)), alpha=0.05, fill='blue') +
  geom_point(data=large_agg1, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi), colour='Naive')) 

plot <- plot + geom_smooth(data=large_agg, method=lm, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi)), colour='red', se=FALSE) + 
  geom_smooth(data=large_agg1, method=lm, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi)), color='blue', se=FALSE)

plot
ggsave('esspi_large.pdf')

fit <- lm(log(large_agg$esspi) ~ log(large_agg$Group.1), data=large_agg)
fit
fit1 <- lm(log(large_agg1$esspi) ~ log(large_agg1$Group.1), data=large_agg1)
fit1

#/////////////////////////////////////////////////////////////////////
#### check if slope is stable
#/////////////////////////////////////////////////////////////////////

N = 50
x = rep(0,N)
for (i in 2:N) {
  fit <- lm(log(tail(large_agg, i)$esspi) ~ log(tail(large_agg, i)$Group.1), data=tail(large_agg, -i))
  x[i] = fit$coefficients[2]
}
dat = data.frame(cbind(1:N,x))
plot <- ggplot() + 
  xlab("data size") +
  ylab("slope") +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2]))
plot
ggsave('slope_lb_2_to_50.pdf')


N = 50
y = rep(0,N)
for (i in 2:N) {
  fit <- lm(log(tail(large_agg1, i)$esspi) ~ log(tail(large_agg1, i)$Group.1), data=tail(large_agg1, -i))
  y[i] = fit$coefficients[2]
}
dat = data.frame(cbind(1:N,y))
plot <- ggplot() + 
  xlab("data size") +
  ylab("slope") +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2]))
plot
ggsave('slope_nlb_2_to_50.pdf')


#/////////////////////////////////////////////////////////////////////
#### esspi 150 to 200
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

large = na.omit(read.csv("esspi_larger/ess_per_iter_150_to_200_lb.csv"))
large = subset(large, abs(mean - 0.5) < 0.4)
low_q = aggregate(large["esspi"], list(large$groupSize), quantile, probs=0.05)
high_q = aggregate(large["esspi"], list(large$groupSize), quantile, probs=0.95)
large_agg = aggregate(large["esspi"], list(large$groupSize), mean)
large_agg["low_q"] = low_q$esspi
large_agg["high_q"] = high_q$esspi

large1 = na.omit(read.csv("esspi_larger/ess_per_iter_150_to_200_nlb.csv"))
large1 = subset(large1, abs(mean - 0.5) < 0.4)
low_q1 = aggregate(large1["esspi"], list(large1$groupSize), quantile, probs=0.05)
high_q1 = aggregate(large1["esspi"], list(large1$groupSize), quantile, probs=0.95)
large_agg1 = aggregate(large1["esspi"], list(large1$groupSize), mean)
large_agg1["low_q"] = low_q1$esspi
large_agg1["high_q"] = high_q1$esspi

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  labs(color='') +
  geom_ribbon(data=large_agg, aes(x=log(Group.1), ymax=log(high_q), ymin=log(low_q)), alpha=0.05, fill='red') +
  geom_point(data=large_agg, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi), colour='LB')) +
  geom_ribbon(data=large_agg1, aes(x=log(Group.1), ymax=log(high_q), ymin=log(low_q)), alpha=0.05, fill='blue') +
  geom_point(data=large_agg1, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi), colour='Naive')) 

plot <- plot + geom_smooth(data=large_agg, method=lm, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi)), colour='red', se=FALSE) + 
  geom_smooth(data=large_agg1, method=lm, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi)), color='blue', se=FALSE)

plot
ggsave('esspi_larger.pdf')

fit <- lm(log(large_agg$esspi) ~ log(large_agg$Group.1), data=large_agg)
fit
fit1 <- lm(log(large_agg1$esspi) ~ log(large_agg1$Group.1), data=large_agg1)
fit1

#/////////////////////////////////////////////////////////////////////
#### Ising visualization 
#/////////////////////////////////////////////////////////////////////

library(gridGraphics)
library(grid)
library(gridExtra)
library(gplots)

ising = read.csv("model_viz/Ising/vertices.csv")

n = 3
t = 2
size = n^2-1

grab_grob <- function(){
  grid.echo()
  grid.grab()
}

m = 5
gl <- lapply(seq(1,size*t*m,size), function(i){
  sub_ising = matrix(ising[i:(i+size),3],ncol=n,nrow=n)
  heatmap(sub_ising, Rowv = NA, Colv = NA, scale = "none", col=c("black", "white"), labRow = "", labCol = "")
  grab_grob()
})

grid.arrange(grobs=gl, ncol=t, clip=TRUE)


#/////////////////////////////////////////////////////////////////////
#### LDA tabulation
#/////////////////////////////////////////////////////////////////////

num_topics = 2
num_documents = 4
num_samples = 1000

phi = read.csv("model_viz/LDA/phi.csv")

i = 1
res = matrix(0,num_documents,num_topics)
for (i in 1:num_documents) {
  for (j in 1:num_topics) {
    ss = 0
    for (k in 1:num_samples) {
      ss = ss + phi[(k-1)*(num_topics*num_documents)+(i-1)*num_topics+j,4]
    }
    res[i,j] = ss/num_samples
  }
}
res

#/////////////////////////////////////////////////////////////////////
#### Worst-case 100 to 150
#/////////////////////////////////////////////////////////////////////


require(dplyr)
require(ggplot2)

large = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_lb.csv"))
# large = subset(large, abs(mean - 0.5) < 0.4)
large_agg = aggregate(large["esspi"], list(large$groupSize), min)

large1 = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_nlb.csv"))
# large1 = subset(large1, abs(mean - 0.5) < 0.4)
large_agg1 = aggregate(large1["esspi"], list(large1$groupSize), min)

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  labs(color='') +
  geom_point(data=large_agg, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi), colour='LB')) +
  geom_point(data=large_agg1, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi), colour='Naive')) 

plot <- plot + geom_smooth(data=large_agg, method=lm, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi)), colour='red', se=FALSE) + 
  geom_smooth(data=large_agg1, method=lm, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi)), color='blue', se=FALSE)

plot
ggsave('esspi_large/esspi_large.pdf')

fit <- lm(log(large_agg$esspi) ~ log(large_agg$Group.1), data=large_agg)
fit
fit1 <- lm(log(large_agg1$esspi) ~ log(large_agg1$Group.1), data=large_agg1)
fit1

#/////////////////////////////////////////////////////////////////////
#### Worst-case 150 to 200
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

large = na.omit(read.csv("esspi_larger/ess_per_iter_150_to_200_lb.csv"))
# large = subset(large, abs(mean - 0.5) < 0.4)
large_agg = aggregate(large["esspi"], list(large$groupSize), min)

large1 = na.omit(read.csv("esspi_larger/ess_per_iter_150_to_200_nlb.csv"))
# large1 = subset(large1, abs(mean - 0.5) < 0.4)
large_agg1 = aggregate(large1["esspi"], list(large1$groupSize), min)

plot <- ggplot() +     
  xlab("log(Group Size)") +
  ylab("log(ESS/Iter)") +
  labs(color='') +
  geom_point(data=large_agg, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi), colour='LB')) +
  geom_point(data=large_agg1, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi), colour='Naive')) 

plot <- plot + geom_smooth(data=large_agg, method=lm, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi)), colour='red', se=FALSE) + 
  geom_smooth(data=large_agg1, method=lm, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi)), color='blue', se=FALSE)

plot
ggsave('esspi_larger/esspi_larger.pdf')

fit <- lm(log(large_agg$esspi) ~ log(large_agg$Group.1), data=large_agg)
fit
fit1 <- lm(log(large_agg1$esspi) ~ log(large_agg1$Group.1), data=large_agg1)
fit1

#/////////////////////////////////////////////////////////////////////
#### PyClone visualization
#/////////////////////////////////////////////////////////////////////

