<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <#assign PAGE_NAME = "스크립트 생성 마법사" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	
  <#assign KENDO_VERSION = "2019.3.917" />	  
  <meta charset="utf-8">
  <!-- Title -->
  <title>STUDIO :: ${PAGE_NAME} </title>
  <meta name="description" content="Script Build Wizard">
  <meta name="author" content="Island">
  
  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.common.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.mobile.nova.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/bootstrap/4.3.1/bootstrap.min.css"/>"> 
  
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/css/unify-admin.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/hs-admin-icons/hs-admin-icons.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-awesome/css/font-awesome.min.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>">  
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/community.ui.studio/custom.css"/>">

  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/jquery.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.all.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/cultures/kendo.culture.ko-KR.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/custom/kendo.messages.ko-KR.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.data.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.core.js"/>"></script> 
  <script src="<@spring.url "/js/ace/ace.js"/>"></script>  
  
  <script> 
   var __name = <#if RequestParameters.name?? >'${RequestParameters.name?remove_ending(".json")?remove_ending(".html")}'<#else>null</#if>;
   var __type = <#if RequestParameters.type?? >'${RequestParameters.type}'<#else>'page'</#if>;
   
  $(document).ready(function() {  
	community.ui.setup(); 
	
	var RequestBody = community.data.Models.define({
		fields: { 	
			name : { type : "string" },
			className : { type : "string" },
			enabled : { type: "boolean", defaultValue: false }
		}
	});
	var GroovyScript = community.data.Models.define({
		id: "scriptId",
		fields: { 	
			scriptId: { type: "number", defaultValue: 0 },
			location : 	{ type: "string", defaultValue: '${CommunityContextHelper.getRepository().getFile('groovy-script')}' },
			filename : { type : "string", defaultValue : "" },
			packageName : { type : "string", defaultValue : 'services.groovy.data' },
			className : { type : "string", defaultValue : "" },
			superClassName : { type : "string" , defaultValue : "void" },
			services : { type : "object" , defaultValue : []},
			parameters :  { type : "object" , defaultValue : []},
		 	requiredRoles : { type : "object" , defaultValue : []},
		 	requestBody : { type : "object" , defaultValue : new RequestBody() },
			exist : { type : "boolean" , defaultValue : false },
			setUser :  { type : "boolean" , defaultValue : false },
			setMultipart : { type : "boolean" , defaultValue : false }
		},
		copy : function ( target ){
		    	target.scriptId = this.get("scriptId");
		    	target.set("location", this.get("location"));
		    	target.set("filename", this.get("filename"));
		    	target.set("packageName", this.get("packageName"));
		    	target.set("className", this.get("className"));	
		    	target.set("superClassName", this.get("superClassName"));
		    	target.set("services", this.get("services"));
		    	target.set("requiredRoles", this.get("requiredRoles"));
		    	target.set("exist", this.get("exist"));
		    	target.set("requestBody", this.get("requestBody"));
		    	target.set("parameters", this.get("parameters"));
		    	target.set("setUser", this.get("setUser"));
		    	target.set("setMultipart", this.get("setMultipart"));
		}
	}); 
	var observable = new community.data.observable({ 
		script : new GroovyScript(),
		editable : false, 
		setSource : function( data ){
			var $this = this ;	  
			data.copy( $this.script );	 
			if( $this.script.exist )
				$this.set('editalbe', false);
			else
				$this.set('editable', true);
		},
		setType : function ( type ){
			var $this = this ;	  
			if( type === 'data' ) {
				$this.set('script.packageName' , 'services.groovy.data' ); 
				$this.set('script.location' , $this.get('script.location') + '/data' ); 
			}else if (type === 'page'){
				$this.set('script.packageName' , 'services.groovy.view' ); 
				$this.set('script.location' , $this.get('script.location') + '/view' ); 
			} 
			$this.set('script.superClassName' , type );
		},
		load: function(name, type){
			var $this = this;  
			$this.set('script.filename' , name + '.groovy' );
			$this.setType( type ); 
			community.ui.progress($('#features'), true);	
			community.ui.ajax('<@spring.url "/data/secure/mgmt/scripts/0/prepare.json"/>', {
				contentType : "application/json",
				data: community.ui.stringify($this.script),
				success: function(data){ 
					$this.setSource( new GroovyScript(data) );
				}	
			}).always( function () {
				community.ui.progress($('#features'), false);
			});	
		},
		createScriptCode: function( handler ){
			var $this = this; 
			community.ui.progress($('#features'), true);	
			community.ui.ajax('<@spring.url "/data/secure/mgmt/scripts/0/create.json"/>', {
				contentType : "application/json",
				data: community.ui.stringify($this.script),
				success: function(data){ 
					$this.set('script.content',data.content);
					if( community.ui.defined(handler)){
						handler();
					}
				}	
			}).always( function () {
				community.ui.progress($('#features'), false);
			});	
		},
		saveOrUpdate: function(){
			var $this = this; 
			var template = community.data.template('스크립트 파일을 서버에 업로드 합니다. 서버에 동일한 파일이 존재하는 경우 업데이트 됩니다. 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '스크립트 업로드',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){ 
                		$this.set('script.content', getScriptCodeEditor().getValue() ); 
                		
                		console.log(community.ui.stringify($this.script));
                		
                		community.ui.progress($('#script-code-window'), true);	
						community.ui.ajax('<@spring.url "/data/secure/mgmt/scripts/0/save-or-update.json"/>', {
							contentType : "application/json",
							data: community.ui.stringify($this.script),
							success: function(response){ 
								if( community.ui.defined( response.data.backup ) )
								{
									community.ui.notify( "파일이 존재합니다. 기존 파일은 <br/>"  + response.data.backup + " 에 <br/>백업되었습니다.");
								}
								Done($this);
							}	
						}).always( function () {
							community.ui.progress($('#script-code-window'), false);
						});	
                		dialog.close();
						return false;
                	},
                	primary: true },
              	{ text: '취소'}
            	]		
			}).open(); 
		},
	 	closeScriptCodeWindow : function(){
	 		community.ui.window( $('#script-code-window') ).close();
	 	},
		next : function(){ 
			var $this = this;
			$this.script.services = community.ui.grid($("#services-grid")).dataSource.data();
			$this.script.requiredRoles = community.ui.multiselect( $("#roles-select") ).value();
			$this.script.parameters = community.ui.grid($("#parameters-grid")).dataSource.data();
			getScriptCodeWindow($this);
		},
		setRequestBody : function(e){
			var $this = this; 
			var item = $( e.sender.element );
			e.sender.enable(false);
			$this.set('script.requestBody.name', item.data('service-name') );
			$this.set('script.requestBody.className', item.data('service-class') );
		},
		addService : function(e){
			var $this = this; 
			var item = $( e.sender.element );
			e.sender.enable(false);
			community.ui.grid( $("#services-grid") ).dataSource.add( getServiceClassInfo(item) ); 
		}
	});  
	
	if( __name != null )
		observable.load(__name , __type);  
	community.data.bind( $('#features') , observable );
	createServicesGrid(observable);
	createRolesSelect(observable);
	createParametersGrid(observable);
  });

  function getServiceClassInfo(item){
  	var dependencies = [];
  	if( community.ui.defined( item.data('service-dependencies') ) ) {
  		dependencies = item.data('service-dependencies').split(",");
  	}
  	return { className: item.data('service-class'), name:item.data('service-name'), dependencies: dependencies, required : false };
  }
    
  function createRolesSelect(observable){ 
	var renderTo = $("#roles-select"); 
  	var select = community.ui.multiselect( renderTo, {
		placeholder: "필요한 권한을 선택하세요.",
		dataTextField: "name",
		dataValueField: "name",
		autoBind: false,
		dataSource: {
			transport: { read : { url:'<@spring.url "/data/secure/mgmt/security/roles/list.json"/>' } },
            error : community.ui.error,
            schema: {
				total: "totalCount",
				data:  "items",
				model: community.data.model.Role
			}
		}
	});
  }
  
  function createParametersGrid(observable){ 
  	var renderTo = $("#parameters-grid");
  	var grid = community.ui.grid(renderTo, {
		dataSource: {
			data: [],
			schema: {
				model: {
					id : 'name',
					fields: {
						type: { type: "string" }, 
						defaultValue: { type: "string" }
					}
				}
			}
		},
		height: 200,
		pageable: false, 
		scrollable: true,
		sortable: true,
		filterable: false, 
		toolbar: [{ name: "create" , text: "파라메터 추가하기", template: '<a href="javascript:void(this);" class="btn u-btn-outline-lightgray g-mr-5 k-grid-add">파라메터 추가</a>' }],
		columns: [ 
			{ field: "name", title: "파이메터" },
			{ field: "defaultValue", title: "디폴트 값", width: "100px" },
			{ command: ["edit", "destroy"], title: "&nbsp;", width: "250px" }],
		editable: "inline"
	});  
  }
  
  function createServicesGrid(observable){ 
  	var renderTo = $("#services-grid");
  	var grid = community.ui.grid(renderTo, {
		dataSource: {
			data: [],
			schema: {
				model: {
					id : 'name',
					fields: {
						className: { type: "string" }, 
						name: { type: "string" , validation: { required: true } }, 
						required: { type: "boolean" , defaultValue: false }
					}
				}
			}
		},
		height: 200,
		pageable: false, 
		scrollable: true,
		sortable: true,
		filterable: false, 
		toolbar: [{ name: "create" , text: "서비스 추가하기", template: '<a href="javascript:void(this);" class="btn u-btn-outline-lightgray g-mr-5 k-grid-add">서비스 추가</a>' }],
		columns: [ 
			{ field: "className", title: "Class Name"},
			{ field: "name", title: "Name", width: "200px" },
			{ field: "required", title: "Required", width: "100px" },
			{ command: ["edit", "destroy"], title: "&nbsp;", width: "250px" }],
		editable: "inline"
	});  
  } 

  function getScriptCodeEditor(){
  	var renderTo = $('#script-code-editor');
	return ace.edit(renderTo.attr("id"));
  } 

  function Done( observable ){
  	var renderTo = $('#script-code-editor');
	ace.edit(renderTo.attr("id")).destroy(); 
	renderTo.remove();  
	var template = kendo.template($('#success-template').html());
	var window = community.ui.window(  $('#script-code-window') );
	window.content(template(observable));  
  }
	 
  function getScriptCodeWindow( observable ){ 
  		var renderTo = $('#script-code-window');
  		if( !community.ui.exists( renderTo )){ 
			var editor = createCodeEditor($('#script-code-editor'));
			editor.setTheme("ace/theme/chrome");    
			var window = community.ui.window( renderTo, {
				width: "900px",
				minHeight : "700px",
				title: "코드",
				scrollable: false,
				visible: false,
				modal: {
				   preventScroll: true
				},
				actions: [ "Minimize", "Maximize", "Close"], // 
				open: function(){  
					observable.createScriptCode(function (){
						editor.setValue( observable.script.content );
					});
				},
				close: function(){ 
				}
			});  
			community.data.bind(renderTo, observable);
  		}
  		community.ui.window( renderTo ).center().open();
  	}
  	
	function createCodeEditor( renderTo, useWrapMode, mode ){
		mode = mode || "ace/mode/java", useWrapMode = useWrapMode || false; 
		if( renderTo.contents().length == 0 ){ 
			var editor = ace.edit(renderTo.attr("id"));		
			editor.getSession().setMode(mode);
			editor.setTheme("ace/theme/monokai");
			editor.getSession().setUseWrapMode( useWrapMode );
		}
		return ace.edit(renderTo.attr("id"));
	} 
  </script>
  <style>
  .k-tabstrip>.k-content{
  	margin:0;
  }
  </style>
