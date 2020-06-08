package architecture.community.web.spring.controller.data;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.comment.Comment;
import architecture.community.comment.CommentService;
import architecture.community.comment.DefaultComment;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.model.ModelObjectTreeWalker;
import architecture.community.model.ModelObjectTreeWalker.ObjectLoader;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.util.StringUtils; 

@Controller("community-comments-data-controller")
@RequestMapping("/data/comments")
public class CommentsDataController {

	@Inject
	@Qualifier("commentService")
	private CommentService commentService;

	/**
	 * 
	 * @return
	 * @throws BoardMessageNotFoundException
	 * @throws BoardNotFoundException
	 */
	@RequestMapping(value = "/{objectType:[\\p{Digit}]+}/{objectId:[\\p{Digit}]+}/add_simple.json", method = {RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result addSimpleComments(
			@PathVariable Integer objectType, 
			@PathVariable Long objectId,
			@RequestParam(value = "name", defaultValue = "", required = false) String name,
			@RequestParam(value = "email", defaultValue = "", required = false) String email,
			@RequestParam(value = "text", defaultValue = "", required = true) String text, HttpServletRequest request,
			ModelMap model) {

		Result result = Result.newResult();
		try {
			User user = SecurityHelper.getUser();
			String address = request.getRemoteAddr(); 
			Comment newComment = commentService.createComment(objectType, objectId, user, text); 
			newComment.setIPAddress(address);
			if (!StringUtils.isNullOrEmpty(name))
				newComment.setName(name);
			if (!StringUtils.isNullOrEmpty(email))
				newComment.setEmail(email); 
			commentService.addComment(newComment); 
			result.setCount(1);
		} catch (Exception e) {
			result.setError(e);
		} 
		return result;
	}

	@RequestMapping(value = "/{objectType:[\\p{Digit}]+}/{objectId:[\\p{Digit}]+}/add.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result addCommentsWithDataSourceRequest(
		@PathVariable Integer objectType, 
		@PathVariable Long objectId,  
		@RequestBody DataSourceRequest reqeustData, 
		HttpServletRequest request, 
		ModelMap model) {
		Result result = Result.newResult();
		try {
			User user = SecurityHelper.getUser();
			String address = request.getRemoteAddr();
			String name = reqeustData.getDataAsString("name", null);
			String email = reqeustData.getDataAsString("email", null);
			String text = reqeustData.getDataAsString("text", null);
			Long parentCommentId = reqeustData.getDataAsLong("parentCommentId", 0L); 
			Comment newComment = commentService.createComment(objectType, objectId, user, text); 
			newComment.setIPAddress(address);
			if (!StringUtils.isNullOrEmpty(name))
				newComment.setName(name);
			if (!StringUtils.isNullOrEmpty(email))
				newComment.setEmail(email); 
			if (parentCommentId > 0) {
				Comment parentComment = commentService.getComment(parentCommentId);
				commentService.addComment(parentComment, newComment);
			} else {
				commentService.addComment(newComment);
			}
			result.setCount(1);
		} catch (Exception e) {
			result.setError(e);
		}
		return result;
	}

	
	@RequestMapping(value = "/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getComments(
		@RequestParam(value = "objectType", defaultValue = "0", required = false) Integer objectType,
		@RequestParam(value = "objectId", defaultValue = "0", required = false) Long objectId,
		NativeWebRequest request) {
		ItemList items = new ItemList();
		ModelObjectTreeWalker walker = commentService.getCommentTreeWalker(objectType, objectId);
		long parentId = -1L;
		int totalSize = walker.getChildCount(parentId);
		List<Comment> list = walker.children(parentId, new ObjectLoader<Comment>() {
			public Comment load(long commentId) throws NotFoundException {
				return commentService.getComment(commentId);
			} 
		});
		items.setItems(list);
		items.setTotalCount(totalSize);
		return items;
	}

	@RequestMapping(value = "/{commentId:[\\p{Digit}]+}/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getChildComments(
		@RequestParam(value = "objectType", defaultValue = "0", required = false) Integer objectType,
		@RequestParam(value = "objectId", defaultValue = "0", required = false) Long objectId,
		@PathVariable Long commentId, 
		NativeWebRequest request)  { 
		 
		ModelObjectTreeWalker walker = commentService.getCommentTreeWalker(objectType, objectId); 
		int totalSize = walker.getChildCount(commentId); 
		List<Comment> list = walker.children(commentId, new ObjectLoader<Comment>() {
			public Comment load(long commentId) throws NotFoundException {
				return commentService.getComment(commentId);
			}
		});
		return new ItemList(list, totalSize);
	}

	
	@RequestMapping(value = "/0/save_or_update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Comment saveOrUpdateTagsObjects(@RequestBody DefaultComment comment, HttpServletRequest request) throws NotFoundException, UnAuthorizedException { 
		User user = SecurityHelper.getUser();
		DefaultComment commentToUse = comment ;
		if( commentToUse.getCommentId() > 0 ) {    
			if( !isAllowed( commentToUse.getUser(),  user ) ) {
				throw new UnAuthorizedException();
			}
			commentService.setBody(commentToUse, comment.getBody());
		}else { 
			if( !user.isAnonymous() )
				commentToUse.setUser(user);
			String address = request.getRemoteAddr();  
			commentToUse.setIPAddress(address); 
			commentService.addComment(commentToUse); 
		}
		return commentToUse;
	}
	
	@RequestMapping(value = "/0/delete.json", method = { RequestMethod.POST })
	@ResponseBody
	public Result deleteComment(@RequestBody DefaultComment comment, NativeWebRequest request) throws NotFoundException, UnAuthorizedException {  
		User user = SecurityHelper.getUser(); 
		if( comment.getCommentId() > 0 ) { 
			Comment commentToUse = commentService.getComment(comment.getCommentId());
			if( !isAllowed( comment.getUser(),  user ) ) {
				throw new UnAuthorizedException();
			}
			commentService.delete(commentToUse);
		}
		return Result.newResult();
	}
	
	
	private boolean isAllowed(User owner, User me) {
		boolean isAllowed = false;
		if( SecurityHelper.isUserInRole("ROLE_ADMINISTRATOR,ROLE_SYSTEM,ROLE_DEVELOPER,ROLE_OPERATOR"))
			return true;
		if( owner.getUserId() > 0 && me.getUserId() > 0 &&  owner.getUserId() == me.getUserId() ) {
			isAllowed = true;
		}
		return true;
	}
	
}