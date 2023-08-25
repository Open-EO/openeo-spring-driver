# OpenAPI generated server

Spring Boot Server 


## Overview  

This server was generated by the [OpenAPI Generator](https://openapi-generator.tech) project.
By using the [OpenAPI-Spec](https://openapis.org), you can easily generate a server stub.
This is an example of building a OpenAPI-enabled server in Java using the SpringBoot framework.

The underlying library integrating OpenAPI to SpringBoot is [springfox](https://github.com/springfox/springfox)

Start your server as a simple java application

You can view the api documentation in swagger-ui by pointing to  
https://localhost:8443/

## Configuration setup hints

This back-end is only functional if an application.properties file is added to ```/src/main/resources```.
This file should contain at least the following:

```
springfox.documentation.swagger.v2.path=/api-docs

spring.jackson.date-format=org.openeo.spring.RFC3339DateFormat
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=true
spring.autoconfigure.exclude = org.keycloak.adapters.springboot.KeycloakAutoConfiguration
spring.datasource.jdbc=jdbc:h2:/path/to/openeo.db;DB_CLOSE_DELAY=-1
spring.datasource.username=my_username
spring.datasource.initialization-mode
spring.security.filter.order=5

server.tomcat.port=8081
server.port=8443

server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:my_keystore.p12
server.ssl.key-store-password=my_keystore_password
server.ssl.key-alias=my_alias
#server.ssl.trust-store=classpath:my_truststore.p12
#server.ssl.trust-store-password=my_truststore_password

security.require-ssl=true
spring.security.filter.order=5

spring.jackson.serialization.write-dates-as-timestamps=false

co.elasticsearch.endpoint=https://my_elastic:9200
co.elasticsearch.service.name=openeo
co.elasticsearch.service.node.name=elk_node_hostname
co.elasticsearch.service.index.name=es_index_name
co.elasticsearch.service.truststore.password=my_password
co.elasticsearch.service.truststore.path=/path/to/elastic-certificates.p12
co.elasticsearch.service.keystore.password=my_password
co.elasticsearch.service.keystore.path=/path/to/http.p12
co.elasticsearch.service.username=elastic_uname
co.elasticsearch.service.password=my_password
	
org.openeo.tmp.dir=tmp/
org.openeo.tmp.file.expiry=60
org.openeo.file.expiry=1
org.openeo.querycollectionsonstartup=true
org.openeo.parallelizedHarvest=true

org.openeo.oidc.configuration.endpoint=https://my_keycloak.url/auth/realms/openeo/
org.openeo.oidc.providers.list=classpath:oidc_providers.json

org.openeo.wcps.endpoint=http://my_wcps_server:8080
org.openeo.wcps.endpoint.version=2.0.1
org.openeo.wcps.provider.name=My Company
org.openeo.wcps.provider.url=http://www.my-company.url
org.openeo.wcps.provider.type=host
org.openeo.wcps.processes.list=classpath:processes_wcps.json
org.openeo.wcps.collections.list=collections_wcps.json
org.openeo.wcps.provider.type=host

org.openeo.odc.endpoint=http://my_open_data_cube_endpoint
org.openeo.odc.deleteResultEndpoint=http://my_open_data_cube_endpoint_for_stopping_a_job
org.openeo.odc.collectionsEndpoint=http://my_open_data_cube_endpoint/collections/
org.openeo.odc.provider.name=open data cube provider name
org.openeo.odc.provider.url=http://www.open_data_cube_provider.url
org.openeo.odc.provider.type=host
org.openeo.odc.processes.list=classpath:processes_odc.json
org.openeo.odc.collections.list=collections_odc.json
org.openeo.odc.provider.type=host

org.openeo.endpoint=https://my_openeo.url
org.openeo.public.endpoint=https://my_openeo_public.url
org.openeo.udf.python.endpoint=http://my_openeo_python_udf_service.url
org.openeo.udf.candela.endpoint=http://my_openeo_candela_service.url
org.openeo.udf.r.endpoint=http://my_openeo_R_udf_service.url
org.openeo.udf.dir=/my/udf/working/directory/
org.openeo.udf.importscript=/my/udf/import/script/import_udf.sh
```

Further files needed are for connection with keycloak: `keycloak.json`

```
{
	"realm": "my_realm",
	"auth-server-url": "https://my_keycloak.url/auth",
	"ssl-required": "external",
	"resource": "my_client_id",
	"verify-token-audience": false,
	"credentials": {
		"secret": "my_secret"
	},
	"use-resource-role-mappings": true,
	"confidential-port": 0,
	"policy-enforcer": {
		"enforcement-mode" : "PERMISSIVE",
		"claim-information-point": {
			"claims": {
				"claim-from-relativePath": "{request.relativePath}"
			}
		}
	}
}
```

and for the support of default client id configuration: `oidc_providers.json`

```
{
	"providers": [
		{
			"id": "my_default_provider",
			"issuer": "https://my_keycloak.url/auth/realms/my_realm",
			"scopes": [
				"email",
				"profile",
				"roles",
				"web-origins",
				"address",
				"microprofile-jwt",
				"offline_access",
				"phone"
			],
			"title": "My Deault Provider",
			"description": "Some more information about my default oidc provider and setup.",
			"default_clients": [
				{
					"id": "my_client_id",
					"grant_types": [
						"authorization_code+pkce",
						"refresh-token"
					],
					"redirect_urls": [
						"https://editor.openeo.org/",
						"http://localhost:1410/*"
					]
				}
			]
		}
	]
}
```

## Logging

All logging can be controlled through **log4j2**.
For tweaking of log level and file output modify
`log4j2.xml` in *./src/main/resources*.

