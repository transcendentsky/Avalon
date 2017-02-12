CREATE TABLE GroupMessage(
  id INTEGER PRIMARY KEY,
  time TEXT,
  senderUid INTEGER,
  senderNickName TEXT,
  receiverUid INTEGER,
  receiverNickName TEXT,
  groupUid INTEGER,
  groupName TEXT,
  content TEXT
);

CREATE TABLE FriendMessage(
  id INTEGER PRIMARY KEY,
  time TEXT,
  senderUid INTEGER,
  senderNickName TEXT,
  content TEXT
)