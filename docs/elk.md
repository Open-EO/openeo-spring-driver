# Quick introduction to the ELK stack
The ELK Stack (E stands for elasticsearch, L for logstash and K for Kibana, the three main components), is a powerful data processing and visualization solution. It includes Elasticsearch for data storage and search, Logstash for data transformation, Filebeat for data collection, and Kibana for data visualization. This stack is widely used for real-time log and event data analysis.\
Elasticsearch is accessible and queryable via HTTP APIs and both results and query are formatted as JSON documents.\
Since ES is a search engine, every JSON document returned is better know as "hit"

# Our environment
For our OpenEO componentes (such as spring driver and ODC driver) we need to implement a specific version of ELK with Filebeat in order to ingest logfiles into Elasticsearch.\
FileBeat has to be installed on client machines (that usually generate logs).\
Logstash has better to be properly configured in the same machine as the ELK master node, in order to receive and process log entries recived from FileBeat via TCP or UDP according to specific grok regex specifications that will parse and ingest into ElasticSearch every according on our log formats

## Network and nodes
In our specific case, we are configuring an ELK stack made up by two distributed nodes: \
eosao13 (10.8.244.14): hosts major services and operates as the “master” for the elasticsearch services\
eosao14 (10.8.244.15): hosts only elasticsearch as a secondary node 

Elasticsearch, due to our security policy, operates only via HTTPS (and with respectives SSL certificates too)

# Installation and configurations

We are sending openEO spring driver and ODC driver logs (log4j multiline json and python logging respectively) through our ELK stack 

Filebeat is configured to send logs to logstash via UPD port. FileBeat is listening on specified files for new raw log entries (each new line appended). FileBeat will then send them to LogStash on eosao13.



## On eosao13

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

Start Elasticsearch Service
```bash
sudo service elasticsearch start
```

Generating p12 certificates

1.  while on / usr / share / elasticsearch folder, launch:
```bash
elasticsearch-certutil ca
elasticsearch-certutil cert --ca elastic-stack-ca.p12
```

2. edit / etc / elasticsearch / elasticsearch.yml conf file as follows:
```
# Impostazioni di rete
network.host: 0.0.0.0
http.port: 9200
transport.port: 9300
http.host: 0.0.0.0

# Paths
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch

# Configurazione del cluster
cluster.name: openeo
node.name: eosao13

# Configurazione SSL
xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.transport.ssl.truststore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.http.ssl.enabled: true
xpack.security.http.ssl.keystore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.http.ssl.truststore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12

# Altre configurazioni consigliate
discovery.seed_hosts: ["10.8.244.14", "10.8.244.15", "eosao14"]
cluster.initial_master_nodes: ["eosao13"]
```

### Installing logstash 


```bash
sudo apt-get update && sudo apt-get install logstash
```

Start Logstash Service
```bash
sudo service logstash start
```

### Editing logstash conf
\
Edit /etc/logstash/conf.d/logstash.conf as follows:

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
      hosts => ["https://localhost:9200"]
      user => "elastic"
      password => "e4PNwff4FgPHh+ksWpB9"

      ssl => true
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


## On eosao14

### installing and configuring elasticsearch

Stop the Elasticsearch service if already installed and active, otherwise, if not installed:

Repeat same steps as for eosao 13 and then 
edit e / etc / elasticsearch / elasticsearch.yml as follows:
```
# Network Settings
network.host: 0.0.0.0
http.port: 9200
transport.port: 9300
http.host: 0.0.0.0

# Paths
path.data: /var/lib/elasticsearch
path.logs: /var/log/elasticsearch

# Cluster config
cluster.name: openeo
node.name: eosao14

# SSL config
xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.transport.ssl.truststore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.http.ssl.enabled: true
xpack.security.http.ssl.keystore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12
xpack.security.http.ssl.truststore.path: /etc/elasticsearch/certs/elastic-stack-ca.p12

# Other configs
discovery.seed_hosts: ["10.8.244.14", "10.8.244.15", "eosao13", "eosao14"]
cluster.initial_master_nodes: ["eosao13"]

```

# checking ELK installation 
### cluster health
wget, curl or navigate on https://<es-host>:9200/_cat/health, to get some info about (in plain text) cluster status and check if everything works fine on the cluster.


### On both eosao13 and -14

Test if ES and LS are working. Restart their respective system services, if necessary:


### On Client Host (where spring and OCD resides)

#### Installing FileBeat
Filebeat is mandatory to be used in the same machines as the logs resides, send logs to logstash

```bash
sudo apt-get update && sudo apt-get install filebeat
```
```bash
sudo systemctl enable filebeat
```


### Add the proper log configs on /etc/filebeat/filebeat.yml
Based on your specific machine, through FB documentation, edit your default filebeat.yml file as follows:

```yaml
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
output.logstash:
  hosts: ["X.X.X.X:5044"] #sobstitute X with real ip bytes
```

## Testing ELK log ingestion

Having all ELK components running and properly configured, you can easily test if log ingestion work by:
- assuring the component (spring driver, OCD driver) is running and is generating log entries on the file you specified
- by easily search display your index's hits with https://eosao-elk-host:9200/indexname/_search

## further notes & suggestion

- Kibana is the ELK default dashboard and user interface where you can view, graph, analyze and graphycally edit your ES data; If you want to use this tool to get your tests and test queries faster and easier, you can follow official documentation on https://www.elastic.co/guide/index.html to install kibana and connect it to ELK stack
- Kibana uses the same Elasticsearch SSL certs, easily configurable on kibana conf file (see official doc)
- Keep your ELK passwords safe and be sure to annotate safely your p12 certificates' password (if you lose a p12 certificate password you have to regenerate it and reconfigure the whole ELK stack; while recovering ELK password such ElasticSearch HTTPAuth password is easier)
- By ingesting data into your ELK a new index, according your conf files, will be created if it this doesn't already exists


