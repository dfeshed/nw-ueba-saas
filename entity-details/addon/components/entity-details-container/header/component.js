import Component from '@ember/component';
import { connect } from 'ember-redux';
import { entityDetails, isFollowed, entityType } from 'entity-details/reducers/entity/selectors';
import computed from 'ember-computed-decorators';

export const severityMap = {
  Critical: 'danger',
  High: 'high',
  Medium: 'medium',
  Low: 'low'
};

const stateToComputed = (state) => ({
  entityDetails: entityDetails(state),
  isFollowed: isFollowed(state),
  entityType: entityType(state)
});

const EntityDetailContainerHeaderComponent = Component.extend({
  classNames: ['entity-details-container-header'],

  @computed('entityDetails')
  entitySeverity(entityDetails) {
    if (entityDetails && entityDetails.scoreSeverity) {
      return severityMap[entityDetails.scoreSeverity];
    }
  }
});

export default connect(stateToComputed)(EntityDetailContainerHeaderComponent);