<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "API" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	 
  <#assign KENDO_VERSION = "2019.3.917" /> 
  <title>STUDIO :: ${PAGE_NAME} </title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">

  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.common.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.min.css"/>"> 
  
  <!-- Application JavaScript
  		================================================== -->
  <script>	
    var __apiId = <#if RequestParameters.apiId?? >${RequestParameters.apiId}<#else>0</#if>;	
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
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>",
			"ace" 							: "<@spring.url "/js/ace/ace"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom",  "ace" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  
  	  community.ui.studio.setup();	
  	   
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		api : new community.data.model.Api(),
		visible : false,
		editable : true,
		isNew : true,
		preview : function () {
			var $this = this;
			community.ui.send('<@spring.url "/data/api/"/>' + $this.api.name, { preview : true } , 'GET', '_blank' );
		},
		wizard : function () {
			var $this = this;
			community.ui.send('resource-scripts-wizard', {name: $this.api.name , type: 'data'} , 'GET', '_blank' );
		},
		objectTypes : [
			{ text: "정의되지 않음", value: "-1" },
			<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
			<#if item.DISPLAY_NAME??>
			{ text:'${item.DISPLAY_NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
			</#if>
			</#list>
		],
		contentTypes : [
			{ text: "XML", value: "application/xml;charset=UTF-8" },
			{ text: "JSON", value: "application/json;charset=UTF-8" }
		], 	
		edit: function(){
			var $this = this;
			$this.set('editable', true);
		},
		back : function(){
			var $this = this;
			community.ui.send("<@spring.url "/secure/studio/resource-apis" />");
			return false;
		},
		cancle: function(){
			var $this = this;
			if( $this.get('isNew') ){
				// or back..
				$('#pageForm')[0].reset();
			}else{
			    // or back..
				$this.set('editable', false);
				$this.load(__apiId); 
			}
		},
		load: function(objectId){
			var $this = this;
			if( objectId > 0 ){
				community.ui.progress($('#features'), true);	
				community.ui.ajax('<@spring.url "/data/secure/mgmt/apis/"/>' + objectId + '/get.json?fields=bodyContent', {
					contentType : "application/json",
					success: function(data){	
						$this.setSource( new community.data.model.Api(data) );
					}	
				}).always( function () {
					community.ui.progress($('#features'), false);
				});	
			}else{
				$this.setSource(new community.data.model.Api()); 
			}	
		},	
		select : function(e){
			var $this = this;
			var type = $(e.currentTarget).data('type');
			getResourceSelectorWindow($('#resource-select-window'), type, 'data', $this); 
		},
		selected(type, vlaue){
			var $this = this;
			if ( type === 'script'){
				observable.set( 'api.scriptSource' , vlaue  );
			}
		},
		property : function(e){
			var $this = this;
			createPropertyWindowIfNotExist(community.data.Models.Api, $this.api.apiId );
		},
		permissions : function(e){
			var $this = this;
			createPermissionsWindowIfNotExist(community.data.Models.Api, $this.api.apiId); 
		},
		setSource : function( data ){
			var $this = this ;	  
			if( data.get('apiId') > 0 ){
				data.copy( $this.api );
				$this.set('editable', false );
				$this.set('isNew', false );
			}else{
				$this.set('editable', true );	
				$this.set('isNew', true );
			}
			if( !$this.get('visible') ) 
				$this.set('visible' , true );
		},
		saveOrUpdate : function(e){ 
			var $this = this;
			var validator = community.ui.validator($("#pageForm"), {});
			 if (validator.validate()){
				var saveOrUpdateUrl = '<@spring.url "/data/secure/mgmt/apis/save-or-update.json" />';  
				community.ui.progress($('#features'), true);	
				community.ui.ajax( saveOrUpdateUrl, {
					data: community.ui.stringify($this.api),
					contentType : "application/json",
					success : function(response){
						$this.setSource( new community.data.model.Api( response.data.item ) );
						if($this.api.scriptSource === null ){
							alert("wizard...");
						}
					}
				}).always( function () {
					community.ui.progress($('#features'), false); 
				});				
			}			
		}
	  });	
	  	
	  observable.bind("change", function(e) {
	  	if ( e.field === 'editable' ){
	  		createCodeEditor($("#htmleditor")).setReadOnly( !observable.get('editable') );
	  	}
	  });
	  community.data.bind( $('#features') , observable );  
	  observable.load(__apiId);  
  	  console.log("END SETUP APPLICATION.");	
  	});	
  	
	function createCodeEditor( renderTo, useWrapMode, mode ){
		mode = mode || "ace/mode/ftl", useWrapMode = useWrapMode || false; 
		if( renderTo.contents().length == 0 ){ 
			var editor = ace.edit(renderTo.attr("id"));		
			editor.getSession().setMode(mode);
			editor.setTheme("ace/theme/monokai");
			editor.getSession().setUseWrapMode( useWrapMode );
		}
		return ace.edit(renderTo.attr("id"));
	}
	
	function getCodeEditor(renderTo){
		return ace.edit(renderTo.attr("id"))
	} 

  </script>
  <#include "includes/styles.ftl"> 
