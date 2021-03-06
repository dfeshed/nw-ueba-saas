import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import { pivotToInvestigateUrl } from 'context/utils/context-data-modifier';
import { isEmpty } from '@ember/utils';
import { openAddToList } from 'context/actions/list-creators';
import { inject as service } from '@ember/service';

/**
 * @private
 * This actionbar component will render actionbar items Like.. Add/Remove from List, Pivot to Endpoint.
 * Toolbar items are dynamic and same varies based on meta. So if user select IP then Add/Remove from List, Pivote to Endpoint and Pivot to Investigate will bedisplayed.
 * For User only Add/Remove from List and Pivot to investigate will be visible.
 */
const stateToComputed = ({ context: { context: { lookupKey, meta, entitiesMetas }, tabs: { headerButtons } } }) => ({
  lookupKey,
  headerButtons,
  meta,
  entitiesMetas
});

const dispatchToActions = {
  openAddToList
};

const ActionbarComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__header',
  eventBus: service(),

  /*
   * @private
   * Pivot to investigate url will be formed based on lookupKey, meta and entitiesMetas. Where entitiesMetas comes from diffferent promise and same supposed to be resolved before panel opens up.
   * Sample Pivot to investigate url::http://localhost:4200/investigation/choosedevice/navigate/query/event.user%253D'100'%257C%257Cuser.src%253D'100'%257C%257Cuser.dst%253D'100'%257C%257Cusername%253D'100'
   */
  investigateUrl: computed('lookupKey', 'meta', 'entitiesMetas', function() {
    if (isEmpty(this.lookupKey) || isEmpty(this.entitiesMetas)) {
      return '';
    }
    return pivotToInvestigateUrl(this.meta, this.lookupKey, this.entitiesMetas[this.meta]);
  }),

  actions: {
    openContextAddToList(entity) {
      this.send('openAddToList', entity);
      this.get('eventBus').trigger('rsa-application-modal-open-addToList');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ActionbarComponent);
