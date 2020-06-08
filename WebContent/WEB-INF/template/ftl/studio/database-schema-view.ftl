<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <#assign PAGE_NAME = "데이터소스" />	
  <#assign PARENT_PAGE_NAME = "설정" />	
  <#assign KENDO_VERSION = "2019.3.917" />	  
  <meta charset="utf-8">
  <!-- Title -->
  <title>STUDIO :: ${PAGE_NAME} </title>
  <meta name="description" content="Database Schema View">
  <meta name="author" content="Island"> 
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.common.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.mobile.nova.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/bootstrap/4.3.1/bootstrap.min.css"/>">  
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/css/unify-admin.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/hs-admin-icons/hs-admin-icons.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-awesome/css/font-awesome.min.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>">  
  <!-- CSS Customization -->
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/community.ui.studio/custom.css"/>">  
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/jquery.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.all.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/cultures/kendo.culture.ko-KR.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/custom/kendo.messages.ko-KR.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.data.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.core.js"/>"></script> 
  <script src="<@spring.url "/js/ace/ace.js"/>"></script>   
  <script> 
	var __name = <#if RequestParameters.name?? >'${RequestParameters.name}'<#else>null</#if>;  
	
	$(document).ready(function() {  
		community.ui.setup();  
		var observable = new community.data.observable({
			database : {},
			load: function(){
				var $this = this;
				community.ui.progress($('#features'), true);	 
				community.ui.ajax('<@spring.url "/data/secure/mgmt/datasource/"/>' + __name + '/get.json', {
					contentType : "application/json",
					success: function(data){
						
						$this.set('database', data );
						createDatabaseSchemaTableGrid({name : __name });
					}	
				}).always( function () {
					community.ui.progress($('#features'), false);
				});
			}
		});  
		observable.load();
		community.data.bind( $('#features') , observable );
		
	}); 
	
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
					{ field: "typeName", title: "TYPE NAME", width: 150 }
				]
			});	 
			renderTo.find(".k-grid-toolbar").on("click", ".view-data", function (e) {
			if( getSelectedTable() == null )
				community.ui.notify( "No table is selected for data query.", "error");
			else
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
		if( dataItem != null )
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
						scrollable : false,
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
  
	<style></style>
</head>
<body class=""> 
	<div id="features" class="container">  
		<section>
			<header class="g-mb-20 g-mt-20">
              <div class="u-heading-v6-2  text-uppercase">
                <h2 class="h4 u-heading-v6__title g-font-weight-300">SCHEMA VIEW</h2>
              </div>
              <div class="g-line-height-2 g-pl-90">
                <p class="g-mb-0 g-font-size-12"><span data-bind="text:database.databaseProductName"></span> <span data-bind="text:database.databaseProductVersion"></span></p>
                <p class="g-mb-0 g-font-size-12"><span data-bind="text:database.driverName"></span> <span data-bind="text:database.driverVersion"></span></p>
                 <p class="g-mb-0 g-font-size-12"><span data-bind="text:database.schema"></span></p>
              </div>
            </header>  
		</section>	
	</div>
	<div class="container-fluid"> 
		<div class="row no-gutters" style="height:700px;">
			<div class="col-6">
				<div id="database-schema-table-grid" data-name="unknown" class="k-gird-auto g-brd-top-0 g-brd-left-0 g-brd-bottom-0 g-brd-right-0" ></div>
			</div>	
			<div class="col-6">
				<div id="database-schema-table-column-grid" class="k-gird-auto g-brd-top-0 g-brd-bottom-0 g-brd-left-0 g-brd-right-0" ></div>
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
	<script type="text/x-kendo-template" id="database-schema-table-grid-template">
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-weight-300 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
			<div class="media-body d-flex justify-content-end">  									 
				<a class="k-grid-refresh d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="\\#!">
					<i class="hs-admin-reload g-font-size-18"></i>  
					<span class="g-hidden-sm-down g-ml-10"> 새로고침</span>
				</a> 
			</div>
		</div>
	</header>  
	</script> 
	<script type="text/x-kendo-template" id="database-schema-table2-grid-template">
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-weight-300 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
			<div class="media-body d-flex justify-content-end">  									 
				<a class="view-data d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="\\#!">
					<i class="hs-admin-layout-grid-3 g-font-size-18"></i>  
					<span class="g-hidden-sm-down g-ml-10">데이터</span>
				</a> 
			</div>
		</div>
	</header>	
	</script> 	
</body>
</html>