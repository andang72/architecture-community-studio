<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "메뉴" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	  
  <#assign MAX_UPLOAD_SIZE = "10" />
  <#assign KENDO_VERSION = "2019.3.1023" />  
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
    var __menuId = <#if RequestParameters.menuId?? >${RequestParameters.menuId}<#else>0</#if>;	
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
	        "hs.markup-copy" 				: { "deps" :['jquery', 'hs.core', 'clipboard'] },
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
			"kendo.web.min"	 				: "<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.all.min"/>", 
			"kendo.messages"				: "<@spring.url "/js/kendo/custom/kendo.messages.ko-KR"/>",
			"kendo.culture.min"				: "<@spring.url "/js/kendo/${KENDO_VERSION}/cultures/kendo.culture.ko-KR.min"/>",
			"jszip"							: "<@spring.url "/js/kendo/${KENDO_VERSION}/jszip.min"/>",
			<!-- community ui -->
			"community.ui.data"				: "<@spring.url "/js/community.ui/community.ui.data"/>",
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>", 
			"dropzone"						: "<@spring.url "/js/dropzone/dropzone"/>",
			"clipboard"						: "<@spring.url "/assets/unify/2.6.2/vendor/clipboard/dist/clipboard.min"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup","studio.custom", "jszip" ], 
  		function($, Clipboard) {  
  	  console.log("START SETUP APPLICATION.");	 
  	  community.ui.studio.setup();	 
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		menu : new community.data.model.Menu(),
		visible : false,
		editable : true,
		isNew : true, 
		linked : false,
		linkedUrl : null,
		imageUrl : '<@spring.url "/images/no-image.jpg"/>',
		editor : { warp : false },
		objectTypes : [
			{ text: "정의되지 않음", value: "-1" },
			{ text: "앨범", value: "40" },
			<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
			{ text:'${item.NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
			</#list>
			
		],
		back: function(){
			window.history.back();
		},
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
				$this.load($this.image.imageId); 
			}
		},
		refresh: function(){
			var $this = this;
			community.ui.treelist($('#items-treelist')).dataSource.read(); 
		},
		load: function(objectId){
			var $this = this;
			if( objectId > 0 ){
				community.ui.progress($('#features'), true);	
				community.ui.ajax('<@spring.url "/data/secure/mgmt/menus/"/>' + objectId + '/get.json', {
					contentType : "application/json",
					success: function(data){	
						$this.setSource( new community.data.model.Menu(data) ); 
						createItemsTreeList( $this );
					}	
				}).always( function () {
					community.ui.progress($('#features'), false);
				});	
			}else{
				$this.setSource(new community.data.model.Menu()); 
			}	
		},	
		property : function(e){
			var $this = this;
			createPropertyWindowIfNotExist(community.data.Models.Menu, $this.menu.menuId );
		},
		permissions : function(e){
			var $this = this;
			createPermissionsWindowIfNotExist(community.data.Models.Menu, $this.menu.menuId); 
		},
		setSource : function( data ){
			var $this = this ;	  
			if( data.get('menuId') > 0 ){
				data.copy( $this.menu );
				$this.set('editable', false );
				$this.set('isNew', false );
			}else{
				$this.set('editable', true );	
				$this.set('isNew', true );
			}
			if( !$this.get('visible') ) {
				$this.set('visible' , true );
			}
		}
	  });	 
	  community.data.bind( $('#features') , observable );   
	  observable.load(__menuId);  
	  //createImageDropzone(observable);
  	  console.log("END SETUP APPLICATION.");	
  	});	

	 
	function createItemsTreeList(observable){
		var renderTo = $('#items-treelist');	
		if( !community.ui.exists(renderTo)){
			var objectId = observable.get('menu.menuId');
			var treelist = community.ui.treelist(renderTo, {
                    dataSource: {
                        transport: {
                            read: {
                            	   	contentType: "application/json; charset=utf-8",
                            		url: '<@spring.url "/data/secure/mgmt/menus/"/>' + objectId + '/items/list.json?widget=treelist' ,
                                	type :'POST',
                                	dataType: 'json'                                
                             },
                             update: {
                                 url: '<@spring.url "/data/secure/mgmt/menus/"/>' + objectId + '/items/save-or-update.json' ,
                                 type :'POST',  
                                 contentType : "application/json", 
                             	dataType: 'json'    
                             },
                             destroy: {
                                 url: '<@spring.url "/data/secure/mgmt/menus/"/>' + objectId + '/items/delete.json' ,
                            		type :'POST',
                            		contentType : "application/json",
                            		dataType: 'json'    
                             },
                             create: {
                                 url: '<@spring.url "/data/secure/mgmt/menus/"/>' + objectId + '/items/save-or-update.json' ,
                             	type :'POST',
                             	contentType : "application/json",
                             	dataType: 'json'    
                             },
							parameterMap: function (options, operation){	 
								if (operation !== "read" && options.models) {
                                     return {models: kendo.stringify(options.models)};
                                 }else{
									return community.ui.stringify(options);
								}
							}
                        },
                        schema: {
							data:  "items",
							model: {
								id: "menuItemId",
								parentId: "parentMenuItemId",
								fields: { 		
									menuItemId: { type: "number", defaultValue: 0 },		
									menuId: { type: "number", defaultValue: 0 },	
									parentMenuItemId: { type: "number", defaultValue: null },	
									name: { type: "string", defaultValue: null },	
									roles: { type: "string", defaultValue: null },	
									sortOrder: { type: "number", defaultValue: 1 },	
									location: { type: "string", defaultValue: null },	
									description: { type: "string", defaultValue: null },
									creationDate:{ type: "date" },			
									modifiedDate:{ type: "date"}
								},	
								expanded: true
							} 
                        }
                    },    
                    pagable : false,               
                    selectable: "row", 
                    filterable: false,
                    sortable: true, 
                    toolbar: [{ name: "create" , text: "${PAGE_NAME} 추가하기" }, { name: "excel" , text: "엑셀 다운로드" } ],
                    /*
                    excel: {
                        fileName: "MenuItemExport.xlsx",
                        proxyURL: "<@spring.url "/download/proxy"/>sss",
                        filterable: true
                    },*/
                    excel: {
                        fileName: "Kendo UI TreeList Export.xlsx",
                        proxyURL: "https://demos.telerik.com/kendo-ui/service/export",
                        filterable: true
                    },
                    change : function() {
						var selectedRows = this.dataItem(this.select()); 
						createMenuItemPropertiesGrid(selectedRows);
					}, 
					save: function(){
						treelist.dataSource.read();
					},
                    editable: {
                    		mode: "inline",
                    		move: true
                    },
                    columns: [
                        { field: "name", expandable: true, title: "메뉴", width: 200 },
                        { field: "description" , title: "설명", sortable: false, width: 250 },
                        { field: "sortOrder" , title: "정렬",  width: 100, attributes: { style: "text-align: center;"  }},
                        { field: "page" , title: "페이지" , sortable : false},
                        { field: "location" , title: "링크" , sortable : false},
                        { field: "roles", title: "권한" , width: 150, sortable : false},
                        { title: " ", command: [ "edit", "destroy" ], width: 200 }
                    ],
                    save: function(e){
				    	treelist.dataSource.read();
				    }
             });
             community.ui.treelist(renderTo).bind(
				"dragend", function(e){
					this.dataSource.sync();
				}
			);  
		}
	}	


	function createMenuItemPropertiesGrid( data ){
		var renderTo = $('#item-grid');  
		if( renderTo.data('object-id') != data.get('menuItemId') ){  
			console.log("create grid with " + data.get('menuItemId') );
			if( community.ui.exists( renderTo ) ){
				console.log( "destroy grid...." );
				community.ui.grid(renderTo).destroy();
				console.log( renderTo.data('kendoGrid') );
			} 
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : 	{url:'<@spring.url "/data/secure/mgmt/menus/0/items/"/>' + data.menuItemId + '/properties/list.json', type:'post', contentType: "application/json; charset=utf-8"},
						create : { url:'<@spring.url "/data/secure/mgmt/menus/0/items/"/>' + data.menuItemId + '/properties/update.json', type:'post', contentType: "application/json; charset=utf-8" },
						update : { url:'<@spring.url "/data/secure/mgmt/menus/0/items/"/>' + data.menuItemId + '/properties/update.json', type:'post', contentType: "application/json; charset=utf-8" },
						destroy : { url:'<@spring.url "/data/secure/mgmt/menus/0/items/"/>' + data.menuItemId + '/properties/delete.json', type:'post', contentType: "application/json; charset=utf-8" },       
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
				sortable: true,
				filterable: false,
				pageable: false, 
				editable: true,
				toolbar: ["create", "save", "cancel"],
				columns: [
					{ field: "name", title: "이름", width: 250 , validation: { required: true} },  
					{ field: "value", title: "값" , validation: { required: true} },
					{ command: ["destroy"], title: "&nbsp;", width: 150}
				],
				save : function(){
				}
			});			
			renderTo.data( "object-id" , data.menuItemId );	
		} 
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
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30" data-bind="text:page.name">${PAGE_NAME}</h1> 
          <!-- Content -->
          <div class="container-fluid"> 
			<div class="row"> 
				<div class="col-lg-10 g-mb-10"> 
				<div class="card g-brd-gray-light-v7 g-brd-0 g-rounded-3 g-mb-30 g-min-height-500" data-bind="visible:visible" style="display:none;" >
                  	<header class="card-header g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
                    <div class="media"> 
                      <a class="hs-admin-angle-left u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover" href="#" data-bind="click:back"></a>
                      <div class="media-body d-flex justify-content-end"> 
                      	<a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:isNew, click:refresh"></a>
                      </div>
                    </div>
                  	</header> 
					<div class="row no-gutters g-pa-15"  >
                        <div class="col-md order-md-2 ml-md-auto text-md-right g-font-weight-300 g-color-gray-dark-v11 g-mb-10">
                          <div class="media align-items-start">
                            <div class="media-body" ><span data-bind="text:menu.modifiedDate" data-format="yyyy.MM.dd HH:mm" ></div>
                          </div>
                        </div>
                        <div class="col-md order-md-1 g-mr-20 g-mb-10">
                          <h3 class="g-font-weight-400 g-font-size-16 g-color-black mb-0" data-bind="text:menu.name"></h3>
                          <em class="d-block g-font-style-normal g-color-gray-dark-v12"><span data-bind="text:menu.description"></span></em>
                        </div>
                    </div> 
					<div class="row no-gutters">
						<div id="items-treelist" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1" ></div> 
						
						<div id="item-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1"></div>
					</div>
				</div> 
				</div> 
				<!-- side menu -->
				<div class="g-brd-left--lg g-brd-gray-light-v4 col-lg-2 g-mb-10 g-mb-0--md"> 
					<section data-bind="invisible:isNew" style="display:none;">  
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
</body>
</html>
</#compress>