var componentLibBower = require('../../component-lib/bower.json')
  , saBower = require('../../sa/bower.json')
  , styleGuideBower = require('../../style-guide/bower.json')
  , allDependencies = {}
  , haveMismatch = false
  ;

// Change into the directory of the script
process.chdir(__dirname);

function arrayUnique(a) {
  return a.reduce(function(p, c) {
    if (p.indexOf(c) < 0) p.push(c);
    return p;
  }, []);
}

function addDependency(n, v) {
  if (!allDependencies[n]) {
    allDependencies[n] = [];
  }
  allDependencies[n].push(v);
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
  dependencyVersions = arrayUnique(dependencyVersions);
  if (dependencyVersions.length > 1) {
    haveMismatch = true;
    var msg =
      '\nDependency mismatch:\n' +
      '  name: ' + dependencyName + '\n' +
      '  versions: ' + dependencyVersions.join(', ');
    console.error(msg);
  }
});

// if there are mismatches between projects, exit, needs resolving
if (haveMismatch) {
  console.error('\nThere are bower dependency mismatches, exiting...\n');
  process.exit(1);
} else {
  console.info("No Bower dependency mismatches.")
}
