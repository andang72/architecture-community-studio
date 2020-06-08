	<script><!--
    /**
     * Reosuce Selection Window.
     * renderTo : 윈도우를 생성할 대상 
     * kind : data, view 
     * type : template, jsp, script
     * editor mode : 코드 에디터의 모드 ace/mode/java , ace/mode/ftl, ace/mode/html etc..
     *
     */ 
	function getResourceSelectorWindow(renderTo, type, kind , observable){ 
		if( !community.ui.exists( renderTo )){
			var __observable = community.data.observable({ 
				type : 'script',
				folder : true,
				path : '', 
				selectable : false,
				visible : false,
				tree: {
					visible : true
				},
				editor : {
					editable : false,
					warp : false,
					mode : 'ace/mode/ftl',
					edit: function(){
						var $this = this;
						$this.set('editor.editable', true);
					},
					cancle: function(){
						var $this = this;
						$this.set('editor.editable', false);
					},
					saveOrUpdate : function(){
						var $this = this;
						var saveOrUpdateUrl = '<@spring.url "/data/secure/mgmt/resources/"/>' + $this.get('type') + '/save-or-update.json' ;
						community.ui.progress($('.k-window'), true );
						community.ui.ajax( saveOrUpdateUrl, {
							data: community.ui.stringify({
								path : $this.get('path'),
								fileContent : editor.getValue()
							}),
							contentType : "application/json",
							success : function(response){}
						}).always( function () {
							community.ui.progress($('.k-window'), false );
						});	
					}			
				},				
				load : function (type) {  
					var $this = this;
					editor.setReadOnly(!$this.get('unlocked'));
					if( $this.get('type') !== type ){
						$this.setType(type);  
					}  
					treeview.dataSource.read(); 
					window.center().open();
				},
				typeSource : [
					{ text: "Freemarker", value: "template" },
					{ text: "JSP", value: "jsp" },
					{ text: "Groovy", value: "script" }
                ],
				setType : function(type){ 
					var $this = this; 
					var filter = {};
					$this.set('type', type );
					
					if( type === 'script' ){
						if( kind === 'view'){
							filter = { field: "path", operator: "startswith", value: "/view" };
						}else if (kind === 'data'){
							filter = { field: "path", operator: "startswith", value: "/data" };
						}
						$this.set('tree.visible' , false );
					}else if(type === 'template' ) {
						filter = { field: "path", operator: "doesnotstartwith", value: "/studio" };
						$this.set('tree.visible' , true );
					}
					treeview.setDataSource(new kendo.data.HierarchicalDataSource({
						filter : filter,						
						transport: {
							read: { url : '<@spring.url "/data/secure/mgmt/resources/"/>' + $this.get('type') + '/list.json', dataType: "json" }
						},
						schema: {		
							model: { id: "path", hasChildren: "directory" }
						}	
					}));
					if( type === 'template' ){
						$this.set('editor.mode', 'ace/mode/ftl' );
					}else if (type === 'script') {
						$this.set('editor.mode', 'ace/mode/java' );
					}else if (type === 'jsp') {
						$this.set('editor.mode', 'ace/mode/jsp' );
					}  
				},
				onResize : function(){ }, 
				select:function(e){
					var $this = this;  
					var selectedCells = treeview.select();	 
					var filePlaceHolder = treeview.dataItem( treeview.select() );   
					if( community.ui.defined(filePlaceHolder) ){
						observable.selected($this.get('type'), filePlaceHolder.path); 
						window.close();
					}
				}
			});  
			
			__observable.bind("change", function(e) { 
				if ( e.field === 'editor.warp' ){ 
					editor.getSession().setUseWrapMode(__observable.get('editor.warp'));
				}else if (e.field === 'type'){ 
					if( community.ui.exists( renderTo ) ) { 
						__observable.setType(__observable.get('type'));
						treeview.dataSource.read(); 
					}
				}
			});			
			
			var leftRenderTo = renderTo.find($('.pane-content:first div'));
			var rightRenderTo = renderTo.find($('.pane-content:last div.editor'));
			var treeview = community.ui.treeview ( leftRenderTo , {
				autoBind : false,
				template: kendo.template($("#resource-treeview-template").html()),
				dataTextField: "name",
				change: function(e) {
					var $this = this;
					var selectedCells = $this.select();	 
					var filePlaceHolder = $this.dataItem( $this.select() );  
					if( community.ui.defined(filePlaceHolder) ){ 
						__observable.set('path', filePlaceHolder.path);	
						community.ui.progress(rightRenderTo, true);
						community.ui.ajax( '<@spring.url "/data/secure/mgmt/resources/" />' + __observable.get('type') + '/get.json' ,{
							data : { path: filePlaceHolder.path },
							success : function(response){
								if( !response.directory ){
									__observable.set('editable', true);
									__observable.set('selectable', true);
								}else{ 
									__observable.set('editable', false);
									__observable.set('selectable', false);
								}
								__observable.set('folder', response.directory );
								editor.setValue( response.fileContent );
							}
						}).always( function () { 
							community.ui.progress(rightRenderTo, false);
						});
					}else{
						__observable.set( 'path', '');	
					}
				}	
			});  
			__observable.setType(type);
			
			var editor = createCodeEditor( rightRenderTo, true, __observable.get('editor.mode') ); 
			editor.setTheme("ace/theme/chrome");
			
			var window = community.ui.window( renderTo, {
				width: "900px",
				title: "리소스 선택",
				visible: false,
				modal: true,
				actions: [ "Minimize", "Maximize", "Close"], // 
				open: function(){
					editor.setValue("");
					__observable.set('visible', true);
				},
				close: function(){ 
					editor.setValue("");
					treeview.select($()); 
					__observable.set('visible', false);
				}
			});
			
			community.data.bind(renderTo, __observable);
			renderTo.data( "model", __observable ); 
		}
		
		renderTo.data("model").set('unlocked', observable.editable );
		renderTo.data("model").load(type); 
	}
	--></script>
	
	<div id="resource-select-window" class="g-height-600 g-bg-gray-light-v5" style="display:none;" >
		<div class="g-pa-5" data-bind="visible:tree.visible"> 
			 <input data-role="dropdownlist"
	                   data-auto-bind="false"
	                   data-text-field="text"
	                   data-value-field="value"
	                   data-bind="value: type, source: typeSource"
	                   style="width: 200px;"
	            /> 
		</div>
		<header class="g-bg-gray-light-v5 g-px-15 g-px-30--sm g-pt-15 g-pt-20--sm g-pb-10 g-pb-15--sm g-bg-white">
			<div class="media">
				<h3 class="d-flex align-self-center g-font-size-12 g-font-size-default--md g-color-black g-mr-10 mb-0">
				</h3>
				<div class="media-body d-flex justify-content-end">
					<label class="d-flex align-items-center justify-content-between g-mb-0">
						<span class="g-pr-20 g-font-weight-300">에디터 줄바꿈 설정/해지</span>
						<div class="u-check">
							<input class="g-hidden-xs-up g-pos-abs g-top-0 g-right-0" name="useWarp" value="true" data-bind="checked:editor.warp" type="checkbox">
							<div class="u-check-icon-radio-v8"><i class="fa" data-check-icon=""></i></div>
						</div>
					</label>		
					<div data-bind="invisible:folder" >
					<a class="hs-admin-lock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#!" data-bind="invisible:editor.editable, click:editor.edit" style=""></a>
					<a class="hs-admin-unlock u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#!" data-bind="visible:editor.editable, click:editor.cancle" style="display: none;"></a>
					<a class="hs-admin-save u-link-v5 g-font-size-20 g-color-gray-light-v1 g-color-secondary--hover g-ml-20" href="#1" data-bind="visible:editor.editable, click:editor.saveOrUpdate" style="display: none;"></a>
					</div>
					<a class="d-flex align-items-center u-link-v5 g-color-gray-light-v6 g-color-secondary--hover g-ml-5 g-ml-10--sm g-ml-15--xl" href="#!" data-bind="click:refresh">
						<i class="hs-admin-reload g-font-size-20"></i>
					</a>
					
				</div>
			</div>
		</header>	
  		<div data-role="splitter" class="g-mb-15" style="height: 700px; width: 100%;"
             data-panes="[
                { collapsible: true, size: '280px', min: '200px', max: '500px' },
                { collapsible: false },
             ]"
             data-bind="events: { resize: onResize }">
			<div class="pane-content">
				<div id="resource-treeview"></div>
			</div>
	        <div class="pane-content"> 
	        	<div data-bind="visible:folder" class="g-mt-150 text-center" style="display:none;"> 	
					<img alt="svgImg" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHg9IjBweCIgeT0iMHB4Igp3aWR0aD0iMjU2IiBoZWlnaHQ9IjI1NiIKdmlld0JveD0iMCAwIDY0IDY0IgpzdHlsZT0iIGZpbGw6IzAwMDAwMDsiPjxyYWRpYWxHcmFkaWVudCBpZD0iT0NQTEV0ZmZScTRuamR3VjRUSlJJYSIgY3g9IjMwLjEyNSIgY3k9IjI3LjEyNSIgcj0iMzMuNjk5IiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIgc3ByZWFkTWV0aG9kPSJyZWZsZWN0Ij48c3RvcCBvZmZzZXQ9IjAiIHN0b3AtY29sb3I9IiNjNWYxZmYiPjwvc3RvcD48c3RvcCBvZmZzZXQ9Ii4zNSIgc3RvcC1jb2xvcj0iI2NkZjNmZiI+PC9zdG9wPjxzdG9wIG9mZnNldD0iLjkwNyIgc3RvcC1jb2xvcj0iI2U0ZmFmZiI+PC9zdG9wPjxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iI2U5ZmJmZiI+PC9zdG9wPjwvcmFkaWFsR3JhZGllbnQ+PHBhdGggZmlsbD0idXJsKCNPQ1BMRXRmZlJxNG5qZHdWNFRKUklhKSIgZD0iTTQ1LDhMNDUsOGMyLjIwOSwwLDQtMS43OTEsNC00djBjMC0yLjIwOS0xLjc5MS00LTQtNGgwYy0yLjIwOSwwLTQsMS43OTEtNCw0djAgQzQxLDYuMjA5LDQyLjc5MSw4LDQ1LDh6Ij48L3BhdGg+PHJhZGlhbEdyYWRpZW50IGlkPSJPQ1BMRXRmZlJxNG5qZHdWNFRKUkliIiBjeD0iMzEiIGN5PSIzMiIgcj0iMzEuNTA0IiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIgc3ByZWFkTWV0aG9kPSJyZWZsZWN0Ij48c3RvcCBvZmZzZXQ9IjAiIHN0b3AtY29sb3I9IiNjNWYxZmYiPjwvc3RvcD48c3RvcCBvZmZzZXQ9Ii4zNSIgc3RvcC1jb2xvcj0iI2NkZjNmZiI+PC9zdG9wPjxzdG9wIG9mZnNldD0iLjkwNyIgc3RvcC1jb2xvcj0iI2U0ZmFmZiI+PC9zdG9wPjxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iI2U5ZmJmZiI+PC9zdG9wPjwvcmFkaWFsR3JhZGllbnQ+PHBhdGggZmlsbD0idXJsKCNPQ1BMRXRmZlJxNG5qZHdWNFRKUkliKSIgZD0iTTYyLDQ3LjVMNjIsNDcuNWMwLTIuNDg1LTIuMDE1LTQuNS00LjUtNC41SDMyYy0xLjEwNSwwLTItMC44OTUtMi0yVjIyYzAtMS4xMDUsMC44OTUtMiwyLTIgaDEwYzIuMjA5LDAsNC0xLjc5MSw0LTR2MGMwLTIuMjA5LTEuNzkxLTQtNC00SDMyYy0xLjEwNSwwLTItMC44OTUtMi0ydjBjMC0xLjEwNSwwLjg5NS0yLDItMmgxYzIuMjA5LDAsNC0xLjc5MSw0LTR2MCBjMC0yLjIwOS0xLjc5MS00LTQtNEg4LjVDNS40NjIsMCwzLDIuNDYyLDMsNS41djBDMyw4LjUzOCw1LjQ2MiwxMSw4LjUsMTFoM2MzLjAzOCwwLDUuNSwyLjQ2Miw1LjUsNS41djAgYzAsMy4wMzgtMi40NjIsNS41LTUuNSw1LjVoLTZDMi40NjIsMjIsMCwyNC40NjIsMCwyNy41djBDMCwzMC41MzgsMi40NjIsMzMsNS41LDMzSDE0YzEuNjU3LDAsMywxLjM0MywzLDN2MGMwLDEuNjU3LTEuMzQzLDMtMywzIEg2LjVDNC4wMTUsMzksMiw0MS4wMTUsMiw0My41djBDMiw0NS45ODUsNC4wMTUsNDgsNi41LDQ4SDE0YzIuMjA5LDAsNCwxLjc5MSw0LDR2MGMwLDIuMjA5LTEuNzkxLDQtNCw0aC0xYy0yLjIwOSwwLTQsMS43OTEtNCw0djAgYzAsMi4yMDksMS43OTEsNCw0LDRoNDBjMi4yMDksMCw0LTEuNzkxLDQtNHYwYzAtMi4yMDktMS43OTEtNC00LTRoLTNjLTEuMTA1LDAtMi0wLjg5NS0yLTJ2MGMwLTEuMTA1LDAuODk1LTIsMi0yaDcuNSBDNTkuOTg1LDUyLDYyLDQ5Ljk4NSw2Miw0Ny41eiI+PC9wYXRoPjxsaW5lYXJHcmFkaWVudCBpZD0iT0NQTEV0ZmZScTRuamR3VjRUSlJJYyIgeDE9IjMwIiB4Mj0iMzAiIHkxPSI1MiIgeTI9IjkiIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIiBzcHJlYWRNZXRob2Q9InJlZmxlY3QiPjxzdG9wIG9mZnNldD0iMCIgc3RvcC1jb2xvcj0iI2ZmOGI2NyI+PC9zdG9wPjxzdG9wIG9mZnNldD0iLjg0NyIgc3RvcC1jb2xvcj0iI2ZmYTc2YSI+PC9zdG9wPjxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iI2ZmYWQ2YiI+PC9zdG9wPjxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iI2ZmYWQ2YiI+PC9zdG9wPjwvbGluZWFyR3JhZGllbnQ+PHBhdGggZmlsbD0idXJsKCNPQ1BMRXRmZlJxNG5qZHdWNFRKUkljKSIgZD0iTTI2LDEzTDI2LDEzYzAtMi4yMDktMS43OTEtNC00LTRIMTJjLTIuMjA5LDAtNCwxLjc5MS00LDR2MzRjMCwyLjc2MSwyLjIzOSw1LDUsNWgzOVYyMCBjMC0yLjIwOS0xLjc5MS00LTQtNEgyOUMyNy4zNDMsMTYsMjYsMTQuNjU3LDI2LDEzeiI+PC9wYXRoPjxsaW5lYXJHcmFkaWVudCBpZD0iT0NQTEV0ZmZScTRuamR3VjRUSlJJZCIgeDE9IjM1LjUiIHgyPSIzNS41IiB5MT0iNTIiIHkyPSIyMyIgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiIHNwcmVhZE1ldGhvZD0icmVmbGVjdCI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjZmZjMDUwIj48L3N0b3A+PHN0b3Agb2Zmc2V0PSIuMDA0IiBzdG9wLWNvbG9yPSIjZmZjMDUwIj48L3N0b3A+PHN0b3Agb2Zmc2V0PSIuNjQxIiBzdG9wLWNvbG9yPSIjZmZiZTc1Ij48L3N0b3A+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjZmZiZDg1Ij48L3N0b3A+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjZmZiZDg1Ij48L3N0b3A+PC9saW5lYXJHcmFkaWVudD48cGF0aCBmaWxsPSJ1cmwoI09DUExFdGZmUnE0bmpkd1Y0VEpSSWQpIiBkPSJNMTksNTJoMzNjMy4zMTQsMCw2LTIuNjg2LDYtNlYyN2MwLTIuMjA5LTEuNzkxLTQtNC00SDIyYy0yLjIwOSwwLTQsMS43OTEtNCw0djIwIGMwLDIuNzYxLTIuMjM5LDUtNSw1SDE5eiI+PC9wYXRoPjxsaW5lYXJHcmFkaWVudCBpZD0iT0NQTEV0ZmZScTRuamR3VjRUSlJJZSIgeDE9IjMxIiB4Mj0iMzEiIHkxPSI1MiIgeTI9IjIzIiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIgc3ByZWFkTWV0aG9kPSJyZWZsZWN0Ij48c3RvcCBvZmZzZXQ9IjAiIHN0b3AtY29sb3I9IiNmZmM5NmEiPjwvc3RvcD48c3RvcCBvZmZzZXQ9Ii4wMDQiIHN0b3AtY29sb3I9IiNmZmM5NmEiPjwvc3RvcD48c3RvcCBvZmZzZXQ9Ii41NzgiIHN0b3AtY29sb3I9IiNmZmM4ODciPjwvc3RvcD48c3RvcCBvZmZzZXQ9IjEiIHN0b3AtY29sb3I9IiNmZmM3OTciPjwvc3RvcD48c3RvcCBvZmZzZXQ9IjEiIHN0b3AtY29sb3I9IiNmZmM3OTciPjwvc3RvcD48L2xpbmVhckdyYWRpZW50PjxwYXRoIGZpbGw9InVybCgjT0NQTEV0ZmZScTRuamR3VjRUSlJJZSkiIGQ9Ik0xOCwyN3YyMGMwLDIuNzYxLTIuMjM5LDUtNSw1aDBoMjJjMS42NTcsMCwzLTEuMzQzLDMtM3YwYzAtMS42NTctMS4zNDMtMy0zLTNoLTQgYy0xLjY1NywwLTMtMS4zNDMtMy0zdjBjMC0xLjY1NywxLjM0My0zLDMtM2g0LjVjMS4zODEsMCwyLjUtMS4xMTksMi41LTIuNXYwYzAtMS4zODEtMS4xMTktMi41LTIuNS0yLjVIMzRjLTEuNjU3LDAtMy0xLjM0My0zLTMgdjBjMC0xLjY1NywxLjM0My0zLDMtM2gxMmMxLjY1NywwLDMtMS4zNDMsMy0zdjBjMC0xLjY1Ny0xLjM0My0zLTMtM0gyMkMxOS43OTEsMjMsMTgsMjQuNzkxLDE4LDI3eiI+PC9wYXRoPjxsaW5lYXJHcmFkaWVudCBpZD0iT0NQTEV0ZmZScTRuamR3VjRUSlJJZiIgeDE9IjQ1LjUiIHgyPSI0NS41IiB5MT0iNDAiIHkyPSIzNSIgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiIHNwcmVhZE1ldGhvZD0icmVmbGVjdCI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjZmZjOTZhIj48L3N0b3A+PHN0b3Agb2Zmc2V0PSIuMDA0IiBzdG9wLWNvbG9yPSIjZmZjOTZhIj48L3N0b3A+PHN0b3Agb2Zmc2V0PSIuNTc4IiBzdG9wLWNvbG9yPSIjZmZjODg3Ij48L3N0b3A+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjZmZjNzk3Ij48L3N0b3A+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjZmZjNzk3Ij48L3N0b3A+PC9saW5lYXJHcmFkaWVudD48cGF0aCBmaWxsPSJ1cmwoI09DUExFdGZmUnE0bmpkd1Y0VEpSSWYpIiBkPSJNNDUuNSw0MEw0NS41LDQwYzEuMzgxLDAsMi41LTEuMTE5LDIuNS0yLjV2MGMwLTEuMzgxLTEuMTE5LTIuNS0yLjUtMi41aDAgYy0xLjM4MSwwLTIuNSwxLjExOS0yLjUsMi41djBDNDMsMzguODgxLDQ0LjExOSw0MCw0NS41LDQweiI+PC9wYXRoPjwvc3ZnPg==">
				</div>
				<div id="resource-editor" class="editor g-brd-gray-light-v6 g-brd-top-1 g-brd-left-0 g-brd-right-0 g-brd-style-solid g-brd-0" style="height:700px;width:100%;" data-bind="invisible:folder"></div> 
	        </div>
		</div> 
		<div class="text-right">  
			<button data-bind="click:select,enabled:selectable" class="btn btn-md g-rounded-50 u-btn-3d u-btn-primary g-mb-10 g-font-size-default g-ml-5">선택</button>
		</div>	
	</div>
	
	<script id="resource-treeview-template" type="text/kendo-ui-template">
	#if(item.directory){# 
                <i class="hs-admin-folder"></i> 
	 # }else{#  
                <i class="hs-admin-file"></i> 
	 #}#
	<span class="g-ml-5">#: item.name # </span>
	# if (!item.items) { #
		<a class='delete-link' href='\#'></a> 
	# } #
    </script>	