</head>    		
<body>
  <main class="container-fluid px-0 g-pt-65">
    <div class="row no-gutters g-pos-rel g-overflow-x-hidden"> 
	  <#include "includes/sidebar.ftl"> 
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

        <div id="features" class="g-pa-20">
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30" data-bind="text:page.name"></h1> 
          <!-- Content -->
          <div class="container-fluid"> 
			<div class="row"> 
				<div class="col-lg-10 g-mb-10">   
				<div class="card g-brd-gray-light-v7 g-brd-0 g-rounded-3 g-mb-30 g-min-height-500" data-bind="visible:visible" style="display:none;" >
                  <header class="card-header g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
                    <div class="media"> 
                      <a class="hs-admin-angle-left u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover" href="#" data-bind="click:back"></a>
                      <div class="media-body d-flex justify-content-end"> 
                      	<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" ></a>
                      	<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" ></a> 
                      	<a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#"></a>
                      </div>
                    </div>
                  </header>
                  <div class="card-block g-pa-15 g-pa-30--sm"> 
                  <form id="pageForm">
							<div class="form-group g-mb-30">
	                    			<label class="g-mb-10 g-font-weight-600" for="input-api-name">이름 <span class="text-danger">*</span></label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      		<input id="input-api-name" name="input-api-name" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="파일명을 입력하세요" 
		                      			data-bind="value: api.name, enabled:editable" required validationMessage="이름을 입력하여 주세요." autofocus>
		                    			<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
		                    			${PAGE_NAME}를 호출할때 사용되는 이름입니다. ex) /data/apis/<span data-bind="text: api.name"></span> 
		                    			</small>
		                    		</div>
		                    		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-api-name" role="alert" style="display:none;"></span>
	                  		</div>
							<div class="form-group g-mb-30">
	                    			<label class="g-mb-10 g-font-weight-600" for="input-api-pattern">패턴</label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      		<input id="input-api-pattern" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="패턴을 입력하세요" data-bind="value: api.pattern, enabled:editable">
		                    			<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
		                    			패턴을 기반으로 ${PAGE_NAME}를 호출합니다. ex) /data/apis/<span data-bind="text: api.pattern"></span> 
		                    			</small>
		                    		</div>
	                  		</div>	                  		
 							<div class="form-group">
	                    			<label class="g-mb-10 g-font-weight-600" for="input-api-title">페이지 타이틀 <span class="text-danger">*</span></label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      			<input id="input-api-title" name="input-api-title" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="파일명을 입력하세요" placeholder="페이지 타이틀을 입력하세요." 
		                      			data-bind="value: api.title, enabled:editable" required validationMessage="타이틀을 입력하여 주세요.">
		                    			<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5"> 타이틀로 사용되는 이름입니다. 이름을 통하여 손쉽게 어떤 ${PAGE_NAME} 인지 알 수 있습니다.</small>
		                    		</div>
		                    		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-api-title" role="alert" style="display:none;"></span>
	                  		</div>  
							<div class="form-group">
			                   	<label class="g-mb-10 g-font-weight-600" for="input-api-description">설명</label>			
			                    	<textarea id="input-api-description" class="form-control form-control-md g-resize-none g-rounded-4" rows="3" placeholder="간략하게 ${PAGE_NAME}에 대한 설명을 입력하세요." data-bind="value:api.description, enabled:editable"></textarea>
							</div>	
						               
							<!-- Optional Setting -->
							
							<div class="g-bg-gray-light-v5 no-gitters g-mb-30 g-px-30 g-round-5 g-pt-30 g-brd-gray-light-v7 g-brd-style-solid g-brd-1">
							<div class="row g-mb-15" >
			            		<div class="col-md-6">
	                					<label class="g-mb-10 g-font-weight-600">OBEJCT TYPE</label>		
		                				<div class="form-group g-pos-rel g-rounded-4 mb-0">
					                    <input data-role="dropdownlist"
										data-option-label="서비스를 소유하는 객체 유형을 선택하세요."
										data-auto-bind="true"
										data-value-primitive="true"
										data-text-field="text"
										data-value-field="value"
										data-bind="value: api.objectType, enabled:editable, source: objectTypes"
										style="width: 100%;" /> 
									</div>
								</div>
								<div class="col-md-6">
	                				<label class="g-mb-10 g-font-weight-600">OBEJCT ID</label>		
		                			<div class="form-group g-pos-rel g-rounded-4 mb-0">
					                    <input placeholder="OBJECT ID" 
					                    class="form-control form-control-md k-textbox" type="text" data-bind="value:api.objectId, enabled:editable" style="width: 100%"/>
				                    </div>
		                		</div>						
							</div>
							<div class="row g-mb-15" >
			            		<div class="col-md-6">
									<div class="form-group">
										<label class="g-font-weight-600 g-mb-10" >버전<span class="text-danger">*</span></label>
										<div class="g-pos-rel">
											<input class="form-control form-control-md k-textbox" type="text"
											placeholder="버전 정보를 입력하세요" data-bind="value: api.version, enabled:editable"  style="width: 100%" >
										</div>
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group">
										<label class="g-mb-10 g-font-weight-600">콘텐츠 유형</label>
										<div class="g-pos-rel">
											<input data-role="dropdownlist"
												data-option-label="콘텐츠 유형을 선택하세요."
												data-auto-bind="true"
												data-value-primitive="true"
												data-text-field="text"
												data-value-field="value"
												data-bind="value: api.contentType, enabled:editable, source: contentTypes"
												style="width: 100%;" /> 
										</div>
									</div>	
		                		</div>						
							</div>
							<div class="row g-pt-15" >
			            		<div class="col-md-6 g-mb-30">
									<label class="d-flex align-items-center justify-content-between g-mb-0">
										<span class="g-pr-20 g-font-weight-500">인증필요</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="api-secured" id="api-secured" value="true" data-bind="checked: api.secured,  enabled:editable" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
									ON 상태인 경우 접근권한이 허용된 사용자만 이용할 수 있습니다.
									</small>
								</div>
			            		<div class="col-md-6 g-mb-30">
									<label class="d-flex align-items-center justify-content-between g-mb-0">
										<span class="g-pr-20 g-font-weight-500">사용여부</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="api-enabled" id="api-enabled" value="true" data-bind="checked: api.enabled,  enabled:editable" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
									ON 상태인 경우 API 서비스가 동작합니다.
									</small>
								</div>					
							</div>
							</div>		 			
							<!-- /.Optional Setting -->	  		               		          	                  		               		          	                  		          
 							 
	 						<div class="form-group">
	                    		<label class="g-mb-10 g-font-weight-600" for="input-api-template">서버 스크립트<span class="text-danger">*</span></label>
		                    	<div class="g-pos-rel">
		                    		<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-sec	ondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#!" data-bind="click:select" data-type="script">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-1 g-opacity-1--success">
				                  		<i class="hs-admin-search g-absolute-centered g-font-size-default"></i>
				                	</span>
									</a> 
		                      		<input id="input-api-script" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="서버 스크립트 파일 경로를 입력하세요" data-bind="value: api.scriptSource, enabled:editable">
		                    	</div>
		                    	<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
		                    	서버 스크립트를 선택하거나 새로운 스크립트를 만들어 사용해주세요.
		                    	</small> 
	                  		</div>  
							<div class="form-group">
								<a href="javascript:void(this);" class="btn btn-md u-btn-3d g-rounded-50 u-btn-blue g-mr-10 g-mb-15" data-bind="click:wizard">새로운 서버 스크립트 만들기</a> 
							</div>
					
					<div class="row" data-bind="visible:editable">
                      <div class="col-md-9 ml-auto text-right g-mt-15"> 
                        <button id="showToast" class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:saveOrUpdate" type="button">저장</button>
                        <button id="clearToasts" class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md g-mb-10" type="button" data-bind="click:cancle" >취소</button>
                      </div>
                    </div>
                  </form>
                  </div>
                </div>
                
                </div> 
				<!-- side menu -->
				<div class="g-brd-left--lg g-brd-gray-light-v4 col-lg-2 g-mb-10 g-mb-0--md"> 
					<section data-bind="invisible:isNew" style="display:none;"> 
						<a href="javascript:void(this);" class="btn btn-md u-btn-outline-red g-mr-10 g-mb-15 g-rounded-50" data-bind="click:preview" >미리보기</a> 
						<ul class="list-unstyled mb-0">
							<li class="g-brd-top g-brd-gray-light-v7 mb-0 ms-hover">
								<a class="d-flex align-items-center u-link-v5 g-parent g-py-15" href="#!" data-bind="click: property">
									<span class="g-font-size-18 g-color-gray-light-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active g-mr-15">
									<i class="hs-admin-view-list-alt"></i>
									</span>
									<span class="g-color-gray-dark-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active">속성</span>
								</a>
							</li>
							<li class="g-brd-top g-brd-gray-light-v7 mb-0 ms-hover">
								<a class="d-flex align-items-center u-link-v5 g-parent g-py-15" href="#!" data-bind="click: permissions">
									<span class="g-font-size-18 g-color-gray-light-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active g-mr-15">
									<i class="hs-admin-lock"></i>
									</span>
									<span class="g-color-gray-dark-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active">접근 권한 설정</span>
								</a>
							</li>
						</ul>
					</section>
				</div>
				<!-- End side menu -->
			</div>
         </div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
	<#include "includes/permissions-and-properties.ftl"> 
	<#include "includes/resource-select-window.ftl"> 
</body>
</html>
</#compress>