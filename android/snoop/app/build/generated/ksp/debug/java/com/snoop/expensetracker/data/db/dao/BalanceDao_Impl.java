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
import com.snoop.expensetracker.data.db.entities.BalanceAnchorEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BalanceDao_Impl implements BalanceDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  private final EntityUpsertionAdapter<BalanceAnchorEntity> __upsertionAdapterOfBalanceAnchorEntity;

  public BalanceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM balance_anchor";
        return _query;
      }
    };
    this.__upsertionAdapterOfBalanceAnchorEntity = new EntityUpsertionAdapter<BalanceAnchorEntity>(new EntityInsertionAdapter<BalanceAnchorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `balance_anchor` (`id`,`balanceMinor`,`anchoredAt`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BalanceAnchorEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBalanceMinor());
        statement.bindLong(3, entity.getAnchoredAt());
      }
    }, new EntityDeletionOrUpdateAdapter<BalanceAnchorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `balance_anchor` SET `id` = ?,`balanceMinor` = ?,`anchoredAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BalanceAnchorEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBalanceMinor());
        statement.bindLong(3, entity.getAnchoredAt());
        statement.bindLong(4, entity.getId());
      }
    });
  }

  @Override
  public Object clear(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClear.acquire();
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
          __preparedStmtOfClear.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAnchor(final BalanceAnchorEntity anchor,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfBalanceAnchorEntity.upsert(anchor);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<BalanceAnchorEntity> observeAnchor() {
    final String _sql = "SELECT * FROM balance_anchor WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"balance_anchor"}, new Callable<BalanceAnchorEntity>() {
      @Override
      @Nullable
      public BalanceAnchorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "balanceMinor");
          final int _cursorIndexOfAnchoredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "anchoredAt");
          final BalanceAnchorEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpBalanceMinor;
            _tmpBalanceMinor = _cursor.getLong(_cursorIndexOfBalanceMinor);
            final long _tmpAnchoredAt;
            _tmpAnchoredAt = _cursor.getLong(_cursorIndexOfAnchoredAt);
            _result = new BalanceAnchorEntity(_tmpId,_tmpBalanceMinor,_tmpAnchoredAt);
          } else {
            _result = null;
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

  @Override
  public Object getAnchorOnce(final Continuation<? super BalanceAnchorEntity> $completion) {
    final String _sql = "SELECT * FROM balance_anchor WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BalanceAnchorEntity>() {
      @Override
      @Nullable
      public BalanceAnchorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBalanceMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "balanceMinor");
          final int _cursorIndexOfAnchoredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "anchoredAt");
          final BalanceAnchorEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpBalanceMinor;
            _tmpBalanceMinor = _cursor.getLong(_cursorIndexOfBalanceMinor);
            final long _tmpAnchoredAt;
            _tmpAnchoredAt = _cursor.getLong(_cursorIndexOfAnchoredAt);
            _result = new BalanceAnchorEntity(_tmpId,_tmpBalanceMinor,_tmpAnchoredAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
