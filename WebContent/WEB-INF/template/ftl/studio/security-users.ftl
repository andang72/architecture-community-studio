<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "사용자" />	
  <#assign PARENT_PAGE_NAME = "보안" />	  
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
  	  
  	  var UserProvider = kendo.data.Model.define({ 
		id : "name",
		fields: { 			 		
			name: { type: "string", defaultValue: "" },	
			enabled: { type: "boolean", defaultValue: false },
			paginationable: { type: "boolean", defaultValue: false },
			updatable : { type: "boolean", defaultValue: false }
		}});
		
		
  	  var observable = new community.data.observable({
  	  	visible : true,
  	  	selected : false,
  	  	provider : new UserProvider(),
  	  	providers : new kendo.data.DataSource({
            transport: {
                read: { url: "<@spring.url "/data/secure/mgmt/security/users/providers/list.json"/>", type:'post', contentType: "application/json; charset=utf-8" }
            },
            error : community.ui.error,
			schema: {
				total: "totalCount",
				data:  "items",
				model: UserProvider
			}
        }), 
       	getApplicationUserGrid : function(){
       		return community.ui.grid($('#local-user-grid'));
       	},
        deleteSelectedUsers : function(e){ 
        	var $this = this; 
			var grid = $this.getApplicationUserGrid();
			if( grid.selectedKeyNames().length > 0  ){
			var template = community.data.template('선택한 사용자를 영구적으로 제거합니다.<br/> 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '선택한 사용자을 삭제하시겠습니까?',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){  
                		community.ui.progress($('#features'), false); 
                		community.ui.ajax( '<@spring.url "/data/secure/mgmt/security/users/delete.json" />', {
							data: community.ui.stringify(grid.selectedKeyNames()),
							contentType : "application/json",
							success : function(response){ 
								dialog.close();
								community.ui.notify( response.count + "건이 삭제되었습니다.");
								grid.dataSource.read();
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
			}
        },
        providerChanged : function(e){
       		var $this = this;	 
       		createExternalUserGrid($this);
        }
  	  }); 
  	  createLocalUserGrid(observable);  
  	  community.data.bind( $('#features') , observable ); 
  			$('#features').on( "click", "a[data-action=edit]", function(e){		
				var $this = $(this);	
				if( community.ui.defined($this.data("object-id")) ){
					var objectId = $this.data("object-id");	
  					community.ui.send("<@spring.url "/secure/studio/security-users-editor" />", { userId: objectId });
  				}		
			});	 
  	  console.log("END SETUP APPLICATION."); 
  	});
  	
  	function createExternalUserGrid(observable){ 
		var renderTo = $('#external-user-grid');
		if( !community.ui.exists(renderTo) && observable.get('provider') != null ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/security/users/providers/"/>' + observable.get('provider.name') + '/list.json', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							return community.ui.stringify(options);
						},
					}, 
					pageSize: 50,
					serverPaging : true,
					serverFiltering:true,
					serverSorting: true,
					error : community.ui.error,
					batch: true, 
					schema: {
						total: "totalCount",
						data:  "items",
						model: community.data.model.User
					}
				},
				/*height : 600,*/
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
					{ field: "USER_ID", title: "#" , template : '#: userId #', width: 80 ,  alidation: { required: true} },
					{ field: "USERNAME", title: "아이디", template : $('#user-column-template').html(), width: 200 , validation: { required: true} },  
					{ field: "NAME", title: "이름",  template : '#: name #', width: 300 , validation: { required: true} },  
					{ field: "EMAIL", title: "메일", template : '#: email #', width: 300 , validation: { required: true} },  
					{ field: "STATUS", title: "상태", template : '#:status#', width: 100 , validation: { required: true} },  
					{ field: "CREATION_DATE", title: "생성일", template : '#= community.data.format.date(creationDate, "yyyy.MM.dd")#', width: 100 , validation: { required: true}, filterable: { ui: "datetimepicker" } },  
					{ field: "MODIFIED_DATE", title: "수정일", template : '#= community.data.format.date(modifiedDate, "yyyy.MM.dd")#', width: 100 , validation: { required: true}, filterable: { ui: "datetimepicker" } }
				]
			}); 
		} 
  	} 
  	
    function createLocalUserGrid(observable){
    	var renderTo = $('#local-user-grid');
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/security/users/find.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
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
						total: "totalCount",
						data:  "items",
						model: community.data.model.User
					}
				},
				toolbar: [{ name: "create" , text: "새로운 페이지 만들기", template:community.ui.template( $('#grid-toolbar-template').html() )  }],
				/*height : 600,*/
				persistSelection: true,
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
                change : function(e) { 
                	if( this.selectedKeyNames().length > 0 ){
                		observable.set('selected', true);
                	}else{ 
                		observable.set('selected', false);
                	} 
                },
				columns: [
					{ selectable: true, width: "50px", headerAttributes: {} },
					{ field: "USER_ID", title: "#" , template : '#: userId #', width: 80 ,  alidation: { required: true} },
					{ field: "USERNAME", title: "아이디", template : $('#user-column-template').html(), width: 200 , validation: { required: true} },  
					{ field: "NAME", title: "이름",  template : '#: name #', width: 300 , validation: { required: true} },  
					{ field: "EMAIL", title: "메일", template : '#: email #', width: 300 , validation: { required: true} },  
					{ field: "USER_EXTERNAL", title: "외부", template : '#: external#', width: 70 , validation: { required: true} },  
					{ field: "STATUS", title: "상태", template : '#:status#', width: 100 , validation: { required: true} },  
					{ field: "CREATION_DATE", title: "생성일", template : '#= community.data.format.date(creationDate, "yyyy.MM.dd")#', width: 100 , validation: { required: true} },  
					{ field: "MODIFIED_DATE", title: "수정일", template : '#= community.data.format.date(modifiedDate, "yyyy.MM.dd")#', width: 100 , validation: { required: true} }
				]
			});						
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
        <div class="g-pa-20 g-pb-0">
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-5">${PAGE_NAME}</h1> 
        </div>  
          <!-- Content -->
			<div id="features" class="container-fluid">  
				<nav>
					<div class="nav nav-pills" id="nav-tab" role="tablist">
						<a class="nav-item nav-link active" id="nav-local-tab" data-toggle="tab" href="#nav-local" role="tab" aria-controls="nav-local" aria-selected="false"><i class="hs-admin-user"></i> Local </a>
						<#if SecurityHelper.isUserInRole("ROLE_ADMINISTRATOR,ROLE_SYSTEM,ROLE_DEVELOPER") >	
						<a class="nav-item nav-link" id="nav-external-tab" data-toggle="tab" href="#nav-external" role="tab" aria-controls="nav-external" aria-selected="false"><i class="hs-admin-user"></i> External </a> 
						</#if>
					</div>
				</nav>  
				<div class="tab-content g-pt-5" id="nav-tabContent"> 
					<div class="tab-pane fade show active" id="nav-local" role="tabpanel" aria-labelledby="nav-local-tab">
						<div id="local-user-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0 g-mb-1" style="min-height:600px;"></div>
						<ul class="list-inline g-font-size-13 g-py-13 mb-0">
							<li class="list-inline-item g-color-gray-dark-v4 mr-2">
								<button class="btn btn-md u-btn-pink g-rounded-50 g-mr-10 g-mb-15" data-bind="visible: selected, click:deleteSelectedUsers" style="display:none;">
									<i class="hs-admin-trash g-mr-5"></i> 선택된 사용자 삭제
								</button>
							</li>
						</ul>		
					</div>
					<#if SecurityHelper.isUserInRole("ROLE_ADMINISTRATOR,ROLE_SYSTEM,ROLE_DEVELOPER") >	
					<div class="tab-pane fade" id="nav-external" role="tabpanel" aria-labelledby="nav-external-tab">  
					<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
						<div class="media">
							<h3 class="d-flex align-self-center g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"> 
								<input data-role="dropdownlist"
									data-option-label="사용자 정보 제공자를 선택하여주세요." 
									data-text-field="name"
									data-value-field="name" 
									data-bind="value:provider, source:providers, events:{ change : providerChanged }"
									style="width: 100%;"  class ="g-width-300"/> 
							</h3> 
							<div class="media-body d-flex justify-content-end"> 
								<a href="javascript:void(this);" class="btn btn-md u-btn-3d u-btn-primary g-width-200--md g-font-size-default k-grid-add" data-action="edit" data-object-id="0"  data-bind="visible: provider.updatable" >새로운 사용자 만들기</a>
							</div>
						</div>
						<p class="g-mt-10 g-mb-0 g-color-red">조회 결과중 LOCAL 에 존재하지 않는 사용자는 자동으로 생성됩니다.</p>
					</header>   
						<div id="external-user-grid" class="g-brd-top-0 g-brd-left-0 g-brd-right-0  g-mb-1" style="min-height:500px;"></div>
					</div> 
					</#if>
				</div> 
			</div>
          <!-- End Content --> 
       
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
	<script type="text/x-kendo-template" id="user-column-template">    
	<div class="media">
    	<div class="d-flex align-self-center">
    		<img class="g-width-36 g-height-36 rounded-circle g-mr-15" src="#= community.data.url.userPhoto( '<@spring.url "/"/>', data ) #" >
		</div>
		<div class="media-body align-self-center text-left">
		<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-action="edit" data-object-id="#=userId#">
			<h5 class="g-font-weight-100 g-font-size-default g-mb-0">#= username #</h5> 
		</a>
		
		</div>
	</div>	
	</script>  
  	<script type="text/x-kendo-template" id="grid-toolbar-template">    
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
			</h3> 
			<div class="media-body d-flex justify-content-end"> 
				<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:void(this);" data-action="edit" data-object-id="0">
					<i class="hs-admin-plus g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
				</a>  
			</div>
		</div>
	</header> 
  	</script>	   
</body>
</html>