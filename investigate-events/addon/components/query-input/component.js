import Ember from 'ember';
import { notEmpty } from 'ember-computed-decorators';

const {
  Component
} = Ember;

export default Component.extend({
  classNames: ['rsa-investigate-query-input'],
  classNameBindings: ['hasValue'],
  placeholder: 'Enter a Meta Key and Value (optional)',
  queryString: '',

  @notEmpty('queryString')
  hasValue: true,

  actions: {
    clear() {
      this.set('queryString', '');
    }
  }
});
