<!DOCTYPE html>
<html lang="en"> 
<head>
	<!-- Title -->
	<title><#if __page??>${__page.title}</#if></title> 
	<!-- Required Meta Tags Always Come First -->
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta http-equiv="x-ua-compatible" content="ie=edge"> 
	<!-- Favicon -->
	 
	<!-- Google Fonts -->
	<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800">
	<!-- CSS Global Compulsory -->
	<link rel="stylesheet" type="text/css" href="<@spring.url "/css/bootstrap/4.3.1/bootstrap.min.css"/>">
	<!-- CSS Global Icons -->
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-awesome/css/font-awesome.min.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line/css/simple-line-icons.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-etlinefont/style.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line-pro/style.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-hs/style.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/dzsparallaxer/dzsparallaxer.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/dzsparallaxer/dzsscroller/scroller.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/dzsparallaxer/advancedscroller/plugin.css"/>">
	<link rel="stylesheet" href="<@spring.url "/css/animate/animate.min.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/hamburgers/hamburgers.min.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/hs-megamenu/src/hs.megamenu.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/slick-carousel/slick/slick.css"/>">
	<link rel="stylesheet" href="<@spring.url "/css/jquery.fancybox/jquery.fancybox.min.css"/>">
	
	<!-- CSS Unify -->
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/css/unify-core.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/css/unify-components.css"/>">
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/css/unify-globals.css"/>">
	
	<!-- CSS Customization -->
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/css/custom.css"/>">
	<style>

	.hamburger--collapse.hamburger { 
	}
	
	.hamburger--collapse .hamburger-inner {
		background-color: white;
	}
	
	.hamburger--collapse.is-active .hamburger-inner {
		background-color: #0a0a0a7d;
	}
	
	</style>
