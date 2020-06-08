	<style><!--
	.k-window { border-radius : 15px; border:0px;} 
	.k-window>.k-header { border-top-left-radius: 15px; border-top-right-radius: 15px; padding: 15px; border:0px;} 
	.k-window .k-window-content { border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; border:0px; }
	.k-window-titlebar .k-window-actions { top : 12px ;}
	.k-window-titlebar .k-window-actions a.k-window-action { width:32px;}
	.k-window-titlebar .k-window-actions a.k-window-action > .k-icon { font-size:32px;}  
	.k-dialog .k-dialog-buttongroup.k-dialog-button-layout-normal .k-button { border-radius: 15px; padding-right: 15px; padding-left: 15px;} 
	.k-window-content .k-grid-content { min-height: inherit; }
	.no-theme .k-window , .no-theme .k-header , .no-theme .k-window-content { border-radius : 0px;} 
	.form-control { box-sizing: border-box; } 
	 
	.k-grid-content { min-height:700px; } 
	.k-grid.k-grid-auto .k-grid-content { min-height: unset; }
	.k-grid.k-grid-lg .k-grid-content { min-height:600px; }
	.k-grid.k-grid-md .k-grid-content { min-height:300px; }
	
	.k-tooltip-validation { margin-top : 5px; }
	.k-splitbar  { background : #ccc; } 
	.k-editor .k-i-custom-insert-image:before {content: "\e501";}	
	
	
	.uploaded-image {
			float: left;
            width: 120px;
            height: 170px;
            margin: 0;
            padding: 0px;
            cursor: pointer;
	}
	.uploaded-image img {
            width: 110px;
            height: 110px;
	}
	.uploaded-image h3 {
            margin: 0;
            margin-left : 5px;
            padding: 3px 5px 0 0;
            max-width: 96px;
            overflow: hidden;
            line-height: 1.1em;
            font-size: .9em;
            font-weight: normal;
            text-transform: uppercase;
            color: #999;
	} 
        
	.image-listview.k-listview:after {
            content: ".";
            display: block;
            height: 0;
            clear: both;
            visibility: hidden;
        }
        
        .uploaded-image span {
           display:none;
        } 
        
	.uploaded-image.k-state-selected span {
			display:block;
            position: absolute;
            width: 110px;
            height: 110px;
            top: 0;
            margin: 0;
            padding: 0;
            line-height: 110px;
            vertical-align: middle;
            text-align: center;
	}
		
	--></style>