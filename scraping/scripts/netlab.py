from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import requests
from bs4 import BeautifulSoup
import os
import json
import re
import csv
import time
import pickle as pk
from tqdm import tqdm

#justifier la stratégie de scraping (l'algo dévéloppé) en le comparant sur les échantillons de données reçus
#Essayer d'expliquer la recommendation effectuée
#indiquer le pourquoi de la recommandation
#Essayer de trouver une explication à la recommandation


#get the firefox driver
driver = webdriver.Firefox()
search_url_base = "https://www.allocine.fr/rechercher/?q="
reviews_link_text = "Critiques spectateurs"
movies_reviews_page = "https://www.allocine.fr/film/fichefilm-249877/critiques/spectateurs/"
link_patterns = "fr/membre-"
data_path = "data/"
csv_columns = ['Ratings','Movies']
all_users = set()
proceeded_users = set()
users_set = set()
user_cpt = 0

class User:
    """
    A simple Allocine user class
    id : user id (Ex : Z20130627102432253638364)
    pid : parent id (the user he is following)
    pl : page_link
    fw : followers
    first of all, the list of followers is a list of
    simple string
    When we compute all followers and get each user data
    We finally transform the simple list of string into
    a list of user that's why we define the new function
    (set_followers) in order to performs this action
    """

    def __init__(self, page_link, followers, name):
        self.id = page_link.split("membre-",2)[1].split("/",3)[0]
        self.pl = page_link
        self.fw = followers
        self.name = name

    def data_init(self, movies, series):
        self.movi = movies
        self.seri = series
    
    def set_followers(self, followers):
        self.fw = followers

# save our final dataset
def save_dataset(dataset,path):
    #save our encoder
    pk.dump(dataset, open(path + ".pk", "wb"))

# load our dataset
def load_dataset(path):
    #load with pk
    return pk.load(open(path + ".pk", "rb"))


def top_ten_movies(path):
    """
    Get top ten films
    in order to scrape max
    user and data from allocine
    """
    content = open(path, "r")
    topten = []
    for x in content:
        topten.append(x)        
    return topten

def sleep(sec):
    time.sleep(sec)

def allocine_connect(url):
    """
    This function allow to
    established a connection
    to allocine
    """
    #go to allocine page
    driver.get(url)
    #sleep until the page load
    sleep(10)
    #click on cookies button
    print("cookies checking")
    cookies_check_v2()
    sleep(1)
    driver.get(url)

def cookies_check_v1():
    """
    This is the 1st version
    of the cookies checker
    In this function, we
    check the website cookies
    """
    cookies_check = driver.find_element_by_xpath("//button[@id='didomi-notice-agree-button']")
    cookies_check.click()

def cookies_check_v2():
    """
    This is the 2nd version
    of the cookies checker
    In this function, we
    check the website cookies
    """
    cookies_check = driver.find_element_by_xpath("//button[@class='jad_cmp_paywall_button jad_cmp_paywall_button-cookies jad_cmp_paywall_cookies didomi-components-button didomi-button didomi-dismiss-button didomi-components-button--color didomi-button-highlight highlight-button']")
    cookies_check.click()

def go_to_movie_reviews_page_v1(movie):
    """
    This is the 1st version
    of movie reviews page
    browser
    In this function, we
    navigate to the movie reviews
    page in order to get the top ten 
    prolifics reviews
    """
    #get search bar input and send the movie name as key
    search_bar = driver.find_element_by_xpath("//input[@id='header-search-input']")
    search_bar.send_keys(movie)
    sleep(5)
    search_button = driver.find_element_by_xpath("//button[@class='header-search-submit icon icon-search']")
    search_button.click()

def go_to_movie_reviews_page_V2(movie):
    """
    This is the 2nd version
    of movie reviews page
    browser
    In this function, we
    navigate to the movie reviews
    page in order to get the top ten 
    prolifics reviews
    """
    #/film/fichefilm-249877/critiques/spectateurs/
    driver.get(search_url_base+movie)
    # driver.get("https://www.allocine.fr/rechercher/?q=yourname")
    sleep(5)
    movie_link = driver.find_element_by_link_text(movie)
    movie_link.click()
    # sleep(5)
    # close_popup = driver.find_element_by_xpath("//button[@class='modal-close icon icon-cross light js-trigger-overlay-close']")
    # close_popup.click()
    sleep(5)
    movie_reviews_link = driver.find_element_by_link_text(reviews_link_text)
    movie_reviews_link.click()

def get_top_ten_prolific_users():
    """
    In this function, we
    scrape the top ten prolific
    user from their reviews
    """
    #we get all users link page
    users_profile = driver.find_elements_by_xpath("//a[@class='xXx']")
    #We then get each links one by one
    links = [elem.get_attribute('href') for elem in users_profile]
    #this reprensents the final list that will contains the 10 prolifics users
    users = []

    #for each link, we first check il the link contains our link pattern ("fr/membre-")
    #if yes, we get the user and append it to our list of user
    for i in range(len(links)):
        if link_patterns in links[i]:
            users.append(links[i])
            print(links[i])
    
    #finaly we get all user except the last 2 lats users
    return users[:2]
    
