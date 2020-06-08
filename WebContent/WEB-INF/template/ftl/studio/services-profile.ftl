<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "사용자 프로필" />	
  <#assign PARENT_PAGE_NAME = "서비스" />	  
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
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom", "ace" ], function($) {  
  	  	
		console.log("START SETUP APPLICATION.");	
		community.ui.studio.setup(); 
		var ProfileServiceConfig = community.data.Models.define({
			fields: { 	
				scriptSource : { type : "object" },
				cacheable : { type: "boolean", defaultValue: false },
				enabled : { type: "boolean", defaultValue: false }
			},
			copy : function ( target ){
				target.set("scriptSource", this.get("scriptSource"));
				target.set("cacheable", this.get("cacheable"));
				target.set("enabled", this.get("enabled")); 
			}
		});
	
  	var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		config : new ProfileServiceConfig(),
		editable : false,
		configurable : false,
		testable : false,
		settings : function(){
			var $this = this;
			$this.set('configurable', !$this.get('configurable') );	
			createSettingsWindow($this);
		},
		editor : {
			editable : false,
			warp : false,
			mode : 'ace/mode/java',
			edit: function(){
				var $this = this;
				$this.set('editor.editable', true);
			},
			cancle: function(){
				var $this = this;
				$this.set('editor.editable', false);
			}
		},		
		refresh : function (){
			var $this = this ;	 
			$this.load();
		},
		setSource : function( data ){
			var $this = this ;	 
			data.copy( $this.config );
			$this.set('editable', false );
			if( $this.config.enabled )
				$this.set('testable', true );
			else 
				$this.set('testable', false ); 
			getCodeEditor($("#resource-editor")).setValue( $this.config.scriptSource.fileContent );	
		},
		load: function(){
			var $this = this;
			community.ui.progress($('#features'), true);	
			community.ui.ajax('<@spring.url "/data/secure/mgmt/services/profile/config.json"/>', {
				contentType : "application/json",
				success: function(data){
					$this.setSource( new ProfileServiceConfig(data) );
				}	
			}).always( function () {
				community.ui.progress($('#features'), false);
			});
		},
		testUserId : 1,
		test: function(){ 
			var $this = this;
			community.ui.progress($('#features'), true); 
			community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/profile/test.json" />', {
				contentType : "application/json",	
				data: community.ui.stringify({ testUserId :  $this.get('testUserId') }),					
				success : function(response){
					if( response.success ){
						community.ui.notification().show({ title:'',  message: '테스트에 성공하였습니다.'}, "success"); 
						getCodeEditor($("#test-editor")).setValue( community.ui.stringify( response.data.profile ) );	
					}else {
						community.ui.notification().show({ title:'',  message: '테스트에 실패 하였습니다.'}, "error"); 
					}
				} 
			}).always( function () {  
				community.ui.progress($('#features'), false); 
			});	 
		},	
		edit: function(){
			var $this = this;
			$this.set('editable', true);
		},
		cancle: function(){
			var $this = this;
			$this.load(); 
			$this.set('configurable', false);
		},
		select : function(e){
			var $this = this; 
		},		
		saveOrUpdate : function(){
			var $this = this;
			var template = community.data.template('${PAGE_NAME} 서비스 설정을 변경하시겠습니까 ?');
			var dialog = community.ui.dialog( null, {
				title : '${PAGE_NAME}',
				content : template($this),
				actions: [
                { text: '확인', 
                	action: function(e){  
                		community.ui.progress($('#features'), false); 
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/profile/save-or-update.json" />', {
							data: community.ui.stringify($this.config),
							contentType : "application/json",						
							success : function(response){
								community.ui.notification().show({ title:'',  message: '${PAGE_NAME} 정보가 변경되었습니다.'}, "success"); 
								$this.load(); 
							} 
						}).always( function () { 
							community.ui.progress($('#features'), false); 
							dialog.close();
						});	  
						return false;
                	},
                	primary: true },
              	{ text: '취소'}
            	]		
			}).open();
			console.log( dialog );
		}
	  });  
	  
	  observable.bind("change", function(e) {
	  	if ( e.field === 'editor.editable' ){
	  		editor.setReadOnly( !observable.get('editor.editable') );   
		}else if (e.field === 'editor.warp'){
			editor.getSession().setUseWrapMode(observable.get('editor.warp'));
		}
	  }); 
	  
	  var editor = createCodeEditor($('#resource-editor'), observable.get('editor.warp') , observable.get('editor.mode'));  
	  editor.setTheme("ace/theme/chrome");
	  
	  community.data.bind( $('#features') , observable ); 
	  observable.load();
  	  console.log("END SETUP APPLICATION.");	
  	});	 

  	function createSettingsWindow( observable ){  
  		var renderTo = $('#settings-window');
  		if( !community.ui.exists( renderTo )){ 
  			var window = community.ui.window( renderTo, {
				width: "800px",
				minWidth : 600,
				maxWidth : 1000,
				title: "${PAGE_NAME} 설정",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){   
					editor.setValue('');
					observable.set('testUserId', 1);
				},
				close: function(){ 
				}
			});  
			var editor = createCodeEditor($('#test-editor'), true, "ace/mode/json"); 
			editor.setReadOnly(true); 
			community.data.bind( renderTo , observable );
  		} 
  		community.ui.window( renderTo ).center().open();
  	} 
	
	function getCodeEditor(renderTo){
		return ace.edit(renderTo.attr("id"))
	} 
		
	function createCodeEditor( renderTo, useWrapMode, mode ){
		mode = mode || "ace/mode/java", useWrapMode = useWrapMode || false; 
		if( renderTo.contents().length == 0 ){ 
			var editor = ace.edit(renderTo.attr("id"));		
			editor.getSession().setMode(mode);
			editor.setTheme("ace/theme/chrome");
			editor.getSession().setUseWrapMode( useWrapMode );
			editor.setReadOnly(true);
		}
		return ace.edit(renderTo.attr("id"));
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

        <div class="g-pa-20">
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30">${PAGE_NAME}</h1> 
          <!-- Content -->
          <div id="features" class="container-fluid"> 
          	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-mb-0">
					<div class="media">
						<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
						</h3> 
						<div class="media-body d-flex justify-content-end"> 
							<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#!" data-bind="click:settings">
								<i class="hs-admin-panel g-font-size-18"></i>  
								 <span class="g-hidden-sm-down g-ml-10"> ${PAGE_NAME} 설정</span>
							</a> 
						</div>
					</div>
			</header>
			
			<header class="card-header g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
				<div class="media">
					<h3 class="d-flex align-self-center g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
						<i class="hs-admin-file g-font-size-18 g-color-gray-light-v6"></i>&nbsp;<span data-bind="html: config.scriptSource.name" class="g-font-weight-200"></span>
					</h3>
					<div class="media-body d-flex justify-content-end">

					<label class="d-flex align-items-center justify-content-between g-mb-0">
						<span class="g-pr-20 g-font-weight-300">에디터 줄바꿈 설정/해지</span>
						<div class="u-check">
							<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="useWarp" value="true" data-bind="checked:editor.warp" type="checkbox">
							<div class="u-check-icon-radio-v8">
							<i class="fa" data-check-icon=""></i>
							</div>
						</div>
					</label> 
						<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#!" data-bind="invisible:editor.editable, click:editor.edit" style=""></a>
						<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#!" data-bind="visible:editor.editable, click:editor.cancle" style="display: none;"></a>
						<a class="hs-admin-save u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#!" data-bind="visible:editor.editable, click:editor.saveOrUpdate" style="display: none;"></a>
						<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-5 g-ml-10--sm g-ml-15--xl" href="#!" data-bind="click:refresh">
							<i class="hs-admin-reload g-font-size-20"></i>
						</a>
					</div>
				</div>
			</header>			
            <div id="resource-editor" class="g-brd-gray-light-v6 g-brd-top-1 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-0" style="height:800px;"></div> 
         </div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
	<div id="settings-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" > 
		<header class="card-header g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
			<div class="media">
				<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
				<div class="media-body d-flex justify-content-end"> 
					<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#!" data-bind="invisible:editable, click:edit" style=""></a>
					<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#1" data-bind="visible:editable, click:cancle" style="display: none;"></a>	 
					<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-5 g-ml-10--sm g-ml-15--xl" href="#!" data-bind="click:refresh" >
						<i class="hs-admin-reload g-font-size-20"></i>
					</a>
				</div>
			</div>
		</header>  	
				<div class="g-pa-15 g-pa-30--sm">  
					<div class="row no-gutters g-mb-15">
						<div class="col-md-6 g-mb-10">
							<label class="d-flex align-items-center justify-content-between g-mb-0 g-pr-10" for="input-cacheable">
								<span class="g-pr-20 g-font-weight-500">캐쉬 사용 여부</span>
								<div class="u-check">
									<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-cacheable" name="input-cacheable" value="true" data-bind="checked: config.cacheable, enabled:editable" type="checkbox">
									<div class="u-check-icon-radio-v8"><i class="fa" data-check-icon=""></i></div>
								</div>
							</label>
							<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
								캐쉬 사용 유무를 지정합니다.
							</small>
						</div>
						<div class="col-md-6 g-mb-10">
							<label class="d-flex align-items-center justify-content-between g-mb-0 g-pr-10" for="input-enabled">
								<span class="g-pr-20 g-font-weight-500">사용 여부</span>
								<div class="u-check">
									<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-enabled" name="input-enabled" value="true" data-bind="checked: config.enabled, enabled:editable" type="checkbox">
									<div class="u-check-icon-radio-v8"><i class="fa" data-check-icon=""></i></div>
								</div>
							</label>
							<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
								서비스 사용 유무를 지정합니다.
							</small>
						</div>
					</div> 		
					
					<div class="form-group">
						<label class="g-mb-10 g-font-weight-600" for="input-api-template">서버 스크립트<span class="text-danger">*</span></label>
						<div class="g-pos-rel">
							<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-sec ondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#!" data-type="script">
							<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-1 g-opacity-1--success"><i class="hs-admin-search g-absolute-centered g-font-size-default"></i></span>
							</a>
							<input id="input-api-script" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="서버 스크립트 파일 경로를 입력하세요" data-bind="value: config.scriptSource.path, enabled:false">
						</div>
						<p class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
						서버 스크립트를 선택하거나 새로운 스크립트를 만들어 사용해주세요.
						</p>
					</div>		

					<div  data-bind="visible:testable" style="display:none;">  	 
						<div class="form-group">
	                    <label class="g-mb-10">서비스 테스트</label>
	                    <div class="g-pos-rel">
	                      <button class="btn u-input-btn--v1 g-width-140 g-bg-blue g-rounded-right-20 g-color-white" data-bind="click:test" >
	                        테스트
	                      </button>
	                      <input class="form-control form-control-md g-brd-gray-light-v7 g-brd-gray-light-v3 g-rounded-20 g-px-14 g-py-10" type="text" placeholder="User Id" data-bind="value:testUserId">
	                    </div>
	                    <p class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-15">테스트를 위한 USER ID 값을 입력하여 주세요.</p>
						</div> 
						<div id="test-editor" class="g-brd-gray-light-v6 g-brd-top-1 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-0" style="height:200px;"></div>
					</div>						
															
					<div  data-bind="visible:editable" style="display:none;">  
					<hr class="d-flex g-brd-gray-light-v7 g-my-15 g-my-30--md">
					<div class="row no-gutters">
						<div class="col-md-8 ml-auto text-right" data-bind="visible:editable" style="display:none;">  
							<button class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10" data-bind="click:saveOrUpdate" type="button">저장</button>
							<button class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md" type="button" data-bind="click:cancle" >취소</button>
						</div>
					</div> 	 
				</div> 
	
	</div>
      
	<!-- RESOURCE WINDOW -->
	<div id="resource-select-window" class="g-pa-5 g-height-600" style="display:none;" >
	<div class="media g-pa-15">
		<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">리소스</h3>
		<div class="media-body d-flex justify-content-end">
		</div>
	</div>     
  	<div id="resource-treeview" class="g-height-400 g-brd-1 g-pa-10 g-mb-15"></div>
	<div class="text-right"> 
		<a href="#!" data-bind="click:select, visible:selectable" class="btn btn-md u-btn-3d u-btn-primary g-mb-10 g-font-size-default g-ml-5">선택</a>
	</div>	
	</div>
	
	<script id="treeview-template" type="text/kendo-ui-template">
	#if(item.directory){# 
                <i class="hs-admin-folder"></i> 
	 # }else{#  
                <i class="hs-admin-file"></i> 
	 #}#
	<span class="g-ml-5">#: item.name # </span>
	# if (!item.items) { #
		<a class='delete-link' href='\#'></a> 
	# } #
    </script>		  
	  
</body>
</html>