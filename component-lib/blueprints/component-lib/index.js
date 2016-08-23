module.exports = {
  normalizeEntityName: function() {},

  afterInstall: function(options) {
    return this.addAddonToProject('ember-d3');
  }
};
