# Quick introduction to the ELK stack
The ELK Stack (E stands for elasticsearch, L for logstash and K for Kibana, the three main components), is a powerful data processing and visualization solution. It includes Elasticsearch for data storage and search, Logstash for data transformation, Filebeat for data collection, and Kibana for data visualization. This stack is widely used for real-time log and event data analysis.
Elasticsearch is accessible and queryable via HTTP APIs and both results and query are formatted as JSON documents.
Since ES is a search engine, every JSON document returned is better know as "hit"

# Our envirnment
We need to implement a specific version of ELK with FileBeat installed on the machines that generate logs and Logstash configured properly to receive and process them, according to specific grok regex specifications that will parse the logs based on our formats

## Network and nodes
In our specific case, we are configuring an ELK stack made up by two distributed nodes 
: eosao13 (10.8.244.14):hosts major services and operates as the “master” for the elasticsearch services
Eosao14 (10.8.244.15): hosts only elasticsearch as a secondary node 

Elasticsearch, due to our security policy, operetes only via HTTPS (and with rispectives SSL certificates too)

# Installation and configurations

We are sending openEO spring driver and ODC driver logs (log4j multiline json and python logging respectively) through our ELK stack 

Filebeat is configured to send logs to logstash via UPD port. FileBeat is listening on specified files for new raw log entries (each new line appended). FileBeat will then send them to LogStash on eosao13.



## On eosao13

### installing and configuring elasticsearch

Stop the Elasticsearch service if already installed and active, otherwise, if not installed:

Add Elasticsearch GPG Key
```wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -```

#### Add Elasticsearch Repository
```echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | sudo tee /etc/apt/sources.list.d/elastic-7.x.list```

Update Package List
```sudo apt update```

Install Elasticsearch
```sudo apt install elasticsearch```

Start Elasticsearch Service
```sudo service elasticsearch start```

Generating p12 certificates

1.  while on / usr / share / elasticsearch folder, launch:
```
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

```wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo gpg --dearmor -o /usr/share/keyrings/elastic-keyring.gpg```

```sudo apt-get install apt-transport-https```

```echo "deb [signed-by=/usr/share/keyrings/elastic-keyring.gpg] https://artifacts.elastic.co/packages/8.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-8.x.list```

```sudo apt-get update && sudo apt-get install logstash```

#### Editing logstash conf

Edit /etc/logstash/conf.d/logsatsh.conf as follows:

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


## On eosao13

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

# Ohter configs
discovery.seed_hosts: ["10.8.244.14", "10.8.244.15", "eosao13", "eosao14"]
cluster.initial_master_nodes: ["eosao13"]

```
# checking ELK health
wget, curl or navigate on https://<es-host>:9200/_cat/health, to check if everything works fine on the cluster.


### On both eosao13 and -14

Test if ES and LS are working. Test and restart their system services

### On Client Host (where spring and OCD resides)

#### Installing FileBeat
Filebeat is mandatory to be used in the same machines as the logs resides, send logs to logstash

```sudo apt-get update && sudo apt-get install filebeat```
```sudo systemctl enable filebeat```


### Add the proper log configs on /etc/filebeat/filebeat.yml
Based on your specific machine, through FB documentation, populate your filebeat.yml file


