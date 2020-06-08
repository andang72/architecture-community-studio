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

import architecture.community.model.ModelObjectTreeWalker;
import architecture.community.user.User;

public interface CommentService {

	public abstract Comment getComment(long commentId) throws CommentNotFoundException;

	public abstract void update(Comment comment);
	
	public abstract void delete(Comment comment);

	public abstract void setBody(Comment comment, String text);

	public abstract void addComment(Comment comment);
	
	public abstract void addComment(Comment parentComment, Comment comment);

	public abstract Comment createComment(int objectType, long objectId, User user, String text);

	public ModelObjectTreeWalker getCommentTreeWalker(int objectType, long objectId);

}
