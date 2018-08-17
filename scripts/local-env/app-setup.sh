function prepareApp {
  cd ../$1

  # At present, the script only supports OS X and Windows
  # If the user is not on Mac, then user must be Windows (CYGWIN*|MINGW32*|MSYS*)

  if [ "$(uname)" == "Linux" ] || [ "$(uname)" == "Darwin" ]
  then
    ln -s ../node_modules node_modules
  else
    info "Setting up windows symbolic links for: $1"
    # Unfortunately on Windows, you need to run on elevalted privileges to create symlinks
    # https://blogs.windows.com/buildingapps/2016/12/02/symlinks-windows-10/
    info "Ensure you are running on elevated privileges, else the next command will fail!"
    cmd //c 'mklink /D node_modules ..\node_modules'
    # TO_FIX: ember windows not working because of symbolic links
    # ember windows
  fi

  yarn link mock-server --silent

  success "$1 is ready to go!"
}

# For MAC users source in nvm. For windows users it is a no-op as nvm-windows is already available in the path
if [ "$(uname)" == "Darwin" ] || [ "$(uname)" == "Linux" ]
then
# source in nvm so it can be used
. ~/.nvm/nvm.sh
fi

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

# da-mock-server is also just Yarn install
info "Running install for direct access mock server"
cd ../direct-access/da-mock-server
yarn
# prepareApp expects to be only one folder level deep when it starts
cd ..

prepareApp component-lib
prepareApp rsa-context-menu
prepareApp rsa-data-filters
prepareApp streaming-data
prepareApp packager
prepareApp recon
prepareApp style-guide
prepareApp investigate-shared
prepareApp investigate-events
prepareApp investigate-hosts
prepareApp investigate-files
prepareApp investigate
prepareApp respond
prepareApp configure
prepareApp admin-source-management
prepareApp admin
prepareApp context
prepareApp preferences
prepareApp test-helpers
prepareApp ember-route-action-helper
prepareApp ember-cli-document-title
prepareApp broccoli-theme
prepareApp investigate-process-analysis
prepareApp direct-access
prepareApp sa

cd $CWD
