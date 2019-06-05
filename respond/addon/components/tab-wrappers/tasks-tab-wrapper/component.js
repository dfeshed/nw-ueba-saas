import Component from '@ember/component';
import { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({

  riac: service(),

  classNames: ['tasks-tab-wrapper'],

  @alias('riac.hasTasksAccess')
  show: null
});