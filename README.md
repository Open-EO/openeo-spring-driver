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

server.port=8443
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:my_keystore.p12
server.ssl.key-store-password=my_keystore_password
server.ssl.key-alias=my_alias

security.require-ssl=true

org.openeo.endpoint=https://my_openeo.url
org.openeo.public.endpoint=https://my_openeo_public.url
org.openeo.tmp.dir=tmp/
org.openeo.tmp.file.expiry=60
org.openeo.file.expiry=1
org.openeo.querycollectionsonstartup=true

org.openeo.oidc.configuration.endpoint=https://my_openeo.url/auth/realms/openeo/

org.openeo.wcps.endpoint=http://my_wcps_server:8080
org.openeo.wcps.provider.name=My Company
org.openeo.wcps.provider.url=http://www.my-company.url
org.openeo.wcps.processes.list=classpath:processes_wcps.json
org.openeo.wcps.collections.list=collections_wcps.json

org.openeo.odc.endpoint=http://my_open_data_cube_endpoint
org.openeo.odc.collectionsEndpoint=http://my_open_data_cube_endpoint/collections/
org.openeo.odc.provider.name=open data cube provider name
org.openeo.odc.provider.url=http://www.open_data_cube_provider.url
org.openeo.odc.processes.list=classpath:processes_odc.json
org.openeo.odc.collections.list=collections_odc.json

org.openeo.udf.python.endpoint=http://my_openeo_python_udf_service.url
org.openeo.udf.r.endpoint=http://my_openeo_R_udf_service.url
org.openeo.udf.dir=/my/udf/working/directory/
org.openeo.udf.importscript=/my/udf/import/script/import_udf.sh
```
## Logging
All logging can be controlled through log4j2.
For tweaking of log level and file output modify
log4j2.xml in ```/src/main/resources```

