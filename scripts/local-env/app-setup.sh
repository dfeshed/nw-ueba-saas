# This can be run to quickly NPM/Bower install
# all the dependencies for all the apps

function installNPMDeps {
  info "Installing NPM dependencies for: $1"
  npm install
  checkError "Failed to install NPM dependencies for $1, try again, if this persists (it shouldn't) then get some help."
}

function installAppDeps {
  cd ../$1

  installNPMDeps $1

  info "Installing Bower dependencies for: $1"
  bower install
  checkError "Failed to install Bower dependencies for $1, try again, if this persists (it shouldn't) then get some help."

  success "$1 is ready to go!"
}

# source in nvm so it can be used
. ~/.nvm/nvm.sh

CWD=$(pwd)
scriptDir="$(dirname $0)"
cd $scriptDir

. _util.sh

# mock-server is just NPM
cd ../mock-server
installNPMDeps mock-server

installAppDeps component-lib
installAppDeps streaming-data
installAppDeps recon
installAppDeps style-guide
installAppDeps sa

cd $CWD
