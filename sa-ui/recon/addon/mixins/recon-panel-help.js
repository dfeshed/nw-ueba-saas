import Mixin from '@ember/object/mixin';
import { inject as service } from '@ember/service';

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
