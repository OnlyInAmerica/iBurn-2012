package com.trailbehind.android.iburn_2012.data;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PlayaContentProvider extends ContentProvider {

		// database
		private DBWrapper database;

		// Used for the UriMacher
		private static final int CAMPS = 1; 		// Query all
		private static final int CAMP_ID = 2; 		// Query single entry by id
		private static final int CAMP_SEARCH = 3; 	// Search title by string
		private static final int EVENTS = 4;
		private static final int EVENT_ID = 5;
		private static final int ART = 7;
		private static final int ART_ID = 8;

		private static final String AUTHORITY = "com.trailbehind.android.iburn_2012.data.playacontentprovider";

		private static final String CAMP_BASE_PATH = "camp";
		private static final String EVENT_BASE_PATH = "event";
		private static final String ART_BASE_PATH = "art";
		
		public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY + "/");
		
		public static final Uri CAMP_URI = AUTHORITY_URI.buildUpon().appendPath(CAMP_BASE_PATH).build();
		public static final Uri CAMP_SEARCH_URI = AUTHORITY_URI.buildUpon().appendPath(CAMP_BASE_PATH).appendPath("search").build();
		public static final Uri EVENT_URI = AUTHORITY_URI.buildUpon().appendPath(EVENT_BASE_PATH).build();
		public static final Uri ART_URI = AUTHORITY_URI.buildUpon().appendPath(ART_BASE_PATH).build();

		public static final String CAMP_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/camps";
		public static final String CAMP_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/camp";

		private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		static {
			sURIMatcher.addURI(AUTHORITY, CAMP_BASE_PATH, CAMPS);
			sURIMatcher.addURI(AUTHORITY, CAMP_BASE_PATH + "/#", CAMP_ID);
			sURIMatcher.addURI(AUTHORITY, CAMP_BASE_PATH + "/search/*", CAMP_SEARCH);
			sURIMatcher.addURI(AUTHORITY, EVENT_BASE_PATH, EVENTS);
			sURIMatcher.addURI(AUTHORITY, EVENT_BASE_PATH + "/#", EVENT_ID);
			sURIMatcher.addURI(AUTHORITY, ART_BASE_PATH, ART);
			sURIMatcher.addURI(AUTHORITY, ART_BASE_PATH + "/#", ART_ID);
		}

		@Override
		public boolean onCreate() {
			database = new DBWrapper(getContext());
			return false;
		}
		

		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {
			
			// Uisng SQLiteQueryBuilder instead of query() method
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

			// Check if the caller has requested a column which does not exists
			checkColumns(projection);

			// Set the table
			queryBuilder.setTables(DBWrapper.TABLE_NAME);

			int uriType = sURIMatcher.match(uri);
			switch (uriType) {
			case CAMPS:
				break;
			case CAMP_ID:
				// Adding the ID to the original query
				queryBuilder.appendWhere(CampTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case CAMP_SEARCH:
				queryBuilder.appendWhere(CampTable.COLUMN_NAME + " LIKE "
						+ "\"%" + uri.getLastPathSegment()+"%\"");
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
			}

			SQLiteDatabase db = database.getWritableDatabase();
			Cursor cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			// Make sure that potential listeners are getting notified
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}

		@Override
		public String getType(Uri uri) {
			return null;
		}

		@Override
		public Uri insert(Uri uri, ContentValues values) {
			int uriType = sURIMatcher.match(uri);
			SQLiteDatabase sqlDB = database.getWritableDatabase();
			long id = 0;
			switch (uriType) {
			case CAMPS:
				id = sqlDB.insert(DBWrapper.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(CAMP_BASE_PATH + "/" + id);
		}

		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
			int uriType = sURIMatcher.match(uri);
			SQLiteDatabase sqlDB = database.getWritableDatabase();
			int rowsDeleted = 0;
			switch (uriType) {
			case CAMPS:
				rowsDeleted = sqlDB.delete(DBWrapper.TABLE_NAME, selection,
						selectionArgs);
				break;
			case CAMP_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsDeleted = sqlDB.delete(DBWrapper.TABLE_NAME,
							CampTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsDeleted = sqlDB.delete(DBWrapper.TABLE_NAME,
							CampTable.COLUMN_ID + "=" + id 
							+ " and " + selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return rowsDeleted;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection,
				String[] selectionArgs) {

			int uriType = sURIMatcher.match(uri);
			SQLiteDatabase sqlDB = database.getWritableDatabase();
			int rowsUpdated = 0;
			switch (uriType) {
			case CAMPS:
				rowsUpdated = sqlDB.update(CampTable.TABLE_NAME, 
						values, 
						selection,
						selectionArgs);
				break;
			case CAMP_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(CampTable.TABLE_NAME, 
							values,
							CampTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(CampTable.TABLE_NAME, 
							values,
							CampTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return rowsUpdated;
		}

		private void checkColumns(String[] projection) {
			String[] available = { CampTable.COLUMN_ID, CampTable.COLUMN_NAME, CampTable.COLUMN_DESCRIPTION, CampTable.COLUMN_HOMETOWN, CampTable.COLUMN_URL,
			    	CampTable.COLUMN_YEAR, CampTable.COLUMN_CAMP_ID, CampTable.COLUMN_LATITUDE, CampTable.COLUMN_LONGITUDE, CampTable.COLUMN_LONGITUDE, CampTable.COLUMN_CONTACT };
			if (projection != null) {
				HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
				HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
				// Check if all columns which are requested are available
				if (!availableColumns.containsAll(requestedColumns)) {
					throw new IllegalArgumentException("Unknown columns in projection");
				}
			}
		}

}
