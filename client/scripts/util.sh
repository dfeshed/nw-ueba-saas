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

function checkError {
  if [[ $? != "0" ]]
  then
    fail "$1"
    fail "Exiting..."
    exit
  fi
}