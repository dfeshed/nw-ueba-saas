import Component from '@ember/component';
import { eventsLoadingStatus, expandedEventId, events, riskScoringServerError, isRiskScoreContextEmpty } from 'investigate-shared/selectors/risk/selectors';
import layout from './template';

export default Component.extend({
  layout,
  tagName: '',

  didReceiveAttrs() {
    this._super(...arguments);
    const state = {
      risk: this.get('riskState')
    };

    this.setProperties({
      events: events(state),
      eventsLoadingStatus: eventsLoadingStatus(state),
      expandedEventId: expandedEventId(state),
      riskScoringServerError: riskScoringServerError(state),
      isRiskScoreContextEmpty: isRiskScoreContextEmpty(state)
    });
  }
});
