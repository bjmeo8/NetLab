import numpy as np
import matplotlib.pyplot as plt
import mysql.connector
import time
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel

# ---------------------- SQL Queries to Database ---------------------- #

# Get data (titles, overviews, genres and production companies) from database
def get_data_from_DB(source):
    title_list = []
    overview_list = []
    genres_list = []
    production_companies_list = []
    cnx = mysql.connector.connect(user='root', database='recomvee')
    cursor = cnx.cursor()
    
    if source is not None : 
        query = ("SELECT title, overview, genres, production_companies FROM movie WHERE data_source = '" + source +"'")
    else :
        query = ("SELECT title, overview, genres, production_companies FROM movie")
    cursor.execute(query)
    
    for (title, overview, genres, production_companies) in cursor :
        title_list.append(format(title))
        overview_list.append(format(overview))
        genres_list.append(format(genres))
        production_companies_list.append(format(production_companies))

    cursor.close()
    cnx.close()
    return title_list, overview_list, genres_list, production_companies_list

# Get a list of all users of a specified source
def get_users(source):
    users = []
    cnx = mysql.connector.connect(user='root', database='recomvee')
    cursor = cnx.cursor()
    
    query = ("SELECT id_user FROM user WHERE user_type = '" + source + "'")
    cursor.execute(query)
    
    for user, in cursor :
        users.append(format(user))

    cursor.close()
    cnx.close()
    return users
    
# Get a list of movies that a user from a specified source has rated
def get_user_history(uid,source):
    history = dict()
    cnx = mysql.connector.connect(user='root', database='recomvee')
    cursor = cnx.cursor()
    
    query = ("SELECT movie.title, rating.rating FROM movie, rating WHERE rating.user_id = '" + uid + "' AND rating.source = '" + source + "' AND rating.movie_id = movie.movie_id")
    cursor.execute(query)
    
    for title, rating in cursor :
        history[title] = rating

    cursor.close()
    cnx.close()
    return history

# ---------------------- Recommendation algorithm ---------------------- #

# Create the Item-Item matrix
def createItemItemMatrix(length, titles_output_file, matrix_output_file,source):
    title_list, overview_list, genres_list, production_companies_list  = get_data_from_DB(source)
    
    criteria = []
    criteria.append(overview_list[:length])
    criteria.append(title_list[:length])
    criteria.append(genres_list[:length])
    criteria.append(production_companies_list[:length])

    cosine_sim = np.zeros((length,length))
    
    for k in range(len(criteria)) :

        tfidf_vectorizer = TfidfVectorizer()
        tfidf_matrix = tfidf_vectorizer.fit_transform(criteria[k])
        cosine_sim += linear_kernel(tfidf_matrix, tfidf_matrix)
 
    cosine_sim /= len(criteria)

    np.save(titles_output_file, title_list[:length])
    np.save(matrix_output_file, cosine_sim)

# Get the recommendation for one film in particular
def get_recommendations(title, cosine_sim, title_list):
    result = []
    # Get the index of the movie that matches the title
    idx = title_list.index(title)
    # Get the pairwsie similarity scores
    sim_scores = list(enumerate(cosine_sim[idx]))
    # Sort the movies based on the similarity scores
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)
    # Get the scores for 100 most similar movies
    sim_scores = sim_scores[1:101]
    # Get the movie indices
    movie_indices = [i[0] for i in sim_scores]
    # Return the top 100 most similar movies
    for i in range(100):
        result.append(title_list[movie_indices[i]])
    return result

# Get the recommendation for an history 
def get_recommendations_history(history, cosine_sim, title_list, timestamp = False):
    votes = dict()
    keys_list = list(history)
    for movie in history.keys() :
        if timestamp :
            w = 1 - (keys_list.index(movie)/len(history))*0.75
        else : w = 1
        rec = get_recommendations(movie, cosine_sim, title_list)
        if movie == keys_list[0]:
            first_movie = movie
            first_rec = rec 
        for movie_rec in rec:
            if movie_rec in votes:
                votes[movie_rec] += (1-rec.index(movie_rec)/100)*w*history[movie]
            else:
                votes[movie_rec] = (1-rec.index(movie_rec)/100)*w*history[movie]
    for movie in history:
        if movie in votes:
            votes.pop(movie)
    return sorted(votes, key = votes.get, reverse = True), votes, first_movie, first_rec

