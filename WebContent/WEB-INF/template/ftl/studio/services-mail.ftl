<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "메일" />	
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
  	  	var MailServiceConfig = community.data.Models.define({
			fields: { 	
				username : { type : "string" },
				password : { type : "string" },
				host : { type : "string" },
				port : { type : "string" , defaultValue: "-1"  },
				protocol : { type : "string" },
				ssl : { type: "boolean", defaultValue: false },
				defaultEncoding : { type : "string" , defaultValue: "UTF-8" },
				properties : { type : "object" , defaultValue: {} },
				enabled : { type: "boolean", defaultValue: false }
			},
			 copy : function ( target ){ 
		    	target.set("username" , this.get("username"));
		    	target.set("password", this.get("password"));
		    	target.set("host", this.get("host"));
		    	target.set("port", this.get("port"));
		    	target.set("protocol", this.get("protocol"));
		    	target.set("ssl", this.get("ssl"));
		    	target.set("defaultEncoding", this.get("defaultEncoding"));
		    	target.set("enabled", this.get("enabled"));
		    	target.set("properties", this.get("properties"));
		    }
		}); 
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		mail : new MailServiceConfig(),
		editable : false,
		configurable : false,
		testable : false,
		refresh : function (){
			var $this = this ;	 
			$this.load();
		},
		settings : function(){
			var $this = this;
			$this.set('configurable', !$this.get('configurable') );	
			createSettingsWindow($this);
		},	
		setSource : function( data ){
			var $this = this ;	 
			data.copy( $this.mail );
			
			$this.set('editable', false );
			
			if( $this.mail.enabled )
				$this.set('testable', true );
			else 
				$this.set('testable', false ); 
			createParametersGrid($this); 
		},
		load: function(){
			var $this = this;
			community.ui.progress($('#features'), true);	
			community.ui.ajax('<@spring.url "/data/secure/mgmt/services/mail/config.json"/>', {
				contentType : "application/json",
				success: function(data){	
					console.log(kendo.stringify(data));
					$this.setSource( new MailServiceConfig(data) );
				}	
			}).always( function () {
				community.ui.progress($('#features'), false);
			});
		},	
		mailProtocols : [
			{ text: "POP3", value: "POP3" },
			{ text: "SMTP", value: "SMTP" },
			{ text: "IMAP", value: "IMAP" }
		],
		edit: function(){
			var $this = this;
			$this.set('editable', true); 
		},
		cancle: function(){
			var $this = this;
			$this.set('editable', false);
		},
		testConnection : function(){
			
			community.ui.progress($('#features'), true); 
			community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/mail/test.json" />', {
				contentType : "application/json",						
				success : function(response){
					if( response.success ){
						community.ui.notification().show({ title:'',  message: '테스트에 성공하였습니다.'}, "success"); 
					}else {
						community.ui.notification().show({ title:'',  message: '테스트에 실패 하였습니다.'}, "error"); 
					}
				} 
			}).always( function () { 
				community.ui.progress($('#features'), false); 
			});	
		
		},
		saveOrUpdate : function(){
			var $this = this;
			var template = community.data.template('메일 서비스 설정을 변경하시겠습니까 ?');
			var dialog = community.ui.dialog( null, {
				title : '${PAGE_NAME}',
				content : template($this),
				actions: [
                { text: '확인', 
                	action: function(e){  
                		community.ui.progress($('#features'), false);  
                		var properties = {};
                		$.each(community.ui.grid($("#parameters-grid")).dataSource.view() , function( index, item ){
                			properties[item.name] = item.value;
                		});
                		$this.mail.properties = properties;
                		console.log( kendo.stringify( $this.get('mail') ) );
 
                		community.ui.notification().show({ title:'',  message: '${PAGE_NAME} 정보가 변경되었습니다.'}, "success"); 
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/mail/save-or-update.json" />', {
							data: community.ui.stringify($this.mail),
							contentType : "application/json",						
							success : function(response){
								$this.load();
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
	  observable.load(); 
  	  console.log("END SETUP APPLICATION.");	
  	});	
  	
  	function createSettingsWindow( observable ){  
  		var renderTo = $('#settings-window');
  		if( !community.ui.exists( renderTo )){ 
  			var window = community.ui.window( renderTo, {
				width: "800px",
				minWidth : 600,
				maxWidth : 1000,
				title: "${PAGE_NAME} 설정",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){  
 
				},
				close: function(){ 
				}
			}); 
			community.data.bind( renderTo , observable );
  		} 
  		community.ui.window( renderTo ).center().open();
  	} 
  	  
  	function createParametersGrid(observable){ 
	  	var renderTo = $("#parameters-grid");
	  	if( !community.ui.exists( renderTo )){ 
		  	var grid = community.ui.grid(renderTo, {
				dataSource: {
					data : [],
					schema: {
						model: community.data.model.Property
					}
				},
				height: 400,
				pageable: false, 
				scrollable: true,
				sortable: true,
				filterable: false, 
				toolbar: [{ name: "create" , text: "파라메터 추가하기", template: '<a href="javascript:void(this);" class="btn u-btn-outline-lightgray g-mr-5 k-grid-add">파라메터 추가</a>' }],
				columns: [ 
					{ field: "name", title: "파이메터" , width: "300px" },
					{ field: "value", title: "값" },
					{ command: ["edit", "destroy"], title: "&nbsp;", width: "250px" }],
				editable: "inline"
			});  
		} 
		community.ui.progress(renderTo, true ); 
		community.ui.ajax( '<@spring.url "/data/secure/mgmt/services/mail/properties/list.json"/>', { 
			contentType : "application/json",						
			success : function(response){
				 community.ui.grid(renderTo).dataSource.data( response );				
			} 
		}).always( function () { 
			community.ui.progress(renderTo, false); 
		});	 	
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
          	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-mb-25">
					<div class="media">
						<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
							
						</h3> 
						<div class="media-body d-flex justify-content-end"> 
							<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#" data-bind="click:settings">
								<i class="hs-admin-panel g-font-size-18"></i>  
								 <span class="g-hidden-sm-down g-ml-10"> ${PAGE_NAME} 설정</span>
							</a>							
						</div>
					</div>
			</header> 
         </div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
  <div id="settings-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" >
	<header class="card-header g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
			<div class="media-body d-flex justify-content-end"> 
				<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" style=""></a>
				<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" style="display: none;"></a>	 
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-5 g-ml-10--sm g-ml-15--xl" href="#" data-bind="click:refresh" >
					<i class="hs-admin-reload g-font-size-20"></i>
				</a>
			</div>
		</div>
	</header>    
          	<!-- mail --> 
          	<form id="fm" class="g-pa-15">
          		<div class="row no-gutters g-mb-15">
					<div class="col-md-4">
						<label class="g-mb-10 g-font-weight-600" for="input-host">Host</label>
						<div class="g-pos-rel g-mr-5">
							<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
								<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
							</span>
							<input id="input-host" name="input-host" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="Host 을 입력하세요" data-bind="value: mail.host, enabled:editable" required="" validationmessage="이름을 입력하여 주세요." autofocus="">
						</div>
						<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-host" role="alert" style="display:none;"></span>
					</div>
					<div class="col-md-4">
						<label class="g-mb-10 g-font-weight-600" for="input-port">Port</label>
						<div class="g-pos-rel g-mr-5">
							<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
								<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
							</span>
							<input id="input-port" name="input-port" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="number" placeholder="Port 를 입력하세요" data-bind="value:mail.port, enabled:editable" required="" validationmessage="메일주소를 입력하여 주세요." autofocus="">
						</div>
						<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-port" role="alert" style="display:none;"></span>
					</div>
					<div class="col-md-4">
						<label class="g-mb-10 g-font-weight-600" for="input-defaultEncoding">Encoding</label>
						<div class="g-pos-rel g-mr-5">
							<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
								<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
							</span>
							<input id="input-defaultEncoding" name="input-defaultEncoding" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="Encoding 를 입력하세요" data-bind="value:mail.defaultEncoding, enabled:editable" required="" validationmessage="메일주소를 입력하여 주세요." autofocus="">
						</div>
						<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-port" role="alert" style="display:none;"></span> 
					</div>
				</div>
				<div class="row no-gutters g-mb-15">
					<div class="col-md-4 g-mb-10">
						<label class="g-mb-10 g-font-weight-600" for="input-username">Username</label>
						<div class="g-pos-rel g-mr-5">
							<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
								<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
							</span>
							<input id="input-username" name="input-username" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="email" placeholder="아이디를 입력하세요" data-bind="value:mail.username, enabled:editable" required="" validationmessage="아이디를 입력하여 주세요." autofocus="">
						</div>
						<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-username" role="alert" style="display:none;"></span>
					</div>
					<div class="col-md-4 g-mb-10">
						<label class="g-mb-10 g-font-weight-600" for="input-password">Password</label>
						<div class="g-pos-rel g-mr-5">
							<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
								<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
							</span>
							<input id="input-password" name="input-password" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="password" placeholder="비밀번호를 입력하세요" data-bind="value:mail.password, enabled:editable" required="" validationmessage="메일주소를 입력하여 주세요." autofocus="">
						</div>
						<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-port" role="alert" style="display:none;"></span>
					</div>
				</div>						
				<hr class="g-brd-gray-light-v4 g-mx-minus-10">	
				<div class="row no-gutters g-mb-15">
					<div class="col-md-6 g-mb-10">
						<label class="g-mb-10 g-font-weight-500" for="input-defaultEncoding">Protocol</label>
 						<input data-role="dropdownlist"
										data-option-label="프로토콜을 선택하세요."
										data-auto-bind="true"
										data-value-primitive="true"
										data-text-field="text"
										data-value-field="value"
										data-bind="value: mail.protocol, enabled:editable, source: mailProtocols" 
										style="width: 90%;" />  
					</div>
					<div class="col-md-6 g-mb-10">
						<label class="d-flex align-items-center justify-content-between g-mb-0" for="input-enabled">
							<span class="g-pr-20 g-font-weight-500">사용 여부</span>
							<div class="u-check">
								<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" id="input-enabled" name="input-enabled" value="true" data-bind="checked: mail.enabled, enabled:editable" type="checkbox">
								<div class="u-check-icon-radio-v8"><i class="fa" data-check-icon=""></i></div>
							</div>
						</label>
						<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
							서비스 사용 유무를 지정합니다.
						</small>
					</div>
				</div>
				<hr class="g-brd-gray-light-v4 g-mx-minus-10 g-mt-10">	
				<label class="g-mb-1">메일 서비스 프로퍼티</label>
				<div class="row no-gutters g-mb-30">
					<div id="parameters-grid" style="width:100%;" class="g-mt-10" data-bind="enabled:editable"></div>
				</div>
				<hr class="g-brd-gray-light-v4 g-mx-minus-10">	
				<div class="row no-gutters">
					<div class="col-md-5" data-bind="visible:testable" style="display:none;"> 
						<a href="javascript:void(this);" class="btn btn-md g-rounded-50 u-btn-blue g-mr-10 g-width-100" data-bind="click:testConnection">테스트 연결</a>
					</div>
					<div class="col-md-5 ml-auto text-right" data-bind="visible:editable" style="display:none;">  
						<button class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10" data-bind="click:saveOrUpdate" type="button">저장</button>
						<button class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md" type="reset">취소</button>
					</div>
				</div>
			</form>
          	<!-- end mail -->  
  </div>
</body>
</html>
</#compress>