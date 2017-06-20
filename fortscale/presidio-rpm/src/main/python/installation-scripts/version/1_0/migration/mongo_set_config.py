import os

os.system('mongo localhost:27017/admin ../utils/mongo_create_root')
os.system('mongo localhost:27017/presidio ../utils/mongo_create_presidio_admin')
os.system('systemctl stop mongod.service')
os.system('mv /etc/mongod.conf /etc/mongod.conf.no_auth')
os.system('cp ../utils/mongod.conf /etc/mongod.conf.auth')
os.system('cp ../utils/mongod.conf /etc/mongod.conf')
os.system('systemctl start mongod.service')