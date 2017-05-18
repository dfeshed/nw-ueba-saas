import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { toggleJournalPanel } from 'respond/actions/creators/incidents-creators';

const { Component } = Ember;

const stateToComputed = ({ respond: { incident: { info } } }) => ({
  entries: info && info.notes
});

const dispatchToActions = (dispatch) => ({
  closeAction: () => dispatch(toggleJournalPanel())
});

const Journal = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-journal'],

  /**
   * Array of journal entries.
   * @type Object[]
   * @public
   */
  entries: null,

  /**
   * Configurable action to be invoked when user clicks on Close button.
   *
   * @type {function}
   * @public
   */
  closeAction: null
});

export default connect(stateToComputed, dispatchToActions)(Journal);
