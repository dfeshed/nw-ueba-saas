import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const stateToComputed = ({ respond: { incident: { info, infoStatus } } }) => ({
  infoStatus,
  entries: info && info.notes
});

const Journal = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-journal'],
  accessControl: service(),

  /**
   * Array of journal entries.
   * @type Object[]
   * @public
   */
  entries: null
});

export default connect(stateToComputed)(Journal);
