<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <title>Dashboard v.1 | Studio Template</title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">
  <!-- Favicon -->
  <link rel="shortcut icon" href="../favicon.ico">
  <!-- Google Fonts -->
  <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans%3A400%2C300%2C500%2C600%2C700%7CPlayfair+Display%7CRoboto%7CRaleway%7CSpectral%7CRubik">
  
  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  
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
	        "studio.custom" 				: { "deps" :['jquery', 'bootstrap', 'hs.core'] }
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
			"studio.custom" 				: "<@spring.url "/js/community.ui.studio/custom"/>"
		}
	});		
  	require([ "jquery", "bootstrap", "hs.side-nav", "hs.hamburgers" , "hs.dropdown" , "jquery.scrollbar", "hs.scrollbar" , "hs.popup", "studio.custom" ], function($) {  
  	  console.log("START SETUP APPLICATION.");	
  	  community.ui.studio.setup();	
  	  console.log("END SETUP APPLICATION.");	
  	});	
  </script>
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
              <a class="u-link-v5 g-color-gray-dark-v6 g-color-secondary--hover g-valign-middle" href="#!">부모 페이지 이름 </a>
              <i class="hs-admin-angle-right g-font-size-12 g-color-gray-light-v6 g-valign-middle g-ml-10"></i>
            </li> 
            <li class="list-inline-item">
              <span class="g-valign-middle">페이지 이름</span>
            </li>
          </ul>
        </div>
        <!-- End Breadcrumb-v1 --> 

        <div class="g-pa-20">
          <h1 class="g-font-weight-300 g-font-size-28 g-color-black g-mb-30">페이지 이름</h1> 
          <!-- Content -->
          <div class="container-fluid">


<nav>
<div class="nav nav-tabs" id="nav-tab" role="tablist">
<a class="nav-item nav-link active" id="nav-attachment-tab" data-toggle="tab" href="#nav-attachment" role="tab" aria-controls="nav-attachment" aria-selected="false">첨부파일</a>
<a class="nav-item nav-link" id="nav-task-tab" data-toggle="tab" href="#nav-task" role="tab" aria-controls="nav-task" aria-selected="false">과업</a>
<a class="nav-item nav-link" id="nav-task-tab" data-toggle="tab" href="#nav-scm" role="tab" aria-controls="nav-scm" aria-selected="false">형상관리</a>
</div>
</nav>

