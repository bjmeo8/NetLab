import mysql.connector
import pickle as pk
import csv
from tqdm import tqdm

kusers = []
movies_id = set()
checked_id = set()

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

def open_conn():
  global conn,cursor
  conn = mysql.connector.connect(**config)
  cursor = conn.cursor(buffered=True)
  print("connected!")


def close_conn():
  global conn,cursor
  cursor.close()
  conn.close()
  print("closed!")


class Kaggle:
    """
    Kaggle user class
    id : the kaggle user id
    name : a generated name
    ratings : all user ratings ([MovieId , Rating , Timestamp])
                                [Str , Float , LongInt]
    """

    def __init__(self, id, name, ratings):
        self.id = id
        self.name = name
        self.ratings = ratings

    def data_init(self, ratings):
        self.ratings = ratings
    
    def set_id(self, id):
        self.id = id

    def set_name(self, name):
        self.name = name
    
    def set_ratings(self, ratings):
        self.ratings = ratings


#Load our dataset
def load_dataset(path):
    return pk.load(open(path, "rb"))


# save our final dataset
def save_dataset(dataset,path):
    #save our encoder
    pk.dump(dataset, open(path, "wb"))


def load_kaggle_dataset():
    n = 1
    tmp = '1'
    ratings = []
    kuser = Kaggle(tmp, "kuser_" + str(n), ratings)
    with open('../data/ratings_small.csv', newline ='') as csvfile:
        spamreader = csv.reader(csvfile, delimiter =',')
        next(spamreader, None)  # skip the headers
        for row in spamreader:
            if row[0] == tmp:
                data = [row[1], float(row[2]), row[3]]
                ratings.append(data)
            else:
                kuser.set_ratings(ratings)
                kusers.append(kuser)
                tmp = row[0]
                ratings = []
                n += 1
                _data = [row[1], float(row[2]), row[3]]
                ratings.append(_data)
                kuser = Kaggle(tmp, "kuser_" + str(n), None)
    
    save_dataset(kusers,"../final_data/kusers.pk")


def read_dataset():
    global movies_id
    data = load_dataset("../final_data/kusers.pk")
    for ku in data:
        print("name : " + ku.name + " id : " + ku.id)
        for r in ku.ratings:
            movies_id.add(str(r[0]))
    
    save_dataset(movies_id,"../final_data/kaggle_movies_id.pk")


def check_movie_id(m_id):
    query = ("SELECT id FROM movie WHERE id = {}".format(m_id))
    cursor.execute(query)
    for (id) in cursor:
        return id


def fetch_movies_id():
    #load movies id dataset
    movies_id = load_dataset("../final_data/kaggle_movies_id.pk")
    #connect to the database
    open_conn()
    #browse the movies id dataset and check each id 
    #and then add it to the checked id set
    for m in tqdm(movies_id):
        m_id = check_movie_id(m)
        if m_id is not None:
            checked_id.add(m_id[0])
    #save our checked movies dataset
    save_dataset(checked_id,"../final_data/checked_id.pk")
    for ch_id in checked_id:
        print("ch_id : " + ch_id)
    print("total : " + str(len(checked_id)))
    #close the database connection
    close_conn()


def read_checked_id():
    checked_id = load_dataset("../final_data/checked_id.pk")
    movies_id = load_dataset("../final_data/kaggle_movies_id.pk")
    
    for ch_id in checked_id:
        print("ch_id : " + ch_id)
    print("total : " + str(len(checked_id)))
    print("stats : " + str(len(checked_id)) + "/" + str(len(movies_id)) + " " +str(int(100*(len(checked_id)/len(movies_id)))) + "%")


def clean_user_ratings():
    checked_id = load_dataset("../final_data/checked_id.pk")
    kusers = load_dataset("../final_data/kusers.pk")
    final_kusers = set()
    for ku in kusers:
        urate = []
        for r in ku.ratings:
            if r[0] in checked_id:
                urate.append([r[0],r[1],r[2]])
        ku.set_ratings(urate)
        final_kusers.add(ku)
    save_dataset(final_kusers,"../final_data/final_kusers.pk")


def read_final_kusers():
    final_kusers = load_dataset("../final_data/final_kusers.pk")
    for ku in final_kusers:
        print("name : " + ku.name + " id : " + ku.id)
        for r in ku.ratings:
            print("     ratings : " + str(r))
    print("total : " + str(len(final_kusers)))


def insert_kuser(kuser):
    query = ("INSERT INTO user "
          "(id_user, name_user, password_user, mail_user, user_type) "
          "VALUES (%(id_user)s, %(name_user)s, %(password_user)s, %(mail_user)s, %(user_type)s)")
    
    kuser_data = {
        'id_user': kuser.id,
        'name_user': kuser.name,
        'password_user': kuser.name + kuser.id + "#kaggle#",
        'mail_user': kuser.name + "@gmail.com",
        'user_type': "kaggle",
    }

    cursor.execute(query , kuser_data)
    # Make sure data is committed to the database
    conn.commit()


def insert_kusers():
    open_conn()
    final_kusers = load_dataset("../final_data/final_kusers.pk")
    for ku in final_kusers:
        insert_kuser(ku)
    print("total : " + str(len(final_kusers)))
    close_conn()


def fetch_movies():
    kaggle_movies = []
    query = ("SELECT * FROM movies")
    open_conn()
    cursor.execute(query)
    for (movie_id, title, genres, overview, production_companies, poster_path, imdb_id, popularity, release_date) in tqdm(cursor):
        
        genres = genres.replace("[","").replace("]","").replace("{","").replace("}","")
        data = {
            'movie_id': movie_id,
            'title': title,
            'genres': genres,
            'overview': overview,
            'production_companies': production_companies,
            'poster_path': poster_path,
            'imdb_id': imdb_id,
            'popularity': float(popularity) if popularity not in ["Beware Of Frost Bites", '']  else 0.0,
            'release_date': release_date,
            'data_source': "kaggle",
        }
        kaggle_movies.append(data)

    close_conn()
    save_dataset(kaggle_movies,"../final_data/kaggle_movies.pk")


def insert_movies():
    kaggle_movies = load_dataset("../final_data/kaggle_movies.pk")
    open_conn()

    query = ("INSERT INTO movie "
          "(movie_id, title, genres, overview, production_companies, poster_path, imdb_id, popularity, release_date, data_source) "
          "VALUES (%(movie_id)s, %(title)s, %(genres)s, %(overview)s, %(production_companies)s, %(poster_path)s, %(imdb_id)s, %(popularity)s, %(release_date)s, %(data_source)s)")

    for km in tqdm(kaggle_movies):
        cursor.execute(query , km)
        conn.commit()

    close_conn()

def insert_rating(rating):
    query = ("INSERT INTO rating "
          "(movie_id, user_id, rating) "
          "VALUES (%(movie_id)s, %(user_id)s, %(rating)s)")
    
    cursor.execute(query , rating)
    conn.commit()

def insert_ratings():
    final_kusers = load_dataset("../final_data/final_kusers.pk")
    open_conn()
    for ku in tqdm(final_kusers):
        for r in ku.ratings:
            rating = {
                'movie_id': r[0],
                'user_id': ku.id,
                'rating': r[1],
            }
            insert_rating(rating)
    close_conn()

#launch
# load_kaggle_dataset()
# read_dataset()
# read_checked_id()
# clean_user_ratings()
# read_final_kusers()
# insert_kusers()
# fetch_movies()
# insert_movies()
# insert_ratings()