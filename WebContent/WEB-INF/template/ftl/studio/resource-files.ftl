<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "파일" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	  
  <title>STUDIO :: ${PAGE_NAME} </title>
  <!-- Required Meta Tags Always Come First -->
  
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">
  
  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>"> 
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
	        "kendo.messages" 				: { "deps" :['kendo.web.min'] },
	        <!-- community ui -->
	        "community.ui.data" 			: { "deps" :['jquery', 'kendo.web.min'] },
	        "community.ui.core" 			: { "deps" :['jquery', 'kendo.web.min', 'community.ui.data'] },
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min', 'community.ui.data' , 'kendo.messages', 'community.ui.core' ] }
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
			"kendo.culture.min"				: "<@spring.url "/js/kendo/2019.2.619/cultures/kendo.culture.ko-KR.min"/>",
			"kendo.messages"				: "<@spring.url "/js/kendo/custom/kendo.messages.ko-KR"/>",
			"jszip"							: "<@spring.url "/js/kendo/2019.2.619/jszip.min"/>",
			<!-- community ui -->
			"community.ui.data"				: "<@spring.url "/js/community.ui/community.ui.data"/>",
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  community.ui.studio.setup();	
  	  var observable = new community.data.observable({ });
  	   
  	  createAttachmentsGrid(observable);
  	   
  	  community.data.bind( $('#features') , observable );
  	  console.log("END SETUP APPLICATION.");	
  	});
  	
  	
    function createAttachmentsGrid(observable){
    	var renderTo = $('#pages-grid');
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/attachments/list.json?fields=link"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					pageSize: 50,
					serverPaging : true,
					serverFiltering:true,
					serverSorting: true,
					error : community.ui.error,
					batch: true, 
					schema: {
						data:  "items",
						total: "totalCount",
						model: community.data.model.Attachment
					}
				}, 
				toolbar: [{ name: "create" , text: "새로운 ${PAGE_NAME} 업로드하기", template:community.ui.template( $('#grid-toolbar-template').html() )  }],
				sortable: true,
				filterable: {
					extra: false,
                    operators: {
                    	string: {
                    		startswith: "시작",
                            eq: "같음",
                            contains: "포함"
                        }
                	}
				},
				pageable: {
					refresh: true,
					pageSizes: [50, 100, 200, 300]
                },
				columns: [
				{ field: "ATTACHMENT_ID", title: "ID", filterable: false, sortable: true , width : 80 , template:'#= attachmentId #', attributes:{ class:"text-center" }}, 
				{ field: "OBJECT_TYPE", title: "OBJECT TYPE", filterable: true, sortable: true , width : 120 , template:'#= objectType #', attributes:{ class:"text-center" }}, 
				{ field: "OBJECT_ID", title: "OBJECT ID", filterable: true, sortable: true , width : 120 , template:'#= objectId #', attributes:{ class:"text-center" }}, 
				{ field: "NAME", title: "${PAGE_NAME}", filterable: true, sortable: true, template:$('#name-column-template').html() },  
				{ field: "FILE_SIZE", title:"크기", filterable: false, sortable: true, template:'#: community.data.format.bytesToSize(size)  #' , attributes:{ class:"text-center" } , width:120 },  
				{ field: "CONTENT_TYPE", title: "콘텐츠 타입", filterable: false, sortable: false, width:120, template: '#= contentType #', attributes:{ class:"text-center" } }, 
				{ field: "USER_ID", title: "작성자", filterable: false, sortable: true, width:150, template: $('#user-column-template').html(), attributes:{ class:"text-center" } },
				{ field: "CREATION_DATE", title: "생성일", filterable: false, sortable: true , width : 100 , template :'#: community.data.format.date( creationDate ,"yyyy.MM.dd")#' ,attributes:{ class:"text-center" } } , 
				{ field: "MODIFIED_DATE", title: "수정일", filterable: false, sortable: true , width : 100 , template :'#: community.data.format.date( modifiedDate ,"yyyy.MM.dd")#' ,attributes:{ class:"text-center" } }
				]			
			});	

			$('#features').on( "click", "a[data-action=edit]", function(e){		
				var $this = $(this);	
				if( community.ui.defined($this.data("object-id")) ){
					var objectId = $this.data("object-id");	
  					community.ui.send("<@spring.url "/secure/studio/resource-files-editor" />", { attachmentId: objectId });
  				}		
			});															
		}			
    } 
  	
  	function refresh(){
  		var renderTo = $('#pages-grid');
  		community.ui.grid(renderTo).dataSource.read();
  		console.log('grid refresh...');  	
  	}
  	
  	function edit (e){
  		var $this = $(this);
  		if( community.ui.defined($this.data("object-id")) ){
  		}
  	}			

	  										  										
  </script>
  <style>
  .k-grid-content { min-height:700px; }
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
				<div class="row"> 
					<div id="pages-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1"></div>
				</div>
			</div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>

	<script type="text/x-kendo-template" id="name-column-template">    
	<div class="media">
		<div class="d-flex">
		<!-- Figure Image -->
			<div class="g-width-100 g-width-100--md g-width-100 g-height-100--md g-brd-2 g-brd-transparent g-brd-lightblue-v3--parent-opened g-mr-20--sm">
				<img class="g-width-100 g-width-100--md g-width-100 g-height-100--md g-brd-2 g-brd-transparent g-brd-lightblue-v3--parent-opened g-mr-20--sm" src="#= community.ui.studio.getFileUrl (data, {thumbnail:true}) #"  
					alt="#= name #">
			</div>
		<!-- Figure Image -->
		</div>
		<div class="media-body">
			<!-- Figure Info -->
			<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-action="edit" data-object-id="#=attachmentId#">
				<h5 class="g-font-weight-100 g-mb-0">
				#if ( sharedLink!=null && sharedLink.publicShared ) { # <i class="hs-admin-unlock"></i>  # } else {# <i class="hs-admin-lock"></i> #}#
			   #= name #
				</h5> 
			</a> 
			#if ( sharedLink!=null ) { #
			<p class="g-font-weight-300 g-color-gray-dark-v6 g-mt-5 g-ml-10 g-mb-0" >
			#: sharedLink.linkId #
			</p>
			#}#
			</div>
			<!-- End Figure Info -->
		</div>
	</div>
	</script>
	
	<script type="text/x-kendo-template" id="user-column-template">    
	<div class="media">
    	<div class="d-flex align-self-center">
    		<img class="g-width-36 g-height-36 rounded-circle g-mr-15" src="#= community.data.url.userPhoto( '<@spring.url "/"/>' , user ) #" >
		</div>
		<div class="media-body align-self-center text-left">#if ( !user.anonymous  && user.name != null ) {# #: user.name # #}#</div>
	</div>	
	</script>
	
  <script type="text/x-kendo-template" id="grid-toolbar-template">    
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
				<a class="u-link-v5 g-font-size-16 g-font-size-18--md g-color-gray-light-v6 g-color-secondary--hover k-grid-refresh" href="javascript:refresh();"><i class="hs-admin-reload"></i></a>
			</h3> 
			<div class="media-body d-flex justify-content-end">
				
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:void(this);"data-action="edit" data-object-id="0" >
					<i class="hs-admin-plus g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 업로드하기</span>
				</a>
			</div>
		</div>
	</header> 
  </script>	  
</body>
</html>