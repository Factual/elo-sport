ENDPOINT=${1:-"ladder"}
WEBAPP_TARGET=$CATALINA_BASE/webapps/$ENDPOINT
sudo mkdir -p $WEBAPP_TARGET
LEIN_ROOT=true lein ring uberwar
rm -f $WEBAPP_TARGET/root.war
cp target/elo-sport-1.0.0-SNAPSHOT-standalone.war $WEBAPP_TARGET/root.war
cd $WEBAPP_TARGET
jar -xf root.war
rm root.war
