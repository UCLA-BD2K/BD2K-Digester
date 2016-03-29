BD2K-Digester
===================
	BD2K Center of Excellence for Big Data Computing at UCLA

	Alan Kha		    akhahaha@gmail.com
	Sneha Venkatesan	ssneha1995@gmail.com
-------------------------------------------------------------------------------
Overview
---------------
Aggregates and digests updates from BD2K member websites specified in an XML
file.

Usage
---------------
	usage: Digester -f <arg> [-h] [-o <arg>] [-r <arg>] [-s <arg>]
	Get a digest of changes to websites.

     -e,--email <arg>      (REQUIRED if -r) Outgoing email properties file
	 -f,--filename <arg>   (REQUIRED) Site .xml file to run
	 -h,--help
	 -o,--output <arg>     Output path
	 -r,--report <arg>     Send a report to the following semi-color separated
						   emails
	 -s,--siteID <arg>     Specific site ID to run (must be in file)

### Scheduling Runs
**Windows**

Create a .bat file to run digester.jar using `java -jar` with the desired
options, and schedule using Windows Task Scheduler. Ensure that the "Start in"
directory is set correctly. Test by manually running the task.

### Email Properties File
Example:

    email = admin@heartbd2k.org
    auth = true
    startTLS = true
    host = smtp.gmail.com
    port = 587
    username = admin@heartbd2k.org
    password = PASSWORD

### Site XML Definitions
Example:

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <CrawlTasks>
        <CrawlTask ID="JHU">
            <RootURL>http://icm.jhu.edu/</RootURL>
            <OutputPath>data/UPitt</OutputPath>
            <Seeds>
                <SeedURL>http://icm.jhu.edu/people/core-faculty/</SeedURL>
            </Seeds>
            <Excludes>
                <ExcludeURL>http://icm.jhu.edu/events</ExcludeURL>
                <ExcludeURL>http://icm.jhu.edu/tag</ExcludeURL>
                <ExcludeURL>http://icm.jhu.edu/news-and-events/calendar/action</ExcludeURL>
            </Excludes>
        </CrawlTask>
    </CrawlTasks>

Name | Description
------------ | -------------
CrawlTasks | Wrapper node for CrawlTasks.
CrawlTask | Contains crawling information for each site.
CrawlTask:ID | Determines output path. Should be unique. Used with `-s` option. (REQUIRED)
RootURL | Root domain prefix to crawl. Also used as the first seed. (REQUIRED)
OutputPath | Specifies output path. (DEPRECATED)
Seeds | Wrapper node for SeedURLs.
SeedURL | Additional seed URLs to visit.
Excludes | Wrapper node for ExcludeURLs.
ExcludeURL | URL prefixes to avoid crawling.

Dependencies
---------------
 - [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)
 - [Crawler4J](https://github.com/yasserg/crawler4j)
 - [JSoup](http://jsoup.org/)
 - [Unirest](http://unirest.io/)
