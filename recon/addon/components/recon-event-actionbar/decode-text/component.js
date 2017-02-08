import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { decodeText } from 'recon/actions/data-creators';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  view: data.currentReconView.code
});

const dispatchToActions = (dispatch) => ({
  decodeText: (decode) => dispatch(decodeText(decode))
});

const DecodeTextComponent = Component.extend({
  layout,
  isDecoded: true,

  @computed('view')
  isDisabled: (view) => view !== RECON_VIEW_TYPES_BY_NAME.TEXT.code,

  @computed('isDecoded')
  caption: (isDecoded) => isDecoded ? 'Compress' : 'Decompress',

  actions: {
    toggleDecode() {
      this.toggleProperty('isDecoded');
      this.send('decodeText', this.get('isDecoded'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DecodeTextComponent);
