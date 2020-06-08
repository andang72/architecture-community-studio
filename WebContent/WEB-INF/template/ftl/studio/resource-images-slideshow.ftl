<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <#assign PAGE_NAME = "이미지 뷰어" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	
  <#assign KENDO_VERSION = "2019.3.917" />	  
  <meta charset="utf-8">
  <!-- Title -->
  <title>STUDIO :: ${PAGE_NAME} </title>
  <meta name="description" content="Slideshow">
  <meta name="author" content="Island">
  
  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.common.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/bootstrap/4.3.1/bootstrap.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/jquery.fancybox/jquery.fancybox.min.css"/>">  
  
  <script src="<@spring.url "/js/jquery/jquery-3.4.1.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.all.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/cultures/kendo.culture.ko-KR.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/custom/kendo.messages.ko-KR.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.data.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.core.js"/>"></script>  
  <script src="<@spring.url "/js/jquery.fancybox/jquery.fancybox.min.js"/>"></script> 		
  <script>
  $(document).ready(function() { 
  	
  	community.ui.culture();  
  	var tags = community.ui.multiselect(
  		$("#tags"),
  		{
                placeholder: "태크를 선택하세요.",
                dataTextField: "name",
                dataValueField: "tagId",
                autoBind: true,
                dataSource: { 
               		transport: { 
               			contentType : "application/json; charset=utf-8", type :'POST', dataType : 'json',
						read : { url:'<@spring.url "/data/secure/mgmt/services/tags/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
						parameterMap : function (options, operation){		
							return community.ui.stringify(options);
						}
					},
					error: community.ui.error,	
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
					
                }
		}
  	);
  	        
  	var dataSource = community.ui.datasource ({
		transport: { 
			read : { url:'<@spring.url "/data/secure/mgmt/images/list.json"/>', type:'post', contentType: "application/json; charset=utf-8"},
			parameterMap: function (options, operation){	 
				if (operation !== "read" && options.models) { 
					return community.ui.stringify(options.models);
				}else{
					options.tags = tags.value() ;
				}
				return community.ui.stringify(options);
			}
		}, 
		pageSize: 54,
		serverPaging : true,
		serverFiltering:true,
		serverSorting: true,
		error : community.ui.error,
		schema: {
			data:  "items",
			total: "totalCount",
			model: community.data.model.Image
		}
  	});

	$("#listview-filter").kendoFilter({
		dataSource: dataSource,
		expressionPreview: false,
		applyButton: true,
		fields: [
			{ name: "FILE_NAME", type: "string", label: "NAME" },
			{ name: "OBJECT_TYPE", type: "number", label: "OBJECT TYPE" },
			{ name: "OBJECT_ID", type: "number", label: "OBJECT ID" }
		],
		expression: {
			logic: "or",
			filters: [ ]
		}
	}).data("kendoFilter").applyFilter();
	      
  	community.ui.listview($("#listview"), {
  		autoBind : false,
    	dataSource: dataSource,
    	template: kendo.template($("#listview-template").html()),
    	dataBound: function() {
	    	console.log("ListView is bound and ready to render."); 
	    	console.log( $(".fancybox").length );
	    	$(".fancybox").fancybox({
	    		protect: true
			});
	    }
  	}); 

	community.ui.pager($("#listview-pager"), {
		dataSource: dataSource,
		pageSizes: [54, 108, 162],
		responsive: false
	}); 
   
  });
  
  </script>
  <style>
  		.photo {
            float: left;
            position: relative;
            width: 119px;
            height: 170px;
            margin: 0 5px;
            padding: 0;
        }
        .photo img {
            width: 118px;
            height: 118px;
        }
        .photo h3 {
            margin: 0;
            padding: 3px 5px 0 0;
            max-width: 96px;
            overflow: hidden;
            line-height: 1.1em;
            font-size: .8em;
            font-weight: normal;
            text-transform: uppercase;
            color: #999;
        }
        .product p {
            visibility: hidden;
        }
        .photo:hover p {
            visibility: visible;
            position: absolute;
            width: 118px;
            height: 118px;
            top: 0;
            margin: 0;
            padding: 0;
            line-height: 118px;
            vertical-align: middle;
            text-align: center;
            color: #fff;
            background-color: rgba(0,0,0,0.75);
            transition: background .2s linear, color .2s linear;
            -moz-transition: background .2s linear, color .2s linear;
            -webkit-transition: background .2s linear, color .2s linear;
            -o-transition: background .2s linear, color .2s linear;
        }
        
        .k-pager-wrap {
        	width:100%;
        	border-left : 0;
        	border-right : 0;
        	border-bottom : 0;
        	font-size : .9em;
        	background-color : #fff;
        }
        
        .listview {
        	min-height : 500px;
        }
        
        .display-4 {
        	font-size: 2.0rem;
        }
        
  </style>
</head>

<body> 
	<article>
		<section style="padding:5px;">
		<div class="container">
			<div class="row">
				<select id="tags" style="width:300px;"></select>
				<div id="listview-filter" style="width:100%;" ></div> 
			</div>		
		</div>
		</section>	
	
		<div class="container"> 
			<div class="row"> 
				<div id="listview" class="border-0 listview"></div>
			</div>				
			<div class="row">
				<div id="listview-pager" class="k-pager-wrap" style="width:100%;" ></div> 
			</div>
		</div>
	</article> 
	<script id="listview-template" type="text/x-kendo-template" >
	<a class="fancybox" data-fancybox="gallery" href="#= community.data.url.image (data) #" data-caption="<h4 class='display-4'>#: name #</h4> <small class='text-muted'> #= community.data.format.bytesToSize( size ) # </small> <small class='text-muted'>#= community.data.format.date( modifiedDate , 'yyyy.MM.dd HH:MM') # </small> <br> <span class='badge badge-pill badge-dark'> #: tags # </span> ">
	<figure class="photo"> 
		<img src="#= community.data.url.image (data, {thumbnail:true}) #"  alt="#: name #" /> 
		<h3>#:name #</h3>
	</figure>
	</a> 
    </script>
</body>
</html>