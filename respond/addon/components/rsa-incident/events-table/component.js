import { get, computed } from '@ember/object';
import { isEmpty } from '@ember/utils';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { storyEvents, storyEventSelections } from 'respond/selectors/storyline';
import { singleSelectEvent } from 'respond/actions/creators/incidents-creators';
import indexOfBy from 'respond/utils/array/index-of-by';

const stateToComputed = (state) => ({
  items: storyEvents(state),
  selections: storyEventSelections(state)
});

const dispatchToActions = (dispatch) => ({
  onRowClick: (item) => dispatch(singleSelectEvent(item && get(item, 'id')))
});

const StoryEvents = Component.extend({
  eventBus: service(),

  // no element needed, just the child data table
  tagName: '',
  items: null,
  selections: null,

  // Computes the index of the first selected id from the current `items` array.
  // This quantity will be passed down the `rsa-data-table` child, which (for now) only supports a
  // single selected index. In the future, once the data table is enhanced to support multiple selected indices,
  // then we will redefine this computed property so it computes multiple selected indices instead of just one.
  selectedIndex: computed('items.[]', 'selections.[]', function() {
    const [ firstId ] = this.selections || [];
    if (isEmpty(firstId)) {
      return -1;
    } else {
      return indexOfBy(this.items, 'id', firstId);
    }
  }),

  /**
   * The `panelId` for the Event Details modal dialog, to be launched by clicking on the individual
   * event rows in the events table.
   * @type {string}
   * @private
   */
  eventDetailsPanelId: 'event-details-panel-1',

  /**
   * The alert event object whose data is to be shown in the event details dialog.
   * @type {object}
   * @private
   */
  eventDetailsModel: null,

  init() {
    this._super(arguments);

    /**
     * Column configurations for data table. @see: component-lib/components/rsa-data-table
     *
     * @type {object[]}
     * @public
     */
    this.columnsConfig = this.columnsConfig || [{
      field: 'custom',
      width: '100%'
    }];
  },

  actions: {
    // Shows the modal dialog with the details of the given alert event object.
    showEventDetails(model) {
      this.set('eventDetailsModel', model);
      this.get('eventBus').trigger(`rsa-application-modal-open-${this.get('eventDetailsPanelId')}`);
    },

    // Hides the modal dialog that displays event details.
    hideEventDetails() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${this.get('eventDetailsPanelId')}`);
      this.set('eventDetailsModel', null);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(StoryEvents);
