<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "시퀀서" />	
  <#assign PARENT_PAGE_NAME = "서비스" />	  
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
		refresh : function (){
			var $this = this ;	  
		},
		edit: function(){
			var $this = this;
			$this.set('editable', true);
		}
	  });	 
	  
	  community.data.bind( $('#features') , observable );   
	  createSequencerGrid ( observable ); 
	  $('#features').on( "click", "a[data-action=edit]", function(e){		
			var $this = $(this);	
				if( community.ui.defined($this.data("object-id")) ){
					var objectId = $this.data("object-id");	
					var grid =  community.ui.grid($('#sequencers-grid'));
					var dataItem = grid.dataSource.get(objectId); 
					if( dataItem == null )
						dataItem = new community.ui.studio.model.Sequencer(); 
					getSequencerCreateWindow(dataItem); 
  				}	
		});	 
  	  console.log("END SETUP APPLICATION.");	
  	});	

  	function getSequencerCreateWindow( data ){  
  		var renderTo = $('#sequencer-window');
  		if( !community.ui.exists( renderTo )){ 
  			var observable = community.data.observable({ 	
  				isNew : false,
  				sequencer : new community.ui.studio.model.Sequencer(),
  				saveOrUpdate : function(){
  					var $this = this;
  					community.ui.progress(renderTo, true);	
					community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/sequencers/0/save-or-update.json" />', {
						data: community.ui.stringify($this.sequencer),
						contentType : "application/json",
						success : function(response){
							community.ui.notify( $this.sequencer.name + "이 저장되었습니다.");
							community.ui.grid( $('#sequencers-grid') ).dataSource.read();
						}
					}).always( function () { 
						community.ui.progress(renderTo, false);
						$this.close();
					});			
  				},
  				setSource : function( data ) {
  					console.log(data);
  					var $this = this ;	  
  					data.copy( $this.sequencer );
  					if( data.sequencerId > 0 ){
  						$this.set('isNew', false);
  						$this.set('editable', true);
  					}else{
  						$this.set('isNew', true );
  						$this.set('editable', true);
  					} 
  				},
  				close : function(){
  					window.close();
  				}
  			}); 
  			var window = community.ui.window(renderTo, {
				width: "580px",
				title: "${PAGE_NAME}",
				visible: false,
				modal: true,
				actions: [  "Close"], // 
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
  	
  	function createSequencerGrid( observable ) {
  		var renderTo = $('#sequencers-grid');
		if( !community.ui.exists(renderTo) ){   
			var grid = community.ui.grid(renderTo, {
				autoBind : false,
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/services/sequencers/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					error : community.ui.error,
					pageSize: 50,
					serverFiltering: false,
					serverSorting: false, 
					serverPaging: false, 
					schema: {
						total: "totalCount",
						data:  "items",
						model: community.ui.studio.model.Sequencer
					}
				},  
				sortable: true,
				filterable: true, 
				columns: [ 
					{ field: "sequencerId", title: "ID", width: 150 }, 
					{ field: "name", title: "NAME" , template: $('#name-column-template').html() },
					{ field: "displayName", title: "DISPLAY_NAME" ,  template: '#if(displayName == null){# N/A #}else{# #:displayName# #}#', width: 250 },
					{ field: "value", title: "VALUE" ,  width: 150  }
				],
				change: function(e) {
 				}
			}); 
		}
		community.ui.grid(renderTo).dataSource.read(); 
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
          	 
          	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-mb-5">
					<div class="media">
						<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
							<a class="u-link-v5 g-font-size-16 g-font-size-18--md g-color-gray-light-v6 g-color-secondary--hover k-grid-refresh" href="#" data-bind="click:refresh"><i class="hs-admin-reload"></i></a>
						</h3> 
						<div class="media-body d-flex justify-content-end">  
							<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:void(this);" data-action="edit" data-object-id="0">
								<i class="hs-admin-plus g-font-size-18"></i>
								<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
							</a>
								
						</div>
					</div>
			</header> 
          	<div id="sequencers-grid" class="g-brd-left-0 g-brd-right-0 g-mb-1">
         </div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
	<div id="sequencer-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" >
		<section class="g-pa-15 g-pa-30--sm"> 
		<!-- Border Alert -->
	    <div class="alert fade show g-brd-around g-brd-gray-dark-v6 g-rounded-10" role="alert"> 
	      <button type="button" class="close u-alert-close--light g-ml-10 g-mt-1" data-dismiss="alert" aria-label="Close">
	        <span aria-hidden="true">×</span>
	      </button> 
	      <div class="media">
	        <div class="d-flex g-mr-10"></div>
	        <div class="media-body"> 
	          <p class="m-0 g-font-size-14">
	          시퀀서는 유니크한 숫자형식의 ID 값을 생성하기 하기 위한 서비스입니다.
	          새로운 시퀀스를 생성할 때는 반듯이 기존 ID 와 중복되지 않는 2000 이상의 ID 값을 입력해주세요. 
	          </p>
	        </div>
	      </div> 
	    </div>
	    <!-- End Border Alert --> 
	    <div class="row no-gutters">
	    	<div class="col-sm-6">
				<div class="form-group g-mb-15">
					<label class="g-mb-10 g-font-weight-600" for="input-image-name">ID <span class="text-danger">*</span></label>
					<div class="g-pos-rel g-mr-5">
						<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
							<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
						</span>
						<input id="input-sequencer-id" name="ID" class="form-control form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-14 g-py-10" type="number" placeholder="ID" 
							data-bind="value:sequencer.sequencerId, enabled:isNew" required="" validationmessage="{0}를 입력하여 주세요.">
					</div>
					<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-sequencer-id" role="alert" style="display:none;"></span>
				</div>
			</div>
			<div class="col-sm-6">
				<div class="form-group g-mb-15">
					<label class="g-mb-10 g-font-weight-600" for="input-image-name">NAME <span class="text-danger">*</span></label>
					<div class="g-pos-rel g-mr-5">
						<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
							<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
						</span>
						<input id="input-sequencer-name" name="이름" class="form-control form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-14 g-py-10 g-mb-5" type="text" placeholder="NAME" 
							data-bind="value:sequencer.name, enabled:isNew" required="" validationmessage="{0}를 입력하여 주세요.">
						<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-mt-5">NAME 값은 영문 대문자를 사용해 주세요. <br/> ex) USER </small>
					</div>
					<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-sequencer-name" role="alert" style="display:none;"></span>
				</div>
			</div>
		</div>	
		<div class="row no-gutters">
	    	<div class="col-sm-6">
			<div class="form-group g-mb-15">
				<label class="g-mb-10 g-font-weight-600" for="input-image-name">DISPLAY_NAME</label>
				<div class="g-pos-rel g-mr-5">
					<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
						<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
					</span>
					<input id="input-sequencer-displayname" name="DISPLAY NAME" class="form-control form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-14 g-py-10" type="text" placeholder="DISPLAY NAME" 
						data-bind="value:sequencer.displayName">
				</div> 
			</div>
			</div>
			<div class="col-sm-6">
			<div class="form-group g-mb-15">
				<label class="g-mb-10 g-font-weight-600" for="input-image-name">VALUE <span class="text-danger">*</span></label>
				<div class="g-pos-rel g-mr-5">
					<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
						<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
					</span>
					<input id="input-sequencer-value" name="VALUE" class="form-control form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-14 g-py-10" type="number" placeholder="VALUE" 
						data-bind="value:sequencer.value" required="" validationmessage="{0}를 입력하여 주세요.">
				</div>
				<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-sequencer-value" role="alert" style="display:none;"></span>
			</div>
			</div>
		</div>
		<div class="row no-gutters">
			<div class="col-12 text-right">
				<button class="btn btn-md u-btn-darkgray g-font-size-default g-ml-10" type="button" data-bind="click:saveOrUpdate">저장</button>
				<button class="btn btn-md u-btn-outline-lightgray g-font-size-default g-ml-10" type="button" data-bind="click:close">닫기</button>
			</div>	
		</div>				
		</section> 
	</div>	
	</div>
	<script type="text/x-kendo-template" id="name-column-template">    
		<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-action="edit" data-object-id="#=sequencerId#">
		<h5 class="g-font-weight-100 g-mb-0 g-font-size-14">
		#= name # 
		</h5> 
		</a>
	</script>
	  
</body>
</html>