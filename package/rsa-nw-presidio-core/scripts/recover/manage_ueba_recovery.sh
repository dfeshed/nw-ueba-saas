#!/bin/bash
#
# Command-line utility to backup and restore ueba data.
#

# statics
BACKUP_HOME="/etc/elasticsearch/backup"
REST_API_LOCATION="http://localhost:9200"
ARCHIVE_API_ENDPOINT="_snapshot/ueba_backup"
CURL_SILENT="curl --silent --output /dev/stderr"
CURL_VERBOSE="curl -v --show-error"
REDIS_DIR="/var/lib/redis"

# source nwcommon functions
. /usr/lib/netwitness/bootstrap/resources/nwcommon || exit 2

# ---------------------------------------------------
# usage
# ---------------------------------------------------
usage() {
  echoUsage "Backup/Restore UEBA data"
  echoUsage ""
  echoUsage "Usage:"
  echoUsage " $(basename ${0}) --dump-dir <dump directory> --mode <backup|restore>"
  echoUsage "Options:"
  echoUsage " -v, --verbose                 Enable verbose output"
  echoUsage " --mode <backup|restore>       Specify mode [ backup|restore ]"
  echoUsage " --dump-dir <dir>              Specify dump location"
  echoUsage ""
  echoUsage ""
  exit 1
}

# ---------------------------------------------------
# checkSystem
# ---------------------------------------------------
checkSystem() {
  # verify root/sudo user is calling script
  isRootUser || exitError "Install requires root privileges."
}

create_snapshot()
{
cat <<EOF
  {
   "type": "fs",
   "settings": {
       "compress" : true,
       "location" : "${BACKUP_HOME}"
   }
  }
EOF
}

# ---------------------------------------------------
# backup
# ---------------------------------------------------
backup() {
  echoInfo "Backing up UEBA data..."
  # If we don't have a backup yet
  if ! [ -d "${UEBA_DUMP_DIR}" ]; then
     # Make a directory
     mkdir -p "${UEBA_DUMP_DIR}"
  else
     #clear the dump directory
     rm -rf "${UEBA_DUMP_DIR:?}"/*
  fi

  # clear the BACKUP_HOME dir
  rm -rf "${BACKUP_HOME:?}"/*


  echoDebug "dump dir = ${DUMP_DIR}"

  ${CURL_COMMAND} \
    -H "Accept: application/json" \
    -H "Content-Type:application/json" \
    -X POST --data "$(create_snapshot)" "${REST_API_LOCATION}/${ARCHIVE_API_ENDPOINT}" ||
    exitError "snapshot creation failed"

  ${CURL_COMMAND} \
    -X PUT "${REST_API_LOCATION}/${ARCHIVE_API_ENDPOINT}/snapshot_ueba?wait_for_completion=true" ||
    exitError "backup to snapshot failed"


  mv "${BACKUP_HOME}"/* "${UEBA_DUMP_DIR}"

  mv "${REDIS_DIR}"/* "${UEBA_DUMP_DIR}"

  echoInfo "- Successfully backed-up REDIS DATA -"
  echoInfo ""
  echoInfo "------------------------------------"
  echoInfo "- Successfully backed-up UEBA DATA -"
  echoInfo "------------------------------------"
}

# ---------------------------------------------------
# restore
# ---------------------------------------------------
restore() {
  # check for data in ueba dump dir
  if ! [ -d "${UEBA_DUMP_DIR}" ]; then
    exitError "UEBA Backup directory not found."
  else
    rm -rf "${BACKUP_HOME:?}"/*
    cp -rp "${UEBA_DUMP_DIR}"/* "${BACKUP_HOME}"
  fi

  #create snapshot fs if it doesn't exist
  ${CURL_COMMAND} \
    -H "Accept: application/json" \
    -H "Content-Type:application/json" \
    -X POST --data "$(create_snapshot)" "${REST_API_LOCATION}/${ARCHIVE_API_ENDPOINT}" ||
    exitError "snapshot creation failed"

  # close open indexes
  ${CURL_COMMAND} \
    -X POST "${REST_API_LOCATION}/_all/_close"

  # restore logic
  echoInfo "Restoring UEBA data"
  ${CURL_COMMAND} \
    -X POST "${REST_API_LOCATION}/${ARCHIVE_API_ENDPOINT}/snapshot_ueba/_restore?wait_for_completion=true" ||
    exitError "restore from snapshot failed"

  # Redistribute the configuration server parameters
  echoInfo "Redistributing the configuration server parameters"
  ${CURL_COMMAND} \
    -X PATCH http://localhost:8881/configuration \
    -H "Cache-Control: no-cache" \
    -H "Content-Type: application/json" \
    -d "{\"operations\":[]}" ||
    exitError "Redistributing the configuration server parameters failed"

  mv "${UEBA_DUMP_DIR}"/*.rdb "${REDIS_DIR}"

  echoInfo "- Successfully restored REDIS DATA -"
  echoInfo ""
  echoInfo "-----------------------------------"
  echoInfo "- Successfully restored UEBA DATA -"
  echoInfo "-----------------------------------"
}

# consume arguments
OPTS=$(getopt -o v --long help,verbose,mode:,dump-dir: -n 'manage-ueba-data' -- "$@")

# check for argument parse errors
if [ $? != 0 ]; then
  echo "Terminating..." >&2
  exit 1
fi

# normalize arguments
eval set -- "${OPTS}"

# process arguments
while true; do
  case "${1}" in
    -h | --help)
      usage
      ;;
    -v | --verbose)
      VERBOSE=true
      shift
      ;;
    --mode)
      MODE=${2}
      shift 2
      ;;
    --dump-dir)
      DUMP_DIR=${2}
      shift 2
      ;;
    --)
      shift
      break
      ;;
    *)
      break
      ;;
  esac
done

if [ -z "${DUMP_DIR}" ]; then
    exitUsage "please specify the dump directory"
else
    UEBA_DUMP_DIR="${DUMP_DIR}/ueba"
fi

if [ -z "${MODE}" ]; then
    exitUsage "please specify the mode [ backup|restore ]"
fi

if [ -z "${VERBOSE}" ]; then
    CURL_COMMAND="${CURL_SILENT} -i --fail"
else
    CURL_COMMAND="${CURL_VERBOSE} -i --fail"
fi

case "${MODE}" in
    backup)
        backup
        ;;
    restore)
        restore
        ;;
    *)
        exitUsage "unknown mode: ${MODE}"
        ;;
esac

exit 0