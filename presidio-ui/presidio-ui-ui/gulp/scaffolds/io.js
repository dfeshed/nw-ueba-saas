'use strict';

let Q = require('q');
let fs = require('fs');
let path = require('path');

let config = require('./config');
let output = process.stdout;
let chp = require('child_process');

/**
 * Returns a promise that is resolved on the user input
 *
 * @param {string} text
 * @param {string=} enc Defaults to 'utf8'
 * @returns {*}
 */
module.exports.inp = function inp (text, enc) {

    enc = enc || 'utf8';

    return Q.Promise(function (resolve) {
        let stdin = process.stdin, stdout = process.stdout;

        stdin.resume();
        process.stdin.setEncoding(enc);
        stdout.write(`${text} `);

        stdin.once('data', function (data) {
            resolve(data.toString().trim());
        });
    });
};

/**
 *
 * @param {string} filePath
 * @param {Array=} fileNamesArray
 * @returns {Array}
 */
module.exports.getAllJsFilesSync = function getAllJsFilesSync (filePath, fileNamesArray) {

    // Create an empty array if one was not provided
    fileNamesArray = fileNamesArray || [];

    // Create a files map
    let filesMap = new Map();
    let names = fs.readdirSync(filePath);
    // Iterate through directory names and for each name get the stat and put in map
    names.map(name => fs.statSync(path.resolve(filePath, name)))
        .forEach((stat, index) => {
            filesMap.set(names[index], stat);
        });

    // Iterate through map and if its a javascript file, add it to a list of file names. If directory, run
    // getAllJsFiles recursively on the directory
    filesMap.forEach((stat, name) => {
        if (stat.isFile() && /\.js$/.test(name) && !/spec\.js$/.test(name)) {
            fileNamesArray.push(path.resolve(filePath, name));
        }

        if (stat.isDirectory()) {
            getAllJsFilesSync(path.resolve(filePath, name), fileNamesArray);
        }
    });

    return fileNamesArray;
};

/**
 * Returns a promise that is resolved on an inspection of a js file. If it has the module declaration, it resolves true,
 * otherwise it resolves false.
 *
 * @param {string} fileName
 * @param {string} moduleName
 * @returns {Q.Promise}
 */
module.exports.isModuleInFile = function isModuleInFile (fileName, moduleName) {
    return Q.Promise((resolve, reject) => {

        // Create read stream
        let rs = fs.createReadStream(fileName, 'utf8');

        // Add on data read event
        rs.on('data', data => {

            // Build a dynamic rgx and test the data against the rgx. If true it means there is a module declaration
            let rgxString = 'angular\\.module\\([\'"]' + moduleName + '[\'"],.*?\\[';
            let rgx = new RegExp(rgxString);
            let hasModuleDeclaration = rgx.test(data);

            if (hasModuleDeclaration) {
                rs.close();
                resolve(true);
            }
        });

        // If resolved on false, that means that it did not match a module declaration
        rs.on('end', () => {
            resolve(false);
        });
    });
};

/**
 * Checks if a module declaration exists in files
 *
 * @param fileNames
 * @param moduleName
 * @returns {Q.Promise}
 */
module.exports.isModuleInFiles = function (fileNames, moduleName) {
    return Q.all(fileNames.map(fileName => module.exports.isModuleInFile(fileName, moduleName)))
        .then(results => {
            return results.some(hasDeclaration => hasDeclaration);
        });
};

/**
 * Returns a promise that resolves on a file's data in utf8
 *
 * @param {string} fileName
 * @returns {Q.Promise}
 */
module.exports.getFileData = function (fileName) {
    return Q.Promise((resolve, reject) => {

        // Create read stream
        let rs = fs.createReadStream(fileName, 'utf8');

        // Add on data read event
        rs.on('data', resolve);
    });
};

/**
 * Returns a promise that resolves on a module js template
 *
 * @param {DirectiveConfig} templateConfig
 * @returns {Q.Promise}
 */
module.exports.getModuleTemplate = function (templateConfig) {
    return module.exports.getFileData(path.resolve(config.templatesFolder, 'module.template'))
        .then(moduleTemplate => {
            moduleTemplate = moduleTemplate.replace(/\{\{moduleName}}/g, templateConfig.moduleName);
            return moduleTemplate;
        });

};

/**
 * Returns a promise that resolves on a directives js template
 *
 * @param {DirectiveConfig} templateConfig
 * @returns {Q.Promise}
 */
module.exports.getDirectiveTemplate = function (templateConfig) {
    return module.exports.getFileData(path.resolve(config.templatesFolder, 'directive.template'))
        .then(directiveTemplate => {

            let name = templateConfig.directiveName;
            let PascalCaseName = name.charAt(0).toUpperCase() + name.substr(1);
            let templateUrl = templateConfig.templateUrl;

            directiveTemplate = directiveTemplate.replace(/\{\{moduleName}}/g, templateConfig.moduleName);
            directiveTemplate = directiveTemplate.replace(/\{\{directiveName}}/g, name);
            directiveTemplate = directiveTemplate.replace(/\{\{directiveNameUpper}}/g, PascalCaseName);
            directiveTemplate = directiveTemplate.replace(/\{\{templateUrl}}/g, templateUrl);

            return directiveTemplate;
        });
};

/**
 * Returns a promise that resolves on a directives html template
 * @returns {Q.Promise}
 */
module.exports.getHTMLTemplate = function () {
    return module.exports.getFileData(path.resolve(config.templatesFolder, 'directive.view.template'));
};


/**
 * Returns a promise that resolves on a directives js template
 *
 * @param {DirectiveConfig} templateConfig
 * @returns {Q.Promise}
 */
module.exports.getSpecTemplate = function (templateConfig) {
    return module.exports.getFileData(path.resolve(config.templatesFolder, 'directive.spec.template'))
        .then(specTemplate => {

            specTemplate = specTemplate.replace(/\{\{moduleName}}/g, templateConfig.moduleName);
            specTemplate = specTemplate.replace(/\{\{directiveName}}/g, templateConfig.directiveName);
            specTemplate = specTemplate.replace(/\{\{fileName}}/g, templateConfig.fileName);

            return specTemplate;
        });};

/**
 * Returns a promise that resolves on gulp-config file
 * @returns {Q.Promise}
 */
module.exports.getGulpConfig = function () {
    return module.exports.getFileData(path.resolve(__dirname, '../../', config.gulpConfigFile));
};

/**
 * Returns a promise that writes gulp-config file
 * @returns {Q.Promise}
 */
module.exports.writeGulpConfig = function (gulpConfig) {
    let fileName = path.resolve(__dirname, '../../', config.gulpConfigFile);
    return Q.ninvoke(fs, 'writeFile', fileName, gulpConfig);
};

/**
 * Returns a promise that is resolved when a cli command is executed. The command is 'git add'.
 *
 * @param {string} fileName
 * @returns {Q.Promise}
 */
module.exports.addFileToGit = function (fileName) {
    // Add file to git
    output.write(`Adding ${fileName} to git.\n`);
    let cmd = `git add ${fileName}`;
    return Q.ninvoke(chp, 'exec', cmd)
        .then(() => output.write(`${fileName} added to git.\n`));
};