# ---------------------- Tests and Graphics ---------------------- #

# Get recommendation's execution time graphics
def exec_time_graph(history):
    exec_time_matrix_creation = []
    exec_time_loading = []
    exec_time_recommendation = []
    for j in range(5):
        length = (j+1)*5000
        print("--- Exec Time Step %s ---" % (j+1))
        
        print("Matrix calculation ...")
        start_time = time.time()
        createItemItemMatrix(length, "titles" + str(j) + ".npy","matrix" + str(j) + ".npy")
        exec_time = time.time() - start_time
        exec_time_matrix_creation.append(exec_time)
        print("Done")
        
        print("Matrix loading ...")
        start_time = time.time()
        title_list = np.load("titles" + str(j) +".npy").tolist()
        item_item_matrix = np.load("matrix" + str(j) +".npy")
        exec_time = time.time() - start_time
        exec_time_loading.append(exec_time)
        print("Done")
    
        print("Recommendation ...")
        start_time = time.time()
        recommendation = get_recommendations_history(history, item_item_matrix,title_list)
        exec_time = time.time() - start_time
        exec_time_recommendation.append(exec_time)
        print("Done")
        
        print("Clearing data") 
        del title_list, item_item_matrix, recommendation
        
    x = [5000,10000,15000,20000,25000]        
    plt.plot(x,exec_time_matrix_creation)
    plt.xlabel('Amount of movie')
    plt.ylabel('Execution Time (seconds)')
    plt.title("Evolution of the matrix's calculation time compared to the amount of movie")
    plt.show()
    
    plt.plot(x,exec_time_loading)
    plt.xlabel('Amount of movie')
    plt.ylabel('Execution Time (seconds)')
    plt.title("Evolution of the execution time of the matrix's loading compared to the amount of movie")
    plt.show()

    plt.plot(x,exec_time_recommendation)
    plt.xlabel('Amount of movie')
    plt.ylabel('Execution Time (seconds)')
    plt.title("Evolution of the recommendation's execution time compared to the amount of movie")
    plt.show()

# Get the relevance percentage of the recommendation having hidden half of the user's history
def relevance_hidden_history(user_id, source):
    history = get_user_history(user_id, source)
    relevance = 0
    processed_user = 0
    if len(history) >= 2 :
        processed_user = 1
        visible_history = dict(list(history.items())[len(history)//2:])
        hidden_history = dict(list(history.items())[:len(history)//2])
    
        recommendation, votes, first_movie, first_rec = get_recommendations_history(visible_history, item_item_matrix,title_list)
        
        for movie in hidden_history :
            if movie in recommendation :
                relevance += 1
        relevance = relevance / len(hidden_history) * 100
    
    return relevance,processed_user

# Get the average relevance percentage for all users
def test_relevance(source):
    users = get_users(source)
    relevance = 0
    processed_user = 0
    for user in users :
        r, pu = relevance_hidden_history(user, source)
        relevance += r
        processed_user += pu
    relevance /= processed_user
    
    return relevance

# ---------------------- Execution Example ---------------------- #

#createItemItemMatrix(9524,"titles.npy","matrix.npy",'allocine')

title_list = np.load('titles.npy').tolist()
item_item_matrix = np.load('matrix.npy')

history = {'Ça' : 5, 'Ça - Il est revenu' : 4, "Avengers : L'ère d'Ultron" : 2, 'Thor' : 1, 'Iron Man 3': 2, 'Avengers: Endgame' : 3}
# history = get_user_history('Z20180216141155987942349', 'allocine')
recommendation, votes, first_movie, first_rec = get_recommendations_history(history, item_item_matrix, title_list)

# relevance = test_relevance('allocine')