kill -9 `ps -ef|grep java|grep -v idea|grep -v dbVis|grep -v grep|cut -c9-14`
