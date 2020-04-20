import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import { initializeIndicator } from 'entity-details/actions/indicator-details';

const stateToComputed = (state) => ({
  alertDetails: getSelectedAlertData(state)
});

const dispatchToActions = {
  initializeIndicator
};

const animateScroll = (elem, scrollLength, directionFactor) => {
  let pos = 0;
  const initialScrollLeft = elem.scrollLeft;
  const id = setInterval(() => {
    if (pos == directionFactor * scrollLength) {
      clearInterval(id);
    } else {
      pos = pos + directionFactor * 5;
      elem.scrollLeft = initialScrollLeft + pos;
    }
  }, 10);
};
const AlertDetailsIndicatorComponent = Component.extend({
  classNames: ['entity-details-container-body-alert-details_indicators'],

  actions: {
    moveLeft() {
      animateScroll(this.element.querySelector('.entity-details-container-body-alert-details_indicators_flow_timeline'), 220, -1);
    },
    moveRight() {
      animateScroll(this.element.querySelector('.entity-details-container-body-alert-details_indicators_flow_timeline'), 220, 1);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertDetailsIndicatorComponent);
