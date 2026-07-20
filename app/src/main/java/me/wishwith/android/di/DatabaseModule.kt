package me.wishwith.android.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.wishwith.android.data.local.AppDatabase
import me.wishwith.android.data.local.dao.BookmarkDao
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.dao.ShareDao
import me.wishwith.android.data.local.dao.UserDao
import me.wishwith.android.data.local.dao.WishlistDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "wishwithme.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideWishlistDao(db: AppDatabase): WishlistDao = db.wishlistDao()

    @Provides
    fun provideItemDao(db: AppDatabase): ItemDao = db.itemDao()

    @Provides
    fun provideMarkDao(db: AppDatabase): MarkDao = db.markDao()

    @Provides
    fun provideShareDao(db: AppDatabase): ShareDao = db.shareDao()

    @Provides
    fun provideBookmarkDao(db: AppDatabase): BookmarkDao = db.bookmarkDao()
}
