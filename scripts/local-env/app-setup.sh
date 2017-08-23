function prepareApp {
  cd ../$1

  ln -s ../node_modules node_modules
  yarn link mock-server

  # At present, the script only supports OS X and Windows
  # If the user is not on Mac, then user must be Windows (CYGWIN*|MINGW32*|MSYS*)
  if [ "$(uname)" != "Darwin" ]
  then
    info "Setting up ember-cli-windows to improve build performance for: $1"
    ember windows
  fi

  success "$1 is ready to go!"
}

# source in nvm so it can be used
. ~/.nvm/nvm.sh

CWD=$(pwd)
scriptDir="$(dirname $0)"
. $scriptDir/_util.sh

# run install on node build node utilities
info "Running install for node build utilities"
cd $scriptDir/node
yarn

# run install on node build node utilities
info "Running install for all application node packages"
cd ../..
yarn

# mock-server is just Yarn install
info "Running install for mock server"
cd $scriptDir/../mock-server
yarn
yarn link

prepareApp component-lib
prepareApp streaming-data
prepareApp recon
prepareApp style-guide
prepareApp investigate
prepareApp respond
prepareApp context
prepareApp sa

cd $CWD
