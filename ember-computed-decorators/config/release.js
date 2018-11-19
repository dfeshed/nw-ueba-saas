var execSync = require('child_process').execSync;

module.exports = {
  // Publish the new release to NPM after a successful push
  afterPush: function() {
    var output = execSync('npm publish', { encoding: 'utf8' });
    console.log(output);
  }
};
