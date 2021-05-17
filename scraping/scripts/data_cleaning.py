import pickle as pk
from netlab import top_ten_movies
from tqdm import tqdm

#Get top ten movies
ttm = top_ten_movies("../backups/topten.txt")
ttm = [(str(x)).split("\n")[0] for x in ttm]
#All movies
movies = set()
series = set()
final_dataset = set()

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

class Fuser:
    """
    Final user class
    comments goes here
    """

    def __init__(self, page_link, followers, name):
        self.id = page_link.split("membre-",2)[1].split("/",3)[0]
        self.pl = page_link
        self.fw = followers
        self.name = name

    def data_init(self, movies, series, mov_ratings, ser_ratings):
        self.movi = movies
        self.seri = series
        self.mov_rat = mov_ratings
        self.ser_rat = ser_ratings
    
    def set_id(self, u_id):
        self.id = u_id

    def set_page_link(self, page_link):
        self.pl = page_link
    
    def set_followers(self, followers):
        self.fw = followers
    
    def set_name(self, name):
        self.name = name
    

#Load our dataset
def load_dataset(path):
    return pk.load(open(path, "rb"))

# save our final dataset
def save_dataset(dataset,path):
    #save our encoder
    pk.dump(dataset, open(path + ".pk", "wb"))

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

    save_dataset(final_dataset,"final_data/final_dataset")
    save_dataset(movies,"final_data/movies")
    save_dataset(series,"final_data/series")

def read_final_dataset():

    dataset = load_dataset("final_data/final_dataset.pk")
    file = open("final_data/users.txt", "w") 
    total = 0
    data = set()
    for m in dataset:
        # print(m.pl)
        data.add(m.pl)
        total += 1
        if m.fw is not None:
            total += len(m.fw)
            for f in m.fw:
                data.add(f.pl)

    for m in data:
        file.write(m+"\n")

    file.close()

print("Begin Movies Recommandation Data Reading Process.")
#clean data
# clean_data()
#read data
read_final_dataset()
print("Begin Movies Recommandation Data Reading Process.")