Todo:

Add "loading" elements when a visible element is waiting on a asynchronous
handler.

Re-add subscriptions, but only use simple reactions. Store complex reactionary
computations in the database and compute them as an event using a handler.
Eventually partition the database into a "seed" partition and a "reactionary"
partition where the "reactionary" partition is a pure function of the "seed".

Done:

Highlight the session being edited in the session list

Remove subscriptions, pass data down through components, subscriptions create
a separate reaction on each call which causes redundant computation

Allow handler registration to happen on file load (delete register fn bullshit)

Copyright © 2015 Sebastian Luy
