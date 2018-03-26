const fs = require('fs-extra');

const _generatePaths = (submodule) => {
  const localPath = path.join(__dirname, '../..', submodule, '/coverage');
  const cachePath = `/mnt/libhq-SA/SAStyle/sa-ui-coverage/${submodule}`;
  return { localPath, cachePath };
};

// Copies the coverage directory in the mount to the jenkins workspace
module.exports.mount_to_ws = (submodule) => {
  const { localPath, cachePath } = _generatePaths(submodule);
  fs.ensureDirSync(cachePath);

  try {
    fs.copySync(cachePath, localPath);
    console.log(`***** mount to ws copy success for '${submodule}'`);
  } catch (err) {
    console.error(err);
  }
};

// Copies the coverage directory in the workspace to the mount
module.exports.ws_to_mount = (submodule) => {
  const { localPath, cachePath } = _generatePaths(submodule);
  // delete old lcov files in the mount location
  fs.emptydirSync(cachePath);

  try {
    fs.copySync(localPath, cachePath);
    console.log(`***** ws to mount copy success for '${submodule}'`);
  } catch (err) {
    console.error(err);
  }
};