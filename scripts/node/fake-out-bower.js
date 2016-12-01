// This file will take a project's NPM installed directories
// and put them in that project's Bower output directories
// making it appear as if Bower installed them.

// This is necessary as a shim for those ember CLI
// plugins that demand their client libraries be
// Bower'd.

// Example execution:
//
// node fake-out-bower jquery faker,Faker
//
// The arguments are a space separated list of
// libraries to "fake". If a libraries NPM install
// and expected Bower install have different
// root directories names, the library name can be
// comma-separated with the NPM name first and
// the Bower name second.

const join = require('path').join;
const fs = require('fs-extra');

const libraries = process.argv.splice(2);
const CWD = process.cwd();
const bowerRoot = join(CWD, "bower_components");
const nodeRoot = join(CWD, "node_modules");

fs.ensureDirSync(bowerRoot);

libraries.forEach((lib) => {
  let [libInput, libOutput] = lib.split(',');

  // if same name for input output
  libOutput = libOutput || lib;

  const nodeDir = join(nodeRoot, libInput);
  const bowerDir = join(bowerRoot, libOutput);

  // Remove existing directory
  fs.removeSync(bowerDir);

  // copy contents to new location
  fs.copySync(nodeDir, bowerDir)

  console.log(`Copied ${nodeDir} to ${bowerDir}`);
});
