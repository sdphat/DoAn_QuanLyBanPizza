/*
  Copyright (c) 2007, 2019, Oracle and/or its affiliates. All rights reserved.

  The MySQL Connector/J is licensed under the terms of the GPLv2
  <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL Connectors.
  There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
  this software, see the FOSS License Exception
  <http://www.mysql.com/about/legal/licensing/foss-exception.html>.

  This program is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation; version 2
  of the License.

  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with this
  program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
  Floor, Boston, MA 02110-1301  USA

 */

package com.mysql.jdbc.profiler;

import com.mysql.jdbc.*;
import com.mysql.jdbc.log.Log;

import java.sql.SQLException;
import java.util.Properties;

/**
 * A profile event handler that just logs to the standard logging mechanism of the JDBC driver.
 */
public class LoggingProfilerEventHandler implements ProfilerEventHandler {
    private Log log;

    public LoggingProfilerEventHandler() {
    }

    public void consumeEvent(ProfilerEvent evt) {
        switch (evt.getEventType()) {
            case ProfilerEvent.TYPE_USAGE:
                this.log.logWarn(evt);
                break;

            default:
                this.log.logInfo(evt);
                break;
        }
    }

    public void destroy() {
        this.log = null;
    }

    public void init(Connection conn, Properties props) throws SQLException {
        this.log = conn.getLog();
    }

    public void processEvent(byte eventType, MySQLConnection conn, Statement stmt, ResultSetInternalMethods resultSet, long eventDuration,
            Throwable eventCreationPoint, String message) {
        String catalog = "";
        try {
            if (conn != null) {
                catalog = conn.getCatalog();
            }
        } catch (SQLException e) {
            // ignore, should not happen
        }
        consumeEvent(new ProfilerEvent(eventType, conn == null ? "" : conn.getHost(), catalog, conn == null ? ProfilerEvent.NA : conn.getId(),
                stmt == null ? ProfilerEvent.NA : stmt.getId(), resultSet == null ? ProfilerEvent.NA : resultSet.getId(), eventDuration,
                conn == null ? Constants.MILLIS_I18N : conn.getQueryTimingUnits(), eventCreationPoint, message));

    }

}
