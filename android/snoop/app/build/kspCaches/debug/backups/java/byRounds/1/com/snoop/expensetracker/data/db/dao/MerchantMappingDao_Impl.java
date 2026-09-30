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
import com.snoop.expensetracker.data.db.entities.MerchantMappingEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class MerchantMappingDao_Impl implements MerchantMappingDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final EntityUpsertionAdapter<MerchantMappingEntity> __upsertionAdapterOfMerchantMappingEntity;

  public MerchantMappingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM merchant_mappings";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM merchant_mappings WHERE merchantKey = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfMerchantMappingEntity = new EntityUpsertionAdapter<MerchantMappingEntity>(new EntityInsertionAdapter<MerchantMappingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `merchant_mappings` (`merchantKey`,`categoryId`,`learnedAt`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MerchantMappingEntity entity) {
        statement.bindString(1, entity.getMerchantKey());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getLearnedAt());
      }
    }, new EntityDeletionOrUpdateAdapter<MerchantMappingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `merchant_mappings` SET `merchantKey` = ?,`categoryId` = ?,`learnedAt` = ? WHERE `merchantKey` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MerchantMappingEntity entity) {
        statement.bindString(1, entity.getMerchantKey());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getLearnedAt());
        statement.bindString(4, entity.getMerchantKey());
      }
    });
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
  public Object delete(final String merchantKey, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, merchantKey);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final MerchantMappingEntity mapping,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfMerchantMappingEntity.upsert(mapping);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<MerchantMappingEntity> mappings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfMerchantMappingEntity.upsert(mappings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByMerchantKey(final String merchantKey,
      final Continuation<? super MerchantMappingEntity> $completion) {
    final String _sql = "SELECT * FROM merchant_mappings WHERE merchantKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, merchantKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MerchantMappingEntity>() {
      @Override
      @Nullable
      public MerchantMappingEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMerchantKey = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantKey");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final MerchantMappingEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMerchantKey;
            _tmpMerchantKey = _cursor.getString(_cursorIndexOfMerchantKey);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpLearnedAt;
            _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            _result = new MerchantMappingEntity(_tmpMerchantKey,_tmpCategoryId,_tmpLearnedAt);
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
  public Flow<List<MerchantMappingEntity>> observeAll() {
    final String _sql = "SELECT * FROM merchant_mappings ORDER BY learnedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"merchant_mappings"}, new Callable<List<MerchantMappingEntity>>() {
      @Override
      @NonNull
      public List<MerchantMappingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMerchantKey = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantKey");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final List<MerchantMappingEntity> _result = new ArrayList<MerchantMappingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MerchantMappingEntity _item;
            final String _tmpMerchantKey;
            _tmpMerchantKey = _cursor.getString(_cursorIndexOfMerchantKey);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpLearnedAt;
            _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            _item = new MerchantMappingEntity(_tmpMerchantKey,_tmpCategoryId,_tmpLearnedAt);
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

  @Override
  public Object getAllOnce(final Continuation<? super List<MerchantMappingEntity>> $completion) {
    final String _sql = "SELECT * FROM merchant_mappings ORDER BY merchantKey";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MerchantMappingEntity>>() {
      @Override
      @NonNull
      public List<MerchantMappingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMerchantKey = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantKey");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final List<MerchantMappingEntity> _result = new ArrayList<MerchantMappingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MerchantMappingEntity _item;
            final String _tmpMerchantKey;
            _tmpMerchantKey = _cursor.getString(_cursorIndexOfMerchantKey);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpLearnedAt;
            _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            _item = new MerchantMappingEntity(_tmpMerchantKey,_tmpCategoryId,_tmpLearnedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
