import Ember from 'ember';
import connect from 'ember-redux/components/connect';

const { Component } = Ember;

const stateToComputed = ({ respond: { dictionaries, users } }) => {
  return {
    priorityTypes: dictionaries.priorityTypes,
    statusTypes: dictionaries.statusTypes,
    users: users.users
  };
};

const IncidentActionDrawer = Component.extend({
  classNames: ['incident-actions'],
  isLocked: false,

  // When the action button drawer is locked (i.e., the row is in focus/selected), do not let the click events bubble,
  // because this will rise to the row and get handled as a row click, which will blur/deselect the row.
  click() {
    return !this.get('isLocked'); // when isLocked is true, click returns false and prevents event bubbling
  }
});

export default connect(stateToComputed, undefined)(IncidentActionDrawer);