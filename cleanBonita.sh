rm -rf ~/bonita
cd ~/workspace/bonita-console/build/bonita_home
mvn clean install -DskipTests
cp -r ./target/bonita_home-6.0-SNAPSHOT/bonita ~/bonita
cp -r ./target/bonita_home-6.0-SNAPSHOT/external ~/bonita/
