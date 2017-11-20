import Component from 'ember-component';
import { connect } from 'ember-redux';
import {
  errorMessage,
  isContentError,
  isLoading
} from 'recon/reducers/data-selectors';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  currentReconView: recon.visuals.currentReconView,
  errorMessage: errorMessage(recon),
  isContentError: isContentError(recon),
  isLoading: isLoading(recon),
  isMetaShown: recon.visuals.isMetaShown
});

const EventContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-content', 'isMetaShown:col-xs-8:col-xs-12'],
  tagName: 'vbox'
});

export default connect(stateToComputed)(EventContentComponent);
