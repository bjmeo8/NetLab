from netlab import allocine_connect
from netlab import go_to_movie_reviews_page_V2
from netlab import get_top_ten_prolific_users
from netlab import get_all_from_top_ten
from netlab import top_ten_movies
from netlab import sleep


def launch():

    #connect to allocine and check the cookies
    print("NetLab Scraping Engine V1.0")
    allocine_connect("https://www.allocine.fr/")
    sleep(5)

    
    #read top ten movies from the file
    ttm = top_ten_movies("topten.txt")

    #navigate to the given movie reviews page
    for x in ttm:
        print("Begin ",x," dataset building...")
        #parse the movie title
        title = (str(x)).split("\n")[0]
        #navigate to the movie reviews page
        go_to_movie_reviews_page_V2(title)
        #perform a quick sleep
        sleep(5)
        #get all users related to this movie
        users = get_top_ten_prolific_users()
        #get all users related to this movie
        get_all_from_top_ten(title,users,50)

launch()