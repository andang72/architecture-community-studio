<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "Working with Menu" />	
  <#assign PARENT_PAGE_NAME = "가이드" />	  
  <#assign KENDO_VERSION = "2019.3.917" /> 
  <title>STUDIO :: ${PAGE_NAME} </title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/unify-decorator.jsp"/>">

  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>"> 
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/docs.min.css"/>"> 
    
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
  	  console.log("END SETUP APPLICATION.");	
  	});	
  </script>
</head>    		
<body data-spy="scroll" data-target=".guide-navbar" data-offset="50" >
  <main class="container-fluid px-0 g-pt-65">
    <div class="row no-gutters g-pos-rel g-overflow-x-hidden"> 
	  <#include "../../includes/sidebar.ftl"> 
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
			<div class="row flex-xl-nowrap"> 
	          <nav class="d-none d-xl-block col-xl-2 bd-toc guide-navbar" aria-label="Secondary navigation">
				<ul class="section-nav">
					<li class="toc-entry toc-h2"><a href="#guide-1">What is Menu</a> 
					</li>
					<li class="toc-entry toc-h2"><a href="#guide-2">Studio 커스텀 메뉴</a></li>
					<li class="toc-entry toc-h2"><a href="#guide-3">메뉴 UI 구현하기</a></li>
					</ul>
	         	</nav> 
				<main class="col-12 col-md-9 col-xl-10 py-md-3 pl-md-5 bd-content" role="main"> 
					<div class="u-heading-v2-3--bottom g-mb-30">
                      <h2 class="u-heading-v2__title g-mb-10" id="guide-1">What is Menu</h2>
                      <h4 class="g-font-weight-200 g-mb-10"></h4>
                    </div>
					<p>Menu 는 Tree 형태의 계증 정보를 갖는 메뉴를 구성하는 것을 지원한다.</p> 
					<p></p> 
					<hr>			
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-2" >Studio 커스텀 메뉴</h2>
					</div>
                    <p>Studio 에 커스텀 메뉴를 추가하고자하는 경우 다음과 같은 방법을 지원하고 있다. </p>
					<p>❶ 메뉴 이름을 <span class="g-color-primary">CUSTOM_ADMIN_MENU</span> 생성하고 구성한다. <a href="/secure/studio/resource-menus" data-menu-item="SIDEBAR_05_03" class="btn btn-sm u-btn-outline-red g-mr-15 g-mb-5"> Menu 관리 바로가기</a></p> 
					<p>❷ 설정 > 프로퍼티에서 <span class="g-color-primary">studio.custom.sidebar</span> 키에 해당하는 값으로 <span class="g-color-primary">CUSTOM_ADMIN_MENU</span> 입력하면 해당 메뉴가 리소스 다음에 추가됩니다. <a href="/secure/studio/setting-properties" data-menu-item="SIDEBAR_02_01" class="btn btn-sm u-btn-outline-red g-mr-15 g-mb-5"> 프로퍼티 관리 바로가기</a></p>	
					<hr>			
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-3" >메뉴 UI 구현하기</h2>
					</div>
					<p>
					메뉴는 <span class="g-color-primary">MenuService</span> 와 <span class="g-color-primary">MenuItemTreeWalker</span> 이용하면 원하는 형태의 메뉴 UI 를 구현할 수 있다. 
					</p>
<pre class="hljs" style="display: block; overflow-x: auto; padding: 0.5em; background: rgb(51, 51, 51); color: rgb(255, 255, 255);">&lt;%@ page language=<span class="hljs-string" style="color: rgb(162, 252, 162);">"java"</span> contentType=<span class="hljs-string" style="color: rgb(162, 252, 162);">"text/html; charset=UTF-8"</span> pageEncoding=<span class="hljs-string" style="color: rgb(162, 252, 162);">"UTF-8"</span>%&gt;
&lt;%@ page <span class="hljs-keyword" style="color: rgb(252, 194, 140);">import</span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"architecture.ee.util.StringUtils,
			architecture.community.user.User,
			architecture.community.util.SecurityHelper,
			architecture.community.web.util.ServletUtils,
			architecture.community.util.CommunityContextHelper,
			architecture.community.navigator.menu.*"</span>%&gt;
