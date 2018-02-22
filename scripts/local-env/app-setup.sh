function prepareApp {
  cd ../$1

  # At present, the script only supports OS X and Windows
  # If the user is not on Mac, then user must be Windows (CYGWIN*|MINGW32*|MSYS*)
  if [ "$(uname)" != "Darwin" ]
  then
    info "Setting up windows symbolic links for: $1"
    # Unfortunately on Windows, you need to run on elevalted privileges to create symlinks
    # https://blogs.windows.com/buildingapps/2016/12/02/symlinks-windows-10/
    info "Ensure you are running on elevated privileges, else the next command will fail!"
    cmd //c 'mklink /D node_modules ..\node_modules'
    # TO_FIX: ember windows not working because of symbolic links
    # ember windows
  else
    ln -s ../node_modules node_modules
  fi

  yarn link mock-server --silent

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
cd $CWD

# mock-server is just Yarn install
info "Running install for mock server"
cd $scriptDir/../mock-server
yarn
yarn link

prepareApp component-lib
prepareApp streaming-data
prepareApp hosts-scan-configure
prepareApp packager
prepareApp recon
prepareApp style-guide
prepareApp investigate-events
prepareApp investigate-hosts
prepareApp investigate-files
prepareApp respond
prepareApp configure
prepareApp context
prepareApp preferences
prepareApp test-helpers
prepareApp broccoli-theme
prepareApp sa

cd $CWD
