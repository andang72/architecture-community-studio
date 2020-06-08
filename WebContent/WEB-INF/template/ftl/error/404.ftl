<#ftl encoding="UTF-8"/>
<#compress>
<!DOCTYPE html>
<html>
<head>
	<!-- Title -->
  	<title>404 | Not found</title>  
  	<!-- Required Meta Tags Always Come First -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"> 
    <meta http-equiv="x-ua-compatible" content="ie=edge"> 
    <style>
	*{
	    -webkit-box-sizing: border-box;
	    box-sizing: border-box;
	    margin: 0;
    	padding: 0;
	}
	html, body{
	    width: 100%;
	    height: 100%;
	    position: relative;
	    display: block;
	}
	
	p, .text {
	    font-weight: 300;
	    font-size: 2em;
	    line-height: 2.5em;
	    color: #fff;
	} 	
	
	body {
		margin: 0;
		padding: 0;
		font-family: "굴림";
		font-size: 12px;
		color: #666;
		scroll: auto;
	}
	
	.wrap{ width: 100%; height: 100%; overflow: hidden;}
	.bg-wrap{ background: url("<@spring.url "/images/bg/error_mobile.png"/>") no-repeat; background-size: cover; position: relative;}
	.con-box{padding: 0 35%; margin: 22% 0 0;}
	.con-box img{ width: 100%;}
	
	/* pc */ 
	.wrap{ width: 100%; height: 100%; overflow: hidden;}
	.bg-error{ background: url("<@spring.url "/images/bg/404.png"/>") no-repeat; background-size: cover; position: relative;}
	.error-box{
	    position: absolute;
	    top: 236px;
	    left: 338px;
	}
	
	.error-box a{ color: #ffffff; font-size: 20px; display: block; padding: 10px 73px; text-align: center; border: 2px solid #ffffff; border-radius: 50px;

	/* Tablet Device */
	@media screen and (min-width : 768px) and (max-width : 1024px) {
	    .con-box{padding: 0 29%; margin: 9% 0 0;}
	    .error-box{
	        position: absolute;
	        top: 10em;
	        left: 10em;
	    }
	}
	@media screen and (max-width : 480px) {
	    .bg-wrap{ background: url("<@spring.url "/images/bg/error_mobile.png"/>") no-repeat 0 0; background-size: cover; position: relative;}
	    .con-box{padding: 0 20%; margin: 68% 0 0;}
	
	    .error-box img{
	        width: 80%;
	    }
	}    
    </style>
</head>
<body>
    <!-- 500 -->
    <div class="wrap bg-error"> 
        <div class="error-box">
            <img src="<@spring.url "/images/bg/error_icon.png"/>" alt="500 sorry! the page you're looking for was not found">
           <div class="text" style="margin-top:50px;">
           요청하신 페이지를 처리중에 오류가 발생하였습니다. 
           <br/>
           서비스 이용에 불편을 드려 죄송합니다.</div>
           <div class="text"> 
           We have encountered a system error while processing your request. <br/>
           We apologize for the inconvenience.
           </div> 
            <a href="/">GO HOME</a>
        </div> 
    </div>
    <!-- //500 -->
</body>
</html>

</#compress>