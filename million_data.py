from faker import Faker
import mysql.connector
import random

conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Sk12sms36!",
    database="expert"
)

fake = Faker()

num_records = 1000000

unique_emails = set()
unique_nicknames = set()
data = []

while len(unique_emails) < num_records:
    email = fake.unique.email()
    nickname = fake.unique.user_name()  
    password = fake.password(length=10)
    user_role = random.choice(["ADMIN", "USER"])
    data.append((nickname, email, password, user_role))
    unique_emails.add(email)
    unique_nicknames.add(nickname)

cursor = conn.cursor()

batch_size = 10000
for i in range(0, len(data), batch_size):
    batch = data[i:i + batch_size]
    cursor.executemany("""
        INSERT IGNORE INTO users (nickname, email, password, user_role) 
        VALUES (%s, %s, %s, %s);
    """, batch)
    conn.commit()

print("삽입 성공")
cursor.close()
conn.close()