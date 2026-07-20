package me.wishwith.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.wishwith.android.data.local.dao.BookmarkDao
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.dao.ShareDao
import me.wishwith.android.data.local.dao.UserDao
import me.wishwith.android.data.local.dao.WishlistDao
import me.wishwith.android.data.local.entity.BookmarkEntity
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.data.local.entity.MarkEntity
import me.wishwith.android.data.local.entity.ShareEntity
import me.wishwith.android.data.local.entity.UserEntity
import me.wishwith.android.data.local.entity.WishlistEntity

@Database(
    entities = [
        UserEntity::class,
        WishlistEntity::class,
        ItemEntity::class,
        MarkEntity::class,
        ShareEntity::class,
        BookmarkEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun itemDao(): ItemDao
    abstract fun markDao(): MarkDao
    abstract fun shareDao(): ShareDao
    abstract fun bookmarkDao(): BookmarkDao
}
