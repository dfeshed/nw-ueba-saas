import Ember from 'ember';
import layout from '../templates/components/rsa-application-layout-panel';

const {
  Component
} = Ember;

export default Component.extend({

  layout,

  tagName: 'vbox',

  classNames: ['rsa-application-layout-panel']
});
