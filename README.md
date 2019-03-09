# Cloud-Computing-and-Virtualization

# HillClimbing@Cloud



## 1 - Introduction

Develop an elastic cluster of web services that is able to execute a simple function.  
The system will receive a stream of web requests from users.  
Each request is for finding the maximum of a given height map, providing the coordinates of the start position and a search rectangle.  
In the end, it displays the height-map and the computed path to reach the maximum, using hill-climbing.  
The system will attempt to optimize the selection of the cluster node for each request and to optimize the number of active nodes in the cluster.



## 2 - Architecture

The HillClimbing@Cloud will run within the Amazon Web Services ecosystem. It will have 4 components.


### 2.1 - Web Servers

The web servers receive web requests to perform escape path solvings, run them and return the result. In HillClimbing@Cloud, there will be a varying number of identical web servers. Each one of them will run on a rented AWS Elastic Compute Cloud (EC2) instance.


### 2.2 - Load Balancer

The load balancer is the entry point into the HillClimbing@Cloud system. It receives all web requests, and for each one, it selects an active web server to serve the request and forwards it to that server.


### 2.3 - Auto-Scaler

The auto-scaler is in charge of collecting system performance metrics and, based on them, adjusting the number of active web servers.


### 2.4 - Metrics Storage System

The metrics storage system will use one of the available data storage mechanisms at AWS to store web server performance metrics relating to requests. These will help the load balancer choose the most appropriate web server.



## 3 - Web Servers

The HillClimbing@Cloud web servers run an off-the-shelf Java-based web server application on top of Linux.  
The web server application will serve a single web page that receives a HTTP request providing the necessary information:  

- height map to analyze
- coordinates for the start position (xS, yS)
- top-left (x0, y0) 
- bottom-right (x1, y1) corners of the active search rectangle
- strategy used for hill-climbing (e.g. BFS, DFS, A* )

The page serving the requests will perform the solving online and, once it is complete, reply to the web request with a confirmation, and if successful by drawing the search path leading to the maximum overlaid on the height-map.



## 4 - Load Balancer

The load balancer is the only entry point into the system: it receives a sequence of web requests and selects one of the active web server cluster nodes to handle each request.  
In a first phase, this job can be performed by an off-the-shelf load balancer such as those available at AWS.  
Later in the project, it is supposed to be designed a more advanced load balancer that uses metrics data obtained in earlier requests, stored in the Metrics Storage System.  

The load balancer can estimate the complexity, load and approximate duration of a request, based on the request’s parameters combined with data previously stored in the MSS, that may be periodically or continuously updated by the MSS.   
The load balancer may know which servers are busy, how many and what requests they are  handling, what the parameters of those requests are, their current progress, and how much work is left taking into account the estimate that was calculated when the request arrived.  



## 5 - Auto-Scaler

The auto-scaler will use an AWS Autoscaling group that adaptively decides how many web server nodes should be active at any given moment.  
The challenge is to design the autoscaling rules that will provide the best balance between performance and cost.  
It should detect that the web app is overloaded and start new instances and, conversely, reduce the number of nodes when the load decreases.  



## 6 - Metrics Storage System

The MSS will store performance metrics collected from the web server cluster nodes.  
These nodes will process the HillClimbing@Cloud requests using code developed in order to collect relevant dynamic performance metrics regarding the application code executed.  
They will allow estimating task complexity realistically.

The final choice of the metrics extracted, instrumentation code, and system used to store the metrics data has no further impositions.  
The selected storage system can be updated directly or may resort to some intermediate transfer mechanism. For realism, it is taken into account that continuously querying this storage system may eventually become a bottleneck for the load balancer component.  




# Intermediate Checkpoint

The following aspects of the HillClimbing@Cloud have to be defined and implemented:
- running and instrumented web server
- load balancer (only partial logic)
- auto-scaler (only partial logic)
- report (2-page, double column) describing:
	1. what is already developed and running in the current implementation (architecture, data structures and algorithmn)
	2. the specification of what remains to be implemented or completed.



# Final Checkpoint

Should include all the intermediate checkpoint features and additionally:
- the connection between the web server instrumentation and the MSS
- an adequate auto-scaling algorithm that aims to balance cost and performance efficiently
- an adequate load balancing algorithm that uses the metrics collected in the MSS
- report (up to 6 pages, double-column)
