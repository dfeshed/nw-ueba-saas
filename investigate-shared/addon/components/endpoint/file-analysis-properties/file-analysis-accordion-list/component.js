import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  tagName: 'box',

  classNames: ['file-analysis-accordion-list'],

  @computed('data')
  importedDllInfoList({ value }) {
    const keys = value ? Object.keys(value) : [];
    let totalFunctionCount = 0;

    const importedDllAndFunctionCountList = keys && keys.map(
      (importedDll) => {
        const functionCount = value[importedDll].length;
        totalFunctionCount = totalFunctionCount + functionCount;
        const functionList = [...value[importedDll]];
        return {
          importedDll,
          functionCount,
          functionList
        };
      }
    );

    return {
      importedDlls: {
        length: keys.length,
        totalFunctionCount
      },
      importedDllAndFunctionCountList
    };
  }
});