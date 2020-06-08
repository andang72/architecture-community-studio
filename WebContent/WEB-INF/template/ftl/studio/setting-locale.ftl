<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "언어 및 지역" />	
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
		currentUser : new community.data.model.User(),
		editable : false,
		visible : false,
		edit: function(){
			var $this = this;
			$this.set('editable', true);
		},
		cancle: function(){
			var $this = this;
			$this.set('editable', false);
		},
		locale : '${ CommunityContextHelper.getConfigService().getLocale().toLanguageTag() }',
		timeZone : '${ CommunityContextHelper.getConfigService().getTimeZone().getID() }',
		saveOrUpdate : function(){
			var $this = this;
			var template = community.data.template('언어(지역) : <span class="g-color-blue" >#: locale #</span> 와 시간대 : <span class="g-color-blue">#: timeZone #</span> 로 변경하시겠습니까 ?');
			var dialog = community.ui.dialog( null, {
				title : '${PAGE_NAME}',
				content : template($this),
				actions: [
                { text: '확인', 
                	action: function(e){  
                		community.ui.progress($('#features'), false); 
                		community.ui.notification().show({ title:'',  message: '${PAGE_NAME} 정보가 변경되었습니다.'}, "success"); 
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/locale/save-or-update.json" />', {
							data: community.ui.stringify({ locale : $this.locale, timeZone : $this.timeZone }),
							contentType : "application/json",						
							success : function(response){
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
	  createLocaleDorpDownList(observable);
	  createTimeZoneDorpDownList(observable) 
	  observable.set('visible', true);
  	  console.log("END SETUP APPLICATION.");	
  	});	
  	
  	function createLocaleDorpDownList(observable){
  	  var renderTo = $('#locale');
  	  renderTo.kendoDropDownList({
		dataTextField: "displayName",
		dataValueField: "languageTag",
		height: 500,
		filter: "contains",
			dataSource: {
				transport: {
					read: {
						type:'post', dataType: 'json', contentType: "application/json; charset=utf-8",
						url: "<@spring.url "/data/secure/mgmt/locale/available-list.json"/>",
					}
				},
				serverFiltering: false,
				group: { field: "displayCountry" }
			}
		});	
  	} 
  	
  	function createTimeZoneDorpDownList(observable){
  		var renderTo = $('#timeZone'); 
  		if( !community.ui.exists( renderTo )){
			renderTo.kendoDropDownList({
				dataTextField: "displayName",
				dataValueField: "id",
				height: 500,
				filter: "contains",
					dataSource: {
						transport: {
							read: {
								type:'post', dataType: 'json', contentType: "application/json; charset=utf-8",
								url: "<@spring.url "/data/secure/mgmt/locale/"/>" + observable.locale + "/timezone/list.json",
							}
						},
						serverFiltering: false
					}
			}); 
  		} 
  	}
  	
  </script>
  <style>
  	/*
	.k-dropdown-wrap .k-select, .k-numeric-wrap .k-select, .k-picker-wrap .k-select {
	    padding: 5px;
	}
	*/
	
	.k-dialog .k-dialog-title {
		min-height:15px;
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
          <div id="features" class="container-fluid" data-bind="visible:visible" style="display:none;"> 
			<div class="row">
				<div class="col-lg-10 g-mb-10">  
				<div class="card g-brd-gray-light-v7 g-brd-0 g-rounded-3 g-mb-30">
                  <header class="card-header g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
                    <div class="media">
                      <h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
                      
                      </h3> 
                      <div class="media-body d-flex justify-content-end"> 
                      	<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" ></a>
                      	<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" style="display:none;"  ></a> 
                      	<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:refresh();">
							<i class="hs-admin-reload g-font-size-18"></i>
						</a>
                      </div>
                    </div>
                  </header>
                  <div class="card-block g-pa-15 g-pa-30--sm" > 
                  <form>
                    <div class="row g-mb-20">
                      <div class="col-md-3 align-self-center g-mb-5 g-mb-0--md">
                        <label class="g-font-weight-300 g-color-black mb-0" for="#locale">Locale</label>
                      </div> 
                      <div class="col-md-9 align-self-center">
                        <div class="form-group mb-0">
                          <input id="locale" class="form-control" style="width:100%;" data-bind="value: locale, enabled:editable" />	
                        </div>
                      </div>
                    </div> 
                    <div class="row g-mb-20">
                      <div class="col-md-3 g-pt-8 g-mb-5 g-mb-0--md">
                        <label class="g-font-weight-300 g-color-black mb-0" for="#timeZone">TimeZone</label>
                      </div> 
                      <div class="col-md-9 align-self-center">
                        <div class="form-group mb-0">
                         <input id="timeZone" class="form-control" style="width:100%;" data-bind="value: timeZone, enabled:editable " />
                        </div>
                      </div>
                    </div> 
                    <div class="row" data-bind="visible:editable" style="display:none;">
                      <div class="col-md-9 ml-auto text-right"> 
                        <button id="showToast" class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:saveOrUpdate" type="button">저장</button>
                        <button id="clearToasts" class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md g-mb-10" type="reset" data-bind="click:cancle">취소</button>
                      </div>
                    </div>
                  </form> 
                  </div>
                </div> 
                
				</div>
				<div class="g-brd-left--lg g-brd-gray-light-v4 col-md-2 g-mb-10 g-mb-0--md">
					<section class="g-mb-10 g-mt-20"></section>
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