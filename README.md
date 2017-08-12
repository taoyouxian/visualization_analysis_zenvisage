# Zenvisage in BigData
For chinese version, please see BACHELOR'S DEGREE THESIS OF HOHAI UNIVERSITY for details: 
http://blog.csdn.net/u013052657/article/details/73468786

# ABSTRACT
Visual analysis platform for the process and analysis on large data is becoming increasingly important. This paper analyzes the demand for visualization analysis of large time series data(ie, data that changes with time series), and some visual analysis tools sounds cumbersome, repetitive, time-consuming, and time-consuming large data (ie, data with time series variation) problem. For this, with the open source Zenvisage visual analysis platform, we propose a large data analysis scheme based on Spark, which is integrated into the bottom of Zenvisage's visual analysis platform, and solves the above problems. The paper mainly discusses the following aspects:

(1) Analying the visualization requirements of large time series data, the system needs to manage amounts of views, support the user to modify the query conditions, adjust the analysis of the property, make interactive visual analysis; according to the visualization of the trend to help users find the views that are similar in trend, or show a significantly different trend, and recommend presenting the most interesting trends in the subset of data currently being viewed, such as representative visualizations, outliers, providing user with data uploading interfaces, and supporting visualization based collections query, specify the required insight from the visualization.

(2) Analying the Zenvisage platform in the large time data application scenarios storage and query performance degradation, find the bottleneck of traditional relational database in the analysis of large data in timing.

(3) According to these bottlenecks, a large data analysis scheme based on Spark is proposed. The program is stored in HDFS for file storage, Parquet columns store data compression, distributed memory engine Spark as the underlying computing framework to solve effectiveness of the Zenvisage platform in the large time series data scenarios, and improve the effectiveness of interactive analysis system performance and operational efficiency.

(4) This analysis scheme is integrated into the bottom of Zenvisage's visual analysis platform, and the effectiveness and availability of the research contents are evaluated by systematic testing and comparative experiments.

This project mainly serves the major technology project of Guangdong Province, "high-throughput large-scale real-time business intelligence system to achieve industrialization". It proves that the system can meet the users’ needs of interactive analysis, optimize the storage and query performance of Zenvisage platform in the scene of large time series data, and it is stable, functional and extensible.

# Key words
Large Time Series Data; Interactive Analysis; Visualization; Zenvisage; HDFS; Column-store; Spark

### Version
The current version is 0.1#copy.

Zenvisage git: https://github.com/zenvisage/zenvisage

### Additional Readings
* Zenvisage github: https://github.com/zenvisage/zenvisage.git
* Zenvisage project webpage is [here] [zenvisage-website]; regular updates will be posted at this webpage.
* Zenvisage VLDB'17 paper describing ZQL, our SmartFuse<sup>1</sup> ZQL optimizer, as well as a ZQL-centric user study is [here] [zenvisage-vldb].
* Zenvisage CIDR'17 paper describing the overall Zenvisage system, along with some target user scenarios is [here] [zenvisage-cidr].
 
### Required Software
* Java Platform (JDK) >= 8; once installed, update `JAVA_HOME` to your installed java folder.
* PostgreSQL >= 9.5;  many ways to install this, including an [app][postgres-installation] on Mac OSX.
* Apache Maven 3.0.5;  many ways to install this, including `brew install maven` on Mac OSX.

### Installation Instructions
The installation of Visualization_analysis_zenvisage is straightforward, assuming Postgres, Java, and Maven are installed. Also, Hadoop + Spark Cluster is needed. If you want to install using a Docker container, the instructions are [here](https://github.com/zenvisage/zenvisage/wiki/Docker-Installation-Instruction).

* Clone the visualization_analysis_zenvisage repository. (Alternatively, you can download the source as a zip.)

     
        git clone https://github.com/taoyouxian/visualization_analysis_zenvisage.git
     

* Contact. Any problem, you can call me with email:     
        
          Aliyun Email : taoyouxian@aliyun.com
          Gmail Email: bthhuc@gmail.com