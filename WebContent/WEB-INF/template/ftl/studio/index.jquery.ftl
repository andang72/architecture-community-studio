<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Title -->
  <title>Dashboard v.1 | Studio Template</title>
  <!-- Required Meta Tags Always Come First -->
  <meta name="decorator" content="<@spring.url "/decorators/studio/decorator.jsp"/>">
  <!-- Favicon -->
  <link rel="shortcut icon" href="../favicon.ico">
  <!-- Google Fonts -->
  <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans%3A400%2C300%2C500%2C600%2C700%7CPlayfair+Display%7CRoboto%7CRaleway%7CSpectral%7CRubik">
 
 <!-- CSS Commons -->
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/bootstrap/4.3.1/bootstrap.min.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/animate/animate.min.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/css/jquery.fancybox/jquery.fancybox.min.css"/>">
  
  <!-- CSS Unify -->
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-awesome/css/font-awesome.min.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-etlinefont/style.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-line-pro/style.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/icon-hs/style.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/hs-admin-icons/hs-admin-icons.css"/>"> 
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/malihu-scrollbar/jquery.mCustomScrollbar.min.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/vendor/hamburgers/hamburgers.min.css"/>">
  <link rel="stylesheet" type="text/css" href="<@spring.url "/assets/unify.admin/2.6.2/css/unify-admin.css"/>">

  <!-- CSS Customization -->
  <link rel="stylesheet" href="<@spring.url "/css/community.ui.studio/custom.css"/>">
</head>

