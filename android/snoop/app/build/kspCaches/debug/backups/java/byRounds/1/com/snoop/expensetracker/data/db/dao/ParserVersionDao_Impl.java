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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.snoop.expensetracker.data.db.entities.ParserVersionEntity;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ParserVersionDao_Impl implements ParserVersionDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<ParserVersionEntity> __upsertionAdapterOfParserVersionEntity;

  public ParserVersionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfParserVersionEntity = new EntityUpsertionAdapter<ParserVersionEntity>(new EntityInsertionAdapter<ParserVersionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `parser_versions` (`bankKey`,`version`,`lastUpdated`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ParserVersionEntity entity) {
        statement.bindString(1, entity.getBankKey());
        statement.bindLong(2, entity.getVersion());
        statement.bindLong(3, entity.getLastUpdated());
      }
    }, new EntityDeletionOrUpdateAdapter<ParserVersionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `parser_versions` SET `bankKey` = ?,`version` = ?,`lastUpdated` = ? WHERE `bankKey` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ParserVersionEntity entity) {
        statement.bindString(1, entity.getBankKey());
        statement.bindLong(2, entity.getVersion());
        statement.bindLong(3, entity.getLastUpdated());
        statement.bindString(4, entity.getBankKey());
      }
    });
  }

  @Override
  public Object upsert(final ParserVersionEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfParserVersionEntity.upsert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByBankKey(final String bankKey,
      final Continuation<? super ParserVersionEntity> $completion) {
    final String _sql = "SELECT * FROM parser_versions WHERE bankKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, bankKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ParserVersionEntity>() {
      @Override
      @Nullable
      public ParserVersionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBankKey = CursorUtil.getColumnIndexOrThrow(_cursor, "bankKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final ParserVersionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpBankKey;
            _tmpBankKey = _cursor.getString(_cursorIndexOfBankKey);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new ParserVersionEntity(_tmpBankKey,_tmpVersion,_tmpLastUpdated);
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
  public Object getAll(final Continuation<? super List<ParserVersionEntity>> $completion) {
    final String _sql = "SELECT * FROM parser_versions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ParserVersionEntity>>() {
      @Override
      @NonNull
      public List<ParserVersionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBankKey = CursorUtil.getColumnIndexOrThrow(_cursor, "bankKey");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final List<ParserVersionEntity> _result = new ArrayList<ParserVersionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ParserVersionEntity _item;
            final String _tmpBankKey;
            _tmpBankKey = _cursor.getString(_cursorIndexOfBankKey);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _item = new ParserVersionEntity(_tmpBankKey,_tmpVersion,_tmpLastUpdated);
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
