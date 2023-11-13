#!/bin/bash

if (( $EUID == 0 )); then
    echo "Don't run as root!"
    exit
fi

export TERM=xterm-color
deployingApp="to.science.labels"
# branch=$(git status | grep branch | cut -d ' ' -f3)
# echo "git currently on branch: "$branch
# if [ ! -z "$1" ]; then
#     branch="$1"
# fi

cd /opt/toscience/git/$deployingApp
#git pull origin $branch
/opt/toscience/activator/activator dist
