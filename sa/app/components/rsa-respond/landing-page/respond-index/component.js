import Ember from 'ember';
import { equal } from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  tagName: 'vbox',

  respondMode: service(),

  model: null,

  @equal('respondMode.selected', 'card') isCardMode: true
});
