import mysql.connector

conn = 0
cursor = 0

def open_conn(database):
  global conn,cursor
  config = {
    'user': 'root',
    'password': 'root',
    'host': 'localhost',
    'port':  8889,
    'database': database,
    'raise_on_warnings': True
  }

  conn = mysql.connector.connect(**config)
  cursor = conn.cursor(buffered=True)
  print("connected!")
  
  return conn,cursor


def close_conn():
  global conn,cursor
  cursor.close()
  conn.close()
  print("closed!")