 /* eslint-disable */

 /**
   * Allows live-reloading when this addon changes even when being served by another projects `ember serve`.
   *
   * But isDevelopingAddon isn't just about live reload, it also controls
   * when certain linters lint the code in this project.
   *
   * We want linting to be run on this project when
   *   1) developing this addon
   *   2) developing something using this addon
   *   3) 'ember test'ing this addon
   *
   * We do not want linting to be run on this project when
   *   1) you are 'ember test'ing something using this addon
   *
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
   * @public
   */
  const isDevelopingAddon = function(projectName) {
    return function() {

      // This is set to the project ember is running
      // which could be THIS project, or it could be
      // something using this addon
      var projName = this.project.pkg.name;

      // This is set to 'test' when running 'ember test'
      var env = process.env.EMBER_ENV

      // We want to report as a 'developingAddon' when
      // we are NOT running 'ember test' OR
      // when the project being run is THIS project
      var isDevAddon = env !== 'test'|| projName === projectName;

      return isDevAddon;
    }
  };

  module.exports = {
    isDevelopingAddon
  };