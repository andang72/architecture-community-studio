<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "메뉴" />	
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
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	 
  	  community.ui.studio.setup();	 
  	  var observable = new community.data.observable({
  	  	visible : false,
  	  	reload : function (e){ 
			var $this = this;
			var template = community.data.template('주의가 필요합니다. 메뉴 설정이 잘못되어 있는 경우 스튜디오 메뉴가 정상 동작하지 않을 수 도 있습니다. <br/><br/> 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '스튜디오 메뉴를 다시 로드합니다',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){   
                		community.ui.progress($('.k-dialog'), true); 
                		community.ui.ajax( '<@spring.url "/data/secure/mgmt/setup/menu/reload.json" />', {
							contentType : "application/json",
							success : function(response){ 
								dialog.close();
								community.ui.notify( "${PAGE_NAME}가 다시 로드 되었습니다.");
							}
						}).always( function () {
							community.ui.progress($('.k-dialog'), false);  
						});  
						return false;
                	},
                	primary: true },
              	{ text: '취소'}
            	]		
			}).open();	 
  	  	}
  	  }); 
  	  
  	  createMenuGrid(observable); 
  	  community.data.bind( $('#features') , observable );
  	  observable.set('visible', true);
  	  
  	  console.log("END SETUP APPLICATION.");	
  	});
  	
    function createMenuGrid(observable){
    	var renderTo = $('#menus-grid');
		if( !community.ui.exists(renderTo) ){  
			var grid = community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/menus/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					pageSize: 50,
					serverPaging : true,
					error : community.ui.error,
					batch: true, 
					schema: {
						data:  "items",
						total: "totalCount",
						model: community.data.model.Menu
					}
				}, 
				toolbar: [{ name: "create" , text: "새로운 ${PAGE_NAME} 만들기", template:community.ui.template( $('#grid-toolbar-template').html() )  }],
				sortable: true,
				filterable: false,
				pageable: {
					refresh: true,
					pageSizes: [50, 100, 200, 300]
				},
				columns: [
				{ field: "MENU_ID", title: "ID", filterable: false, sortable: true , width : 100 , template:'#= menuId #', attributes:{ class:"text-center" }}, 
				{ field: "NAME", title: "이름", filterable: false, sortable: true, width: 300, template:$('#name-column-template').html() },  
				{ field: "DESCRIPTION", title:"설명", filterable: false, sortable: true, template:'#: description #'  },  
				{ field: "CREATION_DATE", title: "생성일", filterable: false, sortable: true , width : 100 , template : '#: community.data.format.date( creationDate ,"yyyy.MM.dd")#' ,attributes:{ class:"text-center" } } , 
				{ field: "MODIFIED_DATE", title: "수정일", filterable: false, sortable: true , width : 100 , template : '#: community.data.format.date( modifiedDate ,"yyyy.MM.dd")#' ,attributes:{ class:"text-center" } }
				]			
			});	

			$('#features').on( "click", "a[data-action=details]", function(e){		
				var $this = $(this);	
				if( community.ui.defined($this.data("object-id")) ){
					var objectId = $this.data("object-id");	
					community.ui.send("<@spring.url "/secure/studio/resource-menus-editor" />", { menuId: objectId });
  				}		
			});
			
			$('#features').on( "click", "a[data-action=edit]", function(e){		
				var $this = $(this);	
				if( community.ui.defined($this.data("object-id")) ){
					var objectId = $this.data("object-id");	
					var dataItem = grid.dataSource.get(objectId);
					if( dataItem == null )
						dataItem = new community.data.model.Menu();
					console.log( dataItem )
					getMenuCreateWindow(dataItem); 
  				}	
			});															
		}			
    } 
  	
  	function refresh(){
  		var renderTo = $('#pages-grid');
  		community.ui.grid(renderTo).dataSource.read();
  		console.log('grid refresh...');  	
  	} 
  	
  	function getMenuCreateWindow( data ){  
  		var renderTo = $('#menu-window');
  		if( !community.ui.exists( renderTo )){ 
  			var observable = community.data.observable({ 
  				justCreated : false,
  				menu : new community.data.model.Menu(),
  				saveOrUpdate : function(){
  					var $this = this;
  					community.ui.progress(renderTo, true);	
					community.ui.ajax( '<@spring.url "/data/secure/mgmt/menus/save-or-update.json" />', {
						data: community.ui.stringify($this.menu),
						contentType : "application/json",
						success : function(response){
							community.ui.notify( $this.menu.name + "이 저장되었습니다.");
							if( reaponse.data.menuId > 0 && $this.menu.menuId == 0 )
								$this.set('justCreated' , true );
							$this.setSource( new community.data.model.Menu( response.data.menu ) );
							community.ui.grid( $('#menus-grid') ).dataSource.read();
						}
					}).always( function () {
						community.ui.progress(renderTo, false); 
					});			
  				},
  				setSource : function( data ) {
  					console.log(data);
  					var $this = this ;	  
  					data.copy( $this.menu );
  					if( data.menuId > 0 )
  						$this.set('editable', true);
  					else
  						$this.set('editable', false );
  				},
  				close : function(){
  					window.close();
  				}
  			}); 
  			var window = community.ui.window(renderTo, {
				width: "600",
				title: "메뉴",
				visible: false,
				modal: true,
				actions: ["Close"], // 
				open: function(){
				
				},
				close: function(){ 
				
				}
			});  
			community.data.bind(renderTo, observable);
			renderTo.data('model', observable );
  		}
  		renderTo.data('model').setSource( data );
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
			<div id="features" class="container-fluid" data-bind="visible:visible" style="display:none;">			
				<div class="row">   
					<div id="menus-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1"></div>	 
				</div>
				<ul class="list-inline g-font-size-13 g-py-13 mb-0">
					<li class="list-inline-item g-color-gray-dark-v4 mr-2">
						<a href="#!" data-bind="click:reload" class="btn btn-md u-btn-blue g-rounded-50 g-mr-10 g-mb-15"><i class="hs-admin-reload g-mr-5"></i> 스튜디오 ${PAGE_NAME} 업데이트</a>
					</li>
				</ul> 
			</div>
          <!-- End Content --> 
        </div>
        
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
  
	<div id="menu-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" >
	<div class="g-pa-15 g-pa-30--sm"> 
	<div class="row no-gutters">
		<div class="col-6" > 
			<div class="form-group g-mb-30 g-width-400">
				<label class="g-mb-10 g-font-weight-600">이름</label>		
				<div class="form-group g-pos-rel g-rounded-4 mb-0">
					<input type="text" class="form-control g-px-14 g-py-10 g-mb-10" placeholder="메뉴 이름을 입력하세요." data-bind="value: menu.name" >
					<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">이름은 영문자를 사용해 주세요. ex) MENU_USER </small>
				</div> 
			</div> 			
			<div class="form-group g-mb-30 g-width-400">
				<label class="g-mb-10 g-font-weight-600">설명</label>		
				<div class="form-group g-pos-rel g-rounded-4 mb-0">
					<textarea class="form-control form-control-md g-resize-none g-brd-gray-light-v7 g-brd-gray-light-v3--focus g-rounded-4" rows="3" placeholder="메뉴에 대한 설명을 기술하여 주세요." data-bind="value: menu.description" ></textarea>
				</div> 
			</div> 	
			<div class="text-right">
				<button class="btn btn-md u-btn-darkgray g-font-size-default g-ml-10" type="button" data-bind="click:saveOrUpdate">저장</button>
				<button class="btn btn-md u-btn-outline-lightgray g-font-size-default g-ml-10" type="button" data-bind="click:close">닫기</button>
			</div>	
		</div>
		<div class="col" > 
			<div data-bind="visible:justCreated" class="align-self-center g-pa-25 text-center"  >
				<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAABmJLR0QA/wD/AP+gvaeTAAAE10lEQVR4nO2aS28bVRiGnzMzrj2+pCWx02DalC4SIXURlS5Iu4EggVQhsUCCDQrsWJUtl0UhouIXFAnEMmXFggWLIoFUWiTUdpMKBAtoFyUhobk4UePL+DYzLBwnbuZie2KPnWSe1dE3c8bn/c57vpk5YwgICAgICAg4rIh6Y2bm2yOJtdwVENPA0z0cUzdZQnAtOxT7ZGbmrTKAUj+SyOQ/A/FB78bmC2lMPhxYy5vAx9CQAEymAc5Mv0rimZT3nxCQUhVURWxdFlYLVYpVE4CwLEjFFCSH7qYJy4Uqv3z+DQBTl173PpZdPF7KMPfdr5jwDlsJaBxHGugL8WXd9D4GF46mh+rNdL2h2J/aPmJLfMRBfEQRpKLKTtHZhWHCShfFO9GZBAhIuogPy4Kki3hzl/i6gxpRF/5j6PYcCMicP4d2YgSAgdAjRuP3AJjPnWWzUosvVjLcLd0HBJORMdLKoO1vO7mxdTpse1URJFXrvAzdmUMuaMh5jcHbc9vx0fg9QpJGSNI4uZUIgLulBxSMMgWjxJ3ifcfh7y0BHsUflau18x3ECyerdAHvCfAoPirpJGSdAalqsb2b+Mz5c+gxlWosyvqF57fj87mzVAyVsqGykNuJT0bGiEpholKYyciYowxvNcCj+Jik85RSm/0BRWckLPinIFqaee3ECP+++ZolvlkZ4Y+Ni5Z4WhnkjfgLTaW0n4A9rPm8IVOuSAyHyjzIClZLrYnvJu0loAMFbyGvk1X6Qzy0k4AOVvsVvX3xmYfL/H3zd4o5reUhA0TiKuNTEwydGrY93loR7NKtrp2Z9yIeoJjT+Ovmb47HmzugD8QD2+Lfv/JiW/2uXr5FKeucOHcHOIgv+Sy+mzg7wGXmxxMGS5rEsah/4iNxlWJO4+rlW233DSdUx2P243cRPxAySYbhdEL4OvPjUxOuQpwIJ1See2nC8bjVAS62Px4xGY3VTkvIOgLY1GWMhtecbtl+6NQwF959ZW8XscEyiU4znwqbnI6ZhMTO62pc1jm29VwP+2PN78aSAKdqv6lLPKocoWjUuugIVish1vUQUHuff1K8RKrPxYNDDXB6nzcQbFQVTBM2qzIls9bd7n0+qco4bgD0EZYa0Ow+ryPY0BUKhlw7fx/avhGLA1p5yDko4sHGAV738Gprfn/YvhHbGnBYxINNArxsYHa64OUqRucu1gTrc0CPn+1zFYN1Te/cBZtgqQFO4v2wfa5ssF70Tzy0uB/QKdvLOH/0yFX8Fw8tJKBTtpcEDIaqxGWrSL9t34hrAjq55sPCICRMVMl4wgm9FA8u+wGdLniaIUFVoWRK1Gt8r8WDQwK6Ve01Y8dw/SAebJbAQbzVuWFJwGESDzZL4KDd55thexfo1uNtv4kHhx2hg277RiwJOEziwaYG7BZ/46vrIAlefu9i27GfvrxOFTjz9s5u7p/XfgRJNI35RdNPY2vzK55iuYrBxoL1vOziaksxv9j7f4Rs6HfbN9LUAclnj7cVaxSfOGn9JN1qzC+aJqBxTTeL7Z55uzXdaswvGpfAEsDaw2VPF9oPtn+8lKk3F+uNbQcImDXhoxtf/+D3uFz5+Yvvu3BVMVtvbSdgMxn7dGAtX/8jcdq23/5nEcRsNhmd6fVAAgICAgICAnrP/4JUEcNfGIrYAAAAAElFTkSuQmCC">
				<p>창을 닫고 메뉴구성하기 버튼을 클릭하여 메뉴를 구성하여 주세요.</p>
			</div>
		</div>
	</div>	
	</div>
	</div>
  
	<script type="text/x-kendo-template" id="name-column-template">    
		<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-action="edit" data-object-id="#=menuId#">
			<h5 class="g-font-weight-100 g-font-size-14 g-mb-5"> #= name #   </h5>  
		</a>
		<span>
			<a href="\#!" class="btn btn-sm u-btn-pink g-rounded-50 g-px-15 g-mx-5 g-mr-10 g-mt-5 g-font-size-14" data-action="details"  data-object-id="#=menuId#"><i class="fa fa-check-circle g-mr-5"></i> 메뉴 구성 하기 </a>
		</span>  
	</script>
	<script type="text/x-kendo-template" id="user-column-template">    
	<div class="media">
    	<div class="d-flex align-self-center">
    		<img class="g-width-36 g-height-36 rounded-circle g-mr-15" src="#= community.data.url.userPhoto( '<@spring.url "/"/>' , user) #" >
		</div>
		<div class="media-body align-self-center text-left">#if ( !user.anonymous ) {# #: user.name # #}#</div>
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
					<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
				</a>
			</div>
		</div>
	</header> 
  </script>	  
</body>
</html>