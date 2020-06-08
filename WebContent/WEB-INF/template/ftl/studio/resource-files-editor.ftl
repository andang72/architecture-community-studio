<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "파일" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	 
  <#assign KENDO_VERSION = "2019.3.917" />
  <#assign MAX_UPLOAD_SIZE = "40" />
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
    var __attachmentId = <#if RequestParameters.attachmentId?? >${RequestParameters.attachmentId}<#else>0</#if>;	
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
	        "hs.markup-copy" 				: { "deps" :['jquery', 'hs.core'] }, 
	        
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
			"hs.markup-copy" 				: "<@spring.url "/assets/unify/2.6.2/js/components/hs.markup-copy"/>",
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
			"ace" 							: "<@spring.url "/js/ace/ace" />",
			"clipboard.min" 				: "<@spring.url "/assets/unify/2.6.2/vendor/clipboard/dist/clipboard.min" />",
			"dropzone"						: "<@spring.url "/js/dropzone/dropzone"/>",
			"imagesloaded"					: "<@spring.url "/js/imagesloaded/imagesloaded.pkgd.min"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom",  "ace", "dropzone", "imagesloaded" ], 
  		function($, Clipboard) {  
  	  console.log("START SETUP APPLICATION.");	
  	  
  	  community.ui.studio.setup();	
  	   
  	  var observable = new community.data.observable({ 
		currentUser : new community.data.model.User(),
		attachment : new community.data.model.Attachment(),
		visible : false,
		editable : true,
		isNew : true, 
		linked : false,
		linkedUrl : null,
		imageUrl : '<@spring.url "/images/no-image.jpg"/>',
		editor : { warp : false },
		objectTypes : [
			{ text: "정의되지 않음", value: "-1" },
			<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
			<#if item.DISPLAY_NAME?? >
			{ text:'${item.DISPLAY_NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
			</#if>
			</#list>
		],
		preview : function(){
			var $this = this; 
			if( $this.attachment.contentType.endsWith('pdf')  ){
				community.ui.send('<@spring.url "/secure/studio/resource-pdf-viewer" />', { preview : true , link : $this.attachment.sharedLink.linkId } , 'GET', '_blank'  );
			}else if ( $this.attachment.contentType.endsWith('mp3')  ){ 
				createPrevieWindow($this); 
			} 
		},
		back: function(){
			window.history.back();
		},
		edit: function(){
			var $this = this;
			$this.set('editable', true); 
		},
		cancle: function(){
			var $this = this;
			if( $this.get('isNew') ){
				// or back..
				$('#pageForm')[0].reset();
			}else{
				// or back..
				$this.set('editable', false);
				$this.load($this.attachment.attachmentId); 
			}
		},
		refresh: function(){
			var $this = this;
			$this.load($this.attachment.attachmentId);
		},
		load: function(objectId){
			var $this = this;
			if( objectId > 0 ){
				community.ui.progress($('#features'), true);	
				community.ui.ajax('<@spring.url "/data/secure/mgmt/attachments/"/>' + objectId + '/get.json?fields=link', {
					contentType : "application/json",
					success: function(data){	
						$this.setSource( new community.data.model.Attachment(data) );
					}	
				}).always( function () {
					community.ui.progress($('#features'), false);
				});	
			}else{
				$this.setSource(new community.data.model.Attachment()); 
			}	
		},	 
		property : function(e){
			var $this = this;
			createPropertyWindowIfNotExist(community.data.Models.Attachment, $this.attachment.attachmentId );
		},
		permissions : function(e){
			var $this = this;
			createPermissionsWindowIfNotExist(community.data.Models.Attachment, $this.attachment.attachmentId); 
		},
		setSource : function( data ){
			var $this = this ;	  
			if( data.get('attachmentId') > 0 ){
				data.copy( $this.attachment );
				$this.set('editable', false );
				$this.set('isNew', false ); 
				$this.set('imageUrl', community.ui.studio.getFileUrl (data, {thumbnail:true, width: 500 , height: 500}) );  
  	  			var url = community.ui.studio.getFileUrl ($this.attachment);
  	  			console.log( url ); 
        		$this.setLinkUrl();
			}else{
				$this.set('editable', true );	
				$this.set('isNew', true );
			}
			if( !$this.get('visible') ) 
				$this.set('visible' , true );
				
			createCommentListView( null, $this.attachment.attachmentId ); 	
		},
		setLinkUrl: function(){
			var $this = this;
			if( $this.attachment.sharedLink != null && $this.attachment.sharedLink.linkId != null ){
				$this.set('linked', true );
				$this.set('linkedUrl', '<@spring.url "/download/files/" />' + $this.attachment.sharedLink.linkId );
			}else{ 
				$this.set('linked', false );
				$this.set('linkedUrl' , null );
			}
		},
		getLink : function(){ 
					var $this = this;
					if( $this.attachment.attachmentId > 0 ){
						community.ui.progress($('#features'), true);
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/attachments/" />' + $this.attachment.attachmentId + '/link.json?create=true', {
							data: community.ui.stringify($this.menu),
							contentType : "application/json",
							success : function(response){
								$this.set('attachment.sharedLink', new community.data.model.SharedLink( response )) ;
								$this.setLinkUrl();
							}
						}).always( function () {
							community.ui.progress($('#features'), false);
						});	
					}	
					return false;
		},	
		delete : function(e){
			var $this = this; 
			var template = community.data.template('"#: attachment.name #"를 영구적으로 제거합니다.<br/> 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '${PAGE_NAME}을 삭제하시겠습니까?',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){   
                		community.ui.progress($('#features'), false); 
                		community.ui.ajax( '<@spring.url "/data/secure/mgmt/attachments/" />'+ $this.attachment.attachmentId +'/delete.json', {
							data: community.ui.stringify({}),
							contentType : "application/json",
							success : function(response){ 
								dialog.close();
								community.ui.notify( "${PAGE_NAME} 이 삭제되었습니다.");
								community.ui.send('<@spring.url "/secure/studio/resource-files"/>');
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
		},
		clearCache : function(e){
			var $this = this; 
			community.ui.progress($('#features'), false); 
            community.ui.ajax( '<@spring.url "/data/secure/mgmt/attachments/" />'+ $this.attachment.attachmentId +'/refresh.json', {
				data: community.ui.stringify({}),
				contentType : "application/json",
				success : function(response){ 
					community.ui.notify( "${PAGE_NAME} 이 캐쉬가 삭제되었습니다.");
					$this.refresh();
				}
			}).always( function () {
				community.ui.progress($('#features'), false); 
			});  
		},
		saveOrUpdate : function(e){ 
			var $this = this;
			var validator = community.ui.validator($("#pageForm"), {});
				community.ui.progress($('#features'), true);	
				community.ui.ajax( '<@spring.url "/data/secure/mgmt/attachments/save-or-update.json" />', {
					data: community.ui.stringify($this.attachment),
					contentType : "application/json",
					success : function(response){
						$this.refresh();
					}
				}).always( function () {
					community.ui.progress($('#features'), false); 
				});	 
		}
	  });	 
	  community.data.bind( $('#features') , observable );   
	  observable.load(__attachmentId);  
	  createAttachmentDropzone(observable);
  	  console.log("END SETUP APPLICATION.");	 
  	  
  	});	
	
	
	function createPrevieWindow ( observable ){ 
		var renderTo = $('#preview-window');
		if( !community.ui.exists( renderTo )){ 
			var window = community.ui.window(renderTo, {
					width: "600px",
					height: "500px",
					title: false,
					visible: false, 
					scrollable : true,
					modal: true, 
					actions: [ "Close"],
					open: function(){	   
						if( !community.ui.exists( $("#mediaplayer") ) ){
							var source = community.ui.studio.getFileUrl (observable.attachment); 
							$("#mediaplayer").kendoMediaPlayer({
				                autoPlay: true,
				                navigatable: true,
				                media: {
				                    title: observable.attachment.name,
				                    source: source
				                }
				            }); 
				            
				            //var bgImgUrl = 'url("/images/bg/play-music-02.jpg")';
				            var bgImgUrl = 'url("' + observable.imageUrl + '")';
				            $("#mediaplayer .k-mediaplayer-overlay").css('background-image', bgImgUrl );
				            $("#mediaplayer .k-mediaplayer-overlay").css('background-size', 'cover');
						} 
					},
					close: function(){
						try{
							$("#mediaplayer").data('kendoMediaPlayer').stop();
							//$("#mediaplayer").html('');
						}catch(err){}
					}
			});   
			window.wrapper.addClass("zIndexEnforce"); 
			community.data.bind(renderTo, observable); 
			renderTo.on( "click", "a[data-action=close]", function(e){		
				var $this = $(this);	
				window.close();	
			});	 
		}							
		community.ui.window(renderTo).center().open();
	}
	
	function createAttachmentDropzone( observable ){	
		var renderTo = '#attachment-file-dropzone' ;	 
		var myDropzone = new Dropzone( renderTo, {
			url: '<@spring.url "/data/secure/mgmt/attachments/upload.json"/>',
			paramName: 'file',
			maxFiles: 1,
			maxFilesize: ${MAX_UPLOAD_SIZE},
			previewsContainer: renderTo + ' .dropzone-previews'	,
			previewTemplate: '<div class="dz-preview dz-file-preview"><div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div></div>'
		}); 
		myDropzone.on("sending", function(file, xhr, formData) {
			formData.append("objectType", observable.attachment.objectType);
			formData.append("objectId", observable.attachment.objectId);
			formData.append("attachmentId", observable.attachment.attachmentId);
		});	 
		myDropzone.on("success", function(file, response) {
			file.previewElement.innerHTML = "";
			$.each( response, function( index , item  ) {
		    	observable.attachment.attachmentId = item.attachmentId;
		    	observable.set('attachment.name', item.filename);
		    	observable.set('isNew', false );
		    	observable.load( item.attachmentId );
			});
		});
		myDropzone.on("error", function(file, message, xhr) {
			console.log(message);
			if(message.startsWith("File is too big") ){
				community.ui.notify( "업로드 가능한 최대 크기 ${MAX_UPLOAD_SIZE}MB 를 초과하였습니다.", "error");
			}
			this.removeFile(file);
		});	  
		myDropzone.on("addedfile", function(file) {
			community.ui.progress($('#features'), true);
			console.log( "file added" );
		});		
		myDropzone.on("complete", function() {
			community.ui.progress($('#features'), false);
			console.log( "complete" );
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
                      	<a class="hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-ml-20" href="#" data-bind="invisible:isNew, click:refresh"></a>
                      </div>
                    </div>
                  	</header>  
					<div class="row g-my-25">
						<div class="col-md-6"> 
							<figure class="g-pos-rel">
								<img class="img-fluid g-rounded-5" data-bind="attr:{src: imageUrl }, invisible:isNew" alt="" src="/images/no-image.jpg" style="max-height:266px;">
							</figure> 	
						</div>
						<div class="col-md-6">
							<!-- Dropdown -->	
							<form action="" method="post" enctype="multipart/form-data" id="attachment-file-dropzone" class="u-dropzone u-file-attach-v3 g-mb-15 dz-clickable">
								<div class="dz-default dz-message">
									<p>
									<img src="data:image/png;base64,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">
									</p>
									<h3 class="g-font-size-16 g-font-weight-400 g-color-gray-dark-v2 mb-0">업로드할 파일은 이곳에 드레그 <span class="g-color-primary">Drag &amp; Drop</span> 하여 놓아주세요. 또는 클릭하여 파일을 선택하여 주세요.</h3>
									<p class="g-font-size-14 g-color-gray-light-v2 mb-0">최대파일 크기는 ${MAX_UPLOAD_SIZE}MB 입니다.</p>
								</div>
								<div class="dropzone-previews"></div>
							</form>	
							<!-- End Dropdown -->
						</div>
					</div>
					
					<form id="pageForm">
					<div class="row g-mb-30" >
						<div class="col-md-6">
	                					<label class="g-mb-10 g-font-weight-600">OBEJCT TYPE</label>		
		                				<div class="form-group g-pos-rel g-rounded-4 mb-0">
					                    <input data-role="dropdownlist"
										data-option-label="서비스를 소유하는 객체 유형을 선택하세요."
										data-auto-bind="true"
										data-value-primitive="true"
										data-text-field="text"
										data-value-field="value"
										data-bind="value: attachment.objectType, enabled:editable, source: objectTypes"
										style="width: 100%;" /> 
									</div>
						</div>
						<div class="col-md-6">
	                				<label class="g-mb-10 g-font-weight-600">OBEJCT ID</label>		
		                			<div class="form-group g-pos-rel g-rounded-4 mb-0">
					                    <input data-role="numerictextbox" placeholder="OBJECT ID" 
					                    class="form-control form-control-md" type="number" data-min="-1" step="1"  data-format="###" data-bind="attachment.objectId, enabled:editable" style="width: 100%"/>
				                    </div>
						</div>	 
					</div>
					<section data-bind="invisible:isNew" >
					<label class="g-mb-10 g-font-weight-600">${PAGE_NAME} 링크</label> 
					<div class="g-pos-rel g-mb-20 ">
					
					<button class="btn u-input-btn--v1 g-width-80 g-color-white g-bg-primary g-rounded-right-4" type="button" data-bind="invisible:linked, click: getLink" style="display: none;">
					링크생성
					</button>
					
					<input class="form-control form-control-md g-brd-gray-light-v7 g-brd-gray-light-v3 g-rounded-4 g-px-14 g-py-10" type="text" placeholder="링크" data-bind="value:attachment.sharedLink.linkId,enabled:false" disabled="disabled">
					<em class="d-flex align-items-center g-absolute-centered--y g-right-15 g-font-style-normal g-color-gray-dark-v11">
					<span class="g-pos-rel g-width-18 g-height-18 g-bg-secondary g-brd-around g-brd-secondary rounded-circle" data-bind="visible: linked" style="">
					<i class="hs-admin-check g-absolute-centered g-font-weight-800 g-font-size-8 g-color-white" title="Confirmed"></i>
					</span>
					</em>
					</div>
					<div class="row g-mb-20">
                      <div class="col-md-12 align-self-center"> 
						<div class="media-md align-items-center g-bg-gray-light-v8 g-rounded-4 g-px-20 g-py-10" data-bind="visible:linked">
	                    <span id="copyLink" class="d-inline-block d-md-flex w-100 g-width-auto--md g-text-overflow-ellipsis g-color-gray-dark-v6 g-mb-5 g-mb-0--md" data-bind="text:linkedUrl"></span>
	                    <a class="js-copy d-flex align-items-center u-link-v5 g-color-secondary g-color-primary--hover ml-auto" href="#" data-content-target="#copyLink" data-success-text="Copied">
	                      <i class="hs-admin-link g-mr-8"></i>
	                      Copy Link
	                    </a>
						</div> 
                      </div>
                    </div>
		          							          							          					
					<div class="form-group g-mb-15">
						<label class="g-mb-10 g-font-weight-600" for="input-attachment-name">이름 <span class="text-danger">*</span></label>
		                    		<div class="g-pos-rel">
			                      	<span class="g-pos-abs g-top-0 g-right-0 d-block g-width-40 h-100 opacity-0 g-opacity-1--success">
				                  		<i class="hs-admin-check g-absolute-centered g-font-size-default g-color-lightblue-v3"></i>
				                		</span>
		                      		<input id="input-attachment-name" name="input-attachment-name" class="form-control form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-14 g-py-10" type="text" placeholder="파일명을 입력하세요" 
		                      			data-bind="value: attachment.name, enabled:editable" required validationMessage="이름을 입력하여 주세요." autofocus>
		                    		</div>
						<span class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" data-for="input-attachment-name" role="alert" style="display:none;"></span>
	                </div>
	                
	                <div class="row g-mb-15" >
	                	<div class="col-md-6">
							<div class="form-group g-mb-30">
	                    		<label class="g-mb-10 g-font-weight-600" for="input-attachment-contentType">콘텐츠 타입</label>
		                    	<div class="g-pos-rel">
		                    		<input  class="form-control h-100 form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-20 g-py-12" type="text" data-bind="value:attachment.contentType, enabled:false" /> 
			                    </div>
							</div>			
						</div>
						<div class="col-md-6">
							<div class="form-group g-mb-30">
							<label class="g-mb-10 g-font-weight-600" >크기 (bytes)</label>
							<div class="g-pos-rel">
								<input  class="form-control h-100 form-control-md g-brd-gray-light-v7 g-brd-lightblue-v3--focus g-rounded-4 g-px-20 g-py-12" type="text" data-bind="value:attachment.size, enabled:false" data-format="##,#" />
							</div>
							</div>
						</div>		
					</div>
					
					<div class="row g-mb-15" >
	                  			<div class="col-md-6">
									<label class="g-mb-10 g-font-weight-600">생성일</label>
									<div class="g-pos-rel">
									<span data-bind="text:attachment.creationDate" data-format="yyyy.MM.dd HH:mm"/>
									</div> 
								</div>
								<div class="col-md-6">
									<label class="g-mb-10 g-font-weight-600">수정일</label>
									<div class="g-pos-rel">
									<span data-bind="text:attachment.modifiedDate" data-format="yyyy.MM.dd HH:mm"/>
									</div> 
								</div>
	                </div>
					<div class="row" data-bind="visible:editable">
						<div class="col-md-9 ml-auto text-right g-mt-15"> 
							<button class="btn btn-md btn-xl--md u-btn-outline-primary g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="visible:editable, click:delete" type="button">삭제</button>
		                        <button id="showToast" class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10 g-mb-10" data-bind="click:saveOrUpdate" type="button">저장</button>
		                        <button id="clearToasts" class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md g-mb-10" type="button" data-bind="click:cancle" >취소</button>
						</div>
					</div>
					</section>
					</form>
				</div> 
				<#if RequestParameters.attachmentId?? > 
				<#assign COMMENT_OBJECT_TYPE = 10 />	
			    <#assign COMMENT_OBJECT_ID = ServletUtils.getStringAsLong(RequestParameters.attachmentId) />
				<#include "includes/comments.ftl">
				</#if>				
				</div> 
				<!-- side menu -->
				<div class="g-brd-left--lg g-brd-gray-light-v4 col-lg-2 g-mb-10 g-mb-0--md"> 
					<section data-bind="invisible:isNew" style="display:none;"> 
						<a href="javascript:void(this);" class="btn btn-md u-btn-outline-red g-mr-10 g-rounded-50 g-mb-15" data-bind="click:preview" >미리보기</a>  
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
							<li class="g-brd-top g-brd-gray-light-v7 mb-0 ms-hover">
								<a class="d-flex align-items-center u-link-v5 g-parent g-py-15" href="#!" data-bind="click: clearCache">
									<span class="g-font-size-18 g-color-gray-light-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active g-mr-15">
									<i class="hs-admin-trash"></i>
									</span>
									<span class="g-color-gray-dark-v6 g-color-lightred-v3--parent-hover g-color-lightred-v3--parent-active">캐쉬 삭제</span>
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
  <div id="preview-window" style="display:none;" class="g-brd-0 g-bg-gray-dark-v1">
		<a class="g-pos-abs g-right-10 g-top-10 u-link-v5 g-font-size-40 g-font-size-40--md g-color-gray-light-v6 g-color-gray-dark-v1--hover" href="javascript:void(this);"  data-action="close" role="button" ><i class="hs-admin-close"></i></a>
		<div class="g-mt-50">
		<div id="mediaplayer" style="height:360px; border:0px;"></div> 
		</div> 
  </div>
  <#include "includes/permissions-and-properties.ftl">
  
</body> 
</html>
</#compress>