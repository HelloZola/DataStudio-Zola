package com.huawei.mppdbide.mock.bl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.huawei.mppdbide.mock.bl.CommonLLTUtilsHelper;
import com.huawei.mppdbide.mock.bl.GaussMockPreparedStatementToHang;
import com.mockrunner.mock.jdbc.MockStatement;

public class MockStatementToHang extends MockStatement
{
    private static ArrayList<String> hangQueries = new ArrayList<String>();
    
    public MockStatementToHang(Connection connection)
    {
        super(connection);
    }
    
    public MockStatementToHang(Connection connection, int resultSetType, int resultSetConcurrency)
    {
        super(connection, resultSetType, resultSetConcurrency);
    }

    public MockStatementToHang(Connection connection, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    {
        super(connection, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    @Override
    public ResultSet executeQuery(String sql) throws SQLException
    {
        hang(sql);
        return super.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException
    {
        hang(sql);
        return super.executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException
    {
        hang(sql);
        return super.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException
    {
        hang(sql);
        return super.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException
    {
        hang(sql);
        return super.executeUpdate(sql, columnNames);
    }

    
    
    public static void setHang(String query)
    {
        hangQueries.add(query);
    }
    
    public static void resetHang(String query)
    {
        hangQueries.remove(query);
    }
    
    private void hang(String query)
    {
        System.out.println("Hang query: "+query+", \n all other: "+hangQueries);
        if (!hangQueries.contains(query))
        {
            return;
        }
        
        GaussMockPreparedStatementToHang.resetHang(CommonLLTUtilsHelper.SYNC_QUERY);
        int counter = 0;
        while (hangQueries.contains(query))
        {
            try
            {
                Thread.sleep(100);
                
                if(counter++ > 50)
                {
                    break;
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
    
    public static void resetHangQueries()
    {
        hangQueries.clear();
    }
}