<div class="tab-content" id="nav-tabContent">
	<div class="tab-pane fade show active" id="nav-attachment" role="tabpanel" aria-labelledby="nav-attachment-tab">
	
	</div>
	<div class="tab-pane fade" id="nav-task" role="tabpanel" aria-labelledby="nav-task-tab">
	
	<div id="task-grid" class="g-brd-gray-light-v7 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-1 g-mt-5 k-grid k-widget k-display-block k-editable" data-bind="enabled:editable" disabled="disabled" data-role="grid"><div class="k-header k-grid-toolbar"><a role="button" class="k-button k-button-icontext k-grid-add" href="#"><span class="k-icon k-i-plus"></span>새 레코드를 추가</a></div><div class="k-grid-header" style="padding-right: 15px;"><div class="k-grid-header-wrap k-auto-scrollable" data-role="resizable"><table role="grid"><colgroup><col style="width:75px"><col style="width:80px"><col style="width:80px"><col style="width:200px"><col style="width:80px"><col style="width:100px"><col style="width:100px"><col><col style="width:80px"><col><col style="width:220"></colgroup><thead role="rowgroup"><tr role="row"><th scope="col" role="columnheader" data-field="taskId" aria-haspopup="true" rowspan="1" data-title="ID" data-index="0" id="1c4c708a-4afc-4aa7-a3a6-d8b41043b088" class="k-header" data-role="columnsorter"><a class="k-link" href="#">ID</a></th><th scope="col" role="columnheader" data-field="objectType" aria-haspopup="true" rowspan="1" data-title="객체 유형" data-index="1" id="97d493de-776a-4efd-bdf8-875f18e74dba" class="k-header" data-role="columnsorter"><a class="k-link" href="#">객체 유형</a></th><th scope="col" role="columnheader" data-field="objectId" aria-haspopup="true" rowspan="1" data-title="객체 ID" data-index="2" id="8386449e-0e4a-4b6c-9ea2-097868157391" class="k-header" data-role="columnsorter"><a class="k-link" href="#">객체 ID</a></th><th scope="col" role="columnheader" data-field="taskName" aria-haspopup="true" rowspan="1" data-title="이름" data-index="3" id="c289b358-ab48-4b25-aa56-a7f6b5c189ee" class="k-header" data-role="columnsorter"><a class="k-link" href="#">이름</a></th><th scope="col" role="columnheader" data-field="version" aria-haspopup="true" rowspan="1" data-title="버전" data-index="4" id="ed6da8ac-ee55-4e24-ba0e-0ec77ec82af8" class="k-header" data-role="columnsorter"><a class="k-link" href="#">버전</a></th><th scope="col" role="columnheader" data-field="price" aria-haspopup="true" rowspan="1" data-title="가격" data-index="5" id="e5bcda7a-d872-4719-8f27-11d1a69017ce" class="k-header" data-role="columnsorter"><a class="k-link" href="#">가격</a></th><th scope="col" role="columnheader" data-field="startDate" aria-haspopup="true" rowspan="1" data-title="시작일" data-index="6" id="e2edada5-0df8-404c-90f0-f9a441fac1fb" class="k-header" data-role="columnsorter"><a class="k-link" href="#">시작일</a></th><th scope="col" role="columnheader" data-field="endDate" aria-haspopup="true" rowspan="1" data-title="종료일" data-index="7" id="a3f5c8a1-1c27-4454-a385-48523f197e0e" class="k-header" data-role="columnsorter"><a class="k-link" href="#">종료일</a></th><th scope="col" role="columnheader" data-field="progress" aria-haspopup="true" rowspan="1" data-title="진행율" data-index="8" id="73f19868-91df-4031-bc7e-9a5fdd335b77" class="k-header" data-role="columnsorter"><a class="k-link" href="#">진행율</a></th><th scope="col" role="columnheader" data-field="description" aria-haspopup="true" rowspan="1" data-title="설명" data-index="9" id="71f13a5e-6111-46bc-9bcf-b930f72a2026" class="k-header" data-role="columnsorter"><a class="k-link" href="#">설명</a></th><th scope="col" id="85242680-3310-4e37-a94b-f586015b5d11" rowspan="1" data-index="10" class="k-header">&nbsp;</th></tr></thead></table></div></div><div class="k-grid-content k-auto-scrollable"><table role="grid"><colgroup><col style="width:75px"><col style="width:80px"><col style="width:80px"><col style="width:200px"><col style="width:80px"><col style="width:100px"><col style="width:100px"><col><col style="width:80px"><col><col style="width:220"></colgroup><tbody role="rowgroup"></tbody></table></div></div>
	
	</div>
	<div class="tab-pane fade" id="nav-scm" role="tabpanel" aria-labelledby="nav-scm-tab">
	
	<div id="scm-grid" class="g-brd-gray-light-v7 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-1 g-mt-5 k-grid k-widget k-display-block k-editable" data-bind="enabled:editable" disabled="disabled" data-role="grid"><div class="k-header k-grid-toolbar"><a role="button" class="k-button k-button-icontext k-grid-add" href="#"><span class="k-icon k-i-plus"></span>새 레코드를 추가</a></div><div class="k-grid-header" style="padding-right: 15px;"><div class="k-grid-header-wrap k-auto-scrollable" data-role="resizable"><table role="grid"><colgroup><col style="width:75px"><col style="width:80px"><col style="width:80px"><col style="width:200px"><col><col style="width:100px"><col style="width:100px"><col style="width:100px"><col style="width:220"></colgroup><thead role="rowgroup"><tr role="row"><th scope="col" role="columnheader" data-field="scmId" aria-haspopup="true" rowspan="1" data-title="ID" data-index="0" id="9dd88f72-0a05-46b9-9993-408df5ae436b" class="k-header" data-role="columnsorter"><a class="k-link" href="#">ID</a></th><th scope="col" role="columnheader" data-field="objectType" aria-haspopup="true" rowspan="1" data-title="객체 유형" data-index="1" id="34d736ed-22f5-4c14-9506-1ddf57aa962d" class="k-header" data-role="columnsorter"><a class="k-link" href="#">객체 유형</a></th><th scope="col" role="columnheader" data-field="objectId" aria-haspopup="true" rowspan="1" data-title="객체 ID" data-index="2" id="d2438e50-4b14-4fc6-9742-bb4536de8c84" class="k-header" data-role="columnsorter"><a class="k-link" href="#">객체 ID</a></th><th scope="col" role="columnheader" data-field="name" aria-haspopup="true" rowspan="1" data-title="이름" data-index="3" id="dafafcee-5115-4f77-afad-8bda93f57602" class="k-header" data-role="columnsorter"><a class="k-link" href="#">이름</a></th><th scope="col" role="columnheader" data-field="description" aria-haspopup="true" rowspan="1" data-title="설명" data-index="4" id="7196c2c3-83c9-4ba2-abe1-23cfeb4b0430" class="k-header" data-role="columnsorter"><a class="k-link" href="#">설명</a></th><th scope="col" role="columnheader" data-field="url" aria-haspopup="true" rowspan="1" data-title="URL" data-index="5" id="2bc9beab-f1b9-402c-b85a-95eeec50e080" class="k-header" data-role="columnsorter"><a class="k-link" href="#">URL</a></th><th scope="col" role="columnheader" data-field="username" aria-haspopup="true" rowspan="1" data-title="User" data-index="6" id="e96d440a-349f-4e42-98b6-41132264fd3a" class="k-header" data-role="columnsorter"><a class="k-link" href="#">User</a></th><th scope="col" role="columnheader" data-field="password" aria-haspopup="true" rowspan="1" data-title="Password" data-index="7" id="cf5a5a26-be77-40fb-8945-bfe91f18583f" class="k-header" data-role="columnsorter"><a class="k-link" href="#">Password</a></th><th scope="col" id="9b3cd881-174f-46be-9c5e-3b13db577023" rowspan="1" data-index="8" class="k-header">&nbsp;</th></tr></thead></table></div></div><div class="k-grid-content k-auto-scrollable"><table role="grid"><colgroup><col style="width:75px"><col style="width:80px"><col style="width:80px"><col style="width:200px"><col><col style="width:100px"><col style="width:100px"><col style="width:100px"><col style="width:220"></colgroup><tbody role="rowgroup"></tbody></table></div></div>
	
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