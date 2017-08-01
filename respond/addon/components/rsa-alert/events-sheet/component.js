import EventsSheet from 'respond/components/rsa-events-sheet/component';
import layout from './template';
import { connect } from 'ember-redux';

const stateToComputed = ({ respond: { alert: { info, events } } }) => ({
  items: events,
  totalCount: info ? parseInt(info.alert.numEvents, 10) : null
});

const AlertDatasheet = EventsSheet.extend({
  tagName: '',
  layout,
  items: null,
  totalCount: null
});

export default connect(stateToComputed)(AlertDatasheet);
