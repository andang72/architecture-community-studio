# RESTful API Architecture Studio Community ![version](https://img.shields.io/badge/version-3.0.2-blue.svg)
## Overview

This is Project for [Java][java] based RESTful API Server and Web Console.

- Realtime RESTful API & Page Create and deploy.
- JWT based stateless session.
- Role based access controls.
- Linux List User System.

------
## Getting Started
5.1.x 버전 부터는 Java 8+ 지원
Spring 5.3.x 지원
Java 11 이상을 사용하는 경우 Groovy 업데이트 필요.

## Core RESTful API
/data/api/*
/data/accounts/*
/data/secure/mgmt/*

- ALLOW TO ROLE_USER
/secure/data/api/*
/secure/data/

/display/view/*
/display/pages/*
/accounts/*
/error/*


-- ALLOW ROLE_SYSTEM, ROLE_ADMINISTRATOR, ROLE_DEVELOPER
/secure/display/* , /secure/studio/*


## STREAMS

/data/streams/me

/data/streams/me/files/

/data/streams/me/photos/upload
 


## Java libraries used in this project:
| Opensource | Version |
|------------|---------|
| architecture-ee | 5.3.2 |
| springframework | 5.3.18 |
| springframework security | 5.6.2|
| springframework Enterprise Integration | 5.5.10|
| springframework oauth | 1.5.6 |
| springframework mobile device | 1.1.5.RELEASE |
| FasterXML | 2.9.9 |
| javax.mail | 1.5.6 |
| Sitemesh| 3.0.1 |

## Opensource Project

GrapesJS https://github.com/artf/grapesjs


## Supported Database 
MySQL 8.x

Oracle 11g 이상 

## TODO
Spring Cloud 사용을 위해서는 5.0 버전 이상 사용이 필수적임.

[java]: https://en.wikipedia.org/wiki/Java_(programming_language)
