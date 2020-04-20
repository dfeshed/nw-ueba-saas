import { computed } from '@ember/object';
import EventsSheet from 'respond/components/rsa-events-sheet/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  getStoryline,
  storyDatasheet,
  selectedStoryEventCountExpected
} from 'respond/selectors/storyline';

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

  selectedIndicatorName: computed('selection.type', 'selection.ids', 'storyline.[]', function() {
    if (this.selection?.type === 'storyPoint') {
      const indicator = this.storyline.findBy('id', this.selection?.ids[0]);
      return indicator && indicator.alert && indicator.alert.name;
    } else {
      return '';
    }
  })
});

export default connect(stateToComputed, undefined)(IncidentDatasheet);
