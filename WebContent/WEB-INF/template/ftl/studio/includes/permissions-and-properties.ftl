	<script>
	/**
	* Property Window for Object
	*
	*/
	function createPropertyWindowIfNotExist(models, objectId, renderTo){ 
		renderTo = renderTo || $('#property-window');
		if( !community.ui.exists( renderTo )){ 
			var grid = community.ui.grid( $('#property-grid'), {
				dataSource: {
					transport: { 
						read : 		{ url:'<@spring.url "/data/secure/mgmt/"/>'+ models.prefix +  objectId + '/properties/list.json',   type:'post', contentType : "application/json" },
						create : 	{ url:'<@spring.url "/data/secure/mgmt/"/>'+  models.prefix + objectId + '/properties/update.json', type:'post', contentType : "application/json" },
						update : 	{ url:'<@spring.url "/data/secure/mgmt/"/>'+  models.prefix + objectId + '/properties/update.json', type:'post', contentType : "application/json" },
						destroy : 	{ url:'<@spring.url "/data/secure/mgmt/"/>'+  models.prefix + objectId + '/properties/delete.json', type:'post', contentType : "application/json" },
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					}, 
					batch: true, 
					schema: {
						model: community.data.model.Property
					}
				},
				height : 550,
				sortable: true,
				filterable: false,
				pageable: false, 
				editable: "inline",
				columns: [
					{ field: "name", title: "이름", width: 250 , validation: { required: true} },  
					{ field: "value", title: "값" , validation: { required: true} },
					{ command: ["destroy"], title: "&nbsp;", width: "250px"}
				],
				toolbar: kendo.template('<div class="p-sm"><div class="g-color-white"><a href="\\#"class="btn u-btn-outline-lightgray g-mr-5 k-grid-add"><span class="k-icon k-i-plus"></span> 속성 추가</a><a href="\\#"class="btn u-btn-outline-lightgray g-mr-5 k-grid-save-changes"><span class="k-icon k-i-check"></span> 저장</a><a href="\\#"class="btn u-btn-outline-lightgray g-mr-5 k-grid-cancel-changes"><span class="k-icon k-i-cancel"></span> 취소</a><a class="pull-right hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-mt-7 g-mr-5" data-kind="properties" data-action="refresh"></a></div></div>'),    
				save : function(){  
				}
			});			
			
			$('#property-grid').on( "click", "a[data-kind=properties],a[data-action=refresh]", function(e){		
				var $this = $(this);	
				grid.dataSource.read();	
			});		
						
			var window = community.ui.window( renderTo, {
				width: "900px",
				title: "프로퍼티",
				visible: false,
				modal: true,
				actions: ["Close"], // 
				open: function(){ 
				},
				close: function(){  
				}
			});
		}	
		community.ui.window( renderTo ).center().open();
	}  

	/**
	* Permissions Window for Object
	*
	*/
	function createPermissionsWindowIfNotExist( models, objectId, renderTo){  
		renderTo = renderTo || $('#permissions-window');
		if( !community.ui.exists( renderTo )){ 
			var grid = community.ui.grid( $('#permissions-grid'), {
				dataSource: {
					transport: { 
						read : 		{ url:'<@spring.url "/data/secure/mgmt/security/permissions/"/>'+ models.objectType + "/" + objectId + '/list.json',   type:'post', contentType : "application/json" },
						destroy : 	{ url:'<@spring.url "/data/secure/mgmt/security/permissions/"/>'+  models.objectType + "/" + objectId + '/remove.json', type:'post', contentType : "application/json" },
						parameterMap: function (options, operation){	 
							if (operation !== "read" && options.models) { 
								return community.ui.stringify(options.models);
							}
							return community.ui.stringify(options);
						}
					},  
					batch: false,
					schema: {
						data:  "items",
						total: "totalCount",
						model: community.data.model.ObjectAccessControlEntry
					}
				},
				height : 300,
				sortable: true,
				editable: "inline",
				filterable: false,
				pageable: false,  
				columns: [
					{ field: "grantedAuthority", title:  "구분", width: 100  },  
					{ field: "grantedAuthorityOwner", title: "대상"  },
					{ field: "permission", title: "권한" },
					{ command: ["destroy"], title: "&nbsp;", width: "150px"}
				],
				toolbar: kendo.template('<div class="p-sm"><div class="g-color-white"><a class="pull-right hs-admin-reload u-link-v5 g-font-size-20 g-color-gray-light-v3 g-color-secondary--hover g-mt-7 g-mb-5 g-mr-5" data-kind="permissions" data-action="refresh"></a></div></div>'),    
				
			});	 
			
			$('#permissions-grid').on( "click", "a[data-kind=permissions],a[data-action=refresh]", function(e){		
				var $this = $(this);	
				grid.dataSource.read();	
			}); 
			
			var observable = community.data.observable({ 
				permissionToType : "",  
				enabledSelectRole : false,
				permissionToDisabled	: true,	
				enabledSelectRole : false,			
				accessControlEntry: new community.data.model.ObjectAccessControlEntry(),
				rolesDataSource: community.ui.datasource ({
				  transport: {
				    read: { url: '<@spring.url "/data/secure/mgmt/security/roles/list.json" />' }
				  },
				  schema: {
				    total: "totalCount",
					data:  "items",
					model: community.data.model.Role
				  }
				}),
				permsDataSource: community.ui.datasource ({
				  transport: {
				    read: { url: '<@spring.url "/data/secure/mgmt/security/permissions/list.json" />' }
				  },
				  schema: {
				    total: "totalCount",
					data:  "items"
				  }
				}),
				addPermission : function (e){
					var $this = this;
					if( $this.accessControlEntry.get('grantedAuthorityOwner').length > 0  && $this.accessControlEntry.get('permission').length > 0 ){
						community.ui.progress(renderTo, true);	
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/security/permissions/" />' + models.objectType + "/" + objectId + '/add.json' , {
							data: community.ui.stringify($this.accessControlEntry),
							contentType : "application/json",
							success : function(response){
								community.ui.notify( $this.accessControlEntry.permission + " 권한이 " + $this.accessControlEntry.grantedAuthorityOwner + " 에게 부여 되었습니다.");
								grid.dataSource.read();
							}
						}).always( function () {
							community.ui.progress(renderTo, false);
							$this.resetAccessControlEntry();
						});							
					}
					return false;
				},
				removePermission : function (data) {
					var $this = this;
					community.ui.progress(renderTo, true);	
					community.ui.ajax( '<@spring.url "/data/secure/mgmt/security/permissions/" />' + models.objectType + "/" + objectId +'/remove.json', {
						data: community.ui.stringify(data),
						contentType : "application/json",
						success : function(response){
							grid.dataSource.read();
						}
					}).always( function () {
						community.ui.progress(renderTo, false);
					});	
					return false;	
				},
				setSource : function(){},
				resetAccessControlEntry : function(e){
					var $this = this;
					$this.set('permissionToType', 'user');
					$this.set('permissionToDisabled' , false);
					$this.set('enabledSelectRole', false);
					$this.accessControlEntry.set('id', 0);	
					$this.accessControlEntry.set('grantedAuthority' , "USER");					
					$this.accessControlEntry.set('grantedAuthorityOwner' , "");			
					$this.accessControlEntry.set('permission' , "");		
					$this.accessControlEntry.set('domainObjectId' , objectId);					
				},
			}); 
			observable.bind("change", function(e){						
				if( e.field == "permissionToType" ){
					if( this.get(e.field) == 'anonymous'){
						observable.set('permissionToDisabled', true);
						observable.set('enabledSelectRole', false);
						observable.accessControlEntry.set('grantedAuthority', "USER");
						observable.accessControlEntry.set('grantedAuthorityOwner', "ANONYMOUS");
					}else if (this.get(e.field) == 'role'){
						observable.set('permissionToDisabled', false);
						observable.set('enabledSelectRole', true);						
						observable.accessControlEntry.set('grantedAuthority', "ROLE");
						observable.accessControlEntry.set('grantedAuthorityOwner', "");
					}else{
						observable.set('enabledSelectRole', false);
					 	observable.set('permissionToDisabled', false);	
					 	observable.accessControlEntry.set('grantedAuthority', "USER");
						observable.accessControlEntry.set('grantedAuthorityOwner', "");
					}					
				}
        	}); 
        					 
			community.data.bind(renderTo, observable );   
			var window = community.ui.window( renderTo, {
				width: "900px",
				title: "권한",
				visible: false,
				modal: true,
				actions: ["Close"], // 
				open: function(){ 
				},
				close: function(){  
				}
			});
		}	
		community.ui.window( renderTo ).center().open();
	} 
	</script>
			
	<!-- Properties Window -->
	<div id="property-window" class="g-pa-5 g-height-600" style="display:none;" >
		<div id="property-grid" ></div>
	</div>
	<!-- End Properties Window -->		
	
	<!-- PERMISSION WINDOW -->
	<div id="permissions-window" class="g-pa-5 g-height-600" style="display:none;" > 
		<div id="permissions-grid" ></div>
		<section class="g-pos-rel h-100 g-brd-around g-brd-gray-light-v7 g-rounded-4 g-pa-15 g-pa-30--md g-mt-5">
			<p class="g-pb-15">
				익명사용자, 특정 회원 또는 롤에 ${PAGE_NAME}에 대한 권한을 부여할 수 있습니다. 먼저 권한를 부여할 대상을 선택하세요. 마지막으로 권한추가를 클릭하면 권한이 부여됩니다.
				${PAGE_NAME}에 대한 접근을 위해서는 READ 권한이 필요합니다.
			</p>		
			<label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
				<input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="permissionToType" checked="" value="anonymous" type="radio" data-bind="checked: permissionToType">
				<div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
					<i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
				</div>익명
			</label> 
			<label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
				<input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="permissionToType" type="radio" value="user" data-bind="checked: permissionToType">
				<div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
					<i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
				</div>회원
			</label> 
			<label class="form-check-inline u-check g-pl-25 ml-0 g-mr-25">
				<input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="permissionToType" type="radio" value="role" data-bind="checked: permissionToType">
				<div class="u-check-icon-radio-v4 g-absolute-centered--y g-left-0 g-width-18 g-height-18">
					<i class="g-absolute-centered d-block g-width-10 g-height-10 g-bg-primary--checked"></i>
				</div>롤
			</label> 		
				<div class="row">
					<div class="col">
						<input data-role="dropdownlist"
								data-option-label="롤을 선택하세요."
								data-auto-bind="false"
								data-text-field="name"
								data-value-field="name"
								data-bind="value: accessControlEntry.grantedAuthorityOwner, source: rolesDataSource, enabled: enabledSelectRole, visible:enabledSelectRole"
								style="width: 100%;" /> 
						<input type="text" class="form-control" placeholder="권한을 부여할 사용자 아이디(username)을 입력하세요" data-bind="value: accessControlEntry.grantedAuthorityOwner , disabled: permissionToDisabled, invisible:enabledSelectRole">  
					</div>
					<div class="col">
						<input data-role="dropdownlist"
												 data-option-label="권한을 선택하세요."
								                 data-auto-bind="false"
								                 data-text-field="name"
								                 data-value-field="name"
								                 data-bind="value: accessControlEntry.permission, source: permsDataSource"
								                 style="width: 100%;" />
				</div>
			</div>
			<div class="text-right">
				<button class="g-mt-15 btn btn-md g-rounded-50 u-btn-3d u-btn-darkgray g-font-size-default" data-bind="click: addPermission">권한 추가</button>								
			</div>
		<section>																						
	</div>
	<!-- END PERMISSION WINDOW -->	
	