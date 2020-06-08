<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "프로퍼터" />	
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
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min', 'kendo.messages',  'community.ui.data' , 'community.ui.core' ] }
		},
		paths : {
			"jquery"    					: "<@spring.url "/js/jquery/jquery-3.4.1.min"/>",
			"jquery.cookie"    				: "<@spring.url "/js/jquery.cookie/1.4.1/jquery.cookie"/>",
			"jquery.fancybox" 				: "<@spring.url "/js/jquery.fancybox/jquery.fancybox.min"/>",
			"jquery.scrollbar" 				: "<@spring.url "/assets/unify.admin/2.6.2/vendor/malihu-scrollbar/jquery.mCustomScrollbar.concat.min"/>",
			"bootstrap" 					: "<@spring.url "/js/bootstrap/4.3.1/bootstrap.bundle.min"/>",
			<!-- unify -->
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
	
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", 
  	  "studio.custom" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	 
  	  community.ui.studio.setup({
			features : {
				accounts: true
		  	},
		  	'features.accounts.authenticate':function(e){
		  	} 	  
  	  });
  	  console.log("END SETUP APPLICATION.");	
	  createPropertiesGrid();
  	});	
  	
  	function refresh(){
  		var renderTo = $('#properties-grid');
  		community.ui.grid(renderTo).dataSource.read();
  		console.log('grid refresh...');
  	}  
  		
	function createPropertiesGrid(observable){ 
		var renderTo = $('#properties-grid');  
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/properties/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						create : { url:'<@spring.url "/data/secure/mgmt/properties/update.json"/>', type:'post', contentType: "application/json; charset=utf-8" },
						update : { url:'<@spring.url "/data/secure/mgmt/properties/update.json"/>', type:'post', contentType: "application/json; charset=utf-8" },
						destroy : { url:'<@spring.url "/data/secure/mgmt/properties/delete.json"/>', type:'post', contentType: "application/json; charset=utf-8" },       
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
						model: community.data.model.Property
					}
				},
				toolbar: [{ name: "create" , text: "새로운 ${PAGE_NAME} 만들기", template:community.ui.template( $('#grid-toolbar-template').html() )  }],
 				sortable: true,
				filterable: false,
				editable: "inline",
				pageable: false,
				columns: [
					{ field: "name", title: "이름", width: 300 , validation: { required: true} },  
					{ field: "value", title: "값" , validation: { required: true} },
					{ command: ["edit", "destroy"], title: "&nbsp;", width: "250px"}
				]
			});						
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
          <!--<hr class="d-flex g-brd-gray-light-v7 g-my-30">-->
          <!-- Content -->
		<div id="features" class="container-fluid">
			<div class="row"> 
				<div id="properties-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1"></div>
			</div>
		</div>
          
          <!--
          <div class="container-fluid"> 	
			<div class="row"> 
				<div class="card g-brd-gray-light-v7 g-rounded-3 g-mb-30" style="width:100%; min-height:500px;">
                  <header class="card-header g-brd-gray-light-v7 g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
                    <div class="media">
                      <h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
                      <div class="media-body d-flex justify-content-end"> 
                        <a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#"></a>
                      </div>
                    </div>
                  </header> 
                  <div class="card-block g-pa-0 g-pa-0--sm">
                    <div id="properties-grid" class="g-brd-0 g-mb-1"></div>
                  </div>
                </div> 
          </div> 
         </div>
         -->
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
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