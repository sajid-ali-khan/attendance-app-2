package com.example.attendance_app_2

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException
import kotlin.jvm.Throws

object HikariCPDataSource  {
    private const val DB_URL = "jdbc:jtds:sqlserver://192.168.202.44:49170;databaseName=college"
    private const val DB_USER = "sajid"
    private const val DB_PASSWORD = "S@j1d2024!"

    private lateinit var dataSource: HikariDataSource

    init {
        val config = HikariConfig().apply {
            jdbcUrl = DB_URL
            username = DB_USER
            password = DB_PASSWORD
            driverClassName = "net.sourceforge.jtds.jdbc.Driver"
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            maxLifetime = 1800000
            connectionTimeout = 10000
        }
        dataSource = HikariDataSource(config)
    }

    @Throws(SQLException::class)
    fun getConnection(): Connection{
        return dataSource.connection
    }

    fun closePool(){
        dataSource.close()
    }
}