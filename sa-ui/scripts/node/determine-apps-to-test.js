const findSubmodules = require('./lib/find-submodules');
const toTestConfig = require('./lib/submodule-config').toTestConfig;

const submods = findSubmodules(toTestConfig);

// console.log it, this essentially passes it
// to the bash script that called it
console.log('|' + submods.join('|') + '|');