</head>
<body class=""> 
	<div id="features" class="container">  
		<section>
			<header class="g-mb-80 g-mt-20">
              <div class="u-heading-v6-2  text-uppercase">
                <h2 class="h4 u-heading-v6__title g-font-weight-300">새로운 서버 스크립트 </h2>
              </div>
              <div class="g-line-height-2 g-pl-90">
                <p>새로운 서버 스크립트를 생성합니다.</p>
              </div>
            </header> 
		<div class="row">
			<div class="col-md-2">
				<h3 class="h4 g-font-weight-300">스크립트 코드를 생성을 위하여 정확하게 기입하여 주세요.</h3>
				<small class="g-color-red">스크립트 유형은 값을 화면에 전달하는 웹 페이지는 PAGE, 결과를 바로 JSON/XML 형식으로 응답하는 API 는 DATA 형식을 선택합니다.</small>
			</div>
			<div class="col-md-10"> 
				<div class="alert alert-dismissible fade show g-bg-red g-color-white rounded-0" role="alert" data-bind="visible:script.exist" >
                        <div class="media">
                          <span class="d-flex g-mr-10 g-mt-5">
                            <i class="icon-ban g-font-size-25"></i>
                          </span>
                          <span class="media-body align-self-center">
                            <strong>이미 존재하는 스크립트 입니다.</strong> 코드를 생성하면 기존 스크립트 코드는 없어 집니다. 
                          </span>
                        </div>
                </div>
				<div class="form-group g-mb-20">
                  <label class="g-mb-10" for="input-script-folder">소스 폴더</label>
                  <input id="input-script-folder" class="form-control form-control-md rounded-0" type="text" placeholder="스크립트 (클래스)  위치 입력하세요." data-bind="value: script.location, enabled:false">
                  <small class="form-text text-muted g-font-size-default g-mt-10">스크립트가 저장되는 위치입니다.</small>
				</div>
				<div class="form-group g-mb-20">
                  <label class="g-mb-10" for="input-script-package">파일</label>
                  <input id="input-script-package" class="form-control form-control-md rounded-0" type="text" placeholder="파일 이름을 입력하세요." data-bind="value:script.filename, enabled:editable">
                  <small class="form-text text-muted g-font-size-default g-mt-10">스크립트가 저장되는 파일이름입니다. 파일이름은 같은 경로에서 유일해야 합니다.</small>
				</div>
				<label class="g-mb-10">스크립트 유형</label>
				<div class="g-mb-15">
                    <label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
                      <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="input-script-superclass" checked="" type="radio" value="page" data-bind="checked: script.superClassName">
                      <div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
                        <i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
                      </div>
                      PAGE
                    </label>
                    <label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
                      <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="input-script-superclass" checked="" type="radio" value="data" data-bind="checked: script.superClassName">
                      <div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
                        <i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
                      </div>
                      DATA
                    </label>
                    <small class="form-text text-muted g-font-size-default g-mt-10">스크립트 유형에 따라 부모 클래스가 자동으로 상속됩니다.</small>
                </div>
				<hr class="g-brd-gray-light-v4 g-mx-minus-30"/> 
				<div class="row">
					<div class="col-md-6">
						<div class="form-group g-mb-20">
		                  <label class="g-mb-10" for="input-script-package">패키지</label>
		                  <input id="input-script-package" class="form-control form-control-md rounded-0" type="text" placeholder="스크립트 (클래스) 패키지를 입력하세요." data-bind="value:script.packageName">
		                  <small class="form-text text-muted g-font-size-default g-mt-10">디폴트는 스크립트 유형에 따라 자동으로 설정됩니다.</small>
						</div>	
					</div>
					<div class="col-md-6">
						<div class="form-group g-mb-20">
		                  <label class="g-mb-10" for="input-script-name">클래스</label>
		                  <input id="input-script-name" class="form-control form-control-md rounded-0" type="text" placeholder="서버 스크립트 클래스 이름을 입력하세요." data-bind="value:script.className">
		                  <small class="form-text text-muted g-font-size-default g-mt-10">(클래스) 이름은 동일한 패키지에서 유일해야 합니다.</small>
						</div>		
					</div>
				</div> 
				
				<hr class="g-brd-gray-light-v4 g-mx-minus-30"/>	 
				<h4 class="h6 g-font-weight-700 g-mb-20">고급설정</h4>
				<label class="g-mb-1">❶ Request 객체의 파라메터를 정의합니다.</label>
				<div class="row g-mb-30">
					<div id="parameters-grid" style="width:100%;" class="g-ma-10"></div>
				</div>
				<hr class="g-brd-gray-light-v4 g-mx-minus-30"/> 
				<label class="g-mb-1">❷ Request Body 를 정의합니다.</label>
				<!-- Toggles Checkbox -->
                <div class="form-group">
                    <label class="d-flex align-items-center justify-content-between">
                      <span>JSON 형식 객체 데이터를 Request Body 로 전송 합니다.</span>
                      <div class="u-check">
                        <input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" data-bind="checked:script.requestBody.enabled" type="checkbox">
                        <div class="u-check-icon-radio-v8">
                          <i class="fa" data-check-icon=""></i>
                        </div>
                      </div>
                    </label>
                </div>  
                <div data-bind="visible:script.requestBody.enabled" >
                <div class="row">
					<div class="col-md-6">
						<div class="form-group g-mb-20">
		                  <label class="g-mb-10" for="input-script-package">클래스</label>
		                  <input id="input-script-package" class="form-control form-control-md rounded-0" type="text" placeholder="클래스를 입력하세요." data-bind="value:script.requestBody.className">
		                  <small class="form-text text-muted g-font-size-default g-mt-10">JSON 객체를 바인딩할 클래스를 입력하세요.</small>
						</div>	
					</div>
					<div class="col-md-6">
						<div class="form-group g-mb-20">
		                  <label class="g-mb-10" for="input-script-name">이름</label>
		                  <input id="input-script-name" class="form-control form-control-md rounded-0" type="text" placeholder="이름을 입력하세요." data-bind="value:script.requestBody.name">
		                  <small class="form-text text-muted g-font-size-default g-mt-10">스크립트에서 사용할 변수 이름을 입력하세요.</small>
						</div>		
					</div>
				</div>
				
                <label class="g-mb-10">이름을 클릭하며 바로 Request Body 설정이 추가됩니다.</label>	
                <div class="row g-mb-30">
                	<button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: setRequestBody }"
			                    data-service-class="architecture.community.web.model.DataSourceRequest" 
			                    data-service-name="dataSourceRequest" >
			             <span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                <i class="hs-admin-plus g-absolute-centered"></i>
			             </span>
			        DataSourceRequest</button>
                </div>
                <!-- End Toggles Checkbox -->
               </div> 
                <hr class="g-brd-gray-light-v4 g-mx-minus-30"/>	 
                <label class="g-mb-1">❸ 접근권한을 설정합니다.</label>
				<label class="g-mb-10 small g-color-blue">권한이 부여되면 해당 권한의 사용자만 서비스 호출이 가능합니다.</label>
				<div class="row g-mb-30" >
					<div id="roles-select" style="width:100%;" class="g-ma-10"></div>
				</div>
				
				<label class="g-mb-10">❹ 스크립트에서 사용할 서비스 객체를 정의합니다.</label>
				<div class="row g-mb-30">
					<div id="services-grid"></div>
				</div>
				
				<label class="g-mb-10">다음의 서비스 이름을 클릭하면 간편하게 서비스가 추가됩니다.</label>	
				<div class="row g-mb-30">
					<!-- service --> 
					<div class="col-md-4">  
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.ee.service.ConfigServicee"
			                    data-service-dependencies="java.util.List,java.util.Map,"
			                    data-service-name="configService" >
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			              	Config Service</button> 
						<button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.security.spring.acls.CommunityAclService" 
			                    data-service-dependencies="architecture.community.security.spring.acls.PermissionsBundle"
			                    data-service-name="aclService" >
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            	Acl Service</button> 

			            
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.services.MailService" 
			                    data-service-dependencies="java.sql.Types,org.springframework.jdbc.core.RowMapper,architecture.community.query.Utils"
			                    data-service-name="mailService" >
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			           Mail Service</button>
			           
			           <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.user.UserProfileService" 
			                    data-service-dependencies="architecture.community.user.User, architecture.community.user.UserProfile"
			                    data-service-name="customUserProfileService" >
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			           UserProfile Service</button>
			           
			            
			        </div>
			        <div class="col-md-4">  
			        	<button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.query.CustomQueryService" 
			                    data-service-dependencies="architecture.community.query.CustomTransactionCallback, architecture.community.query.CustomTransactionCallbackWithoutResult,java.sql.Types,org.springframework.jdbc.core.RowMapper,architecture.community.query.Utils"
			                    data-service-name="customQueryService" >	
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            	Custom Query Service</button>  
			        	<button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.query.dao.CustomQueryJdbcDao" 
			                    data-service-dependencies="java.sql.Types,org.springframework.jdbc.core.RowMapper,architecture.community.query.Utils"
			                    data-service-name="customQueryJdbcDao" >
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            Custom Query Jdbc Dao</button>
												
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="javax.sql.DataSource"
			                    data-service-name="dataSource">
			                <span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			            	</span>
			            DataSource</button> 
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200"
			                    data-bind="events: { click: addService }"
			                    data-service-class="org.springframework.transaction.support.PlatformTransactionManager"
			                    data-service-name="transactionManager">
			                <span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			            	</span>
			            TransactionTemplate</button> 
			            
			        </div>
			        <div class="col-md-4">   
			        	<button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.page.PageService" 
			                    data-service-name="pageService">
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            Page Service</button>
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.image.ImageService" 
			                    data-service-dependencies="architecture.community.image.Image"
			                    data-service-name="imageService"
			                    style="width: 180px" >
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            	Image Service</button>
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.attachment.AttachmentService" 
			                     data-service-dependencies="architecture.community.attachment.Attachment"
			                    data-service-name="attachmentService"> 
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>	Attachment Service
			            </button>
			            
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.share.SharedLinkService" 
			                     data-service-dependencies="architecture.community.share.SharedLink"
			                    data-service-name="sharedLinkService">	
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            	Link Service</button>
			            
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.tag.TagService" 
			                     data-service-dependencies="architecture.community.tag.TagDelegator,architecture.community.tag.ContentTag"
			                    data-service-name="tagService">	
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            	Tag Service</button>	            
			            	
			            <button data-role="button" class="g-mt-5 g-brd-0 g-pa-10 g-width-200" 
			                    data-bind="events: { click: addService }"
			                    data-service-class="architecture.community.viewcount.ViewCountService" 
			                    data-service-name="viewCountService">	
			            	<span class="g-pa-5  g-pos-rel g-width-40 g-height-40  g-font-size-14 g-color-gray-dark-v4  g-brd-around g-brd-gray-dark-v4 rounded-circle g-mr-10">
			                	<i class="hs-admin-plus g-absolute-centered"></i>
			              	</span>
			            	ViewCount Service</button>	 			            	
			        </div>
			        
					<!-- end of service -->
				</div>  
				<hr class="g-brd-gray-light-v4 g-mx-minus-30"/>	 
				<div class="row g-mb-30">
				<div class="col-md-12">
                <!-- Toggles based on Checkboxes --> 
                  <!-- Toggles Checkbox -->
                  <div class="form-group">
                    <label class="d-flex align-items-center justify-content-between">
                      <span>사용자 정보 포함</span>
                      <div class="u-check">
                        <input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="radGroup3_1" data-bind="checked:script.setUser" type="checkbox">
                        <div class="u-check-icon-radio-v8">
                          <i class="fa" data-check-icon=""></i>
                        </div>
                      </div>
                    </label>
                  </div>
                  <!-- End Toggles Checkbox -->
                  <!-- Toggles Checkbox -->
                  <div class="form-group">
                    <label class="d-flex align-items-center justify-content-between">
                      <span>멀티파트 리퀘스트  정보 포함(멀티파트 리퀘스트인 경우)</span>
                      <div class="u-check">
                        <input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="radGroup3_1" data-bind="checked:script.setMultipart" type="checkbox">
                        <div class="u-check-icon-radio-v8">
                          <i class="fa" data-check-icon=""></i>
                        </div>
                      </div>
                    </label>
                  </div>
                  <!-- End Toggles Checkbox -->
                <!-- End Toggles based on Checkboxes -->
				<div class="row">
					<div class="col-md-9 ml-auto text-right g-mt-30">
						<button  class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:next" type="button">코드 만들기</button>
					</div>
				</div>
              </div> 
        	</section>	    		    		    
		</div>
	</div>
	
	<div id="script-code-window" style="display:none;" >
		<section class="g-pa-0">
			<h4 data-bind="text:script.filename" class="g-ml-15 g-font-size-14"></h4>
			<div id="script-code-editor" class="g-height-600 g-brd-0"></div> 
			<div class="g-pa-5 text-right">
		    	<button class="btn btn-xl u-btn-secondary g-width-160--md g-font-size-14 g-mr-15" type="button" data-bind="click:saveOrUpdate" >저장하기</button>
		    	<button class="btn btn-xl u-btn-outline-gray-dark-v6 g-width-160--md g-font-size-14" data-bind="click:closeScriptCodeWindow">취소</button>
		    </div>				
		</section>
	</div>	
	
	<script id="success-template" type="text/kendo-ui-template">
	<section class="g-pa-50" >
		<div class="container">
			<div class="row">
				<div class="col align-self-center">
					<h3 class="h4">#: script.filename #</h3>
					<p class="lead g-mb-20 g-mb-0--md">
					스크립트 파일이 성공적으로 배포되었습니다. 배포된 스크립트 파일은 PAGE, API 에서 선택하여 사용할 수 있습니다.
					</p>
					<p class="lead g-mt-20 g-mb-0--md">
					확인을 클릭하면 창을 닫습니다.
					</p>
				</div>
				<div class="col-md-3 align-self-center text-md-right">
                      <button class="btn btn-md u-btn-primary g-brd-2 rounded-0" onclick="javascript:self.close();" >확인</a>
                </div>
			</div>
        </div>
	</section>
    </script>
</body>
</html>