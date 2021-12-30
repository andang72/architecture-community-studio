# architecture-community-studio
## Overview

[![Build Status](https://github.com/google/auto/actions/workflows/ci.yml/badge.svg)](https://github.com/google/auto/actions/workflows/ci.yml)

[Java][java] based RESTful API Studio.

- 실시간 웹 페이지 및 RESTful API 생성 및 배포
- JWT based stateless session 
- ROLE 기반의 접근 제어
- 사용자 시스템 

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
 

## JavaScript libraries used in theme: 

- Kendo UI 
The ultimate collection of JavaScript UI components with libraries for jQuery, Angular, React, and Vue. Quickly build eye-catching, high-performance, responsive web applications.

- fancyBox 
jQuery lightbox script for displaying images, videos and more.
Touch enabled, responsive and fully customizable.



## Java libraries used in this project:
| Opensource | Version |
|------------|---------|
| architecture-ee | 5.3.1 |
| springframework | 5.3.14 |
| springframework security | 5.6.1  |
| springframework oauth | 1.5.6 |
| FasterXML | 2.9.9 |
| javax.mail | 1.5.6 |
| Sitemesh| 3.0.1 |

## Opensource Project

GrapesJS https://github.com/artf/grapesjs


## Supported Database 
MySQL 8.x
Oracle

## TODO
Spring Cloud 사용을 위해서는 5.0 버전 사용이 필수적임.


[java]: https://en.wikipedia.org/wiki/Java_(programming_language)
