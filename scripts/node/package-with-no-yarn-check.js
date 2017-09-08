// Get all files passed in
const files = process.argv.splice(2);
const isPackageWithNoYarn =
  files.includes('package.json') && !files.includes('yarn.lock');

console.log(isPackageWithNoYarn);
