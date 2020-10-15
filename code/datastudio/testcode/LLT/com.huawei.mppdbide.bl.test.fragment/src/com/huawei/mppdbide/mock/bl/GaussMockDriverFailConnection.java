/**
 * 
 */
package com.huawei.mppdbide.mock.bl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockDriver;

/**
 * @author a00900918
 * 
 */
public class GaussMockDriverFailConnection extends MockDriver
{

    private static int                       connectionCount       = 0;
    private static int                       failOnConnectionCount = 0;
    private static String                    lastUrl               = "";
    private static Properties                lastProps             = null;
    private static ArrayList<MockConnection> connections           = null;
    private static int                       activeConnection      = 0;
    private boolean                   connectFail           = false;

    public GaussMockDriverFailConnection()
    {
        super();
        connections = new ArrayList<MockConnection>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.mock.jdbc.MockDriver#connect(java.lang.String,
     * java.util.Properties)
     */
    @Override
    public Connection connect(String url, Properties props) throws SQLException
    {
        if ("invalidUrl".equals(url))
        {
            throw new SQLException("Connection failed.");
        }
         connectionCount++;
        if (connectionCount == failOnConnectionCount)
        {
            if(isConnectFail()){
                SQLException sqlException = new SQLException(
                        "Throwing SQL exception intentionally.", "57PSQLException");
                throw sqlException;
            }else{
            throw new SQLException("Connection failed.");
            }
        }

        lastUrl = url;
        lastProps = props;

        return super.connect(url, props);
    }

    public int getConnectionCount()
    {
        return connectionCount;
    }

    public void resetConnectionCounters()
    {
        connectionCount = 0;
        failOnConnectionCount = 0;
        lastUrl = "";
        lastProps = null;
        activeConnection = 0;

        if (!connections.isEmpty())
        {
            for (MockConnection connection : connections)
            {

                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    // Skip the errors
                }

                connections.remove(connection);
            }
        }
    }

    public boolean isAllConnectionsClosed()
    {
        if (!connections.isEmpty())
        {
            try
            {
                for (MockConnection connection : connections)
                {
                    if (!connection.isClosed())
                    {
                        return false;
                    }
                }
            }
            catch (SQLException e)
            {
                return false;
            }
        }

        return true;
    }

    public void setFailOnConnectionCount(int count)
    {
        failOnConnectionCount = count;
    }

    public boolean isConnectionParams(String url, String username,
            String password)
    {
        return (lastUrl.equals(url) && username.equals(lastProps.get("user")) && password == null);
    }

    public void setActiveConnection(int active)
    {
        activeConnection = active;
    }

    public MockConnection getActiveConnection()
    {
        if (!connections.isEmpty())
        {
            return connections.get(activeConnection);
        }

        return null;
    }

    public boolean isConnectFail()
    {
        return connectFail;
    }

    public void setConnectFail(boolean connectFail)
    {
        this.connectFail = connectFail;
    }

}
