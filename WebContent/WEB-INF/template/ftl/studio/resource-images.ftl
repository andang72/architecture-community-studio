<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "이미지" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	  
  <#assign KENDO_VERSION = "2019.3.917" />
  <#assign MAX_FILE_SIZW = 20 />
  <title>STUDIO :: ${PAGE_NAME} </title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">
  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/jquery.fancybox/jquery.fancybox.min.css"/>"> 
  
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
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min',  'kendo.messages', 'community.ui.data' , 'community.ui.core' ] }
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
			"community.ui.core"	 			: "<@spring.url "/js/community.ui/community.ui.core"/>",
			"dropzone"						: "<@spring.url "/js/dropzone/dropzone"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom", "dropzone", "jquery.fancybox" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  community.ui.studio.setup();	
  	  var observable = new community.data.observable({ 
  	  	selected :false,
  	  	getTagsWindow : function(e){
  	  		getTagsWindow(this);
  	  	},
  	  	delete : function (e){ 
			var $this = this; 
			var selected = community.ui.grid($('#pages-grid')).dataSource.view();
			var template = community.data.template('"선택한 이미지를 영구적으로 제거합니다.<br/> 이동작은 최소할 수 없습니다.');
			var dialog = community.ui.dialog( null, {
				title : '이미지를 삭제하시겠습니까?',
				content :template($this),
				actions: [
                { text: '확인', 
                	action: function(e){   
                		community.ui.progress($('.k-dialog'), true); 
                		community.ui.ajax( '<@spring.url "/data/secure/mgmt/images/delete.json" />', {
							data: community.ui.stringify( selected ),
							contentType : "application/json",
							success : function(response){ 
								dialog.close();
								community.ui.notify( response.count + "개 이미지가 삭제되었습니다.");
								community.ui.grid($('#pages-grid')).dataSource.read();
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
  	  createImagesGrid(observable); 
  	  community.data.bind( $('#features') , observable );
  	  console.log("END SETUP APPLICATION.");	
  	});
 
 	function getTagsWindow( observable ){ 
  		var renderTo = $('#tags-window');
  		if( !community.ui.exists( renderTo )){ 
  			var observable2 = community.data.observable({ 
  				selectedImageCount : 0,
  				selectedTags : null,
  				tagText : null,
  				tags : community.ui.datasource({
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/services/tags/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"}
					},
					schema: {
						total: "totalCount",
						data:  "items",
						model: {
							id : "tagId",
							fields :{
								tagId: { type: "number", defaultValue: 0 },
								name: {  type: "string", defaultValue: null }
							}
						}
					}
				}),
				addTags : function(e){
					var $this = this;  
					community.ui.progress($('.k-window'), true );
					community.ui.ajax('<@spring.url "/data/secure/mgmt/services/tags/0/objects/add.json"/>', {
							data : community.ui.stringify({ objectType : community.data.Models.Image.objectType, objectIds : community.ui.grid($('#pages-grid')).selectedKeyNames(), tagIds : $this.selectedTags } ),
							contentType : "application/json", 
							success: function(response){ 
								community.ui.notify( "태그가 추가되었습니다.");
							}	
					}).always( function () {
						community.ui.progress($('.k-window'), false );
						window.close();
					}); 
										
				},
				createTag : function(e){ 
					var $this = this; 
					community.ui.progress($('.k-window'), true );
					community.ui.ajax('<@spring.url "/data/secure/mgmt/services/tags/0/create.json"/>', {
							data : community.ui.stringify({ tagId:0, name : $this.get('tagText') }),
							contentType : "application/json", 
							success: function(response){ 
								community.ui.notify( "태그가 추가되었습니다.");
							}	
					}).always( function () {
						community.ui.progress($('.k-window'), false );
						$this.set('tagText', null);
						$this.tags.read();
					}); 
				}
  			});
  			
  			
  			var window = community.ui.window( renderTo, {
				width: "500px",
				title: "태그",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){ 
					var selectedImages = community.ui.grid($('#pages-grid')).selectedKeyNames();
  					observable2.set('selectedImageCount', selectedImages.length );
  					observable2.set('tagText', null);
				},
				close: function(){ 
				}
			});  
			community.data.bind(renderTo, observable2);
  		}
  		community.ui.window( renderTo ).center().open();
  	}
  	
  	
  	function getUploadWindow( ){ 
  		var renderTo = $('#upload-window');
  		if( !community.ui.exists( renderTo )){ 
			var observable = community.data.observable({ 
				shared : false,
				objectType : 0,
				objectId : 0,
				url : null,
				imageUrl : '<@spring.url "/images/no-image.jpg"/>',
				enabled : false,
				preview : function(){
					var $this = this;
					if( $this.get('url')!=null ){
						$.get($this.get('url')).done(function() { 
							$this.set('imageUrl', $this.get('url'));
						}).fail(function() { 
							$this.set('imageUrl', $this.get('url'));
						}); 
						$this.set('enabled', true );
					}
				},
				uploadByUrl : function(){
					var $this = this;
					community.ui.ajax( 
						'<@spring.url "/data/secure/mgmt/images/upload_by_url.json" />', {
							data: community.ui.stringify({ objectType : $this.get('objectType') , objectId : $this.get('objectId'), imageUrl : $this.get('url') }),
							contentType : "application/json",
							success : function(response){
							}
						}
					).always( function () {
						$this.set('imageUrl', '<@spring.url "/images/no-image.jpg"/>' );
						$this.set('url', null);
						$this.set('enabled',  false );			
					});	
				},
				objectTypes : [
					{ text: "정의되지 않음", value: "-1" },
					{ text: "앨범", value: "40" },
					<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
					<#if item.DISPLAY_NAME ?? >
					{ text:'${item.DISPLAY_NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
					</#if>
					</#list>
					
				],
			}); 
			createImageDropzone(observable); 
			var window = community.ui.window( renderTo, {
				width: "700px",
				title: "파일 업로드",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){
					observable.set('shared', false);
					observable.set('objectType', 0);
					observable.set('objectId', 0);
				},
				close: function(){ 
				}
			});  
			community.data.bind(renderTo, observable);
  		}
  		community.ui.window( renderTo ).center().open();
  	}
  	
	function createImageDropzone( observable ){	 
		var renderTo = '#image-file-dropzone' ;	 
		// image dorpzone
		var myDropzone = new Dropzone( renderTo, {
			url: '<@spring.url "/data/secure/mgmt/images/upload.json"/>',
			paramName: 'file',
			parallelUploads : 1,
			maxFilesize : ${MAX_FILE_SIZW},
			previewsContainer: renderTo + ' .dropzone-previews'	,
			previewTemplate: '<div class="dz-preview dz-file-preview"><div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div></div>'
		});
		
		myDropzone.on("sending", function(file, xhr, formData) {
			formData.append("objectType", observable.objectType);
			formData.append("objectId", observable.objectId);
		});	 
		myDropzone.on("success", function(file, response) {
			file.previewElement.innerHTML = "";
			$.each( response, function( index , item  ) {
		    	community.ui.notify( item.name + "이 업로드 되었습니다.");
			});
			refresh();
		}); 
		myDropzone.on("maxfilesexceeded", function() {
			console.log( "maxfilesexceeded" );
		});	
		myDropzone.on("addedfile", function(file) {
			community.ui.progress($('#features'), true);
			console.log( "file added" );
		});		
		myDropzone.on("complete", function() {
			community.ui.progress($('#features'), false);
		});			
	}
	
	
    function createImagesGrid(observable){
    	var renderTo = $('#pages-grid');
		if( !community.ui.exists(renderTo) ){  
			community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/images/list.json?fields=imageLink,tags"/>', type:'post', contentType: "application/json; charset=utf-8"},
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
					schema: {
						total: "totalCount",
						data:  "items",
						model: community.data.model.Image
					}
				}, 
				toolbar: [{ name: "create" , text: "새로운 이미지 업로드하기", template:community.ui.template( $('#grid-toolbar-template').html() )  }],
				sortable: true,
				persistSelection: true,
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
                dataBound: function() {  
			    	$(".fancybox").fancybox({
			    		protect: true
					});
			    },
				columns: [
				{ selectable: true, width: "50px", headerAttributes: {} },
				{ field: "IMAGE_ID", title: "ID", filterable: false, sortable: true , width : 80 , template:'#= imageId #', attributes:{ class:"text-center" }}, 
				{ field: "OBJECT_TYPE", title: "OBJECT TYPE", filterable: true, sortable: true , width : 120 , template:'#= objectType #', attributes:{ class:"text-center" }}, 
				{ field: "OBJECT_ID", title: "OBJECT ID", filterable: true, sortable: true , width : 120 , template:'#= objectId #', attributes:{ class:"text-center" }}, 
				{ field: "FILE_NAME", title: "이미지", filterable: true, sortable: true, template:$('#name-column-template').html() },  
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
  					community.ui.send("<@spring.url "/secure/studio/resource-images-editor" />", { imageId: objectId });
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
			
				<div class="row"> 
					<div id="pages-grid" class="g-brd-top-0 g-brd-left-0  g-brd-right-0 g-mb-1"></div> 
					<ul class="list-inline g-font-size-13 g-py-13 mb-0">
						<li class="list-inline-item g-color-gray-dark-v4 mr-2">
							<button class="btn btn-md u-btn-blue g-rounded-50 g-mr-10 g-mb-15" data-bind="visible: selected, click:getTagsWindow" style="display:none;"><i class="hs-admin-tag g-mr-5"></i> 선택된 이미지에 태그 추가</button>
						</li>
						<li class="list-inline-item g-color-gray-dark-v4 mr-2">
							<button class="btn btn-md u-btn-pink g-rounded-50 g-mr-10 g-mb-15" data-bind="visible: selected, click: delete" style="display:none;"><i class="hs-admin-trash g-mr-5"></i> 선택된 이미지 삭제 </button>
						</li>
					</ul>					
				</div>
			</div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
	
	<div id="tags-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" >
		<div class="g-pa-15">
			<p class="g-pb-15">
			<span data-bind="text:selectedImageCount"></span> 개의 이미지가 선택되었습니다. 
			선택된 이미지들에 태크를 추가합니다.
			</p>
			
			<select data-role="multiselect"
                   data-placeholder="태그를 선택하세요."
                   data-value-primitive="true"
                   data-text-field="name"
                   data-value-field="tagId"
                   data-bind="value: selectedTags, source: tags"></select> 
        
        	<div class="input-group g-mt-25">
				<input type="text" class="form-control g-brd-gray-light-v7 g-brd-gray-light-v3 g-px-14 g-py-10" placeholder="목록에 없는 경우 새로운 태그를 입력하세요." aria-label="태그를 입력하세요." data-bind="value:tagText">
				<div class="input-group-append"> 
					<button class="btn u-btn-primary g-width-80 g-brd-0 g-rounded-right-4" type="button" data-bind="click:createTag">태그 만들기</button>
				</div>
			</div> 
        	<hr class="g-brd-gray-light-v4 g-mx-minus-15"> 
        	<div class="text-right g-mt-25">
				<a href="javascript:void(this);" class="btn btn-md u-btn-blue g-rounded-50 g-mr-10" data-bind="click:addTags">태그 추가하기</a>
			</div>
        </div> 
	</div>
	
	<div id="upload-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" > 
	<div class="g-pa-15 g-pa-30--sm"> 
		<div class="row no-gutters">
			<div class="col-sm-6">
				<div class="form-group">
					<label class="g-mb-10 g-font-weight-600">OBEJCT TYPE</label>		
					<div class="form-group g-pos-rel g-rounded-4 mb-0 g-mr-10">
						<input data-role="dropdownlist"
											data-option-label="서비스를 소유하는 객체 유형을 선택하세요."
											data-auto-bind="true"
											data-value-primitive="true"
											data-text-field="text"
											data-value-field="value"
											data-bind="value:objectType, source: objectTypes"
											style="width: 100%;" /> 
					</div> 
	  			</div> 
	  		</div>
	  		<div class="col-sm-6">
			  	<div class="form-group">
					<label class="g-mb-10 g-font-weight-600">OBEJCT ID</label>		
					<div class="form-group g-pos-rel g-rounded-4 mb-0 g-mr-10">
						<input data-role="numerictextbox" placeholder="OBJECT ID" class="form-control form-control-md" type="number" data-min="0" step="1" data-bind="value:objectId" style="width: 100%"/>
					</div>	
		  		</div> 
	  		</div>
	  	</div>
	  	<div class="row no-gutters">	
	  		<div class="form-check g-mt-20">
				<label class="d-flex align-items-center justify-content-between g-mb-0">
					<span class="g-pr-20 g-font-weight-500">공유여부</span>
					<div class="u-check">
						<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="api-enabled" id="api-enabled" value="true" data-bind="checked: shared" type="checkbox">
						<div class="u-check-icon-radio-v8">
							<i class="fa" data-check-icon=""></i>
						</div>
					</div>
				</label> 
				<small class="g-font-weight-300 g-font-size-12 g-color-gray-dark-v6 g-pt-5 g-hidden-md-down">
				공유링크를 같이 생성합니다. 
				</small>	 	
	  		</div>
	  	</div>  
	  	<hr class="g-brd-gray-light-v4 g-mx-minus-30"/>
	  	<img data-bind="attr:{src:imageUrl}" class="img-thumbnail"  />
	  	<div class="row no-gutters"> 
		  	<div class="input-group g-mt-25">
				<input type="text" class="form-control g-brd-gray-light-v7 g-brd-gray-light-v3 g-px-14 g-py-10" placeholder="이미지 URL" aria-label="이미지 URL를 입력하세요." data-bind="value:url" >
				<div class="input-group-append">
					<button class="btn u-btn-darkgray g-width-80" type="button" data-bind="click:preview" >미리보기</button>
					<button class="btn u-btn-primary g-width-80 g-brd-0 g-rounded-right-4" type="button" data-bind="click:uploadByUrl, enabled: enabled" >업로드</button>
				</div>
			</div>
	  	</div> 
	  	<hr class="g-brd-gray-light-v4 g-mx-minus-30"/>
	  	<div class="row no-gutters">
		<!-- Dropdown -->	
			<form action="" method="post" enctype="multipart/form-data" id="image-file-dropzone" class="u-dropzone u-file-attach-v3 dz-clickable g-rounded-10 g-pa-20" style="width:100%;">
				<div class="dz-default dz-message">
					<p>
					<img src="data:image/png;base64,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">
					</p>
					<h3 class="g-font-size-16 g-font-weight-400 g-color-gray-dark-v2 mb-0">업로드할 이미지 파일은 이곳에 드레그 <span class="g-color-primary">Drag &amp; Drop</span> 하여 놓아주세요. 또는 클릭하여 파일을 선택하여 주세요.</h3>
					<p class="g-font-size-14 g-color-gray-light-v2 mb-0">최대파일 크기는 ${MAX_FILE_SIZW} MB 입니다.</p>
				</div>
				<div class="dropzone-previews"></div>
			</form>	
		<!-- End Dropdown -->
		</div> 	 
	</div>	
	</div>
	
	<script type="text/x-kendo-template" id="name-column-template">    
	<div class="media">
		<div class="d-flex">
		<!-- Figure Image -->
			<div class="g-width-100 g-width-100--md g-width-100 g-height-100--md g-brd-2 g-brd-transparent g-brd-lightblue-v3--parent-opened g-mr-20--sm">
				<a class="fancybox" data-fancybox="gallery" href="#= community.data.url.image (data) #" 
				data-caption="
				#if (tags !=null && tags.length>0 ) {#<span class='u-label g-rounded-3 g-bg-pink g-mr-10 g-mb-15'><i class='fa fa-tags g-mr-3'></i>#: tags #</span>#}#
				<br/> 
				#: name #  #= community.data.format.bytesToSize( size ) # #= community.data.format.date( modifiedDate , 'yyyy.MM.dd HH:MM') #">
				<img class="g-width-100 g-width-100--md g-width-100 g-height-100--md g-brd-2 g-brd-transparent g-brd-lightblue-v3--parent-opened g-mr-20--sm" src="#= community.data.url.image (data, {thumbnail:true}) #" alt="#= name #">
				</a>
			</div>
		<!-- Figure Image -->
		</div>
		<div class="media-body">
			<!-- Figure Info -->
			<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-action="edit" data-object-id="#=imageId#">
				<h5 class="g-font-weight-100 g-mb-0">
			    #if ( imageLink!=null && imageLink.publicShared ) { # <i class="hs-admin-unlock"></i>  # } else {# <i class="hs-admin-lock"></i> #}#
				#= name #
				</h5> 
			</a> 
			#if ( imageLink!=null ) { #
			<p class="g-font-weight-300 g-color-gray-dark-v6 g-mt-5 g-ml-10 g-mb-0" >
			#: imageLink.linkId #
			</p>
			#}#
			#if ( tags != null ) { #
			<p class="g-font-weight-300 g-color-gray-dark-v6 g-mt-5 g-ml-10 g-mb-0" >
			#: tags #
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
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="<@spring.url "/secure/studio/resource-images-slideshow"/>" target="_blank"  >
					<i class="hs-admin-gallery g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">${PAGE_NAME} 슬라이드쇼</span>
				</a>
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:getUploadWindow();" >
					<i class="hs-admin-upload g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">간편 ${PAGE_NAME} 업로드</span>
				</a>
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