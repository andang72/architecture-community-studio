-- testdb 생성
CREATE DATABASE islanddb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- test 계성 생성
CREATE USER 'studio'@'%' IDENTIFIED BY 'studio007A@';
-- testdb에 대한 권한을 test 에게 부여한다. 
GRANT ALL PRIVILEGES ON testdb.* TO 'studio'@'%' WITH GRANT OPTION;