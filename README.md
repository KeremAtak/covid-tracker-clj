# covid-tracker-cljs

This application serves the backend for the covid-tracker-cljs.

The application fetches COVID data from THL's api, parses it and delivers it to the project's frontend.

This application is written in Clojure.

# Installation instructions

Have lein installed. Then run `lein install` to install packages.

Start the application with `lein run`. The application will start at http://localhost:3000/.

The tests are run with `lein test`, or with hot reloading run `lein test-refresh`

# Links

Heroku backend: https://covid-tracker-clj.herokuapp.com/swagger-ui/index.html

Heroku frontend: https://covid-tracker-cljs.herokuapp.com/

Project's frontend: https://github.com/KeremAtak/covid-tracker-cljs
