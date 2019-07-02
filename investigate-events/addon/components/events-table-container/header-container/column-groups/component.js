import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  columnGroups: state.investigate.data.columnGroups
});

const ColumnGroups = Component.extend({
  classNames: ['rsa-investigate-events-table__header__columnGroup']
});

export default connect(stateToComputed)(ColumnGroups);
