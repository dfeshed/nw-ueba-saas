import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'admin-source-management/utils/flash-messages';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import {
  hasSelectedEditItem,
  selectedEditItem,
  hasSelectedDeleteItems,
  selectedDeleteItems,
  hasSelectedPublishItems,
  selectedPublishItems
} from 'admin-source-management/reducers/usm/sources-selectors';

import {
  deleteSources,
  publishSources
} from 'admin-source-management/actions/creators/sources-creators';

const stateToComputed = (state) => ({
  hasSelectedEditItem: hasSelectedEditItem(state),
  selectedEditItem: selectedEditItem(state),
  hasSelectedDeleteItems: hasSelectedDeleteItems(state),
  selectedDeleteItems: selectedDeleteItems(state),
  hasSelectedPublishItems: hasSelectedPublishItems(state),
  selectedPublishItems: selectedPublishItems(state)
});

const dispatchToActions = {
  deleteSources,
  publishSources
};

const UsmSourcesToolbar = Component.extend({
  classNames: ['usm-sources-toolbar'],
  accessControl: service(),

  @computed('hasSelectedEditItem', 'accessControl.canManageSourceServerSources')
  cannotEditSources(hasSelectedEditItem, canManageSourceServerSources) {
    return !hasSelectedEditItem || !canManageSourceServerSources;
  },

  @computed('hasSelectedDeleteItems', 'accessControl.canManageSourceServerSources')
  cannotDeleteSources(hasSelectedDeleteItems, canManageSourceServerSources) {
    return !hasSelectedDeleteItems || !canManageSourceServerSources;
  },

  @computed('hasSelectedPublishItems', 'accessControl.canManageSourceServerSources')
  cannotPublishSources(hasSelectedPublishItems, canManageSourceServerSources) {
    return !hasSelectedPublishItems || !canManageSourceServerSources;
  },

  actions: {

    handleDeleteSources() {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.sources.modals.deleteSources.success');
        },
        onFailure: () => {
          failure('adminUsm.sources.modals.deleteSources.failure');
        }
      };
      this.send('deleteSources', callbackOptions);
    },

    handlePublishSources() {
      const callbackOptions = {
        onSuccess: () => {
          success('adminUsm.sources.modals.publishSources.success');
        },
        onFailure: () => {
          failure('adminUsm.sources.modals.publishSources.failure');
        }
      };
      this.send('publishSources', callbackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmSourcesToolbar);
