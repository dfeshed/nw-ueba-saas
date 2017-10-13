import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed, { notEmpty } from 'ember-computed-decorators';
import service from 'ember-service/inject';
import { initializeServices } from 'investigate-events/actions/data-creators';

const stateToComputed = ({ investigate }) => ({
  services: investigate.services.data,
  isLoadingServices: investigate.services.isLoading,
  isErrorServices: investigate.services.isError
});

const dispatchToActions = {
  initializeServices
};

const EventsContainer = Component.extend({
  classNames: ['events-container'],
  classNameBindings: ['isLoadingServices:wait', 'isErrorServices:rejected'],
  i18n: service(),

  // Returns true if `services` are NOT null or an empty array.
  @notEmpty('services')
  hasServices: false,

  // Return the desired message to display for the `Retry` button.
  @computed('hasServices', 'isErrorServices', 'i18n')
  message(hasServices, isError, i18n) {
    let message = '';
    if (isError) {
      message = i18n.t('investigate.services.error.description');
    } else if (!hasServices) {
      message = i18n.t('investigate.services.empty.description');
    }
    return message;
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsContainer);