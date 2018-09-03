#/////////////////////////////////////////////////////////////////////
#### run this before everything
#/////////////////////////////////////////////////////////////////////


root_dir <- "/home/kevinchern/blang/blang-paper-scripts/blangGradAssignment-scaffold/data_analysis"

read_lots_of_files <- function(dir_name){
  setwd(paste(root_dir,"/",dir_name,sep=""))
  list_of_files <- list.files(pattern="*.csv")
  list_of_files <- lapply(list_of_files, read.csv)
  return (list_of_files)
}

aggregate_them_all <- function(lbs){
  lbs <- lapply(lbs, na.omit)
  lbs <- lapply(lbs, function(lb){
  aggregate(lb["esspi"], list(lb$groupSize), mean)
  })
  return (lbs)
}

plot_them_all <- function(lbs, point=FALSE, id, names){
  if (point){
    plot <- ggplot(bind_rows(setNames(lbs, names), .id=id), aes(x=log(Group.1), y=log(esspi), colour=get(id))) +
      labs(color=id) +
      geom_point()
  } else{
    plot <- ggplot(bind_rows(setNames(lbs, names), .id=id), aes(x=log(Group.1), y=log(esspi), colour=get(id))) +
      labs(color=id) +
      geom_line()
  }
  plot
}

#/////////////////////////////////////////////////////////////////////
#### power from 1 to 1/9
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

list_of_files <- read_lots_of_files("esspi")
lbs <- aggregate_them_all(list_of_files)
names <- c(1,2,3,4,5,6,7,8,9)
plot_them_all(lbs, id="temperature", names = names)
ggsave("esspit.pdf")

#/////////////////////////////////////////////////////////////////////
#### balancing function: max, min, sqrt, baker, naive
#/////////////////////////////////////////////////////////////////////

require(dplyr)
require(ggplot2)

list_of_files <- read_lots_of_files("esspibf")
lbs <- aggregate_them_all(list_of_files)
names = c("baker", "max", "min", "sqrt")
plot_them_all(lbs, id="balancing functions", names=names)
ggsave('esspibf.pdf')

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
  geom_point(data=large_agg, aes(x=log(Group.1), y=log(esspi), colour='LB')) +
  geom_ribbon(data=large_agg1, aes(x=log(Group.1), ymax=log(high_q), ymin=log(low_q)), alpha=0.05, fill='blue') +
  geom_point(data=large_agg1, aes(x=log(Group.1), y=log(esspi), colour='Naive')) 

plot <- plot + geom_smooth(data=large_agg, method=lm, aes(x=log(Group.1), y=log(esspi)), colour='red', se=FALSE) + 
  geom_smooth(data=large_agg1, method=lm, aes(x=log(Group.1), y=log(esspi)), color='blue', se=FALSE)

plot
ggsave('esspi_large/esspi_large.pdf')

fit <- lm(log(esspi) ~ log(Group.1), data=large_agg)
fit
fit1 <- lm(log(esspi) ~ log(Group.1), data=large_agg1)
fit1

#/////////////////////////////////////////////////////////////////////
#### check if slope is stable with dimension
#/////////////////////////////////////////////////////////////////////

N = 50
s = 20
x = rep(0,(N-s))
for (i in s:N) {
  fit <- lm(log(esspi) ~ log(Group.1), data=tail(large_agg, i))
  x[i-s+1] = fit$coefficients[2]
}
dat = data.frame(cbind(s:N,x))
plot <- ggplot() + 
  xlab("data size") +
  ylab("slope") +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2]), colour="red")
plot
ggsave('slope_lb_2_to_50.pdf')


N = 50
s = 20
y = rep(0,(N-s))
for (i in s:N) {
  fit <- lm(log(esspi) ~ log(Group.1), data=tail(large_agg1, i))
  y[i-s+1] = fit$coefficients[2]
}
dat1 = data.frame(cbind(s:N,y))
plot <- ggplot() + 
  xlab("data size") +
  ylab("slope") +
  geom_point(data=dat1, aes(x=dat1[,1], y=dat1[,2]), colour="blue") +
plot
ggsave('slope_nlb_2_to_50.pdf')

