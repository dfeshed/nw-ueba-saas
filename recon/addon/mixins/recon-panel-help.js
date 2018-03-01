import Mixin from '@ember/object/mixin';
import service from 'ember-service/inject';

export default Mixin.create({

  contextualHelp: service(),

  topic: null,

  didInsertElement() {
    this._super(...arguments);
    this.set('contextualHelp.topic', this.get('topic'));
  },

  willDestroyElement() {
    this._super(...arguments);
    this.set('contextualHelp.topic', null);
  }

});
