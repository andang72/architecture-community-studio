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

package architecture.community.streams;

import architecture.community.util.LongTree;

public class MessageTreeWalker {

	private long threadId;

	private LongTree tree;

	public MessageTreeWalker(long threadId, LongTree tree) {
		this.threadId = threadId;
		this.tree = tree;
	}

	public long getThreadId() {
		return threadId;
	}

	protected int getRecursiveChildCount(long parentId) {
		int numChildren = 0;
		int num = tree.getChildCount(parentId);
		numChildren += num;
		for (int i = 0; i < num; i++) {
			long childID = tree.getChild(parentId, i);
			if (childID != -1L)
				numChildren += getRecursiveChildCount(childID);
		}
		return numChildren;
	}

	public int getMessageDepth(StreamMessage message) {
		int depth = tree.getDepth(message.getMessageId());
		if (depth == -1)
			throw new IllegalArgumentException((new StringBuilder()).append("Message ").append(message.getMessageId()).append(" does not belong to this document.").toString());
		else
			return depth - 1;
	}

	public int getRecursiveChildCount(StreamMessage parent) {
		return getRecursiveChildCount(parent.getMessageId());
	}

	public int getChildCount(StreamMessage message) {
		return tree.getChildCount(message.getMessageId());
	}

	public boolean isLeaf(StreamMessage message) {
		return tree.isLeaf(message.getMessageId());
	}

	public boolean hasParent(StreamMessage message) {
		long parentID = tree.getParent(message.getMessageId());
		return parentID != -1L;
	}
	
	public StreamMessage getParent(StreamMessage message) throws StreamMessageNotFoundException {
		long parentId = tree.getParent(message.getMessageId());
		if (parentId == -1L) {
			return null;
		} else {
			return null;
		}
	}

	public StreamMessage getChild(StreamMessage message, int index) throws StreamMessageNotFoundException {
		long childId = tree.getChild(message.getMessageId(), index);
		if (childId == -1L) {
			return null;
		} else {
			return null;
		}
	}

    public long[] getRecursiveChildren(StreamMessage parent)
    {
        long messages[] = tree.getRecursiveChildren(parent.getMessageId());
        return messages;
    }
  
	public long[] getChildIds(StreamMessage parent) {
		return tree.getChildren(parent.getMessageId());
	}

	public int getIndexOfChild(StreamMessage parent, StreamMessage child){
		return tree.getIndexOfChild(parent.getMessageId(), child.getMessageId());
	}

}
