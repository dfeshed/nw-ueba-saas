import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileSummary } from 'investigate-files/reducers/file-detail/selectors';
import { hostList } from 'investigate-files/reducers/file-list/selectors';

const stateToComputed = (state) => ({
  summary: fileSummary(state),
  hostList: hostList(state)
});

const SummaryComponent = Component.extend({
  tagName: 'hbox',
  classNames: ['file-summary', 'flexi-fit']
});

export default connect(stateToComputed, undefined)(SummaryComponent);
