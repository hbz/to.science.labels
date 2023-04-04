#!/bin/bash

if (( $EUID == 0 )); then
    echo "Don't run as root!"
    exit
fi

appDeployDir=$(pwd)
toscienceDir="/opt/toscience"
deployDir="/opt/toscience/git"
targetDir="/opt/toscience/apps"
linkDir="toscience-labels"
fileName="etikett-0.1.0-SNAPSHOT.zip"
folderName="etikett-0.1.0-SNAPSHOT"
newInstallDir="$linkDir.$(date +'%Y%m%d%H%M%S')"
confDir="/etc/toscience/labels"

cp $appDeployDir/target/universal/$fileName $deployDir

cd $deployDir
unzip $fileName

mv $deployDir/$folderName $targetDir/$newInstallDir
mkdir $targetDir/$newInstallDir/logs

if [ -L $toscienceDir/$linkDir ]; then
       	rm $toscienceDir/$linkDir
fi
ln -sf $targetDir/$newInstallDir $toscienceDir/$linkDir
rm -r  $targetDir/$newInstallDir/conf
ln -sf $confDir $targetDir/$newInstallDir/conf

echo ""
echo "Neue Binärversion verfügbar unter $targetDir/$newInstallDir."
echo "Port ist fest eingestellt auf: 9002"
echo "Zum Umschalten auf die neue Version:"
echo "sudo systemctl stop toscience-labels.service"
echo "sudo systemctl start toscience-labels.service"
