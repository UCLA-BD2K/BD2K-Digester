BD2K-Digester
===================
	BD2K Center of Excellence for Big Data Computing at UCLA

	Alan Kha		    akhahaha@gmail.com
	Sneha Venkatesan	ssneha1995@gmail.com
-------------------------------------------------------------------------------
Overview
---------------
Aggregates and digests updates from BD2K member websites.

Usage
---------------
	usage: Digester -f <arg> [-h] [-o <arg>] [-r <arg>] [-s <arg>]
	Get a digest of changes to websites.

	 -f,--filename <arg>   (REQUIRED) Site .xml file to run
	 -h,--help
	 -o,--output <arg>     Output path
	 -r,--report <arg>     Send a report to the following semi-color separated
						   emails from email.prop
	 -s,--siteID <arg>     Specific site ID to run (must be in file)

Dependencies
---------------
 - [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)
 - [Crawler4J](https://github.com/yasserg/crawler4j)
 - [JSoup](http://jsoup.org/)
 - [Unirest](http://unirest.io/)