</head> 
<body>
	<main> 
		<!-- Logo --> 
		<a class="navbar-brand g-pos-abs g-top-20 g-left-20 g-z-index-2" href="/">
			<svg width="56px" height="47px" viewBox="0 0 256 247" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" preserveAspectRatio="xMidYMid"> 
				<path d="M55.2156796,246.742359 L32.426014,246.742359 C32.426014,226.897142 44.0216546,209.771059 60.8031821,201.751186 C55.3919808,209.722777 52.2258757,219.343259 52.2258757,229.704792 C52.2258757,235.689521 53.282951,241.424794 55.2156796,246.742359 Z M181.010566,246.742359 L158.220901,246.742359 C158.220901,226.897142 169.816541,209.771059 186.600995,201.751186 C181.189794,209.722777 178.020762,219.343259 178.020762,229.704792 C178.020762,235.689521 179.081495,241.424794 181.010566,246.742359 Z M245.98156,233.018668 C239.936845,212.300724 221.134072,196.899612 198.463648,196.899612 C170.938724,196.899612 148.62017,219.212314 148.62017,246.737238 L122.619044,246.737238 C122.616117,219.212314 100.301221,196.899612 72.7762973,196.899612 C45.2521048,196.899612 22.933551,219.212314 22.933551,246.737238 L1.84544748,246.737238 C0.637674944,239.669098 0.00123514517,232.412953 0.00123514517,225.001721 C0.00123514517,196.28585 9.45931579,169.772646 25.4259078,148.415336 C14.5354718,140.349376 6.12276185,128.756661 2.14464734,114.695 C-6.32146506,84.8028121 11.0313952,53.7569861 40.9177304,45.2996522 C48.0085476,43.293038 52.4906931,35.9293564 50.4811528,28.8414653 C48.4745385,21.7506481 40.7443554,17.6357356 33.6564643,19.6423498 C28.5751875,21.0798259 23.2861533,18.1302567 21.8508718,13.0460537 C20.409738,7.96185068 23.3571127,2.67866881 28.4442418,1.23753501 C45.7093173,-3.6476891 63.6422889,6.36416732 68.527513,23.6292428 C73.4156633,40.8913922 63.3950284,58.8214376 46.1299528,63.7103194 C31.1867853,67.9393521 22.5099894,83.4597047 26.7390221,98.4050669 C30.1326362,110.386961 38.6470302,119.518043 49.2309509,124.10553 C70.9489104,107.124292 98.2953386,97.0012416 127.99952,97.0012416 C151.201774,97.0012416 172.959969,103.173976 191.725433,113.964923 C193.205338,99.7094027 201.351036,87.1759279 213.341708,80.4633169 C213.667975,81.7932566 213.949618,83.1488002 214.162496,84.5299477 C215.719945,94.6734814 213.793068,104.50099 209.345305,112.756419 C211.991285,111.750551 214.759433,110.977313 217.66072,110.534 C227.764751,108.979478 237.603964,111.292607 245.77746,116.40534 C239.805899,122.228398 232.118877,126.289909 223.368195,127.637405 C219.199149,128.276771 215.077652,128.248241 211.099538,127.648378 C238.57618,151.123497 256,186.028928 256,225.001721 C256,227.607467 255.926114,230.216138 255.770296,232.780186 C255.645203,235.704152 253.905601,237.600303 251.313755,237.73637 C248.715325,237.870241 247.198111,236.252807 245.98156,233.018668 Z M207.139712,160.928327 C207.139712,155.886554 203.056256,151.79944 198.01375,151.79944 C192.974903,151.79944 188.884863,155.886554 188.884863,160.928327 C188.884863,165.967907 192.974903,170.057215 198.01375,170.057215 C203.056256,170.056484 207.139712,165.967907 207.139712,160.928327 Z" fill="#027AC3" fill-rule="nonzero"></path>
			</svg>
		</a> 
		<!-- End Logo -->  
	<!-- Smart Menu -->
	<div class="js-smart-menu u-smart-nav u-smart-nav--top-right g-transition-0_3" data-fix-moment="0" id="accounts">
		<!-- Smart Menu Toggle Button -->  
		<#if SecurityHelper.isAnonymous() > 
		<a href="<@spring.url "/accounts/login"/>" class="btn btn-md u-btn-outline-bluegray g-mr-10">Login</a>
		<#else>
		<img class="profile g-width-40 g-height-40 rounded-circle g-mr-5" src="/images/no-avatar.png" alt="Image Description"> 
		<button class="u-smart-nav__toggler btn u-btn-transparent g-brd-0 g-pa-0" id="u-smart-menu-toggler-1" aria-label="Toggle navigation" aria-haspopup="true" aria-expanded="false" aria-controls="smart-menu-1">
			<span class="hamburger hamburger--collapse">
				<span class="hamburger-box">
					<span class="hamburger-inner"></span>
				</span>
			</span>
		</button>
		<!-- End Smart Menu Toggle Button --> 
		<!-- Navbar -->
		<#if CommunityContextHelper.getMenuService().hasMenu("MENU_FOR_USER") >
		<#assign treeWalker = CommunityContextHelper.getMenuService().getTreeWalker("MENU_FOR_USER") />	
		<nav class="navbar u-shadow-v17 g-bg-white g-rounded-10 g-pa-20 g-pb-15 g-transition-0_3" id="smart-menu-1" aria-labelledby="u-smart-menu-toggler-1">
			<ul class="navbar-nav text-uppercase">		
				<#list treeWalker.getChildren() as item >
				<li class="nav-item"><a href="${item.location}" class="nav-link">${item.name}</a></li> 
				</#list>
				<#if SecurityHelper.isUserInRole("ROLE_DEVELOPER,ROLE_ADMINISTRATOR,ROLE_SYSTEM,ROLE_OPERATOR") >	
				<li class="nav-item">
				<li class="nav-item"><a href="<@spring.url "/secure/studio/status"/>" class="nav-link">STUDIO</a></li> 
				</#if>
				<li class="nav-item g-mt-25"><a class="u-tags-v1 g-color-deeporange g-brd-around g-brd-deeporange g-bg-deeporange--hover g-color-white--hover g-rounded-50 g-py-4 g-px-15" href="<@spring.url "/accounts/logout"/>">Logout</a></li> 
			</ul>		
		</#if> 
		</nav>
		<!-- End Navbar -->
		</#if> 
	</div>
	<!-- End Smart Menu --> 
	<!-- Promo Block -->
	<section class="dzsparallaxer auto-init height-is-based-on-content use-loading mode-scroll loaded dzsprx-readyall g-min-height-100vh g-flex-middle" data-options='{direction: "reverse", settings_mode_oneelement_max_offset: "150"}'>
	<#if wallpapers??> 
	<#list wallpapers as wallpaper>
		<#if wallpaper?is_first> 
		<div class="divimage dzsparallaxer--target w-100 g-bg-pos-bottom-center" style="height: 120%; background-image: url('<@spring.url "/download/images/${wallpaper}"/>');"></div> 
		</#if>
	</#list>
	</#if>  
		<div class="container g-py-200 g-flex-middle-item">
			<div class="row">
				<div class="col-md-6">
					<h3
						class="g-color-black g-font-weight-300 g-font-size-40 g-line-height-1_2 mb-4">
						Have an easy <br> website with Me Drive
					</h3>
					<span class="d-block g-color-black-opacity-0_8 g-font-size-16 mb-5">Build, share, sell and enjoy.</span>
				</div>
			</div>
		</div>
	</section>
	<!-- End Promo Block -->
    <#if announces??> 
    <!-- Announces Block -->
    <div class="g-bg-gray-light-v4">
	<section class="container g-py-20"> 
		<div class="text-center g-mb-0">
			<h2 class="h1 g-color-black g-font-weight-600">Announces</h2>
			<p class="lead">Follow the latest news.</p>
		</div>
		<div id="accordion-announces" class="u-accordion" role="tablist" aria-multiselectable="true">
    <#list announces as announce>  
  <!-- Announce -->
  <div class="card rounded-0 g-bg-gray-light-v4 g-brd-none">
    <div id="accordion-announces-heading-${announce.announceId}" class="u-accordion__header g-pa-20" role="tab">
      <h5 class="mb-0 text-uppercase g-font-size-default g-font-weight-700">
          ${announce.subject} 
          <span class="u-accordion__control-icon g-ml-10"> 
            <i class="fa fa-angle-down"></i> 
            <i class="fa fa-angle-up"></i> 
          </span> 
        </a> 
      </h5> 
    </div>
    <div id="accordion-announces-body-${announce.announceId}" class="collapse show" role="tabpanel" aria-labelledby="accordion-announces-heading-${announce.announceId}" data-parent="#accordion-announces">
      <div class="u-accordion__body g-color-gray-dark-v5 g-pa-0-20-20">${announce.body}</div>
    </div>
  </div>
  <!-- End Announce -->	
    </#list>
    </section>
    </div>
    <!-- End Announces Block -->
    </#if>		 
	<!-- End Footer --> 
	<!-- Copyright Footer --> 
	<footer	class="g-bg-gray-dark-v1 g-color-white-opacity-0_8 g-py-20">
		<div class="container">
			<div class="row">
				<div class="col-md-8 text-center text-md-left g-mb-10 g-mb-0--md">
					<div class="d-lg-flex">
						<small
							class="d-block g-font-size-default g-mr-10 g-mb-10 g-mb-0--md">2019
							&copy; All Rights Reserved.</small>
						<ul class="u-list-inline">
							<li class="list-inline-item"><a
								class="g-color-white-opacity-0_8 g-color-white--hover" href="#">Privacy
									Policy</a></li>
							<li class="list-inline-item"><span>|</span></li>
							<li class="list-inline-item"><a
								class="g-color-white-opacity-0_8 g-color-white--hover" href="#">Terms
									of Use</a></li>
							<li class="list-inline-item"><span>|</span></li>
							<li class="list-inline-item"><a
								class="g-color-white-opacity-0_8 g-color-white--hover" href="#">License</a>
							</li>
							<li class="list-inline-item"><span>|</span></li>
							<li class="list-inline-item"><a
								class="g-color-white-opacity-0_8 g-color-white--hover" href="#">Support</a>
							</li>
						</ul>
					</div>
				</div>

				<div class="col-md-4 align-self-center">
					<ul class="list-inline text-center text-md-right mb-0">
						<li class="list-inline-item g-mx-10" data-toggle="tooltip"
							data-placement="top" title="Facebook"><a href="#"
							class="g-color-white-opacity-0_5 g-color-white--hover"> <i
								class="fa fa-facebook"></i>
						</a></li>
						<li class="list-inline-item g-mx-10" data-toggle="tooltip"
							data-placement="top" title="Skype"><a href="#"
							class="g-color-white-opacity-0_5 g-color-white--hover"> <i
								class="fa fa-skype"></i>
						</a></li>
						<li class="list-inline-item g-mx-10" data-toggle="tooltip"
							data-placement="top" title="Linkedin"><a href="#"
							class="g-color-white-opacity-0_5 g-color-white--hover"> <i
								class="fa fa-linkedin"></i>
						</a></li>
						<li class="list-inline-item g-mx-10" data-toggle="tooltip"
							data-placement="top" title="Pinterest"><a href="#"
							class="g-color-white-opacity-0_5 g-color-white--hover"> <i
								class="fa fa-pinterest"></i>
						</a></li>
						<li class="list-inline-item g-mx-10" data-toggle="tooltip"
							data-placement="top" title="Twitter"><a href="#"
							class="g-color-white-opacity-0_5 g-color-white--hover"> <i
								class="fa fa-twitter"></i>
						</a></li>
						<li class="list-inline-item g-mx-10" data-toggle="tooltip"
							data-placement="top" title="Dribbble"><a href="#"
							class="g-color-white-opacity-0_5 g-color-white--hover"> <i
								class="fa fa-dribbble"></i>
						</a></li>
					</ul>
				</div>
			</div>
		</div>
	</footer> 
	<!-- End Copyright Footer --> 
	<a class="js-go-to u-go-to-v1" href="#" data-type="fixed" data-position='{ "bottom": 15, "right": 15 }' data-offset-top="400" data-compensation="#js-header" data-show-effect="zoomIn"> <i class="hs-icon hs-icon-arrow-top"></i></a>
	</main>

	<!-- JS Global Compulsory -->
	<script type="text/javascript" src="<@spring.url "/js/jquery/jquery-3.4.1.min.js"/>"></script> 
	<script type="text/javascript" src="<@spring.url "/js/bootstrap/4.3.1/bootstrap.bundle.min.js"/>"></script> 

	<!-- JS Implementing Plugins -->
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/appear.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/dzsparallaxer/dzsparallaxer.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/dzsparallaxer/dzsscroller/scroller.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/dzsparallaxer/advancedscroller/plugin.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/masonry/dist/masonry.pkgd.min.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/imagesloaded/imagesloaded.pkgd.min.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/vendor/slick-carousel/slick/slick.js"/>"></script>
	<script src="<@spring.url "/js/jquery.fancybox/jquery.fancybox.min.js"/>"></script>

	<!-- JS Unify -->
	<script src="<@spring.url "/assets/unify/2.6.2/js/hs.core.js"/>"></script>

	<script src="<@spring.url "/assets/unify/2.6.2/js/components/hs.smart-menu.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/js/helpers/hs.hamburgers.js"/>"></script>

	<script src="<@spring.url "/assets/unify/2.6.2/js/components/hs.popup.js"/>"></script>
	<script src="<@spring.url "/assets/unify/2.6.2/js/components/hs.carousel.js"/>"></script>

	<script src="<@spring.url "/assets/unify/2.6.2/js/components/hs.go-to.js"/>"></script>

	<!-- JS Custom -->
	<script src="<@spring.url "/assets/unify/2.6.2/js/custom.js"/>"></script>

	<#assign KENDO_VERSION = "2019.3.917" />	
    <script type="text/javascript" src="<@spring.url '/js/kendo/${KENDO_VERSION}/kendo.core.min.js'/>"></script>
    <script type="text/javascript" src="<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.data.min.js"/>"></script>
    <script type="text/javascript" src="<@spring.url "/js/kendo/${KENDO_VERSION}/kendo.binder.min.js"/>"></script>	
	<script src="<@spring.url "/js/community.ui/community.ui.data.js"/>"></script>
	<script src="<@spring.url "/js/community.ui/community.ui.core.js"/>"></script>

	<!-- JS Plugins Init. -->
	<script>
    $(document).ready(function () {
    
		var setup = community.ui.setup({
			features : {
				accounts: true
			},
			'features.accounts.authenticate' :function(e){
				if( !e.token.anonymous ){
					observable.setUser(e.token);
					$("#accounts img.profile").attr("src", observable.currentUser.getUserProfileImage() );
				}
			}
		});		 
		
		var observable = new community.ui.observable({ 
			currentUser : new community.data.model.User(),
			setUser : function( data ){
				var $this = this;
				data.copy($this.currentUser) ;
				
			}
		});		
		
		// initialization of go to
		$.HSCore.components.HSGoTo.init('.js-go-to');

		// initialization of carousel
		$.HSCore.components.HSCarousel.init('.js-carousel');

		// initialization of popups
		// $.HSCore.components.HSPopup.init('.js-fancybox');
	});

	$(window).on('load', function () {
      // initialization of header
      $.HSCore.helpers.HSHamburgers.init('.hamburger');
      $.HSCore.components.HSSmartMenu.init($('.js-smart-menu'));
	});
	
  </script>
</body>
</html>