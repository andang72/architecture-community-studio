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

import java.util.ArrayList;
import java.util.List;

import architecture.community.model.ModelObjectTreeWalker;
import architecture.community.util.CommunityContextHelper;
import architecture.community.util.LongTree;

public class CommentTreeWalker extends ModelObjectTreeWalker{
	
	/**
     * @param objectType
     * @param objectId
     * @param tree
     */
	public CommentTreeWalker(int objectType, long objectId, LongTree tree) {
		super(objectType, objectId, tree);
	}
	
	public int getRecursiveChildCount(Comment parent) {
		return getRecursiveChildCount(parent.getCommentId());
	}

	public int getIndexOfChild(Comment parent, Comment child) {
		return getTree().getIndexOfChild(parent.getCommentId(), child.getCommentId());
	}
	
	public int getCommentDepth(Comment comment) {
		int depth = getTree().getDepth(comment.getCommentId());
		if (depth == -1)
			throw new IllegalArgumentException((new StringBuilder()).append("Comment ").append(comment.getCommentId()).append(" does not belong to this document.").toString());
		else
			return depth - 1;
	}


	public boolean isLeaf(Comment comment) {
		return getTree().isLeaf(comment.getCommentId());
	}
 
	public int getChildCount(Comment comment) {
		return getTree().getChildCount(comment.getCommentId());
	}
 
	
	
	public List<Comment> topLevelComments() {
		return children(new DefaultComment(-1L));
	}

	
	public Comment getParent(Comment comment) throws CommentNotFoundException {
		long parentId = getTree().getParent(comment.getCommentId());
		if (parentId == -1L) {
			return null;
		} else {
			return CommunityContextHelper.getCommentService().getComment(parentId);
		}
	}
 
	public Comment getChild(Comment comment, int index) throws CommentNotFoundException {
		long childId = getTree().getChild(comment.getCommentId(), index);
		if (childId == -1L) {
			return null;
		} else {
			return CommunityContextHelper.getCommentService().getComment(childId);
		}
	}
 
	public List<Comment> children(Comment comment) {
		long children[] = getTree().getChildren(comment.getCommentId());
		List<Comment> list = new ArrayList<Comment>();
		CommentService commentService = CommunityContextHelper.getCommentService();
		for (long childId : children)
			try {
				list.add(commentService.getComment(childId));
			} catch (CommentNotFoundException e) {
			}
		return list;
	}
 
	public List<Comment> recursiveChildren(Comment comment) {
		long comments[] = getTree().getRecursiveChildren(comment.getCommentId());
		List<Comment> list = new ArrayList<Comment>();
		CommentService commentService = CommunityContextHelper.getCommentService();
		for (long commentId : comments)
			try {
				list.add(commentService.getComment(commentId));
			} catch (CommentNotFoundException e) {
				e.printStackTrace();
			}
		return list;
	}
	
	
	public interface ObjectLoader<T>{
		T load( long primaryKey );
	}

}
