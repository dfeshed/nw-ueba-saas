module.exports = {
  normalizeEntityName: function() {},

  afterInstall: function(options) {
    return this.addBowerPackageToProject('javascript-detect-element-resize');
  }
};
