import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  andOrOperator
} from 'admin-source-management/reducers/usm/group-wizard-selectors';
import {
  handleAndOrOperator
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  andOrOperator: andOrOperator(state)
});

const dispatchToActions = {
  handleAndOrOperator
};

const DefineGroupStep = Component.extend({
  tagName: 'vbox',
  classNames: ['define-group-step', 'scroll-box'],
  andOr: ['AND', 'OR']
});
export default connect(stateToComputed, dispatchToActions)(DefineGroupStep);
