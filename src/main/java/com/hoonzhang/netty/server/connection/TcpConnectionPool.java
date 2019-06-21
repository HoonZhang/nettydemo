package com.hoonzhang.netty.server.connection;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @author: HoonZhang
 * @Creation Date: 2019-06-17 19:10
 * @ModificationHistory:
 * @Link:
 */
public class TcpConnectionPool {
    private static ConcurrentHashMap<InetSocketAddress, TcpConnection> connectionPool = new ConcurrentHashMap<>();

    public static TcpConnection getConnection(InetSocketAddress socketAddress) {
        TcpConnection conn = connectionPool.get(socketAddress);
        if (conn == null) {
            synchronized (TcpConnectionPool.class) {
                conn = connectionPool.get(socketAddress);
                if (conn == null) {
                    conn = new TcpConnection(socketAddress);
                    conn.connect();
                    connectionPool.put(socketAddress, conn);
                }
            }
        } else if (!conn.isConnected()) {
            synchronized (TcpConnectionPool.class) {
                if (!conn.isConnected()) {
                    conn.reconnect();
                }
            }
        }

        return conn;
    }
}
