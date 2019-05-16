import Component from '@ember/component';
import Confirmable from 'component-lib/mixins/confirmable';
import { deleteRule, cloneRule } from 'configure/actions/creators/respond/incident-rule-creators';
import {
  hasOneSelectedRule,
  getSelectedIncidentRules,
  isNoneSelected
} from 'configure/reducers/respond/incident-rules/selectors';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import fetch from 'component-lib/services/fetch';
import csrfToken from 'component-lib/mixins/csrf-token';
import { success, failure } from 'configure/sagas/flash-messages';

let contentDisposition;
const triggerDownload = (blob, isZip) => {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;

  a.download = isZip ? 'exported-rules.zip' : 'failure.json';
  if (contentDisposition) {
    const props = contentDisposition.split(';');
    const [fileNameProp] = props.filter((str) => str.toLowerCase().includes('filename='));
    const [, fileName] = fileNameProp.split('=');
    if (fileName) {
      // remove double and single quotes from the file name
      a.download = fileName.replace(/['"]+/g, '').trim();
    }
  }
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
  if (isZip) {
    success('configure.incidentRules.actionMessages.exportSuccess');
  } else {
    failure('configure.incidentRules.actionMessages.exportFailure');
  }
};

const fetchRules = (selRules, csrfKey) => {
  fetch('/api/respond/rules/export', {
    mode: 'same-origin',
    method: 'POST',
    body: JSON.stringify(selRules),
    headers: {
      'X-CSRF-TOKEN': localStorage.getItem(csrfKey),
      'Content-Type': 'application/json;charset=UTF-8'
    }
  }).then((response) => {
    if (response.headers.has('Content-Disposition')) {
      contentDisposition = response.headers.get('Content-Disposition');
    } else {
      contentDisposition = '';
    }
    return response.blob();
  }).then((blob) => {
    if (blob.type === 'application/zip') {
      triggerDownload(blob, true);
    } else {
      triggerDownload(blob, false);
    }
  });
};

const stateToComputed = (state) => ({
  hasOneSelectedRule: hasOneSelectedRule(state),
  selectedRules: getSelectedIncidentRules(state),
  isNoneSelected: isNoneSelected(state)
});

const dispatchToActions = function(dispatch) {
  return {
    clone: () => {
      const [ templateRuleId ] = this.get('selectedRules');
      const onSuccess = (clonedRuleId) => {
        const transitionToRule = this.get('transitionToRule');
        transitionToRule(clonedRuleId);
      };
      dispatch(cloneRule(templateRuleId, onSuccess));
    },

    delete: () => {
      const [ ruleId ] = this.get('selectedRules');
      this.send('showConfirmationDialog', 'delete-rule', {}, () => {
        dispatch(deleteRule(ruleId));
      });
    },

    export: () => {
      const selRules = this.get('selectedRules');
      const csrfKey = this.get('csrfLocalstorageKey');
      fetchRules(selRules, csrfKey);
    }
  };
};

/**
 * @class IncidentRulesToolbar
 * @public
 */
const IncidentRulesToolbar = Component.extend(Confirmable, csrfToken, {
  tagName: 'hbox',
  accessControl: inject(),
  classNames: ['incident-rules-toolbar']
});

export default connect(stateToComputed, dispatchToActions)(IncidentRulesToolbar);
