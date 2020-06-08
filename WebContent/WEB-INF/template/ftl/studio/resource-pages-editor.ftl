<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "페이지" />	
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
    var __pageId = <#if RequestParameters.pageId?? >${RequestParameters.pageId}<#else>0</#if>;	
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
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>",
			"ace" 							: "<@spring.url "/js/ace/ace"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom",  "ace" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	 
  	  community.ui.studio.setup();	 
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		page : new community.data.model.Page(),
		visible : false,
		editable : true,
		isNew : true,
		preview : function () {
			var $this = this;
			community.ui.send('<@spring.url "/display/pages/"/>' + $this.page.name, { preview : true } , 'GET', '_blank' );
		},
		back: function(){
			if(__pageId > 0)
				window.history.back(); 
		},		
		wizard : function () {
			var $this = this;
			community.ui.send('resource-scripts-wizard', {name: $this.page.name , type: 'page'} , 'GET', '_blank' );
		},
		editor : { 
			type : 'html',
			warp : false 
		},
		objectTypes : [
			{ text: "정의되지 않음", value: "-1" },
			<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
			{ text:'${item.NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
			</#list>
		],
		contentTypes : [
			{ text: "HTML", value: "text/html;charset=UTF-8" },
			{ text: "JSON", value: "application/json;charset=UTF-8" }
		], 	
		edit: function(){
			var $this = this;
			$this.set('editable', true);
			var editor = getCodeEditor($("#htmleditor"));
			if( editor.getReadOnly() )
				editor.setReadOnly(false);
		},
		cancle: function(){
			var $this = this;
			if( $this.get('isNew') ){
				// or back..
				$('#pageForm')[0].reset();
			}else{
			    // or back..
				$this.set('editable', false);
				$this.load(__pageId); 
			}
		},
		load: function(objectId){
			var $this = this;
			if( objectId > 0 ){
				community.ui.progress($('#features'), true);	
				community.ui.ajax('<@spring.url "/data/secure/mgmt/pages/"/>' + objectId + '/get.json?fields=bodyContent', {
					contentType : "application/json",
					success: function(data){	
						$this.setSource( new community.data.model.Page(data) );
					}	
				}).always( function () {
					community.ui.progress($('#features'), false);
				});	
			}else{
				$this.setSource(new community.data.model.Page()); 
			}	
		},	
		select : function(e){
			var $this = this;
			var type = $(e.currentTarget).data('type');
			getResourceSelectorWindow($('#resource-select-window'), type, 'view', $this); 
		},
		selected(type, vlaue){
			var $this = this;
			if( type === 'template' ||type === 'jsp' ) {
				$this.set( 'page.template' , vlaue );
			} else if (type === 'script'){
				$this.set( 'page.script' , vlaue );
			}
		},
		property : function(e){
			var $this = this;
			createPropertyWindowIfNotExist(community.data.Models.Page, observable.page.pageId );
		},
		permissions : function(e){
			var $this = this;
			createPermissionsWindowIfNotExist(community.data.Models.Page, observable.page.pageId); 
		},
		setSource : function( data ){
			var $this = this ;	 
			
			if( data.get('pageId') > 0 ){
				data.copy( $this.page );
				$this.set('editable', false );
				$this.set('isNew', false );
			}else{
				$this.set('editable', true );	
				$this.set('isNew', true );
			}
			
			if( $this.page.bodyContent !=null && $this.page.bodyContent.bodyText != null ){
				getCodeEditor($("#htmleditor")).setValue( $this.page.bodyContent.bodyText );
			}else{
				getCodeEditor($("#htmleditor")).setValue( "" ); 
			}
			getCodeEditor($("#htmleditor")).setReadOnly(!$this.get('editable'));
			
			if( !$this.get('visible') ) 
				$this.set('visible' , true );
		},
		delete : function(e){
			var $this = this; 
			var template = community.data.template('<span class="g-color-primary" >#: page.name #</span> 를 영구적으로 제거합니다.<br/> 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '${PAGE_NAME}를 삭제하시겠습니까?',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){   
                		community.ui.progress($('.k-dialog'), true); 
                		community.ui.ajax( '<@spring.url "/data/secure/mgmt/pages/" />'+ $this.page.pageId +'/delete.json', {
							data: community.ui.stringify({}),
							contentType : "application/json",
							success : function(response){ 
								dialog.close();
								community.ui.notify( "${PAGE_NAME}가 삭제되었습니다.");
								community.ui.send('<@spring.url "/secure/studio/resource-pages"/>');
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
			
		},
		saveOrUpdate : function(e){ 
			var $this = this;
			var validator = community.ui.validator($("#pageForm"), {});
			 if (validator.validate()){
				var saveOrUpdateUrl = '<@spring.url "/data/secure/mgmt/pages/save-or-update.json" />';  
				//if( getCodeEditor($("#htmleditor")).getValue().length > 0 || $this.page.bodyContent.bodyText != null ){
				if( $('#editors-tab a.active').html() === 'HTML'){
					$this.page.bodyContent.bodyText = getCodeEditor($("#htmleditor")).getValue(); 
				}
				if($this.page.bodyContent.bodyText != null) {
					saveOrUpdateUrl = saveOrUpdateUrl + '?fields=bodyContent';
				}
				community.ui.progress($('#features'), true);	
				community.ui.ajax( saveOrUpdateUrl, {
					data: community.ui.stringify($this.page),
					contentType : "application/json",
					success : function(response){
						$this.setSource( new community.data.model.Page( response.data.item ) );
					}
				}).always( function () {
					community.ui.progress($('#features'), false); 
				});				
			}			
		}
	  });	
	  	
	  observable.bind("change", function(e) {
	  	if ( e.field === 'editable' ){
	  		createCodeEditor($("#htmleditor")).setReadOnly( !observable.get('editable') ); 
			console.log(observable.get('editor.warp'));
			getCodeEditor($("#htmleditor")).getSession().setUseWrapMode(observable.get('editor.warp'));
		}
	  });
	  
	  $('#editors-tab a').on('click', function (e) {
		  e.preventDefault() 
		  if( $(this).html()==='HTML' ){
		  	getCodeEditor($("#htmleditor")).setValue( observable.get('page.bodyContent.bodyText' ));
		  }else{
		  	observable.set('page.bodyContent.bodyText', getCodeEditor($("#htmleditor")).getValue());
		  }
	  })	  
	  community.data.bind( $('#features') , observable );  
	  var codeEditor = createCodeEditor($("#htmleditor"), false); 
	  var editor = createEditor($("#editor")); 
	  observable.load(__pageId);  
  	  console.log("END SETUP APPLICATION.");	
  	});	
	
	function createEditor (renderTo){ 
		if( !community.ui.exists( renderTo )){   
			function onCustomInsertImageClick (e) {
  				var editor = $(this).data("kendoEditor"); 
	            var storedRange = editor.getRange(); 
	            var renderTo2 = $('#insert-image-window');
	            if( !community.ui.exists( renderTo2 )){  
					var popupWindow = community.ui.window(renderTo2,{ 
		                modal: true,
		                width: 600,
		                scrollable : false,
		                resizable: false,
		                title : "Image Upload",  
		                visible: false,
		                // Remove the Window from the DOM after closing animation is finished.
		                //deactivate: function(e){ e.sender.destroy(); }
		           	}); 
				 	popupWindow.element.find("a[data-action=close]").click(function(){
		                popupWindow.close(); 
		            });  
					// Insert the new content in the Editor when the Insert button is clicked.
		            popupWindow.element.find("a[data-action=insert]").click(function(){  
		            	var customHtml	= "";
		            	var template = kendo.template('<img class="img-fluid" src="/download/images/#= imageLink.linkId #" alt="#= name #" data-image-link="#=imageLink.linkId" >');
		                var data = listview.dataSource.view();
		                $.each(data, function(index, item ) {
							customHtml = customHtml + template(item);		
						}); 
						editor.selectRange(storedRange);
		                editor.exec("inserthtml", { value: customHtml }); 
		                popupWindow.close();
		                listview.clearSelection(); 
		            });  
		            // Close the Window when any button is clicked.
		            popupWindow.element.find(".k-edit-buttons button").click(function(){
		                // Detach custom event handlers to prevent memory leaks.
		                popupWindow.element.find(".k-edit-buttons button").off();
		                popupWindow.close();
		            }); 
					$('#editor-image-files').kendoUpload({
						async: {
							saveUrl: '<@spring.url "/data/images/0/upload.json?shared=true&objectType="/>' + community.data.Models.Announce.objectType , 
							autoUpload: true,
							multiple: false
						},
						validation: {
							allowedExtensions: [".jpg", ".jpeg", ".png", ".bmp", ".gif"]
						},
						success: function(e){ 
							if (e.operation == "upload") { 
								var response = e.response;  
								$.each(response.items, function(index, item ) { 
									listview.dataSource.add(item); 
								});
							}
		                },
		                showFileList: false,
		                dropZone: ".dropZoneElement"
					}); 
					var listview = $("#editor-image-listview").kendoListView({
						dataSource: { 
							data:[],
							schema: { model: community.data.model.Image }
						},
						selectable: "row",
						template: kendo.template($("#uploaded-image-template").html()),
						scrollable: true,
						height : 150
					}).data('kendoListView');
				}
				community.ui.window(renderTo2).center().open();  
			}
			var editor = renderTo.kendoEditor({ 
  				placeholder: "${PAGE_NAME} 내용을 입력하세요.",
  				height: 500,
  				resizable: {
					content: true,
	  				toolbar: true
				},
				tools: [
					"bold", "italic", "underline", "strikethrough", "justifyLeft", "justifyCenter", "justifyRight", "justifyFull", "createLink", "unlink", 
					{
	                    name: "customInsertImage",
	                    tooltip: "Insert Image",
	                    exec: onCustomInsertImageClick
	                },
	                "createTable", "addColumnLeft", "addColumnRight", "addRowAbove", "addRowBelow", "deleteRow", "deleteColumn",
					"foreColor", "backColor"
				],
            	paste: function(e){ 
            	 
            	},
            	change: function() {
            	
				}
			}).data('kenodEditor');
		}	
		return renderTo.data('kenodEditor');	
	}
	
	function createCodeEditor( renderTo, useWrapMode, mode ){
		mode = mode || "ace/mode/ftl", useWrapMode = useWrapMode || false; 
		if( renderTo.contents().length == 0 ){ 
			var editor = ace.edit(renderTo.attr("id"));		
			editor.getSession().setMode(mode);
			editor.setTheme("ace/theme/monokai");
			editor.getSession().setUseWrapMode( useWrapMode );
		}
		return ace.edit(renderTo.attr("id"));
	}
	
	function getCodeEditor(renderTo){
		return ace.edit(renderTo.attr("id"))
	} 

  </script>
  <style>
  	.k-editor { border : 0!important;} 
  </style>
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
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30" data-bind="text:page.name"></h1> 
          <!-- Content -->
          <div class="container-fluid"> 
			<div class="row"> 
				<div class="col-lg-10 g-mb-10">   
				<div class="card g-brd-gray-light-v7 g-brd-0 g-rounded-3 g-mb-30 g-min-height-500" data-bind="visible:visible" style="display:none;" >
                  <header class="card-header g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
                    <div class="media"> 
                      <a class="hs-admin-angle-left u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover" href="#" data-bind="click:back"></a>
                      <div class="media-body d-flex justify-content-end"> 
                      	<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:editable, click:edit" ></a>
                      	<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="visible:editable, click:cancle" ></a> 
                      	<a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#"></a>
                      </div>
                    </div>
                  </header>
                  <div class="card-block g-pa-15 g-pa-30--sm"> 
                  <form id="pageForm">
                  
							<div class="form-group g-mb-30">
	                    			<label class="g-mb-10 g-font-weight-600" for="input-page-name">이름 <span class="text-danger">*</span></label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      		<input id="input-page-name" name="input-page-name" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="파일명을 입력하세요" 
		                      			data-bind="value: page.name, enabled:editable" required validationMessage="이름을 입력하여 주세요." autofocus>
		                    			<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
		                    			이 페이지를 호출할때 사용되는 이름입니다. ex) /display/pages/<span data-bind="text: page.name"></span> 
		                    			</small>
		                    		</div>
		                    		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-page-name" role="alert" style="display:none;"></span>
	                  		</div>
							<div class="form-group g-mb-30">
	                    			<label class="g-mb-10 g-font-weight-600" for="input-page-name">패턴</label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      		<input id="input-page-name" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="패턴을 입력하세요" data-bind="value: page.pattern, enabled:editable">
		                    			<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">
		                    			패턴을 기반으로 페이지를 호출합니다. ex) /display/pages<span data-bind="text: page.pattern"></span> 
		                    			</small>
		                    		</div>
	                  		</div>	                  		
 							<div class="form-group">
	                    			<label class="g-mb-10 g-font-weight-600" for="input-page-title">페이지 타이틀 <span class="text-danger">*</span></label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      			<input id="input-page-title" name="input-page-title" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="파일명을 입력하세요" placeholder="페이지 타이틀을 입력하세요." 
		                      			data-bind="value: page.title, enabled:editable" required validationMessage="페이지 타이틀을 입력하여 주세요.">
		                    			<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">페이지를 제목으로 사용되는 이름입니다.</small>
		                    		</div>
		                    		<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-page-title" role="alert" style="display:none;"></span>
	                  		</div>  
							<div class="form-group">
			                   	<label class="g-mb-10 g-font-weight-600" for="input-page-description">설명</label>			
			                    <textarea id="input-page-description" class="form-control form-control-md g-resize-none g-rounded-4" rows="3" placeholder="간략하게 페이지에 대한 설명을 입력하세요." data-bind="value:page.description, enabled:editable"></textarea>
							</div> 
							<!-- Optional Setting --> 
							<div class="g-brd-around g-brd-gray-light-v7 g-rounded-4 g-pa-15 g-mb-15 g-bg-gray-light-v5">
							<div class="row g-mb-15" >
			            		<div class="col-md-4">
	                					<label class="g-mb-10 g-font-weight-600">OBEJCT TYPE</label>		
		                				<div class="form-group g-pos-rel g-rounded-4 mb-0">
					                    <input data-role="dropdownlist"
										data-option-label="서비스를 소유하는 객체 유형을 선택하세요."
										data-auto-bind="true"
										data-value-primitive="true"
										data-text-field="text"
										data-value-field="value"
										data-bind="value: page.objectType, enabled:editable, source: objectTypes"
										style="width: 100%;" /> 
									</div>
								</div>
								<div class="col-md-4">
	                				<label class="g-mb-10 g-font-weight-600">OBEJCT ID</label>		
		                			<div class="form-group g-pos-rel g-rounded-4 mb-0">
					                    <input data-role="numerictextbox" placeholder="OBJECT ID" 
					                    class="form-control form-control-md" type="number" data-min="-1" step="1"  data-format="##" data-bind="value:page.objectId, enabled:editable" style="width: 100%"/>
				                    </div>
		                		</div>		
			            		<div class="col-md-4">
									<div class="form-group">
										<label class="g-mb-10 g-font-weight-600">상태</label>
										<div class="g-pos-rel">
											<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
											<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
											</span> 
											<select class="form-control g-pos-rel" data-role="dropdownlist" data-bind="value:page.pageState,enabled:editable" style="width: 100%" class="form-control">
												<option value="INCOMPLETE">INCOMPLETE</option>
												<option value="APPROVAL">APPROVAL</option>
												<option value="PUBLISHED">PUBLISHED</option>
												<option value="REJECTED">REJECTED</option>
												<option value="ARCHIVED">ARCHIVED</option>
												<option value="DELETED">DELETED</option>
												<option value="NONE">NONE</option>
											</select>
										</div>
									</div>
		                		</div>		                						
							</div>
							<div class="row" >
			            		<div class="col-md-4">
									<div class="form-group">
										<label class="g-font-weight-600 g-mb-10" >버전<span class="text-danger">*</span></label>
										<div class="g-pos-rel">
											<input class="form-control form-control-md" data-role="numerictextbox" type="number"  data-format="###" data-min="0" step="1" data-max="100"
											placeholder="버전 정보를 입력하세요" data-bind="value: page.versionId,enabled:editable"  style="width: 100%" >
										</div>
									</div>
								</div>
								<div class="col-md-4">
									<div class="form-group">
										<label class="g-mb-10 g-font-weight-600">콘텐츠 유형</label>
										<div class="g-pos-rel">
											<input data-role="dropdownlist"
												data-option-label="콘텐츠 유형을 선택하세요."
												data-auto-bind="true"
												data-value-primitive="true"
												data-text-field="text"
												data-value-field="value"
												data-bind="value: page.contentType, enabled:editable, source: contentTypes"
												style="width: 100%;" /> 
										</div>
									</div>	
		                		</div>	
		            			<div class="col-md-4">
									<label class="d-flex align-items-center justify-content-between g-mb-15">
										<span class="g-pr-20 g-font-weight-500">인증필요</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="api-secured" value="true" data-bind="checked: source.secured,  enabled:editable" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label> 
									<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
									ON 상태인 경우 접근권한이 허용된 사용자만 이용할 수 있습니다.
									</small>
								</div>						
							</div>
							</div>		 			
							<!-- /.Optional Setting -->	 
 							<div class="form-group">
	                    		<label class="g-mb-10 g-font-weight-600" for="input-page-template">템플릿 (또는 redirect/forward 경로) <span class="text-danger">*</span></label>
		                    	<div class="g-pos-rel">
			                      	<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#!" data-bind="click:select, visible:editable" data-type="template">
			                      		<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-1 g-opacity-1--success"><i class="hs-admin-search g-absolute-centered g-font-size-default"></i></span>
									</a> 
		                      		<input id="input-page-template" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="템플릿으로 사용할 파일 경로를 입력하세요." data-bind="value: page.template, enabled:editable">
		                    	</div>
		                    	<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5">값이 지정되지 않으면 디폴트 템플릿이 적용됩니다. ex) redirect:/xxx/xx, forward:/xxx/xx</small>
	                  		</div> 
	 						<div class="form-group">
	                    		<label class="g-mb-10 g-font-weight-600" for="input-page-template">서버 스크립트<span class="text-danger">*</span></label>
		                    	<div class="g-pos-rel">
		                    		<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-sec	ondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="#!" data-bind="click:select" data-type="script">
			                      		<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-1 g-opacity-1--success"><i class="hs-admin-search g-absolute-centered g-font-size-default"></i></span>
									</a> 
		                      		<input id="input-page-script" class="form-control form-control-md g-rounded-4 g-px-14 g-py-10" type="text" placeholder="서버 스크립트 파일 경로를 입력하세요" data-bind="value: page.script, enabled:editable">
		                    	</div>	
	                  		</div>
	                  		<div class="form-group">
								<a href="javascript:void(this);" class="btn btn-md u-btn-3d u-btn-blue g-rounded-50 g-mr-10 g-mb-15" data-bind="click:wizard">새로운 서버 스크립트 만들기</a> 
							</div> 


							<ul class="nav nav-pills mb-3" id="editors-tab" role="tablist">
							  <li class="nav-item">
							    <a class="nav-link active" id="editors-wysiwyg-tab" data-toggle="pill" href="#editors-wysiwyg" role="tab" aria-controls="editors-wysiwyg" aria-selected="true">글쓰기</a>
							  </li>
							  <li class="nav-item">
							    <a class="nav-link" id="editors-html-tab" data-toggle="pill" href="#editors-html" role="tab" aria-controls="editors-html" aria-selected="false">HTML</a>
							  </li>
							</ul>
							<div class="tab-content" id="editors-tabContent">
							  <div class="tab-pane fade show active" id="editors-wysiwyg" role="tabpanel" aria-labelledby="editors-wysiwyg-tab">
							  	<textarea id="editor" name="Text" aria-label="editor" data-bind="value:page.bodyContent.bodyText, enabled:editable" style="height:600px;"></textarea>
							  </div>
							  <div class="tab-pane fade" id="editors-html" role="tabpanel" aria-labelledby="editors-html-tab">
							  <!-- html editor -->
								<header class="card-header g-brd-gray-light-v7 g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm" data-bind="visible:visible" style="">
			                    <div class="media">
			                      <h3 class="d-flex align-self-center g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0 g-font-weight-300">
			                      </h3> 
			                      <div class="media-body d-flex justify-content-end" data-bind="invisible:folder" style="display: none;">   
									<label class="d-flex align-items-center justify-content-between g-mb-0">
										<span class="g-pr-20 g-font-weight-300">에디터 줄바꿈 설정/해지</span>
										<div class="u-check">
											<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="useWarp" value="true" data-bind="checked: editor.warp" type="checkbox">
											<div class="u-check-icon-radio-v8">
												<i class="fa" data-check-icon=""></i>
											</div>
										</div>
									</label>
			                      </div> 
			                    </div>
								</header>
								<div id="htmleditor" style="height:500px;" class="g-brd-gray-light-v7 g-brd-1"></div> 
							  <!-- end html editor -->
							  </div>
							</div>						 						 
						 	<div class="row" data-bind="visible: editable">
			                      <div class="col-md-9 ml-auto text-right g-mt-15">  
			                      	<button class="btn btn-md btn-xl--md u-btn-outline-primary g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:delete" type="button">삭제</button> 
			                        <button id="showToast" class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:saveOrUpdate" type="button">저장</button>
			                        <button id="clearToasts" class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md g-mb-10" type="button" data-bind="click:cancle" >취소</button>
			                      </div>
			                    </div>								 						 

                  </form>
                  </div>
                </div>  
                </div>
                <!-- side menu -->
				<div class="g-brd-left--lg g-brd-gray-light-v4 col-lg-2 g-mb-10 g-mb-0--md"> 
					<section data-bind="invisible:isNew" style="display:none;"> 
						<a href="javascript:void(this);" class="btn btn-md u-btn-outline-red g-rounded-50 g-mr-10 g-mb-15" data-bind="click:preview" >미리보기</a> 
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
  <#include "includes/resource-select-window.ftl"> 
	<div id="insert-image-window" style="display:none; height:530px;" >
		<div class="k-content wide g-mt-50">
		<div class="u-file-attach-v3 g-mb-15 dropZoneElement">
			<h3 class="g-font-size-16 g-color-gray-dark-v2 mb-0">Drop files here to upload</h3>
			<p class="g-font-size-14 g-color-gray-light-v2 mb-0">Maximum file size 10mb</p>
		</div>
		</div>
		<input name="files" id="editor-image-files" type="file" />  
		<div id="editor-image-listview" class="g-mt-5 image-listview"></div> 
		<div class="card-block text-right">
			<a href="javascript:void(this);" class="btn g-rounded-50 btn-md u-btn-lightred g-mr-5" data-action="insert" role="button" >이미지 삽입</a>
			<a href="javascript:void(this);" class="btn g-rounded-50 btn-md u-btn-outline-bluegray" data-action="close" role="button">취소</a>
		</div>
	</div>   
	<script type="text/x-kendo-template" id="uploaded-image-template">
		<div class="uploaded-image"> 
			<img src="/download/images/#= imageLink.linkId #?thumbnail=true"  class="g-width-110 img-fluid g-ma-5" />
			<h3>#: name #</h3>
			<span class="u-icon-v1 g-color-primary"><i class="icon-check"></i></span> 
		</div>	
	</script>   
</body>
</html>
</#compress>