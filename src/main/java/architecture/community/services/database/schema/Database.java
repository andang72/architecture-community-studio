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

import java.util.HashMap;
import java.util.Map;

/**
 * @author  donghyuck
 */
public class Database {

	/**
	 * @uml.property  name="catalog"
	 */
	private String catalog;
	/**
	 * @uml.property  name="schema"
	 */
	private String schema;

	private Map tables = new HashMap();

	public Database(String catalog, String schema) {
		this.catalog = catalog;
		this.schema = schema;
	}

	/**
	 * @return
	 * @uml.property  name="catalog"
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @return
	 * @uml.property  name="schema"
	 */
	public String getSchema() {
		return schema;
	}

	public void addTable(Table table) {
		tables.put(table.getName().toUpperCase(), table);
	}

	public Table getTable(String name) {
		return (Table) tables.get(name.toUpperCase());
	}

	public String[] getTableNames() {
		return (String[]) tables.keySet().toArray(new String[tables.size()]);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final Database database = (Database) o;

		if (catalog != null ? !catalog.equals(database.catalog)
				: database.catalog != null)
			return false;
		if (schema != null ? !schema.equals(database.schema)
				: database.schema != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (catalog != null ? catalog.hashCode() : 0);
		result = 29 * result + (schema != null ? schema.hashCode() : 0);
		return result;
	}

}
