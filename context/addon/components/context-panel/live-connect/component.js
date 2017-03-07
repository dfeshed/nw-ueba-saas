import Ember from 'ember';
import layout from './template';
import lcColumnList from 'context/config/liveconnect-columns';
import { or } from 'ember-computed-decorators';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  lcColumnList,

  @or('model.contextData.LiveConnect-Ip_ERROR', 'model.contextData.LiveConnect-Domain_ERROR', 'model.contextData.LiveConnect-File_ERROR')
  liveConnectError: null

});
