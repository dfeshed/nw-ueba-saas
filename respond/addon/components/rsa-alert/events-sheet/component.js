import EventsSheet from 'respond/components/rsa-events-sheet/component';
import layout from './template';
import connect from 'ember-redux/components/connect';

const stateToComputed = ({ respond: { alert: { events, eventsStatus } } }) => ({
  items: events,
  itemsStatus: eventsStatus
});

const AlertDatasheet = EventsSheet.extend({
  tagName: '',
  layout,
  items: null,
  itemsStatus: null
});

export default connect(stateToComputed)(AlertDatasheet);
