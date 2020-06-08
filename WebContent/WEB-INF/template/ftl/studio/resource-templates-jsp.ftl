<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "템플릿(JSP)" />	
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
  	  	editable : false,
  	  	filename : '',
  	  	path : '',
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
						community.ui.ajax('<@spring.url "/data/secure/mgmt/resources/jsp/save-or-update.json"/>', {
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
	  
  	  var editor = createCodeEditor($("#resource-editor"), observable.get('warp'), "ace/mode/jsp");
  	  editor.setReadOnly(!observable.get('editable'));
  	  
  	  createTreeView(observable); 
  	  community.data.bind( $('#features') , observable );
  	  console.log("END SETUP APPLICATION.");	
  	}); 
  	
  	function createTreeView(observable){
  		var renderTo = $('#resource-treeview');
		if( !community.ui.exists(renderTo) ){  
			var editor = getCodeEditor($("#resource-editor"));
			var treeview = community.ui.treeview ( renderTo , { 
				dataSource : {					
					transport: {
						read: { url : '<@spring.url "/data/secure/mgmt/resources/jsp/list.json"/>', dataType: "json" }
					},
					schema: {		
						model: { id: "path", hasChildren: "directory" }
					}
				},
				template: kendo.template($("#treeview-template").html()),
				dataTextField: "name",
				change: function(e) {
					var $this = this;
					var selectedCells = $this.select();	 
					var filePlaceHolder = $this.dataItem( $this.select() );  
					if( community.ui.defined(filePlaceHolder) ){ 
						community.ui.progress($('#features'), true );
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/resources/jsp/get.json" />' ,{
							data : { path: filePlaceHolder.path },
							success : function(response){ 
								observable.set('filename', response.name);
								observable.set('fullpath', response.path);
								observable.set('path', filePlaceHolder.path);
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
		mode = mode || "ace/mode/html", useWrapMode = useWrapMode || false; 
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
							<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" style=""></a>
							<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" style="display: none;"></a> 
						</div>
					</div>
				</header>	
				<div class="row no-gutters"> 
					<div class="col col-md-12">
				<!-- start splitter -->
					<div data-role="splitter" style="height: 700px; width: 100%; border:0;"
						data-panes="[
						{ collapsible: true, size: '280px', min: '200px', max: '500px' },
						{ collapsible: false } ]">
						<div class="pane-content g-pa-5">
							<div id="resource-treeview"></div>
						</div>
						<div class="pane-content">  
							<div id="resource-editor" style="height:100%;"></div> 
						</div>
					</div>  		
					<!-- end splitter -->
					<div class="col-md order-md-2 ml-md-auto text-md-right g-font-weight-300 g-color-gray-dark-v11 g-mb-10">
						<div class="media align-items-start">
							<div class="media-body"></div>
							<label class="d-flex align-items-center justify-content-between g-mb-0">
								<span class="g-pr-20 g-font-weight-300">에디터 줄바꿈 설정/해지</span>
								<div class="u-check">
									<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="useWarp" value="true" data-bind="checked: warp" type="checkbox">
									<div class="u-check-icon-radio-v8">
										<i class="fa" data-check-icon=""></i>
									</div>
								</div>
							</label>
							</div>
						</div>	 
					</div>
				</div>
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