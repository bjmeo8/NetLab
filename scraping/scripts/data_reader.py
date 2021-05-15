import pickle as pk

# load our dataset
def load_dataset():
    #load with pk
    return pk.load(open("data/1917.pk", "rb"))


print("Begin Movies Recommandation Data Reading Process.")

def show_data():

    dataset = load_dataset()
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

#run
show_data()

print("Ending Movies Recommandation Data Reading Process.")