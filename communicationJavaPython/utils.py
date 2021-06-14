import mysql.connector
import pickle as pk

conn = 0
cursor = 0


def open_conn(database):
    global conn, cursor
    config = {
        'user': 'root',
        'password': '',
        'host': 'localhost',
        'port': 3306,
        'database': database,
        'raise_on_warnings': True
    }

    conn = mysql.connector.connect(**config)
    cursor = conn.cursor(buffered=True)
    # print("connected!")

    return conn, cursor


def close_conn():
    global conn, cursor
    cursor.close()
    conn.close()


# print("closed!")

# Load our dataset
def load_dataset(path):
    return pk.load(open(path, "rb"))


# save our final dataset
def save_dataset(dataset, path):
    # save our encoder
    pk.dump(dataset, open(path, "wb"))