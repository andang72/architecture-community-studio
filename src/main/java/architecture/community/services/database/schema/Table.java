/*
 * Copyright 2012 Donghyuck, Son
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package architecture.community.services.database.schema;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author   donghyuck
 */
public class Table {

	private String name;

	private String catalog;

	private String schema;
	
	private Map<String, Column> columns = new LinkedHashMap<String, Column>();

	private Column primaryKey;

	public Table(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @param  catalog
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/**
	 * @return
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param  schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void addColumn(Column col) {
		columns.put(col.getName().toUpperCase(), col);
	}

	public Column getColumn(String name) {
		return columns.get(name.toUpperCase());
	}

	public String[] getColumnNames() {
		return columns.keySet().toArray(new String[columns.size()]);
	}

	/**
	 * @param  column
	 */
	public void setPrimaryKey(Column column) {
		primaryKey = column;
	}

	/**
	 * @return
	 */
	public Column getPrimaryKey() {
		return primaryKey;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Table table = (Table) o;
		if (name != null ? !name.equals(table.name) : table.name != null)
			return false;
		return true;
	}

	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}

}
