import { RECON_VIEW_TYPES_BY_NAME } from '../utils/reconstruction-types';

/*
 * An array to store possible event types, currently just logs and network
 * This could eventually hold additional types, and it is used to look up
 * events by their medium value for forcing the recon view to be something
 * specific. Just forces to text view with logs right now.
 * - name {string} the name of the event type
 * - medium {int} the code for the event type
 * - forcedView {object} an entry from RECON_VIEW_TYPES_BY_NAME or null if you do not want to force a view
 */
const EVENT_TYPES = [
  {
    name: 'LOG',
    medium: 32,
    forcedView: RECON_VIEW_TYPES_BY_NAME.TEXT
  },
  {
    name: 'NETWORK',
    medium: 1,
    forcedView: null
  }
];

/**
 * Takes the meta array and looks for medium to determine event type
 * @param meta The array of meta
 * @public
 */
function determineEventType(meta) {
  const medium = meta.find((entry) => {
    return entry[0] === 'medium';
  });

  return EVENT_TYPES.findBy('medium', medium[1]);
}

const EVENT_TYPES_BY_NAME = {};
EVENT_TYPES.forEach((t) => EVENT_TYPES_BY_NAME[t.name] = t);

export {
  determineEventType,
  EVENT_TYPES,
  EVENT_TYPES_BY_NAME
};