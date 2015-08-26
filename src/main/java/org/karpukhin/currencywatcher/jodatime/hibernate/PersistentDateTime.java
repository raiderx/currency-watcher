package org.karpukhin.currencywatcher.jodatime.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Pavel Karpukhin
 * @since 13.05.15
 */
public class PersistentDateTime implements UserType {

    private static final int[] SQL_TYPES = new int[] { Types.TIMESTAMP, };

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass() {
        return DateTime.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        DateTime dtx = (DateTime) x;
        DateTime dty = (DateTime) y;

        return dtx.equals(dty);
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(resultSet, names[0], session);
    }

    public Object nullSafeGet(ResultSet resultSet, String name, SessionImplementor session) throws SQLException {
        Object timestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(resultSet, name, session);
        if (timestamp == null) {
            return null;
        }

        return new DateTime(timestamp);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(preparedStatement, null, index, session);
        } else {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(preparedStatement, ((DateTime) value).toDate(), index, session);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
