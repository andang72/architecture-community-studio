      <!-- Sidebar Nav -->
      <div id="sideNav" class="col-auto u-sidebar-navigation-v1 u-sidebar-navigation--dark">
      	<ul id="sideNavMenu" class="u-sidebar-navigation-v1-menu u-side-nav--top-level-menu g-min-height-100vh mb-0">
      <#assign sidebarNav = CommunityContextHelper.getAdminService().getMenu("STUDIO.SIDEBAR") />	
      <#list sidebarNav.components as item >
          <!-- ${item.name} -->      
          <#if ( item.roles?? && SecurityHelper.isUserInRole(item.roles) ) || !item.roles?? >
   	  <#if item.components?has_content >
          <!-- Dashboards -->
          <li class="u-sidebar-navigation-v1-menu-item u-side-nav--has-sub-menu u-side-nav--top-level-menu-item">
            <a class="media u-side-nav--top-level-menu-link u-side-nav--hide-on-hidden g-px-15 g-py-12" href="#" data-hssm-target="#${item.name}">
              <span class="d-flex align-self-center g-pos-rel g-font-size-18 g-mr-18">
      			<i class="<#if item.icon?? >${item.icon}<#else>hs-admin-folder</#if>"></i>
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
              <#if ( item2.roles?? && SecurityHelper.isUserInRole(item2.roles) ) || !item2.roles?? >
              <!-- ${item2.name} --> 
			  <#if item2.components?has_content >
			  <li class="u-sidebar-navigation-v1-menu-item u-side-nav--has-sub-menu u-side-nav--second-level-menu-item">
                  <a class="media u-side-nav--second-level-menu-link g-px-15 g-py-12" href="#" data-hssm-target="#${item.name + "_" + item2.name }" ">
                    <span class="d-flex align-self-center g-mr-15 g-mt-minus-1">
          				<i class="<#if item2.icon?? >${item2.icon}<#else>hs-admin-folder</#if>"></i>
        			</span>
                    <span class="media-body align-self-center">${item2.title}</span>
                    <span class="d-flex align-self-center u-side-nav--control-icon">
          				<i class="hs-admin-angle-right"></i>
        			</span>
                  </a> 
                   <ul id="${item.name + "_" + item2.name}" class="u-side-nav--third-level-menu">
                  <#list item2.components as item3> 
                  <#if ( item3.roles?? && SecurityHelper.isUserInRole(item3.roles) ) || !item3.roles?? >
                  <li class="u-side-nav--third-level-menu-item">
                    	<a class="u-side-nav--third-level-menu-link u-side-nav--hide-on-hidden g-pl-8 g-pr-15 g-py-6" 
                    		<#if item3.location?? >data-menu-item="${item.name + "_" + "_" + item2.name + "_" + item3.name }"</#if> href="<#if item3.location?? ><@spring.url "/secure/studio/${item3.location}"/><#else>#</#if>">
                            <span class="media-body align-self-center">${item3.title}</span>
                    	</a>
                  	</li>
                  </#if>
                  </#list>
                  </ul>
              </li>    
              <#else> 
              <li class="u-sidebar-navigation-v1-menu-item u-side-nav--second-level-menu-item">
                <a class="media u-side-nav--second-level-menu-link g-px-15 g-py-12" <#if item2.location?? >data-menu-item="${item2.name}"</#if> href="<#if item2.location?? ><@spring.url "/secure/studio/${item2.location}"/><#else>#</#if>">
                  <span class="d-flex align-self-center g-mr-15 g-mt-minus-1">
          			<i class="<#if item2.icon?? >${item2.icon}<#else>hs-admin-settings</#if>"></i>
        		  </span>
                  <span class="media-body align-self-center">${item2.title}</span>
                </a>
              </li> 
               </#if> 
              <!-- End ${item2.name} -->
              </#if>
   	  		</#list>
            </ul>
            <!-- End ${item.name}: Sub -->
         </li>	
   	  <#else>
          <li class="u-sidebar-navigation-v1-menu-item u-side-nav--top-level-menu-item">
            <a class="media u-side-nav--top-level-menu-link u-side-nav--hide-on-hidden g-px-15 g-py-12" <#if item.location?? >data-menu-item="${item.name}"</#if> href="<#if item.location?? ><@spring.url "/secure/studio/${item.location}"/><#else>#</#if>">
              <span class="d-flex align-self-center g-font-size-18 g-mr-18">
      			<i class="<#if item.icon?? >${item.icon}<#else>hs-admin-settings</#if>"></i>
    		  </span>
              <span class="media-body align-self-center">${item.title}</span>
            </a>
          </li>
   	  </#if> 
          </#if>
          <!-- End ${item.name} -->    	     	  
   	  </#list>
   	  <#assign customSidebarNavName = CommunityContextHelper.getConfigService().getApplicationProperty("studio.custom.sidebar", "CUSTOM_ADMIN_MENU") />
   	  <#if CommunityContextHelper.getMenuService().hasMenu(customSidebarNavName) >
   	  	<#assign treeWalker = CommunityContextHelper.getMenuService().getTreeWalker(customSidebarNavName) />	
   	  	 <#list treeWalker.getChildren() as item >
   	  	 <#if ( treeWalker.getChildCount(item) > 0 ) > 
   	  	 <li class="u-sidebar-navigation-v1-menu-item u-side-nav--has-sub-menu u-side-nav--top-level-menu-item">
            <a class="media u-side-nav--top-level-menu-link u-side-nav--hide-on-hidden g-px-15 g-py-12" href="#" data-hssm-target="#CUSTOM_${item.menuId + "_" + item.menuItemId }">
              <span class="d-flex align-self-center g-pos-rel g-font-size-18 g-mr-18">
      			<i class="hs-admin-folder"></i>
    		  </span>
              <span class="media-body align-self-center">${item.name}</span>
              <span class="d-flex align-self-center u-side-nav--control-icon">
      		  	<i class="hs-admin-angle-right"></i>
    		  </span>
              <span class="u-side-nav--has-sub-menu__indicator"></span>
            </a>
            <!-- ${item.name}:  Sub -->
            <ul id="CUSTOM_${item.menuId + "_" + item.menuItemId }" class="u-sidebar-navigation-v1-menu u-side-nav--second-level-menu mb-0">
            <#list treeWalker.getChildren(item) as item2 >
              <!-- ${item2.name} -->	
              <#if ( treeWalker.getChildCount(item2) > 0 ) >   
                <li class="u-sidebar-navigation-v1-menu-item u-side-nav--has-sub-menu u-side-nav--second-level-menu-item">
                  <a class="media u-side-nav--second-level-menu-link g-px-15 g-py-12" href="#" data-hssm-target="#CUSTOM_${item.menuId + "_" + item.menuItemId + "_" + item2.menuItemId }" ">
                    <span class="d-flex align-self-center g-mr-15 g-mt-minus-1">
          				<i class="hs-admin-folder"></i>
        			</span>
                    <span class="media-body align-self-center">${item2.name}</span>
                    <span class="d-flex align-self-center u-side-nav--control-icon">
          				<i class="hs-admin-angle-right"></i>
        			</span>
                  </a> 
                  <ul id="CUSTOM_${item.menuId + "_" + item.menuItemId + "_" + item2.menuItemId }" class="u-side-nav--fourth-level-menu">
                  	<#list treeWalker.getChildren(item2) as item3>
					<li class="u-side-nav--fourth-level-menu-item">
                    	<a class="u-side-nav--fourth-level-menu-link g-px-15 g-py-6" <#if item3.location?? >data-menu-item="CUSTOM_${item.menuId + "_" + item.menuItemId + "_" + item2.menuItemId + "_" + item3.menuItemId }"</#if> href="<#if item3.location?? ><@spring.url "/secure/studio/${item3.location}"/><#else>#</#if>">
                            <span class="media-body align-self-center">${item3.name}</span>
                    	</a>
                  	</li>
                  	</#list>
                  </ul>    
                </li>  
              <#else> 
              <li class="u-sidebar-navigation-v1-menu-item u-side-nav--second-level-menu-item">
                <a class="media u-side-nav--second-level-menu-link g-px-15 g-py-12" <#if item2.location?? >data-menu-item="CUSTOM_${item2.menuId + "_" + item2.menuItemId }"</#if> href="<#if item2.location?? ><@spring.url "/secure/studio/${item2.location}"/><#else>#</#if>">
                  <span class="d-flex align-self-center g-mr-15 g-mt-minus-1">
          			<i class="hs-admin-settings"></i>
        		  </span>
                  <span class="media-body align-self-center">${item2.name}</span>
                </a>
              </li> 
              </#if>		
              <!-- End ${item2.name} --> 
             </#list>
   	  	 	</ul><!-- End ${item.name}: Sub -->
         </li>
   	  	 <#else>
          <li class="u-sidebar-navigation-v1-menu-item u-side-nav--top-level-menu-item">
            <a class="media u-side-nav--top-level-menu-link u-side-nav--hide-on-hidden g-px-15 g-py-12" <#if item.location?? >data-menu-item="CUSTOM_${item.menuId + "_" + item.menuItemId }"</#if> href="<#if item.location?? ><@spring.url "/secure/studio/${item.location}"/><#else>#</#if>">
              <span class="d-flex align-self-center g-font-size-18 g-mr-18">
      			<i class="<#if item.icon?? >${item.icon}<#else>hs-admin-settings</#if>"></i>
    		  </span>
              <span class="media-body align-self-center">${item.name}</span>
            </a>
          </li>
   	  	 </#if>		
   	  	 </#list>
   	  </#if>
        </ul>
      </div>
      <!-- End Sidebar Nav -->