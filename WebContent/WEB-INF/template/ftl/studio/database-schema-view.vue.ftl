<#ftl encoding="UTF-8"/>
<#compress>
<html>
	<head> 
	    <meta charset="utf-8">
	    <meta content="width=device-width,initial-scale=1,minimal-ui" name="viewport">	
		<#include "/includes/global.css.vue.ftl">
	</head>
	<body class="view-page" style="background:#fff;">
		<div id="app">
		<!-- Your code here -->
		</div>
	</body>
	<script src="<@spring.url "/assets/vue/dist/vue.min.js"/>"></script>
	<script src="<@spring.url "/assets/vue-material/dist/vue-material.min.js"/>"></script>
	<script> 
    // VueMaterial ------------------------
    Vue.use(VueMaterial.default) 
	new Vue({ 
        el: '#app',
        bodyClass: "view-page",
		template : `
	<div class="content"  style="padding:10px;">	 
		<h4 class="title">Simple Table</h4>
		<p class="category">Here is a subtitle for this table</p> 
		<div>
		      <small>Flat</small>
		      <md-button class="md-icon-button">
		        <md-icon>home</md-icon>
		      </md-button>
		</div> 
		<div>
		    <md-steppers md-vertical>
		      <md-step id="first" md-label="First Step" md-description="Optional">
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		      </md-step>
		      <md-step id="second" md-label="Second Step">
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		      </md-step>
		      <md-step id="third" md-label="Third Step">
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		        <p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Molestias doloribus eveniet quaerat modi cumque quos sed, temporibus nemo eius amet aliquid, illo minus blanditiis tempore, dolores voluptas dolore placeat nulla.</p>
		      </md-step>
		    </md-steppers>
		</div>
	</div>	  
		`
    });
    
	</script>
	<style>
	.small {
		display: block;
	}	
	</style>
</html>
</#compress>