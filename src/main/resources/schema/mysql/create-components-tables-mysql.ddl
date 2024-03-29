		
	-- DROP TABLE IF EXISTS AC_UI_STREAMS, AC_UI_STREAMS_PROPERTY, AC_UI_STREAMS_THREAD, AC_UI_STREAMS_MESSAGE ;
	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : STREAMS
	-- CREATE : 2020.03.03
	-- UPDATE : 
	-- =================================================	
		
		CREATE TABLE AC_UI_STREAMS (
			CATEGORY_ID             INTEGER NOT NULL COMMENT '카테고리 ID',
			STREAM_ID               INTEGER NOT NULL COMMENT '스트림 ID',
			NAME					VARCHAR(255) NOT NULL COMMENT '이름', 
		 	DISPLAY_NAME 			VARCHAR(255) NOT NULL COMMENT '화면 출력 이름', 
		 	DESCRIPTION             VARCHAR(1000) COMMENT '설명',
		  	CREATION_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '생성일자',
		  	MODIFIED_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '수정일자',	
		 	CONSTRAINT AC_UI_STREAMS_PK PRIMARY KEY (STREAM_ID)
		);
		
		ALTER TABLE AC_UI_STREAMS  COMMENT '스트림 테이블';
		 
		CREATE INDEX AC_UI_STREAMS_NAME_IDX ON AC_UI_STREAMS (NAME);
		 
		
	    CREATE TABLE AC_UI_STREAMS_PROPERTY (
		  STREAM_ID               INTEGER NOT NULL COMMENT '스트림 ID',
		  PROPERTY_NAME          VARCHAR(100)   NOT NULL COMMENT '프로퍼티 이름',
		  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL COMMENT '프로퍼티 값',
		  CONSTRAINT AC_UI_STREAMS_PROPERTY_PK PRIMARY KEY (STREAM_ID, PROPERTY_NAME)
		);	
		
		ALTER TABLE `AC_UI_STREAMS_PROPERTY`  COMMENT '스트림 프로퍼티 테이블';		

		CREATE TABLE AC_UI_STREAMS_THREAD (
			THREAD_ID			INTEGER NOT NULL COMMENT  '스레드 ID',
		  	OBJECT_TYPE			INTEGER NOT NULL COMMENT  '객체 유형',
		 	OBJECT_ID			INTEGER NOT NULL COMMENT  '객체 ID',
		  	ROOT_MESSAGE_ID		INTEGER NOT NULL COMMENT  '최초 게시물 ID',
		  	CREATION_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '생성일자',
		  	MODIFIED_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '수정일자',	
		 	CONSTRAINT AC_UI_STREAMS_THREAD_PK PRIMARY KEY (THREAD_ID)
		);

		ALTER TABLE `AC_UI_STREAMS_THREAD`  COMMENT '스트림 스레드(토픽)';		 
		
		CREATE TABLE AC_UI_STREAMS_MESSAGE (
			MESSAGE_ID			INTEGER NOT NULL COMMENT  '스레드 ID',
		    PARENT_MESSAGE_ID	INTEGER NOT NULL COMMENT  '부모 게시물 ID',
		    THREAD_ID			INTEGER NOT NULL COMMENT  '스레드 ID',
		  	OBJECT_TYPE			INTEGER NOT NULL COMMENT  '객체 유형',
		 	OBJECT_ID			INTEGER NOT NULL COMMENT  '객체 ID',
		    USER_ID				INTEGER NOT NULL COMMENT  '게시자 ID',
		    KEYWORDS			VARCHAR(255) COMMENT  '키워드',
		    SUBJECT				VARCHAR(255) COMMENT  '제목',
		    BODY				LONGTEXT COMMENT  '내용',
		  	CREATION_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '생성일자',
		  	MODIFIED_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '수정일자',	
		 	CONSTRAINT AC_UI_STREAMS_MESSAGE_PK PRIMARY KEY (MESSAGE_ID)
		);

		ALTER TABLE `AC_UI_STREAMS_MESSAGE`  COMMENT '스트림 메시지 테이블';		 
		
		CREATE INDEX AC_UI_STREAMS_MESSAGE_IDX_01 ON AC_UI_STREAMS_MESSAGE (THREAD_ID);
		CREATE INDEX AC_UI_STREAMS_MESSAGE_IDX_02 ON AC_UI_STREAMS_MESSAGE (USER_ID);
		CREATE INDEX AC_UI_STREAMS_MESSAGE_IDX_03 ON AC_UI_STREAMS_MESSAGE (PARENT_MESSAGE_ID);
		CREATE INDEX AC_UI_STREAMS_MESSAGE_IDX_04 ON AC_UI_STREAMS_MESSAGE (OBJECT_TYPE, OBJECT_ID, MODIFIED_DATE);
		

	
	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : PHOTO ALBUMS
	-- CREATE : 2020.04.06
	-- UPDATE : 2020.06.23
	-- =================================================	
	
	DROP TABLE IF EXISTS AC_UI_ALBUM, AC_UI_ALBUM_PROPERTY, AC_UI_ALBUM_IMAGES ;
	
	CREATE TABLE AC_UI_ALBUM (
		ALBUM_ID               INTEGER NOT NULL COMMENT '카테고리 ID',
		NAME				   VARCHAR(255) NOT NULL COMMENT '이름',  
	 	DESCRIPTION            VARCHAR(2000) COMMENT '설명',
	 	USER_ID				   INTEGER NOT NULL COMMENT 'CREATOR',	 	
	  	CREATION_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '생성일자',
	  	MODIFIED_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT '수정일자',	
	 	CONSTRAINT AC_UI_ALBUM_PK PRIMARY KEY (ALBUM_ID)
	);
	ALTER TABLE AC_UI_ALBUM  COMMENT 'IMAGE ALBUM 테이블'; 
			
	CREATE TABLE AC_UI_ALBUM_PROPERTY (
		ALBUM_ID             	INTEGER NOT NULL COMMENT 'ID',
		PROPERTY_NAME         VARCHAR(100)   NOT NULL COMMENT '프로퍼티 이름',
		PROPERTY_VALUE        VARCHAR(1024)  NOT NULL COMMENT '프로퍼티 값',
		CONSTRAINT AC_UI_ALBUM_PROPERTY_PK PRIMARY KEY (ALBUM_ID, PROPERTY_NAME)
	);	
		
	ALTER TABLE `AC_UI_ALBUM_PROPERTY`  COMMENT 'ALBUM 프로퍼티 테이블';
		
	CREATE TABLE AC_UI_ALBUM_IMAGES (
		ALBUM_ID               INTEGER NOT NULL COMMENT 'ALBUM ID', 
		IMAGE_ID 			   INTEGER NOT NULL COMMENT 'IMAGE ID',
		SORT_ORDER			   INTEGER NOT NULL,
		CONSTRAINT AC_UI_ALBUM_IMAGES_PK PRIMARY KEY (ALBUM_ID, IMAGE_ID) 
	);
	
	ALTER TABLE AC_UI_ALBUM_IMAGES  COMMENT 'IMAGE ALBUM IMAGES'; 
	
	-- ADDED 2012.06.23
	
	CREATE TABLE AC_UI_ALBUM_CONTENTS (
		ALBUM_ID               INTEGER NOT NULL COMMENT 'ALBUM ID', 
		CONTENT_TYPE           INTEGER NOT NULL COMMENT 'CONTENT TYPE',
		CONTENT_ID 			   INTEGER NOT NULL COMMENT 'CONTENT ID',
		SORT_ORDER			   INTEGER NOT NULL,
		CONSTRAINT AC_UI_ALBUM_CONTENTS_PK PRIMARY KEY (ALBUM_ID, CONTENT_TYPE, CONTENT_ID) 
	);
	
	ALTER TABLE AC_UI_ALBUM_CONTENTS  COMMENT 'ALBUM CONTENTS'; 		

	-- =================================================  
	-- PACKAGE   : UI  
	-- COMPONENT : RESOURCE ASSET
	-- CREATE    : 2021.05.31
	-- UPDATE    : 
	-- =================================================	
  	
	CREATE TABLE AC_UI_ASSETS
    (	
	ASSET_ID				INTEGER NOT NULL COMMENT 'ASSET ID', 
	OBJECT_ID				INTEGER NOT NULL COMMENT 'OBJECT ID', 
	OBJECT_TYPE				INTEGER NOT NULL COMMENT 'OBJECT TYPE', 
	LINK_ID					VARCHAR(255) NOT NULL COMMENT'유일한 랜덤 키', 
	FILE_NAME				VARCHAR(255) NOT NULL COMMENT '파일 이름', 
	FILE_SIZE				INTEGER	NOT NULL COMMENT  'File Size',
	DESCRIPTION             VARCHAR(1000) COMMENT '설명',
	ENABLED          		TINYINT  DEFAULT 1 COMMENT  'ENABLED 여부',
	SECURED          		TINYINT  DEFAULT 1 COMMENT  'SECURED 여부',
	USER_ID					INTEGER	NOT NULL COMMENT  '생성자 ID',
	CREATION_DATE			DATETIME NULL COMMENT '생성일자',
	MODIFIED_DATE			DATETIME NULL COMMENT '수정일자',	
	CONSTRAINT AC_UI_ASSETS_PK PRIMARY KEY (ASSET_ID)
    ) ;
   
    CREATE INDEX AC_UI_ASSETS_IDX_01 ON AC_UI_ASSETS ( OBJECT_TYPE, OBJECT_ID);
    CREATE INDEX AC_UI_ASSETS_IDX_02 ON AC_UI_ASSETS ( LINK_ID );
    
	CREATE TABLE AC_UI_ASSETS_DATA ( 
      ASSET_ID  INTEGER COMMENT 'ID',
	  ASSET_DATA LONGBLOB COMMENT 'ASSET 데이터' ,
	  CONSTRAINT AC_UI_ASSETS_DATA_PK PRIMARY KEY (ASSET_ID)
	) ;
 
	ALTER TABLE AC_UI_ASSETS_DATA  COMMENT 'ASSET 데이터 테이블'; 
  
   
    CREATE TABLE AC_UI_ASSETS_PROPERTY (
	 ASSET_ID             INTEGER NOT NULL COMMENT 'ID',
	 PROPERTY_NAME         VARCHAR(100)   NOT NULL COMMENT '프로퍼티 이름',
	 PROPERTY_VALUE        VARCHAR(1024)  NOT NULL COMMENT '프로퍼티 값',
	 CONSTRAINT AC_UI_ASSETS_PROPERTY_PK PRIMARY KEY (ASSET_ID, PROPERTY_NAME)
    );	
		
	ALTER TABLE AC_UI_ASSETS_PROPERTY  COMMENT 'ASSET 프로퍼티 테이블';  
	
	-- =================================================  
	-- PACKAGE: UI
	-- COMPONENT: CONTENT TEMPLATES
	-- CREATE : 2020.09.09
	-- UPDATE : 2020.09.09
	-- =================================================	
	
	CREATE TABLE AC_UI_CONTENT_TEMPLATES (	
   	  TEMPLATE_ID        	     INTEGER NOT NULL COMMENT 'TEMPLATE_ID',	
   	  OBJECT_ID				 INTEGER NOT NULL COMMENT 'OBJECT ID', 
	  OBJECT_TYPE			 INTEGER NOT NULL COMMENT 'OBJECT TYPE', 
   	  NAME		     		 VARCHAR(255) NOT NULL COMMENT 'FORM NAME',
   	  DISPLAY_NAME		     VARCHAR(255) COMMENT 'FORM DISPLAY NAME',
	  DESCRIPTION            VARCHAR(2000)COMMENT 'FORM DESCRIPTION',
	  
	  SUBJECT				 VARCHAR(500),
	  BODY					 LONGTEXT,
		    
	  CREATOR				 INTEGER NOT NULL COMMENT 'CREATOR',
	  LAST_MODIFIER		     INTEGER NOT NULL COMMENT 'LAST_MODIFIRE',
	  CREATION_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT 'CREATION DATE',
	  MODIFIED_DATE          DATETIME DEFAULT  NOW() NOT NULL COMMENT 'MODIFIED DATE',	
	  CONSTRAINT AC_UI_CONTENT_TEMPLATES_PK PRIMARY KEY (TEMPLATE_ID)
	);
	
	ALTER TABLE AC_UI_CONTENT_TEMPLATES  COMMENT 'CONTENT TEMPLETS'; 
	
	CREATE INDEX AC_UI_CONTENT_TEMPLATES_IDX_1 ON AC_UI_CONTENT_TEMPLATES (OBJECT_TYPE, OBJECT_ID);
	CREATE INDEX AC_UI_CONTENT_TEMPLATES_IDX_2 ON AC_UI_CONTENT_TEMPLATES (NAME);
	

	-- =================================================  
	-- PACKAGE: UI
	-- COMPONENT: OUTBOUND EMAIL
	-- CREATE : 2020.09.17
	-- UPDATE : 2020.09.17
	-- =================================================	
	CREATE TABLE AC_UI_BULK_EMAIL (	 
		BULK_EMAIL_ID 			INTEGER NOT NULL COMMENT 'BULK EMAIL ID',
   	  	TEMPLATE_ID 			INTEGER DEFAULT 0 NOT NULL COMMENT 'TEMPLATE ID',
   	  	FROM_EMAIL_ADDRESS 		VARCHAR(255) NOT NULL COMMENT 'FORM EMAIL ADDRESS', 
   		CREATOR				 	INTEGER NOT NULL COMMENT 'CREATOR',
		LAST_MODIFIER		    INTEGER NOT NULL COMMENT 'LAST_MODIFIRE',
		CREATION_DATE          	DATETIME DEFAULT  NOW() NOT NULL COMMENT 'CREATION DATE',
		MODIFIED_DATE          	DATETIME DEFAULT  NOW() NOT NULL COMMENT 'MODIFIED DATE',	
		CONSTRAINT AC_UI_BULK_EMAIL_PK PRIMARY KEY (BULK_EMAIL_ID)
	);  	
	
	ALTER TABLE AC_UI_BULK_EMAIL COMMENT 'OUTBOUND BULK EMAL';
	
	CREATE TABLE AC_UI_BULK_EMAIL_TEMPLATES_DATA (	
		BULK_EMAIL_ID 			INTEGER NOT NULL COMMENT 'BULK EMAIL ID',
		TEMPLATE_ID        	   	INTEGER NOT NULL COMMENT 'TEMPLATE ID',	
		DATA_KEY         		VARCHAR(100)   NOT NULL COMMENT 'DATA Name',
		DATA_VALUE        		VARCHAR(1024)  NOT NULL COMMENT 'DATA Value',	
   	  	CONSTRAINT AC_UI_BULK_EMAIL_TEMPLATES_DATA_PK PRIMARY KEY (BULK_EMAIL_ID, TEMPLATE_ID, DATA_KEY )
	);  
	
	ALTER TABLE AC_UI_BULK_EMAIL_TEMPLATES_DATA COMMENT 'OUTBOUND BULK EMAL TEMPLATES DATA'; 
	
	CREATE TABLE AC_UI_BULK_EMAIL_ENTRY (	 
		BULK_EMAIL_ID 			INTEGER NOT NULL COMMENT 'BULK_EMAIL_ID',
   	  	TEMPLATE_ID 			INTEGER DEFAULT 0 NOT NULL COMMENT 'TEMPLATE ID',
   	  	BCC_ADDRESSES 			VARCHAR(255) NOT NULL COMMENT 'BCC EMAIL ADDRESS',
   	  	CC_ADDRESSES 			VARCHAR(255) NOT NULL COMMENT 'CC EMAIL NAME',
   		TO_ADDRESSES			VARCHAR(255) NOT NULL COMMENT 'TO EMAIL NAME',
		CONSTRAINT AC_UI_BULK_EMAIL_ENTRY_PK PRIMARY KEY (BULK_EMAIL_ID)
	);  	
	
	ALTER TABLE AC_UI_BULK_EMAIL_ENTRY COMMENT 'BULK EMAIL ENTRY';
	