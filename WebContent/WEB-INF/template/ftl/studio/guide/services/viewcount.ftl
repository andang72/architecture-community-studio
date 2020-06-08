<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "Working with ViewCount" />	
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
			  <!-- right nav -->
	          <nav class="d-none d-xl-block col-xl-2 bd-toc guide-navbar" aria-label="Secondary navigation">
				<ul class="section-nav">
					<li class="toc-entry toc-h2"><a href="#guide-1">What is ViewCount</a> 
					</li>
					<li class="toc-entry toc-h2"><a href="#guide-2">이벤트를 이용한 조회 수 기록</a></li>
					<li class="toc-entry toc-h2"><a href="#guide-3">함수 호출을 이용한 조회 수 기록</a>
					<li class="toc-entry toc-h2"><a href="#guide-4">조회수</a>
					</ul>
	         	</nav> 
	         	
				<main class="col-12 col-md-9 col-xl-10 py-md-3 pl-md-5 bd-content" role="main"> 
					<div class="u-heading-v2-3--bottom g-mb-30">
                      <h2 class="u-heading-v2__title g-mb-10" id="guide-1">What is ViewCount</h2>
                      <h4 class="g-font-weight-200 g-mb-10"></h4>
                    </div>
					<p> 뷰 카운터는 특정 객체에 대한 조회 수를 기록하는 기능을 제공한다. 조회 수 기록은 ① 이벤트를 발생시키거나 ② 직접 함수를 호출하여 남길 수 있다. </p>		
					<hr>			
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-2" >이벤트를 이용한 조회 수 기록</h2>
					</div>
                    <p><span class="g-color-primary">ViewCountEvent</span> 을 발생시켜 통하여 특정 객체의 조회수를 기록할 수 있다. 이벤트는
                    	<span class="g-color-primary">CommunitySpringEventPublisher</span> 서비스를 사용하여 발생시킬 수 있다. 조회수는 이벤트 발생과 동시에 1 증가한다.
                    </p>	

						<pre><code class="language-java g-font-size-12">
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier; 
import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.viewcount.event.ViewCountEvent;

public class ViewCountExample { 

	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher; 
	
	public void addViewCount() { 
		// 1. source object 
		Object source = this ;  
		// 2. object type for view 
		int objectType = 0  ; 
		// 3. object id for view 
		long objectId = 0 ; 
		communitySpringEventPublisher.fireEvent(new ViewCountEvent(source, objectType , objectId )); 
	}
}</code></pre>
					<p>ViewCountEvent 객체 생성을 위한 인자는 아래와 같다.</p>							
					<div class="table-responsive">
					  <table class="table table-striped">
					    <thead>
					      <tr>
					        <th>Type</th>
					        <th>Parameter</th> 
					        <th>Description</th>
					      </tr>
					    </thead>
					    <tbody>
					      <tr>
					        <td>Object</td>
					        <td>source</td> 
					        <td>이벤트를 발생시키는 객체</td>
					      </tr>
					      <tr>
					        <td>int</td>
					        <td>objectType</td> 
					        <td>객체 종류를 구분하기 위한 값을 의미한다.</td>
					      </tr>     
					      <tr>
					        <td>long</td>
					        <td>objectId</td> 
					        <td>객체를 구분하기위한 유니크한 값을 의미한다.</td>
					      </tr>       
					    </tbody>
					  </table>
					</div>

					<hr>
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-3" >함수 호출을 이용한 조회 수 기록</h2>
					</div>
					<p><span class="g-color-primary">ViewCountService</span> 서비스의 <span class="g-color-primary">addViewCount</span> 함수를 호출하여 특정 객체의 조회수를 기록할 수 있다. 조회수는 호출과 동시에 1 증가한다.</p>	
						<pre><code class="language-java g-font-size-12">
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.viewcount.ViewCountService;

public class ViewCountExample {

	@Autowired(required=false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	public void addViewCount() {
		
		// 1. object type for view 
		int objectType = 0  ;
		
		// 2. object id for view 
		long objectId = 0 ;
		viewCountService.addViewCount(objectType, objectId);
		
	}
}						
</code></pre>

<p>addViewCount 함수 호출 파라메터는 아래와 같다.</p>							
					<div class="table-responsive">
					  <table class="table table-striped">
					    <thead>
					      <tr>
					        <th>Type</th>
					        <th>Parameter</th> 
					        <th>Description</th>
					      </tr>
					    </thead>
					    <tbody>
					      <tr>
					        <td>int</td>
					        <td>objectType</td> 
					        <td>객체 종류를 구분하기 위한 값을 의미한다.</td>
					      </tr>     
					      <tr>
					        <td>long</td>
					        <td>objectId</td> 
					        <td>객체를 구분하기위한 유니크한 값을 의미한다.</td>
					      </tr>       
					    </tbody>
					  </table>
					</div>


					<hr>
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="text-uppercase h5 u-heading-v5__title" id="guide-4" >조회수 조회하기</h2>
					</div>
					<p>뷰 카우터는 실시간으로 데이터베이스에 반영되지는 않으며 3분 주기로 배치 작업을 통하여 반영된다. (WEB-INF/context-config/schedulerSubsystemContext.xml) 이런 이유에서 특정 객체의 뷰 카운터 수는 반듯이 API 를 사용해야 한다.</p>
						<pre><code class="language-java g-font-size-12">
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.viewcount.ViewCountService;

public class ViewCountExample {

	@Autowired(required=false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;
	
	public void addViewCount() {
		
		// 1. object type for view 
		int objectType = 0  ;
		
		// 2. object id for view 
		long objectId = 0 ;
		
		int viewCount = viewCountService.getViewCount(objectType, objectId);
		
		
	}
}						
</code></pre>						
			  			  
					</div>	
					<!-- End Using Service -->					
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