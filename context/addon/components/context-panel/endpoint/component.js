import Ember from 'ember';
import computed from 'ember-computed-decorators';
import ContextHelper from 'context/util/util';
import ModuleColumns from 'context/config/module-columns';
import IiocColumns from 'context/config/iioc-columns';
import layout from './template';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel__endpoint',

  endpointData: null,

  @computed('endpointData.iioc')
  iiocs: (iioc) => ContextHelper.getIocs(iioc),

  modulesColumnListConfig: [].concat(ModuleColumns),

  iiocsColumnListConfig: [].concat(IiocColumns)
});
