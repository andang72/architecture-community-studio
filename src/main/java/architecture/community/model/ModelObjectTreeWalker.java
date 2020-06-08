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

package architecture.community.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;

import architecture.community.exception.NotFoundException;
import architecture.community.util.LongTree;

public class ModelObjectTreeWalker {

	private LongTree tree;
	
	private ModelObject modelObject;

	public ModelObjectTreeWalker(ModelObject modelObject, LongTree tree) {
		this.modelObject = modelObject;
		this.tree = tree;
	}

	public ModelObjectTreeWalker(int objectType, long objectId, LongTree tree) {
		this.modelObject = new PropertyModelObjectAwareSupport(objectType, objectId);
		this.tree = tree;
	}
	
	
	public int getObjectType(){
		return modelObject.getObjectType();
	}
	
	public long getObjectId(){
		return modelObject.getObjectId();
	}

	protected LongTree getTree() {
		return tree;
	}

	protected ModelObject getModelObject() {
		return modelObject;
	}
	

	public T getParent(long childId, ObjectLoader<T> loader) throws NotFoundException {
		long parentId = tree.getParent(childId);
		if (parentId == -1L) {
			return null;
		} else {
			return loader.load(parentId);
		}
	}

	public boolean isLeaf(long objectId) {
		return tree.isLeaf(objectId);
	}
	
	public int getChildCount(long objectId) {
		return tree.getChildCount(objectId);
	}
 
	
	public T getChild(long parentId, int index, ObjectLoader<T> loader) throws NotFoundException {
		long childId = tree.getChild(parentId, index);
		if (childId == -1L) {
			return null;
		} else {
			return loader.load(childId);
		}
	}
	
	public <T> List<T> children(long parentId, ObjectLoader<T> loader) {
		long children[] = tree.getChildren(parentId);
		List<T> list = new ArrayList<T>();
		for (long child : children) {
			try {
				list.add(loader.load(child));
			} catch (NotFoundException e) {
			}
		}
		return list;
	}		
	
	public int getRecursiveChildCount(long parentId) {
		int numChildren = 0;
		int num = tree.getChildCount(parentId);
		numChildren += num;
		for (int i = 0; i < num; i++) {
			long childId = tree.getChild(parentId, i);
			if (childId != -1L)
				numChildren += getRecursiveChildCount(childId);
		}
		return numChildren;
	}
	
	@SuppressWarnings("hiding")
	public <T> List<T> recursiveChildren(long parentId, ObjectLoader<T> loader ) {
		long children[] = tree.getRecursiveChildren(parentId);
		List<T> list = new ArrayList<T>();
		for (long child : children){
			try {
				list.add(loader.load(child));
			} catch (NotFoundException e) {
			}
		}
		return list;
	}
	
	@SuppressWarnings("hiding")
	public interface ObjectLoader<T>{
		T load( long primaryKey ) throws NotFoundException ;
	}
	

}
