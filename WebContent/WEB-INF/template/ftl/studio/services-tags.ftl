<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "태그" />	
  <#assign PARENT_PAGE_NAME = "설정" />	  
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
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min', 'kendo.messages','community.ui.data' , 'community.ui.core' ] }
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
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  community.ui.studio.setup();	
  	  var observable = new community.data.observable({ 
  	  	visible : true, 
  	  	editable : false,
  	  	selectedTag : null,
 		edit: function(){
			var $this = this;
			$this.set('editable', true); 
		},
		cancle: function(){
			var $this = this;
			$this.set('editable', false);
		}	  	
  	  }); 
  	  createTagGrid(observable); 
  	  community.data.bind( $('#features') , observable );
  	  console.log("END SETUP APPLICATION.");	
  	});
  	
 
	function nonEditor(container, options) {
	    container.text(options.model[options.field]);
	} 
    
    var objectTypes = [
		{ NAME: "정의되지 않음", OBJECT_TYPE: "-1" },
		{ NAME: "앨범", OBJECT_TYPE: "40" },
		<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
		<#if item.DISPLAY_NAME??>
		{ NAME:'${item.DISPLAY_NAME}', OBJECT_TYPE:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
		</#if>
		</#list>	
	];
		    
   	function getTagName( code ){
   		var renderTo = $('#tags-grid'); 
   		var item = community.ui.grid(renderTo).dataSource.get(code);
   		if( item != null )
            return item.name;
        else
            return '' ;
   	}
    
    function getObjectTypeName( code ) { 
    	var name = code ;
    	$.each( objectTypes , function( index, value ) { 
    		console.log( value.OBJECT_TYPE );
    		if( parseInt(value.OBJECT_TYPE, 10) === code ) {
    			name = value.NAME;  
    			return false; // breaks
    		}
    	}); 
       return name;
    }
 
    
    function objectTypeDropDownEditor(container, options) {
        $('<input  name="' + options.field + '"/>')
            .appendTo(container)
            .kendoDropDownList({
            autoBind: true,
            valuePrimitive: true,
            dataTextField: "NAME",
            dataValueField: "OBJECT_TYPE",
            dataSource: objectTypes
        });
    }
      	  	
  	function refresh(){
  		var renderTo = $('#tags-grid');
  		community.ui.grid(renderTo).dataSource.read();
  		console.log('grid refresh...');
  	}  	
  	
    function createTagGrid(observable){
    	var renderTo = $('#tags-grid');
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/services/tags/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						create : { url:'<@spring.url "/data/secure/mgmt/services/tags/0/create.json"/>',  type:'post', contentType: "application/json; charset=utf-8"}, 
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					error : community.ui.error,
					serverFiltering: false,
					serverSorting: false, 
					serverPaging: false, 
					schema: {
						total: "totalCount",
						data:  "items",
						model: {
							id : "tagId",
							fields :{
								tagId: { type: "number", defaultValue: 0 },
								name: {  type: "string", defaultValue: null },
								creationDate: {  type: "date", editable:false, filterable:false }
							}
						}
					}
				},
				toolbar: [{ name: "create" , text: "새로운 ${PAGE_NAME} 만들기", template:community.ui.template($('#grid-toolbar-template').html())  }],
				height : 600,
				sortable: true,
				filterable: true,
				selectable : "row",
				pageable: false, 
				editable: "inline", 
				columns: [
					{ field: "tagId", title: "ID", width: 100 , editor: nonEditor },  
					{ field: "name", title: "태그" , template : $('#name-column-template').html() },
					{ field: "creationDate", title: "생성일", width: 150 , filterable:false, format :"{0: yyyy.MM.dd HH:mm:ss}" },
					{ command: ["edit", "destroy"], title: "&nbsp;", width: "250px"}
				],
				change: function(e) {
				    var selectedRows = this.select();
				    var selectedDataItems = [];
				    for (var i = 0; i < selectedRows.length; i++) {
				      var dataItem = this.dataItem(selectedRows[i]); 
				      observable.set('selectedTag', dataItem);
				      grid2.dataSource.read({ tagId : dataItem.tagId });
				      observable.set('editable' , true );
				    }
 				}
			});		
			
			var grid2 = createTagObjectGrid(observable);		
			renderTo.on("click", "a[data-kind=tag]", function(e){ 
				var $this = $(this);
				var objectId = $this.data("object-id");	 
				return false;		
			});	 											
		}			
    }	 
    
	function createTagObjectGrid(observable){
		var renderTo = $('#tag-object-grid');  
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				autoBind : false,
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/services/tags/0/objects/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						create : { url:'<@spring.url "/data/secure/mgmt/services/tags/0/objects/create.json"/>',  type:'post', contentType: "application/json; charset=utf-8"}, 
						destroy : { url:'<@spring.url "/data/secure/mgmt/services/tags/0/objects/delete.json"/>',  type:'post', contentType: "application/json; charset=utf-8"}, 
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							} 
							if( options.tagId === 0 ){
								options.tagId = observable.get('selectedTag').tagId;
							}
							return community.ui.stringify(options);
						}
					},  
					serverFiltering: false,
					serverSorting: false, 
					serverPaging: false, 
					schema: {
						total: "totalCount",
						data:  "items",
						model: {
							id : "key",
							fields :{
								key: { type: "string", defaultValue: null },
								tagId: { type: "number", defaultValue: 0 },
								objectType: { type: "number", defaultValue: 0 },
								objectId: { type: "number", defaultValue: 0 }
							}
						}
					}
				},
				height : 600,
				sortable: true,
				filterable: true,
				pageable: false, 
				editable: "inline", 
				toolbar: ["create"],
				columns: [
					{ field: "tagId", title: "태그", width:300 , editor: nonEditor, template: "#: getTagName(tagId) #" },  
					{ field: "objectType", title: "객체 유형" , validation: { required: true} , editor: objectTypeDropDownEditor, template:"#= getObjectTypeName(objectType) #" },
					{ field: "objectId", title: "객체 ID" , validation: { required: true} },
					{ command: ["edit", "destroy"], title: "&nbsp;", width: "250px"}
				]
			});	  											
		}
		return community.ui.grid(renderTo);		
	}    
  </script>
  <style>
 	#tags-grid .k-command-cell a.k-grid-edit {
		display:none;
	} 
	
	#tag-object-grid .k-dropdown > .k-dropdown-wrap {
		height:2em;
	}
  </style>
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
			<div class="card collapse" id="collapse-help">  
					<header class="card-header g-bg-transparent g-brd-bottom-none g-px-15 g-px-30--sm g-pt-15 g-pt-25--sm pb-0">
						<div class="media">
							<h3 class="d-flex align-self-center g-font-size-20 g-font-size-18--md g-color-primary g-mr-10 mb-0">Working with ${PAGE_NAME}</h3> 
							<div class="media-body d-flex justify-content-end">
								<div class="align-self-center g-pos-rel g-z-index-2">
									<a class="u-link-v5 g-font-size-20 g-font-size-20--md g-color-gray-light-v6 g-color-secondary--hover" data-toggle="collapse" href="#collapse-help" role="button" aria-expanded="true" aria-controls="collapse-help" ><i class="hs-admin-close"></i></a>                      
								</div>
							</div>
						</div>
					</header> 
					<div class="card-body"></div>
				</div> 
				<div class="row"> 
					<div id="tags-grid" class="g-brd-top-0 g-brd-left-0  g-brd-right-0 g-mb-1"></div>
				</div>  
				<div class="row" data-bind="visible:editable" style="display:none;">  
					<div id="tag-object-grid" class="g-mb-1 g-mt-5"></div>
				</div>				
			</div>
            <!-- End Content -->
 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
	<script type="text/x-kendo-template" id="name-column-template">    
		<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-kind="tag" data-action="edit" data-object-id="#=tagId#">
		<h5 class="g-font-weight-100 g-mb-0 g-font-size-14">
		#= name #
		</h5> 
		</a>
		<!--<p class="g-font-weight-300 g-color-gray-dark-v6 g-mt-5 g-ml-10 g-mb-0" >...</p>-->
	</script> 
  <script type="text/x-kendo-template" id="grid-toolbar-template">    
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
			<div class="media-body d-flex justify-content-end">  
				<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="\#">
					<i class="hs-admin-plus g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
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