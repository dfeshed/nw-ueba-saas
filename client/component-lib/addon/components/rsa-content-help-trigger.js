import Ember from 'ember';
import layout from '../templates/components/rsa-content-help-trigger';
import Tooltip from '../mixins/tooltip';

export default Ember.Component.extend(Tooltip, {

  tagName: 'span',

  layout,

  classNames: ['rsa-content-help-trigger']

});
