import Ember from 'ember';
import computed from 'ember-computed-decorators';
import { EVENT_TYPES_BY_NAME } from 'recon/utils/event-types';

const { Mixin } = Ember;

export default Mixin.create({

  /**
   * Check if eventType is 'LOG'
   * @param {object} eventType The event type object
   * @returns {boolean} Log or not
   * @public
   */
  @computed('eventType')
  isLog(eventType) {
    return eventType && eventType.name === EVENT_TYPES_BY_NAME.LOG.name;
  }

});