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


def get_movie_index(mv_id):
    """
    Get the movie index 
    from the movie list
    @mv_id : the given movie id
    """
    return movies_id.index(mv_id) if mv_id in movies_id else -1

def get_user_index(usr_id):
    """
    Get the user index 
    from the user list
    @usr_id : the given user id
    """
    return users_id.index(usr_id) if usr_id in users_id else -1

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

def get_ids(source):
    """
    This function allow to fetch
    each movies and users
    id from the database
    and then store them in a list
    @source : the data source (kaggle or allocine)
    """
    usrs_id = set()
    movs_id = set()
    query = ("SELECT DISTINCT user_id , movie_id FROM rating WHERE source = {}".format(tmp + source + tmp))
    cursor.execute(query)
    
    for (user_id , movie_id) in cursor:
        usrs_id.add(user_id)
        movs_id.add(movie_id)
    
    return list(usrs_id) , list(movs_id)

def get_ids_bis(source,size,field="movies"):
    """
    This is the second version
    of the get_ids() function.
    It's allow to fetch each data
    with a desired size
    @source : the data source (kaggle or allocine)
    @size : the data size (users size or movies size)
    @field : to set a desired size for user or movie only (movies or users)
    for the field, none means that we are going to fetch all the data 
    without a desired size (users and movies)
    """
    usrs_id = set()
    movs_id = set()

    queryu = ("SELECT DISTINCT user_id FROM rating WHERE source = {}".format(tmp + source + tmp))
    querym = ("SELECT DISTINCT movie_id FROM rating WHERE source = {}".format(tmp + source + tmp))

    if field == "users":
        queryu = ("SELECT DISTINCT user_id FROM rating WHERE source = {} LIMIT {}".format(tmp + source + tmp, size))
        
    cursor.execute(queryu)
    
    for user_id in cursor:
        usrs_id.add(user_id)

    if field == "movies":
        querym = ("SELECT DISTINCT movie_id FROM rating WHERE source = {} LIMIT {}".format(tmp + source + tmp, size))
    
    cursor.execute(querym)

    for movie_id in cursor:
        movs_id.add(movie_id)
    
    return list(usrs_id) , list(movs_id)

def get_data_size(source):
    """
    Allow to get our data
    size from the database
    @source : the data source (kaggle or allocine)
    """
    query = ("SELECT COUNT(DISTINCT movie_id) as movie_id_counts, COUNT(DISTINCT user_id) as user_id_counts FROM rating WHERE source = {}".format(tmp + source + tmp))
    cursor.execute(query)
    for (movie_id_counts , user_id_counts) in cursor:
        return movie_id_counts , user_id_counts

def get_users_size(source):
    """
    Only get the users size
    @source : the data source (kaggle or allocine)
    """
    query = ("SELECT COUNT(DISTINCT user_id) as user_id_counts FROM rating WHERE source = {}".format(tmp + source + tmp))
    cursor.execute(query)
    for user_id_counts in cursor:
        return user_id_counts[0]

def get_items_size(source):
    """
    Only get the movies size
    @source : the data source (kaggle or allocine)
    """
    query = ("SELECT COUNT(DISTINCT movie_id) as movie_id_counts FROM rating WHERE source = {}".format(tmp + source + tmp))
    cursor.execute(query)
    for movie_id_counts in cursor:
        return movie_id_counts[0]

