import pickle as pk

# load our dataset
def load_dataset(path):
    #load with pk
    return pk.load(open(path, "rb"))

def show_data():

    dataset = load_dataset("data/1917.pk")
    total = 0
    for user in dataset:

        print("id : ",user.id)
        print("name : ",user.name)
        print("page link : ",user.pl)
        total += 1 + len(user.fw)
        for movi,seri in zip(user.movi,user.seri):
            print("movie : ",[movi,user.movi[movi]])
            print("serie : ",[seri,user.seri[seri]])
        
        for f in user.fw:
            print("follower : ",f.pl,"\n")
            for mo,se in zip(f.movi,f.seri):
                print("movie : ",[mo,f.movi[mo]])
                print("serie : ",[se,f.seri[se]])
            

    print("total",total,"\n")

print("Begin Movies Recommandation Data Reading Process.")
#run
show_data()
print("Ending Movies Recommandation Data Reading Process.")