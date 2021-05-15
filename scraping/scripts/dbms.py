import mysql.connector

config = {
  'user': 'root',
  'password': 'root',
  'host': 'localhost',
  'port':  8889,
  'database': 'kaggle',
  'raise_on_warnings': True
}

conn = mysql.connector.connect(**config)
cursor = conn.cursor()

query = ("SELECT * FROM movie")
cursor.execute(query)

for (id_user, title, genres, overview, production_companies, poster_path, imdb_id) in cursor:
  print("ID : {} TITLE : {} GENRES {} OVERVIEW {} PRODUCTION_COMPANIES {} POSTER_PATH {} IMDB_ID {}".format(
    id_user, title, genres, overview, production_companies, poster_path, imdb_id))

cursor.close()
conn.close()