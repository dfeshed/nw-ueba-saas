import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, classNameBindings, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage
} from 'investigate-process-analysis/reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state)
});

@classic
@tagName('box')
@classNames('process-events-details')
@classNameBindings('isShowFilter')
class EventsTableComponent extends Component {
  isShowFilter = false;

  @action
  toggleFilterPanel() {
    this.toggleProperty('isShowFilter');
  }
}

export default connect(stateToComputed)(EventsTableComponent);
