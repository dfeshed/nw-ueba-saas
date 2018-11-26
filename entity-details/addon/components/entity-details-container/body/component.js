import Component from '@ember/component';
import { connect } from 'ember-redux';
import { entityId, entityType } from 'entity-details/reducers/entity/selectors';
import { alertId } from 'entity-details/reducers/alerts/selectors';
import { indicatorId } from 'entity-details/reducers/indicators/selectors';
import computed from 'ember-computed-decorators';


const stateToComputed = (state) => ({
  entityId: entityId(state),
  entityType: entityType(state),
  alertId: alertId(state),
  indicatorId: indicatorId(state)
});

const EntityDetailContainerBodyComponent = Component.extend({
  classNames: ['entity-details-container-body'],
  isClassic: true,

  @computed('entityId', 'entityType', 'alertId', 'indicatorId')
  entityDetailUrl(entityId, entityType, alertId, indicatorId) {
    return `/presidio/index.html#/${entityType}/${entityId}/alert/${alertId}/indicator/${indicatorId}?iframeMode=true`;
  }
});

export default connect(stateToComputed)(EntityDetailContainerBodyComponent);