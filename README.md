# Quiz

A WORK IN PROGRESS - IE.  Its not working yet

A ClojureScript/Clojure application which presents the user with an image and then 5 choices.
For example, a presidential deck would show a president and ask the user to choose the correct name.

## Architecture

The frontend uses ClojureScript and Om and has a single atom for managing state.
The Clojure backend uses mysql to keep track of players and their progress through a deck.

I'm still exploring various ways to message between ClojureScript and Clojure, at the moment
I'm using POST to send EDN back and forth.   I'm trying to decide if I should have a single
message with a distinctive :type, or seperate REST endpoints for each message type.
 
## To Do/Ideas

  - create a demo site
  
  - sprinkle some bootstrap on this application so it doesnt look so naked
  - figure out a light weight way for users to create/manage accounts or annon long sessions
  - consider various game options (play util perfect deck achieved? play 20)
  - replay mistakes more often (to reinforce?) 
  
  - should their be a leader board?  (email challenge to group?)
  - show other concurrent users? 
  
  - offer multiple decks (presidents, states, works of art?)  
  - play multiple decks together (presidents and prime ministers)
  - have a way to see history?
  
  - https?
  
  - expert mode where users type in answers
  - not image, but play sound (ie for spelling tests)  
  
  - collecting and sharing of decks?
  - extra information about entries (ie. baseball card style)

## Development

```
$ lein repl
user=> (run)
user=>(browser-repl)
```

The call to `(run)` does two things, it starts the webserver at port
10555, and also the Figwheel server which takes care of live reloading
ClojureScript code and CSS. Give them some time to start.

Running `(browser-repl)` starts the Weasel REPL server, and drops you
into a ClojureScript REPL. Evaluating expressions here will only work
once you've loaded the page, so the browser can connect to Weasel.

When you see the line `Successfully compiled "resources/public/app.js"
in 21.36 seconds.`, you're ready to go. Browse to
`http://localhost:10555` and enjoy.


## License

Copyright © 2014,2015 Bob Herrmann

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
