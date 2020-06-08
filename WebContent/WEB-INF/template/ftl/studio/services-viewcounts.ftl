<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "뷰카운터" />	
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
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom" ], function($) {  
  	  	
  	  	console.log("START SETUP APPLICATION.");	
  	  	community.ui.studio.setup();	
  	  
  	  	var ServiceConfig = community.data.Models.define({
			fields: { 	
				enabled : { type : "boolean", defaultValue: ${ CommunityContextHelper.getConfigService().getApplicationBooleanProperty( CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false)?string} } 
			},
			 copy : function ( target ){ 
		    	target.set("enabled", this.get("enabled"));
		    }
		});
	
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		viewcounts : new ServiceConfig(),
		editable : false, 
		configurable : false,
		settings : function(){
			var $this = this;
			$this.set('configurable', !$this.get('configurable') );	
			createSettingsWindow($this);
		},		
		refresh : function (){
			var $this = this ;	 
			$this.load();
		},
		setSource : function( data ){
			var $this = this ;	 
			data.copy( $this.viewcounts );
			$this.set('editable', false ); 
			createStatsGrid($this); 
		},
		load: function(){
			var $this = this;
			community.ui.progress($('#features'), true);	
			community.ui.ajax('<@spring.url "/data/secure/mgmt/services/viewcounts/config.json"/>', {
				contentType : "application/json",
				success: function(data){	
					console.log(kendo.stringify(data));
					$this.setSource( new ServiceConfig(data) );
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
			$this.set('editable', false);
			$this.load();
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
                		community.ui.notification().show({ title:'',  message: '${PAGE_NAME} 정보가 변경되었습니다.'}, "success"); 
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/viewcounts/config/save-or-update.json" />', {
							data: community.ui.stringify($this.viewcounts),
							contentType : "application/json",						
							success : function(response){
								$this.load();
								dialog.close();
							} 
						}).always( function () { 
							community.ui.progress($('#features'), false); 
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
	  community.data.bind( $('#features') , observable );  
	  observable.load();
  	  console.log("END SETUP APPLICATION.");	
  	});	

  	function createSettingsWindow( observable ){  
  		var renderTo = $('#settings-window');
  		if( !community.ui.exists( renderTo )){ 
  			var window = community.ui.window( renderTo, {
				width: "600px",
				minWidth : 600,
				maxWidth : 1000,
				title: "${PAGE_NAME} 설정",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){   
				},
				close: function(){ 
				}
			}); 
			community.data.bind( renderTo , observable );
  		} 
  		community.ui.window( renderTo ).center().open();
  	} 
  	   	 
  	function createStatsGrid( observable ) {
  		var renderTo = $('#stats-grid');
		if( !community.ui.exists(renderTo) ){   
			community.ui.grid(renderTo, {
				autoBind : false,
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/services/viewcounts/stats.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					error : community.ui.error,
					pageSize: 50,
					serverFiltering: true,
					serverSorting: true, 
					serverPaging: true, 
					schema: {
						total: "totalCount",
						data:  "items",
						model: {
							id : "tagId",
							fields :{
								ENTITY_TYPE : { type: "number", defaultValue: 0 },
								ENTITY_ID: { type: "number", defaultValue: 0 },
								NAME: {  type: "string", defaultValue: "N/A" },
								DISPLAY_NAME: {  type: "string", defaultValue: "N/A" },
								VIEWCOUNT:  { type: "number", defaultValue: 0 }
							}
						}
					}
				},  
				sortable: true,
				filterable: true,
				selectable : "row",
				pageable: true, 
				columns: [
					{ field: "NAME", title: "NAME", width: 100 , template: '#if(NAME == null){# N/A #}else{# #:NAME# #}#' ,filterable : false, sortable:false },  
					{ field: "DISPLAY_NAME", title: "DISPLAY_NAME", width: 100 , template: '#if(DISPLAY_NAME == null){# N/A #}else{# #:DISPLAY_NAME# #}#' ,filterable : false, sortable:false },  
					{ field: "ENTITY_TYPE", title: "ENTITY_TYPE" ,  width: 100  },
					{ field: "ENTITY_ID", title: "ENTITY_ID" ,  width: 100  },
					{ field: "VIEWCOUNT", title: "VIEWCOUNT" ,  width: 100  }
				],
				change: function(e) {
 				}
			}); 
		}
		community.ui.grid(renderTo).dataSource.read();
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
				
          	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-mb-5">
					<div class="media">
						<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
						<a class="u-link-v5 g-font-size-16 g-font-size-18--md g-color-gray-light-v6 g-color-secondary--hover k-grid-refresh" href="#" data-bind="click:refresh"><i class="hs-admin-reload"></i></a>
						</h3> 
						<div class="media-body d-flex justify-content-end">
							<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#" data-bind="click:settings">
								<i class="hs-admin-panel g-font-size-18"></i>  
								 <span class="g-hidden-sm-down g-ml-10"> ${PAGE_NAME} 설정</span>
							</a>							 
						</div>
					</div>
			</header>
			<div id="stats-grid" class="g-brd-left-0 g-brd-right-0 g-mb-1">
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
					<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" style=""></a>
					<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" style="display: none;"></a>	 
				</div>
			</div>
		</header>
		<div class="g-pa-15 g-pa-30--sm"> 
					<div class="row no-gutters g-mb-15">
						<div class="col-md-12 g-mb-10">
							<label class="d-flex align-items-center justify-content-between g-mb-0" for="input-enabled">
								<span class="g-pr-20 g-font-weight-500">사용 여부</span>
								<div class="u-check">
									<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-enabled" name="input-enabled" value="true" data-bind="checked: viewcounts.enabled, enabled:editable" type="checkbox">
									<div class="u-check-icon-radio-v8"><i class="fa" data-check-icon=""></i></div>
								</div>
							</label>
							<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
								서비스 사용 유무를 지정합니다. 사용을 채크하면 뷰 카운터 이력이 기록됩니다. 
							</small>
						</div>
					</div> 
					<div class="row no-gutters">
						<div class="col-md-3" style="display:none;"></div>
						<div class="col-md-9 ml-auto text-right" data-bind="visible:editable" style="display:none;">  
							<button class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 " data-bind="click:saveOrUpdate" type="button">저장</button>
							<button class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md " type="button" data-bind="click:cancle" >취소</button>
						</div>
					</div> 
                  </div>
		</div>		
  </div>
</body>
</html>