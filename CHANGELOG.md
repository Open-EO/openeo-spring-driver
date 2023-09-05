# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Support for Basic Authentication for internal/testing purposes as an alternative
  to operational OIDC service (Keycloak) ([PR 86](../../pull/86)).
- Support for debugging a job with logs request through external ElasticSearch service
  ([PR 87](../../pull/87)).
- More unit and integration tests.

### Changed
- Migration to Spring Boot 2.6.1 and Sprint Security 5.6.1.
- Tests to use JUnit 5.

### Deprecated
- Legacy Keycloak security adapters, replaced by 


## [1.1.0] - 2022-08-01
### Added
- Batch jobs for the ODC backend.
- Refactoring of collections to support multiple engines.
- `radar mask` and `filter_bbox` processes.
- List jobs for authenticated users.

### Fixed
- Zillions bug fixes.

### Changed
- Migration to Spring Boot 2.5.1 and Spring Security 5.5.1.

## [1.0.0] - 2021-02-17
- First openEO API [v1.0.0](https://api.openeo.org/1.0.0/) implementation based on OpenAPI v2.0,
  and with external Keycloak authentication service.


[Unreleased]: https://github.com/Open-EO/openeo-spring-driver/compare/master...1.1-draft
[1.1.0]: https://github.com/Open-EO/openeo-spring-driver/tree/1.1-draft
[1.0.0]: https://github.com/Open-EO/openeo-spring-driver/tree/1.0-PKCE
