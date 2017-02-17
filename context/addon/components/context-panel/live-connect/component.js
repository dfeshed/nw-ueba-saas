import Ember from 'ember';
import layout from './template';
import lcColumnList from 'context/config/liveconnect-columns';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  lcColumnList
});
