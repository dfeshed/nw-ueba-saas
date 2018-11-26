import Component from '@ember/component';
import { connect } from 'ember-redux';
import { toggleProcessDetailsView } from 'investigate-hosts/actions/data-creators/process';

const dispatchToActions = {
  toggleProcessDetailsView
};

const ProcessDetailsActionBar = Component.extend({

  classNames: 'host-process-details-action-bar'

});

export default connect(undefined, dispatchToActions)(ProcessDetailsActionBar);
