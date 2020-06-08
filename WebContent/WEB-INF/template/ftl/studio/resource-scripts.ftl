<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "스크립트" />	
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
			"kendo.core.min"				: "<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.core.min"/>",
			"kendo.web.min"	 				: "<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.web.min"/>", 
			"kendo.messages"				: "<@spring.url "/js/kendo/custom/kendo.messages.ko-KR"/>",
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
  	  var observable = new community.data.observable({ 
  	  	visible : false,
  	  	editable : false,
  	  	filename : '',
  	  	path : '',
  	  	folder : true,
  	  	fullpath: '',
  	  	warp : false,
  	  	edit: function(){
			var $this = this;
			$this.set('editable', true);
		},  	  	
		cancle: function(){
			var $this = this;
			$this.set('editable', false); 
		},
		getScriptWindow : function (){
			var $this = this; 
			getScriptWindow( );
		},
		saveOrUpdate : function(e){  
			var $this = this; 
			var template = community.data.template('#: filename # 파일을 수정합니다. 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '스크립트 업데이트',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){ 
                		community.ui.progress($('#features'), true );
						community.ui.ajax('<@spring.url "/data/secure/mgmt/resources/script/save-or-update.json"/>', {
							contentType : "application/json",
							data: community.ui.stringify({ path : $this.get('path'), fileContent : editor.getValue() }),
							success: function(response){ 
								community.ui.notify( "파일이 변경 되었습니다.");
								$this.cancle();
							}	
						}).always( function () {
							community.ui.progress($('#features'), false );
						});	 
                		dialog.close();
						return false;
                	},
                	primary: true },
              	{ text: '취소'}
            	]		
			}).open(); 
		},	
  		onResize : function(){}
  	  }); 
  	  
  	  observable.bind("change", function(e) {
	  	if ( e.field === 'editable' ){
	  		editor.setReadOnly( !observable.get('editable') );
		}
		if ( e.field === 'warp' ){
	  		editor.getSession().setUseWrapMode( observable.warp );
		}
	  });
	  
  	  var editor = createCodeEditor($("#resource-editor"), observable.get('warp'), "ace/mode/java");
  	  editor.setReadOnly(!observable.get('editable'));
  	  createTreeView(observable); 
  	  community.data.bind( $('#features') , observable );
  	  console.log("END SETUP APPLICATION.");	
  	}); 
  	
	function getScriptWindow(){ 
  		var renderTo = $('#script-window');
  		if( !community.ui.exists( renderTo )){ 
			var observable = community.data.observable({ 
				name : null,
				type : null,
				validate : function(){
					var $this = this;
					var kendoValidator = renderTo.kendoValidator({
				        validate: function(e) {
				            console.log("valid" + e.valid);
				        }
				    }).data("kendoValidator");
				    return kendoValidator.validate();
				},
				clear : function(){
					var $this = this; 
					$this.set("name", null);
					$this.set("type", null); 
					var kendoValidator = renderTo.data("kendoValidator"); 
					if(kendoValidator != null){
						kendoValidator.hideMessages();
					} 
				},
				wizard : function () {
					var $this = this; 
					if( $this.validate() ){
						community.ui.send('resource-scripts-wizard', {name: $this.name , type: $this.type} , 'GET', '_blank' );
						window.close();
					} 
				}
			});  
			var window = community.ui.window( renderTo, {
				width: "570px",
				title: "스크립트",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){
					observable.clear();
				},
				close: function(){ 
				}
			});  
			community.data.bind(renderTo, observable);
  		}
  		community.ui.window( renderTo ).center().open();
  	}
  	  	
  	function createTreeView(observable){
  		var renderTo = $('#resource-treeview');
		if( !community.ui.exists(renderTo) ){  
			var editor = getCodeEditor($("#resource-editor"));
			var treeview = community.ui.treeview ( renderTo , { 
				dataSource : {					
					transport: {
						read: { url : '<@spring.url "/data/secure/mgmt/resources/script/list.json"/>', dataType: "json" }
					},
					schema: {		
						model: { id: "path", hasChildren: "directory" }
					}
				},
				template: kendo.template($("#treeview-template").html()),
				dataTextField: "name",
				dataBound: function(e){
					observable.set('visible' , true);
				},
				change: function(e) {
					var $this = this;
					var selectedCells = $this.select();	 
					var filePlaceHolder = $this.dataItem( $this.select() );  
					if( community.ui.defined(filePlaceHolder) ){ 
						community.ui.progress($('#features'), true );
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/resources/script/get.json" />' ,{
							data : { path: filePlaceHolder.path },
							success : function(response){ 
								observable.set('filename', response.name);
								observable.set('fullpath', response.path);
								observable.set('path', filePlaceHolder.path);
								observable.set('folder', response.directory );
								editor.setValue( response.fileContent );
							}
						}).always( function () { 
							community.ui.progress($('#features'), false);
						});
					}else{
						
					}
				}	
			}); 
		}
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
	
	function getCodeEditor(renderTo){
		return ace.edit(renderTo.attr("id"))
	} 
	
  	function refresh(){
  		
  		community.ui.treeview ( $('#resource-treeview')  ).dataSource.read();
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
				<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
					<div class="media">
						<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
						<a class="u-link-v5 g-font-size-16 g-font-size-18--md g-color-gray-light-v6 g-color-secondary--hover k-grid-refresh" href="javascript:refresh();"><i class="hs-admin-reload"></i></a>
						</h3> 
						<div class="media-body d-flex justify-content-end"> 
							<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#" data-bind="click:getScriptWindow, disabled:editable">
								<i class="hs-admin-plus g-font-size-18"></i>
								<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
							</a>
 
						</div>
					</div>
				</header>	
				<header class="card-header g-brd-gray-light-v7 g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm" data-bind="visible:visible" style="display:none;">
			                    <div class="media">
			                      <h3 class="d-flex align-self-center g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0 g-font-weight-300"> 
									<i class="hs-admin-file g-font-size-18 g-color-gray-light-v6" data-bind="invisible:folder" style="display:none;" ></i> 
			                      	<i class="hs-admin-folder g-font-size-18 g-color-gray-light-v6" data-bind="visible:folder" style="display:none;" ></i>
			                     	<span class="g-ml-15 g-color-gray-light-v6 g-font-weight-500" data-bind="text:filename" ></span>
			                      </h3> 
			                      <div class="media-body d-flex justify-content-end" data-bind="invisible:folder">   
									<label class="d-flex align-items-center justify-content-between g-mb-0">
										<span class="g-pr-20 g-font-weight-300">에디터 줄바꿈 설정/해지</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="useWarp" value="true" data-bind="checked: warp" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									<div data-bind="invisible:folder" >
									<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" style=""></a>
									<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" style="display:none;"></a>  
									<a class="hs-admin-save u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#1" data-bind="visible:editable, click:saveOrUpdate" style="display: none;"></a>
			                      	</div>
			                      </div> 
			                    </div>
				</header>	
				<!-- start splitter -->
					<div data-role="splitter" style="height:800px; width: 100%; border:0;"
						data-panes="[
						{ collapsible: true, size: '280px', min: '200px', max: '500px' },
						{ collapsible: false } ]">
						<div class="pane-content g-pa-5 g-bg-white g-pa-0">
							<div id="resource-treeview" style="height:100%;" ></div>
						</div>
						<div class="pane-content" style="padding:0px;marin:0;" > 
							<div data-bind="visible:folder" class="g-mt-150 text-center" style="display:none;"> 	
							<img alt="svgImg" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHg9IjBweCIgeT0iMHB4Igp3aWR0aD0iMjQwIiBoZWlnaHQ9IjI0MCIKdmlld0JveD0iMCAwIDEyOCAxMjgiCnN0eWxlPSIgZmlsbDojMDAwMDAwOyI+PHBhdGggZmlsbD0iI0Q4RDRFQSIgZD0iTTEwMC4yLDEwNEgxOWMtNS41LDAtMTAtNC41LTEwLTEwVjI5aDk4LjdjNiwwLDEwLjcsNS4zLDkuOSwxMS4ybC03LjUsNTVDMTA5LjUsMTAwLjIsMTA1LjIsMTA0LDEwMC4yLDEwNCB6Ij48L3BhdGg+PHBhdGggZmlsbD0iI0ZGRiIgZD0iTTEwNCwxMDRIMTljLTUuNSwwLTEwLTQuNS0xMC0xMFYyNGgyNC42YzMuMywwLDYuNSwxLjcsOC4zLDQuNWw0LjEsNi4xYzEuOSwyLjgsNSw0LjUsOC4zLDQuNUg4OSBjNS41LDAsMTAsNC41LDEwLDEwdjQxdjEuOUM5OSw5Ni41LDEwMC44LDEwMC44LDEwNCwxMDRMMTA0LDEwNHoiPjwvcGF0aD48cGF0aCBmaWxsPSIjNDU0QjU0IiBkPSJNMTAwLjIsMTA3SDE5Yy03LjIsMC0xMy01LjgtMTMtMTNWMjRjMC0xLjcsMS4zLTMsMy0zaDI0LjZjNC40LDAsOC40LDIuMiwxMC44LDUuOGw0LjEsNi4xIGMxLjMsMiwzLjUsMy4xLDUuOCwzLjFIODljNy4yLDAsMTMsNS44LDEzLDEzdjM1YzAsMS43LTEuMywzLTMsM3MtMy0xLjMtMy0zVjQ5YzAtMy45LTMuMS03LTctN0g1NC40Yy00LjQsMC04LjQtMi4yLTEwLjgtNS44IGwtNC4xLTYuMWMtMS4zLTItMy41LTMuMS01LjgtMy4xSDEydjY3YzAsMy45LDMuMSw3LDcsN2g4MS4yYzMuNSwwLDYuNS0yLjYsNi45LTYuMWw3LjUtNTVjMC4yLTItMC40LTQtMS43LTUuNSBjLTEuMy0xLjUtMy4yLTIuNC01LjItMi40Yy0xLjcsMC0zLTEuMy0zLTNzMS4zLTMsMy0zYzMuNywwLDcuMywxLjYsOS43LDQuNGMyLjUsMi44LDMuNiw2LjUsMy4yLDEwLjJsLTcuNSw1NSBDMTEyLjMsMTAyLjEsMTA2LjcsMTA3LDEwMC4yLDEwN3oiPjwvcGF0aD48cGF0aCBmaWxsPSIjNDU0QjU0IiBkPSJNMTA3LjcsMzJINDNjLTEuNywwLTMtMS4zLTMtM3MxLjMtMywzLTNoNjQuN2MxLjcsMCwzLDEuMywzLDNTMTA5LjMsMzIsMTA3LjcsMzJ6Ij48L3BhdGg+PC9zdmc+">
							</div>
							<div id="resource-editor" style="height:100%;width:100%;" data-bind="invisible:folder"></div>  
						</div>
					</div>  		
					<!-- end splitter --> 
 
				<div class="row" data-bind="visible:editable" style="display:none;">
                    <div class="col-md-9 ml-auto text-right g-mt-15"> 
                    	<button class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:saveOrUpdate" type="button">저장</button>
                    	<button class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md g-mb-10" type="button" data-bind="click:cancle" >취소</button>
                	</div>
                </div>
			</div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
  
	<div id="script-window" class="g-pa-5 g-height-600" style="display:none;" >
		<section class="g-pa-15">  
			<label class="g-mb-10 g-font-weight-600" for="input-image-name">스크립트 종류<span class="text-danger">*</span></label>
			<p class="g-pb-15">
			스크립트 종류를 선택하고 파일 이름을 입력하여 주세요. 
			DATA 는 REST 서비스에 사용되며 PAGE 페이지에 필요한 값을 전달하기 위하여 사용됩니다. 
			스크립트 만들기 버튼을 클릭하며 스크립트 생성을 위한 마법사가 실행됩니다.
			</p>
			<div class="form-group g-mb-15">
			<label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
				<input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="script-type" checked="" value="data" type="radio" data-bind="checked: type">
				<div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
					<i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
				</div>DATA
			</label>
			<label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
				<input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="script-type" checked="" value="page" type="radio" data-bind="checked: type">
				<div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
					<i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
				</div>PAGE
			</label>
			</div>
			<div class="form-group g-mb-15">
				<label class="g-mb-10 g-font-weight-600" for="input-image-name">이름 <span class="text-danger">*</span></label>
				<div class="g-pos-rel">
					<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
						<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
					</span>
					<input id="input-script-name" name="파일명" class="form-control form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-14 g-py-10" type="text" 
						placeholder="파일명" data-bind="value:name" required validationMessage="{0}를 입력하여 주세요." >
				</div>
				<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-script-name" role="alert" style="display:none;"></span>
			</div>	
			<div class="text-right g-mt-25">
				<a href="javascript:void(this);" class="btn btn-md g-rounded-50 u-btn-blue g-mr-10" data-bind="click:wizard">새로운 서버 스크립트 만들기</a>
			</div>				
		</section>
	</div>
	
  	<script id="treeview-template" type="text/kendo-ui-template">
	#if(item.directory){# 
		<i class="hs-admin-folder"></i> 
	 # }else{#  
		<i class="hs-admin-file"></i> 
	 #}#
	<span class="g-ml-5">#: item.name # </span>
 
    </script>		  	  
</body>
</html>