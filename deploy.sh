lein uberjar
lein ring uberwar
scp target/quiz.war root@wilddog.local:.
ssh root@wilddog.local chown tomcat.tomcat /mnt/studio/jadn/quiz.war
ssh root@wilddog.local mv quiz.war /mnt/studio/jadn/quiz.war
ssh root@wilddog.local systemctl restart tomcat

