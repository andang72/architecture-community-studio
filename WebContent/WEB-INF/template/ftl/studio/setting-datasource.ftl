<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "데이터소스" />	
  <#assign PARENT_PAGE_NAME = "설정" />	  
  <#assign KENDO_VERSION = "2019.3.917" /> 
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
  	  var observable = new community.data.observable({ 
  	  	visible : true,
  	  	autodeploy : ${ CommunityContextHelper.getConfigService().getApplicationProperty(CommunityConstants.SERVICES_SETUP_DATASOURCES_ENABLED_PROP_NAME, "false") },
  	  	editable : false,
  	  	configurable : false,
  		settings : function(){
			var $this = this;
			$this.set('configurable', !$this.get('configurable') );	
			createSettingsWindow($this);
		},
 		edit: function(){
			var $this = this;
			$this.set('editable', true); 
		},
		cancle: function(){
			var $this = this;
			$this.set('editable', false);
		} 	  	
  	  });
  	  
  	  observable.bind("change", function(e) {
	  	if ( e.field === 'autodeploy' && observable.get('editable') ){ 
	  		enableAutodeploy(observable);
	  	}
	  });
  	  
  	  createDataSourceConfigGrid(observable); 
  	  
  	  $('#features').on( "click", "a[data-action=view]", function(e){		
	      var $this = $(this); 
	      //$this.data('name');
	      //$this.data('bean'); 
	      community.ui.send("<@spring.url "/secure/studio/database-schema-view" />", { name: $this.data('name') }, 'POST', '_blank'); 
  	  });
  	  
  	  community.data.bind( $('#features') , observable ); 
  	  console.log("END SETUP APPLICATION.");	
  	});
  	
  	function createSettingsWindow( observable ){  
  		var renderTo = $('#settings-window');
  		if( !community.ui.exists( renderTo )){ 
  			var window = community.ui.window( renderTo, {
				width: "500px",
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
  	
  	function createDataSource(){
  		kendo.alert("준비중입니다.");
  	}
  	
  	function enableAutodeploy(observable){
		var template = community.data.template('자동배포 설정(#: autodeploy #)을 변경합니다. 이동작은 최소할 수 없습니다.');
		var dialog = community.ui.dialog( null, {
			title : '${PAGE_NAME} 등록 옵션',
			content :template(observable),
				actions: [
                { text: '확인', 
                	action: function(e){ 
                		community.ui.progress($('.k-dialog'), true );
						community.ui.ajax('<@spring.url "/data/secure/mgmt/setup/property/update.json"/>', {
							data : community.ui.stringify({ name:'${CommunityConstants.SERVICES_SETUP_DATASOURCES_ENABLED_PROP_NAME}', value : observable.autodeploy }),
							contentType : "application/json", 
							success: function(response){ 
								if( response.success ){
									community.ui.notify( "설정이 변경 되었습니다.");
								}else{
									community.ui.notify( response.error.message , "error");
								}
							}	
						}).always( function () {
							community.ui.progress($('.k-dialog'), false );
							dialog.close();
						});	  
						return false;
                	},
                	primary: true },
              	{ text: '취소' , 
              		action : function(e){
              			observable.set( 'editable', false);
              			observable.set( 'autodeploy', !observable.autodeploy );
              			dialog.close();
              		}}
            	]		
			}).open(); 
  	}
  	
  	function deployDataSources(){
		var template = community.data.template('아직 등록되지 않는 ${PAGE_NAME}를 컨테이너에 빈으로 등록합니다. 이동작은 최소할 수 없습니다.');
		var dialog = community.ui.dialog( null, {
			title : '${PAGE_NAME} 배포',
			content :template({}),
				actions: [
                { text: '확인', 
                	action: function(e){ 
                		community.ui.progress($('.k-dialog'), true );
						community.ui.ajax('<@spring.url "/data/secure/mgmt/setup/datasource/deploy.json"/>', {
							contentType : "application/json", 
							success: function(response){ 
								if( response.success ){
									community.ui.notify( "데이터소스 배포가 완료되었습니다.");
								}else{
									community.ui.notify( response.error.message , "error");
								}
							}	
						}).always( function () {
							community.ui.progress($('.k-dialog'), false );
							dialog.close();
						});	  
						return false;
                	},
                	primary: true },
              	{ text: '취소'}
            	]		
			}).open(); 
  	}
  	
  	function initDatabase(){
		var template = community.data.template('데이터베이스를 초기화 합니다. 이동작은 최소할 수 없습니다.');
		var dialog = community.ui.dialog( null, {
			title : '데이터베이스 초기화',
			content :template({}),
				actions: [
                { text: '확인', 
                	action: function(e){ 
                		community.ui.progress($('.k-dialog'), true );
						community.ui.ajax('<@spring.url "/data/secure/mgmt/setup/database/init.json"/>', {
							contentType : "application/json", 
							success: function(response){ 
								if( response.success ){
									community.ui.notify( "데이터베이스가 초기화 되었습니다.");
								}else{
									community.ui.notify( response.error.message , "error");
								}
							}	
						}).always( function () {
							community.ui.progress($('.k-dialog'), false );
							dialog.close();
						});	  
						return false;
                	},
                	primary: true },
              	{ text: '취소'}
            	]		
		}).open(); 
  	}
  	  	
  	function refresh(){
  		var renderTo = $('#datasource-grid');
  		community.ui.grid(renderTo).dataSource.read();
  		console.log('grid refresh...');
  	}  	
  	
    function createDataSourceConfigGrid(observable){
    	var renderTo = $('#datasource-grid');
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/datasource/config/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					error : community.ui.error,
					batch: true, 
					schema: {
						model: {
							id : 'name'
						}
					}
				},
				toolbar: [{ name: "create" , text: "새로운 프로퍼티 만들기", template:community.ui.template($('#grid-toolbar-template').html())  }],
				sortable: true,
				filterable: false,
				pageable: false, 
				columns: [
					{ field: "name", title: "이름", width: 300 , validation: { required: true} , template: $('#name-column-template').html()  },  
					{ field: "driverClassName", title: "Driver" , validation: { required: true} },
					{ field: "url", title: "URL" , validation: { required: true} },
					{ field: "beanName", title: "Bean 이름" , width: 200, validation: { required: true} },
					{ field: "active", title: "등록여부" , width: 100  , validation: { required: true} },
				]
			}); 
		}
    } 
    
    function createDatabaseSchemaTableGrid(options){ 
    	var renderTo = $("#database-schema-table-grid");
    	if( renderTo.data('kendoGrid') != null && renderTo.data('name') !=  options.name ){
    		community.ui.grid(renderTo).destroy(); 
    	} 
    	if(renderTo.data('kendoGrid') == null ){
    		var template = community.ui.template('<@spring.url "/data/secure/mgmt/datasource/"/>#= name #/schema/table/list.json'); 
    		var target_url = template(options); 
    		var grid = community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url: target_url, type:'post', contentType: "application/json; charset=utf-8"}
					}, 
					error : community.ui.error, 
					serverFiltering: false,
					schema: {
						total: "totalCount",
						data: "items",
						model: {
							id: "name",
						}
					}
				},  
				toolbar : community.ui.template($('#database-schema-table-grid-template').html()),
				height: 300,
				selectable: "row",
				sortable: true,
				filterable: true,
				pageable: false, 
				noRecords: {
					template: "<p class='g-pa-20' >데이터가 로드되지 않았습니다. 잠시 후에 다시 데이터 새로고침 버튼을 클릭하여 주세요.</p>"
				},
				columns: [
					{ field: "catalog", title: "CATALOG", width: 100  },
					{ field: "schema", title: "SCHEMA", width: 100  },
					{ field: "name", title: "TABLE", width: 300   }
				],
				change: function(e) {
					var selectedRows = this.select();
					var dataItem = this.dataItem(selectedRows[0]); 
					createDatabaseSchemaTableColumnGrid(dataItem);
			    }
			});	
			  
			renderTo.find(".k-grid-toolbar").on("click", ".k-grid-refresh", function (e) {
				createDatabaseSchemaTableColumnGrid([]);
				grid.dataSource.read();
			}); 
			
			createDatabaseSchemaTableColumnGrid({ columns: [] });  
			renderTo.data('name', options.name );
    	}
    	community.ui.grid(renderTo).dataSource.read();
    } 

    
    function createDatabaseSchemaTableColumnGrid(options){ 
    	var renderTo = $("#database-schema-table-column-grid");
    	if( !community.ui.exists(renderTo) ){  
    		community.ui.grid(renderTo, {
				dataSource: {
					data : [],
					schema: {
						model: {
							id: "name",
							fields: {
								name: { type: "string" },
								type: { type: "number" }
							}
						}
					}
				},
				toolbar : community.ui.template($('#database-schema-table2-grid-template').html()),
				height: 300,
				sortable: true,
				filterable: true,
				pageable: false, 
				noRecords: {
					template: "<p class='g-pa-20' >데이터가 없습니다.</p>"
				},
				columns: [
					{ field: "name", title: "COLUMN"},
					{ field: "type", title: "TYPE", width: 100 },
					{ field: "typeName", title: "TYPE NAME", width: 120 }
				]
			});	
			
			renderTo.find(".k-grid-toolbar").on("click", ".view-data", function (e) {
				createTableDataWindow();
			});
			 			
    	}
    	community.ui.grid(renderTo).dataSource.data( options.columns );
  	}
    
    function getSelectedTable (){
   		var renderTo = $("#database-schema-table-grid"); 
   		var grid = community.ui.grid(renderTo); 
   		var selectedRows = grid.select();
		var dataItem = grid.dataItem(selectedRows[0]); 
		
		dataItem.dataSource = renderTo.data('name');
		return dataItem;
    }
      	  	
  	function createTableDataWindow(){   
  		var renderTo = $('#table-data-window');
  		
  		if( !community.ui.exists( renderTo )){ 
  			var observable = community.data.observable({ 	
  				dataSource : null,
  				catalog : null,
  				schema : null,
  				table: null,
  				columns : [],
  				refresh : function (){
  					community.ui.grid($('#table-data-grid')).dataSource.read();
  				},
  				createGrid : function () {
  					var $this = this; 
  					var renderTo2= $('#table-data-grid'); 
  					if( renderTo2.data('kendoGrid') != null ){
			    		community.ui.grid(renderTo2).destroy(); 
			    		renderTo2.html('');
			    	} 
			    	
			    	var template = community.ui.template('<@spring.url "/data/secure/mgmt/datasource/"/>#= dataSource #/schema/table/#= table#/list.json'); 
    				var target_url = template($this);  
			    	var _columns = [];
			    	$.each( $this.get('columns'), function( index, data ){
			    		_columns.push({
			    			field: data.name,
			    			title: data.name,
			    			width: 100 
			    		});
			    	}); 
			    	community.ui.grid(renderTo2, {
				    	dataSource: {
							transport: { 
								read : { url:target_url, type:'post', contentType: "application/json; charset=utf-8"},
								parameterMap: function (options, operation){	 
									if (operation !== "read" && options.models) { 
										return community.ui.stringify(options.models);
									}
									return community.ui.stringify(options);
								}
							}, 
							error : community.ui.error,
							pageSize: 100,
							serverFiltering: false,
							serverSorting: false, 
							serverPaging: true, 
							schema: {
								total: "totalCount",
								data:  "items"
							}
						},  
						height:800,
						sortable: true,
						filterable: false, 
						pageable: true,
						columns: _columns ,
						noRecords: {
							template: "<p class='g-pa-50 g-ma-0' >데이터가 없습니다.</p>"
						},
						change: function(e) {
		 				}
					}); 
  				},
  				setSource : function( data ) {
  					console.log(data);
  					var $this = this;
  					var oldTable = $this.get('table'); 
  					
  					$this.set('dataSource', data.dataSource );
  					$this.set('catalog', data.catalog );
  					$this.set('schema', data.schema );
  					$this.set('table', data.name );
  					$this.set('columns', data.columns );
  					
  					//console.log( $this.get('table') + '---' + oldTable );
  					if( $this.get('table') != oldTable ) {
  						$this.createGrid();
  					}
  				} 
  			}); 
  			var window = community.ui.window( renderTo, {
				width: "900px",
				minWidth : 600,
				title: "데이터",
				visible: false,
				modal: true,
				actions: [ "Close", "Maximize" ], // 
				open: function(){  
 					
				},
				close: function(){
					
				}
			});
			community.data.bind(renderTo, observable);
			renderTo.data('model', observable );
  		}  
  		
  		renderTo.data('model').setSource( getSelectedTable() );
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

        <div class="g-pa-20">
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30">${PAGE_NAME}</h1> 
          <!-- Content -->
		  <div id="features" class="container-fluid">
				<div class="row"> 
					<div id="datasource-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1 k-grid-md" ></div>

										
				</div> 
				
				<div class="row no-gutters" style="height:700px;">
					<div class="col-6">
						<div id="database-schema-table-grid" data-name="unknown" class="k-gird-auto g-brd-top-0 g-brd-left-0 g-brd-bottom-0 g-brd-right-0" ></div>
					</div>
					<div class="col-6">
						<div id="database-schema-table-column-grid" class="k-gird-auto g-brd-top-0 g-brd-bottom-0 g-brd-left-0 g-brd-right-0" ></div>
					</div>
				</div>
		  </div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
  <div id="settings-window" class="g-pa-0 g-height-600" style="display:none;" >
                  <header class="card-header g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
                    <div class="media">
                      <h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
                      <div class="media-body d-flex justify-content-end"> 
						<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" style=""></a>
						<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" style="display: none;"></a>	 
                      </div>
                    </div>
                  </header>  
                  <div class="g-pa-15">
					<div class="row no-gutters"> 
                  		<div class="col-md"> 
	                  		<label class="d-flex align-items-center justify-content-between g-mb-0">
							<span class="g-pr-20 g-font-weight-500">컨테이너에 자동 등록</span>
							<div class="u-check">
								<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" value="true" data-bind="checked: autodeploy, enabled:editable" type="checkbox">
								<div class="u-check-icon-radio-v8"><i class="fa" data-check-icon=""></i></div>
							</div>
							</label>
							<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
							서버가 시작될 때 자동으로 데이터소스를 컨테이너에 빈으로 등록합니다.
							</small> 
                  		</div>
                  		<#if !CommunityContextHelper.getConfigService().isDatabaseInitialized() >
                  		<div class="col-md"> 
                  			<label class="g-mb-10 g-font-weight-500" for="input-defaultEncoding">데이터베이스 초기화</label>	
							<p class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
								데이터베이스를 초기화합니다. 초기화는 애플리케이션이 동작하기위하여 필요한 데이블 및 기초 데이터를 생성하게 됩니다.
							</p>
							<a href="javascript:initDatabase();" class="btn btn-xl btn-outline-danger g-font-weight-600 g-letter-spacing-0_5 text-uppercase text-left rounded-0 g-mr-10 g-mb-15">
								<span class="pull-left">데이터베이스 초기화하기 <span class="d-block g-font-size-11">데이블 및 기초데이터를 생성합니다.</span></span>
								<i class="fa fa-database float-right g-font-size-32 g-ml-15"></i>
			                </a> 
                  		</div>  
                  		</#if>
                  	</div>  
                 </div> 	
  </div>  
  
  <div id="table-data-window" class="g-pa-0 g-height-600 container-fluid" style="display:none; background : #fafafa;" >
  <header class="card-header g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-weight-300 g-font-size-default--md g-color-black g-mr-10 mb-0" data-bind="text:table"></h3> 
			<div class="media-body d-flex justify-content-end"> 
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-5 g-ml-10--sm g-ml-15--xl" href="#!" data-bind="click:refresh" ><i class="hs-admin-reload g-font-size-20"></i></a>
			</div>
		</div> 
  </header> 
  <div id="table-data-grid" ></div>
  </div> 
  
  <script type="text/x-kendo-template" id="name-column-template">    
  <a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-action="view" data-name="#= name #" data-bean="#= beanName #">
	<h5 class="g-font-weight-100 g-mb-0 g-font-size-14"><i class="fa fa-database"></i> #= name #</h5> 
  </a>
  </script>
  
  <script type="text/x-kendo-template" id="database-schema-table-grid-template">
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-weight-300 g-font-size-default--md g-color-black g-mr-10 mb-0">TABLE</h3> 
			<div class="media-body d-flex justify-content-end">  									 
				<a class="k-grid-refresh d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="\\#!">
					<i class="hs-admin-reload g-font-size-18"></i>  
					<span class="g-hidden-sm-down g-ml-10"> 데이터 새로고침</span>
				</a> 
			</div>
		</div>
	</header>  
  </script> 
  
  <script type="text/x-kendo-template" id="database-schema-table2-grid-template">
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-weight-300 g-font-size-default--md g-color-black g-mr-10 mb-0">COLUMN</h3> 
			<div class="media-body d-flex justify-content-end">  									 
				<a class="view-data d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="\\#!">
					<i class="hs-admin-layout-grid-3 g-font-size-18"></i>  
					<span class="g-hidden-sm-down g-ml-10">데이터</span>
				</a> 
			</div>
		</div>
	</header>
  </script> 
    
  <script type="text/x-kendo-template" id="grid-toolbar-template">    
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
				
			</h3> 
			<div class="media-body d-flex justify-content-end">  		
				<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:deployDataSources();">
					<i class="hs-admin-shift-right g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">${PAGE_NAME} 컨테이터에 배포하기</span>
				</a> 
				<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:createDataSource();">
					<i class="hs-admin-plus g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
				</a>
				<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="\#" data-bind="click:settings">
					<i class="hs-admin-panel g-font-size-18"></i>  
					<span class="g-hidden-sm-down g-ml-10"> ${PAGE_NAME} 설정</span>
				</a>				
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:refresh();">
					<i class="hs-admin-reload g-font-size-18"></i>
				</a>				 
			</div>
		</div>
	</header> 
  </script>  
  
</body>
</html>