package com.coredata.core.rx;

import com.coredata.core.CoreDao;
import com.coredata.core.ResultSet;

import java.util.List;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class ResultQuery<T> {
    private final CoreDao<T> coreDao;
    private final ResultSet<T> resultSet;

    private final Function<QueryData, ResultSet<T>> mapResult = new Function<QueryData, ResultSet<T>>() {
        @Override
        public ResultSet<T> apply(QueryData data) {
            return resultSet;
        }
    };

    private final Function<ResultSet<T>, List<T>> mapList = new Function<ResultSet<T>, List<T>>() {
        @Override
        public List<T> apply(ResultSet<T> resultSet) {
            return resultSet.result();
        }
    };

    private final Predicate<QueryData> predicateQuery = new Predicate<QueryData>() {
        @Override
        public boolean test(QueryData data) {
            return data.getCoreDao() == coreDao;
        }
    };

    public ResultQuery(CoreDao<T> coreDao, ResultSet<T> resultSet) {
        this.coreDao = coreDao;
        this.resultSet = resultSet;
    }

    public Predicate<QueryData> getPredicateQuery() {
        return predicateQuery;
    }

    public Function<QueryData, ResultSet<T>> getMapResult() {
        return mapResult;
    }

    public Function<ResultSet<T>, List<T>> getMapList() {
        return mapList;
    }
}
