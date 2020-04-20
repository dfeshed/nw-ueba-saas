'use strict';

let config = require('./gulp-config');
let version = require('../version.json');

var fs = require('fs');

let configConstTemplate = `(function () {
    'use strict';
    angular.module('Config')
        .constant('VERSION_NUMBER', '${version.number}')
        .constant('VERSION_YEAR', '${version.year}')
        .constant('VERSION_COMPANY', '${version.company}');
}());`;

let filePath = config.srcDir + '/app/config/version.constant.js';

let createVersionFile = function (done) {
    fs.writeFile(filePath, configConstTemplate, function(err) {
        if(err) {
            throw err;
        }

        done();
    });
};

let updatePackageVersion = function (done) {
    // Get package.json
    fs.readFile('package.json', 'utf8', (err, data) => {

        if (err) {
            throw err;
        }

        // Find version
        let verRgx = /("version": ")(.*)(",)/;
        // Replace with new version
        data = data.replace(verRgx, `$1${version.number}$3`);

        // save the file
        fs.writeFile('package.json', data, function(err) {
            if(err) {
                throw err;
            }

            done();
        });
    });
};

module.exports.createVersionFile = createVersionFile;
module.exports.updatePackageVersion = updatePackageVersion;
