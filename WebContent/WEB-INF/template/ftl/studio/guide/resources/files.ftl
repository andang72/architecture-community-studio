<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "Working with Attachment" />	
  <#assign PARENT_PAGE_NAME = "가이드" />	  
  <#assign KENDO_VERSION = "2019.3.917" /> 
  <title>STUDIO :: ${PAGE_NAME} </title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">

  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/docs.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>">   
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.common.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.min.css"/>"> 
  
 <!-- Application JavaScript
  		================================================== -->
  <script>		
	require.config({
		shim : {
			"jquery.cookie" 				: { "deps" :['jquery'] },
	        "bootstrap" 					: { "deps" :['jquery'] },
	        "jquery.fancybox" 				: { "deps" :['jquery'] },
	        "jquery.scrollbar" 				: { "deps" :['jquery'] },
	        "hs.core" 						: { "deps" :['jquery', 'jquery.cookie', 'bootstrap'] },
	        "hs.scrollbar" 					: { "deps" :['jquery', 'hs.core', 'jquery.scrollbar'] },
	        "hs.side-nav" 					: { "deps" :['jquery', 'hs.core'] },
	        "hs.focus-state" 				: { "deps" :['jquery', 'hs.core'] },
	        "hs.popup" 						: { "deps" :['jquery', 'hs.core', 'jquery.fancybox'] },
	        "hs.hamburgers" 				: { "deps" :['jquery', 'hs.core'] },
	        "hs.dropdown" 					: { "deps" :['jquery', 'hs.core'] },	 
	        "hs.scrollbar" 					: { "deps" :['jquery', 'hs.core'] },
	        <!-- Kendo UI Professional -->
	        "kendo.web.min" 				: { "deps" :['jquery', 'kendo.core.min', 'kendo.culture.min'] },
	        <!-- community ui -->
	        "community.ui.data" 			: { "deps" :['jquery', 'kendo.web.min'] },
	        "community.ui.core" 			: { "deps" :['jquery', 'kendo.web.min', 'community.ui.data'] },
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min', 'community.ui.data' , 'community.ui.core' ] }
		},
		paths : {
			"jquery"    					: "<@spring.url "/js/jquery/jquery-3.4.1.min"/>",
			"jquery.cookie"    				: "<@spring.url "/js/jquery.cookie/1.4.1/jquery.cookie"/>",
			"jquery.fancybox" 				: "<@spring.url "/js/jquery.fancybox/jquery.fancybox.min"/>",
			"jquery.scrollbar" 				: "<@spring.url "/assets/unify.admin/2.6.2/vendor/malihu-scrollbar/jquery.mCustomScrollbar.concat.min"/>",
			"bootstrap" 					: "<@spring.url "/js/bootstrap/4.3.1/bootstrap.bundle.min"/>",
			"hs.core" 						: "<@spring.url "/assets/unify/2.6.2/js/hs.core"/>", 
			"hs.hamburgers" 				: "<@spring.url "/assets/unify/2.6.2/js/helpers/hs.hamburgers"/>",
			"hs.dropdown" 					: "<@spring.url "/assets/unify/2.6.2/js/components/hs.dropdown"/>",
			"hs.scrollbar" 					: "<@spring.url "/assets/unify/2.6.2/js/components/hs.scrollbar"/>",
			"hs.focus-state" 				: "<@spring.url "/assets/unify/2.6.2/js/helpers/hs.focus-state"/>",
			"hs.side-nav" 					: "<@spring.url "/assets/unify.admin/2.6.2/js/components/hs.side-nav"/>",
			"hs.popup" 						: "<@spring.url "/assets/unify.admin/2.6.2/js/components/hs.popup"/>",
			"studio.custom" 				: "<@spring.url "/js/community.ui.studio/custom"/>",
			<!-- Kendo UI Professional -->
			"kendo.core.min"				: "<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.core.min"/>",
			"kendo.web.min"	 				: "<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.web.min"/>", 
			"kendo.culture.min"				: "<@spring.url "/js/kendo/${KENDO_VERSION}/cultures/kendo.culture.ko-KR.min"/>", 
			"jszip"							: "<@spring.url "/js/kendo/${KENDO_VERSION}/jszip.min"/>",
			<!-- community ui -->
			"community.ui.data"				: "<@spring.url "/js/community.ui/community.ui.data"/>",
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  community.ui.studio.setup();	
  	  console.log("END SETUP APPLICATION.");	
  	});	
  </script>
</head>    		
<body data-spy="scroll" data-target=".guide-navbar" data-offset="50" >
  <main class="container-fluid px-0 g-pt-65">
    <div class="row no-gutters g-pos-rel g-overflow-x-hidden"> 
	  <#include "../../includes/sidebar.ftl"> 
      <div class="col g-ml-45 g-ml-0--lg g-pb-65--md"> 
        <!-- Breadcrumb-v1 -->
        <div class="g-hidden-sm-down g-bg-gray-light-v8 g-pa-20">
          <ul class="u-list-inline g-color-gray-dark-v6"> 
            <li class="list-inline-item g-mr-10">
              <a class="u-link-v5 g-color-gray-dark-v6 g-color-secondary--hover g-valign-middle" href="#!">${PARENT_PAGE_NAME} </a>
              <i class="hs-admin-angle-right g-font-size-12 g-color-gray-light-v6 g-valign-middle g-ml-10"></i>
            </li> 
            <li class="list-inline-item">
                <span class="g-valign-middle">${PAGE_NAME}</span>
            </li>
          </ul>
        </div>
        <!-- End Breadcrumb-v1 -->  
        <div class="g-pa-20">
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30">${PAGE_NAME}</h1> 
          <!-- Content -->
		  <div id="features" class="container-fluid">
			<div class="row flex-xl-nowrap"> 
	          <nav class="d-none d-xl-block col-xl-2 bd-toc guide-navbar" aria-label="Secondary navigation">
				<ul class="section-nav">
					<li class="toc-entry toc-h2"><a href="#guide-1">What is Attachment</a> 
					</li>
					<li class="toc-entry toc-h2"><a href="#guide-2">File Upload</a>
					<ul>
						<li class="toc-entry toc-h2"><a href="#guide-2-1">In KendoUI</a>
					</ul>		
					</li>
					<li class="toc-entry toc-h2"><a href="#guide-3">파일정보조회</a></li>
					<li class="toc-entry toc-h2"><a href="#guide-4">파일다운로드</a></li>
					<li class="toc-entry toc-h2"><a href="#guide-5">파일삭제</a></li>
					</ul>
	         	</nav> 
				<main class="col-12 col-md-9 col-xl-10 py-md-3 pl-md-5 bd-content" role="main"> 
					<div class="u-heading-v2-3--bottom g-mb-30">
                      <h2 class="u-heading-v2__title g-mb-10" id="guide-1">What is Attachment</h2>
                      <h4 class="g-font-weight-200 g-mb-10"></h4>
                    </div>
					<p>Attachment는 게시판의 게시물과 같이 파일이 필요한 객체들에 대하여 파일 업로드 및 수정 삭제 등의 기능을 제공한다. </p> 
					<p><span class="g-color-primary">Integer</span> 타입의 객체 유형 값과 유일한 객체 구분을 위한 <span class="g-color-primary">Long</span> 타입의 객체 아이디 값만 있다면 간편하게 사용할 수 있다.
					</p> 
					<hr>			
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-2" >파일 업로드</h2>
					</div>
                    <p>POST 방식의 파일 업로드를 지원한다.</p>
                    
                    <pre><code class="language-java g-font-size-12">POST /data/files/0/upload.json</code></pre>
					<div class="table-responsive">
					  <table class="table table-striped">
					    <thead>
					      <tr>
					        <th>#</th>
					        <th>Parameter</th>
					        <th class="hidden-sm">Type</th>
					        <th>Description</th>
					      </tr>
					    </thead>
					    <tbody>
					      <tr>
					        <td>1</td>
					        <td>shared</td>
					        <td class="hidden-sm">Boolean</td>
					        <td><span class="g-color-primary">true</span> 값을 전달하면 공유를 위한 링크가 생성가 자동 생성된다.</td>
					      </tr>
					      <tr>
					        <td>2</td>
					        <td>objectType</td>
					        <td class="hidden-sm">Number</td>
					        <td>파일을 소유하는 객체 유형 코드.</td>
					      </tr>     
					      <tr>
					        <td>3</td>
					        <td>objectId</td>
					        <td class="hidden-sm">Number</td>
					        <td>파일을 소유하는 객체 ID</td>
					      </tr>       
					    </tbody>
					  </table>
					</div>
					<div class="alert fade show g-bg-blue-opacity-0_1 g-color-blue rounded-0 g-mt-5" role="alert"> 
                        <div class="media">
                          <div class="d-flex g-mr-10">
                            <span class="u-icon-v3 u-icon-size--sm g-bg-blue g-color-white g-rounded-50x">
                              <i class="icon-info"></i>
                            </span>
                          </div>
                          <div class="media-body">
                          	주의할 점은 shared 파라메터에 true 값을 설정하지 않는 경우는 공유를 위한 링크가 생성되지 않는다. <br/>
                          	파일을 공유하려면 반듯이 링크를 생성해야한다.
                          </div>
                        </div>
                      </div>
                      <p>파일이 성공적으로 업로드 되면 JSON 형식의 배열로 업로드된 파일정보를 응답한다.</p>
					  <pre><code class="language-json g-font-size-11">
[
    {
        "properties": {},
        "objectType": -1,
        "objectId": -1,
        "user": {
            "password": "",
            "username": "SYSTEM",
            "authorities": [
                {
                    "authority": "ROLE_SYSTEM"
                }
            ],
            "accountNonExpired": true,
            "accountNonLocked": true,
            "credentialsNonExpired": true,
            "enabled": true,
            "name": "SYSTEM",
            "properties": {},
            "anonymous": false,
            "userId": 0,
            "creationDate": null,
            "modifiedDate": null,
            "status": "NONE",
            "email": null,
            "passwordHash": null,
            "nameVisible": false,
            "emailVisible": false,
            "external": false
        },
        "attachmentId": 4,
        "name": "전은재.pdf",
        "contentType": "application/pdf",
        "size": 274456,
        "downloadCount": 0,
        "extrnalLink": null,
        "creationDate": "2019-11-07T08:35:57Z",
        "modifiedDate": "2019-11-07T08:35:57Z",
        "sharedLink": {
            "linkId": "jccJa68svklU5Kh3KBSmTXbQ3MlAPBaYILMBKIvx9qu2851u5tS7oVYc8can9ikk",
            "publicShared": true,
            "objectType": 10,
            "objectId": 4
        }
    }
]
					  </code></pre>				
  		
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-2-1" >파일 업로드 KENDOUI</h2>
					</div>
					<p> KENDO UI Upload 를 이용한 파일 업로드 코드이다.</p>
						  		  	
<pre class="hljs" style="display: block; overflow-x: auto; padding: 0.5em; background: rgb(51, 51, 51); color: rgb(255, 255, 255);"><span class="xml"><span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">%</span></span></span><span class="ruby">@ page language=<span class="hljs-string" style="color: rgb(162, 252, 162);">"java"</span> contentType=<span class="hljs-string" style="color: rgb(162, 252, 162);">"text/html; charset=UTF-8"</span> pageEncoding=<span class="hljs-string" style="color: rgb(162, 252, 162);">"UTF-8"</span></span><span class="xml"><span class="hljs-tag" style="color: rgb(98, 200, 243);">%&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">%</span></span></span><span class="ruby">@ taglib prefix=<span class="hljs-string" style="color: rgb(162, 252, 162);">"spring"</span> uri=<span class="hljs-string" style="color: rgb(162, 252, 162);">"http://www.springframework.org/tags"</span></span><span class="xml"><span class="hljs-tag" style="color: rgb(98, 200, 243);">%&gt;</span>
<span class="hljs-meta" style="color: rgb(252, 155, 155);">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">html</span>&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">head</span>&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">meta</span> <span class="hljs-attr">http-equiv</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"Content-Type"</span> <span class="hljs-attr">content</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"text/html; charset=UTF-8"</span>&gt;</span> 
        <span class="hljs-comment" style="color: rgb(136, 136, 136);">&lt;!-- CSS --&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">link</span> <span class="hljs-attr">rel</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"stylesheet"</span> <span class="hljs-attr">href</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"/css/kendo/2019.3.917/kendo.common.min.css"</span> /&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">link</span> <span class="hljs-attr">rel</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"stylesheet"</span> <span class="hljs-attr">href</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"/css/kendo/2019.3.917/kendo.nova.min.css"</span> /&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">link</span> <span class="hljs-attr">rel</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"stylesheet"</span> <span class="hljs-attr">href</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"/css/kendo/2019.3.917/kendo.default.mobile.min.css"</span> /&gt;</span> 
	<span class="hljs-comment" style="color: rgb(136, 136, 136);">&lt;!-- JS --&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">script</span> <span class="hljs-attr">src</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"&lt;spring:url value='/js/jquery/jquery-3.1.1.min.js'/&gt;"</span>&gt;</span><span class="undefined"></span><span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">script</span>&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">script</span> <span class="hljs-attr">type</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"text/javascript"</span> <span class="hljs-attr">src</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"&lt;spring:url value='/js/kendo/2019.3.917/kendo.web.min.js'/&gt;"</span>&gt;</span><span class="undefined"></span><span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">script</span>&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">title</span>&gt;</span>Upload/title&gt;
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">head</span>&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">body</span>&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">script</span>&gt;</span><span class="javascript">
		$(<span class="hljs-built_in" style="color: rgb(255, 255, 170);">document</span>).ready( <span class="hljs-function"><span class="hljs-keyword" style="color: rgb(252, 194, 140);">function</span>(<span class="hljs-params"></span>) </span>{
			$(<span class="hljs-string" style="color: rgb(162, 252, 162);">"#files"</span>).kendoUpload({
			    <span class="hljs-keyword" style="color: rgb(252, 194, 140);">async</span> : { saveUrl : <span class="hljs-string" style="color: rgb(162, 252, 162);">"&lt;spring:url value='/data/files/0/upload.json?shared=true'/&gt;"</span> },
			    validation : { allowedExtensions : [ <span class="hljs-string" style="color: rgb(162, 252, 162);">".pdf"</span> ]}
			});
		});
	</span><span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">script</span>&gt;</span>
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">div</span> <span class="hljs-attr">class</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"demo-section k-content"</span>&gt;</span>
		<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">h4</span>&gt;</span>Upload PDF<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">h4</span>&gt;</span>
		<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">input</span> <span class="hljs-attr">name</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"files"</span> <span class="hljs-attr">id</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"files"</span> <span class="hljs-attr">type</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"file"</span> /&gt;</span>
		<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">div</span> <span class="hljs-attr">class</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"demo-hint"</span>&gt;</span>
			You can only upload <span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;<span class="hljs-name" style="font-weight: 700;">strong</span>&gt;</span>PDF<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">strong</span>&gt;</span> files.
		<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">div</span>&gt;</span> 
	<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">div</span>&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">body</span>&gt;</span>
<span class="hljs-tag" style="color: rgb(98, 200, 243);">&lt;/<span class="hljs-name" style="font-weight: 700;">html</span>&gt;</span></span></pre>			

					<hr/>
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-3" >파일 정보 조회</h2>
					</div>
					<p>특정 파일에 대한 정보는 attatchmentId 를 가지고 조회할 수 있다.</p>
					 <pre><code class="language-java g-font-size-12">GET/POST /data/files/{attachmentId}/get.json</code></pre>
					<p>이 API 는 JSON 형식으로 아래와 파일 정보를 응답한다. 공유 파일이 아닌 경우는 linkId 값은 null 이 된다.</p>
					<pre><code class="language-json g-font-size-12">
					
{
        "properties": {},
        "objectType": -1,
        "objectId": -1,
        "user": {
            "password": "",
            "username": "SYSTEM",
            "authorities": [
                {
                    "authority": "ROLE_SYSTEM"
                }
            ],
            "accountNonExpired": true,
            "accountNonLocked": true,
            "credentialsNonExpired": true,
            "enabled": true,
            "name": "SYSTEM",
            "properties": {},
            "anonymous": false,
            "userId": 0,
            "creationDate": null,
            "modifiedDate": null,
            "status": "NONE",
            "email": null,
            "passwordHash": null,
            "nameVisible": false,
            "emailVisible": false,
            "external": false
        },
        "attachmentId": 4,
        "name": "전은재.pdf",
        "contentType": "application/pdf",
        "size": 274456,
        "downloadCount": 0,
        "extrnalLink": null,
        "creationDate": "2019-11-07T08:35:57Z",
        "modifiedDate": "2019-11-07T08:35:57Z",
        "sharedLink": {
            "linkId": "jccJa68svklU5Kh3KBSmTXbQ3MlAPBaYILMBKIvx9qu2851u5tS7oVYc8can9ikk",
            "publicShared": true,
            "objectType": 10,
            "objectId": 4
        }
    }					
					
					</code></pre>
					
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-4" >파일 다운로드</h2>
					</div>
					<p>파일 다운로드의 경우 링크 생성이 필요합니다. 생성된 링크 값을 사용하여 GET 방식으로 파일을 다운로드 할 수 있다.</p>
					<pre><code class="language-java g-font-size-12">GET /download/files/{link_id}</code></pre>
					<div class="table-responsive">
					  <table class="table table-striped">
					    <thead>
					      <tr>
					        <th>#</th>
					        <th>Path Parameter</th>
					        <th class="hidden-sm">Type</th>
					        <th>Description</th>
					      </tr>
					    </thead>
					    <tbody>
					      <tr>
					        <td>1</td>
					        <td>link_id<span class="g-color-primary">*</span></td>
					        <td class="hidden-sm">String</td>
					        <td>The ID of the file link.</td>
					      </tr> 
					    </tbody>
					  </table>
					</div>  
					<p>PDF, PPT, IMAGE, MP3, MP4 파일은 Thumbnail 을 지원한다. height 와 weight 값이 지정되지 않는 경우 디폴트 150x150 이 적용된다. </p>
					<pre><code class="language-java g-font-size-12">GET /download/files/{link_id}?thumbnail=true&height={height_size}&width={width_size}</code></pre>
					<div class="table-responsive">
					  <table class="table table-striped">
					    <thead>
					      <tr>
					        <th>#</th>
					        <th>Parameter</th>
					        <th class="hidden-sm">Type</th>
					        <th>Description</th>
					      </tr>
					    </thead>
					    <tbody>
					      <tr>
					        <td>1</td>
					        <td>thumbnail</td>
					        <td class="hidden-sm">Boolean</td>
					        <td><span class="g-color-primary">true</span> 값이면 Thumbnail 이미지를 리턴한다.</td>
					      </tr> 
					      <tr>
					        <td>2</td>
					        <td>width</td>
					        <td class="hidden-sm">Number</td>
					        <td>Thumbnail 이미지 width. (default:150)</td>
					      </tr> 
					      <tr>
					        <td>3</td>
					        <td>height</td>
					        <td class="hidden-sm">Number</td>
					        <td>Thumbnail 이미지 height. (default:150)</td>
					      </tr> 					      					      
					    </tbody>
					  </table>
					</div> 	
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-5" >파일 삭제</h2>
					</div>
					<p>삭제는 생성자 또는 권한(ROLE_SYSTEM, ROLE_DEVELOPER, ROLE_OPERATOR, ROLE_ADMINISTRATOR )이 있는 경우에 만 사용할 수 있다.</p>		
					<pre><code class="language-java g-font-size-12">GET/POST /data/files/{attachmentId}/delete.json</code></pre>		
				</main>
			</div>
		  </div>         
          <!-- End Content --> 
          
        </div>
		<#include "../../includes/footer.ftl"> 
      </div>
    </div>
  </main>
</body>
</html>