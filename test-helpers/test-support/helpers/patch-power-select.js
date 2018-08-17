import PowerSelectComponent from 'ember-power-select/components/power-select';

let originalPowerSelectSearch;

export const patchPowerSelect = function() {
  if (!originalPowerSelectSearch) {
    PowerSelectComponent.reopen({
      init() {
        this._super(...arguments);
        originalPowerSelectSearch = this.get('_publicAPIActions.search');
        this.set('_publicAPIActions.search', function() {});
      }
    });
  }
};

export const restorePowerSelect = function() {
  if (originalPowerSelectSearch) {
    PowerSelectComponent.reopen({
      init() {
        this._super(...arguments);
        this.set('_publicAPIActions.search', originalPowerSelectSearch);
        originalPowerSelectSearch = null;
      }
    });
  }
};
