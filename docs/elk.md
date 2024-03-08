# Quick introduction to the ELK stack

* [Introduction](#introduction)
  * [Network and nodes](#network-and-nodes)
* [Installation and configuration](#installation-and-configuration)
  * [Log examples](#log-examples)
    * [Multiline JSON](#multiline-json)
    * [Python logging](#python-logging)
  * [node1 (master)](#node1-master)
    * [Installing and configuring elasticsearch](#installing-and-configuring-elasticsearch)
      * [Generating p12 certificates](#generating-p12-certificates)
      * [Elasticsearch conf](#elasticsearch-conf)
    * [Installing logstash](#installing-logstash)
      * [Multiline filter plugin](#multiline-filter-plugin)
      * [Logstash conf](#logstash-conf)
  * [node2 (optional)](#node2-optional)
    * [Installing and configuring elasticsearch](#installing-and-configuring-elasticsearch)
* [Checking ELK stack](#checking-elk-stack)
  * [Cluster health](#cluster-health)
  * [Check if ES is collecting log entries](#check-if-es-is-collecting-log-entries)
  * [Further notes & suggestions](#further-notes--suggestions)
* [Credits and References](#credits-and-references)

# Introduction

The **ELK Stack** (E stands for *elasticsearch*, L for *logstash* and K for *Kibana*, the three main components alongisde with *Filebeat*), is a powerful data processing and visualization solution.\

Elasticsearch is a NoSQL engine that which is designed to return and search single, aggregated or processed results from large amounts of data efficiently. It uses a scoring algorithm to classify the results based on their relevance to the search query performed. Additionally, Elasticsearch is optimized for parallel processing, meaning it can best leverage the resources of a distributed cluster to accelerate data search and analysis. which provides search engine capabilities on its storage\

Logstash is for data transformation\

Filebeat is for data collection\

Kibana is for data visualization and analisys

This stack is, among many other uses, widely used for real-time log and event data analysis.\
Elasticsearch is accessible and queryable via HTTP APIs and both results and query are formatted as JSON documents.\
Since ES is a search engine, every JSON document returned is better know as "hit"

This doc provides the basis for setting up a multiple-node or single-node elk cluster (extendible by your choice with additional nodes)

To ingest logs from our OpenEO componentes (such as spring driver and ODC driver) we need to implement a specific ELK stack configuration.\
FileBeat has to be installed on client machines that usually generate logs.\
Logstash has better to be configured in the same machine as the ELK master node, in order to receive and process log entries.

Log entries are recived almost instantly line by line on LogStash from FileBeat via TCP or UDP.

According to grok regex specifications defined on Logstash Configuration file, Logastash will parse and ingest every entry into ElasticSearch.

Elasticsearch stores entries as JSON documents called hits, who are organized in indices.



## Network and nodes
In our specific example, we are configuring an ELK stack made up by one (or two) distributed nodes: \
You can choose whether to configure a single-node ES installation or a cluster, according to your needs
node1 (IP 192.168.X.X): hosts major services (elasticsearch and logstash) and operates as the “master” for the elasticsearch services\
node2 (IP 192.168.Y.Y) (optional, according to your needs): hosts only elasticsearch as a secondary node 

Elasticsearch, due to security policies, better operates via HTTPS (and its SSL certificates), but we can also use HTTP for test purposes or if our environment in higly safe (if you are fully certain that it cannot pose a risk to the security of your systems)

NOTE: IP and hostnames used here are examples. choose IP addresses and node names according to your conventions and based on the IP addressing plan of your network

# Installation and configuration
## Log examples
We are sending two examples of logs from the respective files (one is a log4j multiline json and the other is a python logging file, respectively) through this ELK stack example configuration

### Multiline JSON
```json
{"@timestamp":"2024-03-05T23:01:43.235Z", "log.level":"ERROR", "message":"JWT token exception caught: JWT expired at 2024-02-28T23:22:32Z. Current time: 2024-03-05T23:01:43Z, a difference of 517151230 milliseconds.  Allowed clock skew: 0 milliseconds.", "ecs.version": "1.2.0","service.name":"test_service","service.node.name":"test_node","event.dataset":"test_dataset","process.thread.name":"https-jsse-nio-8443-exec-9","log.logger":"org.openeo.spring.components.ExceptionTranslator"}
```

### Python logging
```log
2023-10-23 14:18:29,282 551a37d4-0df9-40b2-b375-6dddff9be748 [INFO] Obtaining job id from graph: 551a37d4-0df9-40b2-b375-6dddff9be748
```


Filebeat is configured to send logs to logstash via UPD port. FileBeat is listening on specified files for new raw log entries (each new line appended). FileBeat will then send them to LogStash on node1.

We are going to install Elasticsearch version 8.12.2, filebeat version no. 8.12.2, logstash version no 8.12.2, make sure to install this version

Setup correctly working and tested on Ubuntu 22.04.3 LTS

## node1 (master)
Consider this node as the master node,

### Installing and configuring elasticsearch

Stop the Elasticsearch service if already installed and active, otherwise, if not installed:

Add Elasticsearch GPG Key

```bash
wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo gpg --dearmor -o /usr/share/keyrings/elastic-keyring.gpg
```

Install required packages

```bash
sudo apt-get install apt-transport-https
```


Add Elasticsearch Repository
```bash
echo "deb [signed-by=/usr/share/keyrings/elastic-keyring.gpg] https://artifacts.elastic.co/packages/8.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-8.x.list
```

Update Package List
```bash
sudo apt update
```

Install Elasticsearch
```bash
sudo apt install elasticsearch
```

Enable System Service
```bash
sudo systemctl enable elasticsearch.service
```

Save elastic user password:\
during install wizard, password for default user ('elastic'), will be prompted on CLI. 
Take care of the password by saving it

Start Elasticsearch Service
```bash
sudo service elasticsearch start
```

#### Generating p12 certificates
**Skip this part if you just want to let ELK modules work in HTTP mode**\
**Recommended for now (HTTPS doc is incmplete)**

1.  go to elasticsearch binaries folder (```/usr/share/ elasticsearch```) and use dedicated tools to generate CA and certificate (launching elasticsearch-certutil will lead to a CLI wizard):
```bash
cd /usr/share/elasticsearch/bin
./elasticsearch-certutil ca
# Generate certificate for transport layer
./elasticsearch-certutil cert --ca </path/to>/elastic-stack-ca.p12
# Generate certificate for HTTPS 
./elasticsearch-certutil cert --ca </path/to>/elastic-stack-ca.p12
```
IMPORTANT: 
- while generating ca and certificate using CLI wizard, make sure to output them in ```/etc/elasticsearch/certs/```, moving or deleting the old ones before (you can enter a full path of output file in the wizard, it will be asked to create 'certs' directory if it doesn't exists)
- strongly adviced to use the default name 'elastic_certificates.p12' for transport layer certificate (as the wizard suggests), 'https.p12' for HTTPS certificate and 'elastic-stack-ca.p12' for CA, especially during test purposes
- ensure that file permissions (r/w) are properly set and certificate are owned by elasticsearch:elasticsearch

#### Elasticsearch conf 
Set ```/etc/elasticsearch/elasticsearch.yml``` (or elasticsearch.yml under your custom ES folder) as follows:
```
# Network settings
network.host: 0.0.0.0
http.port: 9200
transport.port: 9300
http.host: 0.0.0.0

# Paths
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch

# Cluster settings
cluster.name: test-cluster # choose your name
node.name: node1 # Choose your name

# SSL Configuration
xpack.security.enabled: true  # False for HTTP
xpack.security.transport.ssl.enabled: true  # False for HTTP

# Start commenting (line by line, if you need HTTP)
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.transport.ssl.keystore.password: <pwd> # property no needed if blank passowrd
xpack.security.transport.ssl.truststore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.transport.ssl.truststore.password: <pwd> # property no needed if blank passowrd
# End commenting (only for HTTP)

xpack.security.http.ssl.enabled: true # False for HTTP
# Start commenting (line by line, if you need HTTP)
xpack.security.http.ssl.keystore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.http.ssl.truststore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
# End commenting (only for HTTP)

# Other recommended configurations
# not necessary if single-node
# add as many nodes as your nodes are
# add both ip address and node hostname
discovery.seed_hosts: ["192.168.1.100", "192.168.1.150", "node2"] 

#Add only master node hostname
cluster.initial_master_nodes: ["node1"]
```

Restart elasticsearch:\
```bash
sudo systemctl restart elasticsearch.service
```
Restart must go fine, otherwise check typo errors in YML file for troubleshooting

### Installing logstash 

**IMPORTANT**: as mentioned, at the moment this Logstash setup doesn't allow HTTPS mode and SSL certificates
```bash
sudo apt-get update && sudo apt-get install logstash
```

Enable System Service
```bash
sudo systemctl enable logstash.service
```

Start Logstash Service
```bash
sudo service logstash start
```

#### Multiline filter plugin 
This plugin is mandatory to have the configuration below working 
```bash
/usr/share/logstash/bin/logstash-plugin install logstash-filter-multiline
```

#### Logstash conf
\
Edit /etc/logstash/conf.d/logstash.conf as follows:
You have to create this file inside conf.d folder, usually it isn't included by default

```
input{
  beats {
    port => 5044
    type => "beats"
  }
}

filter {
  if [fields][name] == "openeo-odc" {
    grok {
      match => { "message" => "(?m)%{TIMESTAMP_ISO8601:time}%{SPACE}%{UUID:job_id}%{SPACE}%{NOTSPACE}%{LOGLEVEL:level}%{NOTSPACE}%{SPACE}(?<msg>(.|\n)*)"}
      overwrite => [ "message" ]
    }

    multiline {
      pattern => "^%{TIMESTAMP_ISO8601} "
      negate => true
      what => previous

      }
    
  date {
    match => [ "time", "yyyy-MM-dd HH:mm:ss,SSS" ]
    target => "time"
    timezone => "Europe/Rome"
    #target_timezone => "Etc/GMT"
  }

  mutate {  
    gsub => ["time", " ", "T"]
    gsub => ["time", ",", "."]
    #gsub => ["time", "Z", ""]
    replace => { "time" => "%{time}" }
}

    mutate {
      add_field => { "caller" => "openeo_odc_driver" }
    }
 }


  if [fields][name] == "openeo-spring"
  {

    if [jobid]
    {
      mutate {
        rename => {
          "[log.level]" => "level"
          "[@timestamp]" => "time"
          "[message]" => "msg"
          "[jobid]" => "job_id"

          add_field => { "caller" => "openeo_spring_driver" }
        }
      }
    }
    else
   {
      mutate {
        rename => {
          "[log.level]" => "level"
          "[@timestamp]" => "time"
          "[message]" => "msg"

          add_field => { "caller" => "openeo_spring_driver" }
        }
      }
    }

    if ![msg] {
      multiline {
       pattern => "^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},\d{3}"
        negate => true
        what => "previous"
      }
    }

  }
}

output {
  if [type] == "beats" {
    elasticsearch {
      index => "openeo_test"
      #if HTTPS mode => https
      # set proper IP if the ES node is not on the same machine as Logstash' 
      hosts => ["http://localhost:9200"]
      user => "elastic"
      #Password is the one for 'elastic' user you saved on setup wizard while installing ES
      password => "elasticsearch-password"

      ssl => false
      # Change to True if HTTPS with SSL enabled
      ssl_certificate_verification => false
      ## FIXME implement
      #cacert => "/path/to/ca.crt"
      # Optional: Specify client-side SSL certificate and key
      #ssl_certificate => "/path/to/client.crt"
      #ssl_key => "/path/to/client.key"
    }
  }
stdout { codec => rubydebug }
}
```

Restart logstash:\
```bash
sudo systemctl restart logstash.service
```
Restart must go fine, otherwise check typo errors in conf file for troubleshooting

## node2 (optional)

### Installing and configuring elasticsearch

Stop the Elasticsearch service if already installed and active, otherwise, if not installed:

Repeat same steps as for node1 and then

in the ```/etc/elasticsearch/elasticsearch.yml``` make sure to have set:

On cluster settings:
```
# Cluster settings
cluster.name: test-cluster
node.name: node2
```

On other configs:
```
# Other configs
discovery.seed_hosts: ["192.168.1.1", "192.168.1.150", "node1", "node2"]
cluster.initial_master_nodes: ["node1"]

```

If you need more nodes, you can repeat this procedure

At this point RESTART ES and check health  (see section below)



# Installing FileBeat
Filebeat is mandatory to be used in machines where logs are accessible by a local path, send logs to logstash


IMPORTANT: You can have your logging machine without any ELK component but filebeat. Especially if you installed a multi node cluster, is strongly recommended to reserve specific machines for the cluster.

```bash
sudo apt-get update && sudo apt-get install filebeat
```

Enable System Service
```bash
sudo systemctl enable filebeat.service
```


## Filebeat conf
Edit /etc/filebeat/filebeat.yml as follows
Based on your specific log paths, edit these sections of filebeat.yml conf file as follows:

```yaml
# editing filebeat inputs
filebeat.inputs:

# odc-driver input
- type: log
  id: openeo-odc # openeo-test-01

  enabled: true

  paths:
    - /path/to/openeo_odc_driver/openeo_odc_driver/odc_backend.log
  fields:
    name: "openeo-odc"

# spring-driver input
- type: log 
  id: openeo-spring
  enabled: true
  paths:
    - /path/to/openeo-spring-driver/logs/openeo_1.0.0.log
  fields:
    name: "openeo-spring"
  json.keys_under_root: true
```

```yaml
#editing logstash output
output.logstash:
  hosts: ["X.X.X.X:5044"] #sobstitute X with real ip bytes
```

Remove 'output.elasticsearch' property, if present, because we're using the logstash one

Restart filebeat:\
```bash
sudo systemctl restart filebeat.service
```
Restart must go fine, otherwise check typo errors in YML file for troubleshooting


# Checking ELK stack 
## Cluster health
wget, curl or navigate on http(s)://<your-es-host>:9200/_cat/health, to get some info about (in plain text) cluster status and check if everything works fine on the cluster.


On both node1 and node2

Test if ES and LS are working. Restart their respective system services, if necessary:

## Check if ES is collecting log entries

Having all ELK components running and properly configured, you can easily test if log ingestion works by:
- assuring the component (spring driver, ODC driver) is running and is generating log entries on the file you specified
- by easily search display your index's hits with https://any-elk-active-node:9200/indexname/_search
If the index doesn't exists, elasticsearch will return an error response code.

## Further notes & suggestions

- Kibana is the ELK default dashboard and user interface where you can view, graph, analyze and graphycally edit your ES data.
If you want to use this recommended tool to get your tests and test queries faster and easier, you can follow official documentation on https://www.elastic.co/guide/index.html to install kibana and connect it to ELK stack
- Kibana uses the same Elasticsearch SSL certs, easily configurable on kibana conf file (see official doc)
- Keep your ELK passwords safe and be sure to annotate safely your p12 certificates' password (if you lose a p12 certificate password you have to regenerate it and reconfigure the whole ELK stack; while recovering ELK password such ElasticSearch HTTPAuth password is easier)
- By ingesting data into your ELK a new index, according your conf files, will be created if it this doesn't already exists

# Credits and References

- References for elasticsearch and elastic stack: [elastic.co](https://www.elastic.co)
* [OpenEO](https://openeo.org/)
* OpenEO ODC Driver (Eurac Research): [OpenEO ODC driver on GitHub](https://github.com/SARScripts/openeo_odc_driver/)
* OpenEO Spring Driver (Eurac Research): [OpenEO Spring driver on GitHub](https://github.com/Open-EO/openeo-spring-driver/)
