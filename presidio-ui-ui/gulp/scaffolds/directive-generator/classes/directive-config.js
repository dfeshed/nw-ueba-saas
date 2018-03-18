'use strict';
let io = require('../../io');
let path = require('path');

let config = require('../../config');

class DirectiveConfig {
    constructor () {
        this._directiveName = null;
        this._moduleName = null;
        this._shouldCreateNewModule = null;
        this._directivePath = null;
        this._jsFiles = io.getAllJsFilesSync(path.resolve(this.baseDir, config.appFolder));

    }

    /**
     *
     * @param {string|null} value
     */
    set directiveName (value) {
        this._directiveName = value;
    }

    /**
     *
     * @returns {string|null}
     */
    get directiveName () {
        return this._directiveName;
    }

    /**
     *
     * @param {string|null} value
     */
    set moduleName (value) {
        this._moduleName = value;
    }

    /**
     *
     * @returns {string|null}
     */
    get moduleName () {
        return this._moduleName;
    }

    /**
     *
     * @param {boolean|null} value
     */
    set shouldCreateNewModule (value) {
        this._shouldCreateNewModule = value;
    }

    /**
     *
     * @returns {boolean|null}
     */
    get shouldCreateNewModule () {
        return this._shouldCreateNewModule;
    }

    /**
     *
     * @param {string|null} value
     */
    set directivePath (value) {
        this._directivePath = value;
    }

    /**
     *
     * @returns {string|null}
     */
    get directivePath () {
        return this._directivePath;
    }


    get fileName () {
        return this._directiveName.replace(/([A-Z])/g, val => '-' + val.toLowerCase());
    }

    get baseDir () {
        return path.resolve(__dirname, '../../../../');
    }

    get templateUrl () {
        return path.relative(path.resolve(this.baseDir,config.appFolder),
            path.resolve(this.baseDir, config.appFolder, this.directivePath, this.fileName + '.view.html'));
    }

    /**
     * Validates the directive config
     *
     * @returns {Q.Promise}
     */
    validateConfig () {

        // Check if module has been declared
        return io.isModuleInFiles(this._jsFiles, this.moduleName)
            // Validate
            .then(isModuleInFiles => {

                if (this.moduleName === undefined || this.moduleName === '') {
                    return {
                        validate: false,
                        reason: `Module name must be provided.`
                    };
                }

                if (this.directiveName === undefined || this.directiveName === '') {
                    return {
                        validate: false,
                        reason: `Directive name must be provided.`
                    };
                }

                if (this.directivePath === undefined || this.directivePath === '') {
                    return {
                        validate: false,
                        reason: `Directive path must be provided.`
                    };
                }

                if (this.shouldCreateNewModule && isModuleInFiles) {
                    return {
                        validate: false,
                        reason: `Module ${this.moduleName} is already declared.`
                    };
                }

                if (!this.shouldCreateNewModule && !isModuleInFiles) {
                    return {
                        validate: false,
                        reason: `Module ${this.moduleName} is not declared.`
                    };
                }

                return {
                    validate: true
                };
            });
    }

}

module.exports = DirectiveConfig;
