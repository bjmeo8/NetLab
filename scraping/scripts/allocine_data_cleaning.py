import pickle as pk
from netlab import top_ten_movies
from netlab import User
from netlab import Fuser
from tqdm import tqdm
from utils import *

#Get top ten movies
ttm = top_ten_movies("../backups/topten.txt")
ttm = [(str(x)).split("\n")[0] for x in ttm]
#All movies
movies = set()
series = set()
final_dataset = set()

#Load our dataset
def load_dataset(path):
    return pk.load(open(path, "rb"))

# save our final dataset
def save_dataset(dataset,path):
    #save our encoder
    pk.dump(dataset, open(path, "wb"))

def data_merger(limit):
    datam = []
    for t in ttm[:limit]:
        #build the path
        path = t + ".pk"
        #load the data from
        datam.append(load_dataset("data/"+path))

    #merge
    return datam[0] + datam[1]


def clean_data():
    #get data
    data = data_merger(2)
    #browse each title dataset from pk
    #the dataset contains a list of user
    #We are going to browse each user from 
    #the dataset and rebuild his movies data
    #for each user, we are going to get his data (list of films and series)
    for user in tqdm(data):
        #init new fuser (final user) with the user data
        fuser = Fuser(user.pl, user.fw, user.name)
        #User Movies Set
        ums = set()
        #User Series Set
        uss = set()
        #user movies ratings set
        urms = set()
        #user series ratings set
        urss = set()
        #user followers set
        ufs = set()
        #here we browse the user movies and series
        if len(user.movi) > 0 and len(user.seri) > 0:
            for m,s in zip(user.movi,user.seri):
                movies.add(user.movi[m])
                series.add(user.seri[s])
                ums.add(user.movi[m])
                uss.add(user.seri[s])
                m_rate = float(m[1:][0] + "." + (m[1:])[1]) if int((m[1:])[0]) > 0 else float("0." + (m[1:])[1])
                s_rate = float(s[1:][0] + "." + (s[1:])[1]) if int((s[1:])[0]) > 0 else float("0." + (s[1:])[1])
                urms.add(str(m_rate) + ":" + user.movi[m])
                urss.add(str(s_rate) + ":" + user.seri[s])
            fuser.data_init(ums,uss,urms,urss)
            
            #for each followers we make the same process
            for f in user.fw:
                #init new fuser with the follower data
                fuserf = Fuser(f.pl, f.fw, f.name)
                #Follower Movies Set
                fms = set()
                #Follower Series Set
                fss = set()
                #Follower movies ratings set
                frms = set()
                #Follower series ratings set
                frss = set()
                if len(f.movi) > 0 and len(f.seri) > 0:
                    for mo,se in zip(f.movi,f.seri):
                        movies.add(f.movi[mo])
                        series.add(f.seri[se])
                        fms.add(f.movi[mo])
                        fss.add(f.seri[se])
                        m_rate = float(mo[1:][0] + "." + (mo[1:])[1]) if int((mo[1:])[0]) > 0 else float("0." + (mo[1:])[1])
                        s_rate = float(se[1:][0] + "." + (se[1:])[1]) if int((se[1:])[0]) > 0 else float("0." + (se[1:])[1])
                        frms.add(str(m_rate) + ":" + f.movi[mo])
                        frss.add(str(s_rate) + ":" + f.seri[se])
                    fuserf.data_init(fms,fss,frms,frss)
                    ufs.add(fuserf)
                    final_dataset.add(fuserf)
            fuser.set_followers(ufs)
            final_dataset.add(fuser)

    save_dataset(final_dataset,"../final_data/final_dataset.pk")
    save_dataset(movies,"../final_data/movies.pk")
    save_dataset(series,"../final_data/series.pk")

def read_final_dataset():

    dataset = load_dataset("../final_data/final_dataset.pk")

    for d in dataset:
        print(d.id)
        for r in d.mov_rat:
            print("     rating : " + r.split(":")[0] + " movie : " + r.split(":")[1])

def fetch_allocine_movies():
    conn,cursor = open_conn("recomvee2")
    allocine_movies = []
    i = 1
    query = ("SELECT * FROM movie")
    cursor.execute(query)
    for(id, title, genres, overview, production_companies, poster_path, imdb_id, popularity, release_date, data_source) in tqdm(cursor):
        
        genres = genres.replace("[","").replace("]","").replace("'","")
        data = {  
            'movie_id': "acm_" + str(i),
            'title': title,
            'genres': genres,
            'overview': overview,
            'production_companies': production_companies,
            'poster_path': poster_path,
            'release_date': release_date,
            'data_source': "allocine",
        }
        allocine_movies.append(data)
        i += 1

    close_conn()
    save_dataset(allocine_movies,"../final_data/allocine_movies.pk")

def read_allocine_movies():

    dataset = load_dataset("../final_data/allocine_movies.pk")

    for d in dataset:
        print(d)

def insert_allocine_movies():
    allocine_movies = load_dataset("../final_data/allocine_movies.pk")
    conn,cursor = open_conn("recomvee")

    query = ("INSERT INTO movie "
          "(movie_id, title, genres, overview, production_companies, poster_path, release_date, data_source) "
          "VALUES (%(movie_id)s, %(title)s, %(genres)s, %(overview)s, %(production_companies)s, %(poster_path)s, %(release_date)s, %(data_source)s)")

    for am in tqdm(allocine_movies):
        cursor.execute(query , am)
        conn.commit()

    close_conn()

def check_movie_id(title,cursor):
    tmp = "\""
    query = ("SELECT movie_id FROM movie WHERE data_source = {} AND title = {}".format("\"allocine\"",tmp + title + tmp))
    cursor.execute(query)
    for movie_id in cursor:
        return movie_id

def insert_allocine_ratings():
    #connect to the databse
    conn,cursor = open_conn("recomvee")
    #get the allocine user data
    dataset = load_dataset("../final_data/final_dataset.pk")
    #create our query
    query = ("INSERT INTO rating "
             "(movie_id, user_id, rating, source) "
             "VALUES (%(movie_id)s, %(user_id)s, %(rating)s, %(source)s)")

    #browse each user
    for d in tqdm(dataset):
        #for each user, browse his ratings
        for r in d.mov_rat:
            #get the movie rating
            mrate = r.split(":")[0]
            #check and get the movie id from the database
            m_id = check_movie_id(r.split(":")[1],cursor)
            #check if the movie exist
            if m_id is not None:
                #store the rating to the database
                rating = {
                    'movie_id' : m_id[0],
                    'user_id' : d.id,
                    'rating' : mrate,
                    'source' : "allocine",
                }
                cursor.execute(query , rating)
                # Make sure data is committed to the database
                conn.commit()
    close_conn()

print("Begin Allocine Movies Recommandation Data Processing.")
#clean data
# clean_data()
#read data
# read_final_dataset()
# fetch_allocine_movies()
# read_allocine_movies()
# insert_allocine_movies()
# insert_allocine_ratings()
print("End Allocine Movies Recommandation Data Processing.")