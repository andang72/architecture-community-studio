<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "공지" />	
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
  <link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>"> 
    
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
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core', 'kendo.web.min', 'kendo.messages','community.ui.data' , 'community.ui.core' ] }
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
  	  	visible : true, 
  	  	editable : false,
  	  	selectedTag : null,
 		createOrUpdate: function(){
			var $this = this;
			getAnnounceEditorWindow( $this );
		},
		cancle: function(){
			var $this = this;
			$this.set('editable', false);
		}	  	
  	  });
  	  createAnnounceGrid(observable);  
  	  community.data.bind( $('#features') , observable ); 
  	  console.log("END SETUP APPLICATION.");	
  	}); 
    
    var objectTypes = [
		{ text: "정의되지 않음", value: "-1" },
		{ text: "앨범", value: "40" },
		<#list CommunityContextHelper.getCustomQueryService().list("FRAMEWORK_EE.SELECT_ALL_SEQUENCER") as item >
		<#if item.DISPLAY_NAME??>
		{ text:'${item.DISPLAY_NAME}', value:'${item.SEQUENCER_ID}'} <#if !item?is_last>,</#if>
		</#if>
		</#list>	
	];
		    
    function getAnnounceEditorWindow( data ){ 
		var renderTo = $('#announce-window');
  		if( !community.ui.exists( renderTo )){  
  			var observable = community.data.observable({
  				deletable : false,
  				editable : false,
  				announce : new community.data.model.Announce(),
  				objectTypes : objectTypes,
		   	  	edit: function(){
					var $this = this;
					$this.set('editable', true);
					if( $this.announce.announceId > 0 ){
						$this.set('deletable', true );
					}
				},  	
				lock: function(){
					var $this = this;
					$this.set('editable', false);
					$this.set('deletable', false );
				},  	
				cancle: function(){
					var $this = this;  
					window.close();
					validator.hideMessages();	
					$this.set('editable', false);  
					$this.set('deletable', false);  
				}, 				
  				saveOrUpdate:function(){
  					var $this = this;  
  					if (validator.validate()) { 
	  					community.ui.progress($('.k-window'), true);	
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/announces/0/save-or-update.json" />', {
							data: community.ui.stringify($this.announce),
							contentType : "application/json",
							success : function(response){
								community.ui.notify( $this.announce.subject + " 이 저장되었습니다.");
								$this.setSource( new community.data.model.Announce( response ) );
								community.ui.grid( $('#announces-grid') ).dataSource.read();
							}
						}).always( function () {
							community.ui.progress($('.k-window'), false);	
						});	 
					} 
  				}, 
  				setSource : function( data ) {
  					var $this = this ;	  
  					data.copy( $this.announce );
  					if( data.announceId > 0 ){
  						$this.set('editable', false);
  						$this.set('deletable', false );
  					}else{
  						$this.set('editable', true );
  						$this.set('deletable', false );
  					}
  				}
  			});  
  			
  			function onCustomInsertImageClick (e) {
  				var editor = $(this).data("kendoEditor"); 
	            // Store the Editor range object.
	            // Needed for IE.
	            var storedRange = editor.getRange(); 
	            // Create a modal Window from a new DOM element.   
	            
	            var renderTo2 = $('#insert-image-window');
	            if( !community.ui.exists( renderTo2 )){  
					var popupWindow = community.ui.window(renderTo2,{
		                // Modality is recommended in this scenario.
		                modal: false,
		                width: 600,
		                scrollable : false,
		                resizable: false,
		                title : "Image Upload", 
		                // Ensure the opening animation.
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
  			var editor = $("#editor").kendoEditor({ 
  				placeholder: "${PAGE_NAME} 내용을 입력하세요.",
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
            	paste: function(e){},
            	change: function(){}
			}).data('kenodEditor');
			 
			var validator = renderTo.kendoValidator().data("kendoValidator");   
			
  			var window = community.ui.window(renderTo, {
				width: "900px",
				title: "${PAGE_NAME}",
				visible: false,
				scrollable : true,
				modal: true,
				actions: [ "Close"], //  "Minimize", "Maximize",  
				open: function(){	 
				},
				close: function(){  
				}
			});   
			window.wrapper.addClass("zIndexEnforce");
			window.wrapper.addClass("no-theme");		
			community.data.bind(renderTo, observable);
			renderTo.data('model', observable );
  		} 
  		renderTo.data('model').setSource( data ); 
  		var _left =  this.document.documentElement.offsetWidth/2 - 450 ; 
  		community.ui.window( renderTo ).setOptions({
			position: {
				top: 50,
				left :_left
			}
		});  
  		community.ui.window( renderTo ).open(); // .center().open();maximize().
    }  
    
  	function refresh(){
  		var renderTo = $('#announces-grid');
  		community.ui.grid(renderTo).dataSource.read();
  		console.log('grid refresh...');
  	}  	
  	
    function createAnnounceGrid(observable){
    	var renderTo = $('#announces-grid');
		if( !community.ui.exists(renderTo) ){  
			var grid = community.ui.grid(renderTo, {
				dataSource: {
					transport: { 
						read : { url:'<@spring.url "/data/secure/mgmt/announces/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					error : community.ui.error,
					serverFiltering: false,
					serverSorting: false, 
					serverPaging: false, 
					schema: {
						total: "totalCount",
						data:  "items",
						model: community.data.model.Announce
					}
				},
				toolbar: [{ name: "create" , text: "새로운 ${PAGE_NAME} 만들기", template:community.ui.template($('#grid-toolbar-template').html())  }],
				sortable: true,
				filterable: true,
				selectable : "row",
				pageable: false,
				columns: [
					{ field: "ANNOUNCE_ID", title: "ID", width: 100 , template: '#: announceId #' },  
					{ field: "OBJECT_TYPE", width: 150 ,title: "OBJECT TYPE" , template: '#: objectType #'},
					{ field: "OBJECT_ID", width: 130 , title: "OBJECT ID", template: '#: objectId #' },
					{ field: "SUBJECT", title: "제목" , template : $('#name-column-template').html() },
					{ field: "START_DATE", title: "시작일", width: 150 , template :'#: community.data.format.date( startDate ,"yyyy.MM.dd HH:mm") #' },
					{ field: "END_DATE", title: "종료일", width: 150 , template :'#: community.data.format.date( endDate ,"yyyy.MM.dd HH:mm") #' },
					{ field: "USER_ID", title: "작성자", filterable: false, sortable: true, width:150, template: $('#user-column-template').html(), attributes:{ class:"text-center" } },
					{ field: "MODIFIED_DATE", title: "생성일", width: 150 , template :'#: community.data.format.date( modifiedDate ,"yyyy.MM.dd") #' , attributes:{ class:"text-center" } },
					{ field: "CREATION_DATE", title: "생성일", width: 150 , template :'#: community.data.format.date( creationDate ,"yyyy.MM.dd") #' ,attributes:{ class:"text-center" } }
				],
				change: function(e) {
				    var selectedRows = this.select();
				    var selectedDataItems = [];
				    for (var i = 0; i < selectedRows.length; i++) {
				      var dataItem = this.dataItem(selectedRows[i]); 
				    }
 				}
			});	 
			$('#features').on( "click", "a[data-action=edit]", function(e){		
				var $this = $(this);	
				if( community.ui.defined($this.data("object-id")) ){
					var objectId = $this.data("object-id");	
					var dataItem = grid.dataSource.get(objectId);
					if( dataItem == null )
						dataItem = new community.data.model.Announce(); 
					getAnnounceEditorWindow(dataItem); 
  				}	
			});	 														
		}			
    }	 
 
  </script>
  <style> 
  .k-window { border-radius : 15px; border:0px;} 
  .k-window>.k-header { border-top-left-radius: 15px; border-top-right-radius: 15px; padding: 15px; border:0px;} 
  .k-window .k-window-content { border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; border:0px; }
  .k-window-titlebar .k-window-actions { top : 12px ;}
  .k-window-titlebar .k-window-actions a.k-window-action { width:32px;}
  .k-window-titlebar .k-window-actions a.k-window-action > .k-icon { font-size:32px;}  
  .k-dialog .k-dialog-buttongroup.k-dialog-button-layout-normal .k-button { border-radius: 15px; padding-right: 15px; padding-left: 15px;} 
  .no-theme .k-window , .no-theme .k-header , .no-theme .k-window-content { border-radius : 0px;} 
  .k-grid-content { min-height:700px; }    
    
  .form-control { box-sizing: border-box; } 
 
 	#tags-grid .k-command-cell a.k-grid-edit {
		display:none;
	} 
	
	#tag-object-grid .k-dropdown > .k-dropdown-wrap {
		height:2em;
	}
	
	#announce-window .k-editor {
		border : 0;
	}
	
	.k-editor .k-i-custom-insert-image:before
	{ 
		content: "\e501"; 
	}
	
		.k-invalid-msg {
			background: transparent!important;
			border : 0;
			margin-top : 5px;
			color : red!important;
		} 
		 
		.product {
			float: left;
            width: 120px;
            height: 170px;
            margin: 0;
            padding: 0px;
            cursor: pointer;
        }
        .product img {
            width: 110px;
            height: 110px;
        }
        .product h3 {
            margin: 0;
            margin-left : 5px;
            padding: 3px 5px 0 0;
            max-width: 96px;
            overflow: hidden;
            line-height: 1.1em;
            font-size: .9em;
            font-weight: normal;
            text-transform: uppercase;
            color: #999;
        } 
        
        .k-listview:after {
            content: ".";
            display: block;
            height: 0;
            clear: both;
            visibility: hidden;
        }
        
        .product span {
           display:none;
        } 
        
	.product.k-state-selected span {
			display:block;
            position: absolute;
            width: 110px;
            height: 110px;
            top: 0;
            margin: 0;
            padding: 0;
            line-height: 110px;
            vertical-align: middle;
            text-align: center;
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
			<div id="features" class="container-fluid">
				<div class="row"> 
					<div id="announces-grid" class="g-brd-top-0 g-brd-left-0  g-brd-right-0 g-mb-1"></div>
				</div> 
				<div class="row" data-bind="visible:editable" style="display:none;">  
					<div id="tag-object-grid" class="g-mb-1 g-mt-5"></div>
				</div>
			</div>
          <!-- End Content --> 
        </div>
		<#include "includes/footer.ftl"> 
      </div>
    </div>
  </main>
  
  <div id="announce-window" class="g-height-600 g-pa-0 g-brd-0 container" style="display:none;" > 
	<header class="card-header g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0"></h3> 
			<div class="media-body d-flex justify-content-end"> 
				<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="javascript:void(this);" data-bind="invisible:editable, click:edit" style="display:none;"></a>
				<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="javascript:void(this);" data-bind="visible:editable, click:lock" style="display:none;"></a> 
			</div>
		</div>
	</header>  
  	<div class="card g-brd-gray-light-v7 g-rounded-3 g-mb-0 g-brd-0"> 
		<!--
		<a class="g-pos-fix g-right-20 g-top-15 u-link-v5 g-font-size-40 g-font-size-40--md g-color-gray-light-v6 g-color-gray-dark-v1--hover" href="javascript:void(this);"  data-bind="click:cancle" role="button" ><i class="hs-admin-close"></i></a>
 		-->
 		<div class="card-block g-mt-30 g-pa-15 g-pa-15--sm" data-bind="visible:editable" style="display:none;">
			<div class="form-group g-mb-5">
				<label class="g-mb-10 g-font-weight-300" for="input-announce-subject">제목 <span class="text-danger">*</span></label>
				<input id="input-announce-subject" name="Subject" class="form-control form-control-md rounded-3" type="text" placeholder="제목을 입력하세요." 
					data-bind="value:announce.subject, enabled:editable" style="box-sizing: border-box;" required="required" required validationMessage="Enter Announcement {0}."> 
				<span class="k-invalid-msg" data-for="Subject" style="margin-top:5px"></span>
			</div>		
			<textarea id="editor" name="Text" aria-label="editor" required="required" required validationMessage="Enter Announcement {0}." data-bind="value:announce.body, visible:editable" style="height: 400px;"></textarea>  
            <span class="k-invalid-msg" data-for="Text" ></span>
		</div> 
		<article class="g-pa-25">
			<h2 class="g-color-black mb-1" data-bind="text:announce.subject, invisible:editable" style="display:none;" ></h2>
			<section data-bind="html:announce.body, invisible:editable" style="display:none;" ></section>
		</article> 
		<hr class="d-flex g-brd-gray-light-v7 g-mx-15 g-mx-30--sm my-0">
		<div class="card-block g-pa-30 g-pa-30--sm">
			<p class="g-font-weight-300 g-color-primary mb-15">OBEJCT_TYPE 와 OBJECT_ID 값을 기준으로 객체들은 공지를 가질 수 있습니다. 공지는 시작일시와 종료일시 기간동안 노출됩니다.</p>
			<div class="row no-gutters"> 
                <div class="col-md-5">
                    <!-- OEJECT TYPE -->
                    <div class="form-group g-mb-20">
                      <label class="g-mb-10">OEJECT TYPE</label>
					  <input name="Object Type" data-role="dropdownlist"
										data-option-label="객체 유형을 선택하세요."
										data-auto-bind="true"
										data-value-primitive="true"
										data-text-field="text"
										data-value-field="value"
										data-bind="value: announce.objectType, source: objectTypes, enabled:editable"
										required data-required-msg="Select Object Type."
										style="width: 100%;" /> 
						<span class="k-invalid-msg" data-for="Object Type" ></span> 				
                    </div>
                    
                    <!-- End OEJECT TYPE -->
                  </div> 
                  <div class="col-md-5">
                    <!-- OBJECT ID -->
                    <div class="form-group g-mb-20">
                      <label class="g-mb-10">OBJECT ID</label>
                      <input name="Object Id" data-role="numerictextbox" placeholder="객체 ID 를 입력하세요." class="form-control form-control-md" type="number" data-min="0" step="1" data-bind="value:announce.objectId, enabled:editable" style="width: 100%" 
                      	required data-required-msg="Enter Object ID." />
                       <span class="k-invalid-msg" data-for="Object Id" ></span> 	
                    </div> 
                    <!-- End OBJECT ID -->
                </div> 
            </div>
			<div class="row no-gutters">
                  <div class="col-md-5">
                    <!-- StartDate -->
                    <div class="form-group g-mb-20">
                      <label class="g-mb-10">시작일시</label>
                      <input name="Start Date" data-role="datetimepicker" data-bind="value: announce.startDate, enabled:editable" style="width: 100%" required data-required-msg="Select start time"  > 
                      <span class="k-invalid-msg" data-for="Start Date" ></span> 	
                    </div>
                    <!-- End StartDate -->
                  </div> 
                  <div class="col-md-5">
                    <!-- EndDate -->
                    <div class="form-group g-mb-20">
                      <label class="g-mb-10">종료일시</label>
                      <input name="End Date" data-role="datetimepicker" data-bind="value: announce.endDate, enabled:editable" style="width: 100%" required data-required-msg="Select end time" >
                      <span class="k-invalid-msg" data-for="End Date" ></span> 	
                    </div>
                    <!-- End EndDate -->
                  </div>
			</div>  		 
		</div>
		<footer class="card-footer g-bg-transparent g-brd-gray-light-v7">  
			<div class="media w-100">
				<div class="media-body d-flex align-self-center justify-content-end" >
					<button class="btn btn-md btn-xl--md u-btn-outline-primary g-font-size-12 g-font-size-default--md g-mr-10" data-bind="click:delete, visible:deletable" type="button" style="display:none;">삭제</button>
					<button class="btn btn-md btn-xl--md u-btn-secondary g-width-160--md g-font-size-12 g-font-size-default--md g-mr-10" data-bind="click:saveOrUpdate, visible:editable" type="button" style="display:none;">저장</button>
					<button class="btn btn-md btn-xl--md u-btn-outline-gray-dark-v6 g-font-size-12 g-font-size-default--md" type="button" data-bind="click:cancle">취소</button>				
				</div>
			</div>
		</footer>
	</div>      
  </div>    
  
	<div id="insert-image-window" style="display:none; height:530px;" >
		<div class="k-content wide g-mt-50">
		<div class="u-file-attach-v3 g-mb-15 dropZoneElement">
			<h3 class="g-font-size-16 g-color-gray-dark-v2 mb-0">Drop files here to upload</h3>
			<p class="g-font-size-14 g-color-gray-light-v2 mb-0">Maximum file size 10mb</p>
		</div>
		</div>
		<input name="files" id="editor-image-files" type="file" />  
		<div id="editor-image-listview" class="g-mt-5"></div> 
		<div class="card-block text-right">
			<a href="javascript:void(this);" class="btn btn-md u-btn-lightred g-mr-5" data-action="insert" role="button" >이미지 삽입</a>
			<a href="javascript:void(this);" class="btn btn-md u-btn-outline-bluegray" data-action="close" role="button">취소</a>
		</div>
	</div> 
	<script type="text/x-kendo-template" id="user-column-template">    
	<div class="media">
    	<div class="d-flex align-self-center">
    		<img class="g-width-36 g-height-36 rounded-circle g-mr-15" src="#= community.data.url.userPhoto( '<@spring.url "/"/>' , user ) #" >
		</div>
		<div class="media-body align-self-center text-left">#if ( !user.anonymous  && user.name != null ) {# #: user.name # #}#</div>
	</div>	
	</script> 
	<script type="text/x-kendo-template" id="name-column-template">    
		<a class="d-flex align-items-center u-link-v5 u-link-underline g-color-black g-color-lightblue-v3--hover g-color-lightblue-v3--opened" href="\#!" data-kind="tag" data-action="edit" data-object-id="#=announceId#">
		<h5 class="g-font-weight-100 g-mb-0 g-font-size-14">
		#= subject  #
		</h5> 
		</a>
		<!--<p class="g-font-weight-300 g-color-gray-dark-v6 g-mt-5 g-ml-10 g-mb-0" >...</p>-->
	</script> 
	<script type="text/x-kendo-template" id="grid-toolbar-template">    
	<header class="card-header g-brd-gray-light-v7 g-bg-transparent g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm">
		<div class="media">
			<h3 class="d-flex align-self-center text-uppercase g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
				
			</h3> 
			<div class="media-body d-flex justify-content-end"> 
				<a class="k-grid-add d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:void(this);"data-action="edit" data-object-id="0">
					<i class="hs-admin-plus g-font-size-18"></i>
					<span class="g-hidden-sm-down g-ml-10">새로운 ${PAGE_NAME} 만들기</span>
				</a> 
				<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-10 g-ml-15--sm g-ml-30--xl" href="javascript:refresh();" >
					<i class="hs-admin-reload g-font-size-18"></i>
				</a>					
			</div>
		</div>
	</header> 
	</script>  
	
	<script type="text/x-kendo-template" id="uploaded-image-template">
		<div class="product"> 
			<img src="/download/images/#= imageLink.linkId #?thumbnail=true"  class="g-width-110 img-fluid g-ma-5" />
			<h3>#: name #</h3>
			<span class="u-icon-v1 g-color-primary"><i class="icon-check"></i></span> 
		</div>	
	</script> 
</body>
</html>