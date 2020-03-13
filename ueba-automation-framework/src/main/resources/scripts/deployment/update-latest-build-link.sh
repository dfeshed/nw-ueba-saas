#!/bin/bash
set -e

PREVIOUS_BUILD_FILE="/var/lib/nginx/latest-build"
UPLOAD_LOCK_FILE="/www/data/repo/RSA/UEBA-Repo/builds/lock"

cd /var/lib/nginx/
[ -f ${PREVIOUS_BUILD_FILE} ] || echo 0 >${PREVIOUS_BUILD_FILE}

PREVIOUS_BUILD_NUM=$(cat ${PREVIOUS_BUILD_FILE})
LATEST_BUILD_PATH=$(cd /www/data/repo/RSA/UEBA-Repo/builds/ && ls -lt | grep ^d | tail -n 1)
LATEST_BUILD_NUM=$(echo "${LATEST_BUILD_PATH}" | awk '{print $9}')

if [[ ${LATEST_BUILD_NUM} -ne ${PREVIOUS_BUILD_NUM} ]] && [[ ! -f ${UPLOAD_LOCK_FILE} ]]; then
  cd /www/data/repo/RSA/UEBA-Repo && rm -f latest && ln -s /www/data/repo/RSA/UEBA-Repo/builds/${LATEST_BUILD_NUM} latest
  echo "${LATEST_BUILD_NUM}" >${PREVIOUS_BUILD_FILE}
  echo "${PREVIOUS_BUILD_NUM} > ${LATEST_BUILD_NUM}"
fi
