# This can be run to quickly NPM/Bower install
# all the dependencies for all the apps

function installAppDeps {
  cd ../$1
  
  info "Installing NPM dependencies for: $1"
  npm install
  checkError "Failed to install NPM dependencies for $1, try again, if this persists (it shouldn't) then get some help."

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

installAppDeps component-lib
installAppDeps style-guide
installAppDeps sa

cd $CWD