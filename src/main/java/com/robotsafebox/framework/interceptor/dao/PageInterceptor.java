package com.robotsafebox.framework.interceptor.dao;

import com.robotsafebox.framework.model.Pager;
import com.robotsafebox.utils.ReflectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * 分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。 利用拦截器实现Mybatis分页的原理：
 * 要利用JDBC对数据库进行操作就必须要有一个对应的Statement对象，Mybatis在执行Sql语句前就会产生一个包含Sql语句的Statement对象，
 * 而且对应的Sql语句是在Statement之前产生的，所以我们就可以在它生成Statement之前对用来生成Statement的Sql语句下手。
 * 在Mybatis中Statement语句是通过RoutingStatementHandler对象的prepare方法生成的。
 * 所以利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法 ，
 * 然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句，之后再调用StatementHandler对象的prepare方法，即调用invocation.proceed()。
 * 对于分页而言，在拦截器里面我们还需要做的一个操作就是统计满足当前条件的记录一共有多少，
 * 这是通过获取到了原始的Sql语句后，把它改为对应的统计语句再利用Mybatis封装好的参数和设置参数的功能把Sql语句中的参数进行替换，
 * 之后再执行查询记录数的Sql语句进行总记录数的统计。
 */
@Intercepts({@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class})})
public class PageInterceptor implements Interceptor {

    private String databaseType;

    /**
     * 拦截后要执行的方法s
     * 对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler，
     * BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，
     * SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，而CallableStatementHandler是
     * 处理CallableStatement的。Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个
     * StatementHandler类型的delegate属性，RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、
     * PreparedStatementHandler或CallableStatementHandler，在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。
     * 我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候
     * 是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。
     */
    public Object intercept(Invocation invocation) throws Throwable {

        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        // 通过反射获取到当前RoutingStatementHandler对象的delegate属性
        StatementHandler delegate = ReflectUtil.getFieldValue(handler, "delegate");

        // 获取到当前StatementHandler的boundSql，
        // 这里不管是调用handler.getBoundSql()还是直接调用delegate.getBoundSql()结果是一样的，因为之前已经说过了
        // RoutingStatementHandler实现的所有StatementHandler接口方法里面都是调用的delegate对应的方法。
        BoundSql boundSql = delegate.getBoundSql();
        // 拿到当前绑定Sql的参数对象，就是我们在调用对应的Mapper映射语句时所传入的参数对象
        Object obj = boundSql.getParameterObject();
        // 这里我们简单的通过传入的是Page对象就认定它是需要进行分页操作的。
        Pager<?> pager = null;
        try {
            if (obj instanceof Map) {
                pager = (Pager<?>) ((Map) obj).get("pager");
            } else if (obj instanceof Pager<?>) {
                pager = (Pager<?>) obj;
            } else {
                Method m = obj.getClass().getDeclaredMethod("getPager");
                pager = (Pager<?>) m.invoke(obj);
            }
        } catch (Exception ignored) {
        }
        if (pager != null) {
            // 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
            MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
            // 拦截到的prepare方法参数是一个Connection对象
            Connection connection = (Connection) invocation.getArgs()[0];
            // 获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
            String sql = boundSql.getSql();

            if (pager.getOthersql() != null) {
                sql += pager.getOthersql();
            }

            //根据分页类进行分页查询结果集
            String pageSql = sql;
            if (pager.getReturnPager()) {
                setPageParameter(sql, connection, mappedStatement, boundSql, pager);
                // 获取分页Sql语句
                pageSql = this.getPageSql(pager, sql);
            }
            // 利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
            ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
        }
        return invocation.proceed();
    }

    /**
     * 拦截器对应的封装原始对象的方法
     */
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置注册拦截器时设定的属性
     */
    public void setProperties(Properties properties) {
        this.databaseType = properties.getProperty("databaseType");
    }

    /**
     * 根据page对象获取对应的分页查询Sql语句，这里只做了两种数据库类型，Mysql("mysql")和Oracle ("oracle")其它的数据库都 没有进行分页
     */
    private String getPageSql(Pager<?> page, String sql) {
        StringBuffer sqlBuffer = new StringBuffer(sql);
        if ("mysql".equalsIgnoreCase(databaseType)) {
            return getMysqlPageSql(page, sqlBuffer);
        } else if ("oracle".equalsIgnoreCase(databaseType)) {
            return getOraclePageSql(page, sqlBuffer);
        }
        return sqlBuffer.toString();
    }

    /**
     * 获取Mysql数据库的分页查询语句
     */
    private String getMysqlPageSql(Pager<?> page, StringBuffer sqlBuffer) {
        // 计算第一条记录的位置，Mysql中记录的位置是从0开始的。
        int offset = (page.getPageNo() - 1) * page.getPageSize();
        getMysqlPageConditionSql(page, sqlBuffer);

        int endset = page.getPageSize();
        if (page.getTotalRecord() < page.getPageNo() * page.getPageSize()) {
            endset = page.getTotalRecord() - (page.getPageNo() - 1) * page.getPageSize();
        }
        if (endset < 1) {
            endset = 1;
        }

        sqlBuffer.append(" limit ").append(offset).append(",").append(endset);
        return sqlBuffer.toString();
    }

    /**
     * 查询条件
     */
    private StringBuffer getMysqlPageConditionSql(Pager<?> page, StringBuffer sqlBuffer) {
        if (page.getCustomparams().size() != 0) {

            sqlBuffer.append(" where ");

            Map<String, Object> map = page.getCustomparams();
            for (String k : map.keySet())
                sqlBuffer.append(" ").append(map.get(k)).append(" ");
        }
        return sqlBuffer;
    }

    /**
     * 获取Oracle数据库的分页查询语句
     */
    private String getOraclePageSql(Pager<?> page, StringBuffer sqlBuffer) {
        int offset = (page.getPageNo() - 1) * page.getPageSize() + 1;
        sqlBuffer.insert(0, "select u.*, rownum r from (").append(") u where rownum < ").append(offset + page.getPageSize());
        sqlBuffer.insert(0, "select * from (").append(") where r >= ").append(offset);
        return sqlBuffer.toString();
    }

    private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, Pager pager) {
        // 记录总记录数
        String countSql = getCountSql(sql);
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            pager.setTotalRecord(totalCount);
            int totalPage = totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1);
            pager.setTotalPage(totalPage);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (countStmt != null) {
                    countStmt.close();
                }
            } catch (SQLException ignored) {
            }
        }

    }

    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    /**
     * 根据原Sql语句获取对应的查询总记录数的Sql语句
     */
    private String getCountSql(String sql) {
        int index = StringUtils.indexOfIgnoreCase(sql, "from");
        return "select count(*) " + sql.substring(index);
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

}