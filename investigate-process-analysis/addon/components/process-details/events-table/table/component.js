import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  eventsData,
  eventsFilteredCount,
  eventsTableConfig,
  eventsSortField
} from 'investigate-process-analysis/reducers/process-tree/selectors';
import { setSortField } from 'investigate-process-analysis/actions/creators/events-creators';

const stateToComputed = (state) => ({
  eventsData: eventsData(state),
  config: eventsTableConfig(),
  eventsFilteredCount: eventsFilteredCount(state),
  selectedSortType: eventsSortField(state)
});

const dispatchToActions = {
  setSortField
};

@classic
@classNames('process-events-table')
@tagName('box')
class processEventsTable extends Component {
  @action
  sort(column) {
    column.set('isDescending', !column.isDescending);
    this.send('setSortField', { field: column.field, isDescending: column.isDescending });
  }
}

export default connect(stateToComputed, dispatchToActions)(processEventsTable);
