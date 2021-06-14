import numpy as np
import matplotlib.pyplot as plt
import mysql.connector
import time
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel


# Get data (titles, overviews, genres and production companies) from database
def get_data_from_DB(source):
    title_list = []
    overview_list = []
    genres_list = []
    production_companies_list = []
    cnx = mysql.connector.connect(user='root', database='recomvee')
    cursor = cnx.cursor()

    if source is not None:
        query = ("SELECT title, overview, genres, production_companies FROM movie WHERE data_source = '" + source + "'")
    else:
        query = ("SELECT title, overview, genres, production_companies FROM movie")
    cursor.execute(query)

    for (title, overview, genres, production_companies) in cursor:
        title_list.append(format(title))
        overview_list.append(format(overview))
        genres_list.append(format(genres))
        production_companies_list.append(format(production_companies))

    cursor.close()
    cnx.close()
    return title_list, overview_list, genres_list, production_companies_list


# Create the Item-Item matrix
def createItemItemMatrix(length, titles_output_file, matrix_output_file, source):
    title_list, overview_list, genres_list, production_companies_list = get_data_from_DB(source)

    criteria = []
    criteria.append(overview_list[:length])
    criteria.append(title_list[:length])
    criteria.append(genres_list[:length])
    criteria.append(production_companies_list[:length])

    cosine_sim = np.zeros((length, length))

    for k in range(len(criteria)):
        tfidf_vectorizer = TfidfVectorizer()
        tfidf_matrix = tfidf_vectorizer.fit_transform(criteria[k])
        cosine_sim += linear_kernel(tfidf_matrix, tfidf_matrix)

    cosine_sim /= len(criteria)

    np.save(titles_output_file, title_list[:length])
    np.save(matrix_output_file, cosine_sim)


# Get recommendation's execution time graphics
def exec_time_graph():
    exec_time_matrix_creation = []
    for j in range(5):
        length = (j + 1) * 5000
        print("--- Exec Time Step %s ---" % (j + 1))

        print("Matrix calculation ...")
        start_time = time.time()
        createItemItemMatrix(length, "titles" + str(j) + ".npy", "matrix" + str(j) + ".npy")
        exec_time = time.time() - start_time
        exec_time_matrix_creation.append(exec_time)
        print("Done")

    x = [5000, 10000, 15000, 20000, 25000]
    plt.plot(x, exec_time_matrix_creation)
    plt.xlabel('Amount of movie')
    plt.ylabel('Execution Time (seconds)')
    plt.title("Evolution of the matrix's calculation time compared to the amount of movie")
    plt.show()


source = 'allocine'

if source == 'kaggle':
    titles_output_file = "titles_kaggle.npy"
    matrix_output_file = "matrix_kaggle.npy"
elif source == 'allocine':
    titles_output_file = "titles_allocine.npy"
    matrix_output_file = "matrix_allocine.npy"

createItemItemMatrix(6000, titles_output_file, matrix_output_file, source)