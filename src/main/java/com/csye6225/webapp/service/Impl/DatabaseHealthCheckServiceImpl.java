package com.csye6225.webapp.service.Impl;

import com.csye6225.webapp.service.DatabaseHealthCheckService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
@AllArgsConstructor
public class DatabaseHealthCheckServiceImpl implements DatabaseHealthCheckService {

    private DataSource dataSource;

    @Override
    public boolean checkDatabaseConnection()
    {
        try(Connection connection = dataSource.getConnection())
        {
            return true;
        }
        catch(SQLException e)
        {
            System.out.println("Database not connected: " + e);
            return false;
        }
    }

}