def getUserFollowers(user):
    """
    In this function, we
    scrape the top ten prolific
    user from their reviews
    """
    sleep(5)
    first = user+"communaute/"
    driver.get(first)
    sleep(5)
    followers = set()
    followers_page = []
    div_links = []

    nb_follower_div = driver.find_element_by_xpath("//div[@class='inner-nav-item current ']").is_displayed()
    if nb_follower_div:
        nb_follower_text = driver.find_element_by_xpath("//div[@class='inner-nav-item current ']").text
        nb_follower = nb_follower_text.split("(",2)[1].split(")",2)[0]
        nb_follower = int(nb_follower)
        print("NB Followers : ",nb_follower)

        pagination = driver.find_elements_by_xpath("//a[@class='xXx button button-md item']")
        page_links = [elem.get_attribute('href') for elem in pagination]
        page_links.insert(0,first)

        if nb_follower > 0:
            for num in page_links:
                if page_links.index(num) > 0:
                    driver.get(num)
                    sleep(5)
                div_links = driver.find_elements_by_xpath("//a[@class='xXx']")
                followers_page = [elem.get_attribute('href') for elem in div_links]
                for link in followers_page:
                    if link_patterns in link:
                        followers.add(link)
    return followers

def build_final_dataset(users_set,user_cpt,title):
    final_dataset = []
    print("Begin final dataset creation!")
    for user in tqdm(list(users_set)):
        #list of new followers
        followers = []
        #for each user, get his movies and series
        movies , series = getSingleUserData(user.pl)
        #set the user data
        user.data_init(movies,series)
        #browse the user followers and perform the same actions as shown below
        for f in user.fw:
            #create new user
            newUser = User(f,None,"user_" + str(user_cpt))
            user_cpt += 1
            #for each user, get his movies and series
            movies , series = getSingleUserData(f)
            #set the user data
            newUser.data_init(movies,series)
            #add the new user in our final list
            followers.append(newUser)
        #for this user, set his followers
        user.set_followers(followers)
        #add the new user in our final list
        final_dataset.append(user)

    save_dataset(final_dataset,data_path+title)


def getAllUsers(users,all_users,users_set,proceeded_users,max,user_cpt,title):
    followers = []
    user = 0
    movies = []
    series = []
    print("current all_users len : ", len(all_users))
    print("current users_set len : ", len(users_set))
    if len(users_set) <= max:
        for usr in users:
            if usr not in proceeded_users:
                followers = getUserFollowers(usr)
                user = User(usr,list(followers),"user_" + str(user_cpt))
                users_set.add(user)
                proceeded_users.add(usr)
                user_cpt += 1
                for f in followers:
                    all_users.add(f)
                getAllUsers(followers,all_users,users_set,proceeded_users,max,user_cpt,title)
    else:
        build_final_dataset(users_set,user_cpt,title)
        """ Brute force quit """
        exit(1)

def get_all_from_top_ten(title,users,max = 3):
    """ Allow to get all users from top """
    """ ten prolific users """
    """ max  : number of user with related followers """
    getAllUsers(users,all_users,users_set,proceeded_users,max,user_cpt,title)
    for data in users_set:
        print(data.id)

def getData(webSiteLink,x):
    link = webSiteLink + str(x)
    # requete pour ouvrir l'url
    response = requests.get(link)
    person = {}
    # recupere tous le code html
    soup = BeautifulSoup(response.text, 'html.parser')
    divs = soup.findAll("div", {"class": "card entity-card-simple userprofile-entity-card-simple"})

    for d in divs:

        thumbnail1 = d.find("figure", {"class": "thumbnail"})
        title = thumbnail1.find("span").attrs["title"]

        figure = d.find("figure", {"class": "thumbnail"})
        thumbnail_layer = figure.find("div",{"class": "thumbnail-layer"})
        serveral = thumbnail_layer.find("div", {"class": "stareval stareval-medium stareval-theme-default"})

        person[serveral.find('div').attrs['class'][1]] = title
       
    return person

def store_user_data(csv_file,users_data):
    dataset = []
    try:
        with open(csv_file, 'w') as f:
            writer = csv.writer(f)
            for data in users_data:
                for key in data:
                    if [key,data[key]] not in dataset:
                        dataset.append([key,data[key]])
                        writer.writerow([key,data[key]])
    except IOError:
        print("I/O error")

def scrapeUsersData(users):
    for user_link in users:
        users_data = []
        csv_file = "user_" + str(users.index(user_link))
        for i in range(1,11):
            
            films = getData(user_link+"films/?page=",i)
            series = getData(user_link+"series/?page=",i)

            users_data.append(films)
            users_data.append(series)
        
        store_user_data(csv_file,users_data)

def getUsersData(users):
    for user_link in users:
        users_data = []
        csv_file = "user_" + str(users.index(user_link))
        for i in range(1,11):
            
            films = getData(user_link+"films/?page=",i)
            series = getData(user_link+"series/?page=",i)

            users_data.append(films)
            users_data.append(series)
        
    return users_data

def getSingleUserData(user_link):

    for i in range(1,11):
        films = getData(user_link+"films/?page=",i)
        series = getData(user_link+"series/?page=",i)
        
    return films , series