<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <#assign PAGE_NAME = "Working with Sequencer" />	
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
					<li class="toc-entry toc-h2"><a href="#guide-1">What is Sequencer</a>   
						<ul>
							<li class="toc-entry toc-h2"><a href="#guide-2">Using Sequencer Factory</a>
							<li class="toc-entry toc-h2"><a href="#guide-3">Using CustomQueryService</a> 
						</ul>
					</li>
					</ul>
	         	</nav> 
				<main class="col-12 col-md-9 col-xl-10 py-md-3 pl-md-5 bd-content" role="main"> 
					<div class="u-heading-v2-3--bottom g-mb-30">
                      <h2 class="u-heading-v2__title g-mb-10" id="guide-1">What is Sequencer</h2>
                      <h4 class="g-font-weight-200 g-mb-10"></h4>
                    </div>
					<p>Sequencer 는 유일한 숫자값 생성을 위한 서비스로 <span class="g-color-primary">SequencerFactory</span> 를 통하여 제공된다.
					Sequencer 사용은 <span class="g-color-primary">SequencerFactory</span> 를 사용하거나 <span class="g-color-primary">CustomQueryService</span> 사용하는 방법이 있다. 
					Sequencer 사용전에 데이터베이스 테이블에 원하는 시퀀서 정보를 입력한 다음 사용하는 것을 권장하고 있다. 
					</p> 
					<p>Sequencer 데이터가 저장되는 "AC_UI_SEQUENCER" 테이블에 대한 명세는 아래와 같다.</p> 
<div class="table-responsive">
					  <table class="table table-striped">
					    <thead>
					      <tr>
					        <th>Column</th>
					        <th>Type</th> 
					        <th>Description</th>
					      </tr>
					    </thead>
					    <tbody>
					      <tr>
					        <td>SEQUENCER_ID</td>
					        <td>INTEGER</td> 
					        <td>유일한 시퀀서 ID 값. 100번 이후 값 사용을 권장</td>
					      </tr>
					      <tr>
					        <td>NAME</td>
					        <td>VARCHAR2(100)</td> 
					        <td>유일한 시퀀서 이름. 영문 대분자 입력을 권장한다.</td>
					      </tr>     
					      <tr>
					        <td>DISPLAY_NAME</td>
					        <td>VARCHAR(255)</td> 
					        <td>가독성을 위하여 사용되는 이름.</td>
					      </tr>  
					      <tr>
					        <td>VALUE</td>
					        <td>INTEGER</td> 
					        <td>유일한 시퀀서 값.</td>
					      </tr>  					           
					    </tbody>
					  </table>
					</div>
					<p>아래는 "TB_BOARD_SEQ" 이름의 시퀀서를 1000 ID 값으로 추가하는 SQL이다.</p> 					
<pre><code class="language-sql g-font-size-12">Insert into AC_UI_SEQUENCER (SEQUENCER_ID,NAME, DISPLAY_NAME, VALUE) values (1000,'TB_BOARD_SEQ','공지사항 게시판 게시불 순번',1);</code></pre>					
															
					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="h5 u-heading-v5__title" id="guide-2" >Using Sequencer Factory</h2>
					</div>
					
                    <p>getNextValue 함수를 사용하며 이름 또는 ID 값을 사용하여 꺼낼 수 있다.</p>

<pre><code class="language-java g-font-size-12">
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.model.Models;
import architecture.ee.jdbc.sequencer.SequencerFactory;

public class SequencerExample { 

	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;
 
	public void test() {
		long nextId = sequencerFactory.getNextValue("SEQUENCER_NAME");
		// or long nextId = sequencerFactory.getNextValue(3);
	}
}
						</code></pre>					

					<div class="u-heading-v5-2 g-mb-20">
					  <h2 class="h5 u-heading-v5__title" id="guide-3" >Using CustomQueryService</h2>
					</div>
					
                    <p>getNextId 함수를 사용하며 이름 또는 ID 값을 사용하여 꺼낼 수 있다. </p>
<pre><code class="language-java g-font-size-12">				
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;

import architecture.community.query.CustomQueryService;
import architecture.community.query.CustomTransactionCallback;
import architecture.community.query.CustomTransactionCallbackWithoutResult;
import architecture.community.query.dao.CustomQueryJdbcDao;

public class SequencerExample { 

	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	public void test() {
		
		// METHOD 1 
		customQueryService.execute(new CustomTransactionCallback() { 
			public Object doInTransaction(CustomQueryJdbcDao dao) throws DataAccessException {
				long nextId = dao.getNextId("SEQUENCER_NAME");
				// DO SOMETHING. 
				return null;
			}});
		
		// METHOD 2 
		customQueryService.execute(new CustomTransactionCallbackWithoutResult() { 
			protected void doInTransactionWithoutResult(CustomQueryJdbcDao dao) {  
				long nextId = dao.getNextId("SEQUENCER_NAME");
				// DO SOMETHING.
			}});
		
	}
}
						</code></pre>
	
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