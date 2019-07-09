import Component from '@ember/component';
import Confirmable from 'component-lib/mixins/confirmable';
import { deleteRule, cloneRule, enableRules, disableRules, getRules } from 'configure/actions/creators/respond/incident-rule-creators';
import {
  getIncidentRules,
  hasOneSelectedRule,
  getSelectedIncidentRules,
  isNoneSelected,
  getSelectedRuleSwitches,
  isAllEnabled,
  isAllDisabled
} from 'configure/reducers/respond/incident-rules/selectors';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import fetch from 'component-lib/utils/fetch';
import csrfToken from 'component-lib/mixins/csrf-token';
import { success, failure } from 'configure/sagas/flash-messages';
import { later } from '@ember/runloop';

const parseFilename = (contentDisposition) => {
  if (contentDisposition) {
    const props = contentDisposition.split(';');
    const [fileNameProp] = props.filter((str) => str.toLowerCase().includes('filename='));
    const [, fileName] = fileNameProp.split('=');
    if (fileName) {
      // remove double and single quotes from the file name
      return fileName.replace(/['"]+/g, '').trim();
    }
  }
  return '';
};

const downloadFile = (blob, downloadName) => {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.style.display = 'none';
  a.download = downloadName;
  document.body.appendChild(a);
  a.click();
  a.remove();
  later(null, () => {
    URL.revokeObjectURL(url);
  }, 10);
};

const exportRules = async(selRules, csrfKey) => {
  try {
    const response = await fetch('/api/respond/rules/export', {
      mode: 'same-origin',
      method: 'POST',
      body: JSON.stringify(selRules),
      headers: {
        'X-CSRF-TOKEN': localStorage.getItem(csrfKey),
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    const bodyPromise = response.headers.get('Content-type').includes('application/zip') ? response.blob() : response.json();
    const [body, contentDisposition] = await Promise.all([bodyPromise, response.headers.get('Content-Disposition')]);
    if (body instanceof Blob) {
      success('configure.incidentRules.actionMessages.exportSuccess');
      downloadFile(body, parseFilename(contentDisposition) || 'exported-rules.zip');
    } else {
      failure('configure.incidentRules.actionMessages.exportFailure2', {
        missing: body.missingIds.length,
        advanced: body.advancedFilterEnabledIds.length
      });
      const jsonBlob = new Blob([JSON.stringify(body, null, '  ')], { type: 'application/json' });
      downloadFile(jsonBlob, parseFilename(contentDisposition) || 'failure.json');
    }
  } catch (e) {
    failure('configure.incidentRules.actionMessages.exportFailure');
  }
};

const uploadRules = (dispatch, csrfKey) => {
  const input = document.createElement('input');
  input.style.display = 'none';
  input.type = 'file';
  input.accept = '.zip';
  input.addEventListener('change', async() => {
    try {
      const formData = new FormData();
      formData.append('file', input.files[0]);
      const response = await fetch('/api/respond/rules/import', {
        mode: 'same-origin',
        method: 'POST',
        body: formData,
        headers: {
          'X-CSRF-TOKEN': localStorage.getItem(csrfKey)
        }
      }, true);
      if (response.ok) {
        dispatch(getRules());
        success('configure.incidentRules.actionMessages.importSuccess');
        return 'early-termination';
      }
      const bodyPromise = response.headers.get('Content-type').includes('application/json') ? response.json() : response.text();
      const body = await bodyPromise;
      failure('configure.incidentRules.actionMessages.importFailure2');
      const jsonBlob = new Blob([JSON.stringify(body, null, '  ')], { type: 'application/json' });
      downloadFile(jsonBlob, 'failure.json');
    } catch (e) {
      failure('configure.incidentRules.actionMessages.importFailure');
    }
  });
  document.body.appendChild(input);
  input.click();
};

const stateToComputed = (state) => ({
  rules: getIncidentRules(state),
  hasOneSelectedRule: hasOneSelectedRule(state),
  selectedRules: getSelectedIncidentRules(state),
  isNoneSelected: isNoneSelected(state),
  selectedRuleSwitches: getSelectedRuleSwitches(state),
  isAllEnabled: isAllEnabled(state),
  isAllDisabled: isAllDisabled(state)
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
      const csrfKey = this.get('csrfLocalstorageKey');
      const selRulesSet = new Set(this.get('selectedRules'));
      const rules = this.get('rules');
      const exportList = rules.reduce((exportRequest, rule) => {
        if (selRulesSet.has(rule.id)) {
          exportRequest.push({
            id: rule.id,
            name: rule.name
          });
        }
        return exportRequest;
      }, []);
      exportRules(exportList, csrfKey);
    },

    import: () => {
      const csrfKey = this.get('csrfLocalstorageKey');
      uploadRules(dispatch, csrfKey);
    },

    enable: () => {
      const selected = this.get('selectedRules');
      const numSelected = selected.length;
      this.send('showConfirmationDialog', 'enable-rules', { numSelected }, () => {
        dispatch(enableRules(selected));
      });
    },

    disable: () => {
      const selected = this.get('selectedRules');
      const numSelected = selected.length;
      this.send('showConfirmationDialog', 'disable-rules', { numSelected }, () => {
        dispatch(disableRules(selected));
      });
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
