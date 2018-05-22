import Service from '@ember/service';
import { camelize } from '@ember/string';

export default Service.extend({

  init() {
    this._super(...arguments);
    this._flags = Object.create(null);
  },

  setFeatureFlags(flags, reset) {
    if (reset) {
      this._resetFlags();
    }
    for (const flag in flags) {
      if (flags.hasOwnProperty(flag)) {
        if (flags[flag]) {
          this.enable(flag);
        } else {
          this.disable(flag);
        }
      }
    }
  },

  enable(flag) {
    const normalizedFlag = this._normalizeFlag(flag);
    this._flags[normalizedFlag] = true;
  },

  disable(flag) {
    const normalizedFlag = this._normalizeFlag(flag);
    this._flags[normalizedFlag] = false;
  },

  isEnabled(feature) {
    return this._featureIsEnabled(feature);
  },

  _resetFlags() {
    this._flags = Object.create(null);
  },

  _featureIsEnabled(feature) {
    const normalizeFeature = this._normalizeFlag(feature);
    return this._flags[normalizeFeature] || false;
  },

  _normalizeFlag(flag) {
    return camelize(flag);
  },

  unknownProperty(key) {
    return this.isEnabled(key);
  }

});
