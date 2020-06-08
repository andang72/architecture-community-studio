<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head> 
  <!-- Title -->
  <#assign PAGE_NAME = "정보" />
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
	        "community.ui.data" 			: { "deps" :['jquery', 'kendo.all.min'] },
	        "community.ui.core" 			: { "deps" :['jquery', 'kendo.all.min', 'community.ui.data'] },
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.all.min', 'community.ui.data' , 'community.ui.core' ] }
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
			"kendo.all.min"	 				: "<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.all.min"/>",
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
  	  
  	  // fix.. for side menu..
  	  localStorage.setItem('CLICKED_MANU_ITEM', 'SIDEBAR_01');			
  	  
  	  community.ui.studio.setup();	
  	  
  	  var observable = new community.data.observable({
  	  	overview :{},
  	  	refreshPlatformInfo : function(e){
  	  		var $this = this;
  	  		createPlatformInfo($this);
  	  	}
  	  });
  	  
  	  observable.refreshPlatformInfo(); 
  	  createNavTabs(observable);
  	  community.data.bind( $('#features'), observable); 
  	  console.log("END SETUP APPLICATION.");	
  	});	
  	
  	/**
  	* Init Nav Tabs :
  	*/
	function createNavTabs (observable){
		$('#nav-tab').on( 'shown.bs.tab', function (e) {		
			var target = $(e.target);
			switch( target.attr('href') ){
				case "#nav-libs" :						
					createLibsGrid(observable);
				break;
				case "#nav-usage" :						
					createDiskUsage(observable);
					createMemoryUsage(observable);
				break;
			}					
		});
		//$('#nav-tab a:last').tab('show'); 
	}			

	/**
	 * Create One If doesn't exist.
	 */
	function createLibsGrid(observable){
		var renderTo = $('#libs-grid');
		if(! community.ui.exists(renderTo) ){
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read: { url:'<@spring.url "/data/secure/mgmt/status/library/list.json"/>', type:'post' }
					},						
					batch: false, 
					schema: {
						model: {
							fields: {
								name: {type: "string"},
								group: { type: "string", defaultValue: "N/A" },
								artifact:{ type:"string", defaultValue: "N/A"},
								version: {type: "string", defaultValue: "N/A"},
								timestamp: {type: "date"},
								lastModified : { type:"date"}
							}										
						}
					},
					sort: { field: "name", dir: "asc" }
				},
				columns: [
					{ title: "Name", field: "name"},
					{ title: "Group", field: "group" },
					{ title: "Artifact", field: "artifact" },
					{ title: "Version", field: "version", filterable: false, width: 200 },
					{ title: "Timestamp", field: "timestamp" , format: "{0:yyyy.MM.dd hh:mm}", filterable: false, width: 200  },
					{ title: "Last Modified", field: "lastModified", format: "{0:yyyy.MM.dd hh:mm}" , filterable: false , width: 200 },
				],
				pageable: false,
				filterable: true,
				editable : false,
				sortable: true, 
				change: function(e) { }
			});
		}	
								
	}
	
	function createPlatformInfo(observable){
		community.ui.progress($('#features'), true );
		community.ui.ajax('<@spring.url "/data/secure/mgmt/status/platform/info.json"/>', {
			success: function(data){
				observable.set('overview', data );
			},
			complete : function (dataOrjqXHR, textStatus){
				community.ui.progress($('#features'), false );
			}
		});	
			
	}
	function createMemoryUsage (observable){
	
		var renderTo =  $('#memory-usage-chart');
		if(! community.ui.exists(renderTo) ){
            renderTo.kendoChart({
                title: {
                    text: "Java Memory Usage"
                },
                legend: {
                   position: "top"
                },
                seriesDefaults: {
                    labels: {
                        template: "#= category # - #= kendo.format('{0:P}', percentage)#",
                        position: "outsideEnd",
                        visible: true,
                        background: "transparent"
                    }
                },
                series : [{
					type: "pie", 
					field: "percentage",
					categoryField: "source"
				}],
                tooltip: {
                    visible: true,
                    template: "#= category # - #= kendo.format('{0:P}', percentage) # #: community.data.format.bytesToSize(value) #"
                }
            });		
		} 		
	
		community.ui.ajax('<@spring.url "/data/secure/mgmt/status/memory/usage.json"/>', {
			success: function(data){
				var items = [];	
				items.push({ percentage: data.maxHeap.bytes , source: "maxHeap", explode: false });
				items.push({ percentage: data.usedHeap.bytes , source: "usedHeap", explode: false });		
				renderTo.data("kendoChart").setDataSource( new kendo.data.DataSource({ data: items }) );	 			
			}
		});	
		
	}
	
	function createDiskUsage(observable){
		var renderTo = $('#disk-usage-grid');
		var renderTo2 = $('#disk-usage-chart');
		if(! community.ui.exists(renderTo) ){
			var grid = community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read: { url:'<@spring.url "/data/secure/mgmt/status/disk/usage.json"/>', type:'post' }
					},						
					batch: false
				},
				columns: [
				{ title: "Path", field: "absolutePath"},
				{ title: "USED", field: "usableSpace" , format: "{0:##,#}" },
				{ title: "AVAILABLE", field: "freeSpace" , format: "{0:##,#}" },
				{ title: "TOTAL", field: "totalSpace" , format: "{0:##,#}" }
				],
				toolbar: kendo.template('<div class="g-pa-15 text-right"><a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20 k-grid-refresh" href="\\#"></a></div>'),
				selectable : "row",
				pageable: false,
				resizable: true,
				editable : false,
				scrollable: false,
				height: 300	,	
				dataBound: function(e) {                          
					e.sender.select("tr:eq(1)");
				},
				change : function(e){
						 
					if(! community.ui.exists(renderTo2) ){
						console.log("create Chart.");
						renderTo2.kendoChart({
							title : { position: "top", text: "Disk Usage" },
							legend: { visible: false },
							seriesDefaults: {
								labels: {
									template: "#= category # - #= community.data.format.bytesToSize(value) #", 
									visible: true,
									background: "transparent"
								}
							},
							chartArea: { background: "" },
							series : [{
								type: "pie",
								startAngle: 90,
								field: "percentage",
								categoryField: "source",
								explodeField: "explode"
							}],
							seriesColors: [ "#8e8e93", "#007aff"],
							tooltip: {
								visible: true,
								template: "#: category # - #: kendo.format( '{0:\\#\\#,\\#}',  value) #"
							}
						}); 
					}
					var selectedCells = this.select();
					var selectedCell = this.dataItem( selectedCells );			
					var items = [];	
					items.push({ percentage: selectedCell.usableSpace , source: "USED", explode: false });
					items.push({ percentage: selectedCell.freeSpace , source: "FREE", explode: false });		
					renderTo2.data("kendoChart").setDataSource( new kendo.data.DataSource({data: items}) );	 
				}						
			});	
			
			renderTo.find(".k-grid-toolbar").on("click", ".k-grid-refresh", function (e) {
				e.preventDefault();
				grid.dataSource.read();
				createMemoryUsage(observable);
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
          	<#if PARENT_PAGE_NAME??>
            <li class="list-inline-item g-mr-10">
              <a class="u-link-v5 g-color-gray-dark-v6 g-color-secondary--hover g-valign-middle" href="#!">${PARENT_PAGE_NAME} </a>
              <i class="hs-admin-angle-right g-font-size-12 g-color-gray-light-v6 g-valign-middle g-ml-10"></i>
            </li> 
            </#if>
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
			<nav>
				<div class="nav nav-tabs" id="nav-tab" role="tablist">
					<a class="nav-item nav-link active g-ml-20" id="nav-platform-tab" data-toggle="tab" href="#nav-platform" role="tab" aria-controls="nav-platform" aria-selected="false">개요</a>
					<a class="nav-item nav-link" id="nav-usage-tab" data-toggle="tab" href="#nav-usage" role="tab" aria-controls="nav-usage" aria-selected="false">Momery & DISK Usage</a>
					<a class="nav-item nav-link" id="nav-libs-tab" data-toggle="tab" href="#nav-libs" role="tab" aria-controls="nav-libs" aria-selected="false">라이브러리</a>
				</div>
			</nav> 
			<div class="tab-content" id="nav-tabContent">
				<div class="tab-pane fade show active g-pa-0" id="nav-platform" role="tabpanel" aria-labelledby="nav-platform-tab">  

			<div class="card g-brd-gray-light-v7 g-brd-0">
				<header class="card-header g-bg-transparent g-brd-bottom-none g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
                    <div class="media">
                      <div class="media-body d-flex justify-content-end">
                        <a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="click:refreshPlatformInfo"></a>
                      </div>
                    </div>
                </header> 
                <section class="g-mx-15 g-mb-20">
                 <!-- info operation -->	
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Operating System</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.operatingSystem">&nbsp;</span> (<span data-bind="text:overview.operatingSystemArchitecture"></span>)
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Available Processors</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.availableProcessors">&nbsp;</span>
                  
                  <!-- info system -->
                 <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">File System Encoding</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.fileSystemEncoding">&nbsp;</span>
                  
                 <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">System Language (System Timezone) </h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.systemLanguage">&nbsp;</span> <span data-bind="text:overview.systemTimezone"></span>   
                  
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Temp Directory</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.tempDirectory">&nbsp;</span> <span data-bind="text:overview.tempDirectory"></span>   
                  
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Working Directory (User) </h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.workingDirectory">&nbsp;</span> ( <span data-bind="text:overview.userName"></span> ) 
                  <!- info java -->
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Java Vm</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.javaVm">&nbsp;</span> <span data-bind="text:overview.jvmVersion"></span>   
                  
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Jvm Version</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.jvmVersion">&nbsp;</span>         

                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Jvm Vendor</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.jvmVendor">&nbsp;</span>           

                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Jvm Implementation Version</h3>
                  <span class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.jvmImplementationVersion">&nbsp;</span>  
                                                                                                                                                                                                  
                  <h3 class="g-font-size-12 g-font-size-default--md g-color-black g-mt-20">Jvm Input Arguments</h3>
                  <p class="g-font-weight-300 g-color-gray-dark-v6 mb-0" data-bind="text:overview.jvmInputArguments">&nbsp;</p> 
                  
                </section>
              </div> 						
				</div>
				<div class="tab-pane fade" id="nav-usage" role="tabpanel" aria-labelledby="nav-usage-tab"> 
					<div class="row">
						<div class="col-6">
							<div id="disk-usage-chart"></div>
							<div id="disk-usage-grid" class="g-brd-gray-light-v7 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-1 g-mb-1 g-mt-15"></div>
						</div>
						<div class="col-6">
							<div id="memory-usage-chart"></div> 
						</div>
					</div> 
				</div>
				<div class="tab-pane fade" id="nav-libs" role="tabpanel" aria-labelledby="nav-libs-tab">
					<div id="libs-grid" class="g-brd-gray-light-v7 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-1 g-mb-1 g-mt-15"></div>
				</div>
			</div>  
        </div>
		<!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
</body>
</html>