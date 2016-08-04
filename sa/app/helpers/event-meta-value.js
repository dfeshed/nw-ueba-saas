import Ember from 'ember';

const { Helper } = Ember;

export function eventMetaValue([event, key] /* , hash */) {
  return event && event[key];
}

export default Helper.helper(eventMetaValue);
