		
		DROP TABLE IF EXISTS AC_UI_STREAMS, AC_UI_STREAMS_PROPERTY, AC_UI_STREAMS_THREAD, AC_UI_STREAMS_MESSAGE ;
		
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

		
		