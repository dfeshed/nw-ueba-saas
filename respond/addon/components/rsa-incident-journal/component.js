import Ember from 'ember';
import connect from 'ember-redux/components/connect';

const { Component } = Ember;

const stateToComputed = ({ respond: { incident: { info } } }) => {
  return {
    entries: info && info.notes
  };
};

const Journal = Component.extend({
  classNames: ['rsa-incident-journal'],

  /**
   * Array of journal entries.
   * @type Object[]
   * @public
   */
  entries: null
});

export default connect(stateToComputed)(Journal);
