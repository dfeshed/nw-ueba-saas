var fs = require('fs')
  , execSync = require('child_process').execSync
  , dateformat = require('dateformat')
  , rimraf = require('rimraf')
  , bower = require('bower')
  , chalk = require('chalk')
  , uniq = require('lodash.uniq')
  , extend = require('extend')
  , componentLibBower = require('../../component-lib/bower.json')
  , saBower = require('../../sa/bower.json')
  , styleGuideBower = require('../../style-guide/bower.json')
  , allDependencies = {}
  , finalDependencyList = []
  , finalDependencyListJson = {}
  , haveMismatch = false
  ;

// Change into the directory of the script
process.chdir(__dirname);

function addDependency(n, v) {
  if (!allDependencies[n]) {
    allDependencies[n] = [];
  }
  allDependencies[n].push(v);
}

function bowerDone(results){
  var now = new Date()
    , archiveOutPath
    , dateTime
    ;

  console.log(chalk.green('\nBower install completed successfully'));
  console.log('\nCreating archive file...');
  fs.mkdirSync("dist");

  dateTime = dateformat(now, "yyyyddmm.hhMMss");
  // leaving the version/stability hard coded in hopes that
  // we'll either be off bower or off this hacky bower-registry solution
  // before the version ticks up
  archiveOutPath = './dist/bower-registry-11.0.0.0-' + dateTime + '-1.jar'
  execSync('jar cf ' + archiveOutPath  + ' ./bower_repository');
  console.log(chalk.green('\nArchive file created'));

  // TODO, curl to Artifactory
  console.log('\nCleaning up...');
  rimraf.sync('bower_components')
  rimraf.sync('bower_repository')
  console.log(chalk.green('\nDONE!'));
}

function addDependencies(deps) {
  if (deps) {
    Object.keys(deps).forEach(function(dependencyName) {
      var dependencyVersion = deps[dependencyName];
      addDependency(dependencyName, dependencyVersion);
    });
  }
}

[componentLibBower, saBower, styleGuideBower].forEach(function(bowerJson) {
  addDependencies(bowerJson.dependencies);
  addDependencies(bowerJson.devDependencies);
});

// Determine if there are dependency mismatches between projects
Object.keys(allDependencies).forEach(function(dependencyName) {
  var dependencyVersions = allDependencies[dependencyName];
  dependencyVersions = uniq(dependencyVersions);
  if (dependencyVersions.length > 1) {
    haveMismatch = true;
    var msg =
      '\nDependency mismatch:\n' +
      '  name: ' + chalk.red(dependencyName) + '\n' +
      '  versions: ' + chalk.red(dependencyVersions.join(', '));
    console.error(msg);
  } else {
    finalDependencyListJson[dependencyName] = dependencyVersions[0];
    finalDependencyList.push(dependencyName + '#' + dependencyVersions[0]);
  }
});

// if there are mismatches between projects, exit, needs resolving
if (haveMismatch) {
  console.error('\nWill not create bower Archive file, please resolve dependency mismatches.\n');
  process.exit(1);
}

rimraf.sync('dist');
rimraf.sync('bower_components');
rimraf.sync('bower_repository');

// console.log(finalDependencyList)
// console.log(finalDependencyListJson)

console.log('\nRunning \'bower install\'...');
bower.commands.install(finalDependencyList).on('end', bowerDone);
