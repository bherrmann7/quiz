# Quiz

# THIS PROJECT IS A WORK IN PROGRESS - IE.  Its not working yet

A ClojureScript/Clojure application which presents the user with an image and then 5 choices.
For example, a presidential deck would show a president and ask the user to choose the correct name.

# how to initialize and run project (as a developer)

 - create mysql database
 - edit profiles.clj to add database schema/user/password information
 - run "lein run load" - to load presidents and shapes into database
 - lein figwheel
 - lein run (to start web server)


## History

I started this project on June 25, 2015.   Originally using Om.  However there was no obvious way to transform this
project into a WAR file.   So I restarted it using luminusweb.net with the +war option and converted it into reagent.

## License

Copyright © 2015 Bob Herrmann
