# Quiz

A ClojureScript/Clojure application which presents the user with an image and then 5 choices.
For example, a presidential deck would show a president and ask the user to choose the correct name.

# The Story

I wanted to tinker with clojure and clojurescript.  I thought a quiz app would be good for learning stuff.

# How to Initialize and Run Project (as a developer)

 - create mysql database
      ON OSX
      $ brew install mysql
      $ mysql.server start
      $ mysql -uroot
      mysql> create database quiz;
      mysql> GRANT ALL ON quiz.* TO 'quiz'@'localhost' identified by 'quiz';
      ## Not Needed ...  TODO ... only production? $ export QUIZ_DATABASEURL="
      
 - edit profiles.clj to add database schema/user/password information
     :profiles/dev  {:env {:quiz-database-url "jdbc:mysql://localhost:3306/quiz?user=quiz&password=quiz"}}
   
 - create initial schema
      $ lein run migrate
        
 - load initial decks
      $ lein run load-all-decks # to load the sample decks into the databse
      
 - lein figwheel
 - lein run (to start web server)

## History

I started this project on June 25, 2015.   Originally using Om.  However there was no obvious way to transform this
project into a WAR file.   So I restarted it using luminusweb.net with the +war option and converted it into reagent.

## License

Copyright © 2015 Bob Herrmann

The sample decks are lifted from various places - I do not have the copyrights for any of those images.   The presidents
were take from wikipedia.   The other images from google image searches.

