	-- MySQL v5.6.5
	
	
	
	-- =================================================  
	-- PACKAGE: FRAMEWORK   
	-- CREATE : 2018.11.6
	-- UPDATE : 
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_PROPERTY, AC_UI_SEQUENCER ;
	
	CREATE TABLE AC_UI_PROPERTY (
		PROPERTY_NAME	VARCHAR(100)   NOT NULL  COMMENT '프로퍼티 이름'  ,
		PROPERTY_VALUE	VARCHAR(1024)  NOT NULL  COMMENT '프로퍼티 값'  ,
		CONSTRAINT AC_UI_PROPERTY_PK PRIMARY KEY (PROPERTY_NAME)
	);
	
	ALTER TABLE  AC_UI_PROPERTY COMMENT '애플리케이션 전역에서 사용되는 프로퍼티';
	
	CREATE TABLE AC_UI_SEQUENCER(
	SEQUENCER_ID	INTEGER NOT NULL COMMENT '시퀀서 ID',
	NAME			VARCHAR(100) NOT NULL COMMENT '시퀀서 이름', 
	DISPLAY_NAME    VARCHAR(255) COMMENT '출력시 보여줄 이름' ,
	VALUE			INTEGER NOT NULL COMMENT '시퀀서 값',
	CONSTRAINT AC_UI_SEQUENCER_PK PRIMARY KEY (SEQUENCER_ID)
	);
	
	CREATE UNIQUE INDEX  `AC_UI_SEQUENCER_UIDX1` ON `AC_UI_SEQUENCER` (`NAME`);

	ALTER TABLE  `AC_UI_SEQUENCER`  COMMENT '애플리케이션 전역에서 사용되는 시퀀서';

	
	-- =================================================  
	-- PACKAGE: SECURITY   
	-- CREATE : 2018.11.6
	-- UPDATE : 
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_USER, AC_UI_USER_PROPERTY, AC_UI_ROLE, AC_UI_USER_ROLES, AC_UI_USER_LOGIN_TOKEN ;	
	
	-- User
		CREATE TABLE AC_UI_USER (
		  USER_ID                INTEGER NOT NULL,
		  USERNAME               VARCHAR(100) NOT NULL,
		  PASSWORD_HASH          VARCHAR(64)  NOT NULL,
		  NAME                   VARCHAR(100),		  
		  NAME_VISIBLE           TINYINT  DEFAULT 1 , 
		  FIRST_NAME             VARCHAR(100),		  
		  LAST_NAME              VARCHAR(100),		
		  EMAIL                  VARCHAR(100) NOT NULL,
		  EMAIL_VISIBLE          TINYINT  DEFAULT 1,
		  USER_ENABLED           TINYINT  DEFAULT 1, 
		  USER_EXTERNAL          TINYINT  DEFAULT 0, 
		  STATUS                 TINYINT  DEFAULT 0,
		  CREATION_DATE          DATETIME DEFAULT  NOW() NOT NULL,
		  MODIFIED_DATE          DATETIME DEFAULT  NOW() NOT NULL,		    
		  CONSTRAINT AC_UI_USER_PK PRIMARY KEY (USER_ID)
		);		
		
		CREATE UNIQUE INDEX AC_UI_USER_IDX_USERNAME ON AC_UI_USER (USERNAME);
		
		ALTER TABLE  `AC_UI_USER`  COMMENT '사용자 테이블';
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`USER_ID` IS 'ID'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`USERNAME` IS '로그인 아이디'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`NAME` IS '전체 이름'; */
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`PASSWORD_HASH` IS '암호화된 패스워드'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`NAME_VISIBLE` IS '이름 공개 여부'; */        
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`FIRST_NAME` IS '이름'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`LAST_NAME` IS '성'; */        
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`EMAIL` IS '메일주소'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`EMAIL_VISIBLE` IS '메일주소 공개여부'; */           
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`USER_ENABLED` IS '계정 사용여부'; */     
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`STATUS` IS '계정 상태'; */	    
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`CREATION_DATE` IS '생성일자'; */	    
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_USER`.`MODIFIED_DATE` IS '수정일자'; */	
        
		
		CREATE TABLE AC_UI_USER_PROPERTY (
		  USER_ID             	INTEGER NOT NULL COMMENT 'ID',
		  PROPERTY_NAME         VARCHAR(100)   NOT NULL COMMENT '프로퍼티 이름',
		  PROPERTY_VALUE        VARCHAR(1024)  NOT NULL COMMENT '프로퍼티 값',
		  CONSTRAINT AC_UI_USER_PROPERTY_PK PRIMARY KEY (USER_ID, PROPERTY_NAME)
		);	
		
		ALTER TABLE `AC_UI_USER_PROPERTY`  COMMENT 'USER 프로퍼티 테이블';
		
        -- Role
		CREATE TABLE AC_UI_ROLE (
		  ROLE_ID                     INTEGER NOT NULL,
		  NAME                        VARCHAR(100)   NOT NULL,
		  DESCRIPTION              VARCHAR(1000)  NOT NULL,
		  CREATION_DATE           DATETIME DEFAULT  NOW() NOT NULL,
		  MODIFIED_DATE           DATETIME DEFAULT  NOW() NOT NULL,	
		  CONSTRAINT AC_UI_ROLE_PK PRIMARY KEY (ROLE_ID)
		);
		
		ALTER TABLE `AC_UI_ROLE`  COMMENT '롤 테이블';
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_ROLE`.`ROLE_ID` IS '롤 ID'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_ROLE`.`NAME` IS '롤 이름'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_ROLE`.`DESCRIPTION` IS '설명'; */ 	
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_ROLE`.`CREATION_DATE` IS '생성일자'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_ROLE`.`MODIFIED_DATE` IS '수정일자'; */ 				
		
		CREATE UNIQUE INDEX AC_UI_ROLE_NAME_IDX ON AC_UI_ROLE (NAME);
		
		-- User Roles  
		CREATE TABLE AC_UI_USER_ROLES (
		  USER_ID                 INTEGER NOT NULL,
		  ROLE_ID                 INTEGER NOT NULL,
		  CONSTRAINT AC_UI_USER_ROLES_PK PRIMARY KEY (USER_ID, ROLE_ID)
		);

		ALTER TABLE `AC_UI_USER_ROLES`  COMMENT '사용자 롤 테이블';
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `AC_UI_USER_ROLES`.`USER_ID` IS '그룹 ID'; */ 
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `AC_UI_USER_ROLES`.`ROLE_ID` IS '롤 ID'; */ 
		
		
		-- User remember me
		CREATE TABLE AC_UI_USER_LOGIN_TOKEN (
			UUID			VARCHAR(100) NOT NULL,
			USERNAME		VARCHAR(100) NOT NULL, 
		 	TOKEN 			VARCHAR(100) NOT NULL, 
		 	LAST_USE_DATE	DATETIME DEFAULT NOW() NOT NULL
		);
 
		ALTER TABLE `AC_UI_USER_LOGIN_TOKEN`  COMMENT '인증 정보 저장 테이블';
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `AC_UI_USER_LOGIN_TOKEN`.`UUID` IS '시퀀스 값'; */ 
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `AC_UI_USER_LOGIN_TOKEN`.`USERNAME` IS '로그인아이디'; */ 	
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `AC_UI_USER_LOGIN_TOKEN`.`TOKEN` IS '인증 토큰 값'; */ 
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `AC_UI_USER_LOGIN_TOKEN`.`LAST_USE_DATE` IS '마지막 사용일자'; */  
		
		
	--  COMPANY	
	DROP TABLE IF EXISTS AC_UI_COMPANY, AC_UI_COMPANY_PROPERTY ;
	
		CREATE TABLE AC_UI_COMPANY (
			  COMPANY_ID                INTEGER NOT NULL COMMENT '회사 ID',
			  DISPLAY_NAME             	VARCHAR(255)   NOT NULL COMMENT '출력시 보여줄 회사 이름' ,
			  NAME                      VARCHAR(100)   NOT NULL COMMENT '회사 이름',
			  DOMAIN_NAME				VARCHAR(100) COMMENT '도메인 명',
			  DESCRIPTION               VARCHAR(1000)  COMMENT '설명',
			  CREATION_DATE            	DATETIME DEFAULT  NOW() NOT NULL COMMENT '생성일자',
			  MODIFIED_DATE            	DATETIME DEFAULT  NOW() NOT NULL COMMENT '수정일자',	
			  CONSTRAINT AC_UI_COMPANY_PK 	PRIMARY KEY (COMPANY_ID)
		);
		
		CREATE INDEX AC_UI_COMPANY_DATE_IDX ON AC_UI_COMPANY(CREATION_DATE) ;		
		CREATE INDEX AC_UI_COMPANY_NAME_IDX ON AC_UI_COMPANY(NAME);			
		CREATE INDEX AC_UI_COMPANY_DOMAIN_IDX ON AC_UI_COMPANY(DOMAIN_NAME);			
		
		ALTER TABLE `AC_UI_COMPANY`  COMMENT '회사 테이블';
		
	    CREATE TABLE AC_UI_COMPANY_PROPERTY (
		  COMPANY_ID               INTEGER NOT NULL COMMENT '회사 ID',
		  PROPERTY_NAME          VARCHAR(100)   NOT NULL COMMENT '프로퍼티 이름',
		  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL COMMENT '프로퍼티 값',
		  CONSTRAINT AC_UI_COMPANY_PROPERTY_PK PRIMARY KEY (COMPANY_ID, PROPERTY_NAME)
		);	
		
		ALTER TABLE `AC_UI_COMPANY_PROPERTY`  COMMENT '회사 프로퍼티 테이블';

	
	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : MENU  
	-- CREATE : 2019.10.11
	-- UPDATE : 
	-- =================================================	
		DROP TABLE IF EXISTS AC_UI_MENU, AC_UI_MENU_PROPERTY, AC_UI_MENU_ITEM, AC_UI_MENU_ITEM_PROPERTY ;
		
		CREATE TABLE AC_UI_MENU (
		MENU_ID			INTEGER NOT NULL,
		NAME			VARCHAR(255) NULL,
		DESCRIPTION		VARCHAR(1000),
		CREATION_DATE	DATETIME DEFAULT NOW() NOT NULL,
		MODIFIED_DATE	DATETIME DEFAULT NOW() NOT NULL,
		CONSTRAINT AC_UI_MENU_PK PRIMARY KEY (MENU_ID)
		);
		
		-- CREATE UNIQUE INDEX AC_UI_MENU_UIDX1 ON AC_UI_MENU (NAME);
		
		ALTER TABLE  `AC_UI_MENU`  COMMENT '메뉴 테이블';
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU`.`MENU_ID` IS '메뉴 ID'; */
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU`.`NAME` IS '메뉴 이름'; */
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU`.`DESCRIPTION` IS '메뉴 설명'; */
		ALTER TABLE `AC_UI_MENU`  COMMENT 'UI 메뉴 테이블';
	
		CREATE TABLE AC_UI_MENU_PROPERTY (
			  MENU_ID				INTEGER NOT NULL, 
			  PROPERTY_NAME          VARCHAR(100)   NOT NULL,
			  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL,
			  CONSTRAINT AC_UI_MENU_PROPERTY_PK PRIMARY KEY (MENU_ID, PROPERTY_NAME)
		);	
	
		ALTER TABLE  `AC_UI_MENU_PROPERTY`  COMMENT 'MENU 프로퍼티 테이블';
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_PROPERTY`.`MENU_ID` IS 'MENU ID'; */  
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_PROPERTY`.`PROPERTY_NAME` IS '프로퍼티 이름'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_PROPERTY`.`PROPERTY_VALUE` IS '프로퍼티 값'; */  
		
		
		CREATE TABLE AC_UI_MENU_ITEM (
			MENU_ID			INTEGER NOT NULL,
			PARENT_ID		INTEGER NOT NULL,
			MENU_ITEM_ID	INTEGER NOT NULL,
			NAME			VARCHAR(255) NOT NULL,
			SORT_ORDER		INTEGER DEFAULT 1 NULL,
			DESCRIPTION		VARCHAR(4000) NULL,
			LINK_URL		VARCHAR(255) NULL,
			ROLES			VARCHAR(255) NULL,
			CREATION_DATE	DATETIME DEFAULT NOW() NOT NULL,
			MODIFIED_DATE 	DATETIME DEFAULT NOW() NOT NULL,
			CONSTRAINT AC_UI_MENU_ITEM_PK PRIMARY KEY (MENU_ITEM_ID)
		);
	   
		CREATE INDEX AC_UI_MENU_ITEM_IDX1   ON AC_UI_MENU_ITEM (MENU_ID, PARENT_ID);
	   	CREATE INDEX AC_UI_MENU_ITEM_IDX2   ON AC_UI_MENU_ITEM (PARENT_ID);
	 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_ITEM`.`MENU_ID` IS '메뉴 ID'; */
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_ITEM`.`PARENT_ID` IS '부모 메뉴 아이템 이름'; */
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_ITEM`.`MENU_ITEM_ID` IS '메뉴 아이템 ID'; */
		ALTER TABLE `AC_UI_MENU_ITEM`  COMMENT 'UI 메뉴 아이템 테이블';
	
		CREATE TABLE AC_UI_MENU_ITEM_PROPERTY (
			  MENU_ITEM_ID				INTEGER NOT NULL, 
			  PROPERTY_NAME          VARCHAR(100)   NOT NULL,
			  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL,
			  CONSTRAINT AC_UI_MENU_ITEM_PROPERTY_PK PRIMARY KEY (MENU_ITEM_ID, PROPERTY_NAME)
		);	
	
		ALTER TABLE  `AC_UI_MENU_ITEM_PROPERTY`  COMMENT 'MENU_ITEM 프로퍼티 테이블'; 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_ITEM_PROPERTY`.`MENU_ITEM_ID` IS 'MENU_ITEM ID'; */ 	
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_ITEM_PROPERTY`.`PROPERTY_NAME` IS '프로퍼티 이름'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_MENU_ITEM_PROPERTY`.`PROPERTY_VALUE` IS '프로퍼티 값'; */  
		
					

	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : USER AVATAR 
	-- CREATE : 2019.09.18
	-- UPDATE : 
	-- =================================================	
		DROP TABLE IF EXISTS AC_UI_AVATAR_IMAGE, AC_UI_AVATAR_IMAGE_DATA  ;
		
		CREATE TABLE AC_UI_AVATAR_IMAGE (	
			`AVATAR_IMAGE_ID` INTEGER NOT NULL COMMENT 'ID', 
			`USER_ID` INTEGER NOT NULL COMMENT '사용자 ID', 
			`PRIMARY_IMAGE` TINYINT DEFAULT 1 COMMENT '주 이미지 여부 0 : ture', 
			`FILE_NAME` VARCHAR(255) NOT NULL COMMENT '파일이름', 
			`FILE_SIZE` DECIMAL(38,0) NOT NULL COMMENT '파일크기', 
			`CONTENT_TYPE` VARCHAR(50) NOT NULL COMMENT 'CONTENT TYPE', 
			`CREATION_DATE` DATETIME DEFAULT NOW() NOT NULL COMMENT '생성일', 
			`MODIFIED_DATE` DATETIME DEFAULT NOW() NOT NULL COMMENT '수정일', 
			CONSTRAINT AC_UI_AVATAR_IMAGE_PK PRIMARY KEY (AVATAR_IMAGE_ID)
		);
		CREATE INDEX  AC_UI_AVATAR_IMAGE_IDX1  ON AC_UI_AVATAR_IMAGE  (`USER_ID`) ;

		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`AVATAR_IMAGE_ID` IS 'ID'; */	 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`USER_ID` IS '사용자 ID'; */	 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`PRIMARY_IMAGE` IS '주 이미지 여부'; */	 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`FILE_NAME` IS '이미지 파일 이름'; */	 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`FILE_SIZE` IS '이미지 파일 크기'; */	 
	   /* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`CONTENT_TYPE` IS 'CONTENT TYPE 값'; */	 
	   /* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`CREATION_DATE` IS '생성일'; */	 
	   /* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE`.`MODIFIED_DATE` IS '수정일'; */	 
	   ALTER TABLE   `AC_UI_AVATAR_IMAGE`  COMMENT '아바타 이미지 테이블';
 
  		CREATE TABLE  `AC_UI_AVATAR_IMAGE_DATA` (	
  			`AVATAR_IMAGE_ID` INTEGER NOT NULL COMMENT 'ID', 
			`AVATAR_IMAGE_DATA` LONGBLOB COMMENT '이미지 데이터', 
	 		CONSTRAINT `AC_UI_AVATAR_IMAGE_DATA_PK` PRIMARY KEY (`AVATAR_IMAGE_ID`)
	 	);

		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE_DATA`.`AVATAR_IMAGE_ID` IS 'ID'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN  `AC_UI_AVATAR_IMAGE_DATA`.`AVATAR_IMAGE_DATA` IS '이미지 데이터'; */ 
		ALTER TABLE   `AC_UI_AVATAR_IMAGE_DATA`  COMMENT '아바타 이미지 데이터 테이블'; 	 
		
	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : PAGE 
	-- CREATE : 2018.09.19
	-- UPDATE :
	-- 2018.09.17 - AC_UI_PAGE_BODY_VERSION.SCRIPT 추가 
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_PAGE, AC_UI_PAGE_BODY, AC_UI_PAGE_BODY_VERSION, AC_UI_PAGE_VERSION , AC_UI_PAGE_PROPERTY ;	
	
	CREATE TABLE AC_UI_PAGE(	
		PAGE_ID						INTEGER NOT NULL,
		OBJECT_TYPE					INTEGER NOT NULL,
		OBJECT_ID					INTEGER NOT NULL,
		NAME						VARCHAR(255) NOT NULL,	
		VERSION_ID					INTEGER DEFAULT 1 NOT NULL,
		USER_ID						INTEGER NOT NULL,
		READ_COUNT          		INTEGER DEFAULT 0  NOT NULL,
		PATTERN						VARCHAR(255) NULL,	
		CREATION_DATE				DATETIME NULL,
		MODIFIED_DATE				DATETIME NULL,			
		CONSTRAINT AC_UI_PAGE_PK PRIMARY KEY (PAGE_ID)
	);

	CREATE UNIQUE INDEX AC_UI_PAGE_IDX1 ON AC_UI_PAGE (NAME);
	CREATE INDEX AC_UI_PAGE_IDX2   ON AC_UI_PAGE (OBJECT_TYPE, OBJECT_ID, PAGE_ID);
	CREATE INDEX AC_UI_PAGE_IDX3   ON AC_UI_PAGE (USER_ID);

	CREATE TABLE AC_UI_PAGE_BODY
	(	
		BODY_ID						INTEGER NOT NULL,
		PAGE_ID						INTEGER NOT NULL,
		BODY_TYPE					INTEGER NOT NULL,
		BODY_TEXT					LONGTEXT,
		CONSTRAINT AC_UI_PAGE_BODY_PK PRIMARY KEY (BODY_ID)
    ) ;
	
		
	CREATE TABLE AC_UI_PAGE_BODY_VERSION
	(	
		BODY_ID					INTEGER NOT NULL,
		PAGE_ID						INTEGER NOT NULL,
		VERSION_ID				INTEGER DEFAULT 1 NOT NULL ,
		CONSTRAINT AC_UI_PAGE_BODY_VERSION_PK PRIMARY KEY (BODY_ID, PAGE_ID, VERSION_ID )
   );
   
   CREATE INDEX AC_UI_PAGE_BODY_VERSION_IDX1   ON AC_UI_PAGE_BODY_VERSION (PAGE_ID, VERSION_ID);

		
  CREATE TABLE AC_UI_PAGE_VERSION
   (	
	PAGE_ID					INTEGER NOT NULL,
	VERSION_ID				INTEGER DEFAULT 1 NOT NULL ,
	STATE					VARCHAR(20), 
	TITLE					VARCHAR(255), 
	SECURED          		TINYINT  DEFAULT 1,
	CONTENT_TYPE			VARCHAR(255), 
	TEMPLATE				VARCHAR(255), 
	SCRIPT					VARCHAR(255), 
	PATTERN					VARCHAR(255), 	
	SUMMARY					VARCHAR(4000), 
	USER_ID					INTEGER	NOT NULL,
	CREATION_DATE			DATETIME NULL,
	MODIFIED_DATE			DATETIME NULL,	
	CONSTRAINT AC_UI_PAGE_VERSION_PK PRIMARY KEY (PAGE_ID, VERSION_ID)
   ) ;

		
	CREATE INDEX AC_UI_PAGE_VERSION_IDX1   ON AC_UI_PAGE_VERSION (CREATION_DATE);
	CREATE INDEX AC_UI_PAGE_VERSION_IDX2   ON AC_UI_PAGE_VERSION (MODIFIED_DATE);
	CREATE INDEX AC_UI_PAGE_VERSION_IDX3   ON AC_UI_PAGE_VERSION (TITLE);
	CREATE INDEX AC_UI_PAGE_VERSION_IDX4   ON AC_UI_PAGE_VERSION (PAGE_ID, STATE);

			
	CREATE TABLE AC_UI_PAGE_PROPERTY (
		  PAGE_ID				INTEGER NOT NULL,
		  VERSION_ID			INTEGER DEFAULT 1 NOT NULL,
		  PROPERTY_NAME          VARCHAR(100)   NOT NULL,
		  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL,
		  CONSTRAINT AC_UI_PAGE_PROPERTY_PK PRIMARY KEY (PAGE_ID, VERSION_ID, PROPERTY_NAME)
	);	

	ALTER TABLE  `AC_UI_PAGE_PROPERTY`  COMMENT 'PAGE 프로퍼티 테이블';
	/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_PAGE_PROPERTY`.`PAGE_ID` IS 'PAGE ID'; */ 
	/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_PAGE_PROPERTY`.`VERSION_ID` IS 'PAGE VERSION'; */ 	
	/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_PAGE_PROPERTY`.`PROPERTY_NAME` IS '프로퍼티 이름'; */ 
	/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_PAGE_PROPERTY`.`PROPERTY_VALUE` IS '프로퍼티 값'; */ 	 
		

	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : API 
	-- CREATE : 2018.10.18
	-- UPDATE : 2021.04.29
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_API , AC_UI_API_PROPERTY ;
	
	CREATE TABLE AC_UI_API (
		OBJECT_TYPE				INTEGER NOT NULL,
		OBJECT_ID				INTEGER NOT NULL,	 
		API_ID					INTEGER NOT NULL,
		TITLE					VARCHAR(255), 
		API_NAME				VARCHAR(255) NOT NULL,
		API_VERSION				VARCHAR(255) NOT NULL,
		DESCRIPTION				VARCHAR(1000), 
		CONTENT_TYPE			VARCHAR(50), 
		SECURED					TINYINT  DEFAULT 1,
		ENABLED					TINYINT  DEFAULT 0,	
		SCRIPT					VARCHAR(255), 
		PATTERN					VARCHAR(255), 
		CREATOR_ID				INTEGER NOT NULL,
		CREATION_DATE          	DATETIME DEFAULT  NOW() NOT NULL,
		MODIFIED_DATE			DATETIME DEFAULT NOW() NOT NULL,
		CONSTRAINT AC_UI_API_PK PRIMARY KEY (API_ID)
	);	
	
	CREATE INDEX AC_UI_API_IDX_01 ON AC_UI_API (OBJECT_TYPE, OBJECT_ID);

	CREATE TABLE `AC_UI_API_PROPERTY` (	
	`API_ID` DECIMAL(38,0) NOT NULL COMMENT 'API ID', 
	`PROPERTY_NAME` VARCHAR(100) NOT NULL COMMENT '프로퍼티 이름', 
	`PROPERTY_VALUE` VARCHAR(1024) NOT NULL COMMENT '프로퍼티 값', 
	CONSTRAINT `AC_UI_API_PROPERTY_PK` PRIMARY KEY (`API_ID`, `PROPERTY_NAME`));

	ALTER TABLE  `AC_UI_API_PROPERTY`  COMMENT 'API 프로퍼티 테이블';		
	
	
	
	-- UPDATE : 2021.04.29 
	-- ADD HTTP Partameters table.
	
	CREATE TABLE AC_UI_HTTP_PARAMETERS (
		OBJECT_TYPE				INTEGER NOT NULL COMMENT '객체 타입',
		OBJECT_ID				INTEGER NOT NULL COMMENT '객체 아이디',	
		IS_HEADER 				TINYINT DEFAULT 0 COMMENT '객체 아이디',
		IS_REQUESTPARAM			TINYINT DEFAULT 1 COMMENT 'REQUEST PARAM 뎌ㅜ',
		IS_PATHVARIABLE			TINYINT DEFAULT 0 COMMENT 'PATH VARIABLE 여부',
		PARAM_KEY				VARCHAR(255) NOT NULL COMMENT '키',
		PARAM_VALUE				VARCHAR(1000) COMMENT '값',
		DEFAULT_VALUE 			VARCHAR(255) COMMENT '기본값',
		CREATION_DATE          	DATETIME DEFAULT NOW() NOT NULL COMMENT '생성일자',
		MODIFIED_DATE			DATETIME DEFAULT NOW() NOT NULL COMMENT '수정일자',
	CONSTRAINT AC_UI_HTTP_PARAMETERS_PK PRIMARY KEY (OBJECT_TYPE, OBJECT_ID, PARAM_KEY));
	ALTER TABLE  `AC_UI_HTTP_PARAMETERS`  COMMENT 'HTTP PARAMETERS';	
	
	-- =================================================  
	--  VIEWCOUNT	
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_VIEWCOUNT  ;
	
	CREATE TABLE AC_UI_VIEWCOUNT(	
		ENTITY_TYPE					INTEGER NOT NULL COMMENT '객체 타입',
		ENTITY_ID					INTEGER NOT NULL COMMENT '객체 아이디',
		VIEWCOUNT					INTEGER NOT NULL COMMENT '카운트',
		CONSTRAINT AC_UI_VIEWCOUNT_PK PRIMARY KEY (ENTITY_TYPE, ENTITY_ID)
   	);		
   	ALTER TABLE  `AC_UI_VIEWCOUNT`  COMMENT '뷰 카운트 테이블';	
	
   	
	-- =================================================  
	--  IMAGE	
	-- =================================================	   	
	DROP TABLE IF EXISTS AC_UI_IMAGE , AC_UI_IMAGE_PROPERTY, AC_UI_IMAGE_DATA, AC_UI_IMAGE_LINK ;
	
	CREATE TABLE AC_UI_IMAGE (
		IMAGE_ID                 INTEGER NOT NULL COMMENT 'ID',
		OBJECT_TYPE              INTEGER NOT NULL COMMENT '객체 TYPE',
		OBJECT_ID                INTEGER NOT NULL COMMENT '객체 ID',
		FILE_NAME                VARCHAR(255)   NOT NULL COMMENT '파일 이름',
		FILE_SIZE                INTEGER   NOT NULL COMMENT '파일 크기',
		CONTENT_TYPE             VARCHAR(50)  NOT NULL COMMENT 'CONTENT TYPE',			  
		USER_ID				   	 INTEGER NOT NULL COMMENT '생성자 ID',	 	
		CREATION_DATE            DATETIME DEFAULT  NOW() NOT NULL COMMENT '생성일',
		MODIFIED_DATE            DATETIME DEFAULT  NOW() NOT NULL COMMENT '수정일',	
		CONSTRAINT AC_UI_IMAGE_PK PRIMARY KEY (IMAGE_ID)
	);		        
		
		
		CREATE INDEX AC_UI_IMAGE_IDX1 ON AC_UI_IMAGE( OBJECT_TYPE, OBJECT_ID ) ;	
		ALTER TABLE `AC_UI_IMAGE`  COMMENT '이미지 테이블';
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`IMAGE_ID` IS 'ID'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`OBJECT_TYPE` IS '이미지와 연관된 모델 모델 ID'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`OBJECT_ID` IS '이미지와 연관된 모델 ID'; */
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`FILE_NAME` IS '이미지 파일 이름'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`FILE_SIZE` IS '이미지 파일 크기'; */        
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`CONTENT_TYPE` IS 'CONTENT TYPE 값'; */ 
		/* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`CREATION_DATE` IS '생성일'; */ 
        /* Moved to CREATE TABLE
COMMENT ON COLUMN `AC_UI_IMAGE`.`MODIFIED_DATE` IS '수정일'; */

			
		CREATE TABLE AC_UI_IMAGE_PROPERTY (
			  IMAGE_ID				INTEGER NOT NULL,
			  PROPERTY_NAME          VARCHAR(100)   NOT NULL,
			  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL,
			  CONSTRAINT AC_UI_IMAGE_PROPERTY_PK PRIMARY KEY (IMAGE_ID, PROPERTY_NAME)
		);	
	
		ALTER TABLE  `AC_UI_IMAGE_PROPERTY`  COMMENT '이미지 프로퍼티 테이블';
		
        CREATE TABLE AC_UI_IMAGE_DATA (
			  IMAGE_ID                    INTEGER NOT NULL COMMENT 'ID',
			  IMAGE_DATA               LONGBLOB COMMENT '데이터',
			  CONSTRAINT AC_UI_IMAGE_DATA_PK PRIMARY KEY (IMAGE_ID)
		);		        
		
		ALTER TABLE `AC_UI_IMAGE_DATA`  COMMENT '이미지 데이터 테이블'; 
		
		CREATE TABLE AC_UI_IMAGE_LINK ( 
			LINK_ID						VARCHAR(255)	NOT NULL, 
			IMAGE_ID					INTEGER NOT NULL,			
			PUBLIC_SHARED			TINYINT  DEFAULT 1,
			CREATION_DATE DATETIME DEFAULT NOW() COMMENT '생성일', 
			CONSTRAINT AC_UI_IMAGE_LINK_PK PRIMARY KEY (LINK_ID)
		); 		
		CREATE UNIQUE INDEX AC_UI_IMAGE_LINK_IDX ON AC_UI_IMAGE_LINK (IMAGE_ID);
		
		ALTER TABLE `AC_UI_IMAGE_LINK`  COMMENT '이미지 링크 테이블'; 
		
	-- =================================================  
	--  EXTERNAL_LINK	
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_SHARED_LINK ;
	
		CREATE TABLE AC_UI_SHARED_LINK ( 
			LINK_ID						VARCHAR(255)	NOT NULL COMMENT 'ID', 
			OBJECT_TYPE					INTEGER NOT NULL COMMENT '객체타입',		
			OBJECT_ID                INTEGER NOT NULL COMMENT '객체 ID',
			PUBLIC_SHARED			TINYINT  DEFAULT 1 COMMENT '공개여부',
			CREATION_DATE DATETIME DEFAULT NOW() COMMENT '생성일', 
			CONSTRAINT AC_UI_LINK_ID_PK PRIMARY KEY (LINK_ID)
		); 		
		
		CREATE UNIQUE INDEX AC_UI_SHARED_LINK_IDX1 ON AC_UI_SHARED_LINK (OBJECT_TYPE, OBJECT_ID);
		
		ALTER TABLE `AC_UI_SHARED_LINK`  COMMENT '외부 링크 테이블';
		
-- ------------------------------------------------------
--  DDL for Table AC_UI_ATTACHMENT
-- ------------------------------------------------------
DROP TABLE IF EXISTS AC_UI_ATTACHMENT , AC_UI_ATTACHMENT_DATA , AC_UI_ATTACHMENT_PROPERTY;

  CREATE TABLE AC_UI_ATTACHMENT
   (	
    `ATTACHMENT_ID` INTEGER COMMENT 'ID', 
	`OBJECT_TYPE` INTEGER COMMENT '첨부파일과 연관된 모델 유형', 
	`OBJECT_ID` INTEGER COMMENT '첨부파일과 연관된 모델 ID', 
	`CONTENT_TYPE` VARCHAR(255) COMMENT 'CONTENT TYPE 값', 
	`FILE_NAME` VARCHAR(255) COMMENT '첨부파일 이름', 
	`FILE_SIZE` DECIMAL(38,0) COMMENT '첨부파일 크기', 
	 USER_ID				   	 INTEGER NOT NULL COMMENT '생성자 ID',	 	
	`CREATION_DATE` DATETIME DEFAULT NOW() COMMENT '생성일', 
	`MODIFIED_DATE` DATETIME DEFAULT NOW() COMMENT '수정일',
	CONSTRAINT `AC_UI_ATTACHMENT_PK` PRIMARY KEY (`ATTACHMENT_ID`)
   ) ;

   ALTER TABLE `AC_UI_ATTACHMENT`  COMMENT '첨부파일 테이블';
 
  CREATE TABLE AC_UI_ATTACHMENT_DATA 
  ( 
     ATTACHMENT_ID  INTEGER COMMENT 'ID',
	 ATTACHMENT_DATA LONGBLOB COMMENT '첨부파일 데이터' ,
	 CONSTRAINT `AC_UI_ATTACHMENT_DATA_PK` PRIMARY KEY (`ATTACHMENT_ID`)
  ) ;
 
  ALTER TABLE AC_UI_ATTACHMENT_DATA  COMMENT '첨부파일 데이터 테이블'; 
 

  CREATE TABLE `AC_UI_ATTACHMENT_PROPERTY` 
   (
    `ATTACHMENT_ID` INTEGER COMMENT '첨부파일 ID', 
	`PROPERTY_NAME` VARCHAR(100) COMMENT '프로퍼티 이름', 
	`PROPERTY_VALUE` VARCHAR(1024) COMMENT '프로퍼티 값',
	CONSTRAINT AC_UI_ATTACHMENT_PROPERTY_PK PRIMARY KEY (ATTACHMENT_ID, PROPERTY_NAME)
   ) ;
 
   ALTER TABLE `AC_UI_ATTACHMENT_PROPERTY`  COMMENT '첨부파일 프로퍼티 테이블';

	-- =================================================  
	--  TAG	
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_TAG , AC_UI_OBJECT_TAG , AC_UI_TAG_PROPERTY;
    CREATE TABLE AC_UI_TAG(	 
	    TAG_ID				INTEGER NOT NULL,		
	    TAG_NAME			VARCHAR(100) NOT NULL,
	    CREATION_DATE 		DATETIME DEFAULT NOW() COMMENT '생성일',
	    CONSTRAINT AC_UI_TAG_PK PRIMARY KEY (TAG_ID)
    );
   
    CREATE UNIQUE INDEX AC_UI_TAG_IDX1 ON AC_UI_TAG( TAG_NAME ) ;	
    CREATE INDEX AC_UI_TAG_IDX2 ON AC_UI_TAG (CREATION_DATE) ;
   
    CREATE TABLE AC_UI_OBJECT_TAG(	 
    	TAG_ID				INTEGER NOT NULL,	
		OBJECT_TYPE			INTEGER NOT NULL,
		OBJECT_ID			INTEGER NOT NULL,
   		CREATION_DATE 		DATETIME DEFAULT NOW() COMMENT '생성일'
	);
    
	CREATE INDEX AC_UI_OBJECT_TAG_IDX1 ON AC_UI_OBJECT_TAG (OBJECT_ID, TAG_ID, OBJECT_TYPE) ;
    CREATE INDEX AC_UI_OBJECT_TAG_IDX2 ON AC_UI_OBJECT_TAG (TAG_ID) ;
    CREATE INDEX AC_UI_OBJECT_TAG_IDX3 ON AC_UI_OBJECT_TAG (OBJECT_TYPE, OBJECT_ID, CREATION_DATE, TAG_ID) ;
    CREATE INDEX AC_UI_OBJECT_TAG_IDX4 ON AC_UI_OBJECT_TAG (OBJECT_TYPE, TAG_ID) ;


  CREATE TABLE `AC_UI_TAG_PROPERTY` 
   (
    `TAG_ID` INTEGER COMMENT 'ID', 
	`PROPERTY_NAME` VARCHAR(100) COMMENT '프로퍼티 이름', 
	`PROPERTY_VALUE` VARCHAR(1024) COMMENT '프로퍼티 값',
	CONSTRAINT AC_UI_TAG_PROPERTY_PK PRIMARY KEY (TAG_ID, PROPERTY_NAME)
   ) ;
 
   ALTER TABLE `AC_UI_TAG_PROPERTY`  COMMENT 'TAG 프로퍼티 테이블'; 
   
	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : ANNOUNCE 
	-- CREATE : 2019.11.09
	-- UPDATE : 
	-- =================================================		
	DROP TABLE IF EXISTS AC_UI_ANNOUNCE , AC_UI_ANNOUNCE_PROPERTY ;
	
		CREATE TABLE AC_UI_ANNOUNCE (
			ANNOUNCE_ID				INTEGER NOT NULL,
			OBJECT_TYPE						INTEGER NOT NULL,
			OBJECT_ID						INTEGER NOT NULL,	 		
			USER_ID							INTEGER NOT NULL,	 		
			SUBJECT							VARCHAR(255) NOT NULL,
			BODY							LONGTEXT NOT NULL,
			START_DATE						DATETIME DEFAULT NOW() NOT NULL,
			END_DATE						DATETIME DEFAULT NOW() NOT NULL,
			STATUS							TINYINT  DEFAULT 0,	
			CREATION_DATE					DATETIME DEFAULT NOW() NOT NULL,
			MODIFIED_DATE					DATETIME DEFAULT NOW() NOT NULL,
			CONSTRAINT AC_UI_ANNOUNCE_PK PRIMARY KEY (ANNOUNCE_ID)
		);
		
		CREATE INDEX AC_UI_ANNOUNCE_IDX1 ON AC_UI_ANNOUNCE (OBJECT_TYPE, OBJECT_ID);
		CREATE INDEX AC_UI_ANNOUNCE_IDX2 ON AC_UI_ANNOUNCE (START_DATE);
		CREATE INDEX AC_UI_ANNOUNCE_IDX3 ON AC_UI_ANNOUNCE (END_DATE);
		CREATE INDEX AC_UI_ANNOUNCE_IDX4 ON AC_UI_ANNOUNCE (USER_ID);
		
		CREATE TABLE AC_UI_ANNOUNCE_PROPERTY (
		  ANNOUNCE_ID                     INTEGER NOT NULL COMMENT 'ID',
		  PROPERTY_NAME          VARCHAR(100)   NOT NULL  COMMENT '프로퍼티 이름',
		  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL  COMMENT '프로퍼티 값',
		  CONSTRAINT AC_UI_ANNOUNCE_PROPERTY_PK PRIMARY KEY (ANNOUNCE_ID, PROPERTY_NAME)
		);	
		
		ALTER TABLE `AC_UI_ANNOUNCE_PROPERTY`  COMMENT 'ANNOUNCE 프로퍼티 테이블'; 
		 

		
		

	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : COMMENT 
	-- CREATE : 2019.12.04
	-- UPDATE : 
	-- =================================================	
	DROP TABLE IF EXISTS AC_UI_COMMENT_PROPERTY, AC_UI_COMMENT ;
	
	CREATE TABLE AC_UI_COMMENT(	
		COMMENT_ID				INTEGER NOT NULL COMMENT '댓글ID',		
		OBJECT_TYPE				INTEGER NOT NULL COMMENT '객체 유형',
		OBJECT_ID				INTEGER NOT NULL COMMENT '객체 ID',
		PARENT_COMMENT_ID		INTEGER COMMENT '부모 댓글 ID',
		PARENT_OBJECT_TYPE		INTEGER COMMENT '부모 객체 유형',
		PARENT_OBJECT_ID		INTEGER COMMENT '부모 객체 ID',
		USER_ID					INTEGER  COMMENT '사용자 ID',
		NAME					VARCHAR(100)  COMMENT '사용자 이름',
		EMAIL					VARCHAR(255)  COMMENT '사용자 메일',
		URL						VARCHAR(255)  COMMENT 'URL',
		IP						VARCHAR(15)   COMMENT 'IP주소',
		BODY            		VARCHAR(4000)  COMMENT '댓글',
		STATUS					VARCHAR(25) NOT NULL  COMMENT '상태', 
		CREATION_DATE			DATETIME DEFAULT NOW() NOT NULL COMMENT '생성일',
		MODIFIED_DATE			DATETIME DEFAULT NOW() NOT NULL COMMENT '수정일',		
		CONSTRAINT AC_UI_COMMENT_PK PRIMARY KEY (COMMENT_ID)
   );	
   ALTER TABLE `AC_UI_COMMENT`  COMMENT '댓글 테이블'; 
   
	CREATE INDEX AC_UI_COMMENT_IDX1 ON AC_UI_COMMENT (CREATION_DATE) ;
	CREATE INDEX AC_UI_COMMENT_IDX2 ON AC_UI_COMMENT (MODIFIED_DATE) ;
	CREATE INDEX AC_UI_COMMENT_IDX3 ON AC_UI_COMMENT (OBJECT_TYPE, OBJECT_ID) ;
	CREATE INDEX AC_UI_COMMENT_IDX4 ON AC_UI_COMMENT (PARENT_OBJECT_TYPE, PARENT_OBJECT_ID) ;
	CREATE INDEX AC_UI_COMMENT_IDX5 ON AC_UI_COMMENT (USER_ID) ;
	
	CREATE TABLE AC_UI_COMMENT_PROPERTY (
	  COMMENT_ID             INTEGER NOT NULL COMMENT '프로퍼티 ID',
	  PROPERTY_NAME          VARCHAR(100)   NOT NULL COMMENT '프로퍼티 이름',
	  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL COMMENT '프로퍼티 값',
	  CONSTRAINT AC_UI_COMMENT_PROPERTY_PK PRIMARY KEY (COMMENT_ID, PROPERTY_NAME)
	);	
	ALTER TABLE `AC_UI_COMMENT_PROPERTY`  COMMENT '댓글 속성 테이블'; 
	
	-- =================================================  
	-- PACKAGE: UI  
	-- COMPONENT : AC_UI_AUDIT_TRAIL 
	-- CREATE : 2019.12.04
	-- UPDATE : 
	-- =================================================		
	DROP TABLE IF EXISTS AC_UI_AUDIT_TRAIL;
	CREATE TABLE AC_UI_AUDIT_TRAIL
    (
    	AUD_USER      VARCHAR(100) NOT NULL,
    	AUD_CLIENT_IP VARCHAR(15)   NOT NULL,
    	AUD_CLIENT_USERAGENT VARCHAR(255) NOT NULL,
    	AUD_SERVER_IP VARCHAR(15)   NOT NULL,
    	AUD_RESOURCE  VARCHAR(100) NOT NULL,
    	AUD_ACTION    VARCHAR(100) NOT NULL,
    	APPLIC_CD     VARCHAR(255)   NOT NULL, 
    	AUD_DATE      DATETIME DEFAULT NOW() NOT NULL 
    );
     
-- ===================================================== --
-- ACL Schema SQL for MySQL 5.5+ / MariaDB equivalent    --
-- ----------------------------------------------------- --
-- drop table acl_entry;
-- drop table acl_object_identity;
-- drop table acl_class;
-- drop table acl_sid;

DROP TABLE IF EXISTS acl_entry , acl_object_identity , acl_class, acl_sid;

CREATE TABLE acl_sid (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    principal BOOLEAN NOT NULL,
    sid VARCHAR(100) NOT NULL,
    UNIQUE KEY unique_acl_sid (sid, principal)
) ENGINE=InnoDB;

CREATE TABLE acl_class (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    class VARCHAR(100) NOT NULL,
    UNIQUE KEY uk_acl_class (class)
) ENGINE=InnoDB;

CREATE TABLE acl_object_identity (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    object_id_class BIGINT UNSIGNED NOT NULL,
    object_id_identity BIGINT NOT NULL,
    parent_object BIGINT UNSIGNED,
    owner_sid BIGINT UNSIGNED,
    entries_inheriting BOOLEAN NOT NULL,
    UNIQUE KEY uk_acl_object_identity (object_id_class, object_id_identity),
    CONSTRAINT fk_acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
    CONSTRAINT fk_acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
    CONSTRAINT fk_acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
) ENGINE=InnoDB;

CREATE TABLE acl_entry (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    acl_object_identity BIGINT UNSIGNED NOT NULL,
    ace_order INTEGER NOT NULL,
    sid BIGINT UNSIGNED NOT NULL,
    mask INTEGER UNSIGNED NOT NULL,
    granting BOOLEAN NOT NULL,
    audit_success BOOLEAN NOT NULL,
    audit_failure BOOLEAN NOT NULL,
    UNIQUE KEY unique_acl_entry (acl_object_identity, ace_order),
    CONSTRAINT fk_acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
    CONSTRAINT fk_acl_entry_acl FOREIGN KEY (sid) REFERENCES acl_sid (id)
) ENGINE=InnoDB;
			   	