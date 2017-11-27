import Service from 'ember-service';
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
  }
});
