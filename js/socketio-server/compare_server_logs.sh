# script for comparing logs produced by
#
# DEBUG="*"" node server.js
#
# when executing from different clients to verify that the connection, disconnection and message exchange
# is the same.
#

# In the example below we compare the logs produced by postman and gatling
# Copy the relevant logs from the terminal to the files connection_postman.log and connection_gatling.log

# remove the time at the end of each line
# replace the socket id with XXXXXXXXXXXXXXXXXXXX
sed "s/\+[0-9]*[m]*s$//g" connection_postman.log | sed 's/[A-Za-z0-9_-]\{20\}/XXXXXXXXXXXXXXXXXX/g' > connection_postman_.log
sed "s/\+[0-9]*[m]*s$//g" connection_gatling.log | sed 's/[A-Za-z0-9_-]\{20\}/XXXXXXXXXXXXXXXXXX/g' > connection_gatling_.log
diff connection_postman_.log connection_gatling_.log