def create_users_items_matrix(source,size,field):
    """
    This function allow to 
    create the user_items_matrix
    @source : the data source (kaggle or allocine)
    @size : the data size (users size or movies size)
    @field : to set a desired size for user or movie only
    """
    global users_id , movies_id
    
    # we get the dataset size
    if field != "none" and size != 0:
        items_size = size if field == "movies" else get_items_size(source)
        users_size = size if field == "users" else get_users_size(source)
        # init users id set
        users_id , movies_id = get_ids_bis(source,size,field)
    else:
        items_size , users_size = get_data_size(source)
        # init users id set
        users_id , movies_id = get_ids(source)

    # init users_items_matrix with the dataset size
    users_items_matrix = np.zeros((users_size, items_size))
    # init the ratings row
    row = np.zeros(items_size)
    # we create our rating query
    query = ("SELECT movie_id , user_id , rating FROM rating WHERE source = {}".format(tmp + source + tmp))
    # send the request to the database and then fetch the data
    cursor.execute(query)
    
    # get each user id and movie id and compute the users_items_matrix ceils
    for(movie_id , user_id , rating) in cursor:
        uindex = get_user_index(user_id)
        mindex = get_movie_index(movie_id)
        users_items_matrix[uindex][mindex] = rating if uindex != -1 and mindex != -1 else 0

    # save our data by precaution
    # save_dataset(users_items_matrix,"../final_data/collaborative/users_items_matrix.pk")
    # save_dataset(users_id,"../final_data/collaborative/users_id.pk")
    # save_dataset(movies_id,"../final_data/collaborative/movies_id.pk")

    return users_items_matrix


def create_users_users_matrix(source, size, field, th=0.5):
    """
    Allow to create the user user similarity matrix
    @source : the data source (kaggle or allocine)
    @size : the data size (users size or movies size)
    @field : to set a desired size for user or movie only
    @th : the default cosine similarity threshold value
    """
    # we create first the users_items_matrix
    users_items_matrix = create_users_items_matrix(source,size,field)
    similarity = {}

    # now we init our users_users_matrix with the users size
    users_users_matrix = np.eye(len(users_id))
    ui_vect = np.zeros((2,len(movies_id)))
    uj_vect = np.zeros((2,len(movies_id)))

    # we now create the user user matrix with cosine similarity formula
    for ui in tqdm(users_id):
        # get the user index first
        i = get_user_index(ui)
        # init the user movies vect from the users_items_matrix
        ui_vect[0] = users_items_matrix[i]
        # init the similarity vect for this user in order to store all user similar to him
        sim_users = []
        for uj in users_id:
            # get the each other user index except the current user because he have a similarity score = 1 with himself
            j = get_user_index(uj) if ui != uj else -1
            # init the other user movies vector from the users_items_matrix
            uj_vect[0] = users_items_matrix[j]
            # compute the cosine similarity between the current user and all others users except him
            cs = cosine_similarity(ui_vect, uj_vect)[0][0]
            # affect this score to the users_users_matrix
            users_users_matrix[i][j] = cs
            # then if this score is >= th, we decide that this other user is similar to the current user by their movies
            if cs >= th:    
                sim_users.append(uj)

        #we set the similarity dict with the current user id and his list of similar user
        similarity[ui] = sim_users

    # we store our data by precaution because the execution an be too long
    # save_dataset(users_users_matrix,"../final_data/collaborative/users_users_matrix.pk")
    # save_dataset(similarity,"../final_data/collaborative/similarity.pk")

    # finally we return both users_users_matrix and the similarity dict
    return users_users_matrix , similarity

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
    users_users_matrix , similarity = create_users_users_matrix(source, size, field, th)
    
    # list of recommendation
    top_movies = set()
    top_recommendation = set()
    
    # show similarity dict
    # for sim in similarity:
    #     print(str(sim) +":"+ str(similarity[sim]))

    # print("-------------Similar users to {}--------------".format(user_id))
    # get the user similarity
    user_sim = similarity[user_id] if similarity.get(user_id) is not None else []
    # print("user_sim : ", user_sim if user_sim != -1 else "No similar users are founded")

    if len(user_sim) > 0:
        
        # print("--------------------User movies---------------------")

        # fetch the user movies and his similar user movies
        u_movies = get_user_movies(user_id, rth)
        # sort by descending
        u_movies.sort()
        u_movies.reverse()
        
        # since the user movies is a list of tuple, we show each movie rating and his title
        # for m in u_movies:
        #     print(m[1] + " : " + str(m[0]))
        
        # print("-----------Similar movies to user movies-----------")

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
