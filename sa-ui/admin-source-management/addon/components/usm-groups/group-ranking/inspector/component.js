import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedSourceType
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  selectedSourceType: selectedSourceType(state)
});

const UsmRankingInspector = Component.extend({
  classNames: ['usm-ranking-inspector']
});

export default connect(stateToComputed)(UsmRankingInspector);