from utils import *
import numpy as np
import pickle as pk
from tqdm import tqdm
from sklearn.metrics.pairwise import cosine_similarity

tmp = "\""
# allow us to make the mapping between the users_items_matrix and each user
users_id = list()
movies_id = list()

# make a connection to the database
conn,cursor = open_conn("recomvee")

def to_list(entry,index):
    """
    this is a custom 
    list converter that
    convert a list of tuple
    to a list of all the second
    element of each tuple
    @entry : the list of tuple
    """
    out = []
    for e in entry:
        out.append(e[index])
    return out

def get_user_movies(user_id, th=0):
    """
    Allow to get the user movies
    @user_id : the given user id
    @th : the default rating threshold value
    """
    movies = set()
    query = ("SELECT DISTINCT title,rating FROM rating,movie WHERE rating.user_id = {} AND movie.movie_id = rating.movie_id AND rating.rating >= {}".format(tmp + user_id + tmp, th))
    cursor.execute(query)

    for title,rating in cursor:
        movies.add((rating,title))

    return list(movies)

def get_user_movies_bis(user_id):
    """
    Allow to get the user movies
    @user_id : the given user id
    @th : the default rating threshold value
    """
    movies = set()
    query = ("SELECT DISTINCT title,rating FROM rating,movie WHERE rating.user_id = {} AND movie.movie_id = rating.movie_id".format(tmp + user_id + tmp))
    cursor.execute(query)

    for title,rating in cursor:
        movies.add((rating,title))

    return list(movies)

def make_recommendation(user_id, source, rth, size, field, th=0.5):
    """
    Allow to make a recommendation for 
    a given user and other parameters
    @user_id : the given user id
    @source : the data source (kaggle or allocine)
    @rth : the rating threshold value to get all movies with ratings >= rth
    @size : the data size (users size or movies size)
    @field : to set a desired size for user or movie only (movies or users)
    @th : the default cosine similarity threshold value
    """
    # compute users_users_matrix and users similarity    
    if source == 'kaggle' :
        user_user_file = open('user_user_kaggle.pkl', 'rb')
        similarity_file = open('similarity_kaggle.pkl', 'rb')
    else : 
        user_user_file = open('user_user_allocine.pkl', 'rb')
        similarity_file = open('similarity_allocine.pkl', 'rb')
        
    users_users_matrix = pk.load(user_user_file)
    similarity = pk.load(similarity_file)
    
    user_user_file.close()
    similarity_file.close()
    
    # list of recommendation
    top_movies = set()
    top_recommendation = set()

    # get the user similarity
    user_sim = similarity[user_id] if similarity.get(user_id) is not None else []

    if len(user_sim) > 0:

        # fetch the user movies and his similar user movies
        u_movies = get_user_movies(user_id, rth)
        # sort by descending
        u_movies.sort()
        u_movies.reverse()

        # merge all similars movies in order to sort them by ratings
        movies = []
        for sim_id in user_sim:
            # get each similar user movies
            movies = get_user_movies(sim_id, rth)
            # merge
            movies += movies

        # show similar users movies sorted by descending ratings
        movies.sort()
        movies.reverse()
        # print(movies)

        # browse the similar movies and store all the movies that are not in the current user movies
        for m in movies:
            if m not in u_movies:
                top_movies.add(m)

        # make a conversion from list of tuple to list of movies only
        u_movies_list = to_list(u_movies,1)
        top_movies_list = to_list(top_movies,1)

        for tm in top_movies_list:
            if tm not in u_movies_list:
                top_recommendation.add(tm)
        
        close_conn()
        
        return list(top_recommendation)
    
    # if some error appear
    else:
        return "No Recommendation!"

top_recommendation = make_recommendation("121", "kaggle", 3, 0, "none", 0.5)
print(top_recommendation)