plot <- ggplot() + 
  xlab("data size") +
  ylab("slope") +
  labs(color='') +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2], colour="LB")) +
  geom_point(data=dat1, aes(x=dat1[,1], y=dat1[,2], colour="Naive")) 
plot
ggsave('slope_lb_nlb_2_to_50.pdf')


#/////////////////////////////////////////////////////////////////////
#### check if slope is stable with thresholding
#/////////////////////////////////////////////////////////////////////

large1 = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_lb.csv"))
N = 50
z = rep(0,N+1)
i = 1
for (eps in seq(0.1,0.5,(0.5-0.1)/N)) {
  large1_s = subset(large1, abs(mean - 0.5) < eps)
  large_agg1_s = aggregate(large1_s["esspi"], list(large1_s$groupSize), mean)
  fit <- lm(log(esspi) ~ log(Group.1), data=large_agg1_s)
  z[i] = fit$coefficients[2]
  i = i + 1
}
dat = data.frame(cbind(seq(0.1,0.5,(0.5-0.1)/N),z))
plot <- ggplot() + 
  xlab("eps") +
  ylab("slope") +
  labs(color='') +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2], colour="LB"))
plot
ggsave('slope_lb_eps_0.1_to_0.5.pdf')

large1 = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_nlb.csv"))
N = 50
w = rep(0,N+1)
i = 1
for (eps in seq(0.1,0.5,(0.5-0.1)/N)) {
  large1_s = subset(large1, abs(mean - 0.5) < eps)
  large_agg1_s = aggregate(large1_s["esspi"], list(large1_s$groupSize), mean)
  fit <- lm(log(esspi) ~ log(Group.1), data=large_agg1_s)
  w[i] = fit$coefficients[2]
  i = i + 1
}

dat1 = data.frame(cbind(seq(0.1,0.5,(0.5-0.1)/N),w))
plot <- ggplot() + 
  xlab("eps") +
  ylab("slope") +
  labs(color='') +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2], colour="Naive"))
plot
ggsave('slope_nlb_eps_0.1_to_0.5.pdf')

plot <- ggplot() + 
  xlab("eps") +
  ylab("slope") +
  labs(color='') +
  geom_point(data=dat, aes(x=dat[,1], y=dat[,2], colour="LB")) +
  geom_point(data=dat1, aes(x=dat1[,1], y=dat1[,2], colour="Naive"))
plot
ggsave('slope_lb_nlb_eps_0.1_to_0.5.pdf')

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
  geom_point(data=large_agg, aes(x=log(Group.1), y=log(esspi), colour='LB')) +
  geom_ribbon(data=large_agg1, aes(x=log(Group.1), ymax=log(high_q), ymin=log(low_q)), alpha=0.05, fill='blue') +
  geom_point(data=large_agg1, aes(x=log(Group.1), y=log(esspi), colour='Naive')) 

plot <- plot + geom_smooth(data=large_agg, method=lm, aes(x=log(large_agg$Group.1), y=log(large_agg$esspi)), colour='red', se=FALSE) + 
  geom_smooth(data=large_agg1, method=lm, aes(x=log(large_agg1$Group.1), y=log(large_agg1$esspi)), color='blue', se=FALSE)

plot
ggsave('esspi_larger/esspi_larger.pdf')

fit <- lm(log(esspi) ~ log(Group.1), data=large_agg)
fit
fit1 <- lm(log(esspi) ~ log(Group.1), data=large_agg1)
fit1

#/////////////////////////////////////////////////////////////////////
#### Worst-case 100 to 150
#/////////////////////////////////////////////////////////////////////


require(dplyr)
require(ggplot2)

large = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_lb.csv"))
large = subset(large, abs(mean - 0.5) < 0.4)
large_agg = aggregate(large["esspi"], list(large$groupSize), min)

large1 = na.omit(read.csv("esspi_large/ess_per_iter_aggregated_nlb.csv"))
large1 = subset(large1, abs(mean - 0.5) < 0.4)
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
ggsave('esspi_large/esspi_large_worst.pdf')

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
large = subset(large, abs(mean - 0.5) < 0.4)
large_agg = aggregate(large["esspi"], list(large$groupSize), min)

large1 = na.omit(read.csv("esspi_larger/ess_per_iter_150_to_200_nlb.csv"))
large1 = subset(large1, abs(mean - 0.5) < 0.4)
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


