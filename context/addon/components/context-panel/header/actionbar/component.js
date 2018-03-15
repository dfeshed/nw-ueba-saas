import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { pivotToInvestigateUrl } from 'context/util/context-data-modifier';
import { isEmpty } from '@ember/utils';

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

const ActionbarComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__header',

  /*
   * @private
   * Pivot to investigate url will be formed based on lookupKey, meta and entitiesMetas. Where entitiesMetas comes from diffferent promise and same supposed to be resolved before panel opens up.
   * Sample Pivote to investigate url::http://localhost:4200/investigation/choosedevice/navigate/query/event.user%253D'100'%257C%257Cuser.src%253D'100'%257C%257Cuser.dst%253D'100'%257C%257Cusername%253D'100'
   */
  @computed('lookupKey', 'meta', 'entitiesMetas')
  investigateUrl(lookupKey, meta, entitiesMetas) {
    if (isEmpty(lookupKey) || isEmpty(entitiesMetas)) {
      return '';
    }
    return pivotToInvestigateUrl(meta, lookupKey, entitiesMetas[meta]);
  }
});

export default connect(stateToComputed)(ActionbarComponent);
