ENDPOINT=${1:-"ladder"}
WEBAPP_TARGET=$CATALINA_BASE/webapps/$ENDPOINT
sudo mkdir -p $WEBAPP_TARGET
lein ring uberwar
sudo cp target/elo-sport-1.0.0-SNAPSHOT-standalone.war $WEBAPP_TARGET/root.war
