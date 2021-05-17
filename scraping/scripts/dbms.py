import mysql.connector
from netlab import User
from netlab import Fuser
from netlab import load_dataset
from netlab import save_dataset

config = {
  'user': 'root',
  'password': 'root',
  'host': 'localhost',
  'port':  8889,
  'database': 'recomvee',
  'raise_on_warnings': True
}

conn = 0
cursor = 0
dataset = load_dataset("../final_data/final_dataset")

def open_conn():
  global conn,cursor
  conn = mysql.connector.connect(**config)
  cursor = conn.cursor()

def close_conn():
  global conn,cursor
  cursor.close()
  conn.close()


#fetching data
def fetch_followers():
  query = ("SELECT * FROM follower")
  cursor.execute(query)
  for (id, id_user, id_follower) in cursor:
    print("id : {} id_user : {} id_follower {}".format(id, id_user, id_follower))

def fetch_movies():
  query = ("SELECT * FROM movie")
  cursor.execute(query)
  for (id_user, title, genres, overview, production_companies, poster_path, imdb_id) in cursor:
    print("ID : {} TITLE : {} GENRES {} OVERVIEW {} PRODUCTION_COMPANIES {} POSTER_PATH {} IMDB_ID {}".format(
      id_user, title, genres, overview, production_companies, poster_path, imdb_id))

def fetch_ratings():
  query = ("SELECT * FROM rating")
  cursor.execute(query)
  for (rating_id, movie_id_freignkey, user_id_foreignke, rating) in cursor:
    print("ID : {} TITLE : {} GENRES {} OVERVIEW {}".format(rating_id, movie_id_freignkey, user_id_foreignke, rating))

def fetch_users():
  query = ("SELECT * FROM user")
  cursor.execute(query)
  for (id_user, name_user, password_user, mail_user, page_link) in cursor:
    print("ID : {} TITLE : {} GENRES {} OVERVIEW {} PRODUCTION_COMPANIES {}".format(id_user, name_user, password_user, mail_user, page_link))

#insertion data
def insert_followers(id_usr,id_follower):
  query = ("INSERT INTO follower "
          "(id_usr, id_follower) "
          "VALUES (%(id_usr)s, %(id_follower)s)")

  follower_data = {
    'id_usr': id_usr,
    'id_follower': id_follower,
  }

  cursor.execute(query , follower_data)
  # Make sure data is committed to the database
  conn.commit()

def insert_movies():
  return 0

def insert_ratings():
  return 0

def insert_users(user):
  query = ("INSERT INTO user "
          "(id_user, name_user, password_user, mail_user, page_link, user_type) "
          "VALUES (%(id_user)s, %(name_user)s, %(password_user)s, %(mail_user)s, %(page_link)s, %(user_type)s)")

  user_data = {
    'id_user': user.id,
    'name_user': user.name,
    'password_user': user.id,
    'mail_user': user.name + "@gmail.com",
    'page_link': user.pl,
    'user_type': "allocine",
  }

  cursor.execute(query , user_data)
  # Make sure data is committed to the database
  conn.commit()

def insert_multi_users():
  for user in dataset:
    insert_users(user)

def insert_multi_followers():
  for user in dataset:
    if user.fw is not None:
      for f in user.fw:
        insert_followers(user.id,f.id)

def launch():
  open_conn()
  # fetch_movies()
  # insert_multi_users()
  insert_multi_followers()
  close_conn()

launch()