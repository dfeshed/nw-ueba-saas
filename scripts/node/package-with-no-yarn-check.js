// Get all files passed in
var isPackageWithNoYarn = false;
const files = process.argv.splice(2);

const packageJsonFiles = files.filter(function(file) {
  // find package.json files
  return file.endsWith('/package.json');
}).map(function(file) {
  // build corresponding yarn.lock paths
  return file.replace(/(package.json)$/, 'yarn.lock');
}).forEach(function(yarnFile) {
  // that yarn file in there?
  if (files.indexOf(yarnFile) === -1) {
    isPackageWithNoYarn=true;
  }
});

console.log(isPackageWithNoYarn);
