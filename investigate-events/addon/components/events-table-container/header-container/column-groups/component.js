import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { inject as service } from '@ember/service';

const dispatchToActions = {
  setColumnGroup
};

const stateToComputed = (state) => ({
  selectedColumnGroup: getSelectedColumnGroup(state),
  columnGroups: state.investigate.data.columnGroups
});

const ColumnGroups = Component.extend({
  classNames: ['rsa-investigate-events-table__header__columnGroups'],
  eventBus: service(),

  actions: {

    selectColumnGroup(columnGroup) {
      this.get('eventBus').trigger('rsa-content-tethered-panel-hide-tableSearchPanel');
      this.send('setColumnGroup', columnGroup);
    }

  }

});

export default connect(stateToComputed, dispatchToActions)(ColumnGroups);
