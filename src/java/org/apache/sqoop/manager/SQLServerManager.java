/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.sqoop.manager;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudera.sqoop.SqoopOptions;
import com.cloudera.sqoop.mapreduce.ExportBatchOutputFormat;
import com.cloudera.sqoop.mapreduce.JdbcExportJob;
import com.cloudera.sqoop.util.ExportException;

/**
 * Manages connections to SQLServer databases. Requires the SQLServer JDBC
 * driver.
 */
public class SQLServerManager
    extends com.cloudera.sqoop.manager.InformationSchemaManager {

  public static final Log LOG = LogFactory.getLog(
      SQLServerManager.class.getName());

  // driver class to ensure is loaded when making db connection.
  private static final String DRIVER_CLASS =
      "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  public SQLServerManager(final SqoopOptions opts) {
    super(DRIVER_CLASS, opts);
  }

  /**
   * Export data stored in HDFS into a table in a database.
   */
  @Override
  public void exportTable(com.cloudera.sqoop.manager.ExportJobContext context)
      throws IOException, ExportException {
    context.setConnManager(this);
    JdbcExportJob exportJob = new JdbcExportJob(context, null, null,
      ExportBatchOutputFormat.class);
    exportJob.runExport();
  }

  /**
   * SQLServer does not support the CURRENT_TIMESTAMP() function. Instead
   * it has the notion of keyword CURRENT_TIMESTAMP that resolves to the
   * current time stamp for the database system.
   */
  @Override
  public String getCurTimestampQuery() {
      return "SELECT CURRENT_TIMESTAMP";
  }

  @Override
  protected String getListDatabasesQuery() {
    return "SELECT NAME FROM SYS.DATABASES";
  }

  @Override
  protected String getSchemaQuery() {
    return "SELECT SCHEMA_NAME()";
  }

  @Override
  public String escapeColName(String colName) {
    if (null == colName) {
      return null;
    }
    return "[" + colName + "]";
  }

  @Override
  public String escapeTableName(String tableName) {
    if (null == tableName) {
      return null;
    }
    return "[" + tableName + "]";
  }
}

