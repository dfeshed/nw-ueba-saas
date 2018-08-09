import Component from '@ember/component';
import EmberObject from '@ember/object';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { deselectOperation, sendOperation, updateOperationParams, updateCustomParameter } from 'direct-access/actions/actions';
import { selectedOperation, selectedOperationHelp } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  selectedOperation: selectedOperation(state),
  operationHelp: selectedOperationHelp(state),
  params: state.treeOperationParams
});

const dispatchToActions = {
  deselectOperation,
  sendOperation,
  updateOperationParams,
  updateCustomParameter
};

const treeViewOperationPanel = Component.extend({
  pendingCustomParameter: null,
  operationTypes: [
    'text', 'boolean', 'date-time'
  ],

  @computed('params', 'selectedOperation')
  operationMessageObject: (params, selectedOperation) => ({
    message: selectedOperation.name,
    params
  }),

  actions: {
    updateParams(update) {
      const { name, value } = update;
      const params = this.get('params');
      if (value === undefined && params[name]) {
        this.send('updateOperationParams', params.without(name));
      } else {
        this.send('updateOperationParams', params.set(name, value));
      }
    },

    newCustomParam() {
      this.set('pendingCustomParameter', EmberObject.create({
        name: '',
        displayName: '',
        description: 'Custom Parameter',
        type: 'text',
        optional: false,
        custom: true,
        method: 'add'
      }));
    },

    pushAndClearParam(param) {
      this.send('updateCustomParameter', param);
      this.set('pendingCustomParameter', null);
    },

    updateDisplayName() {
      const pendingCustomParameter = this.get('pendingCustomParameter');
      pendingCustomParameter.set('displayName', pendingCustomParameter.get('name'));
    },

    selectCustomParamType(type) {
      this.get('pendingCustomParameter').set('type', type);
    },

    cancelCustomParameter() {
      this.set('pendingCustomParameter', null);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(treeViewOperationPanel);
