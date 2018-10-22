import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  groupRanking
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  groupRanking: groupRanking(state)
});

const EditRankingStep = Component.extend({
  tagName: 'hbox',
  classNames: 'edit-ranking-step'
});

export default connect(stateToComputed)(EditRankingStep);

