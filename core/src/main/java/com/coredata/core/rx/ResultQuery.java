package com.coredata.core.rx;

import com.coredata.core.CoreDao;
import com.coredata.core.db.QuerySet;

import java.util.List;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class ResultQuery<T> {
    private final CoreDao<T> coreDao;
    private final QuerySet<T> querySet;

    private final Function<QueryData, QuerySet<T>> mapResult = new Function<QueryData, QuerySet<T>>() {
        @Override
        public QuerySet<T> apply(QueryData data) {
            return querySet;
        }
    };

    private final Function<QuerySet<T>, List<T>> mapList = new Function<QuerySet<T>, List<T>>() {
        @Override
        public List<T> apply(QuerySet<T> querySet) {
            return querySet.result();
        }
    };

    private final Predicate<QueryData> predicateQuery = new Predicate<QueryData>() {
        @Override
        public boolean test(QueryData data) {
            return data.getCoreDao() == coreDao;
        }
    };

    public ResultQuery(CoreDao<T> coreDao, QuerySet<T> querySet) {
        this.coreDao = coreDao;
        this.querySet = querySet;
    }

    public Predicate<QueryData> getPredicateQuery() {
        return predicateQuery;
    }

    public Function<QueryData, QuerySet<T>> getMapResult() {
        return mapResult;
    }

    public Function<QuerySet<T>, List<T>> getMapList() {
        return mapList;
    }
}
