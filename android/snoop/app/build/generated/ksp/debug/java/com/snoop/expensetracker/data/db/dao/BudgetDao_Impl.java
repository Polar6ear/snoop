package com.snoop.expensetracker.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.snoop.expensetracker.data.db.Converters;
import com.snoop.expensetracker.data.db.entities.BudgetEntity;
import com.snoop.expensetracker.data.db.entities.BudgetPeriod;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BudgetEntity> __insertionAdapterOfBudgetEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfMarkWarningNotified;

  private final SharedSQLiteStatement __preparedStmtOfMarkOverNotified;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForCategory;

  private final EntityUpsertionAdapter<BudgetEntity> __upsertionAdapterOfBudgetEntity;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBudgetEntity = new EntityInsertionAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `budgets` (`id`,`categoryId`,`limitMinor`,`period`,`startDate`,`warningNotifiedPeriodStart`,`overNotifiedPeriodStart`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getLimitMinor());
        final String _tmp = __converters.fromPeriod(entity.getPeriod());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getStartDate());
        if (entity.getWarningNotifiedPeriodStart() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getWarningNotifiedPeriodStart());
        }
        if (entity.getOverNotifiedPeriodStart() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getOverNotifiedPeriodStart());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets";
        return _query;
      }
    };
    this.__preparedStmtOfMarkWarningNotified = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE budgets SET warningNotifiedPeriodStart = ? WHERE categoryId = ? AND (warningNotifiedPeriodStart IS NULL OR warningNotifiedPeriodStart != ?)";
        return _query;
      }
    };
    this.__preparedStmtOfMarkOverNotified = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE budgets SET overNotifiedPeriodStart = ? WHERE categoryId = ? AND (overNotifiedPeriodStart IS NULL OR overNotifiedPeriodStart != ?)";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteForCategory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE categoryId = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfBudgetEntity = new EntityUpsertionAdapter<BudgetEntity>(new EntityInsertionAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `budgets` (`id`,`categoryId`,`limitMinor`,`period`,`startDate`,`warningNotifiedPeriodStart`,`overNotifiedPeriodStart`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getLimitMinor());
        final String _tmp = __converters.fromPeriod(entity.getPeriod());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getStartDate());
        if (entity.getWarningNotifiedPeriodStart() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getWarningNotifiedPeriodStart());
        }
        if (entity.getOverNotifiedPeriodStart() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getOverNotifiedPeriodStart());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `budgets` SET `id` = ?,`categoryId` = ?,`limitMinor` = ?,`period` = ?,`startDate` = ?,`warningNotifiedPeriodStart` = ?,`overNotifiedPeriodStart` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getLimitMinor());
        final String _tmp = __converters.fromPeriod(entity.getPeriod());
        statement.bindString(4, _tmp);
        statement.bindLong(5, entity.getStartDate());
        if (entity.getWarningNotifiedPeriodStart() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getWarningNotifiedPeriodStart());
        }
        if (entity.getOverNotifiedPeriodStart() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getOverNotifiedPeriodStart());
        }
        statement.bindLong(8, entity.getId());
      }
    });
  }

  @Override
  public Object replaceAll(final List<BudgetEntity> budgets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBudgetEntity.insert(budgets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markWarningNotified(final long categoryId, final long periodStart,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkWarningNotified.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, periodStart);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, periodStart);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkWarningNotified.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markOverNotified(final long categoryId, final long periodStart,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkOverNotified.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, periodStart);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, periodStart);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkOverNotified.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForCategory(final long categoryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForCategory.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, categoryId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteForCategory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final BudgetEntity budget, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfBudgetEntity.upsert(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllOnce(final Continuation<? super List<BudgetEntity>> $completion) {
    final String _sql = "SELECT * FROM budgets ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinor");
          final int _cursorIndexOfPeriod = CursorUtil.getColumnIndexOrThrow(_cursor, "period");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfWarningNotifiedPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "warningNotifiedPeriodStart");
          final int _cursorIndexOfOverNotifiedPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "overNotifiedPeriodStart");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpLimitMinor;
            _tmpLimitMinor = _cursor.getLong(_cursorIndexOfLimitMinor);
            final BudgetPeriod _tmpPeriod;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriod);
            _tmpPeriod = __converters.toPeriod(_tmp);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpWarningNotifiedPeriodStart;
            if (_cursor.isNull(_cursorIndexOfWarningNotifiedPeriodStart)) {
              _tmpWarningNotifiedPeriodStart = null;
            } else {
              _tmpWarningNotifiedPeriodStart = _cursor.getLong(_cursorIndexOfWarningNotifiedPeriodStart);
            }
            final Long _tmpOverNotifiedPeriodStart;
            if (_cursor.isNull(_cursorIndexOfOverNotifiedPeriodStart)) {
              _tmpOverNotifiedPeriodStart = null;
            } else {
              _tmpOverNotifiedPeriodStart = _cursor.getLong(_cursorIndexOfOverNotifiedPeriodStart);
            }
            _item = new BudgetEntity(_tmpId,_tmpCategoryId,_tmpLimitMinor,_tmpPeriod,_tmpStartDate,_tmpWarningNotifiedPeriodStart,_tmpOverNotifiedPeriodStart);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getForCategory(final long categoryId,
      final Continuation<? super BudgetEntity> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE categoryId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinor");
          final int _cursorIndexOfPeriod = CursorUtil.getColumnIndexOrThrow(_cursor, "period");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfWarningNotifiedPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "warningNotifiedPeriodStart");
          final int _cursorIndexOfOverNotifiedPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "overNotifiedPeriodStart");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpLimitMinor;
            _tmpLimitMinor = _cursor.getLong(_cursorIndexOfLimitMinor);
            final BudgetPeriod _tmpPeriod;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriod);
            _tmpPeriod = __converters.toPeriod(_tmp);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpWarningNotifiedPeriodStart;
            if (_cursor.isNull(_cursorIndexOfWarningNotifiedPeriodStart)) {
              _tmpWarningNotifiedPeriodStart = null;
            } else {
              _tmpWarningNotifiedPeriodStart = _cursor.getLong(_cursorIndexOfWarningNotifiedPeriodStart);
            }
            final Long _tmpOverNotifiedPeriodStart;
            if (_cursor.isNull(_cursorIndexOfOverNotifiedPeriodStart)) {
              _tmpOverNotifiedPeriodStart = null;
            } else {
              _tmpOverNotifiedPeriodStart = _cursor.getLong(_cursorIndexOfOverNotifiedPeriodStart);
            }
            _result = new BudgetEntity(_tmpId,_tmpCategoryId,_tmpLimitMinor,_tmpPeriod,_tmpStartDate,_tmpWarningNotifiedPeriodStart,_tmpOverNotifiedPeriodStart);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BudgetEntity>> observeAll() {
    final String _sql = "SELECT * FROM budgets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLimitMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "limitMinor");
          final int _cursorIndexOfPeriod = CursorUtil.getColumnIndexOrThrow(_cursor, "period");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfWarningNotifiedPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "warningNotifiedPeriodStart");
          final int _cursorIndexOfOverNotifiedPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "overNotifiedPeriodStart");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpLimitMinor;
            _tmpLimitMinor = _cursor.getLong(_cursorIndexOfLimitMinor);
            final BudgetPeriod _tmpPeriod;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriod);
            _tmpPeriod = __converters.toPeriod(_tmp);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpWarningNotifiedPeriodStart;
            if (_cursor.isNull(_cursorIndexOfWarningNotifiedPeriodStart)) {
              _tmpWarningNotifiedPeriodStart = null;
            } else {
              _tmpWarningNotifiedPeriodStart = _cursor.getLong(_cursorIndexOfWarningNotifiedPeriodStart);
            }
            final Long _tmpOverNotifiedPeriodStart;
            if (_cursor.isNull(_cursorIndexOfOverNotifiedPeriodStart)) {
              _tmpOverNotifiedPeriodStart = null;
            } else {
              _tmpOverNotifiedPeriodStart = _cursor.getLong(_cursorIndexOfOverNotifiedPeriodStart);
            }
            _item = new BudgetEntity(_tmpId,_tmpCategoryId,_tmpLimitMinor,_tmpPeriod,_tmpStartDate,_tmpWarningNotifiedPeriodStart,_tmpOverNotifiedPeriodStart);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
