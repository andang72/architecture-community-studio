<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "사용자" />	
  <#assign PARENT_PAGE_NAME = "보안" />	  
  <title>STUDIO :: ${PAGE_NAME} </title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">

  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" href="<@spring.url "/css/kendo/2019.2.619/kendo.common.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/2019.2.619/kendo.nova.min.css"/>"> 
  
  <!-- Application JavaScript
  		================================================== -->
  <script>	
    var __userId = <#if RequestParameters.userId?? >${RequestParameters.userId}<#else>0</#if>;	
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
	        "kendo.web.min" 				: { "deps" :['jquery', 'kendo.core.min','kendo.culture.min'] },
	        "kendo.messages" 				: { "deps" :['kendo.web.min'] },
	        <!-- community ui -->
	        "community.ui.data" 			: { "deps" :['jquery', 'kendo.web.min'] },
	        "community.ui.core" 			: { "deps" :['jquery', 'kendo.web.min', 'community.ui.data'] },
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min', 'kendo.messages', 'community.ui.data' , 'community.ui.core' ] }
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
			"kendo.core.min"				: "<@spring.url "/js/kendo/2019.2.619/kendo.core.min"/>",
			"kendo.web.min"	 				: "<@spring.url "/js/kendo/2019.2.619/kendo.web.min"/>",
			"kendo.messages"				: "<@spring.url "/js/kendo/custom/kendo.messages.ko-KR"/>",
			
			"kendo.culture.min"				: "<@spring.url "/js/kendo/2019.2.619/cultures/kendo.culture.ko-KR.min"/>", 
			"jszip"							: "<@spring.url "/js/kendo/2019.2.619/jszip.min"/>",
			<!-- community ui -->
			"community.ui.data"				: "<@spring.url "/js/community.ui/community.ui.data"/>",
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>",
			"ace" 							: "<@spring.url "/js/ace/ace"/>",
			"dropzone"					: "<@spring.url "/js/dropzone/dropzone"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom",  "ace", "dropzone" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  
  	  community.ui.studio.setup();	
  	   
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		user : new community.data.model.User(),
		userAvatarUrl : '<@spring.url "/images/no-avatar.png"/>',
		visible : false,
		editable : true,
		isNew : true, 
		editor : { warp : false },
		objectTypes : [
			{ text: "정의되지 않음", value: "-1" },
			<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
			{ text:'${item.NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
			</#list>
		],
		contentTypes : [
			{ text: "HTML", value: "text/html;charset=UTF-8" },
			{ text: "JSON", value: "application/json;charset=UTF-8" }
		], 	
		edit: function(){
			var $this = this;
			$this.set('editable', true); 
		},
		cancle: function(){
			var $this = this;
			if( $this.get('isNew') ){
				// or back..
				$('#pageForm')[0].reset();
			}else{
			    // or back..
				$this.set('editable', false);
				$this.load(__userId); 
			}
		},
		back: function(){
			window.history.back();
		},
		refresh: function(){
			var $this = this;
			$this.load( $this.user.userId );
		},
		load: function(objectId){
			var $this = this;
			if( objectId > 0 ){
				community.ui.progress($('#features'), true);	
				community.ui.ajax('<@spring.url "/data/secure/mgmt/security/users/"/>' + objectId + '/get.json', {
					contentType : "application/json",
					success: function(data){	
						$this.setSource( new community.data.model.User(data) );
						if( $this.user.userId > 0 )
							createAvatarDropzone($this);
					}	
				}).always( function () {
					community.ui.progress($('#features'), false);
				});	
			}else{
				$this.setSource(new community.data.model.User()); 
			}	
		},	 
		property : function(e){
			var $this = this;
			getPropertyWindow($('#property-window'), $this);
		},
		permissions : function(e){
			var $this = this;
			getPermissionsWindow($('#permissions-window'), $this); 
		},
		setSource : function( data ){
			var $this = this ;	  
			if( data.get('userId') > 0 ){
				data.copy( $this.user );
				$this.set('editable', false );
				$this.set('isNew', false ); 
				$this.set('external', false ); 
				$this.set('userAvatarUrl', community.data.url.userPhoto( '<@spring.url "/"/>', $this.user ) ); 
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
			console.log("save or update.." + validator.validate() );
			if (validator.validate()){
				var saveOrUpdateUrl = '<@spring.url "/data/secure/mgmt/security/users/save-or-update.json" />';  
				community.ui.progress($('#features'), true);	
				community.ui.ajax( saveOrUpdateUrl, {
					data: community.ui.stringify($this.user),
					contentType : "application/json",
					success : function(response){
						$this.setSource( new community.data.model.User( response.data.item ) );
					}
				}).always( function () {
					community.ui.progress($('#features'), false); 
				});				
			}			
		}
	  });	
		
	  observable.bind("change", function(e) {
	  	if ( e.field === 'editable' ){ 
	  	}
		if ( e.field === 'editor.warp' ){
			console.log(observable.get('editor.warp')); 
		}
	  });
	    
	  community.data.bind( $('#features') , observable );   
	  observable.load(__userId);  
  	  console.log("END SETUP APPLICATION.");	
  	});	
	 
 
	/**
	*
	*/
	
	function createAvatarDropzone(observable){  
		var renderTo = "#dropzoneForm"; 
		try{ 
			Dropzone.forElement(renderTo)
		} catch (err ){ 
			var myDropzone = new Dropzone(renderTo, {
				url: '<@spring.url "/data/secure/mgmt/security/users/"/>'  + observable.user.userId + '/avatar/upload.json' ,
				paramName: 'file',
				maxFilesize: 5,
				acceptedFiles: 'image/*'	,
				previewsContainer: renderTo + ' .dropzone-previews'	,
				previewTemplate: '<div class="dz-preview dz-file-preview"><div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div></div>'
			}); 
			myDropzone.on("addedfile", function(file) {
			  console.log('start');
			  community.ui.progress($(renderTo), true);
			}); 
			myDropzone.on("complete", function() {
			  community.ui.progress($(renderTo), false);
			  console.log('done');
			  observable.set('userAvatarUrl', community.data.url.userPhoto( '<@spring.url "/"/>', observable.user ) );
			}); 
		}
	}
 
	/**
	* Property Window for Page Object
	*/
	function getPropertyWindow(renderTo,  observable){ 
	
		if( !community.ui.exists( renderTo )){ 
			var observable2 = community.data.observable({ }); 
			var grid = community.ui.grid( $('#property-grid'), {
				dataSource: {
					transport: { 
						read : 		{ url:'<@spring.url "/data/secure/mgmt/security/users/"/>'+  observable.user.get('userId') + '/properties/list.json',   type:'post', contentType : "application/json" },
						create : 	{ url:'<@spring.url "/data/secure/mgmt/security/users/"/>'+  observable.user.get('userId') + '/properties/update.json', type:'post', contentType : "application/json" },
						update : 	{ url:'<@spring.url "/data/secure/mgmt/security/users/"/>'+  observable.user.get('userId') + '/properties/update.json', type:'post', contentType : "application/json" },
						destroy : 	{ url:'<@spring.url "/data/secure/mgmt/security/users/"/>'+  observable.user.get('userId') + '/properties/delete.json', type:'post', contentType : "application/json" },
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					batch: true, 
					schema: {
						model: community.data.model.Property
					}
				},
				height : 550,
				sortable: true,
				filterable: false,
				pageable: false, 
				editable: "inline",
				columns: [
					{ field: "name", title: "이름", width: 250 , validation: { required: true} },  
					{ field: "value", title: "값" , validation: { required: true} },
					{ command: ["destroy"], title: "&nbsp;", width: "250px"}
				],
				toolbar: kendo.template('<div class="p-sm"><div class="g-color-white"><a href="\\#"class="btn u-btn-outline-lightgray g-mr-5 k-grid-add"><span class="k-icon k-i-plus"></span> 속성 추가</a><a href="\\#"class="btn u-btn-outline-lightgray g-mr-5 k-grid-save-changes"><span class="k-icon k-i-check"></span> 저장</a><a href="\\#"class="btn u-btn-outline-lightgray g-mr-5 k-grid-cancel-changes"><span class="k-icon k-i-cancel"></span> 취소</a><a class="pull-right hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-mt-7 g-mr-5" data-kind="properties" data-action="refresh"></a></div></div>'),    
				save : function(){  
				}
			});			
			
			$('#property-grid').on( "click", "a[data-kind=properties],a[data-action=refresh]", function(e){		
				var $this = $(this);	
				grid.dataSource.read();	
			});		
						
			var window = community.ui.window( renderTo, {
				width: "900px",
				title: "프로퍼티",
				visible: false,
				modal: true,
				actions: ["Close"], // 
				open: function(){ 
				},
				close: function(){  
				}
			});
		}	
		community.ui.window( renderTo ).center().open();
	}  
	
	function getPermissionsWindow(renderTo, observable){ 
	
		if( !community.ui.exists( renderTo )){  
			var observable2 = community.data.observable({  
				editable : false,
				roles : new kendo.data.DataSource({
		            schema: {
		                model: community.data.model.Role,
		                total: "totalCount",
						data:  "items" 
		            },
		            batch: true,
		            pageSize: 15,
		            transport: {
		                read: {
		                    url: "<@spring.url "/data/secure/mgmt/security/roles/list.json"/>", type:'post', contentType : "application/json"
		                }
		            }
		        }), 
		        available : new kendo.data.DataSource({
		        	data: [], 
		        	schema:{ model: community.data.model.Role } 
		        }),
		        granted : new kendo.data.DataSource({
		        	data: [], 
		        	schema:{ model: community.data.model.Role } 
		        }),
		        change : function( e ) {
		        	var $this = this; 
		        	console.log('changed..');
		        	$this.set('editable', true);
		        },
		        cancle : function(){
		        	var $this = this; 
		        	community.ui.window( renderTo ).close();
		        },
		        saveOrUpdate:function(e){
					var $this = this;
					community.ui.progress(renderTo, true);	
					community.ui.ajax( '<@spring.url "/data/secure/mgmt/security/users/" />' + observable.user.userId + '/roles/save-or-update.json' , {
						data: community.ui.stringify($this.granted.data()),
						contentType : "application/json",
						success : function(response){
							$this.setSource(function(){
								community.ui.notify("롤이 설정이 변경되어 습니다.");
								community.ui.window( renderTo ).close();
							});
						}
					}).always( function () {
						community.ui.progress(renderTo, false);
					}); 
				},	
		        setSource : function( func ){ 
		        	console.log('loading user permissions...');
		        	var $this = this; 
		        	community.ui.ajax( '<@spring.url "/data/secure/mgmt/security/users/" />' + observable.user.userId + '/roles/list.json', {
						contentType : "application/json",
						success : function(response){
		 		        	var roles = $this.roles.view();
		 		        	$this.granted.data( response.items );
							$this.available.data( roles.slice() );
							$.each( response.items , function (index, value) {
								var data = $this.available.get(value.roleId );
								$this.available.remove(data);
							} ); 
							$this.set('editable', false);
							if( community.ui.defined(func)){
								func();
							}
						}
					}).always( function () {});  
		        }
			});  
			observable2.roles.fetch().then(function(e){
				observable2.setSource();	
			}); 
			var window = community.ui.window( renderTo, {
				width: "540px",
				title: "권한",
				visible: false,
				modal: true,
				actions: ["Close"], // 
				open: function(){ 
					observable2.setSource();
				},
				close: function(){  
				}
			});  
			community.data.bind( renderTo , observable2 );   
		}	
		community.ui.window( renderTo ).center().open();
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
                      	<a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:isNew, click:refresh"></a>
                      </div>
                    </div>
                  </header>
                  <div class="card-block g-pa-15 g-pa-30--sm"> 
	              	
	              	
	              	<!-- photo --> 
	              	<section data-bind="invisible:isNew"> 
								<div class="d-inline-block g-pos-rel g-mb-30" >
			                      <a class="u-badge-v2--lg u-badge--bottom-right g-width-32 g-height-32 g-bg-lightblue-v3 g-bg-primary--hover g-mb-20 g-mr-20" href="#!">
			                        <i class="community-admin-pencil g-absolute-centered g-font-size-16 g-color-white"></i>
			                      </a>
			                      <img class="rounded-circle" data-bind="attr:{ src: userAvatarUrl }" src="/images/no-avatar.png" alt="Image description" width="100" height="100">
			                      <form action="" method="post" enctype="multipart/form-data" id="dropzoneForm" class="u-dropzone">
			                       	 <div class="dz-default dz-message">
			                       	 	<a class="u-badge-v2--lg u-badge--bottom-right g-width-32 g-height-32 g-bg-lightblue-v3 g-bg-primary--hover g-mb-20 g-mr-20" href="#!">
					                        <i class="hs-admin-upload g-absolute-centered g-font-size-16 g-color-white"></i>
										</a>
									</div>       
									<div class="dropzone-previews"></div> 
										<div class="fallback">
											<input name="file" type="file" multiple style="display:none;"/>
										</div>
									</form> 
								</div>	
					</section> 
	              	<!-- ./photo -->
	              	
	             	<form id="pageForm">
	                		<div class="row g-mb-15" >
                  				<div class="col-md-6">
	                				<label class="g-mb-10 g-font-weight-600" for="input-user-name">이름 <span class="text-danger">*</span></label>		
	                				<div class="g-pos-rel">
	                				<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                	</span>
		                			<input id="input-user-name" name="input-user-name" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="이름을 입력하세요" 
		                      			data-bind="value:user.name, enabled:editable" required validationMessage="이름을 입력하여 주세요." autofocus>
		                      		</div>	
		                      		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-user-name" role="alert" style="display:none;"></span>
		                		</div>	
			            		<div class="col-md-6">
	                				<label class="g-mb-10 g-font-weight-600" for="input-user-email">메일 <span class="text-danger">*</span></label>		
	                				<div class="g-pos-rel">
	                				<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                	</span>
		                			<input id="input-user-email" name="input-user-email" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="email" placeholder="메일주소를 입력하세요" 
		                      			data-bind="value:user.email, enabled:editable" required validationMessage="메일주소를 입력하여 주세요." autofocus>
		                      		</div>	
		                      		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-user-email" role="alert" style="display:none;"></span>
								</div> 
							</div>
							<div class="row g-mb-15" >
                  				<div class="col-md-6">
	                				<label class="g-mb-10 g-font-weight-600" for="input-user-username">아이디 <span class="text-danger">*</span></label>		
	                				<div class="g-pos-rel">
	                				<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                	</span>
		                			<input id="input-user-username" name="input-user-username" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="아디디을 입력하세요" 
		                      			data-bind="value:user.username, enabled:editable" required validationMessage="아이디을 입력하여 주세요." autofocus>
		                      			
		                      		</div>	
		                      		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-user-username" role="alert" style="display:none;"></span>
		                		</div>
		                		<div class="col-md-6">
									<label class="g-mb-10 g-font-weight-600" for="input-user-emailvisible">상태</label> 
									<div class="form-group g-mb-30">
										<select data-role="dropdownlist" data-bind="value: user.status, enabled:editable" style="width: 180px">
												<option value="NONE">NONE</option>
												<option value="APPROVED">APPROVED</option>
												<option value="REJECTED">REJECTED</option>
												<option value="VALIDATED">VALIDATED</option>
												<option value="REGISTERED">REGISTERED</option>
										</select>  
									</div>	
	                  			</div>
							</div>
							
							<#if RequestParameters.userId?? && ( RequestParameters.userId ="0" ) >
							<div class="row g-mb-15">
                  				<div class="col-md-6">
	                				<label class="g-mb-10 g-font-weight-600" for="input-user-password">비밀번호 <span class="text-danger">*</span></label>		
	                				<div class="g-pos-rel">
	                				<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                	</span>
		                			<input id="input-user-password" name="input-user-password" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="password" placeholder="비밀번호을 입력하세요" 
		                      			data-bind="value:user.password, enabled:editable" required validationMessage="비밀번호을 입력하여 주세요." autofocus>
		                      			
		                      		</div>	
		                      		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-user-password" role="alert" style="display:none;"></span>
		                		</div>	
		                		
		                		<div class="col-md-6">
		                			<label class="g-mb-10 g-font-weight-600" for="input-user-repassword">비밀번호 확인 <span class="text-danger">*</span></label>		
		                			<div class="g-pos-rel">
	                				<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                	</span>
		                			<input id="input-user-repassword" name="input-user-repassword" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="password" placeholder="비밀번호을 입력하세요" 
		                      			data-bind="value:user.repassword, enabled:editable" required validationMessage="비밀번호을 입력하여 주세요." autofocus>
		                      			
		                      		</div>	
		                      		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-user-repassword" role="alert" style="display:none;"></span>
								</div>
							</div>
							</#if>
							
							<div class="row g-mb-15" >
								<div class="col-auto g-mb-30">
									<label class="d-flex align-items-center justify-content-between g-mb-0" for="input-user-namevisible">
										<span class="g-pr-20 g-font-weight-500">이름공개여부</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-user-namevisible" name="input-user-namevisible" value="true" data-bind="checked: user.nameVisible,  enabled:editable" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
									이름을 공개합니다.
									</small>
								</div>		
								
								 
	                  			<div class="form-group g-mb-30">
									<div class="col-auto">
	                    			<label class="d-flex align-items-center justify-content-between g-mb-0" for="input-user-emailvisible">
										<span class="g-pr-20 g-font-weight-500">메일공개여부</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-user-emailvisible" name="input-user-emailvisible" data-bind="checked: user.emailVisible, enabled:editable" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
									메일주소를 공개합니다.
									</small>
									</div>	
	                  			</div> 
	                  			
	                  			<div class="form-group g-mb-30">
									<div class="col-auto">
	                    			<label class="d-flex align-items-center justify-content-between g-mb-0" for="input-user-enabled">
										<span class="g-pr-20 g-font-weight-500">사용여부</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-user-enabled" name="input-user-enabled" data-bind="checked: user.enabled, enabled:editable" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									</div>	
	                  			</div> 
	                  		</div>
	                  		<!-- /.row -->	
	                  		<div class="row g-mb-15" >
	                  			<div class="col-md-6">
									<label class="g-mb-10 g-font-weight-600">생성일</label>
									<div class="g-pos-rel">
									<span data-bind="text:user.creationDate" data-format="yyyy.MM.dd HH:mm"/>
									</div> 
								</div>
								<div class="col-md-6">
									<label class="g-mb-10 g-font-weight-600">수정일</label>
									<div class="g-pos-rel">
									<span data-bind="text:user.modifiedDate" data-format="yyyy.MM.dd HH:mm"/>
									</div> 
								</div>
	                  		</div>
							
                    <div class="row" data-bind="visible:editable">
                      <div class="col-md-9 ml-auto text-right g-mt-15"> 
                        <button id="showToast" class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click: saveOrUpdate" type="button">저장</button>
                        <button id="clearToasts" class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md g-mb-10" type="button" data-bind="click:cancle" >취소</button>
                      </div>
                    </div>
                  </form>
                  </div>
                </div>  																																																																																																																																																												
				</div> 
				<!-- side menu -->
				<div class="g-brd-left--lg g-brd-gray-light-v4 col-lg-2 g-mb-10 g-mb-0--md"> 
					 <span class="u-label g-bg-lightred g-rounded-3 g-ml-15 g-mr-10 g-mb-15" data-bind="visible:user.external" style="display:none;">External</span>
					<section data-bind="invisible:isNew" style="display:none;">
						<!-- images --> 
						<ul class="list-unstyled mb-0">
							<li class="g-brd-top g-brd-gray-light-v7 mb-0 ms-hover">
								<a class="d-flex align-items-center u-link-v5 g-parent g-py-15" href="#!" data-bind="click:property">
									<span class="g-font-size-18 g-color-gray-light-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active g-mr-15">
									<i class="community-admin-view-list-alt"></i>
									</span>
									<span class="g-color-gray-dark-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active">속성</span>
								</a>
							</li>
							<li class="g-brd-top g-brd-gray-light-v7 mb-0 ms-hover">
								<a class="d-flex align-items-center u-link-v5 g-parent g-py-15" href="#!" data-bind="click:permissions">
									<span class="g-font-size-18 g-color-gray-light-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active g-mr-15">
									<i class="community-admin-lock"></i>
									</span>
									<span class="g-color-gray-dark-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active">권한 설정</span>
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
 
	
	<!-- WINDOW -->
	<div id="permissions-window" class="g-pa-15" style="display:none;">  
		<div class="g-pb-10" style="width: 515px;">
			<select id="listbox1"  data-role="listbox" style="width:270px;"
                data-text-field="name"
                data-value-field="name"
                data-toolbar='{ tools: ["transferTo", "transferFrom", "transferAllTo", "transferAllFrom", "remove"] }'
                data-connect-with="listbox2"
                data-bind="source: available">
            </select>
            <select id="listbox2" data-role="listbox" style="width:236px;"
                data-connect-with="listbox1"
                data-text-field="name"
                data-value-field="name"
                data-bind="source:granted, events: { add: change, remove: change }">
            </select>  
        </div> 
        <div class="text-right">
	        <button class="btn btn-md u-btn-outline u-btn-primary g-width-200--md g-font-size-default" data-bind="visible:editable, click:saveOrUpdate" style="">확인</button>
	        <button class="btn btn-md u-btn-outline-bluegray g-mr-10 g-font-size-default" data-bind="visible:editable, click:cancle" style="">취소</button>
        </div>
	</div>
	
	<div id="property-window" class="g-pa-5 g-height-600" style="display:none;" >
		<div id="property-grid" ></div>
	</div> 
	
</body>
</html>
</#compress>