#!/bin/bash

## chanded by aquast, 13.09.2022

if (( $EUID == 0 )); then
    echo "Don't run as root!"
    exit
fi

appDeployDir=$(pwd)
deployDir="/opt/toscience/src"
targetDir="/opt/toscience/apps"
linkDir="to.science.labels"
fileName="etikett-0.1.0-SNAPSHOT.zip"
folderName="etikett-0.1.0-SNAPSHOT"
newInstallDir="$linkDir.$(date +'%Y%m%d%H%M%S')"
confDir="/etc/toscience/labels.conf"

cp $appDeployDir/target/universal/$fileName $deployDir

cd $deployDir
unzip $fileName

# cp $targetDir/$linkDir/conf/application.conf $deployDir/$folderName/conf

mv $deployDir/$folderName $targetDir/$newInstallDir
mkdir $targetDir/$newInstallDir/logs

if [ -L $targetDir/$linkDir ]; then
       	rm $targetDir/$linkDir
fi
ln -sf $targetDir/$newInstallDir $targetDir/$linkDir
rm -r  $targetDir/$newInstallDir/conf
ln -sf $confDir $targetDir/$newInstallDir/conf
