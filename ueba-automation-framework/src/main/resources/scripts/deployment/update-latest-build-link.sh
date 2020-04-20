#!/bin/bash
set -e

PREVIOUS_BUILD_FILE="/var/lib/nginx/latest-build"
RELOAD_FLAG="/www/data/repo/RSA/UEBA-Repo/builds/reload"

if [[ -f ${RELOAD_FLAG} ]]; then
  [ -f ${PREVIOUS_BUILD_FILE} ] || echo 0 >${PREVIOUS_BUILD_FILE}

  PREVIOUS_BUILD_NUM=$(cat ${PREVIOUS_BUILD_FILE})
  LATEST_BUILD_PATH=$(cd /www/data/repo/RSA/UEBA-Repo/builds/ && ls -l | grep ^d | tail -n 1)
  LATEST_BUILD_NUM=$(echo "${LATEST_BUILD_PATH}" | awk '{print $9}')

  cd /www/data/repo/RSA/UEBA-Repo && rm -f latest && ln -s /www/data/repo/RSA/UEBA-Repo/builds/${LATEST_BUILD_NUM} latest
  echo "${LATEST_BUILD_NUM}" >${PREVIOUS_BUILD_FILE}
  rm -f ${RELOAD_FLAG}
  echo "${PREVIOUS_BUILD_NUM} > ${LATEST_BUILD_NUM}"
fi
