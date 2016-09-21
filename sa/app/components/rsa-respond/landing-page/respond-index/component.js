import Ember from 'ember';
import { equal } from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  model: null,

  respondMode: service(),

  @equal('respondMode.selected', 'card') isCardMode: true
});
