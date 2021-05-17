import pickle as pk
import csv
from tqdm import tqdm

kusers = []

class Kaggle:
    """
    Kaggle user class
    id : the kaggle user id
    name : a generated name
    ratings : all user ratings ([Str , Float , LongInt])
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
    data = load_dataset("../final_data/kusers.pk")
    for ku in data:
        print("name : " + ku.name + " id : " + ku.id)
        for r in ku.ratings:
            print("     rating : " + str(r))

#launch
# load_kaggle_dataset()
read_dataset()