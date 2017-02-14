import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { decodeText } from 'recon/actions/data-creators';
import layout from './template';

const { Component, isArray } = Ember;

const HTTP_DATA = 80;

const stateToComputed = ({ recon: { data } }) => ({
  meta: data.meta
});

const dispatchToActions = (dispatch) => ({
  decodeText: (decode) => dispatch(decodeText(decode))
});

const DecodeTextComponent = Component.extend({
  layout,
  isDecoded: true,

  @computed('meta')
  isDisabled: (meta) => {
    meta = isArray(meta) ? meta : [];
    const service = meta.find((d) => d[0] === 'service');
    return (service && service[1] === HTTP_DATA) ? false : true;
  },

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
