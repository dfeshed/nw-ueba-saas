import Component from '@ember/component';
import { once, later } from '@ember/runloop';
import { connect } from 'ember-redux';
import { initializeEntityDetails } from 'entity-details/actions/entity-creators';


const dispatchToActions = {
  initializeEntityDetails
};

const EntityDetailContainerComponent = Component.extend({
  classNames: ['entity-details-container'],

  _initializeEntityDetails() {
    const iniitalizeProps = this.getProperties('entityId', 'entityType', 'alertId', 'indicatorId');
    this.send('initializeEntityDetails', iniitalizeProps);
  },

  didReceiveAttrs() {
    const { entityId, entityType } = this.getProperties('entityId', 'entityType');
    // nothing to do unless passed parameters
    if (!entityId || !entityType) {
      return;
    }
    later(() => {
      once(this, this._initializeEntityDetails);
    }, 400);
  }
});

export default connect(null, dispatchToActions)(EntityDetailContainerComponent);