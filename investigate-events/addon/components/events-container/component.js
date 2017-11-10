import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import { initializeServices } from 'investigate-events/actions/data-creators';
import { hasServices } from 'investigate-events/reducers/investigate/services/selectors';

const stateToComputed = (state) => ({
  hasServices: hasServices(state),
  isErrorServices: state.investigate.services.isServicesRetrieveError,
  isLoadingServices: state.investigate.services.isServicesLoading
});

const dispatchToActions = {
  initializeServices
};

const EventsContainer = Component.extend({
  classNames: ['events-container'],
  classNameBindings: ['isLoadingServices:wait', 'isErrorServices:rejected'],
  i18n: service(),

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