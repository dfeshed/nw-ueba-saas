module.exports = {
  normalizeEntityName: function() {},

  afterInstall: function(options) {
    return this.addAddonToProject('ember-d3');
    return this.addBowerPackageToProject('javascript-detect-element-resize');
  }
};
