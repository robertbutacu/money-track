The backend serves as the connection between the command line and the data.

It requires a connection to an alive local Mongo database - could be changed in the future so it's dynamic.

The best way to use this is to package the project into a jar.
For this, run: sbt package .
This will create the jar in target/scala-2.12/money-track-backend-assembly-0.1.jar, while also including all the
required dependencies.

With the jar created, it's easy to create an alias to run the jar - making the server available at one command away.

To alias the server start-up:
    1. Open up terminal
    2. Go to ~ => cd ~
    3. If it doesn't exist, create .bash_profile .
    4. Open the file with your favourite editor.
    5. Create the alias. For me, that would be:
    alias money-track-server="scala /Users/robert/own-projects/money-track/money-track-backend/target/scala-2.12/money-track-backend-assembly-0.1.jar"
    6. Restart the terminal.

Now, the server should be up and running every time money-track-server is being called.
However, this doesn't ensure that the Mongo database is up as well.