<body>
  <!-- Header -->
  <header id="js-header" class="u-header u-header--sticky-top">
    <div class="u-header__section u-header__section--admin-dark g-min-height-65">
      <nav class="navbar no-gutters g-pa-0">
        <div class="col-auto d-flex flex-nowrap u-header-logo-toggler g-py-12">
          <!-- Logo -->
          <a href="index" class="navbar-brand d-flex align-self-center g-hidden-xs-down g-line-height-1 py-0 g-mt-5"> 
			<svg width="56px" height="47px" viewBox="0 0 256 247" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" preserveAspectRatio="xMidYMid"> 
				<path d="M55.2156796,246.742359 L32.426014,246.742359 C32.426014,226.897142 44.0216546,209.771059 60.8031821,201.751186 C55.3919808,209.722777 52.2258757,219.343259 52.2258757,229.704792 C52.2258757,235.689521 53.282951,241.424794 55.2156796,246.742359 Z M181.010566,246.742359 L158.220901,246.742359 C158.220901,226.897142 169.816541,209.771059 186.600995,201.751186 C181.189794,209.722777 178.020762,219.343259 178.020762,229.704792 C178.020762,235.689521 179.081495,241.424794 181.010566,246.742359 Z M245.98156,233.018668 C239.936845,212.300724 221.134072,196.899612 198.463648,196.899612 C170.938724,196.899612 148.62017,219.212314 148.62017,246.737238 L122.619044,246.737238 C122.616117,219.212314 100.301221,196.899612 72.7762973,196.899612 C45.2521048,196.899612 22.933551,219.212314 22.933551,246.737238 L1.84544748,246.737238 C0.637674944,239.669098 0.00123514517,232.412953 0.00123514517,225.001721 C0.00123514517,196.28585 9.45931579,169.772646 25.4259078,148.415336 C14.5354718,140.349376 6.12276185,128.756661 2.14464734,114.695 C-6.32146506,84.8028121 11.0313952,53.7569861 40.9177304,45.2996522 C48.0085476,43.293038 52.4906931,35.9293564 50.4811528,28.8414653 C48.4745385,21.7506481 40.7443554,17.6357356 33.6564643,19.6423498 C28.5751875,21.0798259 23.2861533,18.1302567 21.8508718,13.0460537 C20.409738,7.96185068 23.3571127,2.67866881 28.4442418,1.23753501 C45.7093173,-3.6476891 63.6422889,6.36416732 68.527513,23.6292428 C73.4156633,40.8913922 63.3950284,58.8214376 46.1299528,63.7103194 C31.1867853,67.9393521 22.5099894,83.4597047 26.7390221,98.4050669 C30.1326362,110.386961 38.6470302,119.518043 49.2309509,124.10553 C70.9489104,107.124292 98.2953386,97.0012416 127.99952,97.0012416 C151.201774,97.0012416 172.959969,103.173976 191.725433,113.964923 C193.205338,99.7094027 201.351036,87.1759279 213.341708,80.4633169 C213.667975,81.7932566 213.949618,83.1488002 214.162496,84.5299477 C215.719945,94.6734814 213.793068,104.50099 209.345305,112.756419 C211.991285,111.750551 214.759433,110.977313 217.66072,110.534 C227.764751,108.979478 237.603964,111.292607 245.77746,116.40534 C239.805899,122.228398 232.118877,126.289909 223.368195,127.637405 C219.199149,128.276771 215.077652,128.248241 211.099538,127.648378 C238.57618,151.123497 256,186.028928 256,225.001721 C256,227.607467 255.926114,230.216138 255.770296,232.780186 C255.645203,235.704152 253.905601,237.600303 251.313755,237.73637 C248.715325,237.870241 247.198111,236.252807 245.98156,233.018668 Z M207.139712,160.928327 C207.139712,155.886554 203.056256,151.79944 198.01375,151.79944 C192.974903,151.79944 188.884863,155.886554 188.884863,160.928327 C188.884863,165.967907 192.974903,170.057215 198.01375,170.057215 C203.056256,170.056484 207.139712,165.967907 207.139712,160.928327 Z" fill="#027AC3" fill-rule="nonzero"></path>
				</g>
			</svg> 
          </a>
          <!-- End Logo --> 
          <!-- Sidebar Toggler -->
          <a class="js-side-nav u-header__nav-toggler d-flex align-self-center ml-auto" href="#" data-hssm-class="u-side-nav--mini u-sidebar-navigation-v1--mini" data-hssm-body-class="u-side-nav-mini" data-hssm-is-close-all-except-this="true" data-hssm-target="#sideNav">
            <i class="hs-admin-align-left"></i>
          </a>
          <!-- End Sidebar Toggler -->
        </div>

        <!-- Top Search Bar -->
        <!--
        <form id="searchMenu" class="u-header--search col-sm g-py-12 g-ml-15--sm g-ml-20--md g-mr-10--sm" aria-labelledby="searchInvoker" action="#!">
          <div class="input-group g-max-width-450--sm">
            <input class="form-control h-100 form-control-md g-rounded-4 g-pr-40" type="text" placeholder="Enter search keywords">
            <button type="submit" class="btn u-btn-outline-primary g-brd-none g-bg-transparent--hover g-pos-abs g-top-0 g-right-0 d-flex g-width-40 h-100 align-items-center justify-content-center g-font-size-18 g-z-index-2"><i class="hs-admin-search"></i>
            </button>
          </div>
        </form>
        -->
        <!-- End Top Search Bar -->

        <!-- Messages/Notifications/Top Search Bar/Top User -->
        <div class="col-auto d-flex g-py-12 g-pl-40--lg ml-auto">
          <!-- Top Messages -->
          <div class="g-pos-rel g-hidden-sm-down g-mr-5">
            <a id="messagesInvoker" class="d-block text-uppercase u-header-icon-v1 g-pos-rel g-width-40 g-height-40 rounded-circle g-font-size-20" href="#" aria-controls="messagesMenu" aria-haspopup="true" aria-expanded="false" data-dropdown-event="click" data-dropdown-target="#messagesMenu"
            data-dropdown-type="css-animation" data-dropdown-duration="300" data-dropdown-animation-in="fadeIn" data-dropdown-animation-out="fadeOut">
              <span class="u-badge-v1 g-top-7 g-right-7 g-width-18 g-height-18 g-bg-primary g-font-size-10 g-color-white rounded-circle p-0">0</span>
              <i class="hs-admin-comment-alt g-absolute-centered"></i>
            </a> 
            <!-- Top Messages List -->
            <div id="messagesMenu" class="g-absolute-centered--x g-width-340 g-max-width-400 g-mt-17 rounded" aria-labelledby="messagesInvoker">
              <div class="media u-header-dropdown-bordered-v1 g-pa-20">
                <h4 class="d-flex align-self-center text-uppercase g-font-size-default g-letter-spacing-0_5 g-mr-20 g-mb-0">no messages</h4>
                <div class="media-body align-self-center text-right">
                  <a class="g-color-secondary" href="#">View All</a>
                </div>
              </div> 
              <ul class="p-0 mb-0"> 
              </ul>
            </div>
            <!-- End Top Messages List -->
          </div>
          <!-- End Top Messages -->

          <!-- Top Notifications -->
          <div class="g-pos-rel g-hidden-sm-down">
            <a id="notificationsInvoker" class="d-block text-uppercase u-header-icon-v1 g-pos-rel g-width-40 g-height-40 rounded-circle g-font-size-20" href="#" aria-controls="notificationsMenu" aria-haspopup="true" aria-expanded="false" data-dropdown-event="click"
            data-dropdown-target="#notificationsMenu" data-dropdown-type="css-animation" data-dropdown-duration="300" data-dropdown-animation-in="fadeIn" data-dropdown-animation-out="fadeOut">
              <i class="hs-admin-bell g-absolute-centered"></i>
            </a> 
            <!-- Top Notifications List -->
            <div id="notificationsMenu" class="js-custom-scroll g-absolute-centered--x g-width-340 g-max-width-400 g-height-400 g-mt-17 rounded" aria-labelledby="notificationsInvoker">
              <div class="media text-uppercase u-header-dropdown-bordered-v1 g-pa-20">
                <h4 class="d-flex align-self-center g-font-size-default g-letter-spacing-0_5 g-mr-20 g-mb-0">Notifications</h4>
              </div>
              <ul class="p-0 mb-0">
                <!-- Top Notifications List Item -->
                <li class="media u-header-dropdown-item-v1 g-parent g-px-20 g-py-15">
                  <div class="d-flex align-self-center u-header-dropdown-icon-v1 g-pos-rel g-width-55 g-height-55 g-font-size-22 rounded-circle g-mr-15">
                    <i class="hs-admin-bookmark-alt g-absolute-centered"></i>
                  </div>

                  <div class="media-body align-self-center">
                    <p class="mb-0">A Pocket PC is a handheld computer features</p>
                  </div>

                  <a class="d-flex g-color-lightblue-v2 g-font-size-12 opacity-0 g-opacity-1--parent-hover g-transition--ease-in g-transition-0_2" href="#">
                    <i class="hs-admin-close"></i>
                  </a>
                </li>
                <!-- End Top Notifications List Item -->

                <!-- Top Notifications List Item -->
                <li class="media u-header-dropdown-item-v1 g-parent g-px-20 g-py-15">
                  <div class="d-flex align-self-center u-header-dropdown-icon-v1 g-pos-rel g-width-55 g-height-55 g-font-size-22 rounded-circle g-mr-15">
                    <i class="hs-admin-blackboard g-absolute-centered"></i>
                  </div>

                  <div class="media-body align-self-center">
                    <p class="mb-0">The first is a non technical method which requires</p>
                  </div>

                  <a class="d-flex g-color-lightblue-v2 g-font-size-12 opacity-0 g-opacity-1--parent-hover g-transition--ease-in g-transition-0_2" href="#">
                    <i class="hs-admin-close"></i>
                  </a>
                </li>
                <!-- End Top Notifications List Item -->

                <!-- Top Notifications List Item -->
                <li class="media u-header-dropdown-item-v1 g-parent g-px-20 g-py-15">
                  <div class="d-flex align-self-center u-header-dropdown-icon-v1 g-pos-rel g-width-55 g-height-55 g-font-size-22 rounded-circle g-mr-15">
                    <i class="hs-admin-calendar g-absolute-centered"></i>
                  </div>

                  <div class="media-body align-self-center">
                    <p class="mb-0">Stu Unger is of the biggest superstarsis</p>
                  </div>

                  <a class="d-flex g-color-lightblue-v2 g-font-size-12 opacity-0 g-opacity-1--parent-hover g-transition--ease-in g-transition-0_2" href="#">
                    <i class="hs-admin-close"></i>
                  </a>
                </li>
                <!-- End Top Notifications List Item -->

                <!-- Top Notifications List Item -->
                <li class="media u-header-dropdown-item-v1 g-parent g-px-20 g-py-15">
                  <div class="d-flex align-self-center u-header-dropdown-icon-v1 g-pos-rel g-width-55 g-height-55 g-font-size-22 rounded-circle g-mr-15">
                    <i class="hs-admin-pie-chart g-absolute-centered"></i>
                  </div>

                  <div class="media-body align-self-center">
                    <p class="mb-0">Sony laptops are among the most well known laptops</p>
                  </div>

                  <a class="d-flex g-color-lightblue-v2 g-font-size-12 opacity-0 g-opacity-1--parent-hover g-transition--ease-in g-transition-0_2" href="#">
                    <i class="hs-admin-close"></i>
                  </a>
                </li>
                <!-- End Top Notifications List Item -->
                <!-- Top Notifications List Item -->
                <li class="media u-header-dropdown-item-v1 g-parent g-px-20 g-py-15">
                  <div class="d-flex align-self-center u-header-dropdown-icon-v1 g-pos-rel g-width-55 g-height-55 g-font-size-22 rounded-circle g-mr-15">
                    <i class="hs-admin-bookmark-alt g-absolute-centered"></i>
                  </div>

                  <div class="media-body align-self-center">
                    <p class="mb-0">A Pocket PC is a handheld computer features</p>
                  </div>

                  <a class="d-flex g-color-lightblue-v2 g-font-size-12 opacity-0 g-opacity-1--parent-hover g-transition--ease-in g-transition-0_2" href="#">
                    <i class="hs-admin-close"></i>
                  </a>
                </li>
                <!-- End Top Notifications List Item -->

                <!-- Top Notifications List Item -->
                <li class="media u-header-dropdown-item-v1 g-parent g-px-20 g-py-15">
                  <div class="d-flex align-self-center u-header-dropdown-icon-v1 g-pos-rel g-width-55 g-height-55 g-font-size-22 rounded-circle g-mr-15">
                    <i class="hs-admin-blackboard g-absolute-centered"></i>
                  </div>

                  <div class="media-body align-self-center">
                    <p class="mb-0">The first is a non technical method which requires</p>
                  </div>

                  <a class="d-flex g-color-lightblue-v2 g-font-size-12 opacity-0 g-opacity-1--parent-hover g-transition--ease-in g-transition-0_2" href="#">
                    <i class="hs-admin-close"></i>
                  </a>
                </li>
                <!-- End Top Notifications List Item -->
              </ul>
            </div>
            <!-- End Top Notifications List -->
          </div>
          <!-- End Top Notifications -->

          <!-- Top Search Bar (Mobi) -->
          <a id="searchInvoker" class="g-hidden-sm-up text-uppercase u-header-icon-v1 g-pos-rel g-width-40 g-height-40 rounded-circle g-font-size-20" href="#" aria-controls="searchMenu" aria-haspopup="true" aria-expanded="false" data-is-mobile-only="true" data-dropdown-event="click"
          data-dropdown-target="#searchMenu" data-dropdown-type="css-animation" data-dropdown-duration="300" data-dropdown-animation-in="fadeIn" data-dropdown-animation-out="fadeOut">
            <i class="hs-admin-search g-absolute-centered"></i>
          </a>
          <!-- End Top Search Bar (Mobi) -->

          <!-- Top User -->
          <div class="col-auto d-flex g-pt-5 g-pt-0--sm g-pl-10 g-pl-20--sm">
            <div class="g-pos-rel g-px-10--lg">
              <a id="profileMenuInvoker" class="d-block" href="#" aria-controls="profileMenu" aria-haspopup="true" aria-expanded="false" data-dropdown-event="click" data-dropdown-target="#profileMenu" data-dropdown-type="css-animation" data-dropdown-duration="300"
              data-dropdown-animation-in="fadeIn" data-dropdown-animation-out="fadeOut">
                <span class="g-pos-rel">
       				<span class="u-badge-v2--xs u-badge--top-right g-hidden-sm-up g-bg-secondary g-mr-5"></span>
                	<img class="g-width-30 g-width-40--md g-height-30 g-height-40--md rounded-circle g-mr-10--sm" src="assets/img-temp/130x130/img1.jpg" alt="Image description">
                </span>
                <span class="g-pos-rel g-top-2">
        			<span class="g-hidden-sm-down">Charlie Bailey</span>
                	<i class="hs-admin-angle-down g-pos-rel g-top-2 g-ml-10"></i>
                </span>
              </a>

              <!-- Top User Menu -->
              <ul id="profileMenu" class="g-pos-abs g-left-0 g-width-100x--lg g-nowrap g-font-size-14 g-py-20 g-mt-17 rounded" aria-labelledby="profileMenuInvoker">
                <li class="g-hidden-sm-up g-mb-10">
                  <a class="media g-py-5 g-px-20" href="#">
                    <span class="d-flex align-self-center g-pos-rel g-mr-12">
          <span class="u-badge-v1 g-top-minus-3 g-right-minus-3 g-width-18 g-height-18 g-bg-secondary g-font-size-10 g-color-white rounded-circle p-0">10</span>
                    <i class="hs-admin-comment-alt"></i>
                    </span>
                    <span class="media-body align-self-center">Unread Messages</span>
                  </a>
                </li>
                <li class="g-hidden-sm-up g-mb-10">
                  <a class="media g-py-5 g-px-20" href="#">
                    <span class="d-flex align-self-center g-mr-12">
			          <i class="hs-admin-bell"></i>
			        </span>
                    <span class="media-body align-self-center">Notifications</span>
                  </a>
                </li>
                <li class="g-mb-10">
                  <a class="media g-color-primary--hover g-py-5 g-px-20" href="#">
                    <span class="d-flex align-self-center g-mr-12">
			          <i class="hs-admin-user"></i>
			        </span>
                    <span class="media-body align-self-center">My Profile</span>
                  </a>
                </li>
                <li class="mb-0">
                  <a class="media g-color-primary--hover g-py-5 g-px-20" href="<@spring.url "/accounts/logout" />">
                    <span class="d-flex align-self-center g-mr-12">
          			<i class="hs-admin-shift-right"></i>
        			</span>
                    <span class="media-body align-self-center">Sign Out</span>
                  </a>
                </li>
              </ul>
              <!-- End Top User Menu -->
            </div>
          </div>
          <!-- End Top User -->
        </div>
        <!-- End Messages/Notifications/Top Search Bar/Top User --> 
      </nav> 
    </div>
  </header>
  <!-- End Header -->


  <main class="container-fluid px-0 g-pt-65">
    <div class="row no-gutters g-pos-rel g-overflow-x-hidden">
      <!-- Sidebar Nav -->
      <div id="sideNav" class="col-auto u-sidebar-navigation-v1 u-sidebar-navigation--dark">
      	<ul id="sideNavMenu" class="u-sidebar-navigation-v1-menu u-side-nav--top-level-menu g-min-height-100vh mb-0">
      <#assign sidebarNav = CommunityContextHelper.getAdminService().getMenu("STUDIO.SIDEBAR") />	
      <#list sidebarNav.components as item >
          <!-- ${item.name} -->      
   	  <#if item.components?has_content >
          <!-- Dashboards -->
          <li class="u-sidebar-navigation-v1-menu-item u-side-nav--has-sub-menu u-side-nav--top-level-menu-item">
            <a class="media u-side-nav--top-level-menu-link u-side-nav--hide-on-hidden g-px-15 g-py-12" href="#" data-hssm-target="#${item.name}">
              <span class="d-flex align-self-center g-pos-rel g-font-size-18 g-mr-18">
      			<i class="hs-admin-folder"></i>
    		  </span>
              <span class="media-body align-self-center">${item.title}</span>
              <span class="d-flex align-self-center u-side-nav--control-icon">
      		  	<i class="hs-admin-angle-right"></i>
    		  </span>
              <span class="u-side-nav--has-sub-menu__indicator"></span>
            </a>
            <!-- ${item.name}:  Sub -->
            <ul id="${item.name}" class="u-sidebar-navigation-v1-menu u-side-nav--second-level-menu mb-0">
   	  <#list item.components as item2>
              <!-- ${item2.name} -->
              <li class="u-sidebar-navigation-v1-menu-item u-side-nav--second-level-menu-item">
                <a class="media u-side-nav--second-level-menu-link g-px-15 g-py-12" href="<#if item2.location?? >${item2.location}<#else>#</#if>">
                  <span class="d-flex align-self-center g-mr-15 g-mt-minus-1">
          			<i class="<#if item2.icon?? >${item2.icon}<#else>hs-admin-settings</#if>"></i>
        		  </span>
                  <span class="media-body align-self-center">${item2.title}</span>
                </a>
              </li>
              <!-- End ${item2.name} --> 
   	  </#list>
            </ul>
            <!-- End ${item.name}: Sub -->
         </li>  
   	  <#else>
          <li class="u-sidebar-navigation-v1-menu-item u-side-nav--top-level-menu-item">
            <a class="media u-side-nav--top-level-menu-link u-side-nav--hide-on-hidden g-px-15 g-py-12" href="<#if item.location?? >${item.location}<#else>#</#if>">
              <span class="d-flex align-self-center g-font-size-18 g-mr-18">
      			<i class="<#if item.icon?? >${item.icon}<#else>hs-admin-settings</#if>"></i>
    		  </span>
              <span class="media-body align-self-center">${item.title}</span>
            </a>
          </li>
   	  </#if>
          <!-- End ${item.name} -->    	     	  
   	  </#list>
        </ul>
      </div>
      <!-- End Sidebar Nav -->


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
         
         </div>
          <!-- End Content --> 
        </div>

        <!-- Footer -->
        <footer id="footer" class="u-footer--bottom-sticky g-bg-white g-color-gray-dark-v6 g-brd-top g-brd-gray-light-v7 g-pa-20">
          <div class="row align-items-center">
            <!-- Footer Nav -->
            <div class="col-md-4 g-mb-10 g-mb-0--md">
              <ul class="list-inline text-center text-md-left mb-0">
                <!--
                <li class="list-inline-item">
                  <a class="g-color-gray-dark-v6 g-color-secondary--hover" href="#">FAQ</a>
                </li>
                <li class="list-inline-item">
                  <span class="g-color-gray-dark-v6">|</span>
                </li>
                -->
                <li class="list-inline-item">
                  <a class="g-color-gray-dark-v6 g-color-secondary--hover" href="#">Support</a>
                </li>
                <li class="list-inline-item">
                  <span class="g-color-gray-dark-v6">|</span>
                </li>
                <li class="list-inline-item">
                  <a class="g-color-gray-dark-v6 g-color-secondary--hover" href="mailto:helpdesk@podosw.com?Subject=ISSUE">Contact Us</a>
                </li>
              </ul>
            </div>
            <!-- End Footer Nav -->

            <!-- Footer Socials -->
            <!--
            <div class="col-md-4 g-mb-10 g-mb-0--md">
              <ul class="list-inline g-font-size-16 text-center mb-0">
                <li class="list-inline-item g-mx-10">
                  <a href="#" class="g-color-facebook g-color-secondary--hover">
                    <i class="fa fa-facebook-square"></i>
                  </a>
                </li>
                <li class="list-inline-item g-mx-10">
                  <a href="#" class="g-color-google-plus g-color-secondary--hover">
                    <i class="fa fa-google-plus"></i>
                  </a>
                </li>
                <li class="list-inline-item g-mx-10">
                  <a href="#" class="g-color-black g-color-secondary--hover">
                    <i class="fa fa-github"></i>
                  </a>
                </li>
                <li class="list-inline-item g-mx-10">
                  <a href="#" class="g-color-twitter g-color-secondary--hover">
                    <i class="fa fa-twitter"></i>
                  </a>
                </li>
              </ul>
            </div>
            -->
            <!-- End Footer Socials -->

            <!-- Footer Copyrights -->
            <!--
            <div class="col-md-4 text-center text-md-right">
              <small class="d-block g-font-size-default">&copy; 2019 . All Rights Reserved.</small>
            </div>
            -->
            <!-- End Footer Copyrights -->
          </div>
        </footer>
        <!-- End Footer -->
      </div>
    </div>
  </main>

  <!-- JS Global Compulsory -->
  <script src="<@spring.url "/js/jquery/jquery-3.4.1.min.js"/>"></script>
  <script src="<@spring.url "/js/bootstrap/4.3.1/bootstrap.bundle.min.js"/>"></script> 
  <script src="<@spring.url "/js/jquery.cookie/1.4.1/jquery.cookie.js"/>"></script>
  <script src="<@spring.url "/js/jquery.fancybox/jquery.fancybox.min.js"/>"></script>  
  <!-- JS Unify -->
  <script src="<@spring.url "/assets/unify.admin/2.6.2/vendor/malihu-scrollbar/jquery.mCustomScrollbar.concat.min.js"/>"></script>
  <script src="<@spring.url "/assets/unify/2.6.2/js/hs.core.js"/>"></script>
  <script src="<@spring.url "/assets/unify.admin/2.6.2/js/components/hs.side-nav.js"/>"></script>
  <script src="<@spring.url "/assets/unify/2.6.2/js/helpers/hs.hamburgers.js"/>"></script>
  <script src="<@spring.url "/assets/unify/2.6.2/js/components/hs.dropdown.js"/>"></script>
  <script src="<@spring.url "/assets/unify/2.6.2/js/components/hs.scrollbar.js"/>"></script> 
  <script src="<@spring.url "/assets/unify/2.6.2/js/helpers/hs.focus-state.js"/>"></script>
  <script src="<@spring.url "/assets/unify.admin/2.6.2/js/components/hs.popup.js"/>"></script>
  
  <!-- JS Custom -->
  <script src="<@spring.url "/js/community.ui.studio/custom.js"/>"></script>
  
  <!-- JS Plugins Init. -->
  <script> 
  	$(document).ready(function() {  
  	  console.log("Initialization HS."); 
      // initialization of hamburger
      $.HSCore.helpers.HSHamburgers.init('.hamburger'); 
      // initialization of sidebar navigation component
      $.HSCore.components.HSSideNav.init('.js-side-nav', {
        afterOpen: function() {
        },
        afterClose: function() {
        }
      });
      // initialization of HSDropdown component
      $.HSCore.components.HSDropdown.init($('[data-dropdown-target]'), {dropdownHideOnScroll: false}); 
      // initialization of custom scrollbar
      $.HSCore.components.HSScrollBar.init($('.js-custom-scroll')); 
      // initialization of popups
      $.HSCore.components.HSPopup.init('.js-fancybox', {
        btnTpl: {
          smallBtn: '<button data-fancybox-close class="btn g-pos-abs g-top-25 g-right-30 g-line-height-1 g-bg-transparent g-font-size-16 g-color-gray-light-v3 g-brd-none p-0" title=""><i class="hs-admin-close"></i></button>'
        }
      }); 
  	}); 
  </script>
</body>

</html>