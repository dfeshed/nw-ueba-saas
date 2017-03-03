import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { storyEvents } from 'respond/selectors/storyline';

const { Component } = Ember;

const stateToComputed = (state) => {
  return {
    items: storyEvents(state)
  };
};

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
      title: 'Time',
      width: 100
    }, {
      field: 'user',
      title: 'User',
      width: 50
    }, {
      field: 'host',
      title: 'Host',
      width: 100
    }, {
      field: 'sourceIp',
      title: 'Source',
      width: 100
    }, {
      field: 'destinationIp',
      title: 'Destination',
      width: 100
    }, {
      field: 'domain',
      title: 'Domain',
      width: 100
    }, {
      field: 'file',
      title: 'File',
      width: 150
    }, {
      field: 'indicatorName',
      title: 'Indicator',
      width: 150
    }, {
      field: '',
      title: '',
      width: 'auto'
    }
  ]
});

export default connect(stateToComputed)(StoryEvents);
