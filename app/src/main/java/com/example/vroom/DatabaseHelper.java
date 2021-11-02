package com.example.vroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "VRoomDB.db";

    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID = "ID";
    private static final String COLUMN_USER_USERNAME = "Username";
    private static final String COLUMN_USER_PASSWORD = "Password";

    private static final String TABLE_ROOM = "Room";
    private static final String COLUMN_ROOM_ID = "ID";
    private static final String COLUMN_ROOM_PASSWORD = "Password";
    private static final String COLUMN_ROOM_TOPIC = "Topic";
    private static final String COLUMN_ROOM_USERNAME = "Username";

    private static final String TABLE_MESSAGE = "Message";
    private static final String COLUMN_MESSAGE_ID = "ID";
    private static final String COLUMN_MESSAGE_ROOM_ID = "RoomID";
    private static final String COLUMN_MESSAGE_MESSAGE = "Message";
    private static final String COLUMN_MESSAGE_PICTURE = "Picture";
    private static final String COLUMN_MESSAGE_USERNAME = "Username";
    private static final String COLUMN_MESSAGE_DATE = "Date";

    private static final String TABLE_UNLOCKED_ROOMS = "UnlockedRooms";
    private static final String COLUMN_UNLOCKED_ROOMS_ID = "ID";
    private static final String COLUMN_UNLOCKED_ROOMS_USERNAME = "Username";
    private static final String COLUMN_UNLOCKED_ROOMS_ROOMID = "RoomID";

    private String CREATE_ROOM_TABLE = "CREATE TABLE " + TABLE_ROOM + "(" + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ROOM_PASSWORD + " TEXT," +
            COLUMN_ROOM_TOPIC + " TEXT," + COLUMN_ROOM_USERNAME + " TEXT)";
    private String DROP_ROOM_TABLE = "DROP TABLE IF EXISTS " + TABLE_ROOM;

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_USERNAME + " TEXT," +
            COLUMN_USER_PASSWORD + " TEXT)";
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    private String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGE + "(" + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_MESSAGE_MESSAGE +
            " TEXT," + COLUMN_MESSAGE_PICTURE + " BLOB," + COLUMN_MESSAGE_USERNAME + " TEXT," + COLUMN_MESSAGE_ROOM_ID + " INTEGER," + COLUMN_MESSAGE_DATE + " TEXT)";
    private String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS " + TABLE_MESSAGE;

    private String CREATE_UNLOCKED_ROOMS_TABLE = "CREATE TABLE " + TABLE_UNLOCKED_ROOMS + "(" + COLUMN_UNLOCKED_ROOMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_UNLOCKED_ROOMS_USERNAME + " TEXT,"
            + COLUMN_UNLOCKED_ROOMS_ROOMID + " INTEGER)";
    private String DROP_UNLOCKED_ROOMS_TABLE = "DROP TABLE IF EXISTS " + TABLE_UNLOCKED_ROOMS;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_ROOM_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_UNLOCKED_ROOMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_ROOM_TABLE);
        db.execSQL(DROP_MESSAGE_TABLE);
        db.execSQL(DROP_UNLOCKED_ROOMS_TABLE);
        onCreate(db);
    }

    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void addUnlockedRoom(String username, int roomID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNLOCKED_ROOMS_USERNAME, username);
        values.put(COLUMN_UNLOCKED_ROOMS_ROOMID, roomID);
        db.insert(TABLE_UNLOCKED_ROOMS, null, values);
        db.close();
    }

    public boolean checkRoom(String username, int roomID){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_UNLOCKED_ROOMS_ID,
                COLUMN_UNLOCKED_ROOMS_USERNAME,
                COLUMN_UNLOCKED_ROOMS_ROOMID
        };
        String selection = COLUMN_UNLOCKED_ROOMS_USERNAME + " = ? AND " + COLUMN_UNLOCKED_ROOMS_ROOMID + " = ?";
        String[] selectionArgs = {username, String.valueOf(roomID)};

        Cursor cursor = db.query(TABLE_UNLOCKED_ROOMS, columns, selection, selectionArgs,null,null,null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if(cursorCount > 0){
            return true;
        }
        return false;

    }

    public void removeUnlockedRoom(String username, int roomID){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_UNLOCKED_ROOMS_USERNAME + " = ? AND " + COLUMN_UNLOCKED_ROOMS_ROOMID + " = ?";
        String[] selectionArgs = {username, String.valueOf(roomID)};
        db.delete(TABLE_UNLOCKED_ROOMS, selection, selectionArgs);
        db.close();
    }

    public String[] getMyRoomsIDs(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_UNLOCKED_ROOMS_ROOMID
        };
        String selection = COLUMN_UNLOCKED_ROOMS_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_UNLOCKED_ROOMS, columns, selection, selectionArgs, null, null, null);
        ArrayList<String> roomIDs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String roomID = cursor.getString(cursor.getColumnIndex(COLUMN_UNLOCKED_ROOMS_ROOMID));
                roomIDs.add(roomID);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        String arg = roomIDs.toString().replace("[", "");
        arg = arg.replace("]", "");
        String[] roomIds = arg.split(", ");
        return roomIds;
    }

    public void createRoom(Room room){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_TOPIC, room.getTopic());
        values.put(COLUMN_ROOM_PASSWORD, room.getPassword());
        values.put(COLUMN_ROOM_USERNAME, room.getUsername());
        db.insert(TABLE_ROOM,null, values);
        db.close();
    }

    public void createMessage(Message message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_MESSAGE, message.getMessage());
        values.put(COLUMN_MESSAGE_ROOM_ID, message.getRoomID());
        values.put(COLUMN_MESSAGE_USERNAME, message.getUsername());
        values.put(COLUMN_MESSAGE_DATE, message.getDate().toString());
        db.insert(TABLE_MESSAGE, null, values);
        db.close();
    }

    public boolean checkUser(String username){
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USER,columns,selection,selectionArgs,null,null,null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if(cursorCount > 0){
            return true;
        }
        return false;
    }

    public boolean checkUser(String username, String password){
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_USERNAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USER,columns,selection,selectionArgs,null,null,null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if(cursorCount > 0){
            return true;
        }

        return false;
    }

    public ArrayList<Room> getRooms(String username, String[] excludeIDs) {

        String[] columns = {
                COLUMN_ROOM_ID,
                COLUMN_ROOM_TOPIC,
                COLUMN_ROOM_PASSWORD,
                COLUMN_ROOM_USERNAME
        };

        String sortOrder = COLUMN_ROOM_ID + " DESC";
        ArrayList<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = COLUMN_ROOM_ID + " NOT IN (?,";

        if(excludeIDs.length == 0){
            return rooms;
        }
        for (int i = 0; i < excludeIDs.length - 1; i++){
            if(i == excludeIDs.length - 2){
                select += "?)";
                break;
            }
            select += "?,";
        }
        if(excludeIDs.length == 1){
            select = COLUMN_ROOM_ID + " NOT IN (?)";
        }
//        String selection = COLUMN_ROOM_ID + " NOT IN(?)";
        String[] selectionArgs = excludeIDs;
            Cursor cursor = db.query(TABLE_ROOM, //Table to query
                    columns,    //columns to return
                    select,        //columns for the WHERE clause
                    selectionArgs,        //The values for the WHERE clause
                    null,       //group the rows
                    null,       //filter by row groups
                    null); //The sort order

            if (cursor.moveToFirst()) {
                do {
                    Room room = new Room();
                    room.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_ID))));
                    room.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_USERNAME)));
                    room.setTopic(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_TOPIC)));
                    room.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_PASSWORD)));
                    rooms.add(room);
                } while (cursor.moveToNext());
            }
            cursor.close();

        db.close();
        return rooms;
    }

    public ArrayList<Room> getMyRooms(String[] roomIDS) {
        String[] columns = {
                COLUMN_ROOM_ID,
                COLUMN_ROOM_TOPIC,
                COLUMN_ROOM_PASSWORD,
                COLUMN_ROOM_USERNAME
        };

        String sortOrder =
                COLUMN_ROOM_ID + " DESC";
        ArrayList<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = COLUMN_ROOM_ID + " IN (?,";

        if(roomIDS.length == 0){
            return rooms;
        }

        for (int i = 0; i < roomIDS.length - 1; i++){
            if(i == roomIDS.length - 2){
                select += "?)";
                break;
            }
            select += "?,";
        }
        if(roomIDS.length == 1){
            select = COLUMN_ROOM_ID + " IN(?)";
        }
        String selection = COLUMN_ROOM_ID + " IN(?)";
        String[] selectionArgs = roomIDS;

            Cursor cursor = db.query(TABLE_ROOM, //Table to query
                    columns,    //columns to return
                    select,        //columns for the WHERE clause
                    selectionArgs,        //The values for the WHERE clause
                    null,       //group the rows
                    null,       //filter by row groups
                    sortOrder); //The sort order

            if (cursor.moveToFirst()) {
                do {
                    Room room = new Room();
                    room.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_ID))));
                    room.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_USERNAME)));
                    room.setTopic(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_TOPIC)));
                    room.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_PASSWORD)));
                    rooms.add(room);
                } while (cursor.moveToNext());
            }
            cursor.close();


        db.close();
        return rooms;
    }

    public String getRoomTopic(int roomID){
        String[] columns = {
                COLUMN_ROOM_TOPIC
        };

        String sortOrder = COLUMN_ROOM_ID + " DESC";

        String roomTopic = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ROOM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(roomID)};

        Cursor cursor = db.query(TABLE_ROOM,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        if (cursor.moveToFirst()) {
            do {
                roomTopic = cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_TOPIC));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roomTopic;
    }

    public Room getRoomByUsername(String username){
        String[] columns = {
                COLUMN_ROOM_ID,
                COLUMN_ROOM_TOPIC,
                COLUMN_ROOM_PASSWORD,
                COLUMN_ROOM_USERNAME
        };

        String sortOrder =
                COLUMN_ROOM_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_ROOM_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Room room = new Room();
        Cursor cursor = db.query(TABLE_ROOM, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        if (cursor.moveToFirst()){
            room.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_PASSWORD)));
            room.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_ID))));
            room.setTopic(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_TOPIC)));
            room.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_USERNAME)));
        }
        cursor.close();
        db.close();
        return room;
    }

    public ArrayList<Message> getMessages(int roomID) {

        String[] columns = {
                COLUMN_MESSAGE_ID,
                COLUMN_MESSAGE_USERNAME,
                COLUMN_MESSAGE_MESSAGE,
                COLUMN_MESSAGE_PICTURE,
                COLUMN_MESSAGE_ROOM_ID,
                COLUMN_MESSAGE_DATE
        };

        String sortOrder =
                COLUMN_MESSAGE_ID + " ASC";
        ArrayList<Message> messages = new ArrayList<Message>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_MESSAGE_ROOM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(roomID)};


        Cursor cursor = db.query(TABLE_MESSAGE, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_ID))));
                message.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_MESSAGE)));
                message.setRoomID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_ROOM_ID))));
                message.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_USERNAME)));
//                Bitmap pic = BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex(COLUMN_MESSAGE_PICTURE)),0,cursor.getBlob(cursor.getColumnIndex(COLUMN_MESSAGE_PICTURE)).length);
//                message.setPicture(pic);
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATE));
                message.setDate(date);
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    public void deleteRoom(int roomID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROOM, COLUMN_ROOM_ID + " = ?", new String[]{String.valueOf(roomID)});
        db.close();
    }

    public void deleteMessage(int messageID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGE, COLUMN_MESSAGE_ID + " = ?", new String[]{String.valueOf(messageID)});
        db.close();
    }



}
