# Build odcker images and push to dtr2

VERSION=11.2.0
DTR=asoc-dtr2.rsa.lab.emc.com
SA_RPM_FOLDER=$SA_RPM_ROOT/RPMS/noarch
echo "RPM folder $SA_RPM_FOLDER"
unset -v FULL_RPM_FILE_NAME
for file in "$SA_RPM_FOLDER"/*.rpm; do
  [[ $file -nt $latest ]] && FULL_RPM_FILE_NAME=$file
done
echo "FULL_RPM_FILE_NAME $FULL_RPM_FILE_NAME"
cp $FULL_RPM_FILE_NAME .
RPM_FILE_NAME=$(basename $FULL_RPM_FILE_NAME)

# if not a master build tag will be just Git hash
SA_UI_IMAGE_TAG=${VERSION}-latest
if [ "${IS_MASTER_BUILD}" != "true" ]
then
  SA_UI_IMAGE_TAG=${GIT_COMMIT:0:10}
fi
export SA_UI_IMAGE_TAG
echo "SA-UI Docker image tag is ${SA_UI_IMAGE_TAG}"
echo "Latest file: " $FULL_RPM_FILE_NAME
echo "started building sa-ui images..."
docker build -f ./scripts/jenkins/docker/Dockerfile --no-cache --rm --build-arg DTR=${DTR} --build-arg RPM_FILE_NAME=${RPM_FILE_NAME} -t ${DTR}/rsa/rsa-nw-ui:${SA_UI_IMAGE_TAG} .

echo "Finished building rsa-nw-ui image..."
echo "Pushing rsa-nw-ui image..."
docker push ${DTR}/rsa/rsa-nw-ui:${SA_UI_IMAGE_TAG}
