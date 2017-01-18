import Ember from 'ember';
import computed from 'ember-computed-decorators';
import ContextHelper from 'sa/context/helpers';
import ModuleColumns from 'sa/context/module-columns';
import IiocColumns from 'sa/context/iioc-columns';

const {
    Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-context-panel__endpoint',

  endpointData: null,

  @computed('endpointData.iioc')
  iiocs: (iioc) => ContextHelper.getIocs(iioc),

  modulesColumnListConfig: [].concat(ModuleColumns),

  iiocsColumnListConfig: [].concat(IiocColumns)
});
