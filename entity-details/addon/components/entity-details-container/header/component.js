import Component from '@ember/component';
import { connect } from 'ember-redux';
import { entityDisplayName, isFollowed, entityType, entityScore, entitySeverity, enityIcon } from 'entity-details/reducers/entity/selectors';
import { followUser, unfollowUser } from 'entity-details/actions/entity-creators';

const stateToComputed = (state) => ({
  entityDisplayName: entityDisplayName(state),
  entityScore: entityScore(state),
  entitySeverity: entitySeverity(state),
  isFollowed: isFollowed(state),
  entityType: entityType(state),
  enityIcon: enityIcon(state)
});

const dispatchToActions = {
  followUser,
  unfollowUser
};

const EntityDetailContainerHeaderComponent = Component.extend({
  classNames: ['entity-details-container-header']
});

export default connect(stateToComputed, dispatchToActions)(EntityDetailContainerHeaderComponent);