import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { storyEvents } from 'respond/selectors/storyline';

const { Component } = Ember;

const stateToComputed = (state) => ({
  items: storyEvents(state)
});

const StoryEvents = Component.extend({
  // no element needed, just the child data table
  tagName: '',

  /**
   * Column configurations for data table. @see: component-lib/components/rsa-data-table
   *
   * @type {object[]}
   * @public
   */
  columnsConfig: [
    {
      field: 'time',
      title: 'respond.eventsTable.time',
      width: 100
    }, {
      field: 'user',
      title: 'respond.eventsTable.user',
      width: 50
    }, {
      field: 'host',
      title: 'respond.eventsTable.host',
      width: 100
    }, {
      field: 'sourceIp',
      title: 'respond.eventsTable.source',
      width: 100
    }, {
      field: 'destinationIp',
      title: 'respond.eventsTable.destination',
      width: 100
    }, {
      field: 'domain',
      title: 'respond.eventsTable.domain',
      width: 100
    }, {
      field: 'file',
      title: 'respond.eventsTable.file',
      width: 150
    }, {
      field: 'indicatorName',
      title: 'respond.eventsTable.indicator',
      width: 150
    }, {
      field: '',
      title: 'respond.eventsTable.blank',
      width: 'auto'
    }
  ]
});

export default connect(stateToComputed)(StoryEvents);
