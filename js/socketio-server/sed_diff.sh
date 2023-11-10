sed "s/\+[0-9]*[m]*s$//g" connection_postman.log | sed 's/[A-Za-z0-9_-]\{20\}/XXXXXXXXXXXXXXXXXX/g' > connection_postman_.log
sed "s/\+[0-9]*[m]*s$//g" connection_gatling.log | sed 's/[A-Za-z0-9_-]\{20\}/XXXXXXXXXXXXXXXXXX/g' > connection_gatling_.log
diff connection_postman_.log connection_gatling_.log
