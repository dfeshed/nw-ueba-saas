import Component from '@ember/component';
import EmberObject from '@ember/object';
import computed from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import { sendOperation, cancelOperation, toggleOperationManualVisibility, updateOperationParams, updateParameter } from 'ngcoreui/actions/actions';
import { selectedOperation, selectedOperationHelp, selectedOperationManual, selectedOperationRoles, selectedOperationHasPermission } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  selectedOperation: selectedOperation(state),
  operationHelp: selectedOperationHelp(state),
  operationManual: selectedOperationManual(state),
  operationRoles: selectedOperationRoles(state),
  operationHasPermission: selectedOperationHasPermission(state),
  // TODO selectors?
  params: state.shared.treeOperationParams,
  operationResponse: state.shared.operationResponse,
  operationManualVisible: state.shared.operationManualVisible
});

const dispatchToActions = {
  sendOperation,
  cancelOperation,
  toggleOperationManualVisibility,
  updateOperationParams,
  updateParameter
};

const treeViewOperationPanel = Component.extend({
  pendingCustomParameter: null,
  operationTypes: [
    'text', 'boolean', 'date-time'
  ],

  @computed('selectedOperation')
  visibleParams(selectedOperation) {
    if (selectedOperation) {
      return selectedOperation.params.filter((param) => {
        return !param.hidden;
      });
    } else {
      return [];
    }
  },

  @computed('selectedOperation')
  hiddenParamNames(selectedOperation) {
    if (selectedOperation) {
      return selectedOperation.params.filter((param) => {
        return param.hidden;
      }).map((param) => {
        return param.name;
      }).concat('Custom parameter');
    } else {
      return ['Custom parameter'];
    }
  },

  @computed('operationHasPermission')
  doesNotHavePermission(operationHasPermission) {
    return !operationHasPermission;
  },

  @computed('operationRoles')
  operationRoleText(operationRoles) {
    return operationRoles.length === 1 ? `Required permission: ${operationRoles}` : `Required permissions: ${operationRoles}`;
  },

  @computed('operationHelp')
  operationHelpText(helpText) {
    return htmlSafe(helpText);
  },

  @computed('params', 'hiddenParamNames', 'selectedOperation')
  operationMessageObject: (params, hiddenParamNames, selectedOperation) => ({
    message: selectedOperation.name,
    params: params.without(hiddenParamNames)
  }),

  @computed('operationResponse')
  hasPendingOperation: (operationResponse) => {
    return operationResponse && operationResponse.complete === false;
  },

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

    newParam(paramName) {
      if (paramName === 'Custom parameter') {
        this.set('pendingCustomParameter', EmberObject.create({
          name: '',
          displayName: '',
          description: 'Custom Parameter',
          type: 'text',
          optional: false,
          custom: true,
          method: 'add'
        }));
      } else {
        const op = this.get('selectedOperation');
        let param = op.params.find((param) => {
          return param.name === paramName;
        });
        param = param.set('method', 'show');
        this.send('updateParameter', param);
      }
    },

    pushAndClearParam(param) {
      this.send('updateParameter', param);
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
