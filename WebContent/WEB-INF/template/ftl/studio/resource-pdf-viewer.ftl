<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <#assign PAGE_NAME = "PDF 뷰어" />	
  <#assign PARENT_PAGE_NAME = "리소스" />	
  <#assign KENDO_VERSION = "2019.3.1023" />	  
  <meta charset="utf-8">
  <!-- Title -->
  <title>STUDIO :: ${PAGE_NAME} </title>
  <meta name="description" content="PDF Viewer">
  <meta name="author" content="Island">
  
  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  
  <!-- Kendo UI Professional Theme Nova -->
  <link rel="stylesheet" href="<@spring.url "/css/bootstrap/4.3.1/bootstrap.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.common.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.mobile.common.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.min.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/kendo/${KENDO_VERSION}/kendo.nova.mobile.min.css"/>"> 
  
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/jquery.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.all.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/${KENDO_VERSION}/cultures/kendo.culture.ko-KR.min.js"/>"></script>
  <script src="<@spring.url "/js/kendo/custom/kendo.messages.ko-KR.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.data.js"/>"></script>
  <script src="<@spring.url "/js/community.ui/community.ui.core.js"/>"></script>
  <script src="<@spring.url "/js/pdfjs/2.2.228/pdf.js"/>"></script>
  <script>
    window.pdfjsLib.GlobalWorkerOptions.workerSrc = '<@spring.url "/js/pdfjs/2.2.228/pdf.worker.js"/>';
  </script>
  <script>
  var __link = <#if RequestParameters.link?? >'${RequestParameters.link}'<#else>null</#if>;	
  $(document).ready(function() { 
  		community.ui.culture();  
  		if( __link != null )
  		{
			$("#pdfViewer").kendoPDFViewer({
				pdfjsProcessing: {
					file: "<@spring.url "/download/files/"/>" + __link
				},
				toolbar: {
		            items: [
		              "pager", "spacer",  "zoom", "search"
		            ]
		        },
		        scale: 1.5,
				width: "100%",
				height: 1200
	        });
        }  	
  });
  </script>
  <style>
  </style>
</head>
<body class="bg-secondary"> 
	<div class="container" class="bg-white">
	<div id="pdfViewer"></div>
	</div>
</body>
</html>