&lt;ul&gt;
&lt;%
		<span class="hljs-comment" style="color: rgb(136, 136, 136);">// GET MENU BY NAME</span>
		MenuItemTreeWalker treeWalker = CommunityContextHelper.getMenuService().getTreeWalker(<span class="hljs-string" style="color: rgb(162, 252, 162);">"MENU_NAME"</span>);
		<span class="hljs-keyword" style="color: rgb(252, 194, 140);">for</span> (MenuItem menuItem : treeWalker.getChildren()) {
			<span class="hljs-comment" style="color: rgb(136, 136, 136);">// 1. CHECKING ROLES </span>
			<span class="hljs-keyword" style="color: rgb(252, 194, 140);">boolean</span> isAccessAllowed = <span class="hljs-keyword" style="color: rgb(252, 194, 140);">true</span>;
			String _location = <span class="hljs-string" style="color: rgb(162, 252, 162);">"#"</span>;
			<span class="hljs-keyword" style="color: rgb(252, 194, 140);">if</span> (!StringUtils.isNullOrEmpty(menuItem.getRoles())) {
				isAccessAllowed = SecurityHelper.isUserInRole(menuItem.getRoles());
			}
			<span class="hljs-comment" style="color: rgb(136, 136, 136);">// 2. CHECKING HAS HREF </span>
			<span class="hljs-keyword" style="color: rgb(252, 194, 140);">boolean</span> hasLocation = <span class="hljs-keyword" style="color: rgb(252, 194, 140);">false</span>;
			<span class="hljs-keyword" style="color: rgb(252, 194, 140);">if</span> (!StringUtils.isNullOrEmpty(menuItem.getLocation())) {
				_location = menuItem.getLocation();
			}

			<span class="hljs-comment" style="color: rgb(136, 136, 136);">// 3. MAKE MENU HTML </span>
			<span class="hljs-keyword" style="color: rgb(252, 194, 140);">if</span> (isAccessAllowed) {
%&gt;
	&lt;li&gt;&lt;a href=<span class="hljs-string" style="color: rgb(162, 252, 162);">"&lt;%=_location%&gt;"</span>&gt;&lt;%=menuItem.getName()%&gt;&lt;/a&gt; &lt;%
 	<span class="hljs-keyword" style="color: rgb(252, 194, 140);">if</span> (treeWalker.getChildCount(menuItem) &gt; <span class="hljs-number" style="color: rgb(211, 99, 99);">0</span>) { %&gt;
		&lt;ul <span class="hljs-class"><span class="hljs-keyword" style="color: rgb(252, 194, 140);">class</span></span>=<span class="hljs-string" style="color: rgb(162, 252, 162);">"sub-gnb"</span>&gt;
			&lt;%
			<span class="hljs-keyword" style="color: rgb(252, 194, 140);">for</span> (MenuItem menuItem2 : treeWalker.getChildren(menuItem)) {
				<span class="hljs-keyword" style="color: rgb(252, 194, 140);">if</span> (!StringUtils.isNullOrEmpty(menuItem2.getRoles())) {
					isAccessAllowed = SecurityHelper.isUserInRole(menuItem2.getRoles());
				}
				<span class="hljs-keyword" style="color: rgb(252, 194, 140);">if</span> (isAccessAllowed) { %&gt;
			&lt;li&gt;&lt;a href=<span class="hljs-string" style="color: rgb(162, 252, 162);">"&lt;%=menuItem2.getLocation()%&gt;"</span>&gt;&lt;%=menuItem2.getName()%&gt;&lt;/a&gt;&lt;/li&gt;
&lt;%
				}
			}
			%&gt;
		&lt;/ul&gt; 
&lt;%	} %&gt;
 	&lt;/li&gt;
&lt;%
		}
	}
%&gt;
&lt;/ul&gt;</pre>						
 		
				</main>
			</div>
		  </div>         
          <!-- End Content --> 
          
        </div>
		<#include "../../includes/footer.ftl"> 
      </div>
    </div>
  </main>
</body>
</html>