package com.snoop.expensetracker.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.snoop.expensetracker.data.db.Converters;
import com.snoop.expensetracker.data.db.entities.TransactionDirection;
import com.snoop.expensetracker.data.db.entities.TransactionEntity;
import com.snoop.expensetracker.data.db.entities.TransactionStatus;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity_1;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __updateAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfRestore;

  private final SharedSQLiteStatement __preparedStmtOfUpdateUserFields;

  private final SharedSQLiteStatement __preparedStmtOfConfirm;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `transactions` (`id`,`amountMinor`,`direction`,`merchantRaw`,`merchantResolved`,`categoryId`,`sourceApp`,`bank`,`rawText`,`confidence`,`status`,`dedupeKey`,`deletedAt`,`createdAt`,`note`,`suggestedCategory`,`suggestedCategoryConfidence`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        final String _tmp = __converters.fromDirection(entity.getDirection());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getMerchantRaw());
        if (entity.getMerchantResolved() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMerchantResolved());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCategoryId());
        }
        statement.bindString(7, entity.getSourceApp());
        statement.bindString(8, entity.getBank());
        statement.bindString(9, entity.getRawText());
        statement.bindDouble(10, entity.getConfidence());
        final String _tmp_1 = __converters.fromStatus(entity.getStatus());
        statement.bindString(11, _tmp_1);
        if (entity.getDedupeKey() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDedupeKey());
        }
        if (entity.getDeletedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getDeletedAt());
        }
        statement.bindLong(14, entity.getCreatedAt());
        if (entity.getNote() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getNote());
        }
        if (entity.getSuggestedCategory() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getSuggestedCategory());
        }
        statement.bindDouble(17, entity.getSuggestedCategoryConfidence());
      }
    };
    this.__insertionAdapterOfTransactionEntity_1 = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`amountMinor`,`direction`,`merchantRaw`,`merchantResolved`,`categoryId`,`sourceApp`,`bank`,`rawText`,`confidence`,`status`,`dedupeKey`,`deletedAt`,`createdAt`,`note`,`suggestedCategory`,`suggestedCategoryConfidence`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        final String _tmp = __converters.fromDirection(entity.getDirection());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getMerchantRaw());
        if (entity.getMerchantResolved() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMerchantResolved());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCategoryId());
        }
        statement.bindString(7, entity.getSourceApp());
        statement.bindString(8, entity.getBank());
        statement.bindString(9, entity.getRawText());
        statement.bindDouble(10, entity.getConfidence());
        final String _tmp_1 = __converters.fromStatus(entity.getStatus());
        statement.bindString(11, _tmp_1);
        if (entity.getDedupeKey() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDedupeKey());
        }
        if (entity.getDeletedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getDeletedAt());
        }
        statement.bindLong(14, entity.getCreatedAt());
        if (entity.getNote() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getNote());
        }
        if (entity.getSuggestedCategory() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getSuggestedCategory());
        }
        statement.bindDouble(17, entity.getSuggestedCategoryConfidence());
      }
    };
    this.__updateAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`amountMinor` = ?,`direction` = ?,`merchantRaw` = ?,`merchantResolved` = ?,`categoryId` = ?,`sourceApp` = ?,`bank` = ?,`rawText` = ?,`confidence` = ?,`status` = ?,`dedupeKey` = ?,`deletedAt` = ?,`createdAt` = ?,`note` = ?,`suggestedCategory` = ?,`suggestedCategoryConfidence` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAmountMinor());
        final String _tmp = __converters.fromDirection(entity.getDirection());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getMerchantRaw());
        if (entity.getMerchantResolved() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMerchantResolved());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCategoryId());
        }
        statement.bindString(7, entity.getSourceApp());
        statement.bindString(8, entity.getBank());
        statement.bindString(9, entity.getRawText());
        statement.bindDouble(10, entity.getConfidence());
        final String _tmp_1 = __converters.fromStatus(entity.getStatus());
        statement.bindString(11, _tmp_1);
        if (entity.getDedupeKey() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDedupeKey());
        }
        if (entity.getDeletedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getDeletedAt());
        }
        statement.bindLong(14, entity.getCreatedAt());
        if (entity.getNote() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getNote());
        }
        if (entity.getSuggestedCategory() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getSuggestedCategory());
        }
        statement.bindDouble(17, entity.getSuggestedCategoryConfidence());
        statement.bindLong(18, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET deletedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET deletedAt = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateUserFields = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE transactions SET amountMinor = ?, direction = ?,\n"
                + "        merchantRaw = ?, merchantResolved = ?, categoryId = ?, note = ?, suggestedCategory = NULL, suggestedCategoryConfidence = 0\n"
                + "        WHERE id = ? AND deletedAt IS NULL\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfConfirm = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE transactions\n"
                + "        SET categoryId = ?, merchantResolved = ?, status = 'CONFIRMED', suggestedCategory = NULL, suggestedCategoryConfidence = 0\n"
                + "        WHERE id = ?\n"
                + "        ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TransactionEntity transaction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransactionEntity.insertAndReturnId(transaction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<TransactionEntity> transactions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionEntity_1.insert(transactions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionEntity.handle(transaction);
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
  public Object softDelete(final long id, final long at,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, at);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object restore(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUserFields(final long id, final long amountMinor,
      final TransactionDirection direction, final String merchant, final long categoryId,
      final String note, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateUserFields.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, amountMinor);
        _argIndex = 2;
        final String _tmp = __converters.fromDirection(direction);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 3;
        _stmt.bindString(_argIndex, merchant);
        _argIndex = 4;
        _stmt.bindString(_argIndex, merchant);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 6;
        if (note == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, note);
        }
        _argIndex = 7;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdateUserFields.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object confirm(final long id, final long categoryId, final String merchantResolved,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfConfirm.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, merchantResolved);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfConfirm.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllOnce(final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT * FROM transactions ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_1);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _item = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Object findIdByDedupeKey(final String dedupeKey,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT id FROM transactions WHERE dedupeKey = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dedupeKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getLong(0);
            }
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
  public Flow<List<TransactionEntity>> observeByStatus(final TransactionStatus status) {
    final String _sql = "SELECT * FROM transactions WHERE status = ? AND deletedAt IS NULL ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromStatus(status);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp_1);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_2);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _item = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Flow<List<TransactionEntity>> observeAll() {
    final String _sql = "SELECT * FROM transactions WHERE deletedAt IS NULL ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_1);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _item = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Flow<List<TransactionEntity>> observeDeleted() {
    final String _sql = "SELECT * FROM transactions WHERE deletedAt IS NOT NULL ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_1);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _item = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Flow<TransactionEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_1);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _result = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Flow<List<TransactionEntity>> observeFiltered(final TransactionDirection direction,
      final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM transactions\n"
            + "        WHERE deletedAt IS NULL\n"
            + "          AND (? IS NULL OR direction = ?)\n"
            + "          AND (? = '' OR merchantRaw LIKE '%' || ? || '%' OR merchantResolved LIKE '%' || ? || '%')\n"
            + "        ORDER BY createdAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 5);
    int _argIndex = 1;
    final String _tmp;
    if (direction == null) {
      _tmp = null;
    } else {
      _tmp = __converters.fromDirection(direction);
    }
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1;
    if (direction == null) {
      _tmp_1 = null;
    } else {
      _tmp_1 = __converters.fromDirection(direction);
    }
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    _argIndex = 5;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp_2);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_3);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _item = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Flow<Long> observeExpenseSince(final long start) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amountMinor),0) FROM transactions\n"
            + "        WHERE deletedAt IS NULL AND status = 'CONFIRMED' AND direction = 'DEBIT' AND createdAt >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Flow<Long> observeIncomeSince(final long start) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amountMinor),0) FROM transactions\n"
            + "        WHERE deletedAt IS NULL AND status = 'CONFIRMED' AND direction = 'CREDIT' AND createdAt >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Flow<Long> observeExpenseBetween(final long start, final long end) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amountMinor),0) FROM transactions\n"
            + "        WHERE deletedAt IS NULL AND status = 'CONFIRMED' AND direction = 'DEBIT' AND createdAt >= ? AND createdAt < ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Flow<List<TransactionEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM transactions WHERE deletedAt IS NULL ORDER BY createdAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_1);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _item = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Flow<Long> observeNetChangeSince(final long since) {
    final String _sql = "\n"
            + "        SELECT COALESCE(\n"
            + "            SUM(CASE\n"
            + "                WHEN direction = 'CREDIT' AND status = 'CONFIRMED' THEN amountMinor\n"
            + "                WHEN direction = 'DEBIT' AND status = 'CONFIRMED' THEN -amountMinor\n"
            + "                ELSE 0\n"
            + "            END),\n"
            + "            0\n"
            + "        )\n"
            + "        FROM transactions\n"
            + "        WHERE deletedAt IS NULL AND createdAt >= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Object getById(final long id, final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmountMinor = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMinor");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantRaw = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantRaw");
          final int _cursorIndexOfMerchantResolved = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantResolved");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfSourceApp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceApp");
          final int _cursorIndexOfBank = CursorUtil.getColumnIndexOrThrow(_cursor, "bank");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDedupeKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dedupeKey");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSuggestedCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategory");
          final int _cursorIndexOfSuggestedCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryConfidence");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAmountMinor;
            _tmpAmountMinor = _cursor.getLong(_cursorIndexOfAmountMinor);
            final TransactionDirection _tmpDirection;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDirection);
            _tmpDirection = __converters.toDirection(_tmp);
            final String _tmpMerchantRaw;
            _tmpMerchantRaw = _cursor.getString(_cursorIndexOfMerchantRaw);
            final String _tmpMerchantResolved;
            if (_cursor.isNull(_cursorIndexOfMerchantResolved)) {
              _tmpMerchantResolved = null;
            } else {
              _tmpMerchantResolved = _cursor.getString(_cursorIndexOfMerchantResolved);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpSourceApp;
            _tmpSourceApp = _cursor.getString(_cursorIndexOfSourceApp);
            final String _tmpBank;
            _tmpBank = _cursor.getString(_cursorIndexOfBank);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final TransactionStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toStatus(_tmp_1);
            final String _tmpDedupeKey;
            if (_cursor.isNull(_cursorIndexOfDedupeKey)) {
              _tmpDedupeKey = null;
            } else {
              _tmpDedupeKey = _cursor.getString(_cursorIndexOfDedupeKey);
            }
            final Long _tmpDeletedAt;
            if (_cursor.isNull(_cursorIndexOfDeletedAt)) {
              _tmpDeletedAt = null;
            } else {
              _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSuggestedCategory;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategory)) {
              _tmpSuggestedCategory = null;
            } else {
              _tmpSuggestedCategory = _cursor.getString(_cursorIndexOfSuggestedCategory);
            }
            final float _tmpSuggestedCategoryConfidence;
            _tmpSuggestedCategoryConfidence = _cursor.getFloat(_cursorIndexOfSuggestedCategoryConfidence);
            _result = new TransactionEntity(_tmpId,_tmpAmountMinor,_tmpDirection,_tmpMerchantRaw,_tmpMerchantResolved,_tmpCategoryId,_tmpSourceApp,_tmpBank,_tmpRawText,_tmpConfidence,_tmpStatus,_tmpDedupeKey,_tmpDeletedAt,_tmpCreatedAt,_tmpNote,_tmpSuggestedCategory,_tmpSuggestedCategoryConfidence);
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
  public Object sumDebitForCategorySince(final long categoryId, final long periodStart,
      final Continuation<? super Long> $completion) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amountMinor), 0) FROM transactions\n"
            + "        WHERE categoryId = ?\n"
            + "          AND direction = 'DEBIT'\n"
            + "          AND status = 'CONFIRMED'\n"
            + "          AND createdAt >= ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, periodStart);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Flow<Integer> observePendingCount() {
    final String _sql = "SELECT COUNT(*) FROM transactions WHERE status = 'PENDING' AND deletedAt IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Flow<List<CategorySpend>> observeSpendByCategorySince(final long periodStart) {
    final String _sql = "\n"
            + "        SELECT categoryId, COALESCE(SUM(amountMinor), 0) AS spentMinor FROM transactions\n"
            + "        WHERE direction = 'DEBIT' AND status = 'CONFIRMED' AND createdAt >= ? AND categoryId IS NOT NULL\n"
            + "        GROUP BY categoryId\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, periodStart);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<CategorySpend>>() {
      @Override
      @NonNull
      public List<CategorySpend> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfSpentMinor = 1;
          final List<CategorySpend> _result = new ArrayList<CategorySpend>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategorySpend _item;
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpSpentMinor;
            _tmpSpentMinor = _cursor.getLong(_cursorIndexOfSpentMinor);
            _item = new CategorySpend(_tmpCategoryId,_tmpSpentMinor);
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
