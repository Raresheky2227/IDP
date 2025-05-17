# IDP
Project for uni
to run locally please use:
mvn clean package -DskipTests
on every package as for now some tests are bugged and will fail.
in gateway run:
docker compose up --build
in frontend(event-manager-frontend) run npm start
port 8081 will be the gateway to them all in localhost
port 8025 for mailhog notifications
port 300 for the frontend
