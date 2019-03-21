import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  focusedItem
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

// placeholder for future actions
const dispatchToActions = () => {
};

const stateToComputed = (state) => ({
  focusedPolicy: focusedItem(state)
});

const UsmRankingInspector = Component.extend({
  classNames: ['usm-ranking-inspector']
});

export default connect(stateToComputed, dispatchToActions)(UsmRankingInspector);