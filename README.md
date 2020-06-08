# architecture-community-studio
## 소개

자바 기반의 웹 페이지/API 스튜디오 

- 실시간 웹 페이지 및 REST API 생성 및 배포
- ROLE 기반의 접근 제어
- 사용자 관리

------
## Getting Started

5.1.x 버전 부터는 Java 8+ 지원




## REST API

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
 


## Libraries used in theme: 

- Kendo UI 
The ultimate collection of JavaScript UI components with libraries for jQuery, Angular, React, and Vue. Quickly build eye-catching, high-performance, responsive web applications.

- fancyBox 
jQuery lightbox script for displaying images, videos and more.
Touch enabled, responsive and fully customizable.



## Opensource java libs
| Opensource | Version |
|------------|---------|
| architecture-ee | 5.1.2-RELEASE |
| springframework | 4.3.23.RELEASE |
| springframework security | 4.2.12.RELEASE|
| springframework oauth | 1.5.6 |
| FasterXML | 2.9.9 |
| javax.mail | 1.5.6 |
| Sitemesh| 3.0.1 |

## Opensource Project

GrapesJS https://github.com/artf/grapesjs


## Database 
MySQL 8.x
Oracle

## TODO
Spring Cloud 사용을 위해서는 5.0 버전 사용이 필수적임.
