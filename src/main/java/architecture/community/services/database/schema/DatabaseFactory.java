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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

public class DatabaseFactory {

	private static  Logger log = LoggerFactory.getLogger(DatabaseFactory.class);

	private DatabaseFactory() {

	}

	public static Database newDatabase(Connection conn, String catalogFilter, String schemaFilter) throws SQLException {

		return newDatabase(conn, catalogFilter, schemaFilter, null);
	}

	
	private static String getTypeName ( Integer dataType ) {
		try {
			return java.sql.JDBCType.valueOf(dataType).getName();
		}catch (IllegalArgumentException e) {
			return "unknown";
		}
	}
 
	public static Database newDatabase(Connection conn, String catalogFilter, String schemaFilter, String tableNameFilter) throws SQLException {
		
		log.debug("extract database with catalogFilter:{}, schemaFilter:{}, tableNameFilter:{}",catalogFilter, schemaFilter, tableNameFilter );		
		Database database = new Database(catalogFilter, schemaFilter);
		ResultSet rs = null;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			boolean opt = ClassUtils.isPresent("java.sql.JDBCType", ClassUtils.getDefaultClassLoader());
			log.debug("java.sql.JDBCType is exists : {}", opt); 
			try {
				rs = dbmd.getColumns(catalogFilter, schemaFilter, tableNameFilter, null);
				while (rs.next()) { 
					String catalogName = rs.getString("TABLE_CAT");
					String schemaName = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");
					String columnName = rs.getString("COLUMN_NAME");
					int dataType = Integer.parseInt(rs.getString("DATA_TYPE"));
					
					Table table = database.getTable(tableName);
					if (table == null) {
						table = new Table(tableName);
						table.setCatalog(catalogName);
						table.setSchema(schemaName);
						database.addTable(table);
					}
					if( opt )
						table.addColumn(new Column(columnName, dataType, getTypeName(dataType)));
					else
						table.addColumn(new Column(columnName, dataType));
					
				}
			} finally {
				if (rs != null)
					rs.close();
			}
			String[] tableNames = database.getTableNames();
			for (int i = 0; i < tableNames.length; i++) {
				Table table = database.getTable(tableNames[i]);
				try {
					rs = dbmd.getPrimaryKeys(catalogFilter, schemaFilter, table.getName());
					if (rs.next()) {
						String columnName = rs.getString("COLUMN_NAME");
						table.setPrimaryKey(table.getColumn(columnName));
					}
				}catch(Exception e) {
					// ignore..
				} finally {
					if (rs != null)
						rs.close();
				}
			}
		} finally {
			try {
				conn.rollback();
			} catch (Exception e) { 
				/* ignore */
			}
		}
		return database;
	}
}