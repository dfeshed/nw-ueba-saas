import EventsSheet from 'respond/components/rsa-events-sheet/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  getStoryline,
  storyDatasheet,
  selectedStoryEventCountExpected
} from 'respond/selectors/storyline';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  items: storyDatasheet(state),
  storyline: getStoryline(state),
  selection: state.respond.incident.selection,
  totalCount: selectedStoryEventCountExpected(state),
  throttleInterval: (state.respond.incident.info && state.respond.incident.info.alertCount === 1) ? 0 : 1000
});

const IncidentDatasheet = EventsSheet.extend({
  tagName: '',
  layout,
  items: null,
  storyline: null,
  selection: null,
  totalCount: null,
  throttleInterval: null,

  @computed('selection.{type,ids}', 'storyline.[]')
  selectedIndicatorName(type, ids, storyline) {
    if (type === 'storyPoint') {
      const indicator = storyline.findBy('id', ids[0]);
      return indicator && indicator.alert && indicator.alert.name;
    } else {
      return '';
    }
  }
});

export default connect(stateToComputed, undefined)(IncidentDatasheet);
