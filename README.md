[![Java CI with Gradle](https://github.com/EKrysenko/external-projects-users/actions/workflows/ci.yml/badge.svg)](https://github.com/EKrysenko/external-projects-users/actions/workflows/ci.yml) ![Jacoco Coverage](.github/badges/jacoco.svg)
# external-projects-users

## Pre-requisites:
- docker, docker-compose, jre 21

## External services:
- vault: 
  - url: http://localhost:8200/ui
  - token: root
- kibana:
  - url: http://localhost:5601
  - index pattern: app-logs-*
- prometheus:
  - actuator: http://localhost:8080/actuator/prometheus
  - url: http://localhost:9090/
  - query example: ```sum(rate(http_server_requests_seconds_count[5m])) by (status)```
- swagger:
  - url: http://localhost:8080/webjars/swagger-ui/index.html
- openAPI:
  - url: http://localhost:8080/v3/api-docs

