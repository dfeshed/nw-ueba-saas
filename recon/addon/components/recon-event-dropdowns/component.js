import Ember from 'ember';
import connect from 'ember-redux/components/connect';

import * as InteractionActions from 'recon/actions/interaction-creators';
import layout from './template';

const { Component } = Ember;

const dispatchToActions = (dispatch) => ({
  downloadFiles: () => dispatch(InteractionActions.downloadFiles())
});

const DropdownComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-dropdowns']
});

export default connect(null, dispatchToActions)(DropdownComponent);
