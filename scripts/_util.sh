# Utilities for other scripts

GREEN="\033[0;32m"
RED="\033[0;31m"
NC="\033[0m"

function info {
  echo -e "*  $1"
}

function success {
  echo -e "${GREEN}****  $1${NC}"
}

function fail {
  echo -e "${RED}****  $1  ${NC}"
}

function red {
  echo -e "${RED}$1  ${NC}"
}

# if previous command has error, print message and exit
function checkError {
  local status=$?
  if [[ $status != "0" ]]
  then
    fail "$1"
    fail "Exiting..."
    exit $status
  fi
}

function pathExists {
  local _need="false"
  [ -e $1 ] && _need="true"
  echo $_need
}

# variables required for NPM installs to function properly
# Comment these out if running locally
function setWebProxy {
  export http_proxy=http://10.253.136.253:82
}

function unsetWebProxy {
  unset http_proxy
}

function setHttpsProxy {
  export https_proxy=http://10.253.136.253:82
}

function unsetHttpsProxy {
  unset https_proxy
}