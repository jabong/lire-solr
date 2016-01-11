
#This is for creating a local maven repository. Given that you have JOpenSurf.jar file with you. If do not have, download from the proper location

mvn install:install-file -Dfile=/home/jabong/devproj/lire-solr/lib/JOpenSurf.jar -DgroupId=com.stromberglabs -DartifactId=jopensurf -Dversion=1.0.0 -Dpackaging=jar

#creating local repository for the lire.jar

mvn install:install-file -Dfile=/home/jabong/devproj/lire-solr/lib/lire.jar -DgroupId=net.semanticmetadata -DartifactId=lire -Dversion=0.9.4-SNAPSHOT -Dpackaging=jar

# for the jhlabs also create the maven repository

mvn install:install-file -Dfile=/home/jabong/devproj/lire-solr/lib/jhlabs.jar -DgroupId=com.jhlabs -DartifactId=image -Dversion=1.7 -Dpackaging=jar

Then for solr, just add the folder or download 4.10 version and remove the data from 
copy the test folder from with the solr and copy that to a new name.
Change the value in core.properties to new value that you have used as schema.

rm -r {pwd}/data/index/*
rm -r {pwd}/data/tlod/*

Then execute command ./post.sh to_be_indexed_file.xml
