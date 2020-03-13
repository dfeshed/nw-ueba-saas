#!/usr/bin/env bash
set -e
############################### uplaoding side branch rpms from jenkins artifacts to S3 bucket ###############################
BUILD_ID=$1
PRESIDIO_EXPECTED_RPMS=(rsa-nw-presidio-configserver rsa-nw-presidio-airflow rsa-nw-presidio-ext-netwitness rsa-nw-presidio-elasticsearch-init rsa-nw-presidio-ui rsa-nw-presidio-manager rsa-nw-presidio-core rsa-nw-presidio-output rsa-nw-presidio-flume)
RPMS_DIR=/tmp/presidio_rpms/
ARTIFACTORY_LINK=http://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-build-jars-and-packages/${BUILD_ID}/artifact/
PRESIDIO_RPMS=()

function downoadRpmsFromJenkins() {
  if [ "$(ls -a /tmp/presidio_rpms)" ]; then
    rm -f $RPMS_DIR/*
  else
    mkdir $RPMS_DIR
  fi

  ########  Download Branch RPMS from Jenkins Artifacts
  wget -q -O- "http://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-build-jars-and-packages/${BUILD_ID}/api/json?tree=artifacts[relativePath]" | python -m json.tool >build_artifacts.json
  grep 'noarch.rpm' build_artifacts.json >build_rpms.txt
  if [ ! -s build_rpms.txt ]; then
    echo "There is no RPM files in the requsted build id"
    exit 1
  fi

  while IFS= read -r line; do
    PRESIDIO_RPMS+=($(echo "$line" | awk -v FS="(relativePath\": \"|rpm\")" '{print $2}'))
  done <build_rpms.txt

  cd $RPMS_DIR
  for i in "${PRESIDIO_RPMS[@]}"; do
    url=$ARTIFACTORY_LINK$i"rpm"
    echo "going to download - $url"
    wget -q $url
  done
}

function uploadRpmsToS3ByAwscli() {
  export AWS_ACCESS_KEY_ID=AKIA3AISCVGC73ZUJTJY
  export AWS_SECRET_ACCESS_KEY=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
  export AWS_DEFAULT_REGION=us-east-1

  cd $RPMS_DIR
  AWS_DIR_NAME=$(ls rsa-nw-presidio-core-*.el7.noarch.rpm | cut -d "-" -f6 | cut -d "." -f1)
  echo 'Creting new directory on S3 - presidio-repo.rsa.com/rpm/$AWS_DIR_NAME/'
  echo $(aws s3api put-object --bucket presidio-repo.rsa.com --key rpm/$AWS_DIR_NAME/)

  for f in $RPMS_DIR/*; do
    echo "going to upload  $f to S3"
    echo "$(aws s3 cp $f s3://presidio-repo.rsa.com/rpm/$AWS_DIR_NAME/)"
  done
}

function uploadRpmsToS3ByUpdateS3YumRepo() {
  export AWS_ACCESS_KEY_ID=AKIA3AISCVGC73ZUJTJY
  export AWS_SECRET_ACCESS_KEY=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
  export AWS_DEFAULT_REGION=us-east-1

  cd $RPMS_DIR
  AWS_DIR_NAME=$(ls rsa-nw-presidio-core-*.el7.noarch.rpm | cut -d "-" -f6 | cut -d "." -f1)
  echo "$(update-s3-yum-repo s3://team-ueba/RSA/UEBA-Repo/builds/$AWS_DIR_NAME/)"

  # lock is requared to syncronize build trigger
  echo " " >lock && aws s3 cp lock s3://team-ueba/RSA/UEBA-Repo/builds/ && rm -f lock
  for f in $RPMS_DIR/*; do
    echo "going to upload  $f to S3"
    echo "$(update-s3-yum-repo s3://team-ueba/RSA/UEBA-Repo/builds/$AWS_DIR_NAME/ $f)"
  done
  aws s3 rm s3://team-ueba/RSA/UEBA-Repo/builds/lock
}

if [[ "$1" == "only_upload" ]]; then
  uploadRpmsToS3ByUpdateS3YumRepo
elif [[ "$1" -ge 1 ]] && [[ "$1" -lt 6666 ]]; then
  downoadRpmsFromJenkins
  uploadRpmsToS3ByUpdateS3YumRepo
else
  echo "script argument should be the build number of presidio-build-jars-and-packages or only_upload for local upload"
  exit 0
fi
