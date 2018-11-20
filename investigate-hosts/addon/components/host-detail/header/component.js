import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  isOverviewPanelVisible: state.endpoint.detailsInput.isOverviewPanelVisible
});

const Header = Component.extend({
  tagName: 'vbox',
  classNames: 'flexi-fit host-header'

});

export default connect(stateToComputed)(Header);
