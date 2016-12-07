const findSubmodules = require('./lib/find-submodules');
const toInstallConfig = require('./lib/submodule-config').toInstallConfig;

const submods = findSubmodules(toInstallConfig);

// console.log it, this essentially passes it
// to the bash script that called it
console.log('|' + submods.join('|') + '|');