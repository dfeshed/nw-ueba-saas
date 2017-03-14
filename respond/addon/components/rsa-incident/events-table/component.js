import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import { storyEvents, storyEventSelections } from 'respond/selectors/storyline';
import * as UIStateActions from 'respond/actions/ui-state-creators';
import indexOfBy from 'respond/utils/array/index-of-by';

const { Component, get, isEmpty } = Ember;

const stateToComputed = (state) => ({
  items: storyEvents(state),
  selections: storyEventSelections(state)
});

const dispatchToActions = (dispatch) => ({
  onRowClick: (item) => dispatch(UIStateActions.singleSelectEvent(item && get(item, 'id')))
});

const StoryEvents = Component.extend({
  // no element needed, just the child data table
  tagName: '',
  items: null,
  selections: null,

  // Computes the index of the first selected id from the current `items` array.
  // This quantity will be passed down the `rsa-data-table` child, which (for now) only supports a
  // single selected index. In the future, once the data table is enhanced to support multiple selected indices,
  // then we will redefine this computed property so it computes multiple selected indices instead of just one.
  @computed('items.[]', 'selections.[]')
  selectedIndex(items, selections) {
    const [ firstId ] = selections || [];
    if (isEmpty(firstId)) {
      return -1;
    } else {
      return indexOfBy(items, 'id', firstId);
    }
  },

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
      field: 'user',
      title: 'respond.eventsTable.user',
      width: 50
    }, {
      field: 'host',
      title: 'respond.eventsTable.host',
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

export default connect(stateToComputed, dispatchToActions)(StoryEvents);
