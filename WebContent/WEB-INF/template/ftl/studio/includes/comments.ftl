	<script><!-- 
	
	function createChildCommentListView(renderTo, options){ 
		if( !community.ui.exists( renderTo ) ){
			var template = community.ui.template('<@spring.url "/data/comments/"/>#= commentId #/list.json?objectType=#= objectType #&objectId=#= objectId #'); 
			var target_url = template(options); 
			var listview = community.ui.listview( renderTo , {
				dataSource : community.ui.datasource({
					transport:{
						read:{ url :target_url }
					},
					schema: {
						total: "totalCount",
						model: {
							id: "commentId",
						},
						data: "items"
					},
					pageSize : 100
				}),
				template: community.ui.template($("#comment-item-template").html())
			});	
			renderTo.removeClass('k-listview');
			renderTo.removeClass('k-widget');  
		} 
	}
	
	function createReplyCommentWindow( comment ){
		var renderTo = $('#reply-comment-window');	
		if( !community.ui.exists( renderTo )){ 
  			var observable = community.data.observable({
  				message : '',
  				comment : {} ,
  				setSource : function( data ){
  					var $this = this;
  					$this.set('comment', data ); 
  					$this.set('message', '' ); 
  				},
  				reply : function(){
					var $this = this; 
				  	var template = community.ui.template("/data/comments/#= objectType #/#= objectId #/add.json"); 
				  	var target_url = template( $this.get('comment') );		 			
				  	community.ui.progress($('.k-dialog'), true); 
					community.ui.ajax( target_url , {
						data: community.ui.stringify({ data:{ text:$this.get('message'), parentCommentId:$this.get('comment.commentId') } }),
						contentType : "application/json",
						success : function(response){
							refreshCommentListView();
							//community.ui.notify( "Comment 가 삭제되었습니다.");
						}	
					}).always( function () { 
						community.ui.progress($('.k-dialog'), false); 
						window.close();
					});		
  				}
  			}); 
  			
  			var window = community.ui.window( renderTo, {
				width: "500px",
				title: "Comment",
				visible: false,
				modal: true,
				actions: [ "Close"], // 
				open: function(){ 
					
				},
				close: function(){ 
				
				}
			});
			community.data.bind(renderTo, observable);
			renderTo.data("model", observable );
  		} 
  		renderTo.data("model").setSource( comment );
  		community.ui.window( renderTo ).center().open();		
	}
	
	function refreshCommentListView(renderTo){
		renderTo = renderTo || $('#comment-listview');
		community.ui.listview( renderTo ).dataSource.read();
	}	
		
	function createCommentListView( objectType, objectId , renderTo ){ 
	    objectType = objectType || ${COMMENT_OBJECT_TYPE};
	    objectId = objectId || ${COMMENT_OBJECT_ID};
	    renderTo = renderTo || $('#comment-listview'); 
		if( !community.ui.exists( renderTo ) ){  
			$('#features-comments').show();
			var observable = community.data.observable({ 
				totalCommentSize : 0,
				comment : {
					parentCommentId : 0,
					objectType : objectType,
					objectId : objectId,
					text : ''
				},
				refreshComment : function(){
					listview.dataSource.read();
				},
				addComment : function (){ 
					var $this = this;   
					if( $this.get('comment.text')!= null && $this.get('comment.text').length > 1 ){
						community.ui.progress($('#features-comments'), true);				
						var url = '<@spring.url "/data/comments/"/>' + observable.get('comment.objectType') + '/' + observable.get('comment.objectId') + '/add.json';
						community.ui.ajax( url , {
							data: community.ui.stringify({ data : {  text:$this.get('comment.text'), parentCommentId:$this.get('comment.parentCommentId') } }),
							contentType : "application/json",
							success : function(response){
								listview.dataSource.read();
							}	
						}).always( function () {
							community.ui.progress($('#features-comments'), false);
							$this.set('comment.text', '' ); 
						});	
					}
				}
			}); 
			var listview = community.ui.listview( renderTo , { 
				dataSource : community.ui.datasource({
					transport:{
						read:{
							url : '<@spring.url "/data/comments/list.json"/>' + '?objectType=' +  observable.get('comment.objectType') + '&objectId=' + observable.get('comment.objectId')  
						}
					},
					schema: {
						total: "totalCount",
						model: {
							id: "commentId",
						},
						data: "items"
					},
					pageSize : 100
				}),
				dataBound : function(){
					observable.set('totalCommentSize', this.dataSource.total() );
				},
				template: community.ui.template($("#comment-item-template").html())
			}); 
			renderTo.removeClass('k-listview');
			renderTo.removeClass('k-widget');  
			community.data.bind( $('#features-comments') , observable );  
			$('#features-comments').on( "click", "a[role=comment]", function(e){		
				var $this = $(this); 
				if( $this.data('action') === 'child' ){
					var _objectId =  $this.data('object-id');
					if( _objectId != null && _objectId > 0 ){
						var commentToUse = listview.dataSource.get(_objectId);
						if( commentToUse.replyCount > 0)
							createChildCommentListView( $( $this.data('target') ), commentToUse );	
					}	
				}
				else if( $this.data('action') === 'reply' ){
					var _objectId =  $this.data('object-id');
					var commentToUse = listview.dataSource.get(_objectId);
					createReplyCommentWindow(commentToUse);
				}
				else if( $this.data('action') === 'delete' ){
					var _objectId =  $this.data('object-id');
					if( _objectId != null && _objectId > 0 ){
						var commentToUse = listview.dataSource.get(_objectId);
						var template = community.data.template('영구적으로 제거합니다.<br/> 이동작은 최소할 수 없습니다.');
						var dialog = community.ui.dialog( null, {
							title : 'Comment 를 삭제하시겠습니까?',
							content : '영구적으로 제거합니다.<br/> 이동작은 최소할 수 없습니다.' ,
							actions: [
			                { text: '확인', 
			                	action: function(e){   
			                		community.ui.progress($('.k-dialog'), true); 
			                		community.ui.ajax( '<@spring.url "/data/comments/0/delete.json" />', {
										data: community.ui.stringify(commentToUse),
										contentType : "application/json",
										success : function(response){ 
											dialog.close();
											community.ui.notify( "Comment 가 삭제되었습니다.");
											listview.dataSource.read();
										}
									}).always( function () {
										community.ui.progress($('.k-dialog'), false);  
									});  
									return false;
			                	},
			                	primary: true },
			              	{ text: '취소'}
			            	]		
						}).open();	
					}
				}
				
			}); 
		}
		//console.log('read..');
		//community.ui.listview( renderTo ).dataSource.read();
	}
	
	--></script>
	<!-- CSS Customization -->
	<link rel="stylesheet" href="<@spring.url "/assets/unify/2.6.2/vendor/icon-line-pro/style.css"/>"> 	
	<style>
	#comment-listview div.media : first {
		border-top: 0!important;
	} 
	</style>
	
	<script type="text/x-kendo-template" id="comment-item-template">
	<div class="media g-brd-top g-brd-gray-light-v4 g-pt-30 g-mb-30">
		<img class="d-flex g-width-60 g-height-60 rounded-circle g-mt-3 g-mr-15" src="#= community.data.url.userPhoto( '<@spring.url "/"/>' , user ) #" alt="Image Description">
		<div class="media-body">
			<div class="d-flex align-items-start g-mb-15 g-mb-10--sm">
				<div class="d-block">
					<h5 class="h6 g-color-black g-font-weight-600">#: user.name #</h5>
                    <span class="d-block g-color-gray-dark-v5 g-font-size-11">#= kendo.toString( new Date(modifiedDate), 'yyyy.MM.dd HH:mm') #</span>
				</div>
				<div class="ml-auto"> 
					<!--
					<button class="btn u-btn-outline-blue g-mr-10 g-mb-15" type="button" role="button" >Reply</button> 
					<a class="u-link-v5  g-font-size-16 g-color-gray-light-v6 g-color-secondary--hover g-ml-30" href="\#">
						<i class="hs-admin-pencil"></i>
                    </a>
                    -->
					<a class="btn u-btn-outline-blue g-mr-10 g-mb-15" href="\#!" role="comment" data-object-id = "#= commentId #" data-action="reply" data-parent-object-id = "#= parentCommentId  #" >
						Reply
					</a>                    
                    #if (user.userId === ${ SecurityHelper.getUser().getUserId() } ) { #
					<a class="btn u-btn-outline-red g-mr-10 g-mb-15" href="\#!" role="comment" data-object-id = "#= commentId #" data-action="delete" data-parent-object-id = "#= parentCommentId  #" >
						Delete
					</a>
					# } #
				</div>
			</div> 
			<p>#: body #</p>
			<ul class="list-inline g-mb-5">
				<li class="list-inline-item">
					<a class="d-inline-block g-brd-around g-brd-gray-light-v4 g-brd-primary--hover g-color-gray-dark-v5 g-font-size-12 g-text-underline--none--hover rounded g-px-10 g-py-5" href="\#!"
						data-target="\\#comment-reply-listview-#:commentId#"
						role="comment" data-object-id = "#= commentId #" data-action="child" data-parent-object-id = "#= parentCommentId  #">
						<i class="align-middle icon-finance-206 u-line-icon-pro"></i>
						<span class="g-font-weight-600 g-ml-5">#= replyCount #</span>
					</a>
				</li> 
			</ul>
			<div class="comment-reply-listview" id="comment-reply-listview-#= commentId #" ></div>		
		</div>
	</div>
	</script>
    <!--  Single Item Comments -->
    <hr/>
    <section class="container" id="features-comments" style="display:none;">
      <div class="row justify-content-left">
        <div class="col-lg-12"> 
          <!-- Item Comments -->
          <div class="g-brd-top-0 g-brd-gray-light-v4 g-pb-30 g-mb-50">
            <div class="g-brd-y g-brd-top-0 g-brd-gray-light-v4 g-py-30 mb-0">
              <h3 class="h6 g-color-black g-font-weight-600 text-uppercase mb-0"><span data-bind="text:totalCommentSize"></span> Comments</h3> 
            </div>
            <div id="comment-listview" ></div> 
          </div>
          <!-- End Item Comments -->
          <h3 class="h6 g-color-black g-font-weight-600 text-uppercase g-mb-30">Add Comment</h3>
          <div class="g-mb-30">
            <textarea class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--focus g-resize-none rounded-3 g-py-13 g-px-15" rows="3" placeholder="Your message" data-bind="value:comment.text"></textarea>
          </div>
          <button class="btn u-btn-primary g-font-weight-600 g-font-size-12 text-uppercase g-py-12 g-px-25" type="button" role="button" data-bind="click:addComment" >Add Comment</button>
        </div>
      </div>  
    </section>
    <!-- End Single Item Comments --> 
	<div id="reply-comment-window" class="g-pa-0 g-height-600 container-fluid" style="display:none;" >
		<div class="g-pa-15">
			<blockquote class="blockquote g-bg-gray-light-v5 g-brd-primary g-font-size-13 g-pa-40 g-mb-5">
			  <p data-bind="html:comment.body"></p>
			  <footer class="blockquote-footer" data-bind="text: comment.user.name"></footer>
			</blockquote>
			
        	<textarea class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--focus g-resize-none rounded-3 g-py-13 g-px-15" rows="3" 
        		data-bind="value: message"
        		placeholder="Your message"></textarea> 
        	<div class="text-right g-mt-25">
				<button class="btn u-btn-primary g-font-weight-600 g-font-size-12 text-uppercase g-py-12 g-px-25" type="button" role="button" data-bind="click:reply" >Reply</button>
			</div>
        </div> 
	</div>		    