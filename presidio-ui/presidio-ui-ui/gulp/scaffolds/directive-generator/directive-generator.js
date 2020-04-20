'use strict';
let mkdirp = require('mkdirp');
let io = require('../io');
let path = require('path');
let output = process.stdout;
let Q = require('q');
let fs = require('fs');

let DirectiveConfig = require('./classes/directive-config');
let config = require('../config');

module.exports.getConfigFromUser = function () {

    let directiveConfig = new DirectiveConfig();

    return io.inp('Directive name:')
        .then(directiveName => {
            directiveConfig.directiveName = directiveName.trim();
            return io.inp('Module name:');
        })
        .then(moduleName => {
            directiveConfig.moduleName = moduleName.trim();
            return io.inp('Create new module? (yes/No)');
        })
        .then(shouldCreateNewModule => {
            directiveConfig.shouldCreateNewModule = shouldCreateNewModule.trim().toLowerCase() === 'yes';
            return io.inp('Destination path: ');
        })
        .then(directivePath => {
            directiveConfig.directivePath = directivePath;
            return directiveConfig;
        });
};

/**
 * If Validate object indicates an invalid object then write the problem and return invocation of cbInvalid,
 * otherwise (if all is correct) return invocation of cbValid.
 *
 * @param {{validate: boolean, reason: string}} validateObj
 * @param {function} cbInvalid
 * @param {function} cbValid
 * @returns {*}
 */
module.exports.digestValidateObj = function (validateObj, cbInvalid, cbValid) {
    if (!validateObj.validate) {
        output.write('\x1b[31m' + validateObj.reason + '\x1b[0m\n\n');
        return cbInvalid();
    } else {
        return cbValid();
    }
};

/**
 * Displays the config to the user
 *
 * @param {DirectiveConfig} directiveConfig
 */
module.exports.displayConfig = function (directiveConfig) {
    output.write('\n\n\x1b[33mConfiguration: \x1b[0m\n');
    output.write(`Directive name: ${directiveConfig.directiveName}
Module name: ${directiveConfig.moduleName}
Should create new module: ${directiveConfig.shouldCreateNewModule}
Destination path: ${directiveConfig.directivePath}

`);
};

/**
 * Asks the user if the config is correct.
 *
 * @param {DirectiveConfig} directiveConfig
 * @returns {Q.Promise}
 */
module.exports.askUserIfConfigIsCorrect = function (directiveConfig) {
    module.exports.displayConfig(directiveConfig);
    return io.inp('Is this correct? (Yes/no)');
};

/**
 * Digests the user response. If the response is anything but 'no' its considered as a 'yes'. If yes continue
 * (return true). If no, return invocation of callback.
 *
 * @param {string} isConfigCorrect
 * @param {function} cbIncorrect
 * @param {function} cbCorrect
 * @returns {*}
 */
module.exports.digestIsConfigCorrectResponse = function (isConfigCorrect, cbIncorrect, cbCorrect) {
    let isConfigIncorrect = isConfigCorrect.toLowerCase() === 'no';
    if (isConfigIncorrect) {
        return cbIncorrect();
    } else {
        return cbCorrect();
    }
};
/**
 * Creates a new module if shouldCreateNewModule is true.
 *
 * @returns {Q.Promise|boolean}
 */
module.exports.generateModuleFile = function (directiveConfig) {
    if (directiveConfig.shouldCreateNewModule) {
        let fileName;

        // Create (if needed) the destination folder
        return Q.nfcall(mkdirp, path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath))
            .then(() => io.getModuleTemplate({moduleName: directiveConfig.moduleName}))
            // Get the module template
            .then(template => {
                // Resolve the file name
                fileName = path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath,
                    directiveConfig.fileName + '.module.js');

                output.write(`\nWriting module "${directiveConfig.moduleName}" to ${fileName} ...\n`);
                // Write the file
                return Q.ninvoke(fs, 'writeFile', fileName, template);
            })
            .then(() => {
                output.write('Module written.\n');
                return io.addFileToGit(fileName);
            });
    } else {
        return Q.when();
    }
};
/**
 *
 * @param {DirectiveConfig} directiveConfig
 * @returns {*}
 */
module.exports.generateDirectiveFile = function (directiveConfig) {
    let fileName;

    return Q.nfcall(mkdirp, path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath))
        .then(() => io.getDirectiveTemplate(directiveConfig))
        .then(template => {
            fileName = path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath,
                directiveConfig.fileName + '.directive.js');
            output.write(`\nWriting direcive "${directiveConfig.directiveName}" to ${fileName} ...\n`);
            // Write the file
            return Q.ninvoke(fs, 'writeFile', fileName, template);
        })
        .then(() => {
            output.write('Directive written.\n');

            return io.addFileToGit(fileName);
        });
};

