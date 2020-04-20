import Service from '@ember/service';
import config from 'ember-get-config';
import computed, { readOnly } from 'ember-computed-decorators';

const {
  APP: {
    version
  }
} = config;

export default Service.extend({
  version,

  @readOnly
  @computed('version')
  marketingVersion: (version) => {
    return version.replace(/(\d+).(\d+).(\d+).(\d+).*/g, '$1.$2.$3.$4');
  },

  @readOnly
  @computed('version')
  minServiceVersion: (version) => {
    const prunedVersion = version.replace(/(\d+).(\d+).(\d+).(\d+).*/g, '$1.$2');

    let minServiceVersion;
    switch (prunedVersion) {
      case '11.0':
        minServiceVersion = prunedVersion;
        break;
      case '11.1':
        minServiceVersion = prunedVersion;
        break;
      default:
        minServiceVersion = (Number(prunedVersion) - .1).toFixed(1);
    }
    return minServiceVersion;
  }

});
