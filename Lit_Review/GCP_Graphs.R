# GCP GRAPHS

setwd("~/Dropbox/Work2015/Graphs/GraphTheory-Exercises/Lit_Review")

bob = read.csv('results_incremental_runtime.txt',header=F)
colnames(bob) = c('graph','solver','k','time','successes','iterNum')
graph_names = unique(bob$graph)
solver_names = unique(bob$solver)
# 5 graphs, 4 solvers on each graph, k vs time

for(graph_name in graph_names){
  graph = bob[bob$graph==graph_name & bob$solver!="BruteBuckets",]
  graph = graph[order(graph$graph,graph$k),]
  png(file = paste0(graph_name,'.png'), 900,600 )
  plot(graph$k,graph$time/1000,main=gsub('.col','',substr(graph_name, start = 4, stop = 1000000L)),
       pch='',xlab='number of color classes k',ylab='time (seconds)')
  colors = c('red','green','blue')
  ii=1
  for(solver_name in solver_names[-1]){
    lines(graph$k[graph$solver==solver_name],graph$time[graph$solver==solver_name]/1000,
         col=colors[ii],type='o')
    ii=ii+1
  }
  dev.off()
  
}


