import Service from 'ember-service';
import config from 'ember-get-config';

const {
  APP: {
    version
  }
} = config;

export default Service.extend({
  version
});
