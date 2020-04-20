import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  listVisibilityToggled
} from 'rsa-list-manager/actions/creators/creators';
import {
  caption,
  titleTooltip,
  disabledListName
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  caption: caption(state, attrs.stateLocation),
  titleTooltip: titleTooltip(state, attrs.stateLocation),
  disabledListName: disabledListName(state, attrs.stateLocation)
});

const dispatchToActions = {
  listVisibilityToggled
};

const ListMenuTrigger = Component.extend({
  layout,
  classNames: ['list-menu-trigger', 'rsa-button-group'],
  stateLocation: undefined,
  isDisabled: false,

  actions: {
    triggerClicked() {
      this.send('listVisibilityToggled', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListMenuTrigger);