/**
 *
 * @param {DirectiveConfig} directiveConfig
 * @returns {*}
 */
module.exports.generateHTMLFile = function (directiveConfig) {
    let fileName;

    return Q.nfcall(mkdirp, path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath))
        .then(() => io.getHTMLTemplate(directiveConfig))
        .then(template => {
            fileName = path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath,
                directiveConfig.fileName + '.view.html');
            output.write(`\nWriting HTML view "${directiveConfig.directiveName}" to ${fileName} ...\n`);
            // Write the file
            return Q.ninvoke(fs, 'writeFile', fileName, template);
        })
        .then(() => {
            output.write('HTML view written.\n');

            return io.addFileToGit(fileName);
        });
};

/**
 *
 * @param {DirectiveConfig} directiveConfig
 * @returns {*}
 */
module.exports.generateSpecFile = function (directiveConfig) {
    let fileName;

    return Q.nfcall(mkdirp, path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath))
        .then(() => io.getSpecTemplate(directiveConfig))
        .then(template => {
            fileName = path.resolve(directiveConfig.baseDir, config.appFolder, directiveConfig.directivePath,
                directiveConfig.fileName + '.directive.spec.js');
            output.write(`\nWriting spec file "${directiveConfig.directiveName}" to ${fileName} ...\n`);
            // Write the file
            return Q.ninvoke(fs, 'writeFile', fileName, template);
        })
        .then(() => {
            output.write('Spec file written.\n');

            return io.addFileToGit(fileName);
        });
};


/**
 * Injects the relevant files to gulp-config so it will be included in the build
 *
 * @param {DirectiveConfig} directiveConfig
 * @returns {*}
 */
module.exports.injectFiles = function (directiveConfig) {

    let dir = path.relative(path.resolve(config.appFolder),
        path.resolve(config.appFolder, directiveConfig.directivePath));

    let modulePath = dir + '/' + directiveConfig.fileName + '.module.js';
    let directivePath = dir + '/' + directiveConfig.fileName + '.directive.js';
    output.write('Injecting files to gulp-config.\n');
    return io.getGulpConfig()
        .then(gulpConfig => {

            let replaceWith = directiveConfig.shouldCreateNewModule ? `\n$1'${modulePath}',` : ``;
            replaceWith += `\n$1'${directivePath}',\n$1$2`;
            gulpConfig = gulpConfig.replace(/(.*?)(\/\/.\*\*END.of.gulp-scaffolds-inject\*\*)/, replaceWith);

            return io.writeGulpConfig(gulpConfig);
        });
};

module.exports.generateDirective = function () {

    /**
     * Directive configuration holder
     *
     * @type {DirectiveConfig|null}
     * @private
     */
    let _directiveConfig = null;

    /**
     * Generate the required files.
     *
     * @returns {*}
     */
    function _generateDirectiveFiles () {
        return module.exports.generateModuleFile(_directiveConfig)
            .then(() => module.exports.generateHTMLFile(_directiveConfig))
            .then(() => module.exports.generateDirectiveFile(_directiveConfig))
            .then(() => module.exports.generateSpecFile(_directiveConfig))
            .then(() => module.exports.injectFiles(_directiveConfig))
            .then(() => {
                output.write('\n\x1b[32mDirective created.\x1b[0m\nIf you have \x1b[33mgulp-serve\x1b[0m running,' +
                    ' please \x1b[33mrestart\x1b[0m it, so it may be aware of new files.\nIf you\'ve created a ' +
                    '\x1b[33mnew module\x1b[0m, don\'t forget to \x1b[33mrequire\x1b[0m it in an already required ' +
                    'module.\n');
            });
    }

    /**
     * Display the config and ask the user if its correct. If not, re-invoke generateDirective, else invoke
     * _generateDirectiveFiles.
     *
     * @returns {*}
     */
    function _getUserInputForDirective () {
        return module.exports.askUserIfConfigIsCorrect(_directiveConfig)
            .then((isConfigCorrect) => module.exports.digestIsConfigCorrectResponse(isConfigCorrect,
                module.exports.generateDirective, _generateDirectiveFiles));
    }

    // Flow start.
    // Get config from user, validate it. If invalid re-invoke generateDirective else invoke _getUserInputForDirective
    return module.exports.getConfigFromUser()
        .then(directiveConfig => {

            _directiveConfig = directiveConfig;
            return _directiveConfig.validateConfig();
        })
        .then(validateObj => module.exports.digestValidateObj(validateObj, module.exports.generateDirective,
            _getUserInputForDirective))
        .catch(err=> {
            console.error(err);
            console.dir(err.stack);
            process.exit(1);
        });

};

