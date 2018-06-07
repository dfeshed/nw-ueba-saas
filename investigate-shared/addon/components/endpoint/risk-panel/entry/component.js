import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import config from './entry-fields-config';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  config,
  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),
  type: '',

  @computed('config', 'type')
  orderedFields(config, type) {
    return config[type].fields;
  }
});
