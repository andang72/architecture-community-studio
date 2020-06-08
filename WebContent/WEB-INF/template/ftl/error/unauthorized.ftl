<#ftl encoding="UTF-8"/>
<!DOCTYPE html>
<html>
<head>
    <title>${ CommunityContextHelper.getConfigService().getApplicationProperty("website.title", "REPLICANT") } | 접근권한이 없습니다.</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Expires" content="-1">
   <!-- CSS Bootstrap  -->
   <link href="<@spring.url "/css/bootstrap/3.3.7/bootstrap.min.css"/>" rel="stylesheet" type="text/css" />

   <!-- CSS Community -->
   <link href="<@spring.url "/css/community.ui/community.ui.globals.css"/>" rel="stylesheet" type="text/css" >
   <link href="<@spring.url "/css/community.ui/community.ui.style.min.css"/>" rel="stylesheet" type="text/css" >
     
</head>
<!--
<body>
	<div class="container">
	<#if SPRING_SECURITY_LAST_EXCEPTION ?? >	
	<div class="alert alert-danger" role="alert">
	  <strong>Oh Nooooo!</strong> ${ SPRING_SECURITY_LAST_EXCEPTION.getMessage() }
	</div>
	</#if> 
	</div>
</body>
-->
<body class="g-bg-gray-dark-v1 g-color-white">
  <main class="g-height-100x g-min-height-100vh g-flex-centered g-pa-15">
    <div class="text-center g-flex-centered-item g-position-rel">
      <div class="g-font-size-180 g-font-size-240--sm g-font-size-420--lg g-line-height-1 g-font-weight-200 g-color-gray-dark-v2">401</div>

      <div class="g-absolute-centered">
        <h1 class="g-font-weight-200 g-mb-20">접근권한이 없습니다.</h1>
        <p class="g-color-white-opacity-0_6 g-font-size-18">
		<#if SPRING_SECURITY_LAST_EXCEPTION ?? >	
		${ SPRING_SECURITY_LAST_EXCEPTION.getMessage() }
		</#if>        
        </p>
        <p class="g-color-white-opacity-0_6 g-font-size-18">If you think this is a problem with us, please&nbsp;<a href="#!" class="g-color-white g-color-primary--hover g-text-no-underline--hover">tell us</a>.</p>
        <p class="g-color-white-opacity-0_6 g-font-size-18 mb-0">Here is a link to the <a href="#!" class="g-color-white g-color-primary--hover g-text-no-underline--hover">home page</a>.</p>
      </div>
    </div>
  </main>
</body>

</html>