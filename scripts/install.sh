#!/bin/bash


# export tc=/home/xcri/apache-tomcat-7.0.23/bin
export tc=~/apache-tomcat

cd $tc/bin
# ./shutdown sh
rm -Rf ../webapps/FeedManager ../webapps/HandlerRegistry ../webapps/repository ../webapps/discover
rm -Rf ../logs/*
cd ~
rm -Rf xcri_src
mkdir xcri_src
cd xcri_src
git clone git@github.com:k-int/XCRI-Aggregator.git
git clone git@github.com:k-int/AggregatorCore.git


cd ~/xcri_src/aggregator
git checkout release
git pull
cd ~/xcri_src/aggregator/HandlerRegistry
grails prod war
cd ~/xcri_src/aggregator/repository
grails prod war


cd ~/xcri_src/XCRI-Aggregator
git checkout release
git pull
cd ~/xcri_src/XCRI-Aggregator/XCRISearch
grails prod war
cd ~/xcri_src/XCRI-Aggregator/FeedManager
grails prod war


cp ~/xcri_src/XCRI-Aggregator/FeedManager/target/FeedManager-0.1.war $tc/webapps/FeedManager.war
cp ~/xcri_src/aggregator/repository/target/repository-0.1.war $tc/webapps/repository.war
cp ~/xcri_src/aggregator/HandlerRegistry/target/HandlerRegistry-0.1.war $tc/webapps/HandlerRegistry.war
cp ~/xcri_src/XCRI-Aggregator/XCRISearch/target/XCRISearch-0.1.war $tc/webapps/discover.war

