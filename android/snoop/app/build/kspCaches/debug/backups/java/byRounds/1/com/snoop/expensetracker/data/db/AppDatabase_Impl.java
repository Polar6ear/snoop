package com.snoop.expensetracker.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.snoop.expensetracker.data.db.dao.BalanceDao;
import com.snoop.expensetracker.data.db.dao.BalanceDao_Impl;
import com.snoop.expensetracker.data.db.dao.BudgetDao;
import com.snoop.expensetracker.data.db.dao.BudgetDao_Impl;
import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.CategoryDao_Impl;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao_Impl;
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao;
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao_Impl;
import com.snoop.expensetracker.data.db.dao.ParserVersionDao;
import com.snoop.expensetracker.data.db.dao.ParserVersionDao_Impl;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TransactionDao _transactionDao;

  private volatile CategoryDao _categoryDao;

  private volatile MerchantMappingDao _merchantMappingDao;

  private volatile BudgetDao _budgetDao;

  private volatile BalanceDao _balanceDao;

  private volatile ParserVersionDao _parserVersionDao;

  private volatile FallbackPendingDao _fallbackPendingDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(7) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amountMinor` INTEGER NOT NULL, `direction` TEXT NOT NULL, `merchantRaw` TEXT NOT NULL, `merchantResolved` TEXT, `categoryId` INTEGER, `sourceApp` TEXT NOT NULL, `bank` TEXT NOT NULL, `rawText` TEXT NOT NULL, `confidence` REAL NOT NULL, `status` TEXT NOT NULL, `dedupeKey` TEXT, `deletedAt` INTEGER, `createdAt` INTEGER NOT NULL, `note` TEXT, `suggestedCategory` TEXT, `suggestedCategoryConfidence` REAL NOT NULL, FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_categoryId` ON `transactions` (`categoryId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_status` ON `transactions` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_createdAt` ON `transactions` (`createdAt`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_merchantResolved` ON `transactions` (`merchantResolved`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_deletedAt` ON `transactions` (`deletedAt`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_transactions_dedupeKey` ON `transactions` (`dedupeKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon` TEXT NOT NULL, `color` TEXT NOT NULL, `isCustom` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `merchant_mappings` (`merchantKey` TEXT NOT NULL, `categoryId` INTEGER NOT NULL, `learnedAt` INTEGER NOT NULL, PRIMARY KEY(`merchantKey`), FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_merchant_mappings_categoryId` ON `merchant_mappings` (`categoryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER NOT NULL, `limitMinor` INTEGER NOT NULL, `period` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `warningNotifiedPeriodStart` INTEGER, `overNotifiedPeriodStart` INTEGER, FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_budgets_categoryId` ON `budgets` (`categoryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `parser_versions` (`bankKey` TEXT NOT NULL, `version` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`bankKey`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fallback_pending` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dedupeKey` TEXT NOT NULL, `packageName` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `attempts` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_fallback_pending_dedupeKey` ON `fallback_pending` (`dedupeKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `balance_anchor` (`id` INTEGER NOT NULL, `balanceMinor` INTEGER NOT NULL, `anchoredAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'afa1dd4ce8a5893cc01fe857c98a1f50')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `merchant_mappings`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `parser_versions`");
        db.execSQL("DROP TABLE IF EXISTS `fallback_pending`");
        db.execSQL("DROP TABLE IF EXISTS `balance_anchor`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(17);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amountMinor", new TableInfo.Column("amountMinor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("direction", new TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("merchantRaw", new TableInfo.Column("merchantRaw", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("merchantResolved", new TableInfo.Column("merchantResolved", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("sourceApp", new TableInfo.Column("sourceApp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("bank", new TableInfo.Column("bank", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("rawText", new TableInfo.Column("rawText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("dedupeKey", new TableInfo.Column("dedupeKey", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("deletedAt", new TableInfo.Column("deletedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("suggestedCategory", new TableInfo.Column("suggestedCategory", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("suggestedCategoryConfidence", new TableInfo.Column("suggestedCategoryConfidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("categories", "SET NULL", "NO ACTION", Arrays.asList("categoryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(6);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_categoryId", false, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_merchantResolved", false, Arrays.asList("merchantResolved"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_deletedAt", false, Arrays.asList("deletedAt"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_dedupeKey", true, Arrays.asList("dedupeKey"), Arrays.asList("ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.snoop.expensetracker.data.db.entities.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(5);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("isCustom", new TableInfo.Column("isCustom", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.snoop.expensetracker.data.db.entities.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsMerchantMappings = new HashMap<String, TableInfo.Column>(3);
        _columnsMerchantMappings.put("merchantKey", new TableInfo.Column("merchantKey", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantMappings.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantMappings.put("learnedAt", new TableInfo.Column("learnedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMerchantMappings = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMerchantMappings.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION", Arrays.asList("categoryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMerchantMappings = new HashSet<TableInfo.Index>(1);
        _indicesMerchantMappings.add(new TableInfo.Index("index_merchant_mappings_categoryId", false, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        final TableInfo _infoMerchantMappings = new TableInfo("merchant_mappings", _columnsMerchantMappings, _foreignKeysMerchantMappings, _indicesMerchantMappings);
        final TableInfo _existingMerchantMappings = TableInfo.read(db, "merchant_mappings");
        if (!_infoMerchantMappings.equals(_existingMerchantMappings)) {
          return new RoomOpenHelper.ValidationResult(false, "merchant_mappings(com.snoop.expensetracker.data.db.entities.MerchantMappingEntity).\n"
                  + " Expected:\n" + _infoMerchantMappings + "\n"
                  + " Found:\n" + _existingMerchantMappings);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(7);
        _columnsBudgets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("limitMinor", new TableInfo.Column("limitMinor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("period", new TableInfo.Column("period", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("warningNotifiedPeriodStart", new TableInfo.Column("warningNotifiedPeriodStart", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("overNotifiedPeriodStart", new TableInfo.Column("overNotifiedPeriodStart", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysBudgets.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION", Arrays.asList("categoryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(1);
        _indicesBudgets.add(new TableInfo.Index("index_budgets_categoryId", true, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.snoop.expensetracker.data.db.entities.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsParserVersions = new HashMap<String, TableInfo.Column>(3);
        _columnsParserVersions.put("bankKey", new TableInfo.Column("bankKey", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParserVersions.put("version", new TableInfo.Column("version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParserVersions.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysParserVersions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesParserVersions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoParserVersions = new TableInfo("parser_versions", _columnsParserVersions, _foreignKeysParserVersions, _indicesParserVersions);
        final TableInfo _existingParserVersions = TableInfo.read(db, "parser_versions");
        if (!_infoParserVersions.equals(_existingParserVersions)) {
          return new RoomOpenHelper.ValidationResult(false, "parser_versions(com.snoop.expensetracker.data.db.entities.ParserVersionEntity).\n"
                  + " Expected:\n" + _infoParserVersions + "\n"
                  + " Found:\n" + _existingParserVersions);
        }
        final HashMap<String, TableInfo.Column> _columnsFallbackPending = new HashMap<String, TableInfo.Column>(6);
        _columnsFallbackPending.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFallbackPending.put("dedupeKey", new TableInfo.Column("dedupeKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFallbackPending.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFallbackPending.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFallbackPending.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFallbackPending.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFallbackPending = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFallbackPending = new HashSet<TableInfo.Index>(1);
        _indicesFallbackPending.add(new TableInfo.Index("index_fallback_pending_dedupeKey", true, Arrays.asList("dedupeKey"), Arrays.asList("ASC")));
        final TableInfo _infoFallbackPending = new TableInfo("fallback_pending", _columnsFallbackPending, _foreignKeysFallbackPending, _indicesFallbackPending);
        final TableInfo _existingFallbackPending = TableInfo.read(db, "fallback_pending");
        if (!_infoFallbackPending.equals(_existingFallbackPending)) {
          return new RoomOpenHelper.ValidationResult(false, "fallback_pending(com.snoop.expensetracker.data.db.entities.FallbackPendingEntity).\n"
                  + " Expected:\n" + _infoFallbackPending + "\n"
                  + " Found:\n" + _existingFallbackPending);
        }
        final HashMap<String, TableInfo.Column> _columnsBalanceAnchor = new HashMap<String, TableInfo.Column>(3);
        _columnsBalanceAnchor.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalanceAnchor.put("balanceMinor", new TableInfo.Column("balanceMinor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalanceAnchor.put("anchoredAt", new TableInfo.Column("anchoredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBalanceAnchor = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBalanceAnchor = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBalanceAnchor = new TableInfo("balance_anchor", _columnsBalanceAnchor, _foreignKeysBalanceAnchor, _indicesBalanceAnchor);
        final TableInfo _existingBalanceAnchor = TableInfo.read(db, "balance_anchor");
        if (!_infoBalanceAnchor.equals(_existingBalanceAnchor)) {
          return new RoomOpenHelper.ValidationResult(false, "balance_anchor(com.snoop.expensetracker.data.db.entities.BalanceAnchorEntity).\n"
                  + " Expected:\n" + _infoBalanceAnchor + "\n"
                  + " Found:\n" + _existingBalanceAnchor);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "afa1dd4ce8a5893cc01fe857c98a1f50", "83a48bf781869ba99825b83747730f8f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "transactions","categories","merchant_mappings","budgets","parser_versions","fallback_pending","balance_anchor");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `merchant_mappings`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `parser_versions`");
      _db.execSQL("DELETE FROM `fallback_pending`");
      _db.execSQL("DELETE FROM `balance_anchor`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MerchantMappingDao.class, MerchantMappingDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BalanceDao.class, BalanceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ParserVersionDao.class, ParserVersionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FallbackPendingDao.class, FallbackPendingDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public MerchantMappingDao merchantMappingDao() {
    if (_merchantMappingDao != null) {
      return _merchantMappingDao;
    } else {
      synchronized(this) {
        if(_merchantMappingDao == null) {
          _merchantMappingDao = new MerchantMappingDao_Impl(this);
        }
        return _merchantMappingDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public BalanceDao balanceDao() {
    if (_balanceDao != null) {
      return _balanceDao;
    } else {
      synchronized(this) {
        if(_balanceDao == null) {
          _balanceDao = new BalanceDao_Impl(this);
        }
        return _balanceDao;
      }
    }
  }

  @Override
  public ParserVersionDao parserVersionDao() {
    if (_parserVersionDao != null) {
      return _parserVersionDao;
    } else {
      synchronized(this) {
        if(_parserVersionDao == null) {
          _parserVersionDao = new ParserVersionDao_Impl(this);
        }
        return _parserVersionDao;
      }
    }
  }

  @Override
  public FallbackPendingDao fallbackPendingDao() {
    if (_fallbackPendingDao != null) {
      return _fallbackPendingDao;
    } else {
      synchronized(this) {
        if(_fallbackPendingDao == null) {
          _fallbackPendingDao = new FallbackPendingDao_Impl(this);
        }
        return _fallbackPendingDao;
      }
    }
  }
}
