/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.comment;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.comment.dao.CommentDao;
import architecture.community.comment.event.CommentEvent;
import architecture.community.model.ModelObjectTreeWalker;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.ee.spring.event.EventSupport;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element; 

public class CommunityCommentService extends EventSupport implements CommentService {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Inject
	@Qualifier("commentDao")
	private CommentDao commentDao;

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;

	@Inject
	@Qualifier("commentCache")
	private Cache commentCache;
	
	@Inject
	@Qualifier("commentTreeWalkerCache")
	private Cache treeWalkerCache;

	public CommunityCommentService() {
	}


	public Comment getComment(long commentId) throws CommentNotFoundException {
		if (commentId < 1L)
			throw new CommentNotFoundException();
		Comment comment;
		if (commentCache.get(commentId) != null) {
			comment = (Comment) commentCache.get(commentId).getObjectValue();
		} else {
			
			try {
				comment = commentDao.getCommentById(commentId);
			} catch (Exception e) {
				throw new CommentNotFoundException(e);
			}
			
			setUserInComment(comment);
			commentCache.put( new Element(comment.getCommentId(), comment) );
		}
		return comment;
	}

	public void delete(Comment comment) {
		if( comment.getCommentId() > 0){
			commentDao.deleteComment(comment);
			evictCaches(comment);
		}
	}
	
	protected void setUserInComment(Comment comment) {
		long userId = comment.getUser().getUserId();
		try {
			comment.setUser(userManager.getUser(userId));
		} catch (UserNotFoundException e) {
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void update(Comment comment) {
		Date now = Calendar.getInstance().getTime();
		comment.setModifiedDate(now);
		commentDao.updateComment(comment);
		evictCaches(comment);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void setBody(Comment comment, String text) {
		if (text == null)
		    throw new IllegalArgumentException("Body cannot be null");
		try {
		    Comment originalComment = getComment(comment.getCommentId());
		    String originalValue = originalComment.getBody();
		    Date now = Calendar.getInstance().getTime();
		    originalComment.setBody(text);
		    originalComment.setModifiedDate(now);
		    commentDao.updateComment(originalComment);
		    evictCaches(comment);
		    fireEvent(new CommentEvent(originalComment, CommentEvent.Type.UPDATED, originalValue));
		    
		} catch (CommentNotFoundException e) {
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addComment(Comment newComment){
		addComment(null, newComment);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addComment(Comment parentComment, Comment newComment) {
		if (newComment == null)
		    throw new IllegalStateException("Comment cannot be null");

		int objectType = newComment.getObjectType();
		long objectId = newComment.getObjectId();
		boolean isAuthor = false;
		if (newComment.getCommentId() != -1L) {
		    throw new IllegalStateException( "Comment cannot be attached to this object since it is already attached to another object");
		}
		
		if (parentComment != null) {
		    DefaultComment bean = (DefaultComment) newComment;
		    bean.setParentCommentId(parentComment.getCommentId());
		    bean.setParentObjectType(parentComment.getObjectType());
		    bean.setParentObjectId(parentComment.getObjectId());
		}

		// source post checking
		commentDao.createComment(newComment);
		String key = getTreeWalkerCacheKey(objectType, objectId);
		synchronized (key) {
		    treeWalkerCache.remove(key);
		}		
		fireEvent(new CommentEvent(newComment, CommentEvent.Type.CREATED));
	}

	public Comment createComment(int objectType, long objectId, User user, String text) {
		DefaultComment comment = new DefaultComment();
		comment.setObjectType(objectType);
		comment.setObjectId(objectId);
		comment.setBody(text);
		comment.setUser(user);
		return comment;
	}

	public ModelObjectTreeWalker getCommentTreeWalker(int objectType, long objectId) {
		String key = getTreeWalkerCacheKey(objectType, objectId);
		ModelObjectTreeWalker treeWalker;
		if (treeWalkerCache.get(key) != null) {
		    treeWalker = (ModelObjectTreeWalker) treeWalkerCache.get(key).getObjectValue();
		} else {
		    synchronized (key) {
			treeWalker = commentDao.getTreeWalker(objectType, objectId);
			treeWalkerCache.put( new Element(key, treeWalker ) );
		    }
		}
		return treeWalker;
	}

	protected void evictCaches(Comment comment) {
		String key = getTreeWalkerCacheKey(comment.getObjectType(), comment.getObjectId());
		commentCache.remove(comment.getCommentId());
		synchronized (key) {
			treeWalkerCache.remove(key);
		}
	}

	private static String getTreeWalkerCacheKey(int objectType, long objectId) {
		return (new StringBuilder("commentTreeWalker-")).append(objectType).append("-").append(objectId).toString();
	